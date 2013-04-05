package com.vkb.model;

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
	
	@Override
	public String toString() {
		return name;
	}
}
