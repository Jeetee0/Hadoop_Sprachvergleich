package de.berlin.htw;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;

public class SV_Reducer extends Reducer<LongWritable, Text, LongWritable, Text> {
    private boolean maximumFound = false;
    private MultipleOutputs<LongWritable, Text> multipleOutputs;
    String language;

    public void reduce(LongWritable key, Iterable<Text> values, Mapper.Context context) throws IOException, InterruptedException {
        // remove duplicates
        // find max for each language
        // todo: find maximum of longest words

        if (!maximumFound) {
            for (Text t : values) {
                //context.write(key, t);
                multipleOutputs.write(key, t, language);
            }
            maximumFound = true;
        }
    }

    @Override
    public void setup(Context context) throws IOException {
        multipleOutputs = new MultipleOutputs<>(context);
        language = context.getWorkingDirectory().toString();
    }

    @Override
    public void cleanup(final Context context) throws IOException, InterruptedException{
        multipleOutputs.close();
    }
}