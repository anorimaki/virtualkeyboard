package com.vkb.alg.determine;

import java.util.List;

import com.vkb.alg.Determiner;
import com.vkb.model.Signature;

public class OutlierFeatureDeterminer implements Determiner {
	private OutlierFeatureSignaturePattern pattern;
	private double threshold;
	
	public OutlierFeatureDeterminer( List<Signature> patternTraces, double threshold ) throws Exception {
		pattern = new OutlierFeatureSignaturePattern(patternTraces);
		this.threshold = threshold;
	}
	
	public boolean check( Signature signature ) throws Exception {
		double insidersRate = pattern.compare(signature);
		return insidersRate > threshold;
	}
}
