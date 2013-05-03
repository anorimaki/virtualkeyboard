package com.vkb.alg.determine;

import java.util.ArrayList;
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


public class PatternsStatistics {
	private Map<FeatureId, DescriptiveStatistics> scalarFeaturesStatistics;
	private Map<FeatureId, DescriptiveStatistics> functionFeaturesStatistics;
	private Map<FeatureId, List<FunctionFeatureData>> functionFeaturesDatas;
	
	public PatternsStatistics( List<Signature> signatures ) throws Exception {
		scalarFeaturesStatistics = new HashMap<FeatureId, DescriptiveStatistics>();
		functionFeaturesDatas = new HashMap<FeatureId, List<FunctionFeatureData>>();
		
		Signature signature0 = signatures.get(0);
		Set<Feature> scalarFeatures0 = signature0.getFeatures().getAllByModel( ScalarFeatureData.class );
		processSignature0ScalarFeatures( scalarFeatures0 );
			
		Set<Feature> functionFeatures0 = signature0.getFeatures().getAllByModel( FunctionFeatureData.class );
		processSignature0FunctionFeatures( functionFeatures0, functionFeaturesDatas );
		
		for ( int i=1; i<signatures.size(); ++i ) {
			Signature signature = signatures.get(i);
			
			Set<Feature> scalarFeatures = signature.getFeatures().getAllByModel( ScalarFeatureData.class );
			processScalarFeatures( scalarFeatures );
			
			Set<Feature> functionFeatures = signature.getFeatures().getAllByModel( FunctionFeatureData.class );
			processFunctionFeatures( functionFeatures, functionFeaturesDatas );
		}
		
		functionFeaturesStatistics = new HashMap<FeatureId, DescriptiveStatistics>();
		calculateFunctionFeaturesStatistics( functionFeaturesDatas );
	}
	
	public Set<FeatureId> getScalarFeatures() {
		return scalarFeaturesStatistics.keySet();
	}
	
	public Set<FeatureId> getFunctionFeatures() {
		return functionFeaturesStatistics.keySet();
	}
	
	public StatisticalSummary getScalarFeatureStatistics( FeatureId featureId ) {
		return scalarFeaturesStatistics.get( featureId );
	}
	
	public StatisticalSummary getFunctionFeatureStatistics( FeatureId featureId ) {
		return functionFeaturesStatistics.get( featureId );
	}
	
	public List<FunctionFeatureData> getFunctionFeatureDatas( FeatureId featureId ) {
		return functionFeaturesDatas.get( featureId );
	}
	
	private void calculateFunctionFeaturesStatistics(
						Map<FeatureId, List<FunctionFeatureData>> functionFeaturesDatas) throws Exception {
		
		for( Map.Entry<FeatureId, List<FunctionFeatureData>> functionFeatureDatas : functionFeaturesDatas.entrySet() ) {
			DescriptiveStatistics statistics = calculateFunctionsStatistics( functionFeatureDatas.getValue() );
			functionFeaturesStatistics.put( functionFeatureDatas.getKey(), statistics );
		}
	}

	
	private DescriptiveStatistics calculateFunctionsStatistics( List<FunctionFeatureData> datas ) throws Exception {
		FunctionFeatureComparator comparator = new FunctionFeatureComparator();
		
		DescriptiveStatistics ret = new DescriptiveStatistics();
		
		for ( int i=0; i<datas.size(); i++ ){
			for ( int j=i+1; i<datas.size(); i++ ){
				double distance = comparator.distance( datas.get(i), datas.get(j) );
				ret.addValue( distance );
			}
		}
		return ret;
	}


	private void processSignature0ScalarFeatures( Set<Feature> features ) {
		for( Feature feature : features ) {
			ScalarFeatureData data = feature.getData();
			
			DescriptiveStatistics featureStatistics = new DescriptiveStatistics();
			featureStatistics.addValue( data.getValue() );
			scalarFeaturesStatistics.put( feature.getId(), featureStatistics );
		}
	}
	
	private void processScalarFeatures( Set<Feature> features ) throws Exception {
		if ( features.size() != scalarFeaturesStatistics.size() ) {
			throw new Exception( "Error generating pattern. Signatures have different features" );
		}
		
		for( Feature feature : features ) {
			ScalarFeatureData data = feature.getData();
			
			DescriptiveStatistics featureStatistics = scalarFeaturesStatistics.get( feature.getId() );
			if ( featureStatistics==null ) {
				throw new Exception( "Error generating pattern. Signatures have different features" );
			}
			featureStatistics.addValue( data.getValue() );
		}
	}
	
	
	private void processSignature0FunctionFeatures( Set<Feature> features,  
								Map<FeatureId, List<FunctionFeatureData>> functionFeaturesDatas ) {
		for( Feature feature : features ) {
			FunctionFeatureData data = feature.getData();
			
			List<FunctionFeatureData> list = new ArrayList<FunctionFeatureData>();
			list.add( data );
			functionFeaturesDatas.put( feature.getId(), list );
		}
	}
	
	private void processFunctionFeatures( Set<Feature> features, 
								Map<FeatureId, List<FunctionFeatureData>> functionFeaturesDatas ) throws Exception {
		if ( features.size() != functionFeaturesDatas.size() ) {
			throw new Exception( "Error generating pattern. Signatures have different features" );
		}
		
		for( Feature feature : features ) {
			FunctionFeatureData data = feature.getData();
		
			List<FunctionFeatureData> list = functionFeaturesDatas.get( feature.getId() );
			if ( list==null ) {
				throw new Exception( "Error generating pattern. Signatures have different features" );
			}
			list.add( data );
		}
	}
}
