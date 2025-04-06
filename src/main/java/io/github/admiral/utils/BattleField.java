package io.github.admiral.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Where the soldier execute its corresponding task. It is actually a thread pool.
 */
public class BattleField {

    /**Thread pool configuration.*/
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final BlockingQueue<Runnable> workQueue;

    public BattleField(int corePoolSize, int maxPoolSize, long keepAliveTime, int blockingQueueCapacity) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = new LinkedBlockingQueue<>(blockingQueueCapacity);
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue);
    }
}
