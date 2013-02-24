package com.vkb.alg;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class DerivateWrapFunction implements UnivariateFunction {
	private UnivariateDifferentiableFunction f;
	
	public DerivateWrapFunction( UnivariateDifferentiableFunction f ) {
		this.f = f;
	}
	
	@Override
	public double value(double x) {
		DerivativeStructure dt = new DerivativeStructure(1, 1, 0, x);
		DerivativeStructure dx = f.value(dt);
		return dx.getPartialDerivative(1) ;
	}
}
