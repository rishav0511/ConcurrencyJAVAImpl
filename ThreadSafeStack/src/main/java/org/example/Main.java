package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Initialize a stack with capacity of 4
        ThreadSafeStack.BoundedBlockingStack<Integer> stack = new ThreadSafeStack.BoundedBlockingStack<>(4);

        // Producers pushing to the stack
        for (int i = 1; i <= 2; i++) {
            Thread producer = new Thread(() -> {
                int item = 100;
                try {
                    while (true) {
                        stack.push(item++);
                        Thread.sleep(200); // Push fast to fill the stack
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer-" + i);
            producer.start();
        }

        // Consumers popping from the stack
        for (int i = 1; i <= 2; i++) {
            Thread consumer = new Thread(() -> {
                try {
                    while (true) {
                        stack.pop();
                        Thread.sleep(800); // Pop slowly to force producers to wait
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer-" + i);
            consumer.start();
        }
    }
}