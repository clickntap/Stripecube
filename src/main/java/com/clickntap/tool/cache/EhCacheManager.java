package com.clickntap.tool.cache;

public class EhCacheManager implements CacheManager {
	private net.sf.ehcache.CacheManager cacheManager;

	public EhCacheManager() {
		cacheManager = net.sf.ehcache.CacheManager.create();
	}

	public Cache getCache(String cacheName) throws Exception {
		return new EhCache(cacheManager, cacheName);
	}

	public boolean containsCache(String cacheName) {
		return cacheManager.cacheExists(cacheName);
	}

	public void reset() {
		cacheManager.clearAll();
	}

	public void shutdown() {
		cacheManager.shutdown();
	}
}
