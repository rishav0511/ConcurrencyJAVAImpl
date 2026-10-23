A `CountDownLatch` is a synchronization aid that acts like a heavily locked door. You initialize it with a specific number (the "count", like 3 locks on the door).

Some threads walk up to this door and call `await()`. Because the door is locked, they sit down and go to sleep. Other threads do some work, and when they finish, they call `countDown()` (unlocking one lock). When the count hits exactly zero, the door swings open, and all the sleeping threads wake up and rush through simultaneously.

**Crucial Characteristic:** A `CountDownLatch` is a **one-shot mechanism**. Once the count hits zero, it cannot be reset. Any thread that calls `await()` after the count is zero will simply pass right through without blocking. (If you need a reset feature, you would use a `CyclicBarrier`).

## Real-World Use Cases

* **Service Aggregation:** A main thread makes concurrent calls to 3 different microservices (e.g., User Profile, Billing, Recommendations). The main thread calls `await()`, and each microservice thread calls `countDown()` when its network response arrives. The main thread only resumes when all 3 responses are ready to be merged.
* **Load Testing (The Starting Gun):** You want to test how your system handles a sudden spike. You spawn 100 threads, but you want them to hit your API at the exact same millisecond. You give them a `CountDownLatch(1)`. All 100 threads call `await()`. The main thread calls `countDown()`, releasing all 100 threads simultaneously.


## SDE II Design Discussion Points

* **`notifyAll()` vs `notify()`:** Notice that `countDown()` uses `this.notifyAll()`. This is mandatory. If 5 threads are waiting on the latch, calling `notify()` would only wake up a single random thread, leaving the other 4 asleep forever. We need the door to swing wide open for everyone.
* **The `finally` Block Requirement:** In a production system, it is critical to call `countDown()` inside a `finally` block. If "Service-1" throws a `NullPointerException` before reaching `countDown()`, the count will be permanently stuck at 1, and the main thread waiting on `await()` will be deadlocked indefinitely.
* **Zero-State Safety:** Notice the check `if (count > 0)` inside `countDown()`. Without this, if a rogue thread calls `countDown()` after the latch has already opened, the count would go into negative numbers.
