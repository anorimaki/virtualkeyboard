package com.vkb.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Features {
	private Map<FeatureId, Feature> idIndex;
	private Map<Class<? extends FeatureData>, Set<Feature>> modelTndex;
	
	public Features() {
		idIndex = new HashMap<FeatureId, Feature>();
		modelTndex = new HashMap<Class<? extends FeatureData>, Set<Feature>>();
	}
	
	public void put( Feature feature ) {
		idIndex.put( feature.getId(), feature );
		
		Class<? extends FeatureData> modelType = feature.getId().getModel();
		Set<Feature> typeGroup = modelTndex.get( modelType );
		if ( typeGroup == null ) {
			typeGroup = new HashSet<Feature>();
			modelTndex.put( modelType, typeGroup);
		}
		typeGroup.add( feature );
	}
	
	public Feature get( FeatureId feature ) {
		return idIndex.get( feature );
	}
	
	public <T extends FeatureData> Set<Feature> getAllByModel( Class<T> t ) {
		return modelTndex.get( t );
	}
	
	public <T extends FeatureData> Set<T> getAllDatasByModel( Class<T> t ) {
		Set<Feature> features = getAllByModel( t );
		Set<T> ret = new HashSet<T>();
		for( Feature feature : features ) {
			ret.add( feature.<T>getData() );
		}
		return ret;
	}
}
