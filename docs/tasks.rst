Aufgabenstellung
================


Dies ist eine Belegaufgabe im Rahmen unseres Studiums an der HTW Berlin im Master Angewandte Informatik. Im Kurs Programmierkonzepte & Algorithmen arbeiten wir an Aufgabe 11. Es liegt ein Datensatz von 502 Textdateien vor (Beletristik) - unterteilt in 8 Sprachen. Ziel ist es, mit Apache Hadoop diese Texte zu analysieren und dabei drei Aufgaben zu erfüllen:

1. Zählen der Wörterlänge (pro Sprache)
2. Sortieren der Wörter der Länge nach (pro Sprache)
3. Zusammenfassen der Ergebnisse

Dabei soll eine Ausgabe in der Form: "Sprache – Längstes Wort – Länge“ generiert werden.

Lösungsansatz
^^^^^^^^^^^^^

Hadoop stellt vorrangig vier Dinge bereit:

    1. Hadoop distributed file system (HDFS) - ein Verteiltes Dateisystem
    2. Hadoop MapReduce - ein System für parallele Datenverarbeitung
    3. Hadoop YARN - ein Framework um zur Job-Ablaufplanung (scheduling) und Cluster Ressourcen Management
    4. Hadoop Common - alle Werkzeuge die das Kommunizieren dieser drei untereinander ermöglichen

Wir arbeiten dabei besonders eng mit HDFS und MapReduce zusammen.

Mittels Docker starten wir verschiedene Nodes. Auf diesen können wir durch Hadoop HDFS automatisch alle Textdateien (.txt-Format) verteilt speichern. Außerdem können Jobs ausgeführt werden, um die Dateien zu verarbeiten.

Wir starten pro Sprache einen Job der den MapReduce Prozess ausführt. Dieser findet für eine Sprache das Längste Wort und speichert dieses in einer Part-Datei.

[TODO: Oskar, vielleicht kannst du das bessser erklären?)

-  pro Sprache einen Job, der MapReduce-Prozess ausführt -> findet
   längstes Wort pro Sprache
-  Map: Länge des Wortes berechnen, Abspeichern als ,: ,
-  Reduce: das längste Wort als Maximum speichern & sortieren nach Länge
   (Key)
-  Zusammenfassen der längsten Wörter

Derzeitige Probleme:

-  txt-files have commas in path, commas are used for path separation in
   hadoop -> used "FileInputFormat.setInputDirRecursive(job, true);"
-  when unzipping ".\_Deutsch" etc. files get created -> implemented
   workaround
-  two languages fail to unzip (picture) -> renamed
-  use regex for word splitting, currently getting a lot of combines
   words -> implemented different mapper



- aggregationMapper enthält keine information über "path". daher keine zuordnung zur sprache möglich... (bild)
-

Dokumentationsanforderungen
^^^^^^^^^^^^^^^^^^^^^^^^^^^

10+ Seiten und mindestens die folgenden Bestandteile enthalten:

-  Kurze Beschreibung und Erklärung der Aufgabe
-  Detaillierte Lösungsbeschreibung
-  Code-Fragmente mit einer Textbeschreibung
-  Screenshots für die Ergebnisse und/oder Zwischenergebnisse
-  Ausführliche Tests der Anwendung
-  Tabellen, Graphen und Diagramme für die Leistung und vergleichende
   Laufzeit
-  Kurzes Fazit


Doku:

- zwischenergebnisse unter: /hadoop_sv/output
- ergebnisse unter: /hadoop_sv/results