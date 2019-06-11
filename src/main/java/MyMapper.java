import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    private Text word = new Text();
    private long maxLength = 0;
    private String longestWord = "";

    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());

        // mapping will be done for each language
        System.out.println("DEBUG: Mapper started mapping...");
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());

            if (word.getLength() > maxLength) {
                maxLength = word.getLength();
                longestWord = word.toString();
            }
        }
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        System.out.println("DEBUG: Mapper is at cleanup...");
        context.write(new LongWritable(maxLength), new Text(longestWord));
    }
}