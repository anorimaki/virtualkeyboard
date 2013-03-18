package com.vkb.app.util;

import java.util.HashMap;
import java.util.Map;

import com.vkb.math.dtw.FunctionFeatureWarper;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;
import com.vkb.model.Signature;

public class FunctionFeatureComparator {
	private static final FeatureId[] comparableFeatures = { 
		FeatureId.POSITION_X, FeatureId.POSITION_Y,
		FeatureId.VELOCITY_X, FeatureId.VELOCITY_Y,
		FeatureId.ACCELERATION_X, FeatureId.ACCELERATION_Y
	};
	
	static public class Result {
		private double globalResult;
		private Map<FeatureId, Double> partialResults;
		
		Result( Double globalResult, Map<FeatureId, Double> partialResults ) {
			this.partialResults = partialResults;
			this.globalResult = globalResult;
		}
		
		public double getGlobalResult() {
			return globalResult;
		}
		
		public Map<FeatureId, Double> getPartialResults() {
			return partialResults;
		}
		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for( Map.Entry<FeatureId, Double> entry : partialResults.entrySet() ) {
				ret.append( "(" + entry.getKey() + "=" + entry.getValue() + ") " );
			}
			return ret.toString();
		}
	}
	
	private FunctionFeatureWarper warper;
	
	public FunctionFeatureComparator() {
		warper = new FunctionFeatureWarper();
	}
	
	public Result compare( Signature t1, Signature t2 ) throws Exception {
		Map<FeatureId, Double> partialResults = new HashMap<FeatureId, Double>();
		
		FunctionFeatureDatas t1Features = new FunctionFeatureDatas();
		FunctionFeatureDatas t2Features = new FunctionFeatureDatas();
		
		for( FeatureId feature : comparableFeatures ) {
			FunctionFeatureData f1 = t1.getFeature( feature ).getData();
			FunctionFeatureData f2 = t2.getFeature( feature ).getData();
			double result = warper.align( f1, f2 ).getDistance();
			partialResults.put( feature, result );
			
			t1Features.add( f1 );
			t2Features.add( f2 );
		}
		
		double globalResult =  warper.align( t1Features, t2Features ).getDistance();
		
		return new Result( globalResult, partialResults );
	}
}
