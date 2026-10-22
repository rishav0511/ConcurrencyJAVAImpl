package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeStack {

    static class BoundedBlockingStack<T> {
        private final Object[] stack;
        private final int capacity;
        private int top; // Points to the top element's index

        private final ReentrantLock lock = new ReentrantLock();
        private Condition notFull = lock.newCondition();
        private Condition notEmpty = lock.newCondition();

        public BoundedBlockingStack(int capacity) {
            this.capacity = capacity;
            this.stack = new Object[capacity];
            this.top=-1;
        }

        public void push(T item) throws InterruptedException {
            lock.lock();
            try {
                // Wait while the stack is full
                while(top==capacity-1) {
                    System.out.println(Thread.currentThread().getName() + " waiting: Stack is FULL.");
                    notFull.await();
                }
                //insert the element
                top++;
                stack[top]=item;
                System.out.println(Thread.currentThread().getName() + " pushed: " + item + " | Stack size: " + (top + 1));

                // Signal a waiting consumer that the stack is no longer empty
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public T pop() throws InterruptedException {
            lock.lock();
            try {
                // Wait while the stack is empty
                while(top==-1) {
                    System.out.println(Thread.currentThread().getName() + " waiting: Stack is EMPTY.");
                    notEmpty.await();
                }
                // Pop item, nullify reference for GC, and update pointer
                T item = (T) stack[top];
                stack[top] = null;
                top--;
                System.out.println(Thread.currentThread().getName() + " popped: " + item + " | Stack size: " + (top + 1));

                // Signal a waiting producer that the stack is no longer full
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }
    }
}
