package com.vkb.alg.determine;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.vkb.model.FeatureId;
import com.vkb.model.Feature;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Statistics;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.EuclideanDistance;
import com.vkb.math.dtw.DataConvert;

public class OutlierFeatureSignaturePattern {
	private static final double MAGICTHRESHOLD =1.96;
	
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
	
	// Guardem els valors del zscore en un hash amb clau (Feature) per si els tresholds
	// aplicats són diferents en cada cas. Si això no és així, és millor utilitzar un simple
	// array de doubles.
	private Map<FeatureId,Double> FRVectorValues = new HashMap<FeatureId,Double>();
	private Map<FeatureId,Boolean> FRVector = new HashMap<FeatureId,Boolean>();
	private Map<FeatureId,Double> FeatureWeight = new HashMap<FeatureId,Double>();

	
	public double compare( Signature trace, PatternsStatistics pS ) throws Exception {
		double insidersRate = 0.0;
		
		// Metode cutre que dona pes a les features
		// NO SE ON HAURIA D'ANAR!!
		FeatureWeightConstruct();
		
		// Recorrem totes les feature de trace i normalitzem (z-score) per cadascuna
		for( FeatureId feature : scalarFeatures ) {
			compareScalar(feature, trace, pS);
		}

		/* ********************************************** */
		/* PER DESACTIVAR LES COMPARATIVES DE FUNCIONS	  */
		/* ********************************************** */
		
		for( FeatureId feature : temporalFeatures ) {
			compareFunction(feature, trace, pS);
		}
		
		insidersRate = insidersRateCompute();
		
		System.out.println("Vector de zscores: "+FRVectorValues.toString());
		System.out.println("Vector de resultats: "+FRVector.toString());
		System.out.println("Resultat final: "+insidersRate);
		
		
		return insidersRate;
	}
	
	
	
	/*
	 * Vector escalars FeatureEntropy:[
	 * {XAVG,0.8349410056742863},{YAVG,0.9131320183317968},
	 * {X'AVG,1.2139466477499392},{Y'AVG,1.198957799511142},
	 * {X''AVG,0.969685781326788},{Y''AVG,0.8367402193097967},
	 * {XAREA,1.110382116310794},{YAREA,0.9133431709359657},{X_Y_AREA,1.0088712408494909}]
	 */

	private void FeatureWeightConstruct()	{
		// Construccio del vector de ponderacio per feature, caldra veure com es fa...
		double weight=1.0;
		
		for( FeatureId feature : scalarFeatures ) {
			switch(feature){
			case ACCELERATION_X_AVG:
				FeatureWeight.put(feature, new Double(0.969685781326788d));
				break;
			case ACCELERATION_Y_AVG:
				FeatureWeight.put(feature, new Double(0.8367402193097967d));
				break;
			case POSITION_X_AVG:
				FeatureWeight.put(feature, new Double(0.8349410056742863d));
				break;
			case POSITION_Y_AVG:
				FeatureWeight.put(feature, new Double(0.9131320183317968d));
				break;
			case VELOCITY_X_AVG:
				FeatureWeight.put(feature, new Double(1.2139466477499392d));
				break;
			case VELOCITY_Y_AVG:
				FeatureWeight.put(feature, new Double(1.198957799511142d));
				break;
			case AREA_X:
				FeatureWeight.put(feature, new Double(1.110382116310794d));
				break;
			case AREA_Y:
				FeatureWeight.put(feature, new Double(0.9133431709359657d));
				break;
			case RELATION_AREA:
				FeatureWeight.put(feature, new Double(1.0088712408494909d));
				break;
			}
			
		}
		
		
		for( FeatureId feature : temporalFeatures ) {
			FeatureWeight.put(feature, new Double(weight));
		}
		
	}
	
	/*
	 * Vector escalars FeatureQuality:[
	 * {XAVG,0.3768048447466019},{YAVG,0.2729715216542056},
	 * {X'AVG,0.6924663770832848},{Y'AVG,5.743944338705075},
	 * {X''AVG,0.06676699868539843},{Y''AVG,0.12548844928140834},
	 * {XAREA,0.45904199520094613},{YAREA,0.671843784861471},{X_Y_AREA,0.5906716897816078}]
	 */

