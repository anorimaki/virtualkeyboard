package com.vkb.math;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;


public class FunctionUtils {
/*	public static DiscreteFunction sample( UnivariateFunction f, double min,
									double max, double increment ) {
		DiscreteFunction ret = new DiscreteFunction();
		sample( ret, f, min, max, increment );
		return ret;
	} */
	
	public static DiscreteFunction sample( String name, UnivariateFunction f, double min,
									double max, double increment ) {
		DiscreteFunction ret = new DiscreteFunction( name );
		sample( ret, f, min, max, increment );
		return ret;
	}
/*	
	public static DiscreteFunction sample( UnivariateDifferentiableFunction f, 
									int order, double min, double max, double step ) {
		DiscreteFunction ret = new DiscreteFunction();
		sample( ret, f, order, min, max, step );
		return ret;
	} */

	public static DiscreteFunction sample( String name, UnivariateDifferentiableFunction f, 
										int order, double min, double max, double step ) {
		DiscreteFunction ret = new DiscreteFunction( name );
		sample( ret, f, order, min, max, step );
		return ret;
	}
	
	private static void sample( DiscreteFunction result, UnivariateFunction f, 
							double min,	double max, double increment ) {
		for( double i=min; i<=max; i+=increment ) {
			result.add( i, f.value(i) );
		}
	}	
	
	private static void sample( DiscreteFunction result, UnivariateDifferentiableFunction f, 
							int order, double min, double max, double step ) {
		for( double i=min; i<=max; i+=step ) {
			DerivativeStructure dt = new DerivativeStructure(1, order, 0, i);
			DerivativeStructure dx = f.value(dt);
			
			result.add( i, dx.getPartialDerivative(order) );
		}
	}
}
