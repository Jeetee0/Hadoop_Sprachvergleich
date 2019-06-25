Test
====

Um die Funktionalität der "jar"-Datei zu testen, wurde das Shell-Skript 'testHadoopSv.sh' geschrieben.
Da die Hadoop-Jobs aus einem Docker-Container heraus gestartet werden, war ein Shell-Skript die einfachste Lösung.
Dieses Skript wird beim Erstellen des Containers direkt in den Container kopiert.
Es benutzt einen Testdatensatz mit nur zwei Sprachen, führt dafür den Hadoop-Sprachvergleich aus und
vergleicht die Resultate mit den bekannten Lösungen.


Das Skript wird mit dem Argument '0' oder '1' gestartet, um den Sprachvergleich zu starten und danach die Ergebnisse auszuwerten (0), oder nur die Ergebnisse auszuwerten (1).

Demnach wird dann der Sprachvergleich ausgeführt oder nicht.
Die Ergebnisse werden dann automatisch vom HDFS in den Container kopiert: ``/hadoop_sv/test_output`` & ``/hadoop_sv/test_results``

Die Auswertung erfolgt nun, indem die zu vergleichenden Werte erst für den Anwender gezeigt werden und danach mithilfe der "assert()"-Methode vergleichen werden.
Falls der Vergleich nicht erfolgreich ist, beendet sich das Programm.

Es werden die folgenden Fälle untersucht:

-  Anzahl der Eingangsdateien bei "Deutsch" müssen mit der deutschen Wörteranzahl übereinstimmen (pro Datei wird ein längstes Wort gespeichert).
-  Anzahl der Ordner (Sprachen) soll mit der Anzahl der Wörter in den Resultaten übereinstimmen (Zwei Ordner resultieren in zwei längsten Wörtern, pro Sprache eins).
-  Das deutsche & englische Wort werden konrekt mit den Wörtern 'himmelherrgottssakkermentische' & 'circunstanciadamente' verglichen, da dies die längsten Wörter in den Beispieldateien sind.