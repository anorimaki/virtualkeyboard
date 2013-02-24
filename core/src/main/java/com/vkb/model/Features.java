package com.vkb.model;

import java.util.HashMap;
import java.util.Map;


public class Features {
	private Map<FeatureType, FunctionFeature> features =
				new HashMap<FeatureType, FunctionFeature>();
	
	public void put( FunctionFeature feature ) {
		features.put( feature.getType(), feature );
	}
	
	public FunctionFeature get( FeatureType feature ) {
		return features.get( feature );
	}
}
