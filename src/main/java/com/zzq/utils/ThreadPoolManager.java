package com.zzq.utils;

/**
 * @author zhangzhiqiang
 * @date 2018-05-09 16:22
 * &Desc 线程池管理类
 */

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class ThreadPoolManager {
    private static Hashtable tb = new Hashtable();
    /**
     *  线程池维护线程的最少数量
     */
    private final static int CORE_POOL_SIZE = 4;
    /**
     * 线程池维护线程的最大数量
     */
    private final static int MAX_POOL_SIZE = 32;
    /**
     * 线程池维护线程所允许的空闲时间
     */
    private final static int KEEP_ALIVE_TIME = 1;
    /**
     * 线程池所使用的缓冲队列大小
     */
    private final static int WORK_QUEUE_SIZE = 1000;
    /**
     * 请求Request缓冲队列
     */
    public Queue msgQueue = new LinkedList();
    /**
     * 访问请求Request缓存的调度线程
     */
    final Runnable accessBufferThread = new Runnable() {
        @Override
        public void run() {
            // 查看是否有待定请求,如果有,则添加到线程池中
            if (hasMoreAcquire()) {
                threadPool.execute((Runnable) msgQueue.poll());
            }
        }
    };

    //handler - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
    final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(r + "request 放入队列中重新等待执行" + r);
            msgQueue.offer(r);
        }
    };
    // 管理线程池
     public  final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);
    // 调度线程池
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    //定时调度
    final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(
            accessBufferThread, 0, 1, TimeUnit.SECONDS);


    /**
     * 根据key取得对应实例
     *
     * @param key
     * @return
     */
    public static synchronized ThreadPoolManager getInstance(String key) {

        ThreadPoolManager obj = (ThreadPoolManager)tb.get(key);

        if (obj == null)

        {

            System.out.println("new thread pool :" + key);

            obj = new ThreadPoolManager();

            tb.put(key, obj);

        }

        return obj;
    }

    public ThreadPoolManager() {
    }

    private boolean hasMoreAcquire() {
        return !msgQueue.isEmpty();
    }

    public void addTask(Runnable task) {
        threadPool.execute(task);
    }
    public void destory(){
    	msgQueue.clear();
    	tb.clear();
    }
}



