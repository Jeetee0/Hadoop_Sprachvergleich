Performance der Mapper Funktionen
=================================

Für das heraussuchen der Längsten Wörter haben wir zwei verschiedene Mapper implementiert und deren Performance verglichen. 

Replace
~~~~~~~

Der Replace Algorithmus  schaut sich jedes Wort an, entfernt (die meisten) unerwünschten Symbole und prüft ob bereits ein längeres Wort gefunden wurde. 

RegEx
~~~~~

Durch den RegEx Algorithmus werden alle Wörter mithilfe des Regulären Ausdrucks ``\w+`` gesucht und auf Länge überprüft.


Ergebnisse
~~~~~~~~~~

Die Laufzeit der Algorithmen wurde mit den Testdaten überprüft. Es ist ersichtlich dass der RegexMapper wenige Sekunden schneller ist. Darüber hinaus liefert er allerdings auch sauberere Ergebnisse, da er auch Bindestriche filtert. Deswegen haben wir uns für den RegexMapper in der Finalen Implementierung entschieden

+-------------+-----------------+---------------+
| JOB No.     | replaceMapper   | regexMapper   |
+=============+=================+===============+
| 1           | 20s             | 19s           |
+-------------+-----------------+---------------+
| 2           | 27s             | 26s           |
+-------------+-----------------+---------------+
| 3           | 21s             | 21s           |
+-------------+-----------------+---------------+
| 4           | 28s             | 27s           |
+-------------+-----------------+---------------+
| 5           | 25s             | 25s           |
+-------------+-----------------+---------------+
| 6           | 25s             | 24s           |
+-------------+-----------------+---------------+
| 7           | 37s             | 37s           |
+-------------+-----------------+---------------+
| 8           | 25s             | 25s           |
+-------------+-----------------+---------------+
| **Total**   | **213s**        | **209s**      |
+-------------+-----------------+---------------+

