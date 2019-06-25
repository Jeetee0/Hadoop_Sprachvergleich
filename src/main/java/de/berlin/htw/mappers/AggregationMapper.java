package de.berlin.htw.mappers;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AggregationMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    private int max = 0;
    private String longestWord = "";

    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String lines[] = value.toString().split("\\r?\\n");
        for (String line : lines) {
            String number = line.split("\\t")[0];
            int length = Integer.parseInt(number);

            if (length > max) {
                max = length;
                longestWord = line;
            }
        }
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        String[] filepath = context.getInputSplit().toString().split("/");
        String language = filepath[filepath.length- 2];
        context.write(new Text(language), new Text(longestWord));
    }
}
