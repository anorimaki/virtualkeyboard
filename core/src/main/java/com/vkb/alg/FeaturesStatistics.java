package com.vkb.alg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fastdtw.dtw.TimeWarpInfo;
import com.vkb.Feature;
import com.vkb.alg.TraceComparator.Result;

public class FeaturesStatistics {
	private Map<Feature, SummaryStatistics> values = new HashMap<Feature, SummaryStatistics>();
	
	public FeaturesStatistics() {
	}
	
	public FeaturesStatistics( TraceComparator.Result[] results ) {
		add( results );
	}
	
	public void add( TraceComparator.Result[] results  ) {
		for ( TraceComparator.Result result : results ) {
			if ( result != null ) {
				addResult( result );
			}
		}
	}
	
	public Map<Feature, ? extends StatisticalSummary> getValues() {
		return values;
	}

	private void addResult( Result result ) {
		for( Map.Entry<Feature, TimeWarpInfo> featureResult : result.getPartialResults().entrySet() ) {
			SummaryStatistics summary = values.get( featureResult.getKey() );
			if ( summary == null ) {
				summary = new SummaryStatistics();
				values.put( featureResult.getKey(), summary );
			}
			summary.addValue( featureResult.getValue().getDistance() );
		}
	}
}
