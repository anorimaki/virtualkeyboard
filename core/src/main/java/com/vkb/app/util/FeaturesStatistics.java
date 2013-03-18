package com.vkb.app.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.vkb.model.FeatureId;

public class FeaturesStatistics {
	private Map<FeatureId, SummaryStatistics> values = new HashMap<FeatureId, SummaryStatistics>();
	
	public FeaturesStatistics() {
	}
	
	public FeaturesStatistics( FunctionFeatureComparator.Result[] results ) {
		add( results );
	}
	
	public void add( FunctionFeatureComparator.Result[] results  ) {
		for ( FunctionFeatureComparator.Result result : results ) {
			if ( result != null ) {
				addResult( result );
			}
		}
	}
	
	public Map<FeatureId, ? extends StatisticalSummary> getValues() {
		return values;
	}

	private void addResult( FunctionFeatureComparator.Result result ) {
		for( Map.Entry<FeatureId, Double> featureResult : result.getPartialResults().entrySet() ) {
			SummaryStatistics summary = values.get( featureResult.getKey() );
			if ( summary == null ) {
				summary = new SummaryStatistics();
				values.put( featureResult.getKey(), summary );
			}
			summary.addValue( featureResult.getValue() );
		}
	}
}
