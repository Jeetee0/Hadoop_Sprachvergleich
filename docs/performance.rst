Performance
===========

Wir haben verschiedene Tests aufgestellt um die Performance unseres Codes und die von Hadoop zu evaluieren.

Replace vs Regex
~~~~~~~~~~~~~~~~

Für das Heraussuchen der längsten Wörter haben wir zwei verschiedene Mapper implementiert und deren Performance verglichen.

Der Replace-Algorithmus schaut sich jedes Wort an, entfernt (die meisten) unerwünschten Symbole und prüft, ob bereits ein längeres Wort gefunden wurde.
Durch den RegEx-Algorithmus werden alle Wörter mithilfe des regulären Ausdrucks ``\w+`` gesucht und auf Länge überprüft.

Die Laufzeit der Algorithmen wurde mit den Testdaten überprüft. Es ist ersichtlich dass der RegexMapper wenige Sekunden schneller ist. Darüber hinaus liefert er allerdings auch sauberere Ergebnisse, da er auch Bindestriche filtert. Deswegen haben wir uns für den RegexMapper in der finalen Implementierung entschieden.

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

Aufgrund dieser Ergebnisse wird fortan nur noch der RegexMapper genutzt.

Ausführungsdauer des Sprachvergleichs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Der Sprachvergleich wurde mit zwei verschiedenen Datensätzen getestet. ``textfiles.zip`` enthält den Vollen Datensatz aller 502 Textdateien, verteilt über die acht Sprachen. ``textfiles_mini.zip`` ist  eine Reduzierte Version, angelehnt an ``textfiles.zip``, speziell für Tests ausgelegt. Sie enthält nur 44 Textdateien.

Außerdem wurden die Tests auf folgenden zwei Maschinen durchgeführt:

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

In den unten stehenden Tabellen ist die Ausführungsdauer der jeweiligen Schritte und der Gesammtdauer dargestellt. 

+--------------------+-----------------+----------------+----------------+
| textfiles_mini.zip                                                     |
+--------------------+-----------------+----------------+----------------+
| Language           | Amount of files | **MacBookPro** | **Windows PC** |
+====================+=================+================+================+
| Ukrajinska         | 13              | 52s            | 39s            |
+--------------------+-----------------+----------------+----------------+
| Deutsch            | 2               | 33s            | 21s            |
+--------------------+-----------------+----------------+----------------+
| Francais           | 7               | 40s            | 28s            |
+--------------------+-----------------+----------------+----------------+
| Russkyj            | 2               | 27s            | 21s            |
+--------------------+-----------------+----------------+----------------+
| Espanol            | 5               | 33s            | 26s            |
+--------------------+-----------------+----------------+----------------+
| Italiano           | 5               | 39s            | 26s            |
+--------------------+-----------------+----------------+----------------+
| Nederlands         | 6               | 40s            | 27s            |
+--------------------+-----------------+----------------+----------------+
| English            | 4               | 29s            | 27s            |
+--------------------+-----------------+----------------+----------------+
| **Total**          | **44**          | **297s**       | **219s**       |
+--------------------+-----------------+----------------+----------------+




+---------------+-----------------+----------------+----------------+
| textfiles.zip                                                     |
+---------------+-----------------+----------------+----------------+
| Language      | Amount of files | **MacBookPro** | **Windows PC** |
+===============+=================+================+================+
| Ukrajinska    | 46              | 116s           | 99s            |
+---------------+-----------------+----------------+----------------+
| Deutsch       | 50              | 102s           | 102s           |
+---------------+-----------------+----------------+----------------+
| Francais      | 50              | 104s           | 125s           |
+---------------+-----------------+----------------+----------------+
| Russkyj       | 223             | 396s           | 468s           |
+---------------+-----------------+----------------+----------------+
| Espanol       | 25              | 62s            | 70s            |
+---------------+-----------------+----------------+----------------+
| Italiano      | 50              | 123s           | 108s           |
+---------------+-----------------+----------------+----------------+
| Nederlands    | 5               | 31s            | 27s            |
+---------------+-----------------+----------------+----------------+
| English       | 52              | 148s           | 119s           |
+---------------+-----------------+----------------+----------------+
| **Total**     | **502**         | **1085s**      | **1126s**      |
+---------------+-----------------+----------------+----------------+

Multi-Node-Cluster
~~~~~~~~~~~~~~~~~~

Nachträglich versuchten wir ein Multi-Node-Cluster bei dem Online-Dienst Hetzner aufzusetzen. Wir mieteten uns dort mehrere Server, um auf diesen jeweils einen unserer Docker-Container mit Hadoop starten zu können.
Dadurch erhofften wir uns ein deutlich größeres Potential, unseren Sprachvergleich parallelisiert ausführen zu können.

Wir erzeugten mithilfe des Python-Skriptes ``multiply_filesize.py`` eine größere Inputdatei, bei der wir die Länge der einzelnen Textdateien verfünffachten.
Dadurch wollten wir herausfinden, wie sich Hadoop bei größeren Datenmengen verhält. Da es für Auswertung von Petabyte an Daten gedacht ist, wollten wir damit ein bisschen in diese Richtung gehen.
Der MapReduce-Prozess profitiert durch die längeren Textdateien. Die Mapper werten über längere Zeit parallel die einzelnen Textdateien aus.
Dadurch soll der Overhead, der durch Anlegen der verschiedenen Jobs und Mapper entsteht, kompensiert werden.

Leider gelang es uns nicht den Job auf diesen Servern zu starten. Wir haben sehr viele Konfigurationen probiert, allerdings akzeptierte das HDFS die Slave-Container nicht als datanodes, obwohl dies beim Starten bestätigt wurde.
