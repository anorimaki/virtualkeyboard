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
		
		@Override
		public String toString(){
			return statistics.toString();
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
		DescriptiveStatistics position_y_avg = new DescriptiveStatistics();
		DescriptiveStatistics velocity_x_avg = new DescriptiveStatistics();
		DescriptiveStatistics velocity_y_avg = new DescriptiveStatistics();
		DescriptiveStatistics acceleration_x_avg = new DescriptiveStatistics();
		DescriptiveStatistics acceleration_y_avg = new DescriptiveStatistics();
		DescriptiveStatistics area_x = new DescriptiveStatistics();
		DescriptiveStatistics area_y = new DescriptiveStatistics();
		DescriptiveStatistics relation_area = new DescriptiveStatistics();
				
		this.signatures = traces;
		
		// Recorrem tota la llista de signatures
		for ( int i=0; i<traces.size(); ++i ) {
			sign = traces.get(i);
			// Per cada signatura agafem les seves features i les ajuntem
			f=sign.getFeature(FeatureId.POSITION_X_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			position_x_avg.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.POSITION_Y_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			position_y_avg.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.VELOCITY_X_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			velocity_x_avg.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.VELOCITY_Y_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			velocity_y_avg.addValue(sfd.getValue());

			f=sign.getFeature(FeatureId.ACCELERATION_X_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			acceleration_x_avg.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.ACCELERATION_Y_AVG);
			// Cal revisar si te el feature
			sfd = f.getData();
			acceleration_y_avg.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.AREA_X);
			// Cal revisar si te el feature
			sfd = f.getData();
			area_x.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.AREA_Y);
			// Cal revisar si te el feature
			sfd = f.getData();
			area_y.addValue(sfd.getValue());
			
			f=sign.getFeature(FeatureId.RELATION_AREA);
			// Cal revisar si te el feature
			sfd = f.getData();
			relation_area.addValue(sfd.getValue());
		}
		
		CreateStatisticsPositionX_AVG(position_x_avg.getMean(),position_x_avg.getStandardDeviation());
		
		
	}
	
	private void CreateStatisticsPositionX_AVG(double mean, double stdev){
		Statistics st = new Statistics();
		st.setStatistic("Mean", mean);
		st.setStatistic("Stdev", stdev);
		
		featureStatistics.put(FeatureId.POSITION_X_AVG, st);
		
		System.out.println("Afegit estadistic Position_x_avg: "+st.toString());
	}
}
