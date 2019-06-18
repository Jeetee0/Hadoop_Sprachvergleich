package de.berlin.htw;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;

public class AggregationReducer extends Reducer<Text, Text, LongWritable, Text> {

    public void reduce(Text key, Iterable<Text> values, Mapper.Context context) throws IOException, InterruptedException {
        for (Text t : values) {
            context.write(key, t);
        }
    }

}