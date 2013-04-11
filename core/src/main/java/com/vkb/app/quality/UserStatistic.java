package com.vkb.app.quality;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Feature;


public class UserStatistic {
	private double Nk=0.0;
	private double meanK=0.0;
	private double min=0.0;
	private double max=0.0;
	private String user;
	private FeatureId featureId;
	private ArrayList<Double> samples = new ArrayList<Double>();
	
	public UserStatistic(String user, List<Signature> traces, FeatureId id){
		// Tots els traces pertanyen al mateix usuari, per tant treballarem
		// amb la feature id de totes les signatures de l'usuari K
		Signature sign;
		Feature f;
		ScalarFeatureData sfd;
		
		this.user = user;
		this.featureId = id;
		
		DescriptiveStatistics aux = new DescriptiveStatistics();
		
		for ( int i=0; i<traces.size(); ++i ) {
			sign = traces.get(i);
			f=sign.getFeature(featureId);
			// SOLS PER ESCALARS!!!!
			sfd = f.getData();
			// Afegim al DescriptiveStatistic per calcular u(k) [meanK]
			aux.addValue(sfd.getValue());
			// Afegim al vector de samples
			samples.add(new Double(sfd.getValue()));
			// Incrementem nombre de mostres per usuari
			Nk++;
		}
		
		// Calculem u(k) [meanK]
		meanK=aux.getMean();
		max=aux.getMax();
		min=aux.getMin();
		//System.out.println("Mitja calculada per "+this.user+" feature:"+this.featureId+"->"+meanK);
	}
	
	public String getUser(){
		return user;
	}
	
	public FeatureId getFeatureId(){
		return featureId;
	}
	
	public double getMeanK(){
		return meanK;
	}
	
	public double getNK(){
		return Nk;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getMin(){
		return min;
	}
	
	public double getNumberOfSamples(){
		return Nk;
	}
	
	public ArrayList<Double> getSamples(){
		return samples;
	}
	
	public double getInternalVar(){
		double res=0.0;
		
		for(int i=0;i<samples.size();i++){
			res+=Math.pow((samples.get(i)-meanK), 2.0);
		}
		
		return res;
	}
}
