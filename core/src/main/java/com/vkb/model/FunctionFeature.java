package com.vkb.model;

import org.apache.commons.math3.analysis.UnivariateFunction;

import com.vkb.math.FunctionPoints;
import com.vkb.math.FunctionUtils;
import com.vkb.math.Point;

public class FunctionFeature implements Feature {
	private static final double DEFAULT_STEP = 1.0d;
	private FeatureType feature;
	private UnivariateFunction function;
	private FunctionPoints samples;
	
	public FunctionFeature( FeatureType feature, FunctionPoints functionPoints ) {
		this.feature = feature;
		this.function = functionPoints.interpolate();
		
		if ( fitsDesriredInterval( functionPoints ) ) {
			samples = functionPoints;
		}
		else {
			samples = FunctionUtils.sample( feature.name(), function, 
									functionPoints.getMinX(), functionPoints.getMaxX(), DEFAULT_STEP );
		}
	}

	public FunctionFeature( FeatureType feature, UnivariateFunction function, double min, double max ) {
		this.feature = feature;
		this.function = function;
		this.samples = FunctionUtils.sample( feature.name(), function, min, max, DEFAULT_STEP );
	}
	
	@Override
	public FeatureType getType() {
		return feature;
	}
	
	public FunctionPoints getSamples() {
		return samples;
	}

	public double getMinTime() {
		return samples.getMinX();
	}
	
	public double getMaxTime() {
		return samples.getMaxX();
	}
	
	public UnivariateFunction getFunction() {
		return function;
	}
	
	@Override
	public String toString() {
		return feature.getName() + ": " + samples.toString();
	}
	
	
	private static boolean fitsDesriredInterval(FunctionPoints functionPoints) {
		double expectedStep = functionPoints.getMinX();
		for( Point point : functionPoints ) {
			if( point.getX() != expectedStep )
				return false;
			expectedStep += DEFAULT_STEP;
		}
		return true;
	}
}
