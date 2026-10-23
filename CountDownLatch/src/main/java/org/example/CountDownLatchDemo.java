package org.example;

public class CountDownLatchDemo {

    static class CustomCountDownLatch {
        private int count;

        public CustomCountDownLatch(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("Count cannot be negative");
            }
            this.count = count;
        }

        // Threads calling this will block until count == 0
        public synchronized void await() throws InterruptedException {
            // The while loop protects against spurious wakeups.
            // If a thread wakes up randomly, it will re-check the count
            // and go back to sleep if the count is still > 0.
            while (count > 0) {
                this.wait();
            }
        }

        // Threads calling this will decrement the count
        public synchronized void countDown() {
            if (count > 0) {
                count--;
                System.out.println(Thread.currentThread().getName() + " counted down. Latch count is now: " + count);

                if (count == 0) {
                    // When the final lock is removed, wake up ALL waiting threads
                    this.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        int numberOfServices = 3;
        CustomCountDownLatch latch = new CustomCountDownLatch(numberOfServices);

        // Spawn 3 worker threads representing independent microservice calls
        for (int i = 1; i <= numberOfServices; i++) {
            Thread serviceThread = new Thread(() -> {
                try {
                    // Simulate network latency or processing time
                    Thread.sleep((int) (Math.random() * 1000) + 500);
                    System.out.println(Thread.currentThread().getName() + " finished initializing.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Always put countDown in a finally block so the latch
                    // doesn't freeze if a thread throws an exception
                    latch.countDown();
                }
            }, "Service-" + i);
            serviceThread.start();
        }

        System.out.println("MainThread is waiting for services to initialize...");
        try {
            // Main thread blocks here until count reaches 0
            latch.await();
            System.out.println("MainThread: All services initialized. Application is starting.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}