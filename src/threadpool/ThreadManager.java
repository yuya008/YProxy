package threadpool;

class ThreadManager implements Runnable {
    
    private final ThreadPool tp;
    
    public ThreadManager(ThreadPool tp) {
        this.tp = tp;
    }
    
    @Override
    public void run() {
        ThreadPoolTask t = null;
        for (;;) {
            t = tp.pollTask();
            if (t == null)
                continue;
            
            t.run();
        }
    }
    
}
