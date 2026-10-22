package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create a thread pool with 3 worker threads
        CustomThreadPoolDemo.CustomThreadPool threadPool = new CustomThreadPoolDemo.CustomThreadPool(3);

        // Submit 10 tasks to the pool
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing Task-" + taskId);
                try {
                    // Simulate work taking between 100ms to 500ms
                    Thread.sleep((int) (Math.random() * 400) + 100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println(Thread.currentThread().getName() + " finished Task-" + taskId);
            });
        }

        // Allow some time for tasks to process before shutting down
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Initiate graceful shutdown
        threadPool.shutdown();
    }
}