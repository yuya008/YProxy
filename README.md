
##YProxy是什么?
最近在学习java，就写了这个项目练练手，

大致上就是一个代理服务器。

##YProxy有哪些特点？

* 采用socks5协议传输数据
* 支持http、https协议的代理


##YProxy怎么用?

* 首先你必须安装java的运行环境(1.7或以上)，因为这是用java开发的

```
#java -jar YProxy.jar localservice --hostname X.X.X.X --port 11400       #本地服务启动
#java -jar YProxy.jar remoteservice --hostname X.X.X.X --port 11400      #远程服务启动
#java -jar YProxy.jar --help                                             #帮助
```
