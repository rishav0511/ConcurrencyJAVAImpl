package org.example;

import java.util.concurrent.locks.ReentrantLock;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        // Runnable demonstrating reentrancy through recursion
        Runnable recursiveTask = new Runnable() {
            int recursiveDepth = 3;

            @Override
            public void run() {
                try {
                    lock.lock();
                    recursiveDepth--;

                    if (recursiveDepth > 0) {
                        System.out.println(Thread.currentThread().getName() + " is recursing deeper...");
                        // Thread calls a locked block while already holding the lock
                        run();
                    } else {
                        System.out.println(Thread.currentThread().getName() + " reached maximum depth.");
                    }

                } finally {
                    lock.unlock();
                }
            }
        };

        // Thread 1 demonstrates reentrancy
        Thread t1 = new Thread(recursiveTask, "Thread-1");

        // Thread 2 demonstrates blocking while Thread 1 works
        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("Thread-2 finally got the lock!");
            } finally {
                lock.unlock();
            }
        }, "Thread-2");

        t1.start();

        // Slight delay to ensure Thread 1 gets the lock first
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        t2.start();
    }
}