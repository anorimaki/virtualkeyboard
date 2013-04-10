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

public class FeatureQuality{

	private Map<String,UsersStatistics> statistics= new HashMap<String, UsersStatistics>();
		
	public void setUser(String user,UsersStatistics us){
		statistics.put(user, us);
		//System.out.println("Afegit usuari: "+user);
	}

	/* Multivariate ANOVA , sols per a funcions lineals 
	   f=(N-K)/K-1)*VARIANÇA_ENTRE_USUARIS / VARIANÇA_ENTRE_MOSTRES
	   Quanta mes variança entre mostres pitjor qualitat.
	   Quanta mes variança entre usauris millor qualitat (discriminacio major).
	 */
	public double calculateManova(FeatureId id){
		// Caldria optimitzar tot i ser offline
		double f=0.0;
		double globalMean=0.0;
		UserStatistic uS;
		double K=0.0;
		double N=0.0;
		double factor1=0.0;
		double factor2=0.0;
		double globalDev=0.0;
		double sumInternalDev=0.0;
		
		// Busquem mitja global del feature id
		DescriptiveStatistics aux = new DescriptiveStatistics();
		Iterator<String> it=statistics.keySet().iterator();
		while (it.hasNext()){
		    uS=statistics.get(it.next()).getStatistic(id);
			aux.addValue(uS.getMeanK());
			K++;
			N+=uS.getNK();
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
		  globalDev+=uS.getNK()*Math.pow((uS.getMeanK()-globalMean),2);
		  sumInternalDev+=uS.getInternalVar();
		}
		
		f=(factor1*globalDev)/(factor2*sumInternalDev);
		//System.out.println("Feature Quality "+id+": "+f);
		return f;
	}
	
	@Override
	public String toString(){
		return statistics.toString();
	}
}
