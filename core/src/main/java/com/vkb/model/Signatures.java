package com.vkb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Signatures {
	static public <T extends FeatureData> Map<FeatureId, List<T>> extractFeatureDatasByModel( 
			List<Signature> signatures, Class<T> t ) throws Exception {
		Signature signature0 = signatures.get(0);
		
		Map<FeatureId, List<T>> ret = new HashMap<FeatureId, List<T>>();
		Set<Feature> signature0Features = signature0.getFeatures().getAllByModel( t );
		for( Feature feature : signature0Features ) {
			T data = feature.getData();
			
			List<T> list = new ArrayList<T>();
			list.add( data );
			ret.put( feature.getId(), list );
		}
		
		for ( int i=1; i<signatures.size(); ++i ) {
			Signature signature = signatures.get(i);
			Set<Feature> signatureFeatures = signature.getFeatures().getAllByModel( t );
			
			if ( signatureFeatures.size() != signature0Features.size() ) {
				throw new Exception( "Error generating pattern. Signatures have different features" );
			}
			
			for( Feature feature : signatureFeatures ) {
				T data = feature.getData();
				
				List<T> list = ret.get( feature.getId() );
				if ( list==null ) {
					throw new Exception( "Error generating pattern. Signatures have different features" );
				}
				list.add( data );
			}
		}
		
		return ret;
	}
	
	static public <T extends FeatureData> List<T> extractFeatureData( 
			List<Signature> signatures, FeatureId feature ) throws Exception {
		List<T> ret = new ArrayList<T>();
		for( Signature signature : signatures ) {
			T data = signature.getFeature(feature).getData();
			if ( data == null ) {
				throw new Exception( "Missign feature: " + feature );
			}
			ret.add( data );
		}
		return ret;
	}
}
