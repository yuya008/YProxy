package localservice;

import config.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;

public class Connecter {
    
    private final AsynchronousSocketChannel browser;
    private Socket remote;
    private final Config config;
    
    public Connecter(AsynchronousSocketChannel s, Config c) {
        browser = s;
        config = c;
    }

    public boolean connectRemoteService() {
        String remoteHostname = config.getConfig(Config.Remotehost, "localhost");
        int remotePort = Integer.parseInt(config.getConfig(Config.Remoteport, "5500"));
        remote = new Socket();
        try {
            remote.connect(new InetSocketAddress(remoteHostname, remotePort));
        } catch (IOException ex) {
            System.err.println(ex.getMessage() +"\t"+remoteHostname+":"+remotePort);
            return false;
        }
        return remote.isConnected();
    }
    
    
    public AsynchronousSocketChannel getBrowser() {
        return browser;
    }
    
    public Socket getRemoteSocket() {
        return remote;
    }
    
    public void close() {
        try {
            remote.close();
            browser.close();
        } catch (IOException ex) {
        }
    }
}
