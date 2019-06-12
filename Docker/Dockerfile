FROM sequenceiq/hadoop-docker:2.7.0

RUN /etc/bootstrap.sh

RUN mkdir /hadoop_sprachvergleich /hadoop_sprachvergleich/all_languages

ADD all_languages_unedited_small.zip hadoop_sprachvergleich/

RUN unzip /hadoop_sprachvergleich/all_languages_unedited_small.zip -d /hadoop_sprachvergleich/all_languages

# ADD all_languages_unedited.zip hadoop_sprachvergleich/all_languages/

# RUN $HADOOP_PREFIX/bin/hdfs dfs -ls /

# RUN $HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sprachvergleich
# RUN $HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich