package com.stardust.sdk.zzftp.thread_pool_manager;

/**
 * 带有优先级的Runnable类型
 */
public class PriorityRunnable implements Runnable {

    public final Priority priority;//任务优先级
    private final Runnable runnable;//任务真正执行者
    long SEQ;//任务唯一标示

    public PriorityRunnable(Runnable runnable) {
        this(Priority.NORMAL, runnable);
    }

    public PriorityRunnable(Priority priority, Runnable runnable) {
        this.priority = priority == null ? Priority.NORMAL : priority;
        this.runnable = runnable;
    }

    @Override
    public final void run() {
        this.runnable.run();
    }
}