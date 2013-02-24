package com.vkb.alg;

import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import com.vkb.Feature;
import com.vkb.FeatureFunction;
import com.vkb.Features;
import com.vkb.FunctionPoints;
import com.vkb.RawTrace;

public class FeaturesExtractor {
	public Features extractFeatures( RawTrace rawTrace ) {
		Features features = new Features();
		
		FunctionPoints traceX = rawTrace.getXFunction();
		
		extractPositionFeatures( features, traceX, rawTrace.getYFunction() );
		
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, traceX.getMinX(), traceX.getMaxX() );
		extractVelocityFeatures( features, differentiator );
		extractAccelerationFeatures( features, differentiator );
		
		return features;
	}
	
	
	private void extractPositionFeatures( Features features,
								FunctionPoints traceX, FunctionPoints traceY ) {
		features.put( new FeatureFunction( Feature.POSITION_X, traceX ) );
		features.put( new FeatureFunction( Feature.POSITION_Y, traceY ) );
	}
	
	
	private void extractVelocityFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, Feature.VELOCITY_X, features.get(Feature.POSITION_X) ) ); 
		features.put( createDerivate( differentiator, Feature.VELOCITY_Y, features.get(Feature.POSITION_Y) ) );
	}
		

	private void extractAccelerationFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, Feature.ACCELERATION_X, features.get(Feature.VELOCITY_X) ) ); 
		features.put( createDerivate( differentiator, Feature.ACCELERATION_Y, features.get(Feature.VELOCITY_Y) ) );
	}
	
	
	private FeatureFunction createDerivate( FiniteDifferencesDifferentiator differentiator, Feature newFeatureName,
											FeatureFunction originalFeature ) {
		UnivariateDifferentiableFunction derivateFunction = differentiator.differentiate(originalFeature.getFunction());
		FunctionPoints samples = FunctionUtils.sample( derivateFunction, 1, 
				originalFeature.getMinTime(), originalFeature.getMaxTime(),  1.0d );
		return new FeatureFunction( newFeatureName, samples.interpolate(), 
										originalFeature.getMinTime(), originalFeature.getMaxTime() );
//		return new FeatureFunction( newFeatureName, samples );
//		UnivariateFunction function = new DerivateWrapFunction(derivateFunction);
//		return new FeatureFunction( newFeatureName, function, originalFeature.getMinTime(), originalFeature.getMaxTime() );
	}
}
