A Thread-Safe Bounded Stack is structurally identical to the Producer-Consumer (Bounded Blocking Queue) problem, but enforces **LIFO (Last-In-First-Out)** semantics instead of FIFO.

Just like the queue, the most robust way to implement this is using a single `ReentrantLock` paired with two `Condition` variables: **Not Full** and **Not Empty**. For the underlying data structure, an array with a `top` pointer is highly efficient and avoids the object-allocation overhead of a linked list.

## Key SDE II Design Details

* **Memory Management (`stack[top] = null`):** When popping an item, it is critical to explicitly set the array index to `null`. If you simply decrement the `top` pointer, the array still holds a strong reference to the popped object. In a long-running backend system, this creates a severe memory leak (often called "obsolete references") because the Garbage Collector cannot clean up objects the stack no longer cares about.
* **The Spurious Wakeup Guard:** The `while (top == -1)` loop is non-negotiable. If multiple consumers are waiting on `notEmpty.await()` and a producer pushes one item and calls `signalAll()` (or if a spurious wakeup occurs), all consumers wake up. Without the `while` loop re-checking the condition, the first consumer takes the item, and the second consumer throws an `ArrayIndexOutOfBoundsException`.
* **Array vs. Linked List Performance:** An array-based stack guarantees O(1) time complexity without the constant heap allocation and garbage collection overhead of creating new Node objects for every `push()` operation, which is highly preferable in high-throughput concurrent systems.
