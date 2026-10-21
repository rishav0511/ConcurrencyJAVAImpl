The Producer-Consumer pattern decouples threads that generate data from threads that process that data. The foundational data structure linking them is a **Bounded Blocking Queue**.

* **Bounded:** The queue has a strict maximum capacity to prevent `OutOfMemoryError` if producers outpace consumers.
* **Blocking:** If the queue is full, producers must halt and wait for space. If the queue is empty, consumers must halt and wait for data.

When you scale this to **Multiple Producers and Multiple Consumers (MPMC)**, the primary challenges are race conditions (two producers overwriting each other's data) and deadlocks (a consumer waiting for data while holding the lock, preventing producers from adding data).

## The Approach: Two Conditions, One Lock

To build this safely and efficiently, avoid the basic `synchronized` keyword and `wait()/notifyAll()`. Instead, use a single `ReentrantLock` paired with two distinct `Condition` variables: **Not Full** and **Not Empty**.

1. **State Management:** Maintain a data structure (like a `LinkedList`) and a strict `capacity` limit.
2. **Mutual Exclusion:** A single `ReentrantLock` guards all read and write operations on the shared queue. Only one thread can manipulate the queue at any exact microsecond.
3. **Producer Logic (put):** Acquire the lock. If the queue is at capacity, `await()` on the **Not Full** condition (this releases the lock so consumers can work). Once space is available, add the item, then `signal()` the **Not Empty** condition to wake up a waiting consumer. Release the lock.
4. **Consumer Logic (take):** Acquire the lock. If the queue is empty, `await()` on the **Not Empty** condition. Once data is available, remove the item, then `signal()` the **Not Full** condition to wake up a waiting producer. Release the lock.


## How this Design Prevents MPMC Deadlocks

1. **The `while` Loop (State Re-evaluation):** When a consumer wakes up from `notEmpty.await()`, it does not immediately pull data. It loops back to check `queue.isEmpty()`. In a multi-consumer environment, another consumer might have acquired the lock first and snatched the only available item. The `while` loop ensures the waking consumer simply goes back to sleep instead of throwing a `NoSuchElementException` or deadlocking.
2. **Targeted Signaling:** By using two separate `Condition` variables, a producer adding an item calls `notEmpty.signal()`. This is guaranteed to wake up a **consumer**. If you used basic `notify()`, the JVM might wake up another producer by mistake, which would immediately block again, leaving the consumers asleep and the system stalled.
3. **Unlocking Before Sleeping:** The `await()` method intrinsically drops the `ReentrantLock` before putting the thread to sleep, and re-acquires it before waking up. This guarantees that a waiting producer never hoards the lock and starves the consumers.
