package com.vkb.model;

public class ScalarFeatureData implements FeatureData {
	private double value;
	private double weight;
	
	public ScalarFeatureData( double value ) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setWeight(double w){
		this.weight=w;
	}
	
	public double getWirght(){
		return this.weight;
	}
	
	@Override
	public String toString() {
		return Double.toString( value );
	}
}
