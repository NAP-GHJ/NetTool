title: Weave For Docker！

category: weave; docker network

date: 2016-1-10

tags: weave; docker network

author: Ghj
---
欢迎访问NAP Group博客。 

<!--more-->

---

## 1、 简介
[Weave](https://github.com/weaveworks/weave)

## 2、Weave的安装
### 安装weave
```
#Weave安装，可执行文件放到系统环境目录即可
wget -O /usr/local/bin/weave https://raw.githubusercontent.com/zettio/weave/master/weave
chmod a+x /usr/local/bin/weave

```

## 3、Weave配置docker的简单场景

```
#主机一IP：192.168.108.131
#主机二IP：192.168.108.132
#主机三IP：192.168.108.133


#主机一上启动weave
weave launch

#weave launch这条命令之后发生了什么：
#ifconfig查看网卡多了四块网卡:datapath,datapath-link,link-weave,weave
#brctl show查看多了网桥bridge:weave
#docker ps -a查看运行了两个容器:weave和weaveproxy
#weave status可以查看主机上weave容器的详细信息

#weave 启动容器
weave run 10.0.1.2/24 -it test bash

#主机二上启动weave
weave launch 192.168.108.131

weave run 10.0.1.3/24 -it test bash   #可以通信
weave run 10.0.2.2/24 -it test bash   #不可以通信

#主机三上启动weave
weave launch 192.168.108.132

#此时在任意一个主机上查看weave的状态，显示有3个peer端和6条连接，weave launch是一个链式连接
weave status 

#这里体现了weave的网络隔离的好处，weave可以使得相同地址段的容器间可以相互通信，不同地址段的容器间不能相互通信

#默认情况下
weave run -it test bash

#默认情况下，weave容器会为新创建的容器自动分配一个IP地址，地址段为,默认情况下创建的容器之间可以相互通信
Range:10.32.0.0-10.47.255.255
DefaultSubnet: 10.32.0.0/12
每个容器的地址段也是 xx.xx.xx.xx/12，所以所有默认的容器之间都是可以之间通信的

#网络隔离：weave的隔离是可以做到同一个主机上容器之间的隔离，也可以做到跨主机容器的通信和隔离。

```

#### 原理
1）先创建一个由多个peer组成的对等网络，每个peer是一个虚拟路由器容器，叫做“weave路由器”，它们分布在不同的宿主机上。
Weave路由器：实质上是一个容器。对等网络的peer之间会维持一个TCP连接，用于交换拓扑信息，它们也会建立UDP连接用于容器间通信。

2）Weave路由器通过桥接连接到宿主机的其他容器上。当处于不同宿主机上的容器之间想要通信，
一台宿主机上的weave路由器通过截获数据包，使用UDP协议封装后发给另一台宿主机的weave路由器。

3）每个Weave路由器会刷新整个对等网络的拓扑信息，因此它可以决定数据包的下一跳是哪个容器。
Weave能让两个处于不同的宿主机的容器进行通信，只要这两台宿主机在weave拓扑结构内连到同一个weave路由器。

4）weave run：取代了docker run的原因是weave命令内部会调用docker命令来新建容器然后为它设置网络。
容器内部和容器外的虚拟接口对形式和docker0网桥的虚拟接口对形式一样。


