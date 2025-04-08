package io.github.admiral.soldier;

import io.github.admiral.common.BattleFieldExceptionHandler;

import java.util.concurrent.*;

/**
 * Where the soldier execute its corresponding task. It is actually a thread pool.
 */
public class BattleField {

    /**Thread pool configuration.*/
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final ThreadPoolExecutor battleFieldExecutor;
    private final BlockingQueue<Runnable> workQueue;

    /** Time out restriction for execution.*/
    private final int timeout;

    /** Exception Handler when happen exception.*/
    private final BattleFieldExceptionHandler battleFieldExceptionHandler;

    public BattleField(int corePoolSize, int maxPoolSize, long keepAliveTime, int blockingQueueCapacity,
                       int timeout, BattleFieldExceptionHandler battleFieldExceptionHandler) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = new LinkedBlockingQueue<>(blockingQueueCapacity);
        battleFieldExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue);
        this.timeout = timeout;
        this.battleFieldExceptionHandler = battleFieldExceptionHandler;
    }

    /** Submit a soldier wrapper to */
    public void submit(SoldierWrapper soldierWrapper){
        CompletableFuture.supplyAsync(soldierWrapper, battleFieldExecutor)
                .orTimeout(timeout, TimeUnit.MILLISECONDS)
                .whenComplete((result, throwable) -> {
            if (throwable != null) {
                battleFieldExceptionHandler.handle(soldierWrapper, throwable);
            }
        });
    }

}
