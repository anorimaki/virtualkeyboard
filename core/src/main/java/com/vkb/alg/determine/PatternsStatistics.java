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
		// Es pot simplificar el codi a costa de fer mes recorreguts sobre la llista
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
		
		CreateStatisticsMeanStdev(FeatureId.POSITION_X_AVG, position_x_avg.getMean(),position_x_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.POSITION_Y_AVG, position_y_avg.getMean(),position_y_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.VELOCITY_X_AVG, velocity_x_avg.getMean(),velocity_x_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.VELOCITY_Y_AVG, velocity_y_avg.getMean(),velocity_y_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.ACCELERATION_X_AVG, acceleration_x_avg.getMean(),acceleration_x_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.ACCELERATION_Y_AVG, acceleration_y_avg.getMean(),acceleration_y_avg.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.AREA_X, area_x.getMean(),area_x.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.AREA_Y, area_y.getMean(),area_y.getStandardDeviation());
		CreateStatisticsMeanStdev(FeatureId.RELATION_AREA, relation_area.getMean(),relation_area.getStandardDeviation());
	}
	
	private void CreateStatisticsMeanStdev(FeatureId id, double mean, double stdev){
		Statistics st = new Statistics();
			
		st.setStatistic(st.MEAN, mean);
		st.setStatistic(st.STDEV, stdev);
		featureStatistics.put(id, st);
		
		//System.out.println("Afegit estadistic "+id.toString()+": "+st.toString());
	}
	
}
