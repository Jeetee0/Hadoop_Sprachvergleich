Quellcodebeschreibung
=====================

In diesem Abschnitt soll der implementierte Quellcode genau beschrieben werden.


Hadoop_sv
^^^^^^^^^

Die Hauptklasse des Projektes. Von dieser Klasse aus wird der Sprachvergleich gestartet.

Main
~~~~

Diese Methode ist der Einstiegspunkt in das Programm.

Zum Messen der Ausführungszeit nutzen wir ``System.currentTimeMillis()``.

::

    long totalStart = System.currentTimeMillis();


Zuerst wird die Angabe der nötigen Argumente überprüft und, wenn vorhanden, in Variablen gespeichert. Zusätzlich werden weitere Variablen initialisiert, die für die kommenden Schritte verwendet werden.

::

    if(args.length != 3)
    {
        System.out.println("Please provide the following paths as arguments (3): 'input-path', 'output-path' & 'result-path'.");
        System.exit(1);
    }
    if (args[0].equals("") || args[1].equals("") || args[2].equals(""))
        System.exit(1);

    // input folder should have the following structure:
    // <rootPath>/<languageDirectories>/TXT/<txt-files>
    String rootPath = args[0];
    String destinationPath = args[1];
    String resultPath = args[2];
    File[] languageDirectories = new File(rootPath).listFiles();
    int languageProgress = 0;
    int languages = languageDirectories.length;



Danach erstellen wir eine Konfiguration für hadoop

::

    Configuration conf = new Configuration();

Jetzt kann entweder ein Job für alle Sprachpakete ausgeführt werden:
::

    startJobToCountWordLengthForAFolder(conf, new File(rootPath), destinationPath);

oder für jede Sprache ein eigener Job ausgeführt werden (für Multi-Node-Cluster sinnvoll).
Die for-Schleife mit der Funktion ``startJobToCountWordLengthForAFolder`` iteriert durch die bereitgestellten Ordner.
Währendessen werden stets Debug-Informationen ausgegeben.

::

    debugMessage(String.format("Looping through subdirectories of rootfolder: '%s'", rootPath), "DEBUG");
    for (File language : languageDirectories) {
        long start = System.currentTimeMillis();

        debugMessage(String.format("Current state: '%s/%s' languages counted.", languageProgress, languages), "DEBUG");
        startJobToCountWordLengthForAFolder(conf, language, destinationPath);
        languageProgress += 1;

        long timeElapsed = (System.currentTimeMillis() - start)/1000;
        debugMessage(String.format("Time Elapsed for this language: %ds", timeElapsed), "TIMER");
    }

    long totalTimeElapsed = (System.currentTimeMillis() - totalStart)/1000;
    debugMessage(String.format("Time Elapsed for all Jobs: %ds", totalTimeElapsed), "TIMER");

    // use output of first jobs and save to resultPath
    debugMessage(String.format("Aggregating results to %s", resultPath), "DEBUG");

    //data is located at: /hadoop_sprachvergleich/output/<language>/part-r-00000


Zuletzt werden die "part"-Dateien mit der Methode ``aggregatePartFiles`` zusammengefasst, sodass aus den Einzeldateien jeder Sprache eine Datei entsteht, die Wörter aller Sprachen enthält.


::

    aggregatePartFiles(conf, destinationPath, resultPath);
    debugMessage(String.format("Finished aggregating longest words for all languages. Results are under: %s", resultPath), "DEBUG");

startJobToCountWordLengthForAFolder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Diese Methode startet einen Job, um die längsten Wörter aus einem Ordner zu sammeln.

Der Job wird mithilfe der folgenden Codezeilen konfiguriert. Es werden unsere Mapper- und Reducer-Klassen genutzt. Die Anzahl der Reducer wird auf eins gesetzt. Dadurch erhalten wir nur eine Datei, die die Wörter beeinhaltet.
Da das Key-Value Pair einen "Long-Text" erstellt, setzen wir ``OutputKey`` und ``OutputValue`` entsprechend.

