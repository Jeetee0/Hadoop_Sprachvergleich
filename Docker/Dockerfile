FROM sequenceiq/hadoop-docker:2.7.0

RUN /etc/bootstrap.sh

RUN mkdir /hadoop_sv /hadoop_sv/textfiles

ADD textfiles_mini.zip hadoop_sv/
ADD 

RUN unzip /hadoop_sv/textfiles_mini.zip -d /hadoop_sv/textfiles

# ADD textfiles.zip hadoop_sv/textfiles/

# RUN $HADOOP_PREFIX/bin/hdfs dfs -ls /

# RUN $HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv /hadoop_sv/textfiles/
# RUN $HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv