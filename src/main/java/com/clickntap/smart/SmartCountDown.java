package com.clickntap.smart;

public class SmartCountDown {
	private long timeoutInMillis;
	private long time;

	public SmartCountDown() {
		time = System.currentTimeMillis();
	}

	public long getTimeoutInMillis() {
		return timeoutInMillis;
	}

	public void setTimeoutInMillis(Number timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis.longValue();
	}

	public void setTimeoutInSeconds(Number timeoutInSeconds) {
		this.timeoutInMillis = timeoutInSeconds.longValue() * 1000;
	}

	public long getLastModified() {
		long now = System.currentTimeMillis();
		if ((now - time) > timeoutInMillis)
			time = now;
		return time;
	}
}
