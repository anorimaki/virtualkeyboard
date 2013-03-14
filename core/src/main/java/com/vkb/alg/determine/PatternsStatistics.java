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


public class PatternsStatistics {

	public class Statistics{
		private Map<String,Double> statistics= new HashMap<String, Double>();
		
		public Map<String, Double> getStatistics(){
			return statistics;
		}
		
		public double getStatistic(String statisticName){
			Double aux = statistics.get(statisticName);
			return aux.doubleValue();
		}
		
		public void setStatistics(Map<String,Double> statistics){
			this.statistics=statistics;
		}
		
		public void setStatistic(String statisticName, double value){
			Double aux = new Double(value);
			statistics.put(statisticName, aux);
		}
	}
	
	
	private Map<FeatureId, Statistics> featureStatistics = new HashMap<FeatureId, Statistics>();
	private List<Signature> signatures;
	
	public PatternsStatistics (List<Signature> traces){
		Statistics statisticValue;
		Signature sign;
		Feature f;
		ScalarFeatureData sfd;
		DescriptiveStatistics position_x_avg = new DescriptiveStatistics();
		
		this.signatures = traces;
		
		// Recorrem tota la llista de signatures
		for ( int i=0; i<traces.size(); ++i ) {
			sign = traces.get(i);
			// Per cada signatura agafem les seves features i les ajuntem
			f=sign.getFeature(FeatureId.POSITION_X_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			position_x_avg.addValue(sfd.getValue());
			System.out.println("Signatura #"+i+", X_AVG: "+sfd.getValue());
		}
		
		double data=position_x_avg.getMean();
		System.out.println("Posicio_x_avg: "+data);
		
	}
	
}
