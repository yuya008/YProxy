package yproxy;

import config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import utils.EncryptWrap;
import utils.Pack;

public class IoProcess {
    private final Config config;
    private ByteBuffer rbuffer;
    private byte[] wbuffer;
    private final AsynchronousSocketChannel channel;
    private final Socket remote;
    private EncryptWrap encrypt;

    public IoProcess(AsynchronousSocketChannel c, Socket r, Config conf) {
        config = conf;
        channel = c;
        remote = r;
    }
    
    public void setEncryptHandler(EncryptWrap e) {
        encrypt = e;
    }
    
    public void registReadEventLoop(String rbsize) {
        
        if (rbuffer == null)
            rbuffer = ByteBuffer.allocate(Integer.parseInt(rbsize));
        
        channel.read(rbuffer, null, new CompletionHandler<Integer,Object>(){

            @Override
            public void completed(Integer size, Object attachment) {
                rbuffer.flip();
                if (size < 0)
                    return;
                else if (size == -1) {
                    close();
                    return;
                }
                
                try {
                    OutputStream os = remote.getOutputStream();
                    encrypt.wrap(rbuffer, size);
                    os.write(rbuffer.array(), 0, size);
                    os.flush();
                } catch (IOException ex) {
                    close();
                    return;
                }
                registReadEventLoop(rbsize);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                close();
            }
            
        });
    }
    
    public void registWriteEventLoop(String wbsize) {
        try {
            if (wbuffer == null)
                wbuffer = new byte[Integer.parseInt(wbsize)];
            
            InputStream is = remote.getInputStream();
            int readn = is.read(wbuffer);
            if (readn == -1) {
                close();
                return;
            }
            
            ByteBuffer buffer = ByteBuffer.wrap(wbuffer, 0, readn);
            encrypt.wrap(buffer, readn);
            
            channel.write(buffer, null, new CompletionHandler<Integer,Object>(){

                @Override
                public void completed(Integer result, Object attachment) {
                    registWriteEventLoop(wbsize);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    close();
                }
                    
            });
            
        } catch (IOException ex) {
            close();
        }
    }
    
    private void close() {
        try {
            remote.close();
            channel.close();
        } catch (IOException ex) {
        }
    }
}
