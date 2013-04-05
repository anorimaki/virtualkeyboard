package com.vkb.model;

import java.util.HashMap;
import java.util.Map;

public class Statistics{
	private Map<String,Double> statistics= new HashMap<String, Double>();
	
	public static final String MEAN = "Mean";
	public static final String STDEV = "Stdev";
	public static final String MAX = "Max";
	public static final String MIN = "Min";
	public static final String DISTANCE = "Distance";
	
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
