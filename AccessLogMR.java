import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.StreamSupport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Home Page: https://github.com/iShiBin/access-log-analyser
 * @author bin
 * @since 2018-04-14
 *
 */

/*
 * log data format:
 * 190.152.211.196 - - [01/Oct/2017:00:00:00 +0000] "GET /axis2/services/WebFilteringService/getCategoryByUrl?app=chrome_antiporn&ver=0.19.7.1&url=http%3A//www.frlp.utn.edu.ar/materias/integracion3/UT_4_Nomenclatura.pdf&cat=educational-institution HTTP/1.1" 200 133 "-" "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"

 */

public class AccessLogMR extends Configured implements Tool {
    
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    public static class TheMapper extends Mapper<Object, Text, Text, IntWritable> {
        
        private Text date = new Text();
        private final static IntWritable ONE = new IntWritable(1);

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = value.toString().split("]|\\["); // split the string using either ] or [
            date.set(LocalDate.parse(tokens[1], formatter).toString()); // ISO-8601 format uuuu-MM-dd
            context.write(date, ONE);
        }
    }

    public static class TheReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            
//          parallelize the count operation without looping explicitly using Java 8 stream feature
            int count = StreamSupport.stream(values.spliterator(), false).mapToInt(i->i.get()).sum();
            
            context.write(key, new IntWritable(count));
        }
    }
    
    
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        Job job = Job.getInstance(conf, "Access Log Analyser");
        job.setJarByClass(AccessLogMR.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        job.setMapperClass(TheMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setCombinerClass(TheReducer.class);
        
        job.setReducerClass(TheReducer.class);
        
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) {
        int code = -1;
        try {
            code = ToolRunner.run(new Configuration(), new AccessLogMR(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(code);
    }

}
