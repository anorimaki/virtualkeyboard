package com.vkb.model;


public class Feature {
	private FeatureId id;
	private FeatureData model;
	
	public Feature( FeatureId id, FeatureData data ) {
		this.id = id;
		this.model = data;
	}
	
	public FeatureId getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FeatureData> T getData() {
		return (T) model;
	}
	
	@Override
	public String toString() {
		return id + ": " + model.toString();
	}
}
