Installation
============

Das Aufsetzen von Hadoop gestaltete sich als sehr schwierig. Sowohl auf
Windows, als auch auf Linux und Mac gab es unterschiedliche Probleme.
Verschiedene Anleitungen & Hadoop-Versionen wurden ausprobiert. Auch
`Stackoverflow <https://stackoverflow.com/questions/14932794/problems-in-setting-hadoop-on-mac-os-x-10-8>`__
`und <https://medium.com/@jayden.chua/installing-hadoop-on-macos-a334ab45bb3>`__
`andere <https://gist.github.com/christine-le/2a5dd75c9e0a2f87bc1edda42c9b8206>`__
`Foren <https://isaacchanghau.github.io/post/install_hadoop_mac/>`__
konnten die Probleme nicht vollständig beseitigen.

Auf dem Mac entstanden viele Fehler durch Berechtigungsprobleme beim SSH
Zugriff auf localhost. Zu einem Zeitpunkt funktionierte der
Hadoop-Cluster teilweise. Es gibt wohl öfter Probleme beim 
Starten und Beenden von YARN.

Auf Linux konnte Hadoop zwar vermeintlich installiert werden, die
Beispiel-JAR's erreichen beim Testen dann jedoch nicht alle Nodes.

Aus diesen Gründen wurden sich gegen eine einfache Lokale installation
und für eine Containerisierungslösung entschieden. So können auch Sie
schneller und verlässlicher Testen.

Quickstart
^^^^^^^^^^


**Note:** In dieser Dokumentation markieren wir jeden
Komandozeilenausschnitt mit **Local** oder **Docker** um zu
verdeutlichen ob die Befehle für das Hostsystem oder innerhalb des
Docker Containers ausgeführt werden.

Unser Dockerfile basiert auf dem Hadoop Docker Image von
`sequenceiq <https://hub.docker.com/r/sequenceiq/hadoop-docker/>`__,
befindet sich in ``./Docker/Dockerfile`` und kann wie folgt gebaut und
ausgeführt werden:

**Local:**

::

    docker build -t sv .
    docker run -it sv /etc/bootstrap.sh -bash --name docker_hadoop


Um den Docker Container zu **testen** kann das mitgelieferte Beispiel wie folgt ausgeführt und dessen Ergebnisse ausgelesen werden.

**Docker:**

::

    cd $HADOOP_PREFIX
    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.0.jar grep input output 'dfs[a-z.]+'
    bin/hdfs dfs -cat output/*



Ausführung vorbereiten
----------------------

Quick & Dirty commands on Linux & Mac:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Diese Variante bietet den minimalen Aufwand, um die gesamte Umgebung vorzubereiten.

**Local:**

::

    docker ps
    cd <Project>
    ./createAndCopyJAR.sh <container_id>
    docker exec -it <container_id> /bin/bash


**Docker:**
::

    cd /hadoop_sv/
    ./createEnvironment.sh
    runhadoop


Resourcen in den Container kopieren
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Die Textdateien müssen in das HDFS kopiert werden. Dabei sind folgende Schritte nötig:

- Hostmaschine -> Docker-Container (dieser Schritt wird automatisch durch das Dockerfile ausgeführt)
- Docker -> HDFS (dieser Schritt muss wie unten beschrieben ausgeführt werden)

Unter ``./Docker/`` befindet sich die Input datei (``textfiles.zip``) und
eine kleinere Testdatei (``textfiles_mini.zip``). Diese enthalten
Beispiel-Text-Dateien, die analysiert werden sollen. Diese Dateien
werden automatisch durch das Dockerfile in den Docker Container kopiert.
Um andere Dateien zu testen, kann das Dockerfile bearbeitet werden.

Falls man sich noch nicht in der Shell des Containers befindet, holt man sich
die container-id mit ``docker ps``, und benutzt sie im folgenden Befehl:

**Local:**

::

    docker exec -it <docker container_id> /bin/bash

Danach kann das Archiv auf das verteilte Hadoop-Dateisystem (HDFS)
hochgeladen werden:

**Docker:**

::

    $HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv
    $HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv/

JAR-File Kompilieren und in Container kopieren
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Da wir ein Maven-Projekt benutzen, muss nach der Implementierung das
'.jar' file kompiliert, ggf. umbenannt und auf den Docker-Container
kopiert werden. Dafür wurde das Skript ``create_and_copyJAR.sh``
geschrieben:

1.a: Mithilfe eines Skriptes

**Local:**

::

    create_and_copyJAR.sh <containerId>

1.b: manuell: *Alternativ* kann die "jar"-Datei mithilfe von Maven erzeugt und in den
   Container kopiert werden:

**Local:**

::

    cd hadoop_sv
    mvn clean package
    mv target/Hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
    docker cp target/hadoop_sv.jar <containerId>:/hadoop_sv

2. In jedem Fall muss die .jar Datei danach vom Docker Container auf das
   HDFS System kopiert werden:

**Docker:**

::

    $HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/hadoop_sv.jar /hadoop_sv

Hadoop-Job ausführen
--------------------

Um den Sprachvergleich zu starten wird der folgende Befehl ausgeführt. Dabei müssen drei Argumente dem Befehl übergeben werden: 'Input-Pfad', 'Output-Pfad' & 'result-Pfad'.
Beim Starten des Docker-Containers sollte auch ein Alias angelegt worden sein. Damit lässt sich der folgende Befehl auch mit ```runhadoop``` ausführen.

**Docker:**

::

    $HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar de.berlin.htw.Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/ /hadoop_sv/results/


Ergebnisse sichten
------------------

Die Ergebnisse liegen nach Ausführung des Sprachvergleiches in ``hadoop_sv/output`` und ``hadoop_sv/results``.

- ``hadoop_sv/output`` enthält die Zwischenergebnisse - die längsten Wörter einer Sprache
- ``hadoop_sv/results`` enthält das Ergebnis - eine Datei mit den längsten Wörtern aller Sprachen

Die Ergebnisse können im Container mit folgendem Befehl angeguckt werden:

::
    
    $HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sv/results/part-r-00000


Wenn die Resultate auf dem Hostsystem angeguckt werden sollen können sie mittels zwei Schritten kopiert werden:

1. HDFS -> Docker

Um die Ergebnisse vom HDFS auf den Container zu kopieren kann auch der alias ``copyresults`` verwendet werden.

**Docker:**

::

   $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/output /hadoop_sv/
   $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/results /hadoop_sv/

2. Docker ->️ ️Hostmaschine

**Local:**

::

   docker cp <containerId>:/hadoop_sv/output ~/Desktop/
   docker cp <containerId>:/hadoop_sv/results ~/Desktop/