# ChangeLog

## V1.1

- 项目目录更加标准化
- 更改配置文件使用XML文档，使用stax流式解析器
- 网络模块采用AIO异步IO，代替以前的阻塞IO方式
- 采用ThreadPoolExecutor重新实现线程池，代替以前自写的简单线程池策略
