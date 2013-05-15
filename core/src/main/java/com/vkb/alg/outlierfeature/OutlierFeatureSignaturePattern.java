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
	
	
	private static Map<FeatureId,Double> featureWeightsConstruct() {
		Map<FeatureId,Double> ret = new HashMap<FeatureId,Double>();
		
		ret.put( FeatureId.POSITION_X, 3.266666666666663);
		ret.put( FeatureId.POSITION_Y, 5.500000000000009);
		ret.put( FeatureId.VELOCITY_X, 16.333333333333325);
		ret.put( FeatureId.VELOCITY_Y, 10.499999999999986);
		ret.put( FeatureId.ACCELERATION_X, 16.333333333333357);
		ret.put( FeatureId.ACCELERATION_Y, 14.000000000000009);
		ret.put( FeatureId.POSITION_X_AVG, 2.3333333333333317);
		ret.put( FeatureId.POSITION_Y_AVG, 3.4999999999999996);
		ret.put( FeatureId.VELOCITY_X_AVG, 2.3333333333333357);
		ret.put( FeatureId.VELOCITY_Y_AVG, 8.4);
		ret.put( FeatureId.ACCELERATION_X_AVG, 1.8260869565217397);
		ret.put( FeatureId.ACCELERATION_Y_AVG, 1.96);
		ret.put( FeatureId.AREA_X, 4.2);
		ret.put( FeatureId.AREA_Y, 6.999999999999991);
		ret.put( FeatureId.RELATION_AREA, 2.3333333333333357);
		ret.put( FeatureId.RELATION_X_Y, 3.000000000000003);
		
		return ret;
	}

	
	private static Map<FeatureId,Double> featureThresholdsConstruct() {
		Map<FeatureId,Double> ret = new HashMap<FeatureId,Double>();
		
		ret.put( FeatureId.POSITION_X, 1.2428571428571429);
		ret.put( FeatureId.POSITION_Y, 1.322727272727273);
		ret.put( FeatureId.VELOCITY_X, 1.5285714285714287);
		ret.put( FeatureId.VELOCITY_Y, 1.4666666666666668);
		ret.put( FeatureId.ACCELERATION_X, 1.3785714285714288);
		ret.put( FeatureId.ACCELERATION_Y, 1.3250000000000002);
		ret.put( FeatureId.POSITION_X_AVG, 0.9833333333333336);
		ret.put( FeatureId.POSITION_Y_AVG, 0.8749999999999997);
		ret.put( FeatureId.VELOCITY_X_AVG, 1.6500000000000041);
		ret.put( FeatureId.VELOCITY_Y_AVG, 1.1083333333333334);
		ret.put( FeatureId.ACCELERATION_X_AVG, 1.308333333333334);
		ret.put( FeatureId.ACCELERATION_Y_AVG, 0.9714285714285715);
		ret.put( FeatureId.AREA_X, 1.0166666666666666);
		ret.put( FeatureId.AREA_Y, 1.1000000000000005);
		ret.put( FeatureId.RELATION_AREA, 1.325);
		ret.put( FeatureId.RELATION_X_Y, 0.9333333333333335);
	
		return ret;
	}
}