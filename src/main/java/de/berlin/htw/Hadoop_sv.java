package de.berlin.htw;

import de.berlin.htw.mappers.AggregationMapper;
import de.berlin.htw.mappers.RegexMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;
import java.io.IOException;

public class Hadoop_sv {

    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException
    {
        if(args.length != 3)
        {
            System.out.println("Please provide the following paths as arguments: 'input-path', 'output-path' & 'result-path'.");
            System.exit(1);
        }
        if (args[0].equals("") || args[1].equals("") || args[2].equals(""))
            System.exit(1);


        long totalStart = System.currentTimeMillis();

        Configuration conf = new Configuration();

        // input folder should have the following structure:
        // <rootPath>/<languageDirectories>/TXT/<txt-files>
        String rootPath = args[0];
        String destinationPath = args[1];
        String resultPath = args[2];
        File[] languageDirectories = new File(rootPath).listFiles();
        int languageProgress = 0;
        int languages = languageDirectories.length;

        // one job for all files:
        //startJobToCountWordLengthForAFolder(conf, new File(rootPath), destinationPath);

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
        debugMessage(String.format("Time Elapsed for all Jobs: %ds", totalTimeElapsed), "TIMER");


        // use output of first jobs and save to resultPath
        debugMessage(String.format("Aggregating results to %s", resultPath), "DEBUG");

        //data is located at: /hadoop_sprachvergleich/output/<language>/part-r-00000
        //FileUtil.copy(filesystem, new Path(destinationPath), filesystem, new Path(resultPath), false, conf);

        aggregatePartFiles(conf, destinationPath, resultPath);
        debugMessage(String.format("Finished aggregating longest words for all languages. Results are under: %s", resultPath), "DEBUG");

    }

    private static void startJobToCountWordLengthForAFolder(Configuration conf, File languageFolderFile, String destinationPath) throws IOException, InterruptedException, ClassNotFoundException {
        String currentLanguage = languageFolderFile.getName();
        debugMessage(String.format("Start counting words for language folder: '%s''", currentLanguage), "DEBUG");
        
        Job job = Job.getInstance(conf, "Word length counting for language: ");
        job.setJarByClass(Hadoop_sv.class);
        job.setMapperClass(RegexMapper.class);
        job.setCombinerClass(SV_Reducer.class);
        job.setReducerClass(SV_Reducer.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        // sort keys
        job.setSortComparatorClass(LongWritable.DecreasingComparator.class);

        // loop through all subdirectories recursively
        FileInputFormat.setInputDirRecursive(job, true);

        // set input & output paths
        FileInputFormat.addInputPath(job, new Path(languageFolderFile.getAbsolutePath()));
        FileOutputFormat.setOutputPath(job, new Path(destinationPath + currentLanguage));

        // Delete output filepath if already exists
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(new Path(destinationPath + currentLanguage))) {
            fs.delete(new Path(destinationPath + currentLanguage), true);
        }

        job.waitForCompletion(true);

        debugMessage(String.format("DEBUG: Completed counting for language folder: '%s'.", currentLanguage), "DEBUG");
    }

    private static void aggregatePartFiles(Configuration conf, String inputPath, String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
        Job aggregateJob = Job.getInstance(conf, "Aggregating longest words of different languages");
        aggregateJob.setJarByClass(Hadoop_sv.class);
        aggregateJob.setMapperClass(AggregationMapper.class);
        aggregateJob.setReducerClass(Reducer.class);
        aggregateJob.setNumReduceTasks(1);

        aggregateJob.setOutputKeyClass(Text.class);
        aggregateJob.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(aggregateJob, new Path(inputPath));
        FileOutputFormat.setOutputPath(aggregateJob, new Path(outputPath));
        FileInputFormat.setInputDirRecursive(aggregateJob, true);

        // Delete output filepath if already exists
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }

        aggregateJob.waitForCompletion(true);
    }

    private static void debugMessage(String msg, String type){
        String spacing = "------------------------------------";
        System.out.println(String.format("%s %s: %s %s", spacing, type, msg, spacing));
    }
}
