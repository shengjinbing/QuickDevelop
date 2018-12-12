package com.modesty.quickdevelop.utils.threadutil;

import java.util.concurrent.*;

/**
 * 任务提交给线程池之后的处理策略
 *1、如果当前线程数<corePoolSize，如果是则创建新的线程执行该任务
 *2、如果当前线程数>=corePoolSize，则将任务存入BlockingQueue<Runnable>
 *3、如果阻塞队列已满，且当前线程数<maximumPoolSize，则新建线程执行该任务。
 *4、如果阻塞队列已满，且当前线程数>=maximumPoolSize，则抛出异常RejectedExecutionException，告诉调用者无法再接受任务了。
 *
 * 线程池刚创建是空的，出的除非调用了prestartAllCoreThreads()或者prestartCoreThread()方法来预创建corePoolSize个线程或者一个线程
 */
public class CustomThreadPool {

    private TimeUnit                unit          = TimeUnit.SECONDS;
    /*
    阻塞队列，如果BlockingQueue是空的，从BlockingQueue取东西的操作将会被阻断进入等待状态，
    直到BlockingQueue进了东西才会被唤醒，同样，如果BlockingQueue是满的，
    任何试图往里存东西的操作也会被阻断进入等待状态，直到BlockingQueue里有空间时才会被唤醒继续操作。

    1.ArrayBlockingQueue(有界队列)： FIFO 队列，规定大小的BlockingQueue，其构造函数必须带一个int参数来指明其大小
    2.LinkedBlockingQueue(无界队列)：FIFO 队列，大小不定的BlockingQueue，若其构造函数带一个规定大小的参数，生
       成的BlockingQueue有大小限制，若不带大小参数，所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定。
    3.PriorityBlockingQueue：优先级队列， 类似于LinkedBlockingQueue，但队列中元素非 FIFO,
       依据对象的自然排序顺序或者是构造函数所带的Comparator决定的顺序
    4.SynchronousQueue(直接提交策略): 交替队列，队列中操作时必须是先放进去，接着取出来，交替着去处理元素的添加和移除
    */
    private BlockingQueue<Runnable> workQueue     = new ArrayBlockingQueue(100);
    private ThreadFactory           threadFactory = Executors.defaultThreadFactory();


    /*当添加任务出错时的策略捕获器，如果出现错误，则直接抛出异常
    ThreadPoolExecutor.AbortPolicy

    当添加任务出错时的策略捕获器，如果出现错误，直接执行加入的任务
     ThreadPoolExecutor.CallerRunsPolicy

    当添加任务出错时的策略捕获器,如果出现错误,移除第一个任务,执行加入的任务
     ThreadPoolExecutor.DiscardOldestPolicy

    当添加任务出错时的策略捕获器，如果出现错误，不做处理
    ThreadPoolExecutor.DiscardPolicy*/
    private       RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    private final ThreadPoolExecutor       mThreadPoolExecutor;

       public CustomThreadPool(int corePoolSize,
                            int maximumPoolSize,
                            long keepAliveTime) {
        mThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,//核心池的大小,最多创建多少线程，当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中
                keepAliveTime,//线程池最大线程数
                unit,//保持时间
                workQueue,//任务队列
                threadFactory,//线程工厂
                handler);// 捕获异常
    }

    /**
     * 判断线程池是否关闭
     */
    public void isShutdown() {
        mThreadPoolExecutor.isShutdown();
    }

    /**
     * 判断线程池中任务是否执行完成
     */
    public void isTerminated() {
        mThreadPoolExecutor.isShutdown();
    }

    /**
     * 调用后不再接收新任务，如果里面有任务，就执行完
     */
    public void shutdown() {
        mThreadPoolExecutor.shutdown();
    }

    /**
     * 调用后不再接受新任务，如果有等待任务，移出队列；有正在执行的，尝试停止之
     */
    public void shutdownNow() {
        mThreadPoolExecutor.shutdownNow();
    }

    /**
     * 提交执行任务
     *
     * @param task
     */
    public void submit(Runnable task) {
        mThreadPoolExecutor.submit(task);
    }

    /**
     * 执行任务
     *
     * @param command
     */
    public void execute(Runnable command) {
        mThreadPoolExecutor.execute(command);
    }

}
