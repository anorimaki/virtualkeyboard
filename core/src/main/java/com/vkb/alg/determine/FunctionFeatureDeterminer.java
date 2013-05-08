package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class FunctionFeatureDeterminer {
	private double threshold;
	private List<FunctionFeatureData> patternSamples;
	private DescriptiveStatistics patternStatistics;
	private FunctionFeatureComparator comparator;
	
	public FunctionFeatureDeterminer( List<FunctionFeatureData> patternSamples ) throws Exception {
		// No ho veig clar...
		this.patternSamples = patternSamples;
		this.patternStatistics = calculateFunctionsStatistics( patternSamples );
	}
	

	public void setThreshold( double threshold ) {
		this.threshold = threshold;		//alpha factor
	}
	
	public void setPatternStatistics(DescriptiveStatistics patternStatistics){
		this.patternStatistics = patternStatistics;
	}
	
	public boolean check( FunctionFeatureData featureData ) throws Exception {
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		
		for( FunctionFeatureData patternData : patternSamples ) {
			double distance = comparator.distance( patternData, featureData );
			statistics.addValue( distance );
		}
		
		return statistics.getMean() < (threshold * patternStatistics.getMean());
	}
	
	
	public DescriptiveStatistics calculateFunctionsStatistics( List<FunctionFeatureData> datas ) throws Exception {
		FunctionFeatureComparator comparator = new FunctionFeatureComparator();
		
		DescriptiveStatistics ret = new DescriptiveStatistics();
		
		for ( int i=0; i<datas.size(); i++ ){
			for ( int j=i+1; i<datas.size(); i++ ){
				double distance = comparator.distance( datas.get(i), datas.get(j) );
				ret.addValue( distance );
			}
		}
		return ret;
	}
	
	public StatisticalSummary getStatisticalSummary(){
		return this.patternStatistics;
	}

}
	