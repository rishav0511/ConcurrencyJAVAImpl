package org.example;

public class EvenOddPrinterUsingWait {
    private final int max;
    private int current = 1;
    private boolean stopped;

    public EvenOddPrinterUsingWait(int max) {
        this.max = max;
    }

    synchronized void printEven() {
        while (!stopped && current <= max) {
            while (!stopped && current <= max && current % 2 != 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    stopped = true;
                    notifyAll();
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (stopped) {
                return;
            }

            if (current <= max) {
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                notifyAll();
            }
        }
    }

    synchronized void printOdd() {
        while (!stopped && current <= max) {
            while (!stopped && current <= max && current % 2 == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    stopped = true;
                    notifyAll();
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (stopped) {
                return;
            }

            if (current <= max) {
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                notifyAll();
            }
        }
    }
}
