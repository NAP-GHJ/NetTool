title: Pipework For Docker！

category: pipework;docker network;bridge

date: 2016-1-9

tags: pipework;docker network;bridge

author: Ghj
---
欢迎访问NAP Group博客。 

<!--more-->

---

## 1、 简介
[Pipework](https://github.com/jpetazzo/pipework)是自定义Docker容器网络的shell脚本，pipework是一个开源项目，由200多行
shell实现。Pipework是一个集成工具，需要配合使用的两个工具是OpenvSwitch和Bridge-utils.


## 2、Pipework的安装
### 安装pipework
```
$ git clone https://github.com/jpetazzo/pipework.git

# 直接将可执行文件放在环境目录下
$ sudo cp pipework/pipework /usr/local/bin/

```

## 3、Pipework配置docker的四个简单场景

### 3.1 、pipework+linux bridge:配置Docker单主机容器

```
#主机A
#主机A上创建两个容器con1、con2
docker run -itd --name con1 --net=none test bash
docker run -itd --name con2 --net=none test bash

#使用pipework建立网桥br0，为容器con1和con2添加新的网卡，并将它们连接到br0上
pipework br0 con1 10.0.0.2/24
pipework br0 con2 10.0.0.3/24

#原理：网桥br0将con1和con2连接在一个二层网络中
#ifconfig和brctl命令可以看到网桥br0的信息，br0此时有两个接口分别连在容器con1和con2上。在con1和con2内部可以看到新增了一个
网卡地址分别是10.0.0.2/24和10.0.0.3/24

#网络隔离:创建第三个容器con3，配置地址为10.0.1.2/24
docker run -itd --name con3 --net=none test bash
pipework br0 con3 10.0.1.2/24

#此时发现con3是不能与con1和con2通信的，原因是它们不在同一个网段。
```

### 3.2 、pipework+linux bridge:将docker容器配置到本地网络环境中
使用虚拟网桥可以将docker容器桥接到本地网络环境中。
缺点：

1）Docker容器占用主机网络的IP地址

2）大量docker容器可能引起广播风暴，导致主机所在网络性能的下降

3）docker容器连接在主机网络可能引起安全问题

思路：在所有主机上用虚拟网桥将本机上的docker容器连接起来，然后用一块网卡加入到虚拟网桥上，使所有主机上的虚拟网桥级联在一起。这样不同主机上的
docker容器也将如同连在了一个大的逻辑交换机上。
```
#主机A：eth1:192.168.100.1/24
#主机A上创建容器con4
docker run -itd --name con4 --net=none test bash

#使用pipework建立网桥br1，为容器con4添加新的网卡，并将它们连接到br0上
pipework br1 con4 192.168.100.11/24@192.168.100.254

#将主机eth1桥接到br1上，并把eth1的IP配置在br0上。
ip addr add 192.168.100.1/24 dev br1; \
    ip addr del 192.168.100.1/24 dev eth1; \
    brctl addif br1 eth1; \
    ip route del default; \
    ip route add default gw 192.168.100.1/24 dev br1

#此时Docker容器已经可以使用新的IP和主机网络里的机器相互通信了，当然不同主机上的容器可以通过这种方式通信。

```


### 3.3 、pipework+OVS：单主机Docker容器VLAN划分
pipework不仅可以使用Linux bridge连接Docker容器，还可以与OpenVswitch结合，实现Docker容器的VLAN划分。
下面，就来简单演示一下，在单机环境下，如何实现Docker容器间的二层隔离。
为了演示隔离效果，我们将4个容器放在了同一个IP网段中。但实际他们是二层隔离的两个网络，有不同的广播域。

```
#主机A：eth1:192.168.100.1/24

#在主机A上创建4个Docker容器，test1、test2、test3、test4
docker run -itd --name test1 --net=none test bash
docker run -itd --name test2 --net=none test bash
docker run -itd --name test3 --net=none test bash
docker run -itd --name test4 --net=none test bash

#将test1，test2划分到一个vlan中，vlan在mac地址后加@指定，此处mac地址省略。
pipework ovs0 test1 192.168.0.1/24 @100
pipework ovs0 test2 192.168.0.2/24 @100

#将test3，test4划分到另一个vlan中
pipework ovs0 test3 192.168.0.3/24 @200
pipework ovs0 test4 192.168.0.4/24 @200

#隔离：这是一种二层网络隔离的技术
```
#### 原理


### 3.4 、pipework+OVS：多主机Docker容器VLAN划分

```
#主机A：eth1:192.168.100.1/24
#主机B：eth1:192.168.100.2/24

#用来桥架的网卡需要开启混杂模式
ifconfig eth1 promisc

#主机A上
docker run -itd --net=none --name con1 test bash
docker run -itd --net=none --name con2 test bash
 
#划分vlan
pipework ovs con1 10.0.0.1/24 @100
pipework ovs con2 10.0.0.2/24 @200
 
#将eth1连接到ovs上
ovs-vsctl add-port ovs eth1
 
#同理在主机B上进行操作
docker run -itd --net=none --name con3 test bash
docker run -itd --net=none --name con4 test bash
 
#划分vlan
pipework ovs con3 10.0.0.3/24 @100
pipework ovs con4 10.0.0.4/24 @200
 
#将eth1连接到ovs上
ovs-vsctl add-port ovs eth1

#隔离：可以看出来使用Ovs的隔离是一种很有效的方式，一方面它实现了单主机上容器之间的隔离，另一方面也实现了跨主机容器之间的
通信和隔离，而且它的隔离是可以以单个容器为单位的。

```
#### 原理
