package remoteservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Pack;

public class Socks5 {
    
    private final AsynchronousSocketChannel local;
    private String host = null;
    private int port = 0;
    
    public Socks5(AsynchronousSocketChannel s) {
        local = s;
    }
    
    public boolean parseAndEmit() {
        // step 1
        byte[] b = new byte[3];
        ByteBuffer buf = ByteBuffer.allocate(3);
        try {
            Integer n = local.read(buf).get();
            
            if (n != 3)
                return false;
            
            buf.flip();
            buf.get(b, 0, 3);
            Pack.unpack(b);
            
            if (b[0] != 0x05 || b[1] != 0x01 || b[2] != 0x00)
                return false;
        } catch (InterruptedException | ExecutionException ex) {
            return false;
        }
        
        // step 2
        byte[] say = new byte[]{0x05, 0x00};
        Pack.pack(say);
        Future<Integer> future = local.write(ByteBuffer.wrap(say, 0, 2));
        try {
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return false;
        }
        if (!future.isDone())
            return false;
        
        // step 3
        try {
            byte[] bport = null, b = null;
            int n = -1;
            if (Pack.unpack(in.read()) != 0x05 || Pack.unpack(in.read()) != 0x01 || Pack.unpack(in.read()) != 0x00)
                return false;
                
            int type = Pack.unpack(in.read());
            
            if (type == 0x03) {
                int hostlen = in.read();
                
                if (hostlen == -1)
                    return false;
                
                hostlen = Pack.unpack(hostlen);
                b = new byte[1024];
                n = in.read(b);
                
                Pack.unpack(b, n);
                
                this.host = new String(b, 0, hostlen);
                bport = new byte[]{b[n-2], b[n-1]};
                this.port = bytes2int(new byte[]{0, 0, bport[0], bport[1]});
            } else if (type == 0x01) {
                this.host = Pack.unpack(in.read())+"."+Pack.unpack(in.read())+"."+Pack.unpack(in.read())+"."+Pack.unpack(in.read());
                bport = new byte[2];
                in.read(bport);
                Pack.unpack(bport);
                this.port = bytes2int(new byte[]{0, 0, bport[0], bport[1]});
            } else {
                return false;
            }
        } catch (IOException | ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex) {
            return false;
        }
        
        // step 4
        try {
            byte[] b1 = new byte[]{5, 0, 0, 1};
            Pack.pack(b1);
            out.write(b1);
            byte[] b2 = new byte[]{0,0,0,0,0,0};
            Pack.pack(b2);
            out.write(b2);
            out.flush();
        } catch (IOException ex) {
            return false;
        }
        
        return true;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public static int bytes2int(byte[] bytes){  
        int result = 0;  
        if(bytes.length == 4){  
            int a = (bytes[0] & 0xff) << 24; 
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8; 
            int d = (bytes[3] & 0xff);
            result = a|b|c|d;
        }  
        return result;  
    }
    
    public static byte[] int2bytes(int num){  
        byte[] result = new byte[4];  
        result[0] = (byte)((num >>> 24) & 0xff);
        result[1] = (byte)((num >>> 16) & 0xff);
        result[2] = (byte)((num >>> 8)  & 0xff);
        result[3] = (byte)((num >>> 0)  & 0xff);
        return result;  
    }
}
