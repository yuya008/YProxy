package remoteservice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import threadpool.ThreadPoolTask;
import utils.Pack;
import yproxy.Config;

public class WriteHandler implements ThreadPoolTask, Closeable {
    
    private final Socket local;
    private final Socket target;
    private final byte[] buffer = new byte[Config.remote_write_buffer];
    
    public WriteHandler(Socket s1, Socket s2) {
        local = s1;
        target = s2;
    }
    
    @Override
    public void run() {
        InputStream in = null;
        try {
            in = target.getInputStream();
        } catch (IOException ex) {
            close();
            return;
        }
        
        OutputStream out = null;
        try {
            out = local.getOutputStream();
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
                Pack.pack(buffer, n);
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
