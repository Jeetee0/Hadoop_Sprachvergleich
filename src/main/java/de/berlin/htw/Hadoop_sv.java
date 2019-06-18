import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnFieldName;
import org.mortbay.util.IO;
import sun.security.krb5.internal.HostAddress;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Hadoop_sv {


    /*
    stuff todo:
    - ergebnisse zusammenfassen: längste wörter pro sprache in zieldatei (Form: Sprache – Längstes Wort – Länge)

     */

    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException
    {
        long totalStart = System.currentTimeMillis();

        Configuration conf = new Configuration();

        // input folder should have the following structure:
        // <rootPath>/<languageDirectories>/TXT/<txt-files>
        String rootPath = args[0];
        String destinationPath = args[1];
        File[] languageDirectories = new File(rootPath).listFiles();
        int languageProgress = 0;
        int languages = languageDirectories.length;


        debugMessage(String.format("Looping through subdirectories of rootfolder: '%s'", rootPath), "DEBUG");
        // create a different job for each language folder
        
        for (File language : languageDirectories) {
            long start = System.currentTimeMillis();

            debugMessage(String.format("Current state: '%s/%s' languages counted.", languageProgress, languages), "DEBUG");
            startJobToCountWordLengthForAFolder(conf, language, destinationPath);
            languageProgress += 1;

            long timeElapsed = (System.currentTimeMillis() - start)/1000;
            debugMessage(String.format("Time Elapsed for this language: %ds", timeElapsed), "TIMER");
        }

        long totalTimeElapsed = (System.currentTimeMillis() - totalStart)/1000;
        debugMessage(String.format("Time Elapsed for alle Jobs: %ds", totalTimeElapsed), "TIMER");

        //todo: aggregate outputs into one final file
        //data is located at: /hadoop_sprachvergleich/output/<language>/part-r-00000
    }

    private static void startJobToCountWordLengthForAFolder(Configuration conf, File languageFolderFile, String destinationPath) throws IOException, InterruptedException, ClassNotFoundException {
        String currentLanguage = languageFolderFile.getName();
        debugMessage(String.format("Start counting words for language folder: '%s''", currentLanguage), "DEBUG");
        
        Job job = Job.getInstance(conf, "Word length counting for language: ");
        job.setJarByClass(Hadoop_sv.class);
        job.setMapperClass(MyMapper.class);
        job.setCombinerClass(MyReducer.class);
        job.setReducerClass(MyReducer.class);

        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        // sort keys
        job.setSortComparatorClass(LongWritable.DecreasingComparator.class);

        // loop through all subdirectories recursively
        FileInputFormat.setInputDirRecursive(job, true);

        // set input & output paths
        FileInputFormat.addInputPaths(job, languageFolderFile.getAbsolutePath());
        FileOutputFormat.setOutputPath(job, new Path(destinationPath + currentLanguage));

        job.waitForCompletion(true);

        debugMessage(String.format("DEBUG: Completed counting for language folder: '%s'.", currentLanguage), "DEBUG");
    }

    private static void debugMessage(String msg, String type){
        String spaceing = "------------------------------------";
        System.out.println(String.format("%s %s: %s %s", spaceing, type, msg, spaceing));
    }
}
