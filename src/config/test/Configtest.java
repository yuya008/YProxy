package config.test;

import config.Config;
import java.util.Properties;

public class Configtest {
    
    private static void test1() {
        System.setProperty("user.dir", "/home/yuya/NetBeansProjects/YProxy/bin");
        
        String[] args = {"localservice", "--localhostname", "127.0.0.1",
        "--localport", "11600", "--remoteport", "11000"};
        
//        String[] args = {"localservice"};
        
        Config c = new Config(args);
        c.parse();
        Properties p = Config.getConfig();
        
        for (Object key: p.keySet()) {
            System.out.println(key +":"+ p.get(key));
        }
    }
    
    public static void main(String[] args) {
        test1();
    }
}
