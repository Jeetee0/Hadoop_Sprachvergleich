#!/bin/bash

# disable safe mode (happens sometimes)
echo "--- INFO ---: disabling safe mode in HDFS..."
$HADOOP_PREFIX/bin/hdfs dfsadmin -safemode leave

# remove maybe existing data before
echo "--- INFO ---: removing /hadoop_sv folder at HDFS..."
$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sv

# give hdfs resources

echo "--- INFO ---: copying textfiles to HDFS into '/hadoop_sv'..."
$HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv/		# main
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/test_textfiles /hadoop_sv/	# test

# give hdfs jar (not necessary)
#$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/hadoop_sv.jar /hadoop_sv

# show
echo "--- INFO ---: HDFS has following content:"
$HADOOP_PREFIX/bin/hdfs dfs -ls /hadoop_sv/
$HADOOP_PREFIX/bin/hdfs dfs -ls /hadoop_sv/textfiles

echo "--- INFO ---: Hadoop got jar-file & textfiles as input. start job with 'runhadoop'."