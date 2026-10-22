The **Reader-Writer Problem** occurs in shared-memory systems when multiple threads need to access a shared resource (like a database or an in-memory cache), and the access patterns are heavily skewed toward reading rather than writing.

If you use a standard exclusive lock (like `synchronized` or `ReentrantLock`) for every operation, you create a massive performance bottleneck. Two threads reading data simultaneously don't interfere with each other, so forcing them to wait in line is inefficient.

The Reader-Writer solution enforces three strict rules:

1. **Concurrent Reads:** Any number of reader threads can access the resource simultaneously.
2. **Exclusive Writes:** Only one writer thread can access the resource at a time.
3. **Absolute Isolation:** If a writer is modifying the resource, no readers are allowed to read it (to prevent reading half-written or stale data), and no other writers are allowed to write.

## The Threat of Writer Starvation

The biggest architectural challenge in the Reader-Writer problem is **Writer Starvation**. If you prioritize readers, a continuous, heavy stream of incoming read requests will keep the read lock continuously active. A writer waiting to update the cache will be blocked forever because the number of active readers never drops to zero.

Java's `ReentrantReadWriteLock` solves this elegantly by offering a **fairness policy**. When initialized with `true`, the lock grants access to the longest-waiting thread. If a writer is waiting, new incoming readers will be queued *behind* the writer, ensuring the writer eventually gets its turn.

## Key Architectural Points

* **Lock Degradation:** A common interview follow-up is asking if a thread can upgrade a `ReadLock` to a `WriteLock`. In Java, **lock upgrading is not supported** and will cause a deadlock if attempted. However, **lock downgrading is supported**; a thread holding a `WriteLock` can acquire a `ReadLock` before releasing the `WriteLock` to ensure it can read its own changes immediately without other writers jumping in.
* **HashMap vs ConcurrentHashMap:** Inside a `ReentrantReadWriteLock`, it is perfectly safe to use a standard `HashMap`. Since the lock itself enforces the concurrency rules and memory visibility, the internal map does not need its own synchronization overhead.
