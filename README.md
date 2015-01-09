
##YProxy是什么?
最近在学习java，就写了这个项目练练手，

大致上就是一个代理服务器。

##YProxy有哪些特点？

* 采用socks5协议传输数据
* 支持http、https协议的代理


##YProxy怎么用?

* 首先你必须安装java的运行环境(1.7或以上)，因为这是用java开发的
* localService要部署在本机运行，remoteService要部署在你自己的国外服务器上
* 设置浏览器使用socks5代理，将请求全部转发到localService，然后再转发到remoteService

```
#java -jar YProxy.jar localservice --localhostname 127.0.0.1 --localport 11400 --remotehostname 你的vps的ip --remoteport 启动在vps的remoteService的端口号 #本地服务启动
#java -jar YProxy.jar remoteservice --remotehostname vps的ip --remoteport remoteService监听的端口号  #远程服务启动
#java -jar YProxy.jar --help                                             												#帮助
```
