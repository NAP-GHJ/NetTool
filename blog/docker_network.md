title: Docker Network For Docker！

category: docker network 

date: 2016-1-10

tags: docker network

author: Ghj

---
欢迎访问NAP Group博客。 

<!--more-->

---

## 1、 简介
[Docker Networking](https://docs.docker.com/engine/userguide/networking/get-started-overlay/)

## 2、Docker Networking

Docker-Networking:1.9版本

[Docker1.9新特性解读](http://dockone.io/article/799)
新的网络设备可以支持用户创建基于多个主机的虚拟网络，使容器间可以跨网络通信。


## 3、Docker Networking配置docker的简单场景

### 3.1、docker-networking-machine建立集群
```
1、建立key-value store,docker machine创建虚拟机mh-keystore,在mh-keystore上启动consul服务，端口号8500
	docker-machine create -d virtualbox mh-keystore
	docker $(docker-machine config mh-keystore) run -d -p "8500:8500" -h "consul" \
		progrium/consul -server -bootstrap

2、将mh-keystore的环境变量写到本地，docker ps 查看起的consul服务
	eval "$(docker-machine env mh-keystore)"

3、建立集群：建立master节点mhs-demos
	docker-machine create \
	-d virtualbox \
	--swarm --swarm-image="swarm" --swarm-master \
	--swarm-discovery="consul://$(docker-machine ip mh-keystore):8500" \
	--engine-opt="cluster-store=consul://$(docker-machine ip mh-keystore):8500" \
	--engine-opt="cluster-advertise=eth0:2376" \
	mhs-demo0
	
	添加节点：
	docker-machine create -d virtualbox \
    --swarm --swarm-image="swarm:1.0.0-rc2" \
    --swarm-discovery="consul://$(docker-machine ip mh-keystore):8500" \
    --engine-opt="cluster-store=consul://$(docker-machine ip mh-keystore):8500" \
    --engine-opt="cluster-advertise=eth0:2376" \
	mhs-demo1
	
4、建立Overlay Network
	eval $(docker-machine env --swarm mhs-demo0)
	docker network create --driver overlay my-net
	
	查看网络信息 docker network ls 
			NAME           DRIVER
		mhs-demo1/host      host                
		mhs-demo0/bridge    bridge              
		my-net              overlay             
		mhs-demo0/none      null                
		mhs-demo0/host      host                
		mhs-demo1/bridge    bridge              
		mhs-demo1/none      null 

5、分别进入mhs-demo0和mhs-demo1运行容器测试


```
参考文献：
1、[http://dockone.io/article/799](http://dockone.io/article/799) 

2、[http://dockone.io/article/840](http://dockone.io/article/840)

3、[https://docs.docker.com/engine/userguide/networking/get-started-overlay/](https://docs.docker.com/engine/userguide/networking/get-started-overlay/)

### 3.2、docker-networking跨主机通信

```
1、启动key-value service,这里使用的方式是Consul
	docker run -d -p "8500:8500" -h "consul" progrium/consul -server -bootstrap
	其中端口号为8500,IP地址为(192.168.187.128),此时可以通过ip:8500对consul服务进行访问
	
2、将key-value service信息加入docker daemon：先关闭原来的docker service，重启docekr
	service docekr stop
	docker daemon -H tcp://0.0.0.0:2376 -H unix:///var/run/docker.sock \
	--cluster-store=consul://192.168.108.132:8500 --cluster-advertise=eth0:2376 &>/dev/null &
	
	通过修改以下两个参数实现
	--cluster-store= 参数指向docker daemon所使用key value service的地址
	--cluster-advertise= 参数决定了所使用网卡以及docker daemon端口信息

3、docker 网络的创建与操作
	docker network create -d overlay mynet  #[network name]

4、docker网络的连接方式
	可以在两台主机上分别创建容器，互相进行ping
	创建容器接入指定网络
	docker run -it --name=testcontainer --net=mynet busybox sh

	将容器从指定网络中退出
	docker network disconnect mynet testcontainer 

	重新连入指定网络
	docker network connect over testcontainer
	
```
### 参看文献：
[http://dockone.io/article/840](http://dockone.io/article/840)