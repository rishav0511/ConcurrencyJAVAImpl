A Database Connection Pool is fundamentally a **Resource Pool** pattern. At its core, it operates identically to the Producer-Consumer queue we built earlier, but the roles are inverted: the worker threads are the "consumers" pulling connections, and when they are done, they act as "producers" returning the connection back to the pool.

If a thread requests a connection and the pool is exhausted (all connections are currently in use), the thread must block until another thread releases its connection.

## The Implementation

To demonstrate a production-like approach, this implementation pre-initializes a set number of mocked database connections. We use a `ReentrantLock` and a `Condition` variable to manage thread blocking.

When an interviewer asks you to build this, they are looking for you to bring up edge cases that exist in real-world libraries like HikariCP or Apache DBCP:

* **The Proxy Pattern for `.close()`:** In real JDBC, calling `conn.close()` permanently destroys the physical TCP connection to the database. Connection pools intercept this by wrapping the real connection in a **Proxy Object**. When the application calls `close()` on the proxy, it doesn't terminate the TCP connection; instead, it intercepts the method call and executes `pool.releaseConnection(this)` to return it to the queue safely.
* **Connection Leaks & Timeouts:** In the code above, if a worker thread acquires a connection, enters an infinite loop, or crashes without executing the `finally` block, that connection is lost forever. Production pools implement a separate background thread (a Reaper) that scans for connections checked out for longer than a specific timeout (e.g., 30 seconds) and forcibly reclaims them.
* **Validation (Liveness Checks):** Before returning a connection via `getConnection()`, real pools often ping the database (e.g., executing `SELECT 1`) to ensure the TCP connection hasn't been severed by the database server's idle timeout. If it's dead, the pool discards it, creates a fresh connection, and returns that instead.
