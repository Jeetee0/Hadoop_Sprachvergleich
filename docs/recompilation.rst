Neukompellierung
==========================

1. Alte Jar löschen:

A. Automatisch: Run ``delete_jars.sh`` B. Manuell **Docker:**

::

    rm /hadoop_sv/hadoop_sv.jar
    $HADOOP_PREFIX/bin/hdfs dfs -rm /hadoop_sv/hadoop_sv.jar

2. Neue Datei kompellieren und aufs System Kopieren

**Local:**

::

    mvn clean package
    mv target/Hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
    docker cp target/hadoop_sv.jar <containerId>:/hadoop_sv

3. Ausführen:

**Docker:**

::

    $HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/
