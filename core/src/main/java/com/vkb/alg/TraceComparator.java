package com.vkb.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastdtw.dtw.DTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.EuclideanDistance;
import com.vkb.Feature;
import com.vkb.FeatureFunction;
import com.vkb.Trace;

public class TraceComparator {
	private static final Feature[] comparableFeatures = { 
		Feature.POSITION_X, Feature.POSITION_Y,
		Feature.VELOCITY_X, Feature.VELOCITY_Y,
		Feature.ACCELERATION_X, Feature.ACCELERATION_Y
	};
	
	static public class Result {
		private TimeWarpInfo globalResult;
		private Map<Feature, TimeWarpInfo> partialResults;
		
		Result( TimeWarpInfo globalResult, Map<Feature, TimeWarpInfo> partialResults ) {
			this.partialResults = partialResults;
			this.globalResult = globalResult;
		}
		
		public TimeWarpInfo getGlobalResult() {
			return globalResult;
		}
		
		public Map<Feature, TimeWarpInfo> getPartialResults() {
			return partialResults;
		}
		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for( Map.Entry<Feature, TimeWarpInfo> entry : partialResults.entrySet() ) {
				ret.append( "(" + entry.getKey() + "=" + entry.getValue().getDistance() + ") " );
			}
			return ret.toString();
		}
	}
	
	public Result compare( Trace t1, Trace t2 ) throws Exception {
		Map<Feature, TimeWarpInfo> partialResults = new HashMap<Feature, TimeWarpInfo>();
		
		List<FeatureFunction> t1Features = new ArrayList<FeatureFunction>();
		List<FeatureFunction> t2Features = new ArrayList<FeatureFunction>();
		
		for( Feature feature : comparableFeatures ) {
			FeatureFunction f1 = t1.getFeature(feature);
			FeatureFunction f2 = t2.getFeature(feature);
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
	
	
	private TimeWarpInfo compare( FeatureFunction f1, FeatureFunction f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
	}
}
