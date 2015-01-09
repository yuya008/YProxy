package yproxy;

import localservice.LocalService;
import remoteservice.RemoteService;

public class YProxy {
    
    private static void usage() {
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
    
    private static void parseArgs(String[] args) throws NumberFormatException {
        if (args.length == 1)
            return;
        
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--localhostname":
                    Config.local_hostname = args[++i];
                    break;
                case "--localport":
                    Config.local_port = Integer.parseInt(args[++i]);
                    break;
                case "--remotehostname":
                    Config.remote_hostname = args[++i];
                    break;
                case "--remoteport":
                    Config.remote_port = Integer.parseInt(args[++i]);
                    break;
                case "--readbuffersize":
                    if (args[0].equals("localservice")) {
                        Config.local_read_buffer = Integer.parseInt(args[++i]);
                    } else {
                        Config.remote_read_buffer = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--writebuffersize":
                    if (args[0].equals("localservice")) {
                        Config.local_write_buffer = Integer.parseInt(args[++i]);
                    } else {
                        Config.remote_write_buffer = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--threadsn":
                    Config.threadsn = Integer.parseInt(args[++i]);
                    break;
                default:
                    usage();
                    break;
            }
        }
    }
    
    public static void main(String[] args) {
        
        if (args.length == 0)
            usage();
        
        try {
            parseArgs(args);
        } catch (NumberFormatException e) {
            usage();
        }
        
        if (args[0].equals("remoteservice")) {
            new RemoteService().start();
        } else if (args[0].equals("localservice")) {
            new LocalService().start();
        } else {
            usage();
        }
    }
    
}
