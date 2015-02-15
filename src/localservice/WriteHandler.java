package localservice;

import config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import utils.Pack;

public class WriteHandler {
    
    private final Connecter connecter;
    private final Config config;
    private final byte[] bytebuffer;
    private final ByteBuffer buffer;
    private final AsynchronousSocketChannel channel;
    private final Socket remote;

    public WriteHandler(Connecter c, Config conf) {
        connecter = c;
        config = conf;
        
        int writeBufferSize = Integer.parseInt(config.getConfig(Config.LocalWBsize, "1024"));
        
        buffer = ByteBuffer.allocate(writeBufferSize);
        bytebuffer = new byte[writeBufferSize];
        channel = connecter.getBrowser();
        remote = connecter.getRemoteSocket();
    }
    
    public void process() {
        try {
            InputStream is = remote.getInputStream();
            int readn = -1;

            readn = is.read(bytebuffer);
            if (readn == -1) {
                connecter.close();
                return;
            }
            Pack.unpack(bytebuffer, readn);
            channel.write(ByteBuffer.wrap(bytebuffer, 0, readn), null, new CompletionHandler<Integer,Object>(){

                @Override
                public void completed(Integer result, Object attachment) {
                    process();
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    connecter.close();
                    return;
                }
                    
            });
            
        } catch (IOException ex) {
            connecter.close();
        }
    }
}
