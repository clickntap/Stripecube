package com.clickntap.hub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.clickntap.tool.jdbc.JdbcManager;
import com.clickntap.utils.ConstUtils;

public class JSONController extends MultiActionController {

	private BOManager app;
	private JdbcManager jdbcManager;

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(JSONController.class);

	public BOManager getApp() {
		return app;
	}

	public void setApp(BOManager app) {
		this.app = app;
	}

	public JdbcManager getJdbcManager() {
		return jdbcManager;
	}

	public void setJdbcManager(JdbcManager jdbcManager) {
		this.jdbcManager = jdbcManager;
	}

	protected void handleRequest(HttpServletResponse response, Object object) {
		response.setContentType("text/json;charset=UTF-8");
		try {
			byte[] bytes = object.toString().getBytes(ConstUtils.UTF_8);
			response.setContentLength(bytes.length);
			handleRequest(response, bytes);
		} catch (Exception e) {
			handleException(response, e);
		}
	}

	protected void handleRequest(HttpServletResponse response, byte[] out) throws IOException {
		response.getOutputStream().write(out);
	}

	protected void handleException(HttpServletResponse response, Throwable throwable) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(out);
			throwable.printStackTrace(writer);
			writer.close();
			JSONObject res = new JSONObject();
			res.put("exception", throwable);
			res.put("stackTrace", out.toString("UTF-8"));
			handleRequest(response, res);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
