package localservice;

import config.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import yproxy.Service;

public class LocalService implements Service {
    
    private Config config;
    private AsynchronousChannelGroup cgroup;
    private AsynchronousServerSocketChannel ass;
    
    public LocalService(Config c) {
        config = c;
        
        String hostname = config.getConfig(Config.Localhost, "127.0.0.1");
        int tsn = Integer.parseInt(config.getConfig(Config.Localthreads, "1024"));
        int port = Integer.parseInt(config.getConfig(Config.Localport, "11400"));
        
        try {
            cgroup = AsynchronousChannelGroup.withFixedThreadPool(tsn, Executors.defaultThreadFactory());
            ass = AsynchronousServerSocketChannel.open(cgroup);
            ass.bind(new InetSocketAddress(hostname, port));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void start() {
        ass.accept(null, new CompletionHandler<AsynchronousSocketChannel,Object>(){

            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                ass.accept(null, this);
                Connecter connecter = new Connecter(client, config);
                if (connecter.connectRemoteService()) {
                    ReadHandler r = new ReadHandler(connecter, config);
                    r.process();
                    WriteHandler w = new WriteHandler(connecter, config);
                    w.process();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }   
            
        });
    }
    
//    unit test
//    public static void main(String[] args) {
//        new LocalService().start();
//    }
    
}
