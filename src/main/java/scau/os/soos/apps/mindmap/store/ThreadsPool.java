package scau.os.soos.apps.mindmap.store;

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

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        System.out.println(t.getName() + "执行");
        if (!SystemStore.isLoadingState()) {
            SystemStore.setLoadingState(true);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (getQueue().isEmpty() && getActiveCount() <= 1) {
            SystemStore.setLoadingState(false);
        }
    }

    public static void run(Runnable task) {
        pool.execute(task);
    }

    public static void stop() {
        pool.shutdown();
        try {
            // 等待现有任务结束
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            // 保留中断状态
            Thread.currentThread().interrupt();
        }
    }
}
