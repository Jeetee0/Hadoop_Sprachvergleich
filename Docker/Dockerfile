FROM sequenceiq/hadoop-docker:2.7.0

RUN mkdir /hadoop_sv /hadoop_sv/textfiles
RUN echo 'alias runhadoop="$HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar de.berlin.htw.Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/"' >> ~/.bashrc

# testing commands
#ADD textfiles_mini_3languages.zip hadoop_sv/
#RUN unzip /hadoop_sv/textfiles_mini_3languages.zip -d /hadoop_sv/textfiles

# production environment commands
ADD textfiles.zip hadoop_sv/
RUN unzip /hadoop_sv/textfiles.zip -d /hadoop_sv/


# tried to already communicate with hdfs, but didnt work
# RUN $HADOOP_PREFIX/bin/hdfs dfs -ls /

# RUN $HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv /hadoop_sv/textfiles/
# RUN $HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv




