package com.clickntap.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.clickntap.tool.bean.Bean;
import com.clickntap.tool.bean.BeanManager;
import com.clickntap.tool.script.ScriptEngine;
import com.clickntap.tool.types.AbstractComponent;
import com.clickntap.utils.BindUtils;
import com.clickntap.utils.XMLUtils;

public class SmartApp extends AbstractComponent {
	private static Log log = LogFactory.getLog(SmartApp.class);

	private SmartController root;
	private SmartController mapper;
	private Resource controllerDir;
	private Resource envResource;
	private ScriptEngine evalEngine;
	private Properties env;
	private Resource docRoot;
	private List<Long> executionTimes;
	private List<String> lastErrors;

	Map<String, Object> globalObjects;

	public Resource getDocRoot() {
		return docRoot;
	}

	public void setDocRoot(Resource docRoot) {
		this.docRoot = docRoot;
	}

	public void setEnvResource(Resource envResource) {
		this.envResource = envResource;
	}

	public void setEvalEngine(ScriptEngine scriptEngine) {
		this.evalEngine = scriptEngine;
	}

	public void start() throws Exception {
		executionTimes = new ArrayList<Long>();
		lastErrors = new ArrayList<String>();
		Element root = XMLUtils.copyFrom(getConfResource().getInputStream()).getRootElement();
		Element mapping = root.element("mapping");
		if (mapping != null)
			this.mapper = new SmartController(controllerDir, mapping, null);
		this.root = new SmartController(controllerDir, root, null);
		env = new Properties();
		if (envResource != null) {
			env.load(envResource.getInputStream());
			env.put("docRoot", docRoot.getFile().getCanonicalPath());
		}
	}

	public SmartController getRoot() {
		return root;
	}

	public SmartController getController(SmartContext context) {
		return root.getController(context.getRef());
	}

	public void setControllerDir(Resource controllerDir) {
		this.controllerDir = controllerDir;
	}

	public String eval(Map ctx, String script) throws Exception {
		return evalEngine.evalScript(ctx, script);
	}

	public boolean evalRule(Map ctx, String rule) throws Exception {
		return evalEngine.evalRuleScript(ctx, rule);
	}

	public synchronized Object load(SmartContext ctx, String objectName, String objectClass, String channel, String scope) throws Exception {
		Object object = null;
		if ("request".equals(scope)) {
			object = ctx.getRequest().getAttribute(objectName);
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				ctx.getRequest().setAttribute(objectName, object);
			}
		} else if ("session".equals(scope)) {
			object = ctx.getSession().getAttribute(objectName);
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				ctx.getSession().setAttribute(objectName, object);
			}
		} else if ("global".equals(scope)) {
			if (globalObjects == null)
				globalObjects = new HashMap<String, Object>();
			object = globalObjects.get(objectName);
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				globalObjects.put(objectName, object);
			}
		} else {
			object = Class.forName(objectClass).newInstance();
		}
		if (object instanceof Bean)
			((Bean) object).setBeanManager((BeanManager) ctx.getBean(channel));
		return object;
	}

	public synchronized SmartBindingResult bind(SmartContext ctx, String objectName, String objectClass, String channel, String[] allowedFields, String[] disallowedFields, String scope) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("bind '" + objectName + "' with scope '" + scope + "' on channel '" + channel + "'");
		}
		boolean isNew = false;
		Object object = null;
		if ("request".equals(scope)) {
			object = ctx.getRequest().getAttribute(objectName);
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				ctx.getRequest().setAttribute(objectName, object);
				isNew = true;
			}
		} else if ("session".equals(scope)) {
			try {
				object = ctx.getSession().getAttribute(objectName);
			} catch (Exception e) {
			}
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				try {
					ctx.getSession().setAttribute(objectName, object);
				} catch (Exception e) {
				}
				isNew = true;
			}
		} else if ("global".equals(scope)) {
			if (globalObjects == null)
				globalObjects = new HashMap<String, Object>();
			object = globalObjects.get(objectName);
			if (object == null) {
				object = Class.forName(objectClass).newInstance();
				globalObjects.put(objectName, object);
				isNew = true;
			}
		} else {
			object = Class.forName(objectClass).newInstance();
			isNew = true;
		}
		if (log.isDebugEnabled() && isNew) {
			log.debug("new '" + objectName + "' with scope '" + scope + "' on channel '" + channel + "'");
		}
		if (object instanceof Bean)
			((Bean) object).setBeanManager((BeanManager) ctx.getBean(channel));
		SmartBindingResult bindingResult = bind(ctx, object, objectName, allowedFields, disallowedFields);
		bindingResult.setNew(isNew);
		return bindingResult;
	}

	public static SmartBindingResult bind(SmartContext ctx, Object object, String objectName, String[] allowedFields, String[] disallowedFields) {
		if (ctx.getSmartRequest() != null)
			return bind(ctx.getSmartRequest().getParameters(), ctx, object, objectName, allowedFields, disallowedFields);
		else
			return bind(ctx.getRequest(), ctx, object, objectName, allowedFields, disallowedFields);
	}

	public static SmartBindingResult bind(Object params, Map map, Object object, String objectName, String[] allowedFields, String[] disallowedFields) {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(object, objectName);
		if (allowedFields != null)
			binder.setAllowedFields(allowedFields);
		if (disallowedFields != null)
			binder.setDisallowedFields(disallowedFields);
		BindUtils.registerCustomEditor(binder);
		if (params instanceof HttpServletRequest)
			binder.bind((HttpServletRequest) params);
		if (params instanceof Map) {
			MutablePropertyValues parameters = new MutablePropertyValues();
			parameters.addPropertyValues((Map) params);
			binder.bind(parameters);
		}
		if (map != null)
			map.putAll(binder.getBindingResult().getModel());
		return new SmartBindingResult(binder.getBindingResult());
	}

	public String conf(String key) {
		return env.getProperty(key);
	}

	public SmartController getMapper() {
		return mapper;
	}

	public void addError(Throwable error) {
		try {
			if (lastErrors.size() > 9) {
				lastErrors.remove(9);
			}
			lastErrors.add(0, error.toString());
		} catch (Exception e) {
		}
	}

	public void addExecutionTimes(long millis) {
		try {
			if (executionTimes.size() > 99) {
				executionTimes.remove(99);
			}
			executionTimes.add(0, millis);
			if (log.isInfoEnabled()) {
				if (executionTimes.size() < 10) {
					log.info("last execution times: " + executionTimes);
				} else {
					log.info("last execution times: " + executionTimes.subList(0, 10));
				}
			}
		} catch (Exception e) {
		}
	}

	public List<Long> getExecutionTimes() {
		return executionTimes;
	}

	public List<String> getLastErrors() {
		return lastErrors;
	}

	public void clearErrors() {
		lastErrors.clear();
	}

}
