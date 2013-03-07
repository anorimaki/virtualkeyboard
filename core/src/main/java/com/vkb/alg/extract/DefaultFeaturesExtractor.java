package com.vkb.alg.extract;

import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.FunctionUtils;
import com.vkb.model.CapturedData;
import com.vkb.model.Feature;
import com.vkb.model.Features;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FeatureId;
import com.vkb.model.Trace;

public class DefaultFeaturesExtractor implements FeaturesExtractor {
	@Override
	public Features extract( CapturedData inputData ) {
		Features features = new Features();
		
		Trace rawTrace = inputData.getTrace();
		
		DiscreteFunction traceX = rawTrace.getXFunction();
		
		extractPositionFeatures( features, traceX, rawTrace.getYFunction() );
		
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, traceX.getMinX(), traceX.getMaxX() );
		extractVelocityFeatures( features, differentiator );
		extractAccelerationFeatures( features, differentiator );
		
		return features;
	}
	
	
	private void extractPositionFeatures( Features features,
								DiscreteFunction traceX, DiscreteFunction traceY ) {
		features.put( new Feature( FeatureId.POSITION_X, new FunctionFeatureData( traceX ) ) );
		features.put( new Feature( FeatureId.POSITION_Y, new FunctionFeatureData( traceY ) ) );
	}
	
	
	private void extractVelocityFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureId.VELOCITY_X, features.get(FeatureId.POSITION_X) ) );
		features.put( createDerivate( differentiator, FeatureId.VELOCITY_Y, features.get(FeatureId.POSITION_Y) ) );
	}
		

	private void extractAccelerationFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureId.ACCELERATION_X, features.get(FeatureId.VELOCITY_X) ) );
		features.put( createDerivate( differentiator, FeatureId.ACCELERATION_Y, features.get(FeatureId.VELOCITY_Y) ) );
	}
	
	
	private Feature createDerivate( FiniteDifferencesDifferentiator differentiator,
											FeatureId newFeatureId,
											Feature originalFeature ) {
		FunctionFeatureData originalFeatureData = originalFeature.getData();
		
		UnivariateDifferentiableFunction derivateFunction = 
				differentiator.differentiate(originalFeatureData.getFunction());
	
		DiscreteFunction samples = FunctionUtils.sample( newFeatureId.getName(),
				derivateFunction, 1, 
				originalFeatureData.getMinTime(), originalFeatureData.getMaxTime(),  1.0d );
		
		FunctionFeatureData data = new FunctionFeatureData( newFeatureId.getName(), 
				samples.interpolate(), 
				originalFeatureData.getMinTime(), originalFeatureData.getMaxTime() );
		
		return new Feature( newFeatureId, data );
	}
}
