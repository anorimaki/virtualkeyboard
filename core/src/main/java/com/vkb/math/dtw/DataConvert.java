package com.vkb.math.dtw;

import java.util.Arrays;
import java.util.List;

import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.vkb.math.DiscreteFunction;
import com.vkb.model.FunctionFeatureData;

// Use com.vkb.math.dtw.FunctionWarper
@Deprecated
public class DataConvert {

	public static TimeSeries getTimeSeries( FunctionFeatureData... functions ) throws Exception {
		return getTimeSeries( Arrays.asList( functions) );
	}
	
	public static TimeSeries getTimeSeries( List<FunctionFeatureData> functions ) throws Exception {
		TimeSeries ret = new TimeSeries( functions.size() );
		
		DiscreteFunction sample0 = functions.get(0).getSamples();
		
		for( int i=0; i<sample0.size(); ++i ) {
			DiscreteFunction.Point p0 = sample0.get( i );
			double time = p0.getX();
			double[] values = getTimeSeriesPoint( functions, i, time );
					
			ret.addLast( time, new TimeSeriesPoint( values ) );
		}
		
		return ret;
	}
	
	
	private static double[] getTimeSeriesPoint( List<FunctionFeatureData> functions, 
												int index, double time ) throws Exception {
		double[] values = new double[functions.size()];
		for( int j = 0; j<functions.size(); ++j ) {
			DiscreteFunction.Point otherP = functions.get(j).getSamples().get(index);
			if ( time != otherP.getX() )
				throw new InvalidDataException( "Time series must be sampled in same intervals" );
			values[j] = otherP.getY();
		}
		
		return values;
	}
}
