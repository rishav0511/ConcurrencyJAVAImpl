package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnectionPoolDemo {
    static class Connection{
        private String connectionId;

        public Connection() {
            this.connectionId = UUID.randomUUID().toString();
        }

        public void executeQuery(String query) {
            System.out.println(Thread.currentThread().getName()
                    + " executing '" + query + "' on connection [" + connectionId + "]");
        }

        public String getConnectionId() {
            return connectionId;
        }
    }

    static class DatabaseConnectionPool {
        private final Queue<Connection> availableConnections;
        private final int poolSize;

        public DatabaseConnectionPool(int poolSize) {
            this.poolSize = poolSize;
            this.availableConnections = new LinkedList<>();
            for(int i=0;i<poolSize;i++) {
                availableConnections.add(new Connection());
            }
            System.out.println("Connection Pool initialized with " + poolSize + " connections.");
        }

        private final ReentrantLock lock = new ReentrantLock();
        private final Condition connectionAvailable = lock.newCondition();

        public Connection getConnection() throws InterruptedException {
            lock.lock();
            try {
                // Block if no connections are available
                while (availableConnections.isEmpty()) {
                    System.out.println(Thread.currentThread().getName() + " is waiting for a connection...");
                    connectionAvailable.await();
                }
                Connection available = availableConnections.poll();
                System.out.println(Thread.currentThread().getName() + " acquired connection [" + available.getConnectionId() + "]");
                return available;
            } finally {
                lock.unlock();
            }
        }

        public void releaseConnection(Connection connection) {
            lock.lock();
            try {
                // Prevent adding more connections than the max size (defends against rogue releases)
                if(availableConnections.size() < poolSize) {
                    availableConnections.add(connection);
                    System.out.println(Thread.currentThread().getName() + " released connection [" + connection.getConnectionId() + "]");

                    // Signal one waiting thread that a connection is now available
                    connectionAvailable.signal();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
