import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import sun.security.krb5.internal.HostAddress;

import java.io.File;
import java.io.IOException;
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
        Job job = Job.getInstance(conf, "Hadoop_Sprachvergleich");
        job.setJarByClass(Hadoop_Sprachvergleich.class);
        job.setMapperClass(MyMapper.class);
        job.setCombinerClass(MyReducer.class);
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);



        // add all txt files to array/list
        String pathToFolder = args[0];
        String pathsToTxtFiles = loopThroughDirectory(pathToFolder);

        FileInputFormat.addInputPaths(job, pathsToTxtFiles);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));



        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    private static String loopThroughDirectory(String pathToLanguageFolder) throws NullPointerException {

        System.out.println("DEBUG: start path: " + pathToLanguageFolder);
        // this String will be accumulated with absolute paths to txt-files separated by a comma. it will be used as Input for the hadoop job
        StringBuilder pathsToTxtFiles = new StringBuilder();

        // accept folder with txt files in following structure: <foldername>/<language>/TXT/<txt files>
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
                                //todo replace ',' in path?
                                pathsToTxtFiles.append(txtFile.getAbsolutePath());
                                pathsToTxtFiles.append(",");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("DEBUG: Accumulated String with absolute Paths: ");
        System.out.println(pathsToTxtFiles.toString());
        return pathsToTxtFiles.toString();
    }
}
