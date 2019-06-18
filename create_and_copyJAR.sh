#!/bin/bash
if [ -z "$1" ]
then
  echo "Please give 'docker_container_id' as argument"
else
  mvn clean package
  mv target/hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
  docker cp target/hadoop_sv.jar $1:/hadoop_sv/
  echo "Created jar and copied into docker container to /hadoop_sv."
fi
