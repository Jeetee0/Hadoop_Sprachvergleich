Performance der Mapper Funktionen
=================================

Für das Heraussuchen der längsten Wörter haben wir zwei verschiedene Mapper implementiert und deren Performance verglichen.

Replace
~~~~~~~

Der Replace-Algorithmus schaut sich jedes Wort an, entfernt (die meisten) unerwünschten Symbole und prüft, ob bereits ein längeres Wort gefunden wurde.

RegEx
~~~~~

Durch den RegEx-Algorithmus werden alle Wörter mithilfe des regulären Ausdrucks ``\w+`` gesucht und auf Länge überprüft.


Ergebnisse
~~~~~~~~~~

Die Laufzeit der Algorithmen wurde mit den Testdaten überprüft. Es ist ersichtlich dass der RegexMapper wenige Sekunden schneller ist. Darüber hinaus liefert er allerdings auch sauberere Ergebnisse, da er auch Bindestriche filtert. Deswegen haben wir uns für den RegexMapper in der finalen Implementierung entschieden

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


Ausführung auf MacBookPro, 2,3 GHz, 16 GB Memory:
(textfiles_mini.zip)

+-------------+-----------------+---------------+
| Language    | Amount of files | Elapsed time  |
+=============+=================+===============+
| Ukrajinska  | 13              | 52s           |
+-------------+-----------------+---------------+
| Deutsch     | 2               | 33s           |
+-------------+-----------------+---------------+
| Francais    | 7               | 40s           |
+-------------+-----------------+---------------+
| Russkyj     | 2               | 27s           |
+-------------+-----------------+---------------+
| Espanol     | 5               | 33s           |
+-------------+-----------------+---------------+
| Italiano    | 5               | 39s           |
+-------------+-----------------+---------------+
| Nederlands  | 6               | 40s           |
+-------------+-----------------+---------------+
| English     | 4               | 29s           |
+-------------+-----------------+---------------+
| **Total**   | **509**         | **297s**      |
+-------------+-----------------+---------------+

(textfiles.zip)

+-------------+-----------------+---------------+
| Language    | Amount of files | Elapsed time  |
+=============+=================+===============+
| Ukrajinska  | 47              | 116s          |
+-------------+-----------------+---------------+
| Deutsch     | 51              | 102s          |
+-------------+-----------------+---------------+
| Francais    | 51              | 104s          |
+-------------+-----------------+---------------+
| Russkyj     | 224             | 396s          |
+-------------+-----------------+---------------+
| Espanol     | 26              | 62s           |
+-------------+-----------------+---------------+
| Italiano    | 51              | 123s          |
+-------------+-----------------+---------------+
| Nederlands  | 6               | 31s           |
+-------------+-----------------+---------------+
| English     | 53              | 148s          |
+-------------+-----------------+---------------+
| **Total**   | **509**        | **1085s**      |
+-------------+-----------------+---------------+

