package yproxy;

import config.Config;
import localservice.LocalService;
import remoteservice.RemoteService;

public class YProxy {

    public static Service createService(Config c) {
        String s = c.whatService();
        if (s.equals("localservice"))
            return new LocalService(c);
        else
            return new RemoteService(c);
    }
    
    public static void main(String[] args) {
        Config config = new Config(args);
        Service service = YProxy.createService(config);
        service.start();
    }
    
}
