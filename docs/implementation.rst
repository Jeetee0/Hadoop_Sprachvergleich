Umsetzung / Implementierung
===========================



Entwicklerumgebung
^^^^^^^^^^^^^^^^^^

Mittels IDE oder Konsole kann ein Maven-Projekt erstellt werden, in
welchem die Dependencies ``hadoop-core`` und
``hadoop-mapreduce-client-core`` eingebunden werden müssen.

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



Hauptklasse: **Hadoop_sv.java**

- Umfasst Konfiguration (Drei Variablen als Argumente: Input, Output & Results)
- Ausführen der verschiedenen Jobmethoden
- Konsolenausgaben zum nachvollziehen des Fortschritts
- Stoppt die Zeit zum Ausführen der verschiedenen Jobs

Mapper: 

**RegexMapper**/**ReplaceMapper**:

- Findet in jeder Datei das längste Wort


**AggregationMapper**

- Findet das längste Wort von jeder Sprache

Reducer: **SV_Reducer**

- Fässt alle langen Worte einer Sprache zusammen

