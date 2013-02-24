package com.vkb.math;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;


public class FunctionUtils {
	public static FunctionPoints sample( UnivariateFunction f, double min,
									double max, double increment ) {
		FunctionPoints ret = new FunctionPoints();
		sample( ret, f, min, max, increment );
		return ret;
	}
	
	public static FunctionPoints sample( String name, UnivariateFunction f, double min,
									double max, double increment ) {
		FunctionPoints ret = new FunctionPoints( name );
		sample( ret, f, min, max, increment );
		return ret;
	}
	
	public static FunctionPoints sample( UnivariateDifferentiableFunction f, 
									int order, double min, double max, double step ) {
		FunctionPoints ret = new FunctionPoints();
		sample( ret, f, order, min, max, step );
		return ret;
	}

	public static FunctionPoints sample( String name, UnivariateDifferentiableFunction f, 
										int order, double min, double max, double step ) {
		FunctionPoints ret = new FunctionPoints( name );
		sample( ret, f, order, min, max, step );
		return ret;
	}
	
	private static void sample( FunctionPoints result, UnivariateFunction f, 
							double min,	double max, double increment ) {
		for( double i=min; i<=max; i+=increment ) {
			result.add( i, f.value(i) );
		}
	}	
	
	private static void sample( FunctionPoints result, UnivariateDifferentiableFunction f, 
							int order, double min, double max, double step ) {
		for( double i=min; i<=max; i+=step ) {
			DerivativeStructure dt = new DerivativeStructure(1, order, 0, i);
			DerivativeStructure dx = f.value(dt);
			
			result.add( i, dx.getPartialDerivative(order) );
		}
	}
}
