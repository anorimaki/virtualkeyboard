package com.vkb.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastdtw.dtw.DTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.EuclideanDistance;
import com.vkb.math.dtw.DataConvert;
import com.vkb.model.FunctionFeature;
import com.vkb.model.FeatureType;
import com.vkb.model.Signature;

public class SignatureComparator {
	private static final FeatureType[] comparableFeatures = { 
		FeatureType.POSITION_X, FeatureType.POSITION_Y,
		FeatureType.VELOCITY_X, FeatureType.VELOCITY_Y,
		FeatureType.ACCELERATION_X, FeatureType.ACCELERATION_Y
	};
	
	static public class Result {
		private TimeWarpInfo globalResult;
		private Map<FeatureType, TimeWarpInfo> partialResults;
		
		Result( TimeWarpInfo globalResult, Map<FeatureType, TimeWarpInfo> partialResults ) {
			this.partialResults = partialResults;
			this.globalResult = globalResult;
		}
		
		public TimeWarpInfo getGlobalResult() {
			return globalResult;
		}
		
		public Map<FeatureType, TimeWarpInfo> getPartialResults() {
			return partialResults;
		}
		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for( Map.Entry<FeatureType, TimeWarpInfo> entry : partialResults.entrySet() ) {
				ret.append( "(" + entry.getKey() + "=" + entry.getValue().getDistance() + ") " );
			}
			return ret.toString();
		}
	}
	
	public Result compare( Signature t1, Signature t2 ) throws Exception {
		Map<FeatureType, TimeWarpInfo> partialResults = new HashMap<FeatureType, TimeWarpInfo>();
		
		List<FunctionFeature> t1Features = new ArrayList<FunctionFeature>();
		List<FunctionFeature> t2Features = new ArrayList<FunctionFeature>();
		
		for( FeatureType feature : comparableFeatures ) {
			FunctionFeature f1 = t1.getFeature(feature);
			FunctionFeature f2 = t2.getFeature(feature);
			TimeWarpInfo result = compare( f1, f2 );
			partialResults.put( feature, result );
			
			t1Features.add( f1 );
			t2Features.add( f2 );
		}
		
		TimeSeries ts1 = DataConvert.getTimeSeries( t1Features );
		TimeSeries ts2 = DataConvert.getTimeSeries( t2Features );
		TimeWarpInfo globalResult =  DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
		
		return new Result( globalResult, partialResults );
	}
	
	
	private TimeWarpInfo compare( FunctionFeature f1, FunctionFeature f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
	}
}
