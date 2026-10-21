package org.example;

import java.util.concurrent.Semaphore;

public class EvenOddPrinterUsingSemaphore {

    private final int MAX;
    private int count = 1;
    private Semaphore evenSemaphore = new Semaphore(1);
    private Semaphore oddSemaphore = new Semaphore(0);

    public EvenOddPrinterUsingSemaphore(int MAX) {
        this.MAX = MAX;
    }

    public void printOdd() {
        while (count < MAX) {
            try {
                oddSemaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ": " + count);
                count++;
                evenSemaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void printEven() {
        while (count <= MAX) {
            try {
                evenSemaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ": " + count);
                count++;
                oddSemaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
