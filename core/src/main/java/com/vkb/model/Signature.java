package com.vkb.model;


public class Signature {
	private CapturedData capturedData;
	private Features features;
	
	public Signature( CapturedData capturedData, Features features ) {
		this.capturedData = capturedData;
		this.features = features;
	}

	public FunctionFeature getFeature( FeatureType feature ) {
		return features.get( feature );
	}
	
	public CapturedData getCapturedData() {
		return capturedData;
	}
	
	@Override
	public String toString() {
		return capturedData.toString();
	}
}
