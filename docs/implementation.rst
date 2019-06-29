Umsetzung / Implementierung
===========================



Entwicklerumgebung
^^^^^^^^^^^^^^^^^^

Mittels IDE oder Konsole kann ein Maven-Projekt erstellt werden, in
welchem die Dependencies ``hadoop-core`` und
``hadoop-mapreduce-client-core`` eingebunden werden müssen. Damit können Hadoop-Funktionen verwendet werden.

Struktur
~~~~~~~~


::

    ├── java
    │   └── de
    │       └── berlin
    │           └── htw
    │               ├── Hadoop_sv.java
    │               ├── SV_Reducer.java
    │               └── mappers
    │                   ├── AggregationMapper.java
    │                   ├── RegexMapper.java
    │                   └── ReplaceMapper.java
    ├── python
    │   └── python-sprachvergleich.py
    └── resources



Hauptklasse - **Hadoop_sv.java**

- Umfasst Konfiguration der Hadoop-Jobs und Verarbeitung der Eingangsvariablen (Drei Variablen als Argumente: Input, Output & Results)
- Ausführen der verschiedenen Jobmethoden
- Konsolenausgaben zum nachvollziehen des Fortschritts
- Stoppt die Zeit zum Ausführen der verschiedenen Jobs

Mapper - **RegexMapper**/**ReplaceMapper**:

- Liest alle Wörter einer Datei und zählt die Länge. Gibt nur das längste Wort an den Reducer weiter.


**AggregationMapper**

- Bearbeitet Dateien verschiedener Sprachen und fässt die längsten Wörter aller Sprachen zusammen.

Reducer: **SV_Reducer**

- Fässt alle langen Worte einer Sprache zusammen

