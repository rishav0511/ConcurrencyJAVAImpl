package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int numPhilosophers = 5;
        DiningPhilosopherDemo.Philosopher[] philosophers = new DiningPhilosopherDemo.Philosopher[numPhilosophers];

        DiningPhilosopherDemo.Fork[] forks = new DiningPhilosopherDemo.Fork[numPhilosophers];
        // Initialize the forks with IDs
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new DiningPhilosopherDemo.Fork(i);
        }

        // Initialize and start the philosophers
        for (int i = 0; i < numPhilosophers; i++) {
            DiningPhilosopherDemo.Fork leftFork = forks[i];
            // The last philosopher wraps around to fork 0
            DiningPhilosopherDemo.Fork rightFork = forks[(i + 1) % numPhilosophers];

            philosophers[i] = new DiningPhilosopherDemo.Philosopher(i, leftFork, rightFork);
            Thread t = new Thread(philosophers[i], "Philosopher-" + i);
            t.start();
        }
    }
}