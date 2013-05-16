package com.vkb.alg.outlierfeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.alg.determine.ScalarFeatureDeterminer;
import com.vkb.math.dtw.DefaultFunctionFeatureComparator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;

public class OutlierFeatureSignaturePattern {
	private static Map<FeatureId,Double> featureWeights = featureWeightsConstruct();
	private static Map<FeatureId,Double> featureThresholds = featureThresholdsConstruct();
	private Map<FeatureId, FunctionFeatureDeterminer> functionFeatureDeterminers;
	private Map<FeatureId, ScalarFeatureDeterminer> scalarFeatureDeterminers;
	
	public OutlierFeatureSignaturePattern( List<Signature> traces ) throws Exception {
		this( traces, new DefaultFunctionFeatureComparator() );
	}
	
	
	public OutlierFeatureSignaturePattern( List<Signature> traces, 
							FunctionFeatureComparator functionFeatureComparator ) throws Exception {
		createFuntionFeatureDeterminers( traces, functionFeatureComparator );
		createScalarFeatureDeterminer( traces );
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getFeatureValidator( FeatureId feature ) {
		FunctionFeatureDeterminer ret = functionFeatureDeterminers.get(feature);
		if ( ret!=null ) {
			return (T) ret;
		}
		return (T) scalarFeatureDeterminers.get(feature);
	}
	
	
	public void setFunctionComparator( FunctionFeatureComparator functionComparator ) {
		for( Map.Entry<FeatureId, FunctionFeatureDeterminer> determiner : functionFeatureDeterminers.entrySet() ) {
			determiner.getValue().setFunctionComparator(functionComparator);
		}
	}

	
	public double compare( Signature trace ) throws Exception {
		Map<FeatureId,Boolean> featureCheckResults = new HashMap<FeatureId,Boolean>();
		
		for( Map.Entry<FeatureId, ScalarFeatureDeterminer> determiner : scalarFeatureDeterminers.entrySet() ) {
			FeatureId featureId = determiner.getKey();
			ScalarFeatureData featureData = trace.getFeature(featureId).getData();
			featureCheckResults.put( featureId, determiner.getValue().check(featureData) );
		}

		for( Map.Entry<FeatureId, FunctionFeatureDeterminer> determiner : functionFeatureDeterminers.entrySet() ) {
			FeatureId featureId = determiner.getKey();
			FunctionFeatureData featureData = trace.getFeature(featureId).getData();
			featureCheckResults.put( featureId, determiner.getValue().check(featureData) );
		}
		
		return insidersRateCompute( featureCheckResults );
	}
	

	private double insidersRateCompute( Map<FeatureId,Boolean> featureCheckResults ){
		// A partir del vector de ratios calcula un ratio global, per comparar amb treshold
		double sum=0.0;
		double res=0.0;

		for( Map.Entry<FeatureId,Boolean> featureCheckResult : featureCheckResults.entrySet() ) {
			double weight = featureWeights.get( featureCheckResult.getKey() );
			sum += weight;
			if ( featureCheckResults.get( featureCheckResult.getKey() ) ) {
				res += weight;
			}
		}
		
		if(sum>0.0)
			res = res/sum;
					
		return res;
	}
	
	
	private void createScalarFeatureDeterminer( List<Signature> signatures ) throws Exception {
		scalarFeatureDeterminers = new HashMap<FeatureId, ScalarFeatureDeterminer>();
		Map<FeatureId, List<ScalarFeatureData>> featuresDatas = 
					Signatures.extractFeatureDatasByModel( signatures, ScalarFeatureData.class );
		for( Map.Entry<FeatureId, List<ScalarFeatureData>> featureDatas : featuresDatas.entrySet() ) {
			FeatureId featureId = featureDatas.getKey();
			if ( featureWeights.containsKey(featureId) ) {
				ScalarFeatureDeterminer validator = new ScalarFeatureDeterminer(featureDatas.getValue());
				validator.setThreshold( featureThresholds.get(featureId) );
				scalarFeatureDeterminers.put( featureDatas.getKey(), validator );
			}
		}
	}
	

	private void createFuntionFeatureDeterminers( List<Signature> signatures,
						FunctionFeatureComparator functionFeatureComparator ) throws Exception {
		functionFeatureDeterminers = new HashMap<FeatureId, FunctionFeatureDeterminer>();
		Map<FeatureId, List<FunctionFeatureData>> featuresDatas = 
					Signatures.extractFeatureDatasByModel( signatures, FunctionFeatureData.class );
		for( Map.Entry<FeatureId, List<FunctionFeatureData>> featureDatas : featuresDatas.entrySet() ) {
			FeatureId featureId = featureDatas.getKey();
			if ( featureWeights.containsKey(featureId) ) {
				FunctionFeatureDeterminer validator = new FunctionFeatureDeterminer( featureDatas.getValue(), functionFeatureComparator );
				validator.setThreshold( featureThresholds.get(featureId) );
				functionFeatureDeterminers.put( featureDatas.getKey(), validator );
			}
		}
	}
	
	
	public static Map<FeatureId,Double> getFeatureWeights() {
		return featureWeights;
	}
	
	
	private static Map<FeatureId,Double> featureWeightsConstruct() {
		Map<FeatureId,Double> ret = new HashMap<FeatureId,Double>();
		
		ret.put( FeatureId.POSITION_X, 5.833333333333344 );
		ret.put( FeatureId.POSITION_Y, 6.3636363636363695 );
		ret.put( FeatureId.VELOCITY_X, 17.499999999999975 );
		ret.put( FeatureId.VELOCITY_Y, 17.499999999999975 );
		ret.put( FeatureId.ACCELERATION_X, 18.846153846153992 );
		ret.put( FeatureId.ACCELERATION_Y, 17.50000000000006 );
		ret.put( FeatureId.POSITION_X_AVG, 3.130081300813009 );
		ret.put( FeatureId.POSITION_Y_AVG, 2.841880341880344 );
		ret.put( FeatureId.VELOCITY_X_AVG, 3.4027777777777724 );
		ret.put( FeatureId.VELOCITY_Y_AVG, 6.621621621621631 );
		ret.put( FeatureId.ACCELERATION_X_AVG, 2.1875000000000018 );
		ret.put( FeatureId.ACCELERATION_Y_AVG, 2.464788732394369 );
		ret.put( FeatureId.AREA_X, 6.125000000000011 );
		ret.put( FeatureId.AREA_Y, 5.0 );
		ret.put( FeatureId.RELATION_AREA, 3.47328244274809 );
		ret.put( FeatureId.RELATION_X_Y, 3.0624999999999947 );
		
		return ret;
	}

	
	private static Map<FeatureId,Double> featureThresholdsConstruct() {
		Map<FeatureId,Double> ret = new HashMap<FeatureId,Double>();
		
		ret.put( FeatureId.POSITION_X, 1.0699999999999992 );
		ret.put( FeatureId.POSITION_Y, 1.2749999999999997 );
		ret.put( FeatureId.VELOCITY_X, 1.5800000000000003 );
		ret.put( FeatureId.VELOCITY_Y, 1.6000000000000019 );
		ret.put( FeatureId.ACCELERATION_X, 1.7071428571428586 );
		ret.put( FeatureId.ACCELERATION_Y, 1.5166666666666673 );
		ret.put( FeatureId.POSITION_X_AVG, 0.8409090909090915 );
		ret.put( FeatureId.POSITION_Y_AVG, 0.9842105263157905 );
		ret.put( FeatureId.VELOCITY_X_AVG, 1.1357142857142861 );
		ret.put( FeatureId.VELOCITY_Y_AVG, 1.2857142857142863 );
		ret.put( FeatureId.ACCELERATION_X_AVG, 0.7624999999999965 );
		ret.put( FeatureId.ACCELERATION_Y_AVG, 0.840000000000001 );
		ret.put( FeatureId.AREA_X, 0.9142857142857148 );
		ret.put( FeatureId.AREA_Y, 1.3750000000000022 );
		ret.put( FeatureId.RELATION_AREA, 1.0153846153846156 );
		ret.put( FeatureId.RELATION_X_Y, 0.928571428571427 );
	
		return ret;
	}
}