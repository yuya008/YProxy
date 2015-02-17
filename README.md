
##YProxy是什么?
最近在学习java，就写了这个项目练练手，

大致上就是一个代理服务器。

##YProxy有哪些特点？

* 采用socks5协议传输数据
* 支持http、https协议的代理


##YProxy怎么用?

* 首先你必须安装java的运行环境(1.8)，因为这是用java开发的
* 你必须要有自己的国外服务器，然后localservice是部署在本机的，remotehostname放在你的国外服务器上
* 两个服务要进行通信，localservice要监听自己的端口地址，还要设置remoteservice的地址端口
* 比如像下面这样运行服务，可以设定浏览器走socks5协议走127.0.0.1和11400端口，代理翻墙即可

```
#java -jar YProxy.jar localservice --localhostname 127.0.0.1 --localport 11400 --remotehostname 192.228.105.18 --remoteport 5500 #本地服务启动
#java -jar YProxy.jar remoteservice --remotehostname 192.228.105.18 --remoteport 5500                                            #远程服务启动
#java -jar YProxy.jar --help                                             												         #帮助信息
```
