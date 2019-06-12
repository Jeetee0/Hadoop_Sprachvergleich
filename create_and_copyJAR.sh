#!/bin/bash
if [ -z "$1" ]
then
  echo "Please give 'docker_container_id' as argument"
else
  mvn clean package
  mv target/hadoop_sprachvergleich-1.0-SNAPSHOT.jar target/hadoop_sprachvergleich.jar
  docker cp target/hadoop_sprachvergleich.jar $1:/hadoop_sprachvergleich/
  echo "Created jar and copied into docker container."
fi
