package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(5);
        // Create Multiple Producers
        for (int i = 1; i <= 3; i++)
        {
            Thread producer = new Thread(() -> {
                int count = 0;
                try {
                    while (true) {
                        queue.put(count);
                        count++;
                        // Sleep slightly faster than consumers to force the queue to fill up
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer-" + i);
            producer.start();
        }

        // Create Multiple Consumers
        for (int i = 1; i <= 2; i++) {
            Thread consumer = new Thread(() -> {
                try {
                    while (true) {
                        queue.take();
                        // Sleep slightly slower to allow producers to overwhelm the queue
                        Thread.sleep(600);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer-" + i);
            consumer.start();
        }
    }
}