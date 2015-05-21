package com.clickntap.tool.cache;

import java.util.List;

public interface CacheManager {

	public List<String> getCacheNames() throws Exception;

	public Cache getCache(String cacheName) throws Exception;

	public boolean containsCache(String cacheName);

	public void reset();
}
