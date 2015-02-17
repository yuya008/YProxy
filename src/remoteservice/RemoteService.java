package remoteservice;

import config.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import utils.Pack;
import yproxy.IoProcess;
import yproxy.Service;

public class RemoteService implements Service {
    
    private Config config;
    private AsynchronousChannelGroup cgroup;
    private AsynchronousServerSocketChannel ass;
    
    public RemoteService(Config c) {
        config = c;
        
        String hostname = config.getConfig(Config.Remotehost, "127.0.0.1");
        int tsn = Integer.parseInt(config.getConfig(Config.Remotethreads, "1024"));
        int port = Integer.parseInt(config.getConfig(Config.Remoteport, "11400"));
        
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
            public void completed(AsynchronousSocketChannel local, Object attachment) {
                ass.accept(null, this);
                
                Socks5 socks = new Socks5(local);
                if (socks.parseAndResponse()) {
                    if (socks.getHost().equals("") || socks.getPort() == 0) {
                        try {
                            local.close();
                        } catch (IOException ex) {
                        }
                        return;
                    }
                    
                    System.err.println("Connecting... "+ socks.getHost() +":"+ socks.getPort());
                    
                    try {
                        Socket target = new Socket(socks.getHost(), socks.getPort());
                        
                        IoProcess rd = new IoProcess(local, target, config);
                        rd.setEncryptHandler((ByteBuffer buffer, int size) -> {
                            Pack.unpack(buffer, size);
                        });
                        
                        IoProcess wr = new IoProcess(local, target, config);
                        wr.setEncryptHandler((ByteBuffer buffer, int size) -> {
                            Pack.pack(buffer, size);
                        });
                        
                        rd.registReadEventLoop(config.getConfig(Config.RemoteRBsize, "1024"));
                        wr.registWriteEventLoop(config.getConfig(Config.RemoteWBsize, "1024"));
                    } catch (IOException ex) {
                        try {
                            local.close();
                        } catch (IOException e) {
                        }
                    }
                } else {
                    try {
                        local.close();
                    } catch (IOException ex) {
                    }
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
//        new RemoteService().start();
//    }
    
}
