package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.FunctionFeatureData;

public class FunctionFeatureDeterminer {
	private static final double DEFAULT_THRESHOLD = 1.0d;
	private double threshold = DEFAULT_THRESHOLD;
	private List<FunctionFeatureData> patternSamples;
	private DescriptiveStatistics patternStatistics;
	private FunctionFeatureComparator comparator;
	
	public FunctionFeatureDeterminer( List<FunctionFeatureData> patternSamples ) throws Exception {
		this.patternSamples = patternSamples;
		this.comparator = new FunctionFeatureComparator();
		this.patternStatistics = calculateFunctionsStatistics( patternSamples );
	}
	
	public void setThreshold( double threshold ) {
		this.threshold = threshold;		//alpha factor
	}
	
	public boolean check( FunctionFeatureData featureData ) throws Exception {
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		for( FunctionFeatureData patternData : patternSamples ) {
			double distance = comparator.distance( patternData, featureData );
			statistics.addValue( distance );
		}
		
		return statistics.getMean() < (threshold * patternStatistics.getMean());
	}
	
	private DescriptiveStatistics calculateFunctionsStatistics( List<FunctionFeatureData> datas ) throws Exception {
		DescriptiveStatistics ret = new DescriptiveStatistics();
		
		for ( int i=0; i<datas.size(); i++ ){
			for ( int j=i+1; i<datas.size(); i++ ){
				double distance = comparator.distance( datas.get(i), datas.get(j) );
				ret.addValue( distance );
			}
		}
		return ret;
	}
}
