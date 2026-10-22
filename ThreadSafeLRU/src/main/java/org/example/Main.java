package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Initialize cache with capacity of 3
        ThreadSafeLRUCache<String, String> lruCache = new ThreadSafeLRUCache<>(3);

        // Simulation: Concurrent Producers and Consumers
        Runnable producerTask = () -> {
            String[] keys = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String value = "Val-" + key;
                System.out.println(Thread.currentThread().getName() + " putting: " + key);
                lruCache.put(key, value);

                try { Thread.sleep((int)(Math.random() * 100)); } catch (InterruptedException e) {}
            }
        };

        Runnable consumerTask = () -> {
            String[] keys = {"A", "C", "B"};
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String value = lruCache.get(key);
                System.out.println(Thread.currentThread().getName() + " got: " + key + " -> " + value);

                try { Thread.sleep((int)(Math.random() * 150)); } catch (InterruptedException e) {}
            }
        };

        Thread p1 = new Thread(producerTask, "Producer-1");
        Thread c1 = new Thread(consumerTask, "Consumer-1");
        Thread p2 = new Thread(producerTask, "Producer-2");

        p1.start();
        c1.start();
        p2.start();

        try {
            p1.join();
            c1.join();
            p2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n--- Final State ---");
        lruCache.printCacheState();


    }
}