package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static final int NUM_THREADS = 20;

    private static volatile ThreadPool sharedInstance;

    private ExecutorService pool;

    private ThreadPool() {
        pool = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void addTask(Runnable task) {
        synchronized (pool) {
            pool.submit(task);
        }
    }


    public static ThreadPool getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (ThreadPool.class) {
                sharedInstance = new ThreadPool();
            }
        }
        return sharedInstance;
    }
}
