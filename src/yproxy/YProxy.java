package yproxy;

import config.Config;
import localservice.LocalService;
import remoteservice.RemoteService;

public class YProxy {
    
    private static Service service;
    
    public static Service createService(Config c) {
        if (service != null) return service;
        
        String s = c.whatService();
        if (s.equals("localservice"))
            return new LocalService(c);
        else
            return new RemoteService(c);
    }
    
    public static void main(String[] args) {
        
        System.setProperty("user.dir", "/home/yuya/NetBeansProjects/YProxy/bin");
        Config config = new Config(args);
        Service service = YProxy.createService(config);
        service.start();
    }
    
}
