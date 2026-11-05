package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Task implements Comparable<Task> {
    final Runnable action;
    long scheduledTime;
    final long period;
    final boolean recurring;

    public Task(Runnable action, long scheduledTime, long period, boolean recurring) {
        this.action = action;
        this.scheduledTime = scheduledTime;
        this.period = period;
        this.recurring = recurring;
    }

    @Override
    public int compareTo(Task other) {
        return Long.compare(this.scheduledTime, other.scheduledTime);
    }
}

public class AsyncScheduler {
    private final PriorityQueue<Task> queue = new PriorityQueue<>();
    private final Object lock = new Object();
    private boolean running = true;

    // Using a cached thread pool ensures that long-running tasks don't block the scheduler thread,
    // guaranteeing exact-time firing for subsequent tasks.
    private final ExecutorService workerPool = Executors.newCachedThreadPool();

    public AsyncScheduler() {
        Thread schedulerThread = new Thread(this::runLoop);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }

    public void schedule(Runnable action, long delayMs) {
        long time = System.currentTimeMillis() + delayMs;
        addToQueue(new Task(action, time, 0, false));
    }

    public void scheduleAtFixedRate(Runnable action, long initialDelayMs, long periodMs) {
        long time = System.currentTimeMillis() + initialDelayMs;
        addToQueue(new Task(action, time, periodMs, true));
    }

    private void addToQueue(Task task) {
        synchronized (lock) {
            queue.add(task);
            // If the newly added task is at the head of the queue, it means it has the earliest
            // execution time. Wake up the scheduler thread to recalculate its wait time.
            if (queue.peek() == task) {
                lock.notifyAll();
            }
        }
    }

    private void runLoop() {
        while (running) {
            Task taskToRun = null;
            synchronized (lock) {
                try {
                    while (queue.isEmpty() && running) {
                        lock.wait(); // Wait indefinitely until a task is added
                    }
                    if (!running) break;

                    Task nextTask = queue.peek();
                    long now = System.currentTimeMillis();
                    long delay = nextTask.scheduledTime - now;

                    if (delay <= 0) {
                        // Time to fire
                        taskToRun = queue.poll();
                        if (taskToRun.recurring) {
                            // Schedule the next occurrence
                            taskToRun.scheduledTime = now + taskToRun.period;
                            queue.add(taskToRun);
                        }
                    } else {
                        // Sleep until the exact time the next task is scheduled to fire
                        lock.wait(delay);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Execute outside the synchronized block to avoid holding the lock
            if (taskToRun != null) {
                workerPool.submit(taskToRun.action);
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }
        workerPool.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        AsyncScheduler scheduler = new AsyncScheduler();
        long startTime = System.currentTimeMillis();
        System.out.println("Scheduler started at: 0 ms");

        // 1. One-time task scheduled to run after 2 seconds
        scheduler.schedule(() -> {
            System.out.println("[One-Time] Executed at: " + (System.currentTimeMillis() - startTime) + " ms");
        }, 2000);

        // 2. Recurring task scheduled to run after 1 second, then every 3 seconds
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[Recurring] Executed at: " + (System.currentTimeMillis() - startTime) + " ms");
        }, 1000, 3000);

        // Let the scheduler run for 10 seconds to observe the outputs
        Thread.sleep(10000);

        scheduler.stop();
        System.out.println("Scheduler stopped.");
    }
}