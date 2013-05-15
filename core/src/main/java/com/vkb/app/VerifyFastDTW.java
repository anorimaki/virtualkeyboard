package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.io.CapturedDatasParser;
import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.dtw.EuclideanCellCostFunction;
import com.vkb.math.dtw.FastDTW;
import com.vkb.math.dtw.FunctionComparator;
import com.vkb.math.dtw.OriginalDTW;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;



public class VerifyFastDTW {
	private static final File INPUT_FOLDER = new File( Environment.RESOURCES_DIR, "user1/pattern" );
		
	private File inputFolder;
	

	public VerifyFastDTW( File inputFolder ) {
		this.inputFolder = inputFolder;
	}
	

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		List<CapturedData> inputData = inputDataParser.parse(inputFolder);
		
		GenericSignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		List<Signature> signatures = signatureBuilder.buildSignatures(inputData);
		
		List<FunctionFeatureData> featureDatas = Signatures.extractFeatureData(signatures, FeatureId.VELOCITY_Y);
		List<DiscreteVectorFunction> functions = getFunctions(featureDatas);
		
		FunctionComparator originalDTW = new OriginalDTW( new EuclideanCellCostFunction() );

		List<Double> originalDistances = new ArrayList<Double>();
		long originalTime = calculate( originalDTW, functions, originalDistances );
		
		System.out.println( "- Original distances: " + originalDistances );
		System.out.println( "- Time consumed: " + originalTime + "ms." );
		
		for( int searchRadious=0; searchRadious<5; ++searchRadious ) {
			FunctionComparator fastDTW = new FastDTW( new EuclideanCellCostFunction(), searchRadious );
			
			List<Double> distances = new ArrayList<Double>();
			long time = calculate( fastDTW, functions, distances );
			
			ErrorResult error = calculateError( originalDistances, distances );
					
			System.out.println( "* Search radious: " + searchRadious );
			System.out.println( "       Distances: " + distances );
			System.out.println( "       Time consumed: " + time + "ms." );
			System.out.println( "       Error rate: " + error.getAbsoluteErrorRate()  +  "%" );
			System.out.println( "       Relative error rate: " + error.getRelativeErrorRate()  +  "%" );
			
			double gain = (double)(originalTime-time) / (double)originalTime;
			System.out.println( "       Performance gain: " + 100.0d * gain +  "%" );
			System.out.println();
		}
	}
	
	
	private static class ErrorResult {
		private double absoluteError;
		private double relativeError;
		
		public ErrorResult( double absoluteError, double relativeError ) {
			this.absoluteError = absoluteError;
			this.relativeError = relativeError;
		}
		
		public double getAbsoluteErrorRate() {
			return absoluteError;
		}
		
		public double getRelativeErrorRate() {
			return relativeError;
		} 
	}
	
	private ErrorResult calculateError( List<Double> originalDistances, List<Double> distances ) {
		double sumOriginal = 0;
		double sumEvaluated = 0;
		for( int i=0; i<originalDistances.size(); ++i ) {
			sumOriginal += originalDistances.get(i);
			sumEvaluated += distances.get(i);
		}
		double relativeFactor = sumEvaluated / sumOriginal;
		
		double absoluteErrorRate = 0;
		double relativeErrorRate = 0; 
		for( int i=0; i<originalDistances.size(); ++i ) {
			double originalDistance = originalDistances.get(i);
			double evaluatedDistance = distances.get(i);
			
			double diff = Math.abs(originalDistance - evaluatedDistance);
			absoluteErrorRate += (diff / originalDistance);
			
			double relativizedOriginalDistance = originalDistance * relativeFactor;
			diff = Math.abs( relativizedOriginalDistance - evaluatedDistance );
			relativeErrorRate += (diff / relativizedOriginalDistance);
		}
		absoluteErrorRate = absoluteErrorRate / originalDistances.size();
		relativeErrorRate = relativeErrorRate / originalDistances.size();
		
		return new ErrorResult( absoluteErrorRate * 100.0d, relativeErrorRate * 100.0d );
	}


	private List<DiscreteVectorFunction> getFunctions( List<FunctionFeatureData> featureDatas ) throws Exception {
		List<DiscreteVectorFunction> ret = new ArrayList<DiscreteVectorFunction>();
		for( FunctionFeatureData featureData : featureDatas ) {
			ret.add( new DiscreteVectorFunction(featureData.getSamples()) );
		}
		return ret;
	}
	

	private long calculate( FunctionComparator comparator,
						List<DiscreteVectorFunction> functions, List<Double> distances ) throws Exception {
		long start = System.currentTimeMillis();
		for ( int i=0; i<functions.size(); ++i ) {
			for ( int j=i+1; j<functions.size(); ++j ) {
				double distance = comparator.distance( functions.get(i), functions.get(j) );
				distances.add(distance);
			}
		}
		return System.currentTimeMillis() - start;
	}

	
	public static void main(String[] args) {
		try {
			VerifyFastDTW app = new VerifyFastDTW( INPUT_FOLDER );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
