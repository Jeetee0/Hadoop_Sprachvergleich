# Hadoop_Sprachvergleich

## Anforderungen

### Aufgabe

Dies ist eine Belegaufgabe im Rahmen unseres Studiums an der HTW Berlin im Master Angewandte Informatik. Für mehr Informationen kann die [dokumentation](https://hadoop-sprachvergleich.readthedocs.io/de/latest/index.html) eingesehen werden


### Dokumentation

10+ Seiten und mindestens die folgenden Bestandteile enthalten:

- Kurze Beschreibung und Erklärung der Aufgabe
- Detaillierte Lösungsbeschreibung
- Code-Fragmente mit einer Textbeschreibung
- Screenshots für die Ergebnisse und/oder Zwischenergebnisse
- Ausführliche Tests der Anwendung
- Tabellen, Graphen und Diagramme für die Leistung und vergleichende Laufzeit
- Kurzes Fazit

---

## Aufsetzen der Hadoop-Umgebung

### Installation:

Da die Installation viele Probleme bereitete, nahmen wir die Lösung Hadoop in Docker zu nutzen und auf ein vorhandenes Image zurückzugreifen.

#### Hadoop mit Docker



`Note:` In dieser Dokumentation markieren wir jeden Komandozeilenausschnitt mit `Local` oder `Docker` um zu verdeutlichen, ob die Befehle für das Hostsystem oder innerhalb des Docker Containers ausgeführt werden.

Unser Dockerfile basiert auf dem Hadoop Docker Image von [sequenceiq](https://hub.docker.com/r/sequenceiq/hadoop-docker/), befindet sich in `./Docker/Dockerfile` und kann wie folgt gebaut und ausgeführt werden:

**Local:**

```
docker build -t sv .

docker run -it sv /etc/bootstrap.sh -bash --name docker_hadoop
```

---

## Vorbereitung zur Ausführung:

### Quick & Dirty commands on Linux:

**Local:**

```
docker ps
cd <Project>
./createAndCopyJAR.sh <container_id>
docker exec -it <container_id> /bin/bash
```

**Docker:**
```
cd /hadoop_sv/
./createEnvironment.sh
runhadoop
```


### Resourcen bereitstellen

In `./Docker/` befindet sich die Input datei (`textfiles.zip`) und eine kleinere Testdatei (`textfiles_mini.zip`) . Diese enthalten Beispiel-Text-Dateien, die analysiert werden sollen.
Diese Dateien werden automatisch durch das Dockerfile in den Docker Container kopiert. Um andere Dateien zu testen kann das Dockerfile bearbeitet werden.

Falls man sich noch nicht in der Konsole des Containers befindet, kann man die container-id mit `docker ps` herausfinden, und benutzt sie im folgenden Befehl:

**Local:**

```
docker exec -it <container_id> /bin/bash
```

Danach kann das Archiv auf das verteilte Hadoop-Dateisystem (HDFS) hochgeladen und entpackt werden.

**Docker:**

```
$HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv/
```
Alternativ kann zuerst die "jar"-Datei in den Container kopiert (nächster Schritt) und dann das Skript 'createEnvironment.sh' im Container ausgeführt werden, um die Umgebung einzurichten.

### JAR-File kompilieren und in Container kopieren

Da wir ein Maven-Projekt benutzen, muss nach der Implementierung das '.jar' file kompiliert, ggf. umbenannt und in den Docker-Container kopiert werden. Dafür wurde das Skript `createAndCopyJAR.sh` geschrieben:

1. A - mithilfe eines Skriptes:

**Local:**

```
create_and_copyJAR.sh <containerId>
```

1. B - manuell: _Alternativ_ kann die "jar"-Datei manuell erzeugt und in den Container kopiert werden:

**Local:**

```
cd Hadoop_Sprachvergleich
mvn clean package
mv target/Hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
docker cp target/hadoop_sv.jar <containerId>:/hadoop_sv
```

2. In jedem Fall muss die "jar"-Datei danach vom Docker-Container auf das HDFS kopiert werden:

**Docker:**

```
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/hadoop_sv.jar /hadoop_sv
```

### Hadoop-Job ausführen

Um den Hadoop-Job zu starten wird folgender Befehl ausgeführt (beim Starten des Docker-Containers sollte auch ein Alias angelegt worden sein. Damit lässt sich der lange Befehl auch mit ```runhadoop``` ausführen.):

**Docker:**

``` 
$HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar de.berlin.htw.Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/ /hadoop_sv/results/
```

### weitere nützliche Befehle:

- interact with hdfs cluster: `$HADOOP_PREFIX/bin/hdfs dfs -ls /hadoop_sv/results`
- remove outout: `$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sv/output`
- show output: `$HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sv/output/part-r-00000`
- get files (HDFS to Docker): `$HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/output /hadoop_sv/`
- get files (Docker to Local): `docker cp <containerId>:/hadoop_sv/output ~/Desktop/`

## Implementierung

Derzeitiger Ansatz:

- pro Sprache einen Job, der MapReduce-Prozess ausführt -> findet längstes Wort pro Sprache
  - Map: Länge des Wortes berechnen, Abspeichern als <Key>,<Value>: <Länge>,<Wort>
  - Reduce: das längste Wort als Maximum speichern & sortieren nach Länge (Key)
- Zusammenfassen der längsten Wörter

Derzeitige Probleme:

- txt-files have commas in path, commas are used for path separation in hadoop -> used "FileInputFormat.setInputDirRecursive(job, true);"
- when unzipping ".\_Deutsch" etc. files get created -> implemented workaround
- two languages fail to unzip (picture) -> renamed
- use regex for word splitting, currently getting a lot of combines words



todos:
- tests schreiben
- skript in docker, dass test automatisch ausführt (testzip kopieren, unzippen, hadoop starten, vergleichen)