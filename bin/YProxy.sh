#!/bin/sh

#
# YProxy的bash启动脚本
#

if [ -z "$YPROXYHOME" ] ; then
	echo "警告：没有在环境变量中设置YPROXYHOME路径"
	exit 1
fi

if [ ! -d "$YPROXYHOME" ] ; then
	echo "警告：YPROXYHOME路径错误"
	exit 1
fi

if ! hash java 2>/dev/null; then
	echo "警告：java环境设置有问题"
	exit 1
fi

JARFILE="$YPROXYHOME/dist/YProxy.jar"

if [ "$1" = "localservice" ]; then
	java -Dyproxy.user.basedir=$YPROXYHOME -jar $JARFILE localservice &
        echo "本地服务启动成功..."
	exit 0
fi

if [ "$1" = "remoteservice" ]; then
	java -Dyproxy.user.basedir=$YPROXYHOME -jar $JARFILE remoteservice &
        echo "远程服务启动成功..."
	exit 0
fi

java -jar $JARFILE --help

exit 1
