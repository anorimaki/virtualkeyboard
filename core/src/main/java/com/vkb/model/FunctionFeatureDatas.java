package com.vkb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vkb.math.DiscreteFunction;
import com.vkb.math.DiscreteVectorFunction;

public class FunctionFeatureDatas {
	private List<FunctionFeatureData> features;
	
	public FunctionFeatureDatas() {
		features = new ArrayList<FunctionFeatureData>();
	}
	
	public FunctionFeatureDatas( FunctionFeatureData...datas ) {
		this( Arrays.asList(datas) );
	}
	
	public FunctionFeatureDatas( Collection<FunctionFeatureData> datas ) {
		this.features = new ArrayList<FunctionFeatureData>( datas );
	}
	
	public void add( FunctionFeatureData data ) {
		features.add( data );
	}
	
	public DiscreteVectorFunction getSamples() throws Exception {
		List<DiscreteFunction> ret = new ArrayList<DiscreteFunction>();
		for( FunctionFeatureData feature : features ) {
			ret.add( feature.getSamples() );
		}
		Collections.sort( ret, new Comparator<DiscreteFunction>() {

			@Override
			public int compare(DiscreteFunction o1, DiscreteFunction o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		
		return new DiscreteVectorFunction( ret, DiscreteFunction.class );
	}
}
