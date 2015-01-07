package remoteservice;

import java.io.IOException;
import java.net.Socket;
import threadpool.ThreadPool;
import threadpool.ThreadPoolTask;

public class Requester implements ThreadPoolTask {
    
    private final Socket local;
    private final ThreadPool tp;
    private final Socks5 s5;
    private Socket target;
    
    public Requester(Socket s, ThreadPool t) {
        local = s;
        tp = t;
        s5 = new Socks5(local);
    }

    @Override
    public void run() {
        if (!s5.parseAndEmit()) {
            try {
                local.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (s5.getHost() == null) {
            try {
                local.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        try {
            target = new Socket(s5.getHost(), s5.getPort());
        } catch (IOException ex) {
            try {
                local.close();
            } catch (IOException e) {
                ex.printStackTrace();
            }
            return;
        }
        tp.putTask(new ReadHandler(local, target));
        tp.putTask(new WriteHandler(local, target));
    }

}
