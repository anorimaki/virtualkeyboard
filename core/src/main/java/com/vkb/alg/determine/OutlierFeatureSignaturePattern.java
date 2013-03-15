package com.vkb.alg.determine;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.vkb.model.FeatureId;
import com.vkb.model.Feature;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Statistics;

public class OutlierFeatureSignaturePattern {
	
	// Guardem els valors del zscore en un hash amb clau (Feature) per si els tresholds
	// aplicats són diferents en cada cas. Si això no és així, és millor utilitzar un simple
	// array de doubles.
	private Map<FeatureId,Double> FRVector = new HashMap<FeatureId,Double>();
	private PatternsStatistics pS;
	
	public OutlierFeatureSignaturePattern( List<Signature> traces ) {
		pS = new PatternsStatistics(traces);
		
		// PROVA
		/*
		double dada = pS.getFeatureStatistic(FeatureId.POSITION_X_AVG, "Mean");
		System.out.println("Mitja obtinguda de les mitjes de la posicio X als patterns: "+dada);
		dada = pS.getFeatureStatistic(FeatureId.POSITION_X_AVG, "Stdev");
		System.out.println("Stdev obtinguda de les mitjes de la posicio X als patterns: "+dada);
		*/
	}
	
	public double compare( Signature trace ) {
		double insidersRate = 0.0;
		// Recorrem totes les feature de trace i normalitzem (z-score) per cadascuna
		// De moment ho farem escollint manualment les feature
		
		compareScalar(FeatureId.POSITION_X_AVG, trace);
		compareScalar(FeatureId.POSITION_Y_AVG, trace);
		compareScalar(FeatureId.VELOCITY_X_AVG, trace);
		compareScalar(FeatureId.VELOCITY_Y_AVG, trace);
		compareScalar(FeatureId.ACCELERATION_X_AVG, trace);
		compareScalar(FeatureId.ACCELERATION_Y_AVG, trace);
		compareScalar(FeatureId.AREA_X, trace);
		compareScalar(FeatureId.AREA_Y, trace);
		compareScalar(FeatureId.RELATION_AREA, trace);
		
		System.out.println("Vector de zscores: "+FRVector.toString());
		
		insidersRate = insidersRateCompute();
		
		return insidersRate;
	}
	
	private void compareScalar(FeatureId id, Signature trace){
		Feature f;
		ScalarFeatureData sfd;
		double zscore;

		f=trace.getFeature(id);
		sfd = f.getData();
		zscore = ScalarZScore(sfd.getValue(),pS.getFeatureStatistic(id, Statistics.MEAN),pS.getFeatureStatistic(id, Statistics.STDEV));
		FRVector.put(id,new Double(zscore));
	
		//System.out.println("Feature "+id.toString()+": "+sfd.getValue());
	}

	
	private double ScalarZScore(double value, double mean, double stdev){
		double res;
		
		if(stdev!=0)
			res = (value-mean)/stdev;
		else
			res = 0.0; // Potser caldria revisar-ho
		
		return res;
	}
	
	private double insidersRateCompute(){
		// A partir del vector de ratios calcula un ratio global, per comparar amb treshold
		
		return 0.0;
	}
}
