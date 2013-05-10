package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.SignaturePatternBasedValidator;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class OutlierFeatureSignatureValidator implements SignaturePatternBasedValidator {
	private OutlierFeatureSignaturePattern pattern;
	private double threshold;
	
	public void setThreshold( double th ) {
		threshold = th;
	}
	
	public <T> T getFeatureValidator( FeatureId feature ) {
		return pattern.getFeatureValidator( feature );
	}
	
	@Override
	public void setPattern( List<Signature> patternTraces ) throws Exception {
		pattern = new OutlierFeatureSignaturePattern( patternTraces );
	}
	
	@Override
	public boolean check( Signature signature ) throws Exception {
		double insidersRate = pattern.compare(signature);
		return insidersRate > threshold;
	}
}
