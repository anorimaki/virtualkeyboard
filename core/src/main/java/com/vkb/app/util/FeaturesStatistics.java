package com.vkb.app.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fastdtw.dtw.TimeWarpInfo;
import com.vkb.model.FeatureId;

public class FeaturesStatistics {
	private Map<FeatureId, SummaryStatistics> values = new HashMap<FeatureId, SummaryStatistics>();
	
	public FeaturesStatistics() {
	}
	
	public FeaturesStatistics( SignatureComparator.Result[] results ) {
		add( results );
	}
	
	public void add( SignatureComparator.Result[] results  ) {
		for ( SignatureComparator.Result result : results ) {
			if ( result != null ) {
				addResult( result );
			}
		}
	}
	
	public Map<FeatureId, ? extends StatisticalSummary> getValues() {
		return values;
	}

	private void addResult( SignatureComparator.Result result ) {
		for( Map.Entry<FeatureId, TimeWarpInfo> featureResult : result.getPartialResults().entrySet() ) {
			SummaryStatistics summary = values.get( featureResult.getKey() );
			if ( summary == null ) {
				summary = new SummaryStatistics();
				values.put( featureResult.getKey(), summary );
			}
			summary.addValue( featureResult.getValue().getDistance() );
		}
	}
}
