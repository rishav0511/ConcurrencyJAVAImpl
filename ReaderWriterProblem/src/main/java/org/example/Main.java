package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ThreadSafeCache<String, String> cache = new ThreadSafeCache<>();
        cache.put("config_1", "initial_value");

        // Spawn 5 Reader Threads
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                while (true) {
                    cache.get("config_1");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {}
                }
            }, "Reader-" + i).start();
        }

        // Spawn 2 Writer Threads
        for (int i = 1; i <= 2; i++) {
            new Thread(() -> {
                int version = 1;
                while (true) {
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    cache.put("config_1", "updated_value_v" + version++);
                }
            }, "Writer-" + i).start();
        }
    }
}