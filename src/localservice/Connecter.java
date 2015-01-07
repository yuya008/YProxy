package localservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import threadpool.ThreadPool;
import threadpool.ThreadPoolTask;

public class Connecter implements ThreadPoolTask {
    
    private final Socket browser;
    private Socket remote;
    private final ThreadPool tp;
    
    public Connecter(Socket s, ThreadPool t) {
        browser = s;
        tp = t;
    }

    @Override
    public void run() {
        remote = new Socket();
        try {
            remote.connect(new InetSocketAddress("127.0.0.1", 5500));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        tp.putTask(new ReadHandler(browser, remote));
        tp.putTask(new WriteHandler(browser, remote));
    }
}
