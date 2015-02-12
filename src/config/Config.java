package config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Config {
    
    private String[] args;
    private static final Properties config;
    private String whatService;
    
    static {
        config = new Properties(System.getProperties());
    }

    public Config(String[] args) {
        this.args = args;
    }
    
    private void usage() {
        System.err.println("YProxy Usage:");
        System.err.println(" localservice      启动本地服务（必选）");
        System.err.println(" remoteservice     启动远程服务（必选）");
        System.err.println(" --localhostname   启动服务的地址，默认127.0.0.1");
        System.err.println(" --localport       启动服务监听的端口，默认11400");
        System.err.println(" --remotehostname  启动服务的地址，默认127.0.0.1");
        System.err.println(" --remoteport      启动服务监听的端口，默认5500");
        System.err.println(" --readbuffersize  服务IO读缓冲字节大小，默认1024byte");
        System.err.println(" --writebuffersize 服务IO写缓冲字节大小，默认1024byte");
        System.err.println(" --threadsn        分配给线程池的线程数，默认1024");
        System.err.println(" --help            帮助信息");
        System.exit(1);
    }
    
    public void parse() {
        if (args == null)
            usage();
        if (args.length < 1)
            usage();
        if (!args[0].equals("localservice"))
            if (!args[0].equals("remoteservice"))
                usage();
        
        advanceParse();
        String[] s = new String[]{"localservice", "remoteservice"};
        for (String server : s)
            parseServiceXML(server);
        parseArgs();
    }
    
    private void advanceParse() {
        String userDirStr = System.getProperty("user.dir");
        
        if (userDirStr == null)
            panic("Property user.dir is null");
        
        config.setProperty("yproxy.user.dir", userDirStr);
        String baseDir = Paths.get(userDirStr+"/../").normalize().toAbsolutePath().toString();
        config.setProperty("yproxy.user.basedir", baseDir);
        
        if (args[0].equals("localservice"))
            whatService = "localservice";
        else
            whatService = "remoteservice";
    }
    
    public static String getConfig(String key) {
        return config.getProperty(key);
    }
    
    private void parseServiceXML(String service) {
        
        String baseDir = Config.getConfig("yproxy.user.basedir");
        Path confpath = Paths.get(baseDir, "conf");
        
        if (!Files.isDirectory(confpath) || !Files.isReadable(confpath))
            panic("conf/ dir not found or not readable");
        
        Path configFile;
        String newkey = "yproxy."+service+".";
        configFile = Paths.get(confpath.toString(), service+".xml");
        if (!Files.isRegularFile(configFile, LinkOption.NOFOLLOW_LINKS) ||
            !Files.isReadable(configFile)) {
            panic(service+".xml file not found or not readable or is a symlink");
        }
        XMLStreamReader xsr = null;
        try {
            try (InputStream u = Files.newInputStream(configFile, StandardOpenOption.READ)) {
                XMLInputFactory xif = XMLInputFactory.newFactory();
                xsr = xif.createXMLStreamReader(u);
                for (;xsr.hasNext();) {
                    if (xsr.next() == XMLStreamReader.START_ELEMENT) {
                        String ename = xsr.getName().toString();
                        if (xsr.next() == XMLStreamReader.CHARACTERS) {
                            if (xsr.isWhiteSpace()) continue;
                            String evalue = xsr.getText().trim();
                            config.setProperty(newkey+ename, evalue);
                        }
                    }
                }
            } catch (XMLStreamException ex) {
                if (xsr != null) xsr.close();
                panic(ex.getMessage());
            }
        } catch (MalformedURLException ex) {
            panic(ex.getMessage());
        } catch (IOException | XMLStreamException ex) {
            panic(ex.getMessage());
        }
    }
    
    private void panic(String msg) {
        System.err.println("YProxy panic : "+msg);
        System.exit(1);
    }
    
    private void parseArgs() {
        if (args.length == 1)
            return;
        try {
            for (int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "--localhostname":
                        config.setProperty("yproxy.localservice.hostname", args[++i]);
                        break;
                    case "--localport":
                        config.setProperty("yproxy.localservice.port", args[++i]);
                        break;
                    case "--remotehostname":
                        config.setProperty("yproxy.remoteservice.hostname", args[++i]);
                        break;
                    case "--remoteport":
                        config.setProperty("yproxy.remoteservice.port", args[++i]);
                        break;
                    case "--readbuffersize":
                        config.setProperty("yproxy."+whatService+".read-buffer-size", args[++i]);
                        break;
                    case "--writebuffersize":
                        config.setProperty("yproxy."+whatService+".write-buffer-size", args[++i]);
                        break;
                    case "--threadsn":
                        config.setProperty("yproxy."+whatService+".thread-pool-threads", args[++i]);
                        break;
                    default:
                        usage();
                        break;
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            usage();
        }
        
    }

}
