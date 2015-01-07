package threadpool;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    
    private LinkedBlockingQueue<ThreadPoolTask> taskQueue = new LinkedBlockingQueue();
    
    public ThreadPool() {
        createThread();
    }
    
    private void createThread() {
        for (int i = 0; i < 1024; i++) {
            new Thread(new ThreadManager(this)).start();
        }
    }
    
    public void putTask(ThreadPoolTask t) {
        try {
            taskQueue.put(t);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public ThreadPoolTask pollTask() {
        ThreadPoolTask t = null;
        try {
            t = taskQueue.take();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return t;
    }
    
}
