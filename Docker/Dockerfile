FROM sequenceiq/hadoop-docker:2.7.0

# handy aliases and structure:
RUN mkdir /hadoop_sv /hadoop_sv/textfiles
RUN echo 'alias ll="ls -al"' >> ~/.bashrc
RUN echo 'alias runhadoop="$HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar de.berlin.htw.Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/ /hadoop_sv/results/"' >> ~/.bashrc
RUN echo 'alias copyresults="rm -r /hadoop_sv/results; rm -r /hadoop_sv/output; $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/output /hadoop_sv/; $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/results /hadoop_sv/"' >> ~/.bashrc

ADD createEnvironment.sh hadoop_sv/
RUN chmod 777 /hadoop_sv/createEnvironment.sh
ADD testHadoopSv.sh hadoop_sv/
RUN chmod 777 /hadoop_sv/testHadoopSv.sh

# add test_zip:
ADD test_textfiles.zip hadoop_sv/
RUN unzip /hadoop_sv/test_textfiles.zip -d /hadoop_sv/test_textfiles

# QA environment zip (abstract of all files):
#ADD textfiles_mini_2_languages.zip hadoop_sv/
#RUN unzip /hadoop_sv/textfiles_mini_2_languages.zip -d /hadoop_sv/textfiles

# production environment zip (all files):
ADD textfiles.zip hadoop_sv/
RUN unzip /hadoop_sv/textfiles.zip -d /hadoop_sv/textfiles