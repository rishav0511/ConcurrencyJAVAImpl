package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create a pool of exactly 2 connections
        DatabaseConnectionPoolDemo.DatabaseConnectionPool pool = new DatabaseConnectionPoolDemo.DatabaseConnectionPool(2);

        // Spawn 5 threads competing for the 2 connections
        for (int i = 1; i <= 5; i++) {
            Thread t = new Thread(() -> {
                DatabaseConnectionPoolDemo.Connection conn = null;
                try {
                    // 1. Request a connection (will block if pool is empty)
                    conn = pool.getConnection();

                    // 2. Simulate database work
                    conn.executeQuery("SELECT * FROM users");
                    Thread.sleep((int) (Math.random() * 1000) + 500);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 3. Always release the connection in a finally block to prevent connection leaks
                    pool.releaseConnection(conn);
                }
            }, "Worker-" + i);
            t.start();
        }

    }
}