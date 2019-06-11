# Hadoop_Sprachvergleich

Aufgabe 11 für die uni:
- Zählen der Wörterlänge (pro Sprache)
- Sortieren der Wörter der Länge nach (pro Sprache)
- Zusammenfassen der Ergebnisse

Form: "Sprache – Längstes Wort – Länge“
(nur das längste Wort pro Sprache herausfinden?!)



# Aufsetzen der Hadoop-Umgebung

## Installation:

Folgende Links & Anleitungen wurden genutzt:
https://medium.com/@jayden.chua/installing-hadoop-on-macos-a334ab45bb3
https://isaacchanghau.github.io/post/install_hadoop_mac/

https://stackoverflow.com/questions/14932794/problems-in-setting-hadoop-on-mac-os-x-10-8
https://gist.github.com/christine-le/2a5dd75c9e0a2f87bc1edda42c9b8206

Aufsetzen gestaltete sich als sehr schwierig. Verschiedene Anleitungen & Hadoop-Versionen wurden ausprobiert. Die Installation wurde auf Mac & linux getestet.
Auf dem Mac entstanden viele Fehler durch Berechtigungsprobleme beim SSH Zugriff auf localhost.
Zu einem Zeitpunkt funktionierte der Hadoop-Cluster teilweise. Wir konnten jeweils einmal die Seiten: 'localhost:50070' oder 'localhost:8088' aufrufen. Leider ging es danach wieder nicht. Es gibt wohl öfter Probleme beim Starten und Beenden von YARN.

### Hadoop 2.7.0 in Docker als Lösung:
```docker pull sequenceiq/hadoop-docker:2.7.0```
```docker run -it sequenceiq/hadoop-docker:2.7.0 /etc/bootstrap.sh -bash```

Beispiel funktionierte sofort:
```cd $HADOOP_PREFIX```
```bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.0.jar grep input output 'dfs[a-z.]+'```
```bin/hdfs dfs -cat output/*```

## Entwicklerumgebung

Da bereits Vorkenntnisse vorhanden sind, gestaltete sich das Einrichten der Entwicklerumgebung einfacher. Es wurde ein Maven-Projekt erstellt. Dort kann nach Einbinden der Dependencies: 'hadoop-core' & 'hadoop-mapreduce-client-core' für Hadoop programmiert werden.




# Vorbereitung zur Ausführung:

Erst einmal muss die 'full.zip' heruntergeladen werden. Ich wandelte sie in 'full.tar.gz' um.
```unzip full.zip```
```tar cfvz full.tar.gz full```

Danach muss das Archiv aufs verteilte Hadoop-Dateisystem (HDFS) hochgeladen werden:
```mkdir /hadoop_sprachvergleich```											(docker)
```mkdir /hadoop_sprachvergleich/full```									(docker)
```docker cp ~/Desktop/full.tar.gz <docker container_id>:/hadoop_sprachvergleich/``` (local)
```tar -zxvf /hadoop_sprachvergleich/full.tar.gz```							(docker)
```$HADOOP_PREFIX/bin/hdfs dfs -put /hadoop_sprachvergleich/full /```		(docker)

Da wir ein Maven-Projekt benutzen, wird nach der Implementierung das '.jar' folgendermaßen erzeugt und auf das verteilte Hadoop-Dateisystem (HDFS) hochgeladen:
```cd <Project>```
```mvn clean package```
```mv target/hadoop_sprachvergleich-1.0-SNAPSHOT.jar target/hadoop_sprachvergleich.jar```
```docker cp target/hadoop_sprachvergleich.jar <docker container_id>:/```

Um den Job auszuführen werden folgende Befehle ausgeführt:
```docker exec -it 03d49939644f /bin/bash```
```$HADOOP_PREFIX/bin/hadoop jar /hadoop_sprachvergleich.jar Hadoop_Sprachvergleich /hadoop_sprachvergleich/full /hadoop_sprachvergleich/output/```

	


Derzeitige Probleme:
- txt-files have commas in path, commas are used for path separation in hadoop
- when unzipping "._Deutsch" etc. files get created


# Implementierung
aufgaben aufteilung - mehrere jobs:

job 1: loop through files, give each process a language folder
job 2: count length of words for each language
job 3: aggregate longest words

- Erstellen von Klassen: main, Mapper & Reducer
- 'FileInputFormat.addInputPaths' erstellen durch Methode, die den Ordner (und alle weiteren Unterordner) nach Dateien durchsucht.


