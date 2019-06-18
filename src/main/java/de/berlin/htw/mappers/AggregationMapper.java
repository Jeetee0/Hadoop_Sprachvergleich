package de.berlin.htw.mappers;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.util.regex.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class RegexMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    private long maxLength = 0;
    private String longestWord = "";

    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Matcher m = Pattern.compile("\\w+").matcher(value.toString());

        while (m.find()) {
            String hit = m.group(0);

            if (hit.length() > maxLength) {
                maxLength = hit.length();
                longestWord = hit;
            }
        }
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        context.write(new LongWritable(maxLength), new Text(longestWord));
    }
}
