package com.vkb.math.dtw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;

public class PreCalculatedFunctionFeatureComparator implements FunctionFeatureComparator {
	private static class Key<T> {
		private T first;
		private T second;
		
		public Key( T feature1, T feature2 ) {
			this.first = feature1;
			this.second = feature2;
		}

		@Override
		public int hashCode() {
			int hashFirst = first != null ? first.hashCode() : 0;
	    	int hashSecond = second != null ? second.hashCode() : 0;
	    	return (hashFirst + hashSecond) * 13;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;
			if ( getClass() != obj.getClass() )
				return false;
			Key<?> other = (Key<?>) obj;
			if ( first == null ) {
				if ( second == null ) {
					return (other.first == null) && (other.second==null);
				}
				if ( other.first == null ) {
					return second.equals( other.second );
				}
				return (other.second==null) && second.equals( other.first );
			} 
			if ( second == null ) {
				if ( other.first == null ) {
					return first.equals( other.second );
				}
				return (other.second==null) && first.equals( other.first );
			}
			return (first.equals(other.first) && second.equals(other.second)) ||
					(first.equals(other.second) && second.equals(other.first));
		}
		
		public static <K> Key<K> create( K f1, K f2 ) {
			return new Key<K>( f1, f2 );
		}
	}
	
	private Map<Key<FunctionFeatureDatas>, Double> functionFeatureDatasValues;
	private Map<Key<FunctionFeatureData>, Double> functionFeatureDataValues;
	
	public PreCalculatedFunctionFeatureComparator() {
		functionFeatureDatasValues = new HashMap<Key<FunctionFeatureDatas>, Double>();
		functionFeatureDataValues = new HashMap<Key<FunctionFeatureData>, Double>();
	}
	
	public void put( FunctionFeatureComparator baseComparator, List<FunctionFeatureData> features ) throws Exception {
		for ( int i=0; i<features.size(); i++ ){
			for ( int j=i; j<features.size(); j++ ){
				double distance = baseComparator.distance( features.get(i), features.get(j) );
				put( features.get(i), features.get(j), distance );
			}
		}
	}

	public void put( FunctionFeatureDatas features1, FunctionFeatureDatas features2, double value ) {
		functionFeatureDatasValues.put( Key.create(features1, features2), value );
	}
	
	public void put( FunctionFeatureData features1, FunctionFeatureData features2, double value ) {
		functionFeatureDataValues.put( Key.create(features1, features2), value );
	}
	
	@Override
	public double distance( FunctionFeatureDatas features1,
							FunctionFeatureDatas features2 ) throws Exception {
		Double ret = functionFeatureDatasValues.get( Key.create(features1, features2) );
		if ( ret == null ) {
			throw new Exception( "Value not cached" );
		}
		return ret;
	}

	@Override
	public double distance( FunctionFeatureData feature1,
							FunctionFeatureData feature2 ) throws Exception {
		Double ret = functionFeatureDataValues.get( Key.create(feature1, feature2) );
		if ( ret == null ) {
			throw new Exception( "Value not cached" );
		}
		return ret;
	}
}
