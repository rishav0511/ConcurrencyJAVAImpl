package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create a map with 4 segments (Concurrency Level = 4)
        // Up to 4 threads can write simultaneously as long as they hit different segments
        StrippedConcurrentHashMapDemo.StripedConcurrentMap<String, String> map = new StrippedConcurrentHashMapDemo.StripedConcurrentMap<>(4);

        Runnable writerTask = () -> {
            String threadName = Thread.currentThread().getName();
            for (int i = 1; i <= 3; i++) {
                String key = "Key-" + (int)(Math.random() * 10); // Random key 0-9
                map.put(key, threadName + "-Value-" + i);
                try { Thread.sleep((int)(Math.random() * 50)); } catch (InterruptedException e) {}
            }
        };

        // Spawn 5 concurrent writers
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(writerTask, "Writer-" + (i + 1));
            threads[i].start();
        }

        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { }
        }

        System.out.println("Final approximate size: " + map.size());
    }
}