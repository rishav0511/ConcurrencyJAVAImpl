A standard lock prevents multiple threads from accessing a critical section simultaneously. However, if a thread holding a standard lock attempts to acquire it again—for example, if a locked method calls another locked method, or during a recursive function call—it will see that the lock is already taken and put itself to sleep. The thread has just **deadlocked itself**.

A **Reentrant Lock** solves this by tracking *which* thread currently holds the lock. If the thread requesting the lock is the exact same thread that already owns it, the lock grants access immediately and increments a counter.

## Core Mechanisms

To build this from scratch using intrinsic synchronization (`wait()` and `notify()`), you need three state variables:

1. **`isLocked`**: A boolean flag indicating if the lock is currently held.
2. **`lockedBy`**: A reference to the `Thread` object that currently owns the lock.
3. **`lockCount`**: An integer tracking how many times the owning thread has re-entered the lock.

## SDE II Design Discussion Points

* **`notify()` vs `notifyAll()`:** In earlier examples like the `CountDownLatch`, we used `notifyAll()` because multiple threads needed to proceed simultaneously. For an exclusive lock, only *one* thread can acquire the lock next. Using `notify()` is an intentional optimization here to prevent the "Thundering Herd" problem, waking only a single blocked thread instead of causing all waiting threads to spike the CPU fighting for a lock only one can win.
* **The `IllegalMonitorStateException`:** The `unlock()` method strictly validates that `lockedBy == currentThread`. This prevents malicious or poorly written code in a rogue thread from calling `unlock()` and arbitrarily clearing the lock while another thread is in the middle of modifying critical data. This directly mimics the behavior of Java's standard `ReentrantLock`.
* **Performance Tradeoff:** While custom locks are great for understanding concurrency, using `synchronized` blocks inside the custom lock itself means you are using an intrinsic lock to build an explicit lock. In production, `java.util.concurrent.locks.ReentrantLock` uses `sun.misc.Unsafe` hardware-level Compare-And-Swap (CAS) instructions to manage state changes without immediately delegating to OS-level thread suspension, making it exponentially faster.
