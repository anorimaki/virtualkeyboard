package com.vkb.app.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.FeatureId;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Feature;

public class FeatureQualityEntropy implements FeatureQualityCalculator{
	
	private static int Nh=10; // Disretitzacio histograma per Entropia
	private Map<String,FeatureStatistics> statistics= new HashMap<String, FeatureStatistics>();
		
	public void setUser(String user,FeatureStatistics us){
		statistics.put(user, us);
		//System.out.println("Afegit usuari: "+user);
	}

	public double calculate(FeatureId id){
		double R=0.0;
		double sum;
		double[] globalMaxMin;//MIN | MAX
		UserStatistic uS;
		ArrayList<Double> H;
		ArrayList<Double> notH;
		
		globalMaxMin=calculateGlobalMaxMin(id);
		Iterator<String> it=statistics.keySet().iterator();
		while (it.hasNext()){
		    uS=statistics.get(it.next()).getStatistic(id);
		    H=constructH(uS, globalMaxMin[0],globalMaxMin[1]);
		    // Passem statistics sense l'usuari a comparar
		    notH=constructNotH(id, uS, globalMaxMin[0],globalMaxMin[1]);
		    // Calculem sumatori per cada nivell de H i notH
		    sum=0.0;
		    for(int i=0;i<Nh;i++)
		    	sum+=H.get(i)*notH.get(i);
		    // Calculem sumatori amb -ln(sqrt(sum))
		    R+=(-1.0)*Math.log(Math.sqrt(sum));
		}
		//System.out.println("Entropy: "+R);
		return R;
	}
	
	private ArrayList<Double> constructH(UserStatistic uS, double min, double max){
		// CAL VEURE SI EL NOMBRE DE DADES ES UN ELEMENT DE PONDERACIO (quan mes mostres
		// major sera el valor de columna), O SI CAL DIVIDIR ENTRE EL NOMBRE DE MOSTRES!
		
		double inc, aux;
		int value;
		ArrayList<Double> samples;
		ArrayList<Double> tF = new ArrayList<Double>();
		
		// Inicialitzem vector Taula Frequencia
		for(int i=0;i<Nh;i++)
			tF.add(i, new Double(0.0));
		
		// Calculem increment per cada columna
		inc=(max-min)/(double)Nh;
		//System.out.println("Max: "+max+" Min: "+min+" Inc: "+inc);
		
		// Obtenim els valors de les mostres de la carcteristica id per l'usuari uS
		samples=uS.getSamples();
		
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
		
		// NORMALITZACIO
		for(int i=0;i<Nh;i++)
			tF.set(i, new Double(tF.get(i).doubleValue()/((double)samples.size())));
					
		//System.out.println("TF H("+uS.getUser()+"): "+tF.toString());
		return tF;
	}
	
	private ArrayList<Double> constructNotH(FeatureId id, UserStatistic uS, double min, double max){
		// CAL VEURE SI EL NOMBRE DE DADES ES UN ELEMENT DE PONDERACIO (quan mes mostres
		// major sera el valor de columna), O SI CAL DIVIDIR ENTRE EL NOMBRE DE MOSTRES!
		
		double inc, aux;
		double numMostres=0.0;
		int value;
		Iterator<String> itS;
		UserStatistic uS2;
		ArrayList<Double> samples;
		ArrayList<Double> tF = new ArrayList<Double>();
		
		// Inicialitzem vector Taula Frequencia
		for(int i=0;i<Nh;i++)
			tF.add(i, new Double(0.0));
		
		// Calculem increment per cada columna
		inc=(max-min)/(double)Nh;
		//System.out.println("Max: "+max+" Min: "+min+" Inc: "+inc);
		
		// Recorrem els usuaris que no siguin uS 
		itS=statistics.keySet().iterator();
		while (itS.hasNext()){
			uS2=statistics.get(itS.next()).getStatistic(id);
			if(uS2.getUser().compareTo(uS.getUser())!=0){
				// Obtenim els valors de les mostres de la carcteristica id per l'usuari uS
				samples=uS2.getSamples();
		
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
					numMostres++;
				}
			}
		}
		// NORMALITZACIO
				for(int i=0;i<Nh;i++)
					tF.set(i, new Double(tF.get(i).doubleValue()/(numMostres)));
		
		//System.out.println("TF NotH("+uS.getUser()+"): "+tF.toString());
		return tF;
	}
	
	
	private double[] calculateGlobalMaxMin(FeatureId id){
		double maxmin[]={0.0,0.0}; //MIN | MAX
		DescriptiveStatistics aux = new DescriptiveStatistics();
		UserStatistic uS;
				
		Iterator<String> it=statistics.keySet().iterator();
		while (it.hasNext()){
		    uS=statistics.get(it.next()).getStatistic(id);
		   	aux.addValue(uS.getMax());
		   	aux.addValue(uS.getMin());
		}
		maxmin[0]=aux.getMin();
		maxmin[1]=aux.getMax();
		
		return maxmin;
	}

	
	@Override
	public String toString(){
		return statistics.toString();
	}
}
