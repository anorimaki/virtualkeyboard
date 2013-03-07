package com.vkb.model;

public class ScalarFeatureData implements FeatureData {
	private double value;
	
	public ScalarFeatureData( double value ) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Double.toString( value );
	}
}
