package de.berlin.htw.mappers;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AggregationMapper extends Mapper<LongWritable, Text, Text, Text> {

    String language = "";
    String path = "";
    int max = 0;
    String longestWord = "";

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

    @Override
    public void setup(Mapper.Context context) throws IOException {
        language = context.toString();
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        String[] filepath = context.getInputSplit().toString().split("/");
        language = filepath[filepath.length- 2];
        context.write(new Text(language), new Text(longestWord));
    }
}
