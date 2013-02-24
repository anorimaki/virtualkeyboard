package com.vkb;

import java.util.HashMap;
import java.util.Map;

public class Features {
	private Map<Feature, FeatureFunction> features =
				new HashMap<Feature, FeatureFunction>();
	
	public void put( FeatureFunction feature ) {
		features.put( feature.getFeature(), feature );
	}
	
	public FeatureFunction get( Feature feature ) {
		return features.get( feature );
	}
}
