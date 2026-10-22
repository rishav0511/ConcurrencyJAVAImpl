package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeCache<K,V> {
    private final Map<K,V> cache = new HashMap<>();
    // Passing 'true' enables fairness policy, preventing Writer Starvation
    private ReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();

    // Multiple threads can execute this block simultaneously
    public V get(K key) {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is READING key: " + key +" with value: " + cache.get(key));
            // Simulate some read latency
            Thread.sleep(100);
            return cache.get(key);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            readLock.unlock();
        }
    }

    // Only ONE thread can execute this block at a time.
    // Blocks all readers and other writers.
    public void put(K key, V value) {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is WRITING key: " + key + " with value: " + value);
            // Simulate some write latency
            Thread.sleep(200);
            cache.put(key, value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writeLock.unlock();
        }
    }

}
