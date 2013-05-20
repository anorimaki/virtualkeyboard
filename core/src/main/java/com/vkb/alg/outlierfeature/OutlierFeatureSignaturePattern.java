package com.vkb.alg.outlierfeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.alg.determine.ScalarFeatureDeterminer;
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
		
		ret.put( FeatureId.POSITION_X, 3.0034542314335115 );
		ret.put( FeatureId.POSITION_Y, 4.166486486486489 );
		ret.put( FeatureId.VELOCITY_X, 6.266666666666674 );
		ret.put( FeatureId.VELOCITY_Y, 8.545454545454561 );
		ret.put( FeatureId.VELOCITY, 7.230769230769241 );
		ret.put( FeatureId.ACCELERATION_X, 7.861818181818216 );
		ret.put( FeatureId.ACCELERATION_Y, 12.164705882352964 );
		ret.put( FeatureId.ACCELERATION_Y, 7.410788381742729 );
		ret.put( FeatureId.POSITION_X_AVG, 2.5824175824175826 );
		ret.put( FeatureId.POSITION_Y_AVG, 2.2267343485617572 );
		ret.put( FeatureId.VELOCITY_X_AVG, 3.1596638655462264 );
		ret.put( FeatureId.VELOCITY_Y_AVG, 4.492647058823548 );
		ret.put( FeatureId.ACCELERATION_X_AVG, 2.1705150976909424 );
		ret.put( FeatureId.ACCELERATION_Y_AVG, 2.4736842105263177 );
		ret.put( FeatureId.AREA_X, 3.298245614035093 );
		ret.put( FeatureId.AREA_Y, 3.6797153024911053 );
		ret.put( FeatureId.RELATION_AREA, 2.4560570071258825 );
		ret.put( FeatureId.RELATION_X_Y, 2.6574029707714413 );
		
		return ret;
	}

	
	private static Map<FeatureId,Double> featureThresholdsConstruct() {
		Map<FeatureId,Double> ret = new HashMap<FeatureId,Double>();
		
		ret.put( FeatureId.POSITION_X, 1.4175675675675683 );
		ret.put( FeatureId.POSITION_Y, 1.4243902439024394 );
		ret.put( FeatureId.VELOCITY_X, 1.938888888888888 );
		ret.put( FeatureId.VELOCITY_Y, 1.7666666666666684 );
		ret.put( FeatureId.VELOCITY, 1.6545454545454552 );
		ret.put( FeatureId.ACCELERATION_X, 1.967391304347828 );
		ret.put( FeatureId.ACCELERATION_Y, 2.0045454545454553 );
		ret.put( FeatureId.ACCELERATION_Y, 1.6578947368421044 );
		ret.put( FeatureId.POSITION_X_AVG, 1.1149999999999989 );
		ret.put( FeatureId.POSITION_Y_AVG, 1.2464285714285723 );
		ret.put( FeatureId.VELOCITY_X_AVG, 1.731250000000001 );
		ret.put( FeatureId.VELOCITY_Y_AVG, 2.126923076923078 );
		ret.put( FeatureId.ACCELERATION_X_AVG, 0.8038461538461537 );
		ret.put( FeatureId.ACCELERATION_Y_AVG, 0.8777777777777775 );
		ret.put( FeatureId.AREA_X, 1.5750000000000015 );
		ret.put( FeatureId.AREA_Y, 1.5727272727272743 );
		ret.put( FeatureId.RELATION_AREA, 1.2931818181818175 );
		ret.put( FeatureId.RELATION_X_Y, 0.9830508474576277 );
	
		return ret;
	}
}