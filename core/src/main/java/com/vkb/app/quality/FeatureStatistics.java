package com.vkb.app.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Feature;

public class FeatureStatistics{
	private static final FeatureId[] scalarFeatures = { 
		FeatureId.POSITION_X_AVG, FeatureId.POSITION_Y_AVG,
		FeatureId.VELOCITY_X_AVG, FeatureId.VELOCITY_Y_AVG,
		FeatureId.ACCELERATION_X_AVG, FeatureId.ACCELERATION_Y_AVG,
		FeatureId.AREA_X, FeatureId.AREA_Y, FeatureId.RELATION_AREA
	};
	
	private Map<FeatureId,UserStatistic> statistics= new HashMap<FeatureId, UserStatistic>();
	private String user;
	
	
	public FeatureStatistics(String user, List traces){
		this.user=user;
		for( FeatureId feature : scalarFeatures ) {
			UserStatistic aux = new UserStatistic( user, traces, feature );
			statistics.put(feature,aux);
		}
	}	

	
	public Map<FeatureId,UserStatistic> getStatistics(){
		return statistics;
	}
	
	public UserStatistic getStatistic(FeatureId id){
		UserStatistic aux = statistics.get(id);
		return aux;
	}
	
	public String getUser(){
		return user;
	}
	
	@Override
	public String toString(){
		return statistics.toString();
	}
}
