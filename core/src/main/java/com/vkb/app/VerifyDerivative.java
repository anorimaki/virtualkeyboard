package com.vkb.app;

import java.io.File;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.vkb.Feature;
import com.vkb.FunctionPoints;
import com.vkb.Point;
import com.vkb.RawTrace;
import com.vkb.alg.DerivateWrapFunction;
import com.vkb.alg.FunctionUtils;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.TraceParser;

public class VerifyDerivative {
	private File inputFile;
	
	public VerifyDerivative( File inputFile ) {
		this.inputFile = inputFile;
	}
	
	private void run() throws Exception {
		RawTrace trace = new TraceParser().parse( inputFile );
		
		FunctionPoints x = trace.getXFunction();
		
		LoessInterpolator interpolator = new LoessInterpolator( 0.05d, 0, LoessInterpolator.DEFAULT_ACCURACY );
		PolynomialSplineFunction interpolationFunction = interpolator.interpolate( x.getX(), x.getY() );
		
								//Derivate with interpolation and commons-math DerivativeStructures
		FunctionPoints ds_p = new FunctionPoints( "Interpolation + DerivativeStructure - " + Feature.POSITION_X.name() );
		FunctionPoints ds_v = new FunctionPoints( "Interpolation + DerivativeStructure - " + Feature.VELOCITY_X.name() );
		FunctionPoints ds_a = new FunctionPoints( "Interpolation + DerivativeStructure - " + Feature.ACCELERATION_X.name() );
		generateFunctionsWithDerivativeStructures( interpolationFunction, x.getMinX(), x.getMaxX(), ds_p, ds_v, ds_a );
		
								//Derivate with finite differences
		FunctionPoints p = FunctionUtils.sample( Feature.POSITION_X.name(), 
												interpolationFunction, x.getMinX(), x.getMaxX(), 1.0d );
		FunctionPoints v = derivate( "Finite differences - " + Feature.VELOCITY_X.name(), p );
		FunctionPoints a = derivate( "Finite differences - " + Feature.ACCELERATION_X.name(), v );
		
								//Derivate with interpolation and commons-math FiniteDifferencesDifferentiator
		FunctionPoints ids_v = new FunctionPoints( "Interpolations + FiniteDifferencesDifferentiator - " + Feature.VELOCITY_X.name() );
		FunctionPoints ids_a = new FunctionPoints( "Interpolations + FiniteDifferencesDifferentiator - " + Feature.ACCELERATION_X.name() );
		generateFunctionsWithFiniteDifferencesDifferentiator( x, ids_v, ids_a );
		
								//Derivate with interpolation and commons-math FiniteDifferencesDifferentiator
		FunctionPoints fp_v = new FunctionPoints( "Multiples Interpolations + FiniteDifferencesDifferentiator - " + Feature.VELOCITY_X.name() );
		FunctionPoints fp_a = new FunctionPoints( "Multiples Interpolations + FiniteDifferencesDifferentiator - " + Feature.ACCELERATION_X.name() );
		generateFunctionsWithFiniteDifferencesDifferentiatorAndMultiplesInterpolations( x, fp_v, fp_a );
		
		XYPlot positionPlot = generatePlot( "Position", p, ds_p );
		XYPlot velocityPlot = generatePlot( "Velocity", v, ds_v, ids_v, fp_v );
		XYPlot accelerationPlot = generatePlot( "Accelereation", a, ds_a, ids_a, fp_a );
		
		Application application = new Application("VerifyInterpolation");
		application.run( positionPlot, velocityPlot, accelerationPlot );
	}
		
		
	private XYPlot generatePlot( String yName, FunctionPoints... f ) {
		NumberAxis xAxis = new NumberAxis("Time");
        xAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis yAxis = new NumberAxis(yName);
        
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer( renderer );
        
        plot.setDataset( 0, DataConvert.getDataset(f) );
        
		return plot;
	}
	
	private void generateFunctionsWithFiniteDifferencesDifferentiator(
			FunctionPoints p, FunctionPoints fp_v, FunctionPoints fp_a ) {
	
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, p.getMinX(), p.getMaxX() );
		
		UnivariateDifferentiableFunction pDerivateFunction = differentiator.differentiate( p.interpolate() );
		UnivariateFunction vFunction = new DerivateWrapFunction(pDerivateFunction);
		FunctionPoints vSamples = FunctionUtils.sample( vFunction, p.getMinX(), p.getMaxX(), 1.0d );
		
		UnivariateDifferentiableFunction vDerivateFunction = differentiator.differentiate( vFunction );
		UnivariateFunction aFunction = new DerivateWrapFunction(vDerivateFunction);
		FunctionPoints aSamples = FunctionUtils.sample( aFunction, vSamples.getMinX(), vSamples.getMaxX(), 1.0d );
		
		fp_v.addAll( vSamples );
		fp_a.addAll( aSamples );
	}

	
	private void generateFunctionsWithFiniteDifferencesDifferentiatorAndMultiplesInterpolations(
					FunctionPoints x, FunctionPoints fp_v, FunctionPoints fp_a ) {
		
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, x.getMinX(), x.getMaxX() );
		
		UnivariateDifferentiableFunction vFunction = differentiator.differentiate( x.interpolate() );
		FunctionPoints vSamples = FunctionUtils.sample( vFunction, 1, x.getMinX(), x.getMaxX(), 1.0d );
		
		UnivariateDifferentiableFunction aFunction = differentiator.differentiate( vSamples.interpolate() );
		FunctionPoints aSamples = FunctionUtils.sample( aFunction, 1, vSamples.getMinX(), vSamples.getMaxX(), 1.0d );
		
		fp_v.addAll( vSamples );
		fp_a.addAll( aSamples );
	}

	
	private void generateFunctionsWithDerivativeStructures(
							PolynomialSplineFunction interpolationFunction, double minX,
							double maxX, FunctionPoints ds_p, FunctionPoints ds_v,
							FunctionPoints ds_a) {
		for( double i = minX; i<maxX; i+=1.0d ) {
			DerivativeStructure dt = new DerivativeStructure(1, 2, 0, i);
			DerivativeStructure dx = interpolationFunction.value(dt);
			
			ds_p.add( i, dx.getValue() );
			ds_v.add( i, dx.getPartialDerivative(1) );
			ds_a.add( i, dx.getPartialDerivative(2) );
		}
	}

	private FunctionPoints derivate( String name, FunctionPoints points) {
		FunctionPoints ret = new FunctionPoints( name );
		Point previous = null;
		for ( Point p : points ) {
			if ( previous != null ) {
				double inc = p.getX() - previous.getX();
				double value = (p.getY() - previous.getY()) / inc;
				ret.add( previous.getX(), value );
			}
			previous = p;
		}
		return ret;
	}

	public static void main(String[] args) {
		try {
			VerifyDerivative prueba = 
					new VerifyDerivative( new File( "src/resources/jig/A_192.168.7.13_1358442748589.json" ) );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
