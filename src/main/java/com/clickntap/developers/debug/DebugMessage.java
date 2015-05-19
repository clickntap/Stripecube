package com.clickntap.developers.debug;

import java.io.Serializable;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.clickntap.tool.types.Datetime;

public class DebugMessage implements Serializable {
	private String timestamp;
	private String message;

	public DebugMessage(String message) {
		this.timestamp = new Datetime().format("yyyy-MM-dd HH:mm:ss.SSS");
		this.message = message;
	}

	public DebugMessage(String script, Exception e) {
		this.timestamp = new Datetime().format("yyyy-MM-dd HH:mm:ss.SSS");
		this.message = ExceptionUtils.getFullStackTrace(e);
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

}
