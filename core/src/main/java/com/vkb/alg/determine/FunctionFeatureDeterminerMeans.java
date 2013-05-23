package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.math.Statistics;
import com.vkb.math.dtw.DefaultFunctionFeatureComparator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.FunctionFeatureData;

public class FunctionFeatureDeterminerMeans implements FunctionFeatureDeterminer{
	private static final double DEFAULT_THRESHOLD = 1.0d;
	private double threshold = DEFAULT_THRESHOLD;
	private List<FunctionFeatureData> patternSamples;
	private StatisticalSummary patternStatistics;

	private FunctionFeatureComparator comparator;
	
		
	public FunctionFeatureDeterminerMeans( List<FunctionFeatureData> patternSamples ) throws Exception {
		this( patternSamples, new DefaultFunctionFeatureComparator() );
	}
	
	public FunctionFeatureDeterminerMeans( List<FunctionFeatureData> patternSamples,
								FunctionFeatureComparator functionFeatureComparator ) throws Exception {
		this.patternSamples = patternSamples;
		this.comparator = functionFeatureComparator;
		this.patternStatistics = calculateFunctionsStatistics( patternSamples );
		
	}
	
	public void setFunctionComparator( FunctionFeatureComparator comparator ) {
		this.comparator = comparator;
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
	
	private StatisticalSummary calculateFunctionsStatistics( List<FunctionFeatureData> datas ) throws Exception {
		DescriptiveStatistics ret = new DescriptiveStatistics();											
		for ( int i=0; i<datas.size(); i++ ){
			for ( int j=i+1; j<datas.size(); j++ ){
				double distance = comparator.distance( datas.get(i), datas.get(j) );
				ret.addValue( distance );
			}
		}
		return Statistics.resume( ret );
	}
	
}