package com.vkb.alg.determine;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.EuclideanDistance;
import com.vkb.math.dtw.DataConvert;
import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
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
	private List<Signature> traces;
	
	public List<Signature> getSignatures(){
		return traces;
	}
	
	public Statistics getFeatureStatistics(FeatureId id){
		return featureStatistics.get(id);
	}
	
	public double getFeatureStatistic(FeatureId id, String statisticName){
		return featureStatistics.get(id).getStatistic(statisticName);
	}
	
	public PatternsStatistics (List<Signature> traces) throws Exception{
		Statistics statisticValue;
		Signature sign;
		Feature f;
		ScalarFeatureData sfd;
		FunctionFeatureData ffd,ffd2;
		Map<FeatureId,DescriptiveStatistics> statisticsList = new HashMap<FeatureId,DescriptiveStatistics>();
		
		this.traces = traces;
		
		// Recorrem tota la llista de signatures
		// Es pot simplificar el codi a costa de fer mes recorreguts sobre la llista
		for ( int i=0; i<traces.size(); ++i ) {
			for( FeatureId feature : scalarFeatures ) {
				sign = traces.get(i);
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
		
		// Acte seguit hauriem de calcular D per a les funcions temporals (donada la complexitat
		// i la poca variabilitat, seria millor tenir-ho emmagatzemat en un fitxer!!
		// DTW es commutativa DTW(A,B) == DTW (B,A)
		for( FeatureId feature : temporalFeatures ) {
			for (int i=0;i<traces.size();i++){
				sign = traces.get(i);
				f=sign.getFeature(feature);
				ffd=f.getData();
				for (int x=(i+1);x<traces.size();x++){
					sign = traces.get(x);
					f=sign.getFeature(feature);
					ffd2=f.getData();
					double distance = compareFast(ffd,ffd2);
					
					// System.out.print(feature.toString()+"("+i+","+x+")");
					
					if(statisticsList.containsKey(feature)){
						DescriptiveStatistics aux = statisticsList.get(feature);
						aux.addValue(distance);
						// System.out.println("Existeix estadistic");
					}else{
						DescriptiveStatistics aux = new DescriptiveStatistics();
						aux.addValue(distance);
						statisticsList.put(feature, aux);
						// System.out.println("No existeix estadistic");
					}
				}
			}
		}
		
		for( FeatureId feature : temporalFeatures ) {
			CreateStatisticsDistance(feature,statisticsList.get(feature).getMean()); 
		}
		
		
	}
	
	
	public double compareFunctions(FeatureId feature, FunctionFeatureData f1) throws Exception {
		TimeSeries ts2;
		FunctionFeatureData ffd;
		Signature sign;
		
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		DescriptiveStatistics sta = new DescriptiveStatistics();

		for (int i=0;i<traces.size();i++){
			sign = traces.get(i);
			ffd=sign.getFeature(feature).getData();
			ts2=DataConvert.getTimeSeries(ffd);
		    double distance = FastDTW.getWarpDistBetween(ts1,ts2,new EuclideanDistance());
		    
		    sta.addValue(distance);
		    
		    //System.out.println("Comparativa("+i+") de la feature "+feature.toString());
		}
		
		return sta.getMean();
	}
	
	
	private void CreateStatisticsMeanStdev(FeatureId id, double mean, double stdev){
		Statistics st = new Statistics();
			
		st.setStatistic(st.MEAN, mean);
		st.setStatistic(st.STDEV, stdev);
		featureStatistics.put(id, st);
		
		//System.out.println("Afegit estadistic "+id.toString()+": "+st.toString());
	}
	
	
	private double compareFast( FunctionFeatureData f1, FunctionFeatureData f2 ) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return FastDTW.getWarpDistBetween(ts1,ts2,new EuclideanDistance());
	}
	
	
	private void CreateStatisticsDistance(FeatureId id, double mean){
		Statistics st = new Statistics();
			
		st.setStatistic(st.DISTANCE, mean);
		featureStatistics.put(id, st);
		
		//System.out.println("Afegit estadistic "+id.toString()+": "+st.toString());
	}
	
}
