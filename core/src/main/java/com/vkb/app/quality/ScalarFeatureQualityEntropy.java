package com.vkb.app.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.model.FeatureId;

public class ScalarFeatureQualityEntropy implements ScalarFeatureQualityCalculator{
	
	private static int Nh=10; // Disretitzacio histograma per Entropia
	private Map<String,ScalarFeaturesStatistics> statistics= new HashMap<String, ScalarFeaturesStatistics>();
		
	public void add( String user, ScalarFeaturesStatistics us ){
		statistics.put(user, us);
	}

	public double calculate( FeatureId id ){
		double R=0.0;
		StatisticalSummary globalMaxMin = calculateGlobalMaxMin(id);
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : statistics.entrySet() ) {
			DescriptiveStatistics uS = entry.getValue().get(id);
			List<Double> H=constructH( uS, globalMaxMin.getMin(), globalMaxMin.getMax() );
			
			// Passem statistics sense l'usuari a comparar
			List<Double> notH=constructNotH( id, entry.getKey(), globalMaxMin.getMin(), globalMaxMin.getMax() );
			
			double sum=0.0;
		    for(int i=0;i<Nh;i++) {
		    	sum += H.get(i)*notH.get(i);
		    }
		    
		    R += (-1.0) * Math.log(Math.sqrt(sum));
		}
		
		return R;
	}
	
	// NO ES POT APROFITAR EL HISTO DE uS JA QUE AQUEST ES CALCULA SOBRE MAX/MIN GLOBAL!
	private List<Double> constructH( DescriptiveStatistics uS, double min, double max ){
		// CAL VEURE SI EL NOMBRE DE DADES ES UN ELEMENT DE PONDERACIO (quan mes mostres
		// major sera el valor de columna), O SI CAL DIVIDIR ENTRE EL NOMBRE DE MOSTRES!
		
		List<Double> valuesFrequency = createFrequecyVector();
		updateFrequencyVector( uS, min, max, valuesFrequency );
		normalize( valuesFrequency, uS.getN() );
		return valuesFrequency;
	}
	
	
	private List<Double> constructNotH( FeatureId id, String user, double min, double max ) {
		// CAL VEURE SI EL NOMBRE DE DADES ES UN ELEMENT DE PONDERACIO (quan mes mostres
		// major sera el valor de columna), O SI CAL DIVIDIR ENTRE EL NOMBRE DE MOSTRES!
		
		List<Double> valuesFrequency = createFrequecyVector();
		
		// Recorrem els usuaris que no siguin uS
		double numMostres = 0.0;
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : statistics.entrySet() ) {
			if ( !entry.getKey().equals( user ) ) {
				DescriptiveStatistics uS2 = entry.getValue().get(id);
				updateFrequencyVector( uS2, min, max, valuesFrequency );
				numMostres += uS2.getN();
			}
		}
		
		normalize( valuesFrequency, numMostres );
		return valuesFrequency;
	}
		
		
	private List<Double> createFrequecyVector() {
		ArrayList<Double> tF = new ArrayList<Double>();
		for( int i=0; i<Nh; i++ )
			tF.add( new Double(0.0) );
		return tF;
	}
	
	// NO ES POT APROFITAR EL HISTO DE uS JA QUE AQUEST ES CALCULA SOBRE MAX/MIN GLOBAL!
	private void updateFrequencyVector( DescriptiveStatistics uS, double min, double max, List<Double> tF ){
		// CAL VEURE SI EL NOMBRE DE DADES ES UN ELEMENT DE PONDERACIO (quan mes mostres
		// major sera el valor de columna), O SI CAL DIVIDIR ENTRE EL NOMBRE DE MOSTRES!
		
		// Calculem increment per cada columna
		double inc = (max-min) / (double)Nh;
		
		// Obtenim els valors de les mostres de la carcteristica id per l'usuari uS
		double[] samples = uS.getValues();
		for( double sample : samples ) {
			double aux = sample - min;
			int value=(int)(Math.floor(aux/inc));
			// Evitem problemes amb truncament de decimals
			if(value>=Nh)
				value=Nh-1;
			if(value<0)
				value=0;

			// Incrementem
			tF.set( value, tF.get(value)+1.0 );
		}
	}
	
	
	private void normalize( List<Double> v, double denom ) {
		for(int i=0; i<v.size(); i++) {
			v.set( i, v.get(i).doubleValue() / denom );
		}
	}

	
	private StatisticalSummary calculateGlobalMaxMin( FeatureId id ){
		DescriptiveStatistics aux = new DescriptiveStatistics();
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : statistics.entrySet() ) {
			DescriptiveStatistics uS = entry.getValue().get(id);
			aux.addValue(uS.getMax());
		   	aux.addValue(uS.getMin());
		}
		return aux;
	}

	
	@Override
	public String toString(){
		return statistics.toString();
	}
}
