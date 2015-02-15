package localservice;

import config.Config;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import utils.Pack;

public class ReadHandler {
    
    private final Connecter connecter;
    private final Config config;
    private final ByteBuffer buffer;
    private final byte[] bytebuffer;
    private final AsynchronousSocketChannel channel;
    private final Socket remote;
    
    public ReadHandler(Connecter c, Config conf) {
        connecter = c;
        config = conf;
        
        int readBufferSize = Integer.parseInt(config.getConfig(Config.LocalRBsize, "1024"));
        
        buffer = ByteBuffer.allocate(readBufferSize);
        bytebuffer = new byte[readBufferSize];
        channel = connecter.getBrowser();
        remote = connecter.getRemoteSocket();
    }

    public void process() {
        
        channel.read(buffer, null, new CompletionHandler<Integer,Object>(){

            @Override
            public void completed(Integer size, Object attachment) {
                buffer.flip();
                if (size < 0)
                    return;
                else if (size == -1) {
                    connecter.close();
                    return;
                }
                
                try {
                    buffer.get(bytebuffer, 0, size);
                    OutputStream os = remote.getOutputStream();
                    Pack.pack(bytebuffer, size);
                    
                    os.write(bytebuffer, 0, size);
                    os.flush();
                } catch (IOException ex) {
                    connecter.close();
                    return;
                }
                process();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                connecter.close();
            }
            
        });
    }
    
}
