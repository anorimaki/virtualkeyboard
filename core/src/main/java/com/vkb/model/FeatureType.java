package com.vkb.model;

public enum FeatureType {
	POSITION_X("X(t)"), POSITION_Y("Y(t)"),
	VELOCITY_X("X'(t)"), VELOCITY_Y("Y'(t)"),
	ACCELERATION_X("X''(t)"), ACCELERATION_Y("Y''(t)");;
	
	private String name;
	
	private FeatureType( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
