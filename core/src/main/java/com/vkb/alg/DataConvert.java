package com.vkb.alg;

import java.util.Arrays;
import java.util.List;

import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.vkb.FeatureFunction;
import com.vkb.FunctionPoints;
import com.vkb.Point;

public class DataConvert {

	public static TimeSeries getTimeSeries( FeatureFunction... functions ) throws Exception {
		return getTimeSeries( Arrays.asList( functions) );
	}
	
	public static TimeSeries getTimeSeries( List<FeatureFunction> functions ) throws Exception {
		TimeSeries ret = new TimeSeries( functions.size() );
		
		FunctionPoints sample0 = functions.get(0).getSamples();
		
		for( int i=0; i<sample0.size(); ++i ) {
			Point p = sample0.get(i);
			
			double time = p.getX();
			double[] values = new double[functions.size()];
			values[0] = p.getY();
			
			for( int j = 1; j<functions.size(); ++j ) {
				Point otherP = functions.get(j).getSamples().get(i);
				if ( time != otherP.getX() )
					throw new InvalidDataException( "Time series must be sampled in same intervals" );
				values[j] = otherP.getY();
			}
					
			ret.addLast( time, new TimeSeriesPoint( values ) );
		}
		
		return ret;
	}
}
