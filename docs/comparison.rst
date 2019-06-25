Vergleich
=========

Die Ergebnisse sollen hier mit anderen Ausführungen verglichen werden.

Hadoop - Sprachvergleich ohne Sprachseparierung
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


Zuerst war die Idee, den Job nicht auf Sprachen aufzuteilen, sondern alle Sprachen zusammen als Input zu wählen.
Damit wurden auch etwas schneller Ergebnisse erzielt (996 Sekunden). Hier ist allerdings das Problem, dass uns
dadurch die Information verloren geht, zu welcher Sprache ein Wort gehört. Dadurch werden die Wörter direkt vermischt und sind nicht mehr übersichtlich in einer Datei abrufbar.

Die etwas schnellere Ausführung lässt sich dadurch erklären, da das Programm nur einen Job anlegen muss und nicht pro Sprache einen. Im Live-Betrieb würde das Setup auf ein Multi-Node Cluster umgelegt werden. Hier müsste eine deutliche Performance-verbesserung sichtbar werden.

Python - Sprachvergleich
~~~~~~~~~~~~~~~~~~~~~~~~

Ein Python-Skript sollte die gleiche Aufgabe bewältigen:

- Alle Textdateien eines Verzeichnisses finden
- Textdateien durchgehen und längstes Wort herausfinden
- Pro Sprache das längste Wort finden und alle Sprachen zusammenfassen

Das Skript erledigt diese Aufgabe in 22 Sekunden.
Die Ausgabe des Skriptes:
::

    INFO: Counting words for all txt-files from root-folder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Espanol'
    INFO: Found 25 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Espanol/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Russkyj'
    INFO: Found 223 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Russkyj/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Francais'
    INFO: Found 50 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Francais/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/English'
    INFO: Found 52 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/English/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Ukrajinska'
    INFO: Found 47 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Ukrajinska/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Italiano'
    INFO: Found 50 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Italiano/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Nederlands'
    INFO: Found 5 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Nederlands/TXT'
    INFO: Found 0 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Deutsch'
    INFO: Found 50 amount of files in subfolder: '/Users/d064467/Projects/Hadoop_Sprachvergleich/Docker/textfiles/Deutsch/TXT'
    FINISHED: Needed 22.09651803970337 seconds to count words for 502 files.
    INFO: Longest word for each language:
        Espanol - 20 - circunstanciadamente
        Russkyj - 25 - человеконенавистничеством
        Francais - 21 - constitutionnellement
        English - 70 - Mekkamuselmannenmassenmenchenmoerdermohrenmuttermarmormonumentenmacher
        Ukrajinska - 20 - благочестивомудренно
        Italiano - 27 - quattrocentoquarantatremila
        Nederlands - 22 - landbouwgereedschappen
        Deutsch - 34 - eindusendsöbenhunnertuneiunsösstig