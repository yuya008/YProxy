package remoteservice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import threadpool.ThreadPoolTask;
import utils.Pack;

public class ReadHandler implements ThreadPoolTask, Closeable {
    
    private final Socket local;
    private final Socket target;
    private final byte[] buffer = new byte[1024];
    
    public ReadHandler(Socket s1, Socket s2) {
        local = s1;
        target = s2;
    }
    
    @Override
    public void run() {
        InputStream in = null;
        try {
            in = local.getInputStream();
        } catch (IOException ex) {
            close();
            return;
        }
        
        OutputStream out = null;
        try {
            out = target.getOutputStream();
        } catch (IOException ex) {
            close();
            return;
        }
        
        int n = -1;
        for (;;) {
            try {
                n = in.read(buffer);
            } catch (IOException ex) {
                close();
                break;
            }
            
            if (n == -1) {
                close();
                break;
            }
            
            try {
                Pack.unpack(buffer, n);
                out.write(buffer, 0, n);
            } catch (IOException ex) {
                close();
                break;
            }
        }
    }

    @Override
    public void close() {
        try {
            local.close();
            target.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
