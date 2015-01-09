package yproxy;

public class Config {
    public static String local_hostname = "127.0.0.1";
    public static int local_port = 11400;
    
    public static int local_read_buffer = 1024;
    public static int local_write_buffer = 1024;
    
    public static String remote_hostname = "127.0.0.1";
    public static int remote_port = 5500;
    
    public static int remote_read_buffer = 1024;
    public static int remote_write_buffer = 1024;
    
    public static int threadsn = 1024;
    
}
