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

public class Hadoop_Sprachvergleich {


    /*
    stuff todo:
    - pro Sprache: wörterlänge zählen, sortieren nach länge
    - ergebnisse zusammenfassen: längste wörter pro sprache in zieldatei (Form: Sprache – Längstes Wort – Länge)


     */

    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException
    {
        Configuration conf = new Configuration();

        // input folder should have the following structure:
        // <rootPath>/<languageDirectories>/TXT/<txt-files>
        String rootPath = args[0];
        String destinationPath = args[1];
        File[] languageDirectories = new File(rootPath).listFiles();

        // create a different job for each language folder
        System.out.println("------------------------------------ DEBUG: Looping through subdirectories of rootfolder: '" + rootPath + "' ------------------------------------");
        for (File language : languageDirectories) {
            startJobToCountWordLengthForAFolder(conf, language, destinationPath);
        }
    }

    private static void startJobToCountWordLengthForAFolder(Configuration conf, File languageFolderFile, String destinationPath) throws IOException, InterruptedException, ClassNotFoundException {
        String currentLanguage = languageFolderFile.getName();
        System.out.println("------------------------------------ DEBUG: Start counting words for language folder: '" + currentLanguage + "' ------------------------------------");

        Job job = Job.getInstance(conf, "Word length counting for language: ");
        job.setJarByClass(Hadoop_Sprachvergleich.class);
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

        System.out.println("------------------------------------ DEBUG: Completed counting for language folder: " + currentLanguage + " ------------------------------------");
    }





    private static String loopThroughDirectory(String pathToLanguageFolder) throws NullPointerException, IOException {

        System.out.println("------------------------------------ DEBUG: start path: " + pathToLanguageFolder);
        // this String will be accumulated with absolute paths to txt-files separated by a comma. it will be used as Input for the hadoop job
        StringBuilder pathsToTxtFiles = new StringBuilder();

        // accept folder with txt files in
        File[] languages = new File(pathToLanguageFolder).listFiles();

        // /<language>/TXT/<txt files>
        for (File laguage : languages) {
            if (laguage.isDirectory()) {
                File[] subdirectories = laguage.listFiles();

                // /TXT/<txt files>
                for (File subdirectory : subdirectories) {
                    if (subdirectory.isDirectory()) {
                        File[] txtFilesOfOneLanguage = subdirectory.listFiles();

                        // /<txt files>
                        for (File txtFile : txtFilesOfOneLanguage) {
                            if (!txtFile.isDirectory()) {
                                String currentPath = txtFile.getAbsolutePath();
                                //System.out.println("DEBUG: currentPath: " + currentPath);
                                //todo replace ',' in path?
                                if (currentPath.contains(", ")) {
                                    System.out.println();
                                    System.out.println();
                                    System.out.println("------------------------------------ DEBUG: contains ',': " + currentPath);
                                    java.nio.file.Path source = Paths.get(currentPath);
                                    Files.move(source, source.resolveSibling(currentPath.replace(',', '_')));
                                    currentPath = txtFile.getAbsolutePath();
                                }

                                //quick fix: skip paths that contain '._'
                                if (!currentPath.contains("._")) {
                                    pathsToTxtFiles.append(currentPath);
                                    pathsToTxtFiles.append(",");
                                }


                                //pathsToTxtFiles.append(.replaceAll(",","\\,"));


                            }
                        }
                    }
                }
            }
        }
        return pathsToTxtFiles.toString();
    }
}
