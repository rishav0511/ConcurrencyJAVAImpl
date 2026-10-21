package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<T> {
    private final Queue<T> queue;
    private final int capacity;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();


    public BoundedBlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size()==capacity)
            {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue is FULL.");
                notFull.await();
            }
            queue.add(item);
            System.out.println(Thread.currentThread().getName() + " produced: " + item + " | Queue size: " + queue.size());

            // Signal that the queue is no longer empty
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty())
            {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue is EMPTY.");
                notEmpty.await();
            }

            T item = queue.poll();
            System.out.println(Thread.currentThread().getName() + " consumed: " + item + " | Queue size: " + queue.size());

            // Signal that the queue is no longer full
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
