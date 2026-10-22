An LRU Cache requires O(1) time complexity for both read and write operations. To achieve this, you must bind a **Hash Map** (for instant lookups) to a **Doubly Linked List** (for instant reordering and eviction).

The biggest concurrency trap in an LRU cache is that **every read is actually a write**. When a thread calls `get()`, the cache must detach that item from the middle of the linked list and move it to the front (marking it as most recently used). Because reads mutate the underlying data structure, you cannot safely use a `ReentrantReadWriteLock`—you must enforce strict mutual exclusion for both `get` and `put` operations.

## The Architecture

1. **The Node:** A basic wrapper holding the key, value, and pointers to the previous and next nodes.
2. **Dummy Head and Tail:** Sentinel nodes at the front and back of the linked list. These eliminate null-pointer edge cases when adding or removing nodes.
3. **The Lock:** A single `ReentrantLock` guarding both the Hash Map and the Linked List pointers.

## Optimizing for High Throughput

In a production environment (like Memcached or Redis internals), wrapping the entire cache in a single global lock creates a severe bottleneck. If a thousand threads try to read simultaneously, they form a single-file line.

If an interviewer asks how to scale this further, the answer is **Lock Striping** (or sharding). Instead of one giant cache, you create an array of `N` smaller `ThreadSafeLRUCache` instances (e.g., 16 segments). When a request comes in, you hash the key to determine which specific segment it belongs to, and only lock that fraction of the cache. This allows multiple threads to read and write to different segments concurrently, massively increasing throughput.

* **Why not use `ConcurrentHashMap`?** Because the `cache.put()` and the linked list's `addToHead()` must happen simultaneously. If you used a `ConcurrentHashMap` without a global lock, Thread 1 could add to the map, get paused, and Thread 2 could add to the map and linked list. Now your map and linked list are out of sync.
* **The Global Lock Bottleneck:** The single `ReentrantLock` guarantees safety but limits throughput in highly concurrent environments because all threads queue up.
* **Advanced Optimization (Lock Striping):** In production systems like Guava Cache or Caffeine, this bottleneck is mitigated by breaking the cache into smaller segments (e.g., 16 smaller LRU caches), each with its own lock. A key is hashed to a specific segment, allowing multiple threads to update the cache simultaneously as long as they hit different segments.
