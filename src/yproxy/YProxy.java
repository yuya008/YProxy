package yproxy;

import localservice.LocalService;
import remoteservice.RemoteService;

public class YProxy {
    
    private static void usage() {
        System.exit(1);
    }
    
    private static void parseArgs(String[] args) {
        
    }
    
    public static void main(String[] args) {
        
        if (args.length == 0)
            usage();
        
        for (String arg : args) {
            System.out.println(arg);
        }
        
        parseArgs(args);
        
        if (args[0].equals("remoteservice")) {
            new RemoteService().start();
        } else if (args[0].equals("localservice")) {
            new LocalService().start();
        } else {
            usage();
        }
    }
    
}
