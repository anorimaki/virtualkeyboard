package com.vkb.alg.determine;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.fastdtw.dtw.TimeWarpInfo;
import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Feature;
import com.vkb.model.Statistics;


public class PatternsStatistics {
	private static final FeatureId[] scalarFeatures = { 
		FeatureId.POSITION_X_AVG, FeatureId.POSITION_Y_AVG,
		FeatureId.VELOCITY_X_AVG, FeatureId.VELOCITY_Y_AVG,
		FeatureId.ACCELERATION_X_AVG, FeatureId.ACCELERATION_Y_AVG,
		FeatureId.AREA_X, FeatureId.AREA_Y, FeatureId.RELATION_AREA
	};
	
	private static final FeatureId[] temporalFeatures = { 
		FeatureId.POSITION_X, FeatureId.POSITION_Y,
		FeatureId.VELOCITY_X, FeatureId.VELOCITY_Y,
		FeatureId.ACCELERATION_X, FeatureId.ACCELERATION_Y, FeatureId.RELATION_X_Y
	};
		
	private Map<FeatureId, Statistics> featureStatistics = new HashMap<FeatureId, Statistics>();
	private List<Signature> signatures;
	
	public List<Signature> getSignatures(){
		return signatures;
	}
	
	public Statistics getFeatureStatistics(FeatureId id){
		return featureStatistics.get(id);
	}
	
	public double getFeatureStatistic(FeatureId id, String statisticName){
		return featureStatistics.get(id).getStatistic(statisticName);
	}
	
	public PatternsStatistics (List<Signature> traces){
		Statistics statisticValue;
		Signature sign;
		Feature f;
		ScalarFeatureData sfd;
		Map<FeatureId,DescriptiveStatistics> statisticsList = new HashMap<FeatureId,DescriptiveStatistics>();
		this.signatures = traces;
		
		// Recorrem tota la llista de signatures
		// Es pot simplificar el codi a costa de fer mes recorreguts sobre la llista
		for ( int i=0; i<traces.size(); ++i ) {
			for( FeatureId feature : scalarFeatures ) {
				sign = traces.get(i);
				// Per cada signatura agafem les seves features i les ajuntem
				f=sign.getFeature(feature);
				// Cal revisar si te el feature
				sfd = f.getData();
				if(statisticsList.containsKey(feature)){
					DescriptiveStatistics aux = statisticsList.get(feature);
					aux.addValue(sfd.getValue());
				}else{
					DescriptiveStatistics aux = new DescriptiveStatistics();
					aux.addValue(sfd.getValue());
					statisticsList.put(feature, aux);
				}
			}
		}
		
		for( FeatureId feature : scalarFeatures ) {
			CreateStatisticsMeanStdev(feature,statisticsList.get(feature).getMean(),statisticsList.get(feature).getStandardDeviation()); 
		}
	}
	
	private void CreateStatisticsMeanStdev(FeatureId id, double mean, double stdev){
		Statistics st = new Statistics();
			
		st.setStatistic(st.MEAN, mean);
		st.setStatistic(st.STDEV, stdev);
		featureStatistics.put(id, st);
		
		//System.out.println("Afegit estadistic "+id.toString()+": "+st.toString());
	}
	
}
