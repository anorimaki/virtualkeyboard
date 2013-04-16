package com.vkb.app.quality;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Feature;


public class UserStatistic {
	private double mean=0.0;
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
		}
		
		// Calculem u(k) [meanK]
		mean=aux.getMean();
		max=aux.getMax();
		min=aux.getMin();
		//System.out.println("Mitja calculada per "+this.user+" feature:"+this.featureId+"->"+meanK);
	}
	
	public double getHistogramInc (int Nh){
		return (max-min)/(double)Nh;
	}
	
	public ArrayList<Double> getHistogram (int Nh){
		double inc, aux;
		int value;
		ArrayList<Double> tF = new ArrayList<Double>();
			
		// Inicialitzem vector Taula Frequencia
		for(int i=0;i<Nh;i++)
			tF.add(i, new Double(0.0));
			
		// Calculem increment per cada columna
		//inc=(max-min)/(double)Nh;
		inc=getHistogramInc(Nh);
		//System.out.println("Max: "+max+" Min: "+min+" Inc: "+inc);
			
		// Fem recompte del valor de les mostres
		Iterator<Double> it=samples.iterator();
		while (it.hasNext()){
			aux=it.next().doubleValue()-min;
			value=(int)(Math.floor(aux/inc));
			// Evitem problemes amb truncament de decimals
			if(value>=Nh) value=Nh-1;
			if(value<0) value=0;
			//System.out.println("Sample-min: "+aux+" Index: "+value);
			// Incrementem
			tF.set(value,tF.get(value)+1.0);
		}
			
		//System.out.println("TF H("+uS.getUser()+"): "+tF.toString());
		return tF;
	}
	
	
	public String getUser(){
		return user;
	}
	
	public FeatureId getFeatureId(){
		return featureId;
	}
	
	public double getMean(){
		return mean;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getMin(){
		return min;
	}
	
	public double getNumberOfSamples(){
		return samples.size();
	}
	
	public ArrayList<Double> getSamples(){
		return samples;
	}
	
	public double getInternalVar(){
		double res=0.0;
		
		for(int i=0;i<samples.size();i++){
			res+=Math.pow((samples.get(i)-mean), 2.0);
		}
		
		return res;
	}
}
