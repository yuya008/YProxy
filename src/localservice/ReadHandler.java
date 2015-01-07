package localservice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import threadpool.ThreadPoolTask;
import utils.Pack;
import yproxy.Config;

public class ReadHandler implements ThreadPoolTask, Closeable {
    
    private final Socket browser;
    private final Socket remote;
    private final byte[] buffer = new byte[Config.local_read_buffer];
    
    public ReadHandler(Socket s1, Socket s2) {
        browser = s1;
        remote = s2;
    }
    
    @Override
    public void run() {
        InputStream in = null;
        try {
            in = browser.getInputStream();
        } catch (IOException ex) {
            close();
            return;
        }
        
        OutputStream out = null;
        try {
            out = remote.getOutputStream();
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
            browser.close();
            remote.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
