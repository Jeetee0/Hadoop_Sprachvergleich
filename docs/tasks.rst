Aufgabenstellung
================


Wir haben uns die Aufgabe 11 ausgesucht. Dazu gehören drei
Hauptbestandteile

-  Zählen der Wörterlänge (pro Sprache)
-  Sortieren der Wörter der Länge nach (pro Sprache)
-  Zusammenfassen der Ergebnisse

Dabei sollen die Ergebnisse in einer Datei mit der Form: "Sprache – Längstes Wort – Länge“ zusammengefasst werden.

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


Implementierung
^^^^^^^^^^^^^^^

Derzeitiger Ansatz:

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

Doku:

- zwischenergebnisse unter: /hadoop_sv/output
- ergebnisse unter: /hadoop_sv/results