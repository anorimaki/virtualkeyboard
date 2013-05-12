package com.vkb.app.quality;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.vkb.model.FeatureId;

public class ScalarFeatureQualityManova implements ScalarFeatureQualityCalculator {
	
	private Map<String,ScalarFeaturesStatistics> statistics= new HashMap<String, ScalarFeaturesStatistics>();
		
	public void add( String user,ScalarFeaturesStatistics us ){
		statistics.put(user, us);
	}

	/* Multivariate ANOVA , sols per a funcions lineals 
	   f=(N-K)/K-1)*VARIAN�A_ENTRE_USUARIS / VARIAN�A_ENTRE_MOSTRES
	   Quanta mes varian�a entre mostres pitjor qualitat.
	   Quanta mes varian�a entre usauris millor qualitat (discriminacio major).
	 */
	public double calculate(FeatureId id){
		// Busquem mitja global del feature id
		double K=0.0;
		double N=0.0;
		DescriptiveStatistics aux = new DescriptiveStatistics();
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : statistics.entrySet() ) {
			DescriptiveStatistics uS = entry.getValue().get(id);
			aux.addValue(uS.getMean());
			K++;
			N+=uS.getN();
		}
		double globalMean=aux.getMean();
		
		// Pel significat dels factors no es pot donar divisio entre 0
		double factor1=1.0/(K-1);
		double factor2=1.0/(N-K);
		double globalVar=0.0;
		double sumInternalVar=0.0;
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : statistics.entrySet() ) {
			DescriptiveStatistics uS = entry.getValue().get(id);
			globalVar += uS.getN() * Math.pow((uS.getMean()-globalMean),2);
			sumInternalVar += pseudoVariance( uS.getValues(), uS.getMean() );
		}
		
		return (factor1*globalVar)/(factor2*sumInternalVar);
	}
	
	private static double pseudoVariance( double[] values, double mean ){
		double res=0.0;
		for( double value : values ){
			res+=Math.pow(value-mean, 2.0);
		}
		return res;
	}
		
	@Override
	public String toString(){
		return statistics.toString();
	}
}
