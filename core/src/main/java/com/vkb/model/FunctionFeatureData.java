package com.vkb.model;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.StepFunction;

import com.vkb.math.DiscreteFunction;
import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.FunctionUtils;

public class FunctionFeatureData implements FeatureData {
	private static final double DEFAULT_STEP = 1.0d;
	private UnivariateFunction function;
	private DiscreteFunction samples;
	private DiscreteVectorFunction vectorFunction;
	private double weight;
	
	public FunctionFeatureData( DiscreteFunction functionPoints ) throws Exception {
		if ( fitsDesriredInterval( functionPoints ) ) {
			samples = functionPoints;
			function = new StepFunction( samples.getX(), samples.getY() );
		}
		else {
			function = functionPoints.interpolate();
			samples = FunctionUtils.sample( functionPoints.getName(), function, 
						functionPoints.getMinX(), functionPoints.getMaxX(), DEFAULT_STEP );
		}
		vectorFunction = new DiscreteVectorFunction(samples);
	}
	
	public FunctionFeatureData( String name, UnivariateFunction function, 
								double min, double max ) throws Exception {
		this.function = function;
		this.samples = FunctionUtils.sample( name, function, min, max, DEFAULT_STEP );
		this.vectorFunction = new DiscreteVectorFunction(samples);
	}
	
	public DiscreteFunction getSamples() {
		return samples;
	}
	
	public DiscreteVectorFunction getVectorFunction() {
		return vectorFunction;
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
