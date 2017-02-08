//Import all required libraries
package project.kfold;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Iterator;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import java.lang.Math;

public class LinearRegression {
	
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		if (args.length != 2) {
			System.out.println("usage: [input] [output]");
			System.exit(-1);
		}

		Job job = Job.getInstance(new Configuration());
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(RegressMapper.class);
		job.setReducerClass(SumReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setJarByClass(LinearRegression.class);
		job.waitForCompletion(true);
		long endTime = System.currentTimeMillis();
		long duration = (endTime-startTime)/1000;
		System.out.println(String.format("Time duration: %s sec\n", duration));
	}
	public static class RegressMapper extends Mapper<Object, Text, IntWritable, Text> {
		@Override
			public void map(Object key, Text value,
					Context contex) throws IOException, InterruptedException {
                    // Intialize indices
				int index = 0;
				int trainingDataIndex = 0;
				int testingDataIndex = 0;
			try {
            // Read data from input and split them with ',' and set fold size =10 
				String line = value.toString();
				String[] sline = line.split(",");
				int length = sline.length;
				int foldSize = 10;
				int vars = 2; 
				int obs = (length-1)/(vars+1);

				int originalSampleSize = (length-1)/(vars+1);
				int sampleSize = originalSampleSize - (originalSampleSize%foldSize);

				int trainingDataSize = (vars+1)*sampleSize*(foldSize-1)/foldSize;

				Double[] trainingData = new Double[trainingDataSize];
				if (trainingData == null) throw new RuntimeException ("Null training data\n");
			
                //set the test data size (1 fold from the 10 folds created)
				int testDataSize = (vars+1)*sampleSize/foldSize;
				Double[] testingData = new Double[testDataSize];
				if (testingData == null) throw new RuntimeException ("Null testing data\n");

				index = Integer.valueOf(sline[0]);

				int foldLength = testDataSize;
                // Bucket all the testing data set into an array
				for (int i=1; i<sampleSize*(vars+1)+1; i++) {
					if ((i-1)/foldLength == index) {
						testingData[testingDataIndex++] = Double.valueOf(sline[i]);
					} else {
						trainingData[trainingDataIndex++] = Double.valueOf(sline[i]);
					}
				}
				//Function to fit the Regression model
				OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();		

				try {
				    ols.newSampleData(ArrayUtils.toPrimitive(trainingData), trainingDataSize/(vars+1), vars); // 3
				}
				catch(IllegalArgumentException e) {
				    System.out.print("Can't sample data: ");
				    e.printStackTrace();
				    return;
				}
				 // Extract the regression coefficients from the model created
				double[] coe = null;
				try {
				    coe = ols.estimateRegressionParameters(); 
				}
				catch(IllegalArgumentException e) { 
				    System.out.print("Can't estimate parameters: ");
				    e.printStackTrace();
				    return;
				}
                //Compute RMSE
				double mse = returnMeanSquareError(coe, testingData, vars);
				contex.write(new IntWritable(index), new Text(String.format("%f",Math.sqrt(mse))));
			 } catch (Exception e) {
				throw new RuntimeException(String.format("Exception in index: %d, trainingDataIndex: %d, testingDataIndex: %d\n", index, trainingDataIndex, testingDataIndex), e);
			}
			}
	}
//Function to predict the testing dataset and calculate RMSE. Calculate the predicted value using the regression coefficients extracted
	private static double returnMeanSquareError(double[] coef, Double[] testingData, int featureSize) {
		double sumErrorSquare = 0;
		for (int i=0; i<testingData.length; i++) {
			double y = testingData[i];
			double predict = coef[0];
			for (int k=1; k<=featureSize; k++) {
				predict += coef[k]*testingData[i+k];
			}
			i+=2;
			sumErrorSquare += (predict-y)*(predict-y);
		}	
		return sumErrorSquare/(testingData.length/(featureSize+1));
	}

	private static void dumpEstimation(double[] coe) {
	    if(coe == null)
		return;
	 
	    for(double d : coe)
		System.out.print(d + " ");
	    System.out.println();
	}
	
//Reducer will collect the RMSE from each fold and emit them
	public static class SumReducer extends Reducer<IntWritable, Text, IntWritable, Text> {

		@Override
			public void reduce(IntWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
				Iterator<Text> val = values.iterator();
				Iterator<Text> it=values.iterator();
				String output = "";
				while (it.hasNext()) {
					output += it.next().toString();
					if (it.hasNext()) output += ", ";
				}
				//if (true) {
				//throw new RuntimeException(String.format("Values received: %s\n", output));
//}
				context.write(key, new Text(output));
				
			}
	}
}
