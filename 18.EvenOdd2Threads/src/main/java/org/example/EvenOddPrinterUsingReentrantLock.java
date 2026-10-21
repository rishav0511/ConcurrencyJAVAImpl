package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EvenOddPrinterUsingReentrantLock {
    private int MAX;
    private int current = 1;
    private ReentrantLock lock = new ReentrantLock();
    private Condition oddCondition = lock.newCondition();
    private Condition evenCondition = lock.newCondition();

    public EvenOddPrinterUsingReentrantLock(int MAX) {
        this.MAX = MAX;
    }

     void printEven() {
        while (current<=MAX) {
            lock.lock();
            try {
                while(current<=MAX && current%2!=0) {
                    evenCondition.await();
                }
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                oddCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

     void printOdd() {
        while (current<=MAX) {
            lock.lock();
            try {
                while(current<=MAX && current%2==0) {
                    oddCondition.await();
                }
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                evenCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}
