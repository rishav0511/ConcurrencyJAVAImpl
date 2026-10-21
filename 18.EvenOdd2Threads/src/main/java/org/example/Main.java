package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //USING WAIT() AND NOTIFYALL()
//        EvenOddPrinterUsingWait printer = new EvenOddPrinterUsingWait(20);
//
//        Thread evenThread = new Thread(() -> printer.printEven());
//        Thread oddThread = new Thread(() -> printer.printOdd());
//        evenThread.setName("Even Thread");
//        oddThread.setName("Odd Thread");
//        evenThread.start();
//        oddThread.start();


        //USING REENTRANTLOCK AND CONDITION
//        EvenOddPrinterUsingReentrantLock printer2 = new EvenOddPrinterUsingReentrantLock(20);
//        Thread evenThread2 = new Thread(() -> printer2.printEven());
//        Thread oddThread2 = new Thread(() -> printer2.printOdd());
//        evenThread2.setName("Even Thread 2");
//        oddThread2.setName("Odd Thread 2");
//        evenThread2.start();
//        oddThread2.start();

        //USING SEMAPHORE
        EvenOddPrinterUsingSemaphore printer3 = new EvenOddPrinterUsingSemaphore(20);
        Thread evenThread3 = new Thread(() -> printer3.printEven());
        Thread oddThread3 = new Thread(() -> printer3.printOdd());
        evenThread3.setName("Even Thread 3");
        oddThread3.setName("Odd Thread 3");
        evenThread3.start();
        oddThread3.start();
    }
}

