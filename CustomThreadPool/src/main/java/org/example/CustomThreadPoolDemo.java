package org.example;

import java.util.LinkedList;

public class CustomThreadPoolDemo {
    static class CustomThreadPool {
        private final int poolSize;
        private WorkerThread[] workers;
        private LinkedList<Runnable> taskQueue;

        // Volatile ensures the shutdown flag is immediately visible to all worker threads
        private volatile boolean isShutdown;

        public CustomThreadPool(int poolSize) {
            this.poolSize = poolSize;
            this.workers = new WorkerThread[poolSize];
            this.isShutdown=false;
            this.taskQueue = new LinkedList<>();
            for(int i=0;i<poolSize;i++) {
                workers[i] = new WorkerThread("Worker-" + (i + 1));
                workers[i].start();
            }
            System.out.println("ThreadPool initialized with " + poolSize + " workers.");
        }

        public void execute(Runnable task) {
            if(isShutdown) {
                throw new IllegalStateException("ThreadPool is shut down, cannot accept new tasks.");
            }
            synchronized (taskQueue) {
                taskQueue.add(task);
                // Notify a single sleeping worker that a new task is available
                taskQueue.notify();
            }
        }

        public void shutdown() {
            isShutdown = true;
            System.out.println("Shutdown initiated. No new tasks will be accepted.");

            synchronized (taskQueue) {
                // Wake up all dormant threads so they evaluate the isShutdown flag and exit
                taskQueue.notifyAll();
            }
        }

        public class WorkerThread extends Thread {

            public WorkerThread(String name) {
                super(name);
            }

            @Override
            public void run() {
                Runnable task;
                while (true) {
                    synchronized (taskQueue) {
                        // Wait if the queue is empty AND the pool is not shut down
                        while (taskQueue.isEmpty() && !isShutdown) {
                            try {
                                taskQueue.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        // Exit condition: graceful shutdown requires draining the queue first
                        if (isShutdown && taskQueue.isEmpty()) {
                            break;
                        }

                        // Poll the task from the queue
                        task = taskQueue.poll();
                    }

                    // Execute the task OUTSIDE the synchronized block
                    // This prevents locking the queue while the task is running
                    if (task != null) {
                        try {
                            task.run();
                        } catch (RuntimeException e) {
                            System.err.println(Thread.currentThread().getName() + " encountered an exception: " + e.getMessage());
                        }
                    }
                }
                System.out.println(Thread.currentThread().getName() + " has terminated.");
            }
        }
    }

}
