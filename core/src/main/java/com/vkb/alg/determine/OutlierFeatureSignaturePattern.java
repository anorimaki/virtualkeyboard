package com.vkb.alg.determine;

import java.util.List;

import com.vkb.model.Signature;

public class OutlierFeatureSignaturePattern {
	public OutlierFeatureSignaturePattern( List<Signature> traces ) {
		PatternsStatistics pS = new PatternsStatistics(traces);
	}
	
	public double compare( Signature trace ) {
		return 0;
	}
}
