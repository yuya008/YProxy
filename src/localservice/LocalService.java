package localservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import threadpool.ThreadPool;

public class LocalService {
    
    private final ThreadPool tp = new ThreadPool();
    private ServerSocket server;
    private Socket browser;
    
    public LocalService() {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress("127.0.0.1", 11400));
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
    
    public static void main(String[] args) {
        new LocalService().start();
        
    }
}
