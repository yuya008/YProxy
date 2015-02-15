package remoteservice;

import config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import utils.Pack;


public class WriteHandler {
    private final Config config;
    private final ByteBuffer buffer;
    private final byte[] bytebuffer;
    private final AsynchronousSocketChannel channel;
    private final Socket remote;
    
    public WriteHandler(AsynchronousSocketChannel c, Socket r, Config conf) {
        channel = c;
        config = conf;
        
        int readBufferSize = Integer.parseInt(config.getConfig(Config.RemoteWBsize, "1024"));
        
        buffer = ByteBuffer.allocate(readBufferSize);
        bytebuffer = new byte[readBufferSize];
        remote = r;
    }
    
    public void process() {
        try {
            InputStream is = remote.getInputStream();
            int readn = -1;
            readn = is.read(bytebuffer);
            if (readn == -1) {
                close();
                return;
            }
            Pack.pack(bytebuffer, readn);
            channel.write(ByteBuffer.wrap(bytebuffer, 0, readn), null, new CompletionHandler<Integer,Object>(){

                @Override
                public void completed(Integer result, Object attachment) {
                    process();
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    close();
                    return;
                }
                    
            });       
        } catch (IOException ex) {
            close();
        }
    }
    
    public void close() {
        try {
            remote.close();
            channel.close();
        } catch (IOException ex) {
        }
    }
}
