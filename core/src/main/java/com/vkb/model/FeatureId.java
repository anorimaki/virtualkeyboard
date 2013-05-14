package com.vkb.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum FeatureId {
	POSITION_X( "X(t)", "POSITION_X", FunctionFeatureData.class ), 
	POSITION_Y( "Y(t)", "POSITION_Y", FunctionFeatureData.class ),
	VELOCITY_X( "X'(t)", "VELOCITY_X", FunctionFeatureData.class ),
	VELOCITY_Y( "Y'(t)", "VELOCITY_Y", FunctionFeatureData.class ),
	ACCELERATION_X( "X''(t)", "ACCELERATION_X", FunctionFeatureData.class ),
	ACCELERATION_Y( "Y''(t)", "ACCELERATION_Y", FunctionFeatureData.class ),
	POSITION_X_AVG( "XAVG", "POSITION_X_AVG", ScalarFeatureData.class ), 
	POSITION_Y_AVG( "YAVG", "POSITION_Y_AVG", ScalarFeatureData.class ),
	VELOCITY_X_AVG( "X'AVG", "VELOCITY_X_AVG", ScalarFeatureData.class ),
	VELOCITY_Y_AVG( "Y'AVG", "VELOCITY_Y_AVG", ScalarFeatureData.class ),
	ACCELERATION_X_AVG( "X''AVG", "ACCELERATION_X_AVG", ScalarFeatureData.class ),
	ACCELERATION_Y_AVG( "Y''AVG", "ACCELERATION_Y_AVG", ScalarFeatureData.class ),
	AREA_X( "XAREA", "AREA_X", ScalarFeatureData.class ),
	AREA_Y( "YAREA", "AREA_Y", ScalarFeatureData.class ),
	RELATION_AREA( "X_Y_AREA", "RELATION_AREA", ScalarFeatureData.class ),
	RELATION_X_Y( "X_Y_REL(t)", "RELATION_X_Y", FunctionFeatureData.class );
	
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
	
	private String userName;
	private String codeName;
	private Class<? extends FeatureData> type;
	
	private FeatureId( String name, String codeName, Class<? extends FeatureData> type ) {
		this.userName = name;
		this.codeName = "FeatureId." + codeName;
		this.type = type;
	}
	
	public String getName() {
		return userName;
	}
	
	public String getCodeName() {
		return codeName;
	}
	
	public Class<? extends FeatureData> getModel() {
		return type;
	}
	
	public static Set<FeatureId> getByModel( Class<? extends FeatureData> modelClass ) {
		return indexByModel.get( modelClass );
	}
	
	@Override
	public String toString() {
		return userName;
	}
}
