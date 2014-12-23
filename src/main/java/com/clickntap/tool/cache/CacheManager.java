package com.clickntap.tool.cache;

public interface CacheManager {
	public Cache getCache(String cacheName) throws Exception;

	public boolean containsCache(String cacheName);

	public void reset();
}
