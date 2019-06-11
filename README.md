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

Das Aufsetzen gestaltete sich als sehr schwierig. Verschiedene Anleitungen & Hadoop-Versionen wurden ausprobiert. Die Installation wurde auf Mac & linux getestet.
Auf dem Mac entstanden viele Fehler durch Berechtigungsprobleme beim SSH Zugriff auf localhost.
Zu einem Zeitpunkt funktionierte der Hadoop-Cluster teilweise. Wir konnten jeweils einmal die Seiten: 'localhost:50070' oder 'localhost:8088' aufrufen. Leider ging es danach wieder nicht. Es gibt wohl öfter Probleme beim Starten und Beenden von YARN.

#### Hadoop 2.7.0 in Docker als Lösung:
[Docker-Image](https://hub.docker.com/r/sequenceiq/hadoop-docker/)

```docker pull sequenceiq/hadoop-docker:2.7.0```

```docker run -it sequenceiq/hadoop-docker:2.7.0 /etc/bootstrap.sh -bash```

Beispiel funktionierte sofort:
```cd $HADOOP_PREFIX```

```bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.0.jar grep input output 'dfs[a-z.]+'```

```bin/hdfs dfs -cat output/*```

### Entwicklerumgebung

Da bereits Vorkenntnisse vorhanden sind, gestaltete sich das Einrichten der Entwicklerumgebung einfacher. Es wurde ein Maven-Projekt erstellt. Dort kann nach Einbinden der Dependencies: 'hadoop-core' & 'hadoop-mapreduce-client-core' für Hadoop programmiert werden.




## Vorbereitung zur Ausführung:

Erst einmal muss die 'full.zip' heruntergeladen werden. Diese enthält Beispiel-Text-Dateien, die analysiert werden sollen.
```cp ~/Downloads```
```mv full.zip ~/Desktop/all_languages_unedited.zip```

Um sich mit der Docker-Instanz zu verbinden holt man sich die container-id mit:
```docker ps```.
Danach kann man sich zum Container verbinden: 
```docker exec -it <docker container_id> /bin/bash```


Danach muss das Archiv aufs verteilte Hadoop-Dateisystem (HDFS) hochgeladen werden:

```mkdir /hadoop_sprachvergleich /hadoop_sprachvergleich/all_languages```											(docker)

```docker cp ~/Desktop/all_languages_unedited.zip <docker container_id>:/hadoop_sprachvergleich/``` (local)

```unzip all_languages_unedited.zip```						(docker)

Hier gab es Probleme mit der Umwandlung der kyrillischen Buchstaben. Ich nahm die kyrillischen Buchstaben und wandelte sie in lateinische um. (Bild)

optional?!
(```$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich```)		(docker)


Da wir ein Maven-Projekt benutzen, wird nach der Implementierung das '.jar' folgendermaßen erzeugt und auf das verteilte Hadoop-Dateisystem (HDFS) hochgeladen:
```cd <Project>```
```mvn clean package```
```mv target/hadoop_sprachvergleich-1.0-SNAPSHOT.jar target/hadoop_sprachvergleich.jar```
```docker cp target/hadoop_sprachvergleich.jar <docker container_id>:/hadoop_sprachvergleich/```

Hierzu wurde auch das Skript ```create&copyJar.sh``` geschrieben, um den Prozess nach jedem neu implementierten Feature zu automatisieren.

Um den Hadoop-Job zu starten wird folgender Befehl ausgeführt:

```$HADOOP_PREFIX/bin/hadoop jar /hadoop_sprachvergleich/hadoop_sprachvergleich.jar Hadoop_Sprachvergleich /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich/output/```

	


Derzeitige Probleme:
- txt-files have commas in path, commas are used for path separation in hadoop -> used "FileInputFormat.setInputDirRecursive(job, true);"
- when unzipping "._Deutsch" etc. files get created -> implemented workaround
- two languages fail to unzip (picture)



useful commands:
- ```remove "._" files: ```find . -iname '._*' -exec rm -rf {} \;```
- ```remove .DS_Store: ```find . -name '.DS_Store' -type f -delete```
- interact with hdfs cluster: ```$HADOOP_PREFIX/bin/hdfs dfs -ls /```
- remove outout: ```$HADOOP_PREFIX/bin/hdfs dfs -rm -r /hadoop_sprachvergleich/output```
- check output: ```$HADOOP_PREFIX/bin/hdfs dfs -cat /hadoop_sprachvergleich/output/part-r-00000```

## Implementierung
Derzeitiger Ansatz:
- pro Sprache einen Job, der MapReduce-Prozess ausführt -> findet längstes Wort pro Sprache
-- Map: Länge des Wortes berechnen, Abspeichern als <Key>,<Value>: <Länge>,<Wort>
-- Reduce: das längste Wort als Maximum speichern & sortieren nach Länge (Key)
- Zusammenfassen der längsten Wörter

