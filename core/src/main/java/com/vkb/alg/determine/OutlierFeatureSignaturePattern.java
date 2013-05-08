package com.vkb.alg.determine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vkb.model.Feature;
import com.vkb.model.FeatureData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;

public class OutlierFeatureSignaturePattern {
	
	private static Map<FeatureId,Double> featureWeight = featureWeightConstruct();
	private static Map<FeatureId,Double> alphaFunctionWeight = alphaFunctionWeightConstruct();
	private Map<FeatureId, FunctionFeatureDeterminer> functionFeatureDeterminers;
	private Map<FeatureId, ScalarFeatureDeterminer> scalarFeatureDeterminers;
	
	public OutlierFeatureSignaturePattern( List<Signature> traces ) throws Exception {
		createFuntionFeatureDeterminers( traces );
		createScalarFeatureDeterminer( traces );
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
	
	
	private void createScalarFeatureDeterminer( List<Signature> signatures ) throws Exception {
		scalarFeatureDeterminers = new HashMap<FeatureId, ScalarFeatureDeterminer>();
		Map<FeatureId, List<ScalarFeatureData>> featuresDatas = 
								extractFeaturesByModel( signatures, ScalarFeatureData.class );
		for( Map.Entry<FeatureId, List<ScalarFeatureData>> featureDatas : featuresDatas.entrySet() ) {
			scalarFeatureDeterminers.put( featureDatas.getKey(), 
								new ScalarFeatureDeterminer(featureDatas.getValue()) );
		}
	}
	

	private void createFuntionFeatureDeterminers( List<Signature> signatures ) throws Exception {
		functionFeatureDeterminers = new HashMap<FeatureId, FunctionFeatureDeterminer>();
		Map<FeatureId, List<FunctionFeatureData>> featuresDatas = 
								extractFeaturesByModel( signatures, FunctionFeatureData.class );
		for( Map.Entry<FeatureId, List<FunctionFeatureData>> featureDatas : featuresDatas.entrySet() ) {
			functionFeatureDeterminers.put( featureDatas.getKey(), 
								new FunctionFeatureDeterminer(featureDatas.getValue()) );
		}
	}
	
	static private <T extends FeatureData> Map<FeatureId, List<T>> extractFeaturesByModel( 
								List<Signature> signatures, Class<T> t ) throws Exception {
		Signature signature0 = signatures.get(0);
		
		Map<FeatureId, List<T>> ret = new HashMap<FeatureId, List<T>>();
		Set<Feature> signature0Features = signature0.getFeatures().getAllByModel( t );
		for( Feature feature : signature0Features ) {
			T data = feature.getData();
			
			List<T> list = new ArrayList<T>();
			list.add( data );
			ret.put( feature.getId(), list );
		}
		
		for ( int i=1; i<signatures.size(); ++i ) {
			Signature signature = signatures.get(i);
			Set<Feature> signatureFeatures = signature.getFeatures().getAllByModel( t );
			
			if ( signatureFeatures.size() != signature0Features.size() ) {
				throw new Exception( "Error generating pattern. Signatures have different features" );
			}
			
			for( Feature feature : signatureFeatures ) {
				T data = feature.getData();
			
				List<T> list = ret.get( feature.getId() );
				if ( list==null ) {
					throw new Exception( "Error generating pattern. Signatures have different features" );
				}
				list.add( data );
			}
		}
		
		return ret;
	}
}