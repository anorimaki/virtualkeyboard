package com.vkb.alg.determine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.Feature;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;

public class OutlierFeatureSignaturePattern {
	private static final double MAGICTHRESHOLD =1.96;
	
	private static Map<FeatureId,Double> featureWeight = featureWeightConstruct();
	private static Map<FeatureId,Double> alphaFunctionWeight = alphaFunctionWeightConstruct();
	private PatternsStatistics patternsStatistics;
	private Set<FeatureId> scalarFeatues;		//Pre computed set for performance
	private Set<FeatureId> functionFeatues;		//Pre computed set for performance
	
	public OutlierFeatureSignaturePattern( List<Signature> traces ) throws Exception {
		patternsStatistics = new PatternsStatistics(traces);
		scalarFeatues = patternsStatistics.getScalarFeatures();
		functionFeatues = patternsStatistics.getFunctionFeatures();
		
		featureWeightConstruct();
		alphaFunctionWeightConstruct();
	}
	
	
	public double compare( Signature trace ) throws Exception {
		Map<FeatureId,Boolean> featureCheckResults = new HashMap<FeatureId,Boolean>();
		
		// Recorrem totes les feature de trace i normalitzem (z-score) per cadascuna
		for( FeatureId feature : scalarFeatues ) {
			compareScalar( feature, trace, featureCheckResults );
		}

		for( FeatureId feature : functionFeatues ) {
			compareFunction( feature, trace, featureCheckResults );
		}
		
		return insidersRateCompute( featureCheckResults );
	}
	

	private static Map<FeatureId,Double> featureWeightConstruct() {
		Map<FeatureId,Double> featureWeight = new HashMap<FeatureId,Double>();
		
		
		featureWeight.put( FeatureId.ACCELERATION_X_AVG, 0.969685781326788d );
		featureWeight.put( FeatureId.ACCELERATION_Y_AVG, 0.8367402193097967d );
		featureWeight.put( FeatureId.POSITION_X_AVG, 0.8349410056742863d );
		featureWeight.put( FeatureId.POSITION_Y_AVG, 0.9131320183317968d );
		featureWeight.put( FeatureId.VELOCITY_X_AVG, 1.2139466477499392d );
		featureWeight.put( FeatureId.VELOCITY_Y_AVG, 1.198957799511142d );
		featureWeight.put( FeatureId.AREA_X, 1.110382116310794d );
		featureWeight.put( FeatureId.AREA_Y, 0.9133431709359657d );
		featureWeight.put( FeatureId.RELATION_AREA, 1.0088712408494909d );

		for( FeatureId feature : FeatureId.getByModel( FunctionFeatureData.class ) ) {
			featureWeight.put( feature, 1.0d );
		}
		
		
//		Proves per a una sola FunctionFeature
		/*
		featureWeight.put( FeatureId.ACCELERATION_X_AVG, 0.0d );
		featureWeight.put( FeatureId.ACCELERATION_Y_AVG, 0.0d );
		featureWeight.put( FeatureId.POSITION_X_AVG, 0.0d );
		featureWeight.put( FeatureId.POSITION_Y_AVG, 0.0d );
		featureWeight.put( FeatureId.VELOCITY_X_AVG, 0.0d );
		featureWeight.put( FeatureId.VELOCITY_Y_AVG, 0.0d );
		featureWeight.put( FeatureId.AREA_X, 0.0d );
		featureWeight.put( FeatureId.AREA_Y, 0.0d );
		featureWeight.put( FeatureId.RELATION_AREA, 0.0d );
		
		featureWeight.put( FeatureId.POSITION_X, 0.0d );
		featureWeight.put( FeatureId.POSITION_Y, 1.0d );
		featureWeight.put( FeatureId.VELOCITY_X, 0.0d );
		featureWeight.put( FeatureId.VELOCITY_Y, 0.0d );
		featureWeight.put( FeatureId.ACCELERATION_X, 0.0d );
		featureWeight.put( FeatureId.ACCELERATION_Y, 1.0d );
		featureWeight.put( FeatureId.RELATION_X_Y, 0.0d );
*/
		
		return featureWeight;
	}

	
	private static Map<FeatureId,Double> alphaFunctionWeightConstruct() {
		Map<FeatureId,Double> alphaWeight = new HashMap<FeatureId,Double>();
		
		alphaWeight.put( FeatureId.POSITION_X, 1.0d );
		alphaWeight.put( FeatureId.POSITION_Y, 1.0d );
		alphaWeight.put( FeatureId.VELOCITY_X, 1.0d );
		alphaWeight.put( FeatureId.VELOCITY_Y, 1.0d );
		alphaWeight.put( FeatureId.ACCELERATION_X, 1.0d );
		alphaWeight.put( FeatureId.ACCELERATION_Y, 1.0d );
		alphaWeight.put( FeatureId.RELATION_X_Y, 1.0d );
	
		return alphaWeight;
	}
	
	private void compareScalar( FeatureId featureId, Signature trace, 
								Map<FeatureId,Boolean> featureCheckResults ){
		Feature feature = trace.getFeature(featureId);
		ScalarFeatureData featureData = feature.getData();
		
		StatisticalSummary statistics = patternsStatistics.getScalarFeatureStatistics( featureId );
		
		double zscore = calculateZScore( featureData.getValue(), 
								statistics.getMean(), statistics.getStandardDeviation() );
		
		featureCheckResults.put( featureId, zscore < MAGICTHRESHOLD );
	}

	
	private double calculateZScore(double value, double mean, double stdev){
		double res;
		
		if(stdev!=0)
			res = (value-mean)/stdev;
		else
			res = 0.0; // Potser caldria revisar-ho
		
		return res;
	}
	
	private void compareFunction( FeatureId featureId, Signature trace,
								Map<FeatureId,Boolean> featureCheckResults ) throws Exception{

		Feature feature = trace.getFeature(featureId);
		FunctionFeatureData featureData = feature.getData();
		
		// Cal calcular DTW de ffd amb tots els patterns per cada feature i fer la mitja
		FunctionFeatureComparator comparator = new FunctionFeatureComparator();
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		List<FunctionFeatureData> patternDatas = patternsStatistics.getFunctionFeatureDatas( featureId );
		for( FunctionFeatureData patternData : patternDatas ) {
			double distance = comparator.distance( patternData, featureData );
			statistics.addValue( distance );
		}
		
		StatisticalSummary patternStatistics = patternsStatistics.getFunctionFeatureStatistics( featureId );
		featureCheckResults.put( featureId, statistics.getMean() < (alphaFunctionWeight.get(featureId)*patternStatistics.getMean()));
		
		// Prova per coneixer d(j) vs. D(j))
		//System.out.println(featureId+"-> "+statistics.getMean()+" < ("+(alphaFunctionWeight.get(featureId)*patternStatistics.getMean())+","+patternStatistics.getStandardDeviation()+")");
	}
	
	
	private double insidersRateCompute( Map<FeatureId,Boolean> featureCheckResults ){
		// A partir del vector de ratios calcula un ratio global, per comparar amb treshold
		double sum=0.0;
		double res=0.0;

		for( Map.Entry<FeatureId,Boolean> featureCheckResult : featureCheckResults.entrySet() ) {
			double weight = featureWeight.get( featureCheckResult.getKey() );
			sum += weight;
			if ( featureCheckResults.get( featureCheckResult.getKey() ) ) {
				res += weight;
			}
		}
		
		if(sum>0.0)
			res = res/sum;
					
		return res;
	}
}