package com.lingtao.thread_pool_manager;

import android.os.Handler;

import java.util.concurrent.ExecutorService;

/**
 * 线程池管理代理类
 * 示例:
 * <p>
 * ExecutorManager.getInstance().request(new Runnable() {
 *
 * @Override public void run() {
 * // 自己的代码
 * }
 * };)
 * </>
 */
public class ExecutorManager {

    private ExecutorService executorService = new PriorityExecutor(5, true);
    private Handler mH = new Handler();

    private ExecutorManager() {

    }

    private static class Instance {
        private static ExecutorManager _instance = new ExecutorManager();

    }

    public static ExecutorManager getInstance() {
        return Instance._instance;
    }

    /**
     * 优先级正常状态
     */
    public void request(Runnable runnable) {
        executorService.execute(new PriorityRunnable(runnable));
    }


    /**
     * 优先级正常状态
     */
    public void requestDelayed(Runnable runnable, long delayMillis) {
        mH.postDelayed(() -> {
            executorService.execute(new PriorityRunnable(runnable));
        }, delayMillis);
    }

    /**
     * 优先级高
     */
    public void requestH(Runnable runnable) {
        executorService.execute(new PriorityRunnable(Priority.HIGH, runnable));
    }

    /**
     * 优先级低
     */
    public void requestL(Runnable runnable) {
        executorService.execute(new PriorityRunnable(Priority.LOW, runnable));
    }
}
