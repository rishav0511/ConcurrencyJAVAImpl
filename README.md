# ConcurrencyJAVAImpl

1. ~~**Print Sequence using N Threads:** Generalize the Even/Odd problem. Given `N` threads, Thread 1 prints 1, Thread 2 prints 2... Thread N prints N, Thread 1 prints N+1, etc.~~
3. ~~**Print Even and Odd using Two Threads:** The classic problem. Thread 1 prints odd numbers, Thread 2 prints even numbers up to `N`. Practice this using:~~

   * ~~`wait()` and `notifyAll()` with a shared object monitor.~~
   * ~~`ReentrantLock` and `Condition` variables.~~
   * ~~Java `Semaphore`~~


## 1. Thread Coordination & Synchronization (The Classics)

These test your core understanding of `wait()`, `notifyAll()`, `ReentrantLock`, `Condition`, and `Semaphore`.

* **Print Sequence / FooBar:** (Already covered) Print ordered sequences using multiple threads.
* ~~**The Producer-Consumer Problem:** Implement a `BoundedBlockingQueue` from scratch. Expand it to handle multiple producers and multiple consumers without deadlocks.~~
* ~~**The Reader-Writer Problem:** Implement a shared cache where multiple threads can read simultaneously, but writes require exclusive locks. *Bonus challenge: Ensure writer threads do not starve if there is a continuous influx of readers.*~~
* ~~**Dining Philosophers:** Write the code to simulate resource allocation. *Key focus: Guarantee absolute prevention of deadlocks by imposing lock ordering.*~~
* **Building H2O:** Coordinate threads representing Hydrogen and Oxygen so that water molecules are formed exactly as H-H-O.

## 2. Custom Concurrency Primitives

Interviewers often ask you to build standard library tools from scratch to see if you understand how they work under the hood.

* ~~**Implement a Custom Thread Pool:** Manage a task queue and worker thread lifecycle.~~
* **Implement a CountDownLatch:** Build a synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
* **Implement a Custom Reentrant Lock:** Write a lock that allows the thread currently holding it to acquire it again without blocking itself.
* **Implement a Spin Lock:** Use `AtomicReference` or `AtomicBoolean` to create a lock where waiting threads actively poll (spin) instead of yielding the CPU. Understand the performance tradeoffs versus sleeping locks.

## 3. Thread-Safe Data Structures

These questions combine standard Data Structures and Algorithms (DSA) with concurrency control.

* **Thread-Safe Singleton:** Implement the Double-Checked Locking pattern. *Key focus: Explain exactly why the `volatile` keyword is mandatory in Java for this to work safely.*
* ~~**Thread-Safe LRU Cache:** Combine a hash map and a doubly linked list. Ensure that concurrent reads and writes maintain O(1) time complexity without corrupting the node pointers or eviction order.~~
* ~~**Thread-Safe Bounded Stack:** Implement `push()` and `pop()`. If full, `push()` blocks until space is available. If empty, `pop()` blocks until an item is added.~~
* ~~**Concurrent Hash Map Implementation:** Design a simplified version of `ConcurrentHashMap` using lock striping (an array of locks for different segments of the map) to allow higher throughput than a single global lock.~~

## 4. Real-World Backend Components (SDE II Focus)

These blend low-level design (LLD) with concurrency, closely mirroring actual production systems.

* **Design an In-Memory Rate Limiter:** Implement a Token Bucket algorithm. Ensure it is strictly thread-safe and can accurately restrict thousands of concurrent API requests per second without race conditions.
* **Design an Asynchronous Task Scheduler:** Implement a system to execute tasks at fixed intervals or after specific delays (similar to a timer). Guarantee exact-time firing using a Priority Queue and Wait/Notify logic.
* ~~**Design a Database Connection Pool:** Create a manager that distributes active connections to requesting threads, blocks if the maximum pool size is reached, and safely reclaims connections upon release.~~
* **In-Memory Pub/Sub Message Broker:** Build a system where publishers push messages to specific topics, and multiple consumer threads read them asynchronously without dropping messages or corrupting the queue.

## 5. Conceptual Deep Dives (Verbal/Whiteboard)

Be prepared to explain these Java-specific memory model concepts in depth:

* **The Happens-Before Relationship:** Explain how the Java Memory Model (JMM) guarantees visibility of state changes across threads.
* **CAS (Compare-And-Swap):** Explain how `AtomicInteger` works internally using `sun.misc.Unsafe` hardware-level atomic instructions instead of locks.
* **ThreadLocal:** Explain how to use `ThreadLocal` safely in a web server environment (like Spring Boot/Tomcat) and how failing to clean it up leads to severe memory leaks.
* **CompletableFuture vs. Parallel Streams:** Compare the use cases, thread pool assignments (ForkJoinPool), and exception handling differences between the two.
