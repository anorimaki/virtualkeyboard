package com.vkb.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum FeatureId {
	POSITION_X( "X(t)", FunctionFeatureData.class ), 
	POSITION_Y( "Y(t)", FunctionFeatureData.class ),
	VELOCITY_X( "X'(t)", FunctionFeatureData.class ),
	VELOCITY_Y( "Y'(t)", FunctionFeatureData.class ),
	ACCELERATION_X( "X''(t)", FunctionFeatureData.class ),
	ACCELERATION_Y( "Y''(t)", FunctionFeatureData.class ),
	POSITION_X_AVG( "XAVG", ScalarFeatureData.class ), 
	POSITION_Y_AVG( "YAVG", ScalarFeatureData.class ),
	VELOCITY_X_AVG( "X'AVG", ScalarFeatureData.class ),
	VELOCITY_Y_AVG( "Y'AVG", ScalarFeatureData.class ),
	ACCELERATION_X_AVG( "X''AVG", ScalarFeatureData.class ),
	ACCELERATION_Y_AVG( "Y''AVG", ScalarFeatureData.class ),
	AREA_X( "XAREA", ScalarFeatureData.class ),
	AREA_Y( "YAREA", ScalarFeatureData.class ),
	RELATION_AREA( "X_Y_AREA", ScalarFeatureData.class ),
	RELATION_X_Y( "X_Y_REL(t)", FunctionFeatureData.class );
	
	private static Map<Class<? extends FeatureData>, Set<FeatureId>> indexByModel;
	
	static {
		indexByModel = new HashMap<Class<? extends FeatureData>, Set<FeatureId>>();
		for ( FeatureId feature : FeatureId.values() ) {
			Set<FeatureId> set = indexByModel.get( feature.getModel() );
			if ( set == null ) {
				set = new HashSet<FeatureId>();
				indexByModel.put( feature.getModel(), set );
			}
			set.add( feature );
		}
	}
	
	private String name;
	private Class<? extends FeatureData> type;
	
	private FeatureId( String name, Class<? extends FeatureData> type ) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<? extends FeatureData> getModel() {
		return type;
	}
	
	public static Set<FeatureId> getByModel( Class<? extends FeatureData> modelClass ) {
		return indexByModel.get( modelClass );
	}
	
	@Override
	public String toString() {
		return name;
	}
}
