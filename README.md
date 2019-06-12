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



## Aufsetzen der Hadoop-Umgebung

### Installation:

Folgende Links & Anleitungen wurden genutzt:
- https://medium.com/@jayden.chua/installing-hadoop-on-macos-a334ab45bb3
- https://isaacchanghau.github.io/post/install_hadoop_mac/

- https://stackoverflow.com/questions/14932794/problems-in-setting-hadoop-on-mac-os-x-10-8
- https://gist.github.com/christine-le/2a5dd75c9e0a2f87bc1edda42c9b8206

Das Aufsetzen gestaltete sich als sehr schwierig. Verschiedene Anleitungen & Hadoop-Versionen wurden ausprobiert. Die Installation wurde auf Mac & Linux getestet.
Auf dem Mac entstanden viele Fehler durch Berechtigungsprobleme beim SSH Zugriff auf localhost.
Zu einem Zeitpunkt funktionierte der Hadoop-Cluster teilweise. Wir konnten jeweils einmal die Seiten: <localhost:50070> oder <localhost:8088> aufrufen. Leider ging es danach wieder nicht. Es gibt wohl öfter Probleme beim Starten und Beenden von YARN.

#### Hadoop 2.7.0 in Docker als Lösung:
Docker container mit folgendem Image wurde genutzt: https://hub.docker.com/r/sequenceiq/hadoop-docker/

```docker pull sequenceiq/hadoop-docker:2.7.0```

```docker run -it sequenceiq/hadoop-docker:2.7.0 /etc/bootstrap.sh -bash```

Das Testbeispiel funktionierte einwandfrei:

```cd $HADOOP_PREFIX```

```bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.0.jar grep input output 'dfs[a-z.]+'```

```bin/hdfs dfs -cat output/*```

### Entwicklerumgebung

Da bereits Vorkenntnisse vorhanden sind, gestaltete sich das Einrichten der Entwicklerumgebung einfacher. Es wurde ein Maven-Projekt erstellt. Dort kann nach Einbinden der Dependencies: 'hadoop-core' & 'hadoop-mapreduce-client-core' für Hadoop programmiert werden.


## Vorbereitung zur Ausführung:

Erst einmal muss die 'full.zip' heruntergeladen werden. Diese enthält Beispiel-Text-Dateien, die analysiert werden sollen.
Begriffe in Klammern geben die Umgebung an, auf der der Befehl ausgeführt werden muss.
```cp ~/Downloads```	(__Local__)
```mv full.zip ~/Desktop/all_languages_unedited.zip```	(__Local__)

Um sich mit der Docker-Instanz zu verbinden holt man sich die container-id mit:
```docker ps```.
Danach kann man sich zum Container verbinden: 
```docker exec -it <docker container_id> /bin/bash```	(__Local__)


Danach muss das Archiv aufs verteilte Hadoop-Dateisystem (HDFS) hochgeladen und entpackt  werden:

```mkdir /hadoop_sprachvergleich /hadoop_sprachvergleich/all_languages```				(__Docker__)

```docker cp ~/Desktop/all_languages_unedited.zip <containerId>:/hadoop_sprachvergleich/``` (__Local__)

```unzip all_languages_unedited.zip```	(__Docker__)

Hier gab es Probleme mit der Umwandlung der kyrillischen Buchstaben. Ich nahm die kyrillischen Buchstaben und wandelte sie in lateinische um. (Bild)

```$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich```		(__Docker__)


Da wir ein Maven-Projekt benutzen, wird nach der Implementierung das '.jar' folgendermaßen erzeugt und auf das verteilte Hadoop-Dateisystem (HDFS) hochgeladen:
- ```cd <Project>```	(__Local__)
- ```mvn clean package```	(__Local__)
- ```mv target/hadoop_sprachvergleich-1.0-SNAPSHOT.jar target/hadoop_sprachvergleich.jar```	(__Local__)
- ```docker cp target/hadoop_sprachvergleich.jar <containerId>:/hadoop_sprachvergleich/```	(__Local__)

Um diesen Prozess zu automatisieren, wurde das Skript ```create&copyJar.sh``` geschrieben.

Um den Hadoop-Job zu starten wird folgender Befehl ausgeführt:

```$HADOOP_PREFIX/bin/hadoop jar /hadoop_sprachvergleich/hadoop_sprachvergleich.jar Hadoop_Sprachvergleich /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich/output/```	(__Docker__)


### useful commands:
- interact with hdfs cluster: ```$HADOOP_PREFIX/bin/hdfs dfs -ls /```
- remove outout: ```$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sprachvergleich/output```
- show output: ```$HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sprachvergleich/output/part-r-00000```
- get files (HDFS to Docker): ```$HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sprachvergleich/output /hadoop_sprachvergleich/ ```
- get files (Docker to Local): ```docker cp <containerId>:/hadoop_sprachvergleich/output ~/Desktop/```

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

