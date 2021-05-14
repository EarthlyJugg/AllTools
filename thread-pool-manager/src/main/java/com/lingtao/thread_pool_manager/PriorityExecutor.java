package com.lingtao.thread_pool_manager;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 1.execute一个线程之后，如果线程池中的线程数未达到核心线程数，则会立马启用一个核心线程去执行
 * 2.execute一个线程之后，如果线程池中的线程数已经达到核心线程数，且workQueue未满，则将新线程放入workQueue中等待执行
 * 3.execute一个线程之后，如果线程池中的线程数已经达到核心线程数但未超过非核心线程数，且workQueue已满，则开启一个非核心线程来执行任务
 * 4.execute一个线程之后，如果线程池中的线程数已经超过非核心线程数，则拒绝执行该任务
 */
public class PriorityExecutor extends ThreadPoolExecutor {

    private static final int CORE_POOL_SIZE = 5;//核心线程池大小
    private static final int MAXIMUM_POOL_SIZE = 128;//最大线程池队列大小
    private static final int KEEP_ALIVE = 1;//保持存活时间，当线程数大于corePoolSize的空闲线程能保持的最大时间。
    private static final AtomicLong SEQ_SEED = new AtomicLong(0);//主要获取添加任务

    /**
     * 创建线程工厂
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "download#" + mCount.getAndIncrement());
        }
    };

    /**
     * 线程队列方式 先进先出
     */
    private static final Comparator<Runnable> FIFO = new Comparator<Runnable>() {
        @Override
        public int compare(Runnable lhs, Runnable rhs) {
            if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                PriorityRunnable lpr = ((PriorityRunnable) lhs);
                PriorityRunnable rpr = ((PriorityRunnable) rhs);
                int result = lpr.priority.ordinal() - rpr.priority.ordinal();
                return result == 0 ? (int) (lpr.SEQ - rpr.SEQ) : result;
            } else {
                return 0;
            }
        }
    };

    /**
     * 线程队列方式 后进先出
     */
    private static final Comparator<Runnable> LIFO = new Comparator<Runnable>() {
        @Override
        public int compare(Runnable lhs, Runnable rhs) {
            if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                PriorityRunnable lpr = ((PriorityRunnable) lhs);
                PriorityRunnable rpr = ((PriorityRunnable) rhs);
                int result = lpr.priority.ordinal() - rpr.priority.ordinal();
                return result == 0 ? (int) (rpr.SEQ - lpr.SEQ) : result;
            } else {
                return 0;
            }
        }
    };

    /**
     * 默认工作线程数5
     *
     * @param fifo 优先级相同时, 等待队列的是否优先执行先加入的任务.
     */
    public PriorityExecutor(boolean fifo) {
        this(CORE_POOL_SIZE, fifo);
    }

    /**
     * @param poolSize 工作线程数
     * @param fifo     优先级相同时, 等待队列的是否优先执行先加入的任务.
     */
    public PriorityExecutor(int poolSize, boolean fifo) {
        this(poolSize,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE, fifo ? FIFO : LIFO),
                sThreadFactory);
    }

    /**
     * @param corePoolSize    线程池核心线程数量
     * @param maximumPoolSize 线程池维护线程的最大数量
     * @param keepAliveTime   非核心线程的超时时长，当系统中非核心线程闲置时间超过keepAliveTime之后，则会被回收。如果ThreadPoolExecutor的allowCoreThreadTimeOut属性设置为true，则该参数也表示核心线程的超时时长
     * @param unit            线程池维护线程所允许的空闲时间的单位
     * @param workQueue       线程池所使用的缓冲队列
     * @param threadFactory   线程池用于创建线程
     * @handler： 线程池对拒绝任务的处理策略
     */
    public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * 判断当前线程池是否繁忙
     *
     * @return
     */
    public boolean isBusy() {
        return getActiveCount() >= getCorePoolSize();
    }

    //　shutdown()：不会立即终止线程池，而是要等所有任务缓存队列中的任务都执行完后才终止，但再也不会接受新的任务
    //　shutdownNow()：立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列，返回尚未执行的任务

    /**
     * 提交任务
     *
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {
        if (runnable instanceof PriorityRunnable) {
            ((PriorityRunnable) runnable).SEQ = SEQ_SEED.getAndIncrement();
        }
        super.execute(runnable);
    }
}