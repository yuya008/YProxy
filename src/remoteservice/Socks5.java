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
        try {
            ByteBuffer buf = ByteBuffer.allocate(3);
            Integer n = local.read(buf).get();
            if (n != 3)
                return false;
            Pack.unpack(buf, n);
            buf.flip();
            if (buf.get() != 0x05 || buf.get() != 0x01 || buf.get() != 0x00)
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
            ByteBuffer buf = ByteBuffer.allocate(3);

            Integer n = local.read(buf).get();

            if (n != 3)
                return false;
            
            Pack.unpack(buf, n);
            buf.flip();
            if (buf.get() != 0x05 || buf.get() != 0x01 || buf.get() != 0x00)
                return false;
            
            ByteBuffer typebuf = ByteBuffer.allocate(1);
            Integer typesize = local.read(typebuf).get();
            
            if (typesize <= 0)
                return false;
            typebuf.flip();
            int type = Pack.unpack(typebuf.get());
            
            if (type == 0x03) {
                ByteBuffer hostlenbuffer = ByteBuffer.allocate(1);
                Integer hostlensize = local.read(hostlenbuffer).get();
                
                if (hostlensize <= 0) return false;
                
                Pack.unpack(hostlenbuffer, hostlensize);
                hostlenbuffer.flip();
                int hostlen = (((int)hostlenbuffer.get()) & 0xff);
                ByteBuffer hostbuffer = ByteBuffer.allocate(hostlen);
                Integer hostsize = local.read(hostbuffer).get();
                
                if (hostsize <= 0) return false;
                
                Pack.unpack(hostbuffer, hostsize);
                this.host = new String(hostbuffer.array(), 0, hostsize);
                ByteBuffer portbuffer = ByteBuffer.allocate(2);
                Integer portlensize = local.read(portbuffer).get();
                
                if (portlensize <= 0) return false;
                
                Pack.unpack(portbuffer, portlensize);
                portbuffer.flip();
                this.port = bytes2int(new byte[]{0, 0, portbuffer.get(), portbuffer.get()});
            } else if (type == 0x01) {
                ByteBuffer hostbuffer = ByteBuffer.allocate(4);
                Integer hostsize = local.read(hostbuffer).get();
                if (hostsize <= 0)
                    return false;
                Pack.unpack(hostbuffer, hostsize);
                hostbuffer.flip();
                for (int i = 0; i < 3; i++) {
                    this.host += (((int)hostbuffer.get()) & 0xff) + ".";
                }
                this.host += (((int)hostbuffer.get()) & 0xff);
                
                ByteBuffer portbuffer = ByteBuffer.allocate(2);
                Integer portlensize = local.read(portbuffer).get();
                Pack.unpack(portbuffer, portlensize);
                portbuffer.flip();
                this.port = bytes2int(new byte[]{0, 0, portbuffer.get(), portbuffer.get()});
            } else {
                return false;
            }
            
        } catch (InterruptedException | ExecutionException ex) {
            return false;
        }
        // step 4
        byte[] b1 = new byte[]{5, 0, 0, 1};
        Pack.pack(b1);
        local.write(ByteBuffer.wrap(b1));
        byte[] b2 = new byte[]{0,0,0,0,0,0};
        Pack.pack(b2);
        local.write(ByteBuffer.wrap(b2));
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
