package com.vkb.app.quality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.Feature;
import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;

public class ScalarFeaturesStatistics{
	private Map<FeatureId,DescriptiveStatistics> statistics= new HashMap<FeatureId, DescriptiveStatistics>();
	
	public ScalarFeaturesStatistics( List<Signature> signatures ){
		for ( Signature signature : signatures ) {
			Set<Feature> scalarFeatures = 
					signature.getFeatures().getAllByModel( ScalarFeatureData.class );
			
			for( Feature scalarFeature : scalarFeatures ) {
				add( scalarFeature );
			}
		}
	}	
	
	private void add(Feature scalarFeature) {
		DescriptiveStatistics stats = statistics.get( scalarFeature.getId() );
		if ( stats == null ) {
			stats = new DescriptiveStatistics();
			statistics.put( scalarFeature.getId(), stats );
		}
		stats.addValue( scalarFeature.<ScalarFeatureData>getData().getValue() );
	}
	
	public DescriptiveStatistics get( FeatureId id ){
		return statistics.get(id);
	}
	
	@Override
	public String toString(){
		return statistics.toString();
	}
}
