package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.fastdtw.dtw.DTW;
import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.EuclideanDistance;
import com.vkb.alg.SignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.app.util.SignaturesComparators;
import com.vkb.io.CapturedDatasParser;
import com.vkb.math.dtw.DataConvert;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.alg.determine.OutlierFeatureDeterminer;



public class TemporalFeatureCompareTry {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" )};
	private static final File CHECK_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "CapturaUser1" )};
		
	private File[] inputFolders;
	private File[] checkFolders;
	
	private static final FeatureId[] temporalFeatures = { 
		FeatureId.POSITION_X, FeatureId.POSITION_Y,
		FeatureId.VELOCITY_X, FeatureId.VELOCITY_Y,
		FeatureId.ACCELERATION_X, FeatureId.ACCELERATION_Y, FeatureId.RELATION_X_Y
	};
	
	public TemporalFeatureCompareTry( File[] inputFolders, File[] checkFolders ) {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
	}

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		List<List<Signature>> signaturesGroups = new ArrayList<List<Signature>>();
		SignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		for ( File inputFolder : inputFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(inputFolder);
			signaturesGroups.add( signatureBuilder.build(inputData) );
		}
		
		// Suposem que sols analitzem un directori amb signatures
		List<Signature> patterns = signaturesGroups.get(0);
		
		List<List<Signature>> signaturesCheckGroups = new ArrayList<List<Signature>>();
		for ( File checkFolder : checkFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(checkFolder);
			signaturesCheckGroups.add( signatureBuilder.build(inputData) );
		}
		
		// Suposem una unica signature al directori de checking (DANGER!)
		Signature forChecking = signaturesCheckGroups.get(0).get(0);
		
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		for(int i=0;i<patterns.size();i++){
				makeDTW(patterns.get(i),forChecking);
		}
		time_end = System.currentTimeMillis();
		
		long time_startF, time_endF;
		time_startF = System.currentTimeMillis();
		for(int i=0;i<patterns.size();i++){
			makeDTWFast(patterns.get(i),forChecking);
	    }
		time_endF = System.currentTimeMillis();

		System.out.println("Patterns: "+patterns.size()+" <- checked: "+signaturesCheckGroups.get(0).size());
		System.out.println("Time StandardDTW: "+(time_end-time_start)+"\nTimeFastDTW: "+(time_endF-time_startF));
	}
	
	
	public double makeDTW(Signature t1, Signature t2) throws Exception{
		double ret = 0.0;
		Map<FeatureId, TimeWarpInfo> partialResults = new HashMap<FeatureId, TimeWarpInfo>();
		
		List<FunctionFeatureData> t1Features = new ArrayList<FunctionFeatureData>();
		List<FunctionFeatureData> t2Features = new ArrayList<FunctionFeatureData>();
		
		for( FeatureId feature : temporalFeatures ) {
			FunctionFeatureData f1 = t1.getFeature( feature ).getData();
			FunctionFeatureData f2 = t2.getFeature( feature ).getData();
			TimeWarpInfo result = compare( f1, f2 );
			partialResults.put( feature, result );
			
			System.out.println("Feature "+feature.toString()+": "+result.getDistance());
			
			t1Features.add( f1 );
			t2Features.add( f2 );
		}
		
				
		TimeSeries ts1 = DataConvert.getTimeSeries( t1Features );
		TimeSeries ts2 = DataConvert.getTimeSeries( t2Features );
		TimeWarpInfo globalResult =  DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
		
		System.out.println("Global: "+globalResult.getDistance()+"\n");
		
		return ret;
	}
	
	public double makeDTWFast(Signature t1, Signature t2) throws Exception{
		double ret = 0.0;
		Map<FeatureId, Double> partialResults = new HashMap<FeatureId, Double>();
		
		List<FunctionFeatureData> t1Features = new ArrayList<FunctionFeatureData>();
		List<FunctionFeatureData> t2Features = new ArrayList<FunctionFeatureData>();
		
		for( FeatureId feature : temporalFeatures ) {
			FunctionFeatureData f1 = t1.getFeature( feature ).getData();
			FunctionFeatureData f2 = t2.getFeature( feature ).getData();
			double result = compareFast( f1, f2 );
			partialResults.put( feature, new Double(result));
			
			System.out.println("Feature (Fast) "+feature.toString()+": "+result);
			
			t1Features.add( f1 );
			t2Features.add( f2 );
		}
		
				
		TimeSeries ts1 = DataConvert.getTimeSeries( t1Features );
		TimeSeries ts2 = DataConvert.getTimeSeries( t2Features );
		double globalResult =  FastDTW.getWarpDistBetween(ts1, ts2, new EuclideanDistance());
		
		System.out.println("Global (Fast): "+globalResult+"\n");
		
		return ret;
	}
	
	private TimeWarpInfo compare( FunctionFeatureData f1, FunctionFeatureData f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
	}
	
	private double compareFast( FunctionFeatureData f1, FunctionFeatureData f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return FastDTW.getWarpDistBetween(ts1,ts2,new EuclideanDistance());
	}
	
	
	
	
	public static void main(String[] args) {
		try {
			TemporalFeatureCompareTry prueba = new TemporalFeatureCompareTry( INPUT_FOLDERS, CHECK_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
