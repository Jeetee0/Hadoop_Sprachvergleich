# Hadoop multi node cluster setup:

first build docker image with ```Dockerfile_multi-node```.

Then setup the cluster wth docker-swarm using the following commands:


delete:

```docker swarm leave --force```

setup swarm:

```docker swarm init --advertise-addr 127.0.0.1```
```docker network create --driver overlay swarm-net```

start master:

```docker service create	--name hadoop-master	--network swarm-net	--hostname hadoop-master	--constraint node.role==manager	--replicas 1 --endpoint-mode dnsrr sv_multi```

start slave:

```docker service create	--name hadoop-slave1	--network swarm-net	--hostname hadoop-slave1	--replicas 1	--endpoint-mode dnsrr sv_multi```

check nodes:

```docker node ps```

go into master:

```docker exec -it hadoop-master.1.$(docker service ps hadoop-master --no-trunc | tail -n 1 | awk '{print $1}' ) bash```
	
restart HDFS:
	
```cd $HADOOP_PREFIX; sbin/stop-yarn.sh; sbin/stop-dfs.sh; bin/hadoop namenode -format; sbin/start-dfs.sh; sbin/start-yarn.sh; cd /hadoop_sv/```