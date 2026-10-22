A custom Thread Pool relies on the Producer-Consumer pattern, where the main application acts as the producer submitting `Runnable` tasks, and a pre-allocated set of worker threads act as consumers polling a shared task queue.

Here is the complete, runnable implementation demonstrating the lifecycle management of worker threads, task queuing, and a graceful shutdown mechanism.

## Lifecycle & Synchronization Mechanisms

* **Task Execution Outside the Lock:** The `task.run()` invocation happens strictly *after* the `synchronized (taskQueue)` block closes. If a task executes inside the synchronized block, the worker holds the monitor lock for the entire duration of the task. This would paralyze the pool—producers couldn't add tasks, and other workers couldn't fetch tasks.
* **Graceful Shutdown (`isShutdown` + `isEmpty`):** When `shutdown()` is called, `isShutdown` becomes true, but the worker's exit condition is `if (isShutdown && taskQueue.isEmpty())`. This guarantees that all previously queued tasks are fully processed before the threads actually die, perfectly mirroring Java's `ExecutorService.shutdown()`.
* **Exception Isolation:** The `try-catch(RuntimeException e)` block wrapping `task.run()` ensures that if a poorly written task throws a `NullPointerException` or similar unchecked exception, it does not crash the worker thread itself. The exception is caught, logged, and the worker survives to loop back and pick up the next task.
