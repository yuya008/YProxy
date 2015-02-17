package localservice;

import yproxy.IoProcess;
import config.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import utils.Pack;
import yproxy.Service;

public class LocalService implements Service {
    
    private Config config;
    private AsynchronousChannelGroup cgroup;
    private AsynchronousServerSocketChannel ass;
    
    public LocalService(Config c) {
        config = c;
        
        String hostname = config.getConfig(Config.Localhost, "127.0.0.1");
        int lthreads = Integer.parseInt(config.getConfig(Config.Localthreads, "1024"));
        int port = Integer.parseInt(config.getConfig(Config.Localport, "11400"));
        
        try {
            cgroup = AsynchronousChannelGroup.withFixedThreadPool(lthreads, Executors.defaultThreadFactory());
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
                    IoProcess rd = new IoProcess(connecter.getBrowser(), connecter.getRemoteSocket(), config);
                    IoProcess wr = new IoProcess(connecter.getBrowser(), connecter.getRemoteSocket(), config);
                    
                    
                    rd.setEncryptHandler((ByteBuffer buffer, int size) -> {
                        Pack.pack(buffer, size);
                    });
                    rd.registReadEventLoop(config.getConfig(Config.LocalRBsize, "1024"));
                    
                    
                    wr.setEncryptHandler((ByteBuffer buffer, int size) -> {
                        Pack.unpack(buffer, size);
                    });
                    wr.registWriteEventLoop(config.getConfig(Config.LocalWBsize, "1024"));
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
