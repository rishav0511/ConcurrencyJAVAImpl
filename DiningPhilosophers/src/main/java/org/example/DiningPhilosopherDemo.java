package org.example;

public class DiningPhilosopherDemo {
    static class Fork {
        int id;

        public Fork(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    static class Philosopher implements Runnable {
        private final int id;
        private final Fork firstFork;
        private final Fork secondFork;

        public Philosopher(int id, Fork left, Fork right) {
            this.id = id;
            if(left.getId()<right.getId()) {
                this.firstFork = left;
                this.secondFork = right;
            } else {
                this.firstFork = right;
                this.secondFork = left;
            }
        }

        private void doAction(String action) throws InterruptedException {
            System.out.println("Philosopher " + id + " " + action);
            Thread.sleep(((int) (Math.random() * 100)));
        }


        @Override
        public void run() {
            try {
                while (true) {
                    // Thinking
                    doAction("is thinking.");

                    // Acquiring locks in strict numerical order
                    synchronized (firstFork) {
                        doAction("picked up lower-order fork " + firstFork.getId());
                        synchronized (secondFork) {
                            doAction("picked up higher-order fork " + secondFork.getId() + " - EATING");
                            // Eating takes time
                            Thread.sleep(((int) (Math.random() * 100)));
                        }
                        // Dropping higher-order fork implicitly as we exit the inner block
                        doAction("put down higher-order fork " + secondFork.getId());
                    }
                    // Dropping lower-order fork implicitly as we exit the outer block
                    doAction("put down lower-order fork " + firstFork.getId() + " and goes back to thinking.");
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
