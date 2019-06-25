Aufgabenstellung
================

    *Hinweis* 

    Diese Dokumentation ist ebenfalls einsehbar auf `readthedocs.io <https://hadoop-sprachvergleich.readthedocs.io/>`__ .

    Der Quellcode des Projekts ist öffentlich auf `github <https://github.com/Jeetee0/Hadoop_Sprachvergleich>`__.

Dies ist eine Belegaufgabe im Rahmen unseres Studiums an der HTW Berlin im Master Angewandte Informatik. Im Kurs Programmierkonzepte & Algorithmen arbeiten wir an Aufgabe 11. Es liegt ein Datensatz von 502 Textdateien vor (Beletristik) - unterteilt in 8 Sprachen. Ziel ist es, mit Apache Hadoop diese Texte zu analysieren und dabei drei Aufgaben zu erfüllen:

1. Zählen der Wörterlänge (pro Sprache)
2. Sortieren der Wörter der Länge nach (pro Sprache)
3. Zusammenfassen der Ergebnisse

Dabei soll eine Ausgabe in der Form: "Sprache – Längstes Wort – Länge“ generiert werden.

`sequenceiq <https://hub.docker.com/r/sequenceiq/hadoop-docker/>`__

Dokumentationsanforderungen
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Anforderung an Dokumentation sind mindestens 10 Seiten und sie muss mindestens die folgenden Bestandteile enthalten:

-  Kurze Beschreibung und Erklärung der Aufgabe
-  Detaillierte Lösungsbeschreibung
-  Code-Fragmente mit einer Textbeschreibung
-  Screenshots für die Ergebnisse und/oder Zwischenergebnisse
-  Ausführliche Tests der Anwendung
-  Tabellen, Graphen und Diagramme für die Leistung und vergleichende
   Laufzeit
-  Kurzes Fazit

Lösungsansatz
^^^^^^^^^^^^^

Hadoop stellt vorrangig vier Dinge bereit:

    1. Hadoop distributed file system (HDFS) - ein Verteiltes Dateisystem
    2. Hadoop MapReduce - ein System für parallele Datenverarbeitung
    3. Hadoop YARN - ein Framework um zur Job-Ablaufplanung (scheduling) und Cluster Ressourcen Management
    4. Hadoop Common - alle Werkzeuge die das Kommunizieren dieser drei untereinander ermöglichen

Wir arbeiten dabei besonders eng mit HDFS und MapReduce zusammen.

Mittels Docker starten wir einen Single-Node Cluster. Auf diesen können wir durch HDFS (Hadoop distributed file system) automatisch alle Textdateien (.txt-Format) verteilt speichern. Außerdem können Jobs ausgeführt werden, um die Dateien zu verarbeiten.

Wir starten pro Sprache einen Job der den MapReduce Prozess ausführt. Dieser findet für eine Sprache das längste Wort und speichert dieses in einer Part-Datei.
