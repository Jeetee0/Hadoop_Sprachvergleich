# Hadoop_Sprachvergleich

## Anforderungen 

### Aufgabe

Aufgabe 11

- Zählen der Wörterlänge (pro Sprache)
- Sortieren der Wörter der Länge nach (pro Sprache)
- Zusammenfassen der Ergebnisse

Form: "Sprache – Längstes Wort – Länge“
(nur das längste Wort pro Sprache herausfinden?!)

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

Das Aufsetzen von Hadoop gestaltete sich als sehr schwierig. Sowohl auf Windows, als auch auf Linux und Mac gab es unterschiedliche Probleme. Verschiedene Anleitungen & Hadoop-Versionen wurden ausprobiert. Auch [Stackoverflow](https://stackoverflow.com/questions/14932794/problems-in-setting-hadoop-on-mac-os-x-10-8) [und](https://medium.com/@jayden.chua/installing-hadoop-on-macos-a334ab45bb3) [andere](https://gist.github.com/christine-le/2a5dd75c9e0a2f87bc1edda42c9b8206) [Foren](https://isaacchanghau.github.io/post/install_hadoop_mac/) konnten die Probleme nicht vollständig beseitigen.

Auf dem Mac entstanden viele Fehler durch Berechtigungsprobleme beim SSH Zugriff auf localhost. Zu einem Zeitpunkt funktionierte der Hadoop-Cluster teilweise. Wir konnten jeweils einmal die Seiten: <localhost:50070> oder <localhost:8088> aufrufen. Leider ging es danach wieder nicht. Es gibt wohl öfter Probleme beim Starten und Beenden von YARN.

Auf Linux konnte Hadoop zwar vermeintlich installiert werden, die Beispiele erreichen beim Testen dann jedoch nicht alle Nodes. 

Aus diesen Gründen wurden sich gegen eine einfache Lokale installation und für eine Containerisierungslösung entschieden. So können auch Sie schneller und verlässlicher Testen.

#### Hadoop mit Docker
`Note:` In dieser Dokumentation markieren wir jeden Komandozeilenausschnitt mit `Local` oder `Docker` um zu verdeutlichen ob die Befehle für das Hostsystem oder innerhalb des Docker Containers ausgeführt werden. 

Unser Dockerfile basiert auf dem Hadoop Docker Image von [sequenceiq](https://hub.docker.com/r/sequenceiq/hadoop-docker/), befindet sich in `./Docker/Dockerfile` und kann wie folt gebaut und ausgeführt werden

```
docker build -t sv .

docker run -it sv /etc/bootstrap.sh -bash
```

Um den Docker Container zu testen kann das mitgelieferte Beispiel wie folgt ausgeführt und dessen Ergebnisse ausgelesen werden. 

```
cd $HADOOP_PREFIX

bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.0.jar grep input output 'dfs[a-z.]+'

bin/hdfs dfs -cat output/*
```
### Entwicklerumgebung

Mittels IDE oder Konsole kann ein Maven-Projekt erstellt werden, in welchem die  Dependencies `hadoop-core` und `hadoop-mapreduce-client-core`  eingebunden werden müssen.

---

## Vorbereitung zur Ausführung:

### Resourcen auf Container bringen

In `./Docker/` befindet sich die Input datei (`textfiles.zip`) und eine kleinere Testdatei (`textfiles_mini.zip`) . Diese enthalten Beispiel-Text-Dateien, die analysiert werden sollen.
Diese Dateien werden Automatisch durch das Dockerfile in den Docker Container kopiert. Um andere Dateien zu testen kann das Dockerfile bearbeitet werden. 

Um sich mit der Docker-Instanz zu verbinden holt man sich die container-id mit `docker ps`.
Danach kann man sich zum Container verbinden.

__Local:__
```
docker exec -it <docker container_id> /bin/bash
```

Danach muss das Archiv aufs verteilte Hadoop-Dateisystem (HDFS) hochgeladen und entpackt werden:

__Docker:__
```
$HADOOP_PREFIX/bin/hdfs dfs -mkdir /hadoop_sv /hadoop_sv/textfiles/
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/textfiles /hadoop_sv/textfiles
```

### JAR-File Kompilieren und in Container kopieren

Da wir ein Maven-Projekt benutzen, muss nach der Implementierung das '.jar' file kompiliert, ggf. umbenannt und auf den Docker-Container kopiert werden. Dafür wurde das Skript ```create_and_copyJAR.sh``` geschrieben:

1. A: Mit Hilfe von script

__Local:__
```
create_and_copyJAR.sh <containerId>
```

1. B: Manuel: *Alternativ* kann die Maven .jar Manuel erzeugt und in den Container kopiert werden:

__Local:__
```
cd <Project>
mvn clean package
mv target/Hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
docker cp target/hadoop_sv.jar <containerId>:/hadoop_sv
```

2. In jedem Fall muss die .jar Datei danach vom Docker Container auf das HDFS System kopiert werden:

__Docker:__
```
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sv/hadoop_sv.jar /hadoop_sv
```

### Hadoop-Job ausführen

Um den Hadoop-Job zu starten wird folgender Befehl ausgeführt:

__Docker:__
```
$HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/
```


### useful commands:
- interact with hdfs cluster: `$HADOOP_PREFIX/bin/hdfs dfs -ls /`
- remove outout: `$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sv/output`
- show output: `$HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sv/output/part-r-00000`
- get files (HDFS to Docker): `$HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/output /hadoop_sv/ `
- get files (Docker to Local): `docker cp <containerId>:/hadoop_sv/output ~/Desktop/`

## Implementierung
Derzeitiger Ansatz:
- pro Sprache einen Job, der MapReduce-Prozess ausführt -> findet längstes Wort pro Sprache
    - Map: Länge des Wortes berechnen, Abspeichern als <Key>,<Value>: <Länge>,<Wort>
    - Reduce: das längste Wort als Maximum speichern & sortieren nach Länge (Key)
- Zusammenfassen der längsten Wörter



Derzeitige Probleme:
- txt-files have commas in path, commas are used for path separation in hadoop -> used "FileInputFormat.setInputDirRecursive(job, true);"
- when unzipping "._Deutsch" etc. files get created -> implemented workaround
- two languages fail to unzip (picture) -> renamed
- use regex for word splitting, currently getting a lot of combines words


## TODO Bei neukompellierung:

1. Alte Jar löschen:

A. Automatisch: Run `delete_jars.sh`
B. Manuell
__Docker:__
```
rm /hadoop_sv/hadoop_sv.jar
$HADOOP_PREFIX/bin/hdfs dfs -rm /hadoop_sv/hadoop_sv.jar
```

2. Neue Datei kompellieren und aufs System Kopieren

__Local:__

```
mvn clean package
mv target/Hadoop_sv-1.0-SNAPSHOT.jar target/hadoop_sv.jar
docker cp target/hadoop_sv.jar <containerId>:/hadoop_sv
```

3. Ausführen:

__Docker:__
```
$HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar Hadoop_sv /hadoop_sv/textfiles /hadoop_sv/output/
```


## Statistiken


Runtime für *small.zip: 

| JOB No. | Mapper A| Mapper B|
| ----|----:| ---:|
| 1 |20s |19s |
| 2 |27s |26s |
| 3 |21s |21s |
| 4 |28s |27s |
| 5 |25s |25s |
| 6 |25s |24s |
| 7 |37s |37s |
| 8 |25s |25s |
| `Total` | `213s` | `209s` |