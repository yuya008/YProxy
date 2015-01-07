package remoteservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import threadpool.ThreadPool;

public class RemoteService {
    
    private final ThreadPool tp = new ThreadPool();
    private ServerSocket server;
    private Socket local;
    
    public RemoteService() {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress("127.0.0.1", 5500));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void start() {
        for (;;) {
            try {
                local = server.accept();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Requester conn = new Requester(local, tp);
            tp.putTask(conn);
        }
    }

    public static void main(String[] args) {
        new RemoteService().start();
    }
}
