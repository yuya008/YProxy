package localservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import threadpool.ThreadPool;
import yproxy.Config;

public class LocalService {
    
    private final ThreadPool tp = new ThreadPool();
    private ServerSocket server;
    private Socket browser;
    
    public LocalService() {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(Config.local_hostname, Config.local_port));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void start() {
        for (;;) {
            try {
                browser = server.accept();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Connecter conn = new Connecter(browser, tp);
            tp.putTask(conn);
        }
    }
    
//    unit test
//    public static void main(String[] args) {
//        new LocalService().start();
//    }
    
}
