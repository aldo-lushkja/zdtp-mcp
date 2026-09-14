package com.ibm.mcp.zdtp.shared.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe lightweight in-memory cache with Time-To-Live (TTL).
 * Zero-framework policy compliant.
 */
public class TtlCache<K, V> {

    private final long ttlMs;
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    public TtlCache(long ttlMs) {
        this.ttlMs = ttlMs;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (System.currentTimeMillis() > entry.expiryTime()) {
            cache.remove(key);
            return null;
        }
        return entry.value();
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMs));
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    private record CacheEntry<V>(V value, long expiryTime) {}
}
