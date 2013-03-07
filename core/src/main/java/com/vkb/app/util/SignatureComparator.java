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
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class SignatureComparator {
	private static final FeatureId[] comparableFeatures = { 
		FeatureId.POSITION_X, FeatureId.POSITION_Y,
		FeatureId.VELOCITY_X, FeatureId.VELOCITY_Y,
		FeatureId.ACCELERATION_X, FeatureId.ACCELERATION_Y
	};
	
	static public class Result {
		private TimeWarpInfo globalResult;
		private Map<FeatureId, TimeWarpInfo> partialResults;
		
		Result( TimeWarpInfo globalResult, Map<FeatureId, TimeWarpInfo> partialResults ) {
			this.partialResults = partialResults;
			this.globalResult = globalResult;
		}
		
		public TimeWarpInfo getGlobalResult() {
			return globalResult;
		}
		
		public Map<FeatureId, TimeWarpInfo> getPartialResults() {
			return partialResults;
		}
		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for( Map.Entry<FeatureId, TimeWarpInfo> entry : partialResults.entrySet() ) {
				ret.append( "(" + entry.getKey() + "=" + entry.getValue().getDistance() + ") " );
			}
			return ret.toString();
		}
	}
	
	public Result compare( Signature t1, Signature t2 ) throws Exception {
		Map<FeatureId, TimeWarpInfo> partialResults = new HashMap<FeatureId, TimeWarpInfo>();
		
		List<FunctionFeatureData> t1Features = new ArrayList<FunctionFeatureData>();
		List<FunctionFeatureData> t2Features = new ArrayList<FunctionFeatureData>();
		
		for( FeatureId feature : comparableFeatures ) {
			FunctionFeatureData f1 = t1.getFeature( feature ).getData();
			FunctionFeatureData f2 = t2.getFeature( feature ).getData();
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
	
	
	private TimeWarpInfo compare( FunctionFeatureData f1, FunctionFeatureData f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return DTW.getWarpInfoBetween(ts1, ts2, new EuclideanDistance());
	}
}
