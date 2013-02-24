package com.vkb.alg.extract;

import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.math.FunctionPoints;
import com.vkb.math.FunctionUtils;
import com.vkb.model.CapturedData;
import com.vkb.model.Features;
import com.vkb.model.FunctionFeature;
import com.vkb.model.FeatureType;
import com.vkb.model.Trace;

public class DefaultFeaturesExtractor implements FeaturesExtractor {
	@Override
	public Features extract( CapturedData inputData ) {
		Features features = new Features();
		
		Trace rawTrace = inputData.getTrace();
		
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
		features.put( new FunctionFeature( FeatureType.POSITION_X, traceX ) );
		features.put( new FunctionFeature( FeatureType.POSITION_Y, traceY ) );
	}
	
	
	private void extractVelocityFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureType.VELOCITY_X, features.get(FeatureType.POSITION_X) ) ); 
		features.put( createDerivate( differentiator, FeatureType.VELOCITY_Y, features.get(FeatureType.POSITION_Y) ) );
	}
		

	private void extractAccelerationFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureType.ACCELERATION_X, features.get(FeatureType.VELOCITY_X) ) ); 
		features.put( createDerivate( differentiator, FeatureType.ACCELERATION_Y, features.get(FeatureType.VELOCITY_Y) ) );
	}
	
	
	private FunctionFeature createDerivate( FiniteDifferencesDifferentiator differentiator, FeatureType newFeatureName,
											FunctionFeature originalFeature ) {
		UnivariateDifferentiableFunction derivateFunction = differentiator.differentiate(originalFeature.getFunction());
		FunctionPoints samples = FunctionUtils.sample( derivateFunction, 1, 
				originalFeature.getMinTime(), originalFeature.getMaxTime(),  1.0d );
		return new FunctionFeature( newFeatureName, samples.interpolate(), 
										originalFeature.getMinTime(), originalFeature.getMaxTime() );
//		return new FeatureFunction( newFeatureName, samples );
//		UnivariateFunction function = new DerivateWrapFunction(derivateFunction);
//		return new FeatureFunction( newFeatureName, function, originalFeature.getMinTime(), originalFeature.getMaxTime() );
	}
}
