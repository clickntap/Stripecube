package com.clickntap.tool.mail;

import java.util.Map;

import com.clickntap.tool.script.ScriptEngine;
import com.clickntap.utils.ConstUtils;

public class Mailer {

	private String from;
	private String host;
	private String port;
	private String username;
	private String password;
	private Boolean startTtl;
	private ScriptEngine scriptEngine;

	public Boolean getStartTtl() {
		return startTtl;
	}

	public void setStartTtl(Boolean startTtl) {
		this.startTtl = startTtl;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public Mail newMail(String key, boolean starttl) {
		Mail mail = new Mail(starttl);
		mail.setKey(key);
		mail.setFrom(from);
		mail.setHost(host);
		mail.setPort(port);
		mail.setUsername(username);
		mail.setPassword(password);
		return mail;
	}

	public Mail newMail(String key) {
		return newMail(key, getStartTtl());
	}

	public void setup(Mail mail, Map<String, Object> ctx) throws Exception {
		setSubject(mail, ctx);
		setPlainBody(mail, ctx);
		setHtmlBody(mail, ctx);
	}

	public void setSubject(Mail mail, Map<String, Object> ctx) throws Exception {
		mail.setSubject(scriptEngine.eval(ctx, "mail." + mail.getKey() + ".subject.txt"));
	}

	public void setHtmlBody(Mail mail, Map<String, Object> ctx) throws Exception {
		mail.addBody(scriptEngine.eval(ctx, "mail." + mail.getKey() + ".htm"), ConstUtils.TEXT_HTML_CONTENT_TYPE);
	}

	public void setPlainBody(Mail mail, Map<String, Object> ctx) throws Exception {
		mail.addBody(scriptEngine.eval(ctx, "mail." + mail.getKey() + ".txt"), ConstUtils.TEXT_PLAIN_CONTENT_TYPE);
	}

	public void sendmail(Mail mail) throws Exception {
		mail.sendAsynchronous();
	}
}
