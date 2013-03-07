package com.vkb.model;

public enum FeatureId {
	POSITION_X( "X(t)", FunctionFeatureData.class ), 
	POSITION_Y( "Y(t)", FunctionFeatureData.class ),
	VELOCITY_X( "X'(t)", FunctionFeatureData.class ),
	VELOCITY_Y( "Y'(t)", FunctionFeatureData.class ),
	ACCELERATION_X( "X''(t)", FunctionFeatureData.class ),
	ACCELERATION_Y( "Y''(t)", FunctionFeatureData.class );
	
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
