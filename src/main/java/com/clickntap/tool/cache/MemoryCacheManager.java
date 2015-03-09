package com.clickntap.tool.cache;

import java.util.HashMap;

public class MemoryCacheManager implements CacheManager {

	private HashMap<String, MemoryCache> caches;

	public MemoryCacheManager() {
		caches = new HashMap<String, MemoryCache>();
	}

	public Cache getCache(String cacheName) throws Exception {
		synchronized (caches) {
			if (!caches.containsKey(cacheName))
				caches.put(cacheName, new MemoryCache());
			return caches.get(cacheName);
		}
	}

	public boolean containsCache(String cacheName) {
		return true;
	}

	public void reset() {
		synchronized (caches) {
			caches = new HashMap<String, MemoryCache>();
		}
	}

}
