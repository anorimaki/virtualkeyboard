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

public class FeatureQualityManova implements FeatureQualityCalculator {
	
	private Map<String,FeatureStatistics> statistics= new HashMap<String, FeatureStatistics>();
		
	public void setUser(String user,FeatureStatistics us){
		statistics.put(user, us);
		//System.out.println("Afegit usuari: "+user);
	}

	/* Multivariate ANOVA , sols per a funcions lineals 
	   f=(N-K)/K-1)*VARIAN�A_ENTRE_USUARIS / VARIAN�A_ENTRE_MOSTRES
	   Quanta mes varian�a entre mostres pitjor qualitat.
	   Quanta mes varian�a entre usauris millor qualitat (discriminacio major).
	 */
	public double calculate(FeatureId id){
		// Caldria optimitzar tot i ser offline
		double f=0.0;
		double globalMean=0.0;
		UserStatistic uS;
		double K=0.0;
		double N=0.0;
		double factor1=0.0;
		double factor2=0.0;
		double globalVar=0.0;
		double sumInternalVar=0.0;
		
		// Busquem mitja global del feature id
		DescriptiveStatistics aux = new DescriptiveStatistics();
		Iterator<String> it=statistics.keySet().iterator();
		while (it.hasNext()){
		    uS=statistics.get(it.next()).getStatistic(id);
			aux.addValue(uS.getMean());
			K++;
			N+=uS.getNumberOfSamples();
		}
		globalMean=aux.getMean();
		//System.out.println("N:"+N+" K:"+K+" Global Mean:"+globalMean);
		
		// Pel significat dels factors no es pot donar divisio entre 0
		factor1=1.0/(K-1);
		factor2=1.0/(N-K);
		
		// Repetim iteracio, optimitzar-ho implica refer la formulacio ANOVA2
		it=statistics.keySet().iterator();
		while (it.hasNext()){
		  uS=statistics.get(it.next()).getStatistic(id);
		  globalVar+=uS.getNumberOfSamples()*Math.pow((uS.getMean()-globalMean),2);
		  sumInternalVar+=uS.getInternalVar();
		}
		
		f=(factor1*globalVar)/(factor2*sumInternalVar);
		//System.out.println("Feature Quality "+id+": "+f);
		return f;
	}
		
	@Override
	public String toString(){
		return statistics.toString();
	}
}
