
##YProxy是什么?
最近在学习java，就写了这个项目练练手，

大致上就是一个代理服务器。

##YProxy有哪些特点？

* 采用socks5协议传输数据
* 支持http、https协议的代理

##YProxy怎么用?

* 首先你必须安装java的运行环境(1.8)，因为这是用java开发的
* 你必须要有自己的国外服务器，然后localservice是部署在本机的，remoteservice放在你的国外服务器上
* 两个服务要进行通信，localservice要监听自己的端口地址，还要设置remoteservice的地址端口
* 在环境变量中要设置YPROXYHOME变量，路径指向YProxy的主目录，比如/usr/local/YProxy
* 配置conf中xml的配置文件
* 然后在bin目录中使用启动脚本启动服务

```
#bin/YProxy localservice 	#本地服务启动
#bin/YProxy remoteservice 	#远程服务启动
#bin/YProxy --help			#帮助信息
```
