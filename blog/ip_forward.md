title: IP-Forward For Docker！

category: ip route; docker network

date: 2016-1-10

tags: ip route; docker network

author: Ghj
---
欢迎访问NAP Group博客。 

<!--more-->

---

## 1、 简介
[IP-Forward]通过在主机中添加静态路由来实现跨主机通信。如果有两台主机host1和host2，
两主机上的docker容器是两个独立的二层网络，将con1发往con2的数据流先转发到主机host2上，再由host2转发到其上的docker容器中，反之亦然。
由于使用容器的IP进行路由，就需要避免不同主机上的docker容器使用相同冲突的IP，所有应该为不同的主机分配不同的IP子网。

## 2、路由方式实现容器间通信
ps：路由方式是最简单的实现方式，需要的工具也最少，但是路由方式所能做到的网络隔离也有
一定的局限，只能以整个主机上的所有容器为单位进行隔离。

![](/ip_forward.png)

### 路由配置
```
#主机A上：192.168.108.131，主机B：192.168.108.132
#S1：添加网卡docker0，我们这里为了区别docker默认的网卡docker0，使用其他名字br0
brctl addbr br0

#S2：配置网卡，添加路由
ifconfig br0 172.17.1.1/24 up
ip route add 172.17.2.0/24 via 192.168.108.132  #主机B的IP地址

#S3：启动docker服务
service docker stop
docker daemon -b=br0 

#S4：启动一个容器
docker run -it ubuntu:14.04 bash

#S5:进入容器，其IP地址应该为172.17.1.2

```

```
#主机B上：192.168.108.132
#S1：添加网卡docker0，我们这里为了区别docker默认的网卡docker0，使用其他名字br0
brctl addbr br0

#S2：配置网卡，添加路由
ifconfig br0 172.17.2.1/24 up
ip route add 172.17.1.0/24 via 192.168.108.131  #主机A的IP地址

#S3：启动docker服务
service docker stop
docker daemon -b=br0 

#S4：启动一个容器
docker run -it ubuntu:14.04 bash

#S5:进入容器，其IP地址应该为172.17.2.2
ping 172.17.1.2  #成功通信

```

## 原理


