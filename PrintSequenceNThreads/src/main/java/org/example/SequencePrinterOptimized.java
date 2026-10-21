package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SequencePrinterOptimized {
    private final int MAX;
    private final int N;
    private int count = 1;
    private ReentrantLock lock = new ReentrantLock();
    private Condition[] conditions;

    public SequencePrinterOptimized(int MAX, int n) {
        this.MAX = MAX;
        this.N = n;
        this.conditions = new Condition[N];
        for(int i=0;i<N;i++) {
            conditions[i] = lock.newCondition();
        }
    }

    void print(int threadId) {
        while (true) {
            lock.lock();
            try {
                // Exit Strategy: If the limit is reached, wake ALL threads so they can exit gracefully.
                // Without this, the threads waiting their turn would block indefinitely after MAX is reached.
                if (count > MAX) {
                    for (Condition c : conditions) {
                        c.signal();
                    }
                    break;
                }

                // If it is NOT this thread's turn, await on its specific condition
                if ((count - 1) % N != threadId) {
                    conditions[threadId].await();
                } else {
                    // It IS this thread's turn
                    System.out.println("Thread-" + (threadId + 1) + ": " + count);
                    count++;
                    // Targeted Signaling: Calculate the exact next thread and wake ONLY it
                    int nextThreadId = (count - 1) % N;
                    conditions[nextThreadId].signal();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }
}
