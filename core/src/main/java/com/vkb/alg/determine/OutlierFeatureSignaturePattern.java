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
	private static final FeatureId[] scalarFeatures = { 
		FeatureId.POSITION_X_AVG, FeatureId.POSITION_Y_AVG,
		FeatureId.VELOCITY_X_AVG, FeatureId.VELOCITY_Y_AVG,
		FeatureId.ACCELERATION_X_AVG, FeatureId.ACCELERATION_Y_AVG,
		FeatureId.AREA_X, FeatureId.AREA_Y, FeatureId.RELATION_AREA
	};
	
	// Guardem els valors del zscore en un hash amb clau (Feature) per si els tresholds
	// aplicats són diferents en cada cas. Si això no és així, és millor utilitzar un simple
	// array de doubles.
	private Map<FeatureId,Double> FRVector = new HashMap<FeatureId,Double>();
	private PatternsStatistics pS;
	
	public OutlierFeatureSignaturePattern( List<Signature> traces ) {
		pS = new PatternsStatistics(traces);
	}
	
	public double compare( Signature trace ) {
		double insidersRate = 0.0;
		// Recorrem totes les feature de trace i normalitzem (z-score) per cadascuna
		for( FeatureId feature : scalarFeatures ) {
			compareScalar(feature, trace);
		}

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
