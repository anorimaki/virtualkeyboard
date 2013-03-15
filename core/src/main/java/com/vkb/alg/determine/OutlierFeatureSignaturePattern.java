package com.vkb.alg.determine;

import java.util.List;

import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class OutlierFeatureSignaturePattern {
	public OutlierFeatureSignaturePattern( List<Signature> traces ) {
		PatternsStatistics pS = new PatternsStatistics(traces);
		
		// PROVA
		double dada = pS.getFeatureStatistic(FeatureId.POSITION_X_AVG, "Mean");
		System.out.println("Mitja obtinguda de les mitjes de la posicio X als patterns: "+dada);
		dada = pS.getFeatureStatistic(FeatureId.POSITION_X_AVG, "Stdev");
		System.out.println("Stdev obtinguda de les mitjes de la posicio X als patterns: "+dada);
	}
	
	public double compare( Signature trace ) {
		return 0;
	}
}
