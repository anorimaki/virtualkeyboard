package com.vkb.model;

public class ScalarFeature implements Feature {
	private double value;
	private FeatureType type;
	
	public ScalarFeature( FeatureType type, double value ) {
		this.value = value;
		this.type = type;
	}
	
	@Override
	public FeatureType getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}
}
