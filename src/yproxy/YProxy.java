package yproxy;

import config.Config;

public class YProxy {

    public static void main(String[] args) {
        Config config = new Config(args);
        config.parse();
    }
    
}
