package scau.os.soos.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadsPool extends ThreadPoolExecutor {
    static {
        pool = new ThreadsPool(2, 2, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    private static final ThreadsPool pool;

    private ThreadsPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }


    public static void run(Runnable task) {
        pool.execute(task);
    }

    public static void stop() {
        pool.shutdownNow();
        System.out.println("stopping");
    }
}
