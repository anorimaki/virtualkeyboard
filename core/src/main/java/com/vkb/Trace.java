package com.vkb;

import com.vkb.alg.FeaturesExtractor;

public class Trace {
	private RawTrace rawTrace;
	private Features features;
	
	public Trace( RawTrace rawTrace ) {
		this.rawTrace = rawTrace;
		
		FeaturesExtractor featuresExtractor = new FeaturesExtractor();
		features = featuresExtractor.extractFeatures( rawTrace );
	}

	public FeatureFunction getFeature( Feature feature ) {
		return features.get( feature );
	}
	
	public RawTrace getRawTrace() {
		return rawTrace;
	}
	
	@Override
	public String toString() {
		return rawTrace.toString();
	}
}