::

    Job job = Job.getInstance(conf, "Word length counting for language: ");
    job.setJarByClass(Hadoop_sv.class);
    job.setMapperClass(RegexMapper.class);
    job.setCombinerClass(SV_Reducer.class);
    job.setReducerClass(SV_Reducer.class);
    job.setNumReduceTasks(1);
    job.setOutputKeyClass(LongWritable.class);
    job.setOutputValueClass(Text.class);

Um die Keys absteigend zu sortieren, setzen wir noch folgenden Wert:

::

    // sort keys
    job.setSortComparatorClass(LongWritable.DecreasingComparator.class);

Wir geben an, die Verzeichnisse Rekursiv zu durchsuchen (sodass auch alle Unterverzeichnisse nach .txt Dateien untersucht werden)

::

    // loop through all subdirectories recursively
    FileInputFormat.setInputDirRecursive(job, true);

Wir setzen die In- und Outputpfade unseres Jobs:

::

    // set input & output paths
    FileInputFormat.addInputPath(job, new Path(languageFolderFile.getAbsolutePath()));
    FileOutputFormat.setOutputPath(job, new Path(destinationPath + currentLanguage));

Gegebenenfalls muss noch der alte Output ordner gelöscht werden. 

::

    // Delete output filepath if already exists
    FileSystem fs = FileSystem.get(conf);
    if (fs.exists(new Path(destinationPath + currentLanguage))) {
        fs.delete(new Path(destinationPath + currentLanguage), true);
    }

Der Job wird gestartet:

::

    job.waitForCompletion(true);

    debugMessage(String.format("DEBUG: Completed counting for language folder: '%s'.", currentLanguage), "DEBUG");



aggregatePartFiles
~~~~~~~~~~~~~~~~~~

Hier wird ein Job angelegt um die PartFiles zu aggregieren. Je Sprache gibt es ein PartFile. In jedem Part file befindet sich je eine Zeile für jede .txt Datei mit dem jeweils längsten Wort dieser Datei. Diese werden in der durch den in aggregatePartFiles aufgesetzten Job zusammengefasst.

Wir holen uns die Configurierte Instanz und setzen für unseren Aggregierungsjob unseren Mapper ``AggregationMapper``. Für den Reducer reicht der ``Reducer.class`` (Oberklasse). Wir möchten dabei nur einen Reduce Task ausführen.
::


    Job aggregateJob = Job.getInstance(conf, "Aggregating longest words of different languages");
    aggregateJob.setJarByClass(Hadoop_sv.class);
    aggregateJob.setMapperClass(AggregationMapper.class);
    aggregateJob.setReducerClass(Reducer.class);
    aggregateJob.setNumReduceTasks(1);


Da wir die Key-Value Paare für die Ausgabe als jeweils als Text erwarten setzen wir die entsprechende Werte.
::

    aggregateJob.setOutputKeyClass(Text.class);
    aggregateJob.setOutputValueClass(Text.class);


Wir setzen die durch die Argumente bereitgestellten Input und Output Pfade für unseren ``aggregateJob`` und geben an, die Verzeichnisse Rekursiv zu durchsuchen (sodass auch alle Unterverzeichnisse nach .txt Dateien untersucht werden)

::

    FileInputFormat.addInputPath(aggregateJob, new Path(inputPath));
    FileOutputFormat.setOutputPath(aggregateJob, new Path(outputPath));
    FileInputFormat.setInputDirRecursive(aggregateJob, true);

Zum schluss sorgen wir dafür, dass das Ausgabeverzeichniss gelöscht wird sofern dieses bereits existiert und wir starten unseren Job.

::

    // Delete output filepath if already exists
    FileSystem fs = FileSystem.get(conf);
    if (fs.exists(new Path(outputPath))) {
        fs.delete(new Path(outputPath), true);
    }

    aggregateJob.waitForCompletion(true);

debugMessage
~~~~~~~~~~~~

