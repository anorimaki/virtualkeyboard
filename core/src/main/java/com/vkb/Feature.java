package com.vkb;

public enum Feature {
	POSITION_X("X(t)"), POSITION_Y("Y(t)"),
	VELOCITY_X("X'(t)"), VELOCITY_Y("Y'(t)"),
	ACCELERATION_X("X''(t)"), ACCELERATION_Y("Y''(t)");;
	
	private String name;
	
	private Feature( String name ) {
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
