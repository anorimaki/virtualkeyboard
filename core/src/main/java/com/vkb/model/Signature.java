package com.vkb.model;


public class Signature {
	private CapturedData capturedData;
	private Features features;
	
	public Signature( CapturedData capturedData, Features features ) {
		this.capturedData = capturedData;
		this.features = features;
	}

	public Feature getFeature( FeatureId feature ) {
		return features.get( feature );
	}
	
	public Features getFeatures() {
		return features;
	}
	
	public CapturedData getCapturedData() {
		return capturedData;
	}
	
	@Override
	public String toString() {
		return capturedData.toString();
	}
}
