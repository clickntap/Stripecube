package com.clickntap.api;

import java.util.List;

import com.clickntap.developers.debug.DebugRequest;
import com.clickntap.smart.SmartApp;
import com.clickntap.tool.cache.CacheManager;

public class Console implements ConsoleInterface {

	private CacheManager cacheManager;
	private SmartApp smartApp;
	private String version;

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public SmartApp getSmartApp() {
		return smartApp;
	}

	public void setSmartApp(SmartApp smartApp) {
		this.smartApp = smartApp;
	}

	public void resetCache() throws Exception {
		cacheManager.reset();
	}

	public List<Long> executionTimes() throws Exception {
		return smartApp.getExecutionTimes();
	}

	public List<String> lastErrors() throws Exception {
		return smartApp.getLastErrors();
	}

	public void clearErrors() throws Exception {
		smartApp.clearErrors();
	}

	public String getVersion() throws Exception {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<DebugRequest> lastDebugs() throws Exception {
		return smartApp.getLastDebugs();
	}

}
