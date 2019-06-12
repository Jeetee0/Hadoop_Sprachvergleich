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
Wir empfehlen das Hadoop Docker Image von [sequenceiq](https://hub.docker.com/r/sequenceiq/hadoop-docker/). Wir markieren jeden Komandozeilenausschnitt mit `Local` oder `Docker` um zu verdeutlichen ob die Befehle für das Hostsystem oder innerhalb des Docker Containers `[Todo]` die Befehle für Es kann mit folgendem Komando heruntergeladen und in der shell gestartet werden:

```
docker pull sequenceiq/hadoop-docker:2.7.0

docker run -it sequenceiq/hadoop-docker:2.7.0 /etc/bootstrap.sh -bash
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

Erst einmal muss die 'full.zip' heruntergeladen werden. Diese enthält Beispiel-Text-Dateien, die analysiert werden sollen.
Begriffe in Klammern geben die Umgebung an, auf der der Befehl ausgeführt werden muss.

__Local__:
```
cp ~/Downloads
mv full.zip ~/Desktop/all_languages_unedited.zip
```

Um sich mit der Docker-Instanz zu verbinden holt man sich die container-id mit:
```docker ps```.
Danach kann man sich zum Container verbinden: 

__Local__:
```
docker exec -it <docker container_id> /bin/bash
```


Danach muss das Archiv aufs verteilte Hadoop-Dateisystem (HDFS) hochgeladen und entpackt  werden:

__Docker__:
```
mkdir /hadoop_sprachvergleich /hadoop_sprachvergleich/all_languages
```

```bash
docker cp ~/Desktop/all_languages_unedited.zip <containerId>:/hadoop_sprachvergleich/
```
```
unzip /hadoop_sprachvergleich/all_languages_unedited.zip -d /hadoop_sprachvergleich/all_languages
```	

Hier gab es Probleme mit der Umwandlung der kyrillischen Buchstaben. Ich nahm die kyrillischen Buchstaben und wandelte sie in lateinische um. (Bild)
```bash
mv \#U0420#U0443#U0441#U0441#U043a#U0438#U0439/ Russkyj
mv \#U0423#U043a#U0440#U0430#U0457#U043d#U0441#U044c#U043a#U0430/ Ukrajinska
```


__Docker__:
```
$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich
```


Da wir ein Maven-Projekt benutzen, wird nach der Implementierung das '.jar' folgendermaßen erzeugt und auf das verteilte Hadoop-Dateisystem (HDFS) hochgeladen:

__Local__:
```
cd <Project>
mvn clean package
mv target/hadoop_sprachvergleich-1.0-SNAPSHOT.jar target/hadoop_sprachvergleich.jar
docker cp target/hadoop_sprachvergleich.jar <containerId>:/hadoop_sprachvergleich
```

Um diesen Prozess zu automatisieren, wurde das Skript ```create&copyJar.sh``` geschrieben.

Um den Hadoop-Job zu starten wird folgender Befehl ausgeführt:

__Docker__:```$HADOOP_PREFIX/bin/hadoop jar /hadoop_sprachvergleich/hadoop_sprachvergleich.jar Hadoop_Sprachvergleich /hadoop_sprachvergleich/all_languages /hadoop_sprachvergleich/output/```


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