	/*
	private void FeatureWeightConstruct()	{
		// Construccio del vector de ponderacio per feature, caldra veure com es fa...
		double weight=1.0;
		
		for( FeatureId feature : scalarFeatures ) {
			switch(feature){
			case ACCELERATION_X_AVG:
				FeatureWeight.put(feature, new Double(0.06676699868539843d));
				break;
			case ACCELERATION_Y_AVG:
				FeatureWeight.put(feature, new Double(0.12548844928140834d));
				break;
			case POSITION_X_AVG:
				FeatureWeight.put(feature, new Double(0.3768048447466019d));
				break;
			case POSITION_Y_AVG:
				FeatureWeight.put(feature, new Double(0.2729715216542056d));
				break;
			case VELOCITY_X_AVG:
				FeatureWeight.put(feature, new Double(0.6924663770832848d));
				break;
			case VELOCITY_Y_AVG:
				FeatureWeight.put(feature, new Double(5.743944338705075d));
				break;
			case AREA_X:
				FeatureWeight.put(feature, new Double(0.45904199520094613d));
				break;
			case AREA_Y:
				FeatureWeight.put(feature, new Double(0.671843784861471d));
				break;
			case RELATION_AREA:
				FeatureWeight.put(feature, new Double(0.5906716897816078d));
				break;
			}
			
		}
		
		
		for( FeatureId feature : temporalFeatures ) {
			FeatureWeight.put(feature, new Double(weight));
		}
		
	}
*/
	
	
	/*
	
	// SENSE PONDERACIO
	 
	private void FeatureWeightConstruct()	{
		// Construccio del vector de ponderacio per feature, caldra veure com es fa...
		double weight=1.0;
		
		for( FeatureId feature : scalarFeatures ) {
			switch(feature){
			case ACCELERATION_X_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case ACCELERATION_Y_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case POSITION_X_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case POSITION_Y_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case VELOCITY_X_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case VELOCITY_Y_AVG:
				FeatureWeight.put(feature, 1.0d);
				break;
			case AREA_X:
				FeatureWeight.put(feature, 1.0d);
				break;
			case AREA_Y:
				FeatureWeight.put(feature, 1.0d);
				break;
			case RELATION_AREA:
				FeatureWeight.put(feature, 1.0d);
				break;
			}
			
		}
		
		
		for( FeatureId feature : temporalFeatures ) {
			FeatureWeight.put(feature, new Double(weight));
		}
		
	}
	*/
	private void compareScalar(FeatureId id, Signature trace, PatternsStatistics pS){
		Feature f;
		ScalarFeatureData sfd;
		double zscore;

		f=trace.getFeature(id);
		sfd = f.getData();
		zscore = ScalarZScore(sfd.getValue(),pS.getFeatureStatistic(id, Statistics.MEAN),pS.getFeatureStatistic(id, Statistics.STDEV));
		FRVectorValues.put(id,new Double(zscore));
		
		if(zscore < MAGICTHRESHOLD)
			FRVector.put(id, new Boolean(true));
		else
			FRVector.put(id, new Boolean(false));
	
		//System.out.println("Feature "+id.toString()+": "+sfd.getValue());
	}

	
	private double ScalarZScore(double value, double mean, double stdev){
		double res;
		
		if(stdev!=0)
			res = (value-mean)/stdev;
		else
			res = 0.0; // Potser caldria revisar-ho
		
		return res;
	}
	
	private void compareFunction(FeatureId id, Signature trace, PatternsStatistics pS) throws Exception{
		Feature f;
		FunctionFeatureData ffd;
		double d=0.0;

		f=trace.getFeature(id);
		ffd = f.getData();
		
		// Cal calcular DTW de ffd amb tots els patterns per cada feature i fer la mitja
		d=pS.compareFunctions(id, ffd);
		
		FRVectorValues.put(id,new Double(d));
	
		// Per acabar cal comparar amb la D(i) emmagatzemada a pS
		if(d < pS.getFeatureStatistic(id, Statistics.DISTANCE))
			FRVector.put(id, new Boolean(true));
		else
			FRVector.put(id, new Boolean(false));
	}
	
	private double insidersRateCompute(){
		// A partir del vector de ratios calcula un ratio global, per comparar amb treshold
		double p=0.0;
		double sum=0.0;
		double res=0.0;
		boolean aux;
		
		for( FeatureId feature : scalarFeatures ) {
			aux=FRVector.containsKey(feature);
			if(aux){
				p = FeatureWeight.get(feature).doubleValue();
				sum=sum+p;
				if(FRVector.get(feature))
					res = res+p;
			}
		}

		for( FeatureId feature : temporalFeatures ) {
			aux=FRVector.containsKey(feature);
			if(aux){
				p = FeatureWeight.get(feature).doubleValue();
				sum=sum+p;
				if(FRVector.get(feature))
					res = res+p;
			}
		}
		
		if(sum>0.0)
			res = res/sum;
					
		return res;
	}
}