package com.clickntap.tool.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ProgressCommonsListener implements org.apache.commons.fileupload.ProgressListener {

	private long pos;
	private long size;
	private long startTime;
	private String key;
	private HttpSession session;

	public ProgressCommonsListener(HttpServletRequest request) {
		key = request.getParameter(ProgressCommonsMultipartResolver.PROGRESS_SESSION_KEY).toString();
		session = request.getSession();
		session.setAttribute(key, this);
		startTime = System.currentTimeMillis();
	}

	public long getPos() {
		return pos;
	}

	public long getPercentage() {
		return pos * 100 / size;
	}

	public long getSize() {
		return size;
	}

	public void update(long pos, long size, int n) {
		this.pos = pos;
		this.size = size;
	}

	public long elapsedInMillis() {
		return System.currentTimeMillis() - startTime;
	}

	public long leftInMillis() {
		return (size - pos) * elapsedInMillis() / pos;
	}

	public float uplinkSpeed() {
		return (float) pos * 1000 / (1024 * elapsedInMillis());
	}

	public void detach() {
		session.removeAttribute(key);
	}

}
