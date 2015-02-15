package remoteservice;

import config.Config;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import utils.Pack;

public class ReadHandler {
    private final Config config;
    private final ByteBuffer buffer;
    private final byte[] bytebuffer;
    private final AsynchronousSocketChannel channel;
    private final Socket remote;
    
    public ReadHandler(AsynchronousSocketChannel c, Socket r, Config conf) {
        channel = c;
        config = conf;
        
        int readBufferSize = Integer.parseInt(config.getConfig(Config.RemoteRBsize, "1024"));
        
        buffer = ByteBuffer.allocate(readBufferSize);
        bytebuffer = new byte[readBufferSize];
        remote = r;
    }

    public void process() {
        
        channel.read(buffer, null, new CompletionHandler<Integer,Object>(){

            @Override
            public void completed(Integer size, Object attachment) {
                buffer.flip();
                if (size < 0)
                    return;
                else if (size == -1) {
                    close();
                    return;
                }
                
                try {
                    buffer.get(bytebuffer, 0, size);
                    OutputStream os = remote.getOutputStream();
                    
                    Pack.unpack(bytebuffer, size);
                    
                    os.write(bytebuffer, 0, size);
                    os.flush();
                } catch (IOException ex) {
                    close();
                    return;
                }
                process();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                close();
            }
            
        });
    }
    
    public void close() {
        try {
            remote.close();
            channel.close();
        } catch (IOException ex) {
        }
    }
}
