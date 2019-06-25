Performance der Mapper Funktionen
=================================

Für das Heraussuchen der längsten Wörter haben wir zwei verschiedene Mapper implementiert und deren Performance verglichen.

Replace vs Regex
~~~~~~~~~~~~~~~~

Der Replace-Algorithmus schaut sich jedes Wort an, entfernt (die meisten) unerwünschten Symbole und prüft, ob bereits ein längeres Wort gefunden wurde.
Durch den RegEx-Algorithmus werden alle Wörter mithilfe des regulären Ausdrucks ``\w+`` gesucht und auf Länge überprüft.


Ergebnisse
~~~~~~~~~~

Die Laufzeit der Algorithmen wurde mit den Testdaten überprüft. Es ist ersichtlich dass der RegexMapper wenige Sekunden schneller ist. Darüber hinaus liefert er allerdings auch sauberere Ergebnisse, da er auch Bindestriche filtert. Deswegen haben wir uns für den RegexMapper in der finalen Implementierung entschieden

+-----------+---------------+-------------+
|  JOB No.  | replaceMapper | regexMapper |
+===========+===============+=============+
| 1         | 20s           | 19s         |
+-----------+---------------+-------------+
| 2         | 27s           | 26s         |
+-----------+---------------+-------------+
| 3         | 21s           | 21s         |
+-----------+---------------+-------------+
| 4         | 28s           | 27s         |
+-----------+---------------+-------------+
| 5         | 25s           | 25s         |
+-----------+---------------+-------------+
| 6         | 25s           | 24s         |
+-----------+---------------+-------------+
| 7         | 37s           | 37s         |
+-----------+---------------+-------------+
| 8         | 25s           | 25s         |
+-----------+---------------+-------------+
| **Total** | **213s**      | **209s**    |
+-----------+---------------+-------------+

Aufgrund dieser Ergebnisse wird fortann nurnoch der RegexMapper genutzt. 

Folgende Tests wurden auf zwei verschiedenen Maschinen durchgeführt. 


+------------+----------------------+--------------------+
|  Feature   |       **Mac**        |   **Windows PC**   |
+============+======================+====================+
| OS         | Mac OS               | Windows 10         |
+------------+----------------------+--------------------+
| Prozessor  | Intel Core i7-4850HQ | Intel Core i5-4460 |
+------------+----------------------+--------------------+
| RAM        | 16 GB Dual Channel   | 16 GB Dual Channel |
+------------+----------------------+--------------------+
| Festplatte | AHCI SSD             | AHCI SSD           |
+------------+----------------------+--------------------+

Da die Hardware sehr ähnlich ist, werden Testergebnisse erwartet, die nah beieinander liegen. 

+--------------------+-----------------+----------+----------------+
| textfiles_mini.zip                                               |
+--------------------+-----------------+----------+----------------+
| Language           | Amount of files | **Mac**  | **Windows PC** |
+====================+=================+==========+================+
| Ukrajinska         | 13              | 52s      | 39s            |
+--------------------+-----------------+----------+----------------+
| Deutsch            | 2               | 33s      | 21s            |
+--------------------+-----------------+----------+----------------+
| Francais           | 7               | 40s      | 28s            |
+--------------------+-----------------+----------+----------------+
| Russkyj            | 2               | 27s      | 21s            |
+--------------------+-----------------+----------+----------------+
| Espanol            | 5               | 33s      | 26s            |
+--------------------+-----------------+----------+----------------+
| Italiano           | 5               | 39s      | 26s            |
+--------------------+-----------------+----------+----------------+
| Nederlands         | 6               | 40s      | 27s            |
+--------------------+-----------------+----------+----------------+
| English            | 4               | 29s      | 27s            |
+--------------------+-----------------+----------+----------------+
| **Total**          | **509**         | **297s** | **219s**       |
+--------------------+-----------------+----------+----------------+


+---------------+-----------------+-----------+------------+
| textfiles.zip                                            |
+---------------+-----------------+-----------+------------+
| Language      | Amount of files | Mac       | Windows PC |
+===============+=================+===========+============+
| Ukrajinska    | 47              | 116s      | 99s        |
+---------------+-----------------+-----------+------------+
| Deutsch       | 51              | 102s      | 102s       |
+---------------+-----------------+-----------+------------+
| Francais      | 51              | 104s      | 125s       |
+---------------+-----------------+-----------+------------+
| Russkyj       | 224             | 396s      | 468s       |
+---------------+-----------------+-----------+------------+
| Espanol       | 26              | 62s       | 70s        |
+---------------+-----------------+-----------+------------+
| Italiano      | 51              | 123s      | 108s       |
+---------------+-----------------+-----------+------------+
| Nederlands    | 6               | 31s       | 27s        |
+---------------+-----------------+-----------+------------+
| English       | 53              | 148s      | 119s       |
+---------------+-----------------+-----------+------------+
| **Total**     | **509**         | **1085s** | **1126s**  |
+---------------+-----------------+-----------+------------+

Wenn die Anzahl der Jobs auf einen Reduziert wird, kann eine Performance verbesserung von ca. 8% (auf 996 Sekunden) beobachtet werden. Dies ist zurück zu führen auf das Starten und Stoppen der Jobs. Im Live-Betrieb würde das Setup auf ein Multi-Node Cluster umgelegt werden. Hier müsste eine deutliche Performance-verbesserung sichtbar werden.