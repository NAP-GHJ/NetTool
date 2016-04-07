title: Cacilo For Docker！

category: docker cacilo 

date: 2016-1-10

tags: cacilo; docker network

author: Ghj

---
欢迎访问NAP Group博客。 

<!--more-->

---

## 1、 简介
[Cacilo](https://github.com/projectcalico/calico)


## 2、Cacilo的安装
### 安装cacilo
```
#将可执行文件加到系统路径下即可

wget http://www.projectcalico.org/builds/calicoctl
chmod a+x calicoctl
cp calicoctl /usr/local/bin

```

## 3、Cacilo配置docker的简单场景

```
#主机A和主机B

#在主机A和B上启动etcd服务，构建一个简单的集群
#启动calico服务,这里使用自发现模式
#etcd官网提供了集群的公共访问的etcd存储地址，通过如下命令得到etcd服务的目录，作为-discovery参数使用
curl http://discovery.etcd.io/new?size=2  #集群结点数为2

#得到的返回值如下，后面每个节点加入集群的时候需要用作参数：
https://discovery.etcd.io/9a0601245e2ac2c06b34014bcb811721

#在每个节点上运行
etcd -name [Node_Name] \
-initial-advertise-peer-urls http://[Node_IP]:2380 \
-listen-peer-urls http://[Node_IP]:2380 \
-discovery [上面返回的结果]

#查看etcd集群信息:在其中任何一个主机上可以查看到集群的信息
etcdctl member list 
746147aa2b01123e: name=node2 peerURLs=http://192.168.108.132:2380 clientURLs=http://localhost:2379,http://localhost:4001
acf4d40ae6f803c1: name=node1 peerURLs=http://192.168.108.131:2380 clientURLs=http://localhost:2379,http://localhost:4001

#查看etcd存储的共享信息
etcdctl ls

#在确保成功启动etcd服务之后，在每个节点上启动calicoctl服务

calicoctl node

#让我们看看启动cacilo服务之后etcd有什么变化，原来的etcdctl ls看到的内容是空的现在已经有了
信息，此时etcd只是保存了集群中两个主机启动calico服务绑定的IP地址

#用下面的命令也可以看到其他主机的信息
calicoctl status

#在启动calico服务之后还需要配置地址池
calicoctl pool add 10.0.0.0/16 --ipip --nat-outgoing

## 网络隔离

#主机A
#启动容器--net=none:conA conB conC

#主机B
#启动容器--net=none:conD conE

#添加Calico网络
sudo calicoctl container add [container_name] [IP]

#添加配置文件，在任意一个主机上
calicoctl profile add PROF_A_C_E
calicoctl profile add PROF_B
calicoctl profile add PROF_D

#将容器添加到某个配置文件中
calicoctl container conA profile append PROF_A_C_E
calicoctl container conC profile append PROF_A_C_E
calicoctl container conE profile append PROF_A_C_E
calicoctl container conB profile append PROF_B
calicoctl container conD profile append PROF_D

#此时容器A,C,E之间可以相互通信，容器B和D之间可以相互通信，可以看到calico网络可以做到
的隔离是以容器为单位的

```
## 原理：

