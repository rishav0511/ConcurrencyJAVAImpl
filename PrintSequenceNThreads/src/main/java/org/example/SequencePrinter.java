package org.example;

public class SequencePrinter {
    private final int MAX;
    private final int N;
    private int count = 1;
    private Object lock = new Object();

    public SequencePrinter(int MAX, int n) {
        this.MAX = MAX;
        this.N = n;
    }

    void print(int threadId) {
        synchronized (lock) {
            while (count <= MAX)
            {
                if((count-1)%N!=threadId)
                {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else
                {
                    System.out.println("Thread-" + (threadId + 1) + ": " + count);
                    count++;
                    lock.notifyAll();
                }
            }
        }

    }

}