Dies Funktion formatiert lediglich Debug Nachrrichten, damit sie eindeutig von den Hadoop-Internen Nachrrichten zu unterscheiden sind. 

::

    String spacing = "------------------------------------";
    System.out.println(String.format("%s %s: %s %s", spacing, type, msg, spacing));


SV_Reducer
^^^^^^^^^^

Der SV_Reducer wird für jeden Job ein mal Ausgeführt und fasst die Key-Value-Pairs aller Mapper dieses Jobs zusammen. 

reduce
~~~~~~

Wir prüfen ob ein Wort gefunden wurde. Dann iterieren wir über alle Key-Value-Paare und schreiben diese in den Kontext.
::

    if (!maximumFound) {
        for (Text t : values) {
            context.write(key, t);
        }
        maximumFound = true;
    }






RegexMapper
^^^^^^^^^^^

Der Regex Mapper durchsucht, wie auch der Replace Mapper, eine ganze Datei nach dem längsten Wort. 

map
~~~

Hier wird mittels der Regular Expression ``\w`` jedes Wort erfasst. Wichtig ist das Pattern auf ``Pattern.UNICODE_CHARACTER_CLASS`` zu setzen. 

::

    Matcher m = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS).matcher(value.toString());

Danach können wir die Matches durchsuchen und bei jedem Hit überprüfen ob wir hiermit ein längeres gefunden haben. 

::

    while (m.find()) {
        String hit = m.group(0);

        if (hit.length() > maxLength) {
            maxLength = hit.length();
            longestWord = hit;
        }
    }

cleanup
~~~~~~~

Beim Cleanup schreiben wir nurnoch das Key-Value-Pair in den Entsprechenden Kontext.

::

    context.write(new LongWritable(maxLength), new Text(longestWord));


ReplaceMapper
^^^^^^^^^^^^^

Der Replace Mapper durchsucht, wie auch der Regex Mapper, eine ganze Datei nach dem längsten Wort. 

map
~~~

Der Ansatz ist anders als bei RegexMapper, da wir nicht alle Wörter mit ``\w`` finden, sondern mit dem StringTokenizer alle Wörter in dem Dokument trennen. 

::

    StringTokenizer itr = new StringTokenizer(value.toString());


Danach können wir über alle diese Wörter iterieren. Wir entfernen noch jegliche Sonderzeichen und suchen dann das längste wie gewohnt heraus.

::

    while (itr.hasMoreTokens()) {
        String currentToken = itr.nextToken().replaceAll("([\\p{Punct}])", "").trim().toLowerCase();
        word.set(currentToken);

        if (word.getLength() > maxLength) {
            maxLength = word.getLength();
            longestWord = word.toString();
        }
    }


cleanup
~~~~~~~

Beim Cleanup schreiben wir nurnoch das Key-Value-Pair in den Entsprechenden Kontext.

::

    context.write(new LongWritable(maxLength), new Text(longestWord));


AggregationMapper
^^^^^^^^^^^^^^^^^

Der AggregationMapper wird zuletzt ausgeführt und fasst alle Part-Dateien zusammen. 

map
~~~

In der Map Funktion iterieren wir über alle Zeilen (im Key-Value-Pair) und findet darin das längste Wort pro Sprache. 

::

    String lines[] = value.toString().split("\\r?\\n");

    for (String line : lines) {
        String number = line.split("\\t")[0];
        int length = Integer.parseInt(number);

        if (length > max) {
            max = length;
            longestWord = line;
        }
    }


cleanup
~~~~~~~

In dieser Funktion schreiben wir in den Context die Sprache und das Längste Wort welches ebenfalls die Anzahl an Zeichen enthält. Davor holen wir uns noch die Sprache aus dem Dateipfad des aktuellen Kontextes. 

::

    String[] filepath = context.getInputSplit().toString().split("/");
    language = filepath[filepath.length- 2];
    context.write(new Text(language), new Text(longestWord));