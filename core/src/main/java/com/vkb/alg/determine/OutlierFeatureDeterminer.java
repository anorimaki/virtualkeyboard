package com.vkb.alg.determine;

import java.util.List;

import com.vkb.alg.Determiner;
import com.vkb.model.Signature;

public class OutlierFeatureDeterminer implements Determiner {
	private OutlierFeatureSignaturePattern patternComparator;
	private double threshold;
	
	// Arreglada per l'optimitzacio!
	public OutlierFeatureDeterminer(double threshold ) throws Exception {
		patternComparator = new OutlierFeatureSignaturePattern();
		this.threshold = threshold;
	}
	
	public boolean check( Signature signature, PatternsStatistics pS  ) throws Exception {
		double insidersRate = checkRate(signature, pS);
		return insidersRate > threshold;
	}
	
	public double checkRate( Signature signature, PatternsStatistics pS ) throws Exception {
		double insidersRate = patternComparator.compare(signature, pS);
		return insidersRate;
	}
}
