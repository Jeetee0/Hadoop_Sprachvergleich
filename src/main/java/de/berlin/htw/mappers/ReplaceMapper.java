package de.berlin.htw.mappers;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.util.regex.*;
import java.io.IOException;
import java.util.StringTokenizer;

// deprecated
public class ReplaceMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    private Text word = new Text();
    private long maxLength = 0;
    private String longestWord = "";

    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        StringTokenizer itr = new StringTokenizer(value.toString());

        while (itr.hasMoreTokens()) {
            String currentToken = itr.nextToken().replaceAll("([\\p{Punct}])", "").trim().toLowerCase();
            word.set(currentToken);

            if (word.getLength() > maxLength) {
                maxLength = word.getLength();
                longestWord = word.toString();
            }
        }
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        context.write(new LongWritable(maxLength), new Text(longestWord));
    }
}