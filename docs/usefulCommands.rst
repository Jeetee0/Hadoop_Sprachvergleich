Nützliche Befehle
=================

interact with hdfs cluster: 
   ``$HADOOP_PREFIX/bin/hdfs dfs -ls /``

remove outout:
   ``$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sv/output``

show output:
   ``$HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sv/output/part-r-00000``

get files (HDFS to Docker):
   ``$HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/output /hadoop_sv/``

get files (Docker to Local):
   ``docker cp <containerId>:/hadoop_sv/output ~/Desktop/``