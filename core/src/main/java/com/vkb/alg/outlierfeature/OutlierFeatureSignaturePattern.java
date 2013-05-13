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
	

	private static Map<FeatureId,Double> featureWeightsConstruct() {
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
		
		return featureWeight;
	}

	
	private static Map<FeatureId,Double> featureThresholdsConstruct() {
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
				scalarFeatureDeterminers.put( featureDatas.getKey(), 
								new ScalarFeatureDeterminer(featureDatas.getValue()) );
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
				functionFeatureDeterminers.put( featureDatas.getKey(), 
								new FunctionFeatureDeterminer( featureDatas.getValue(), functionFeatureComparator ) );
			}
		}
	}
}