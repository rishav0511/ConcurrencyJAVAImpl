package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class StrippedConcurrentHashMapDemo {

    static class StripedConcurrentMap<K, V> {
        // Inner class representing a single locked segment
        private static class Segment<K, V> {
            private final Map<K, V> map = new HashMap<>();
            private final ReentrantLock lock = new ReentrantLock();

            private void put(K key, V value) {
                lock.lock();
                try {
                    map.put(key, value);
                    System.out.println(Thread.currentThread().getName() + " wrote [" + key + "=" + value + "]");
                } finally {
                    lock.unlock();
                }
            }

            private V get(K key) {
                lock.lock();
                try {
                    V value = map.get(key);
                    return value;
                } finally {
                    lock.unlock();
                }
            }

            private int size() {
                lock.lock();
                try {
                    return map.size();
                } finally {
                    lock.unlock();
                }
            }
        }

        public final Segment<K, V>[] segments;
        private final int concurrencyLevel;

        @SuppressWarnings("unchecked")
        public StripedConcurrentMap(int concurrencyLevel) {
            this.concurrencyLevel = concurrencyLevel;
            this.segments = (Segment<K, V>[]) new Segment[concurrencyLevel];
            for(int i=0;i<concurrencyLevel;i++) {
                this.segments[i] = new Segment<>();
            }
        }

        // Hashing function to route a key to a specific segment
        private int getSegmentIndex(K key) {
            int hash = key.hashCode();
            // Bitwise AND with 0x7FFFFFFF clears the sign bit, preventing
            // Math.abs(Integer.MIN_VALUE) from returning a negative index.
            return (hash & 0x7FFFFFFF) % concurrencyLevel;
        }

        public V get(K key) {
            if (key == null)
                return null;
            int index = getSegmentIndex(key);
            return segments[index].get(key);
        }

        public void put(K key, V value) {
            if (key == null)
                throw new IllegalArgumentException("Null keys not supported");
            int index = getSegmentIndex(key);
            segments[index].put(key,value);
        }

        // The architectural tradeoff of lock striping: Global operations are expensive
        public int size() {
            int totalSize = 0;
            for (Segment<K, V> segment : segments) {
                totalSize += segment.size();
            }
            return totalSize;
        }
    }
}
