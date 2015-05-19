package com.clickntap.developers.debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DebugRequest implements Serializable {

	private List<DebugMessage> messages;

	public DebugRequest() {
		messages = new ArrayList<DebugMessage>();
	}

	public void addScriptException(String script, Exception e) {
		messages.add(new DebugMessage(script, e));
	}

	public void log(String message) {
		messages.add(new DebugMessage(message));
	}

	public List<DebugMessage> getMessages() {
		return messages;
	}

}
