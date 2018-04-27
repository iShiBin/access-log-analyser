
/**
 * Use the MapReduce Random Sample pattern. 
 * 
 * @author Bin Shi
 * @since 2018-04-26
 */

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SamplerMR extends Configured implements Tool {
   
    public static class TheMapper extends Mapper<Object, Text, NullWritable, Text> {
        
        private static double rate = 0.01; // default value
        private static Random random = new Random();
        
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            rate = context.getConfiguration().getDouble("rate", rate); // use '-D rate=0.02' in the command line
            rate = rate > 1.0? rate/100: rate;
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            if (random.nextDouble() < rate) {
                context.write(NullWritable.get(), value);
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        Job job = Job.getInstance(conf, "Random Sampler");
        job.setJarByClass(SamplerMR.class);
        job.setNumReduceTasks(0); // map only job

        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        job.setMapperClass(TheMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) {
        int code = -1;
        try {
            code = ToolRunner.run(new Configuration(), new SamplerMR(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(code);
    }
}