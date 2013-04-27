package com.vkb.model;

import org.apache.commons.math3.analysis.UnivariateFunction;

import com.vkb.math.DiscreteFunction;
import com.vkb.math.FunctionUtils;

public class FunctionFeatureData implements FeatureData {
	private static final double DEFAULT_STEP = 1.0d;
	private UnivariateFunction function;
	private DiscreteFunction samples;
	private double weight;
	
	public FunctionFeatureData( DiscreteFunction functionPoints ) {
		this.function = functionPoints.interpolate();
		
		if ( fitsDesriredInterval( functionPoints ) ) {
			samples = functionPoints;
		}
		else {
			samples = FunctionUtils.sample( functionPoints.getName(), function, 
						functionPoints.getMinX(), functionPoints.getMaxX(), DEFAULT_STEP );
		}
	}

	public FunctionFeatureData( String name, UnivariateFunction function, 
								double min, double max ) {
		this.function = function;
		this.samples = FunctionUtils.sample( name, function, min, max, DEFAULT_STEP );
	}
	
	public DiscreteFunction getSamples() {
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
	
	public void setWeight(double w){
		this.weight=w;
	}
	
	public double getWirght(){
		return this.weight;
	}
	
	@Override
	public String toString() {
		return samples.toString();
	}
	
	
	private static boolean fitsDesriredInterval(DiscreteFunction function) {
		double expectedStep = function.getMinX();
		for( DiscreteFunction.Point point : function.getPoints() ) {
			if( point.getX() != expectedStep )
				return false;
			expectedStep += DEFAULT_STEP;
		}
		return true;
	}
}
