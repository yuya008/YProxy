package localservice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import threadpool.ThreadPoolTask;
import utils.Pack;

public class WriteHandler implements ThreadPoolTask, Closeable {
    
    private final Socket browser;
    private final Socket remote;
    private final byte[] buffer = new byte[1024];
    
    public WriteHandler(Socket s1, Socket s2) {
        browser = s1;
        remote = s2;
    }
    
    @Override
    public void run() {
        InputStream in = null;
        try {
            in = remote.getInputStream();
        } catch (IOException ex) {
            close();
            return;
        }
        
        OutputStream out = null;
        try {
            out = browser.getOutputStream();
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
            browser.close();
            remote.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
