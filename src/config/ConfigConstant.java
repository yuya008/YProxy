package config;

public class ConfigConstant {
    public static final String Localhost = "yproxy.localservice.hostname";
    public static final String Localport = "yproxy.localservice.port";
    public static final String Remotehost = "yproxy.remoteservice.hostname";
    public static final String Remoteport = "yproxy.remoteservice.port";
    public static final String Localthreads = "yproxy.localservice.thread-pool-threads";
    public static final String Remotethreads = "yproxy.remoteservice.thread-pool-threads";
    public static final String LocalRBsize = "yproxy.localservice.read-buffer-size";
    public static final String RemoteRBsize = "yproxy.remoteservice.read-buffer-size";
    public static final String LocalWBsize = "yproxy.localservice.write-buffer-size";
    public static final String RemoteWBsize = "yproxy.remoteservice.write-buffer-size";
    
    public static final String UserDir = "yproxy.user.dir";
    public static final String BaseDir = "yproxy.user.basedir";
    public static final String ConfDir = "yproxy.user.confDir";
    
    public static final String Localservice = "localservice";
    public static final String Remoteservice = "remoteservice";
    
    public static final String[] Servicenamearr = {Localservice, Remoteservice};
}
