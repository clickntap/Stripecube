package com.clickntap.api;

import java.util.List;

import com.clickntap.developers.debug.DebugRequest;

public interface ConsoleInterface {
	public void resetCache() throws Exception;

	public List<Long> executionTimes() throws Exception;

	public List<String> lastErrors() throws Exception;

	public List<DebugRequest> lastDebugs() throws Exception;

	public void clearErrors() throws Exception;

	public String getVersion() throws Exception;
}
