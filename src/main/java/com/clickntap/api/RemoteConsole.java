package com.clickntap.api;

import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;

public class RemoteConsole implements ConsoleInterface {

	private ConsoleInterface console;

	public void setServerUrl(String serverUrl) throws Exception {
		console = (ConsoleInterface) new HessianProxyFactory().create(ConsoleInterface.class, serverUrl);
	}

	public void resetCache() throws Exception {
		console.resetCache();
	}

	public List<Long> executionTimes() throws Exception {
		return console.executionTimes();
	}

	public List<String> lastErrors() throws Exception {
		return console.lastErrors();
	}

	public void clearErrors() throws Exception {
		console.clearErrors();
	}

	public String getVersion() throws Exception {
		return console.getVersion();
	}

}
