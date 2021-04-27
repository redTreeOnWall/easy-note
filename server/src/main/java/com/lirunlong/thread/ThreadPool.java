package com.lirunlong.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ThreadPool {
    private List<Thread> threadPool       = new Vector<Thread>();
    BlockingQueue<Runnable>  taskQueue;
    private int workerMax ;

    public ThreadPool(int workerMax,int queueMax){
        this.workerMax = workerMax;
        taskQueue = new LinkedBlockingDeque<Runnable>(queueMax);
    }

    // public synchronized boolean addTask(Runnable runnable) {
    public boolean addTask(Runnable runnable) {
        if (threadPool.size() < workerMax) {
            Thread newWorkerTHread = new Thread(new ThreadPoolWorker(this));
            newWorkerTHread.start();
            threadPool.add(newWorkerTHread);
        }
        return taskQueue.offer(runnable);
    }
    public void debug(){
        System.out.println("poolSize:"+threadPool.size());
        System.out.println("waitTaskNum:"+taskQueue.size());
    }
    public boolean isRunning = true;
    public void finish(){
        isRunning = false;
    }
}

class ThreadPoolWorker implements Runnable{
    private ThreadPool  threadPool;
    ThreadPoolWorker(ThreadPool pool){
        this.threadPool = pool;
    }
    @Override
    public void run() {
        while(threadPool.isRunning){
            try {
                Runnable workTask = threadPool.taskQueue.take();
                workTask.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
