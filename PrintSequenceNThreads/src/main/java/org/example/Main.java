package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        int max = 20,nThreads = 3;
//        SequencePrinter sequencePrinter = new SequencePrinter(max,nThreads);
//        for (int i = 0; i < nThreads; i++) {
//            final int threadId = i;
//            new Thread(() -> sequencePrinter.print(threadId)).start();
//        }

        /**
         * Because we use notifyAll(), every time a number is printed,
         * all dormant threads are woken up. They all contend for the lock,
         * but only one will evaluate its modulo condition to true—the rest will
         * immediately go back to sleep. This CPU-wasting phenomenon is called the Thundering Herd.
         *
         * To fix this in a production environment (or an SDE II interview),
         * you would use a single ReentrantLock paired with an array of Condition
         * variables (one Condition per thread). Instead of notifyAll(), the current thread
         * calculates the ID of the next thread ((count - 1) % N) and explicitly signals
         * only that specific thread's Condition, leaving the others peacefully asleep.
         *
         * By assigning a dedicated Condition variable to each thread,
         * we achieve targeted signaling. When a thread finishes its turn,
         * it calculates exactly which thread should go next and signals only that
         * specific thread's condition, leaving the remaining N-2 threads completely dormant.
         */
//        int MAX = 100, nThreads = 7;
//        SequencePrinterOptimized sequencePrinterOptimized = new SequencePrinterOptimized(MAX,nThreads);
//        for (int i = 0; i < nThreads; i++) {
//            final int threadId = i;
//            new Thread(() -> sequencePrinterOptimized.print(threadId)).start();
//        }


    }
}