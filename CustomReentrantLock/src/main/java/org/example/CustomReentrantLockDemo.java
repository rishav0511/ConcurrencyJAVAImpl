package org.example;

public class CustomReentrantLockDemo {
    static class ReentrantLock {
        private boolean isLocked=false;
        private Thread lockedBy=null;
        private int lockCount=0;

        public synchronized void lock() throws InterruptedException {
            Thread callingThread = Thread.currentThread();

            // Wait if the lock is held by a DIFFERENT thread.
            // If isLocked is true but lockedBy == callingThread, this loop is skipped.
            while (isLocked && lockedBy != callingThread) {
                this.wait();
            }

            // Grant lock or process re-entry
            isLocked = true;
            lockedBy = callingThread;
            lockCount++;

            System.out.println(callingThread.getName() + " acquired lock. Re-entry count: " + lockCount);
        }

        public synchronized void unlock() {
            Thread callingThread = Thread.currentThread();

            // Security check: Only the thread holding the lock can unlock it
            if (lockedBy != callingThread) {
                throw new IllegalMonitorStateException("Calling thread does not hold the lock.");
            }

            lockCount--;
            System.out.println(callingThread.getName() + " released lock. Re-entry count: " + lockCount);

            // If count reaches 0, the thread has fully backed out of all locked scopes
            if (lockCount == 0) {
                isLocked = false;
                lockedBy = null;

                // Wake up ONE waiting thread to hand over the lock
                this.notify();
            }
        }
    }
}
