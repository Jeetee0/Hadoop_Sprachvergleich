package de.berlin.htw;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SV_Reducer extends Reducer<LongWritable, Text, LongWritable, Text> {
    private boolean maximumFound = false;

    public void reduce(LongWritable key, Iterable<Text> values, Mapper.Context context) throws IOException, InterruptedException {

        // write every word to output
        if (!maximumFound) {
            for (Text t : values) {
                context.write(key, t);
            }
            maximumFound = true;
        }
    }
}