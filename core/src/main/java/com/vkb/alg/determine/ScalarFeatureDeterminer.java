package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.ScalarFeatureData;

public class ScalarFeatureDeterminer {
	private static final double DEFAULT_THRESHOLD = 1.96d;
	private double threshold = DEFAULT_THRESHOLD;
	private DescriptiveStatistics patternStatistics;
	
	public ScalarFeatureDeterminer( List<ScalarFeatureData> patternSamples ) throws Exception {
		this.patternStatistics = calculateStatistics( patternSamples );
	}
	
	public void setThreshold( double threshold ) {
		this.threshold = threshold;		//alpha factor
	}
	
	public boolean check( ScalarFeatureData featureData ) throws Exception {
		double zscore = calculateZScore( featureData.getValue() );
		return zscore < threshold;
	}
	
	private double calculateZScore( double value ){
		double diff = Math.abs( value-patternStatistics.getMean() );
		if ( diff==0 ) {
			return 0.0d;
		}
		
		double stdev = patternStatistics.getStandardDeviation();
		if ( stdev==0 ) {
			return Double.MAX_VALUE;
		}
		
		return diff / stdev;
	}
	
	private static DescriptiveStatistics calculateStatistics( List<ScalarFeatureData> datas ) throws Exception {
		DescriptiveStatistics ret = new DescriptiveStatistics();
		for( ScalarFeatureData data : datas ) {
			ret.addValue( data.getValue() );
		}
		return ret;
	}
}
