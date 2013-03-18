package com.vkb.app;

import java.io.File;
import java.util.Arrays;

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

import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.CapturedDataParser;
import com.vkb.math.DerivateWrapFunction;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.FunctionUtils;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Trace;

public class VerifyDerivative {
	private File inputFile;
	
	public VerifyDerivative( File inputFile ) {
		this.inputFile = inputFile;
	}
	
	private void run() throws Exception {
		CapturedData capturedData = new CapturedDataParser().parse( inputFile );
		Trace trace = capturedData.getTrace();
		
		DiscreteFunction x = trace.getXFunction();
		
		LoessInterpolator interpolator = new LoessInterpolator( 0.05d, 0, LoessInterpolator.DEFAULT_ACCURACY );
		PolynomialSplineFunction interpolationFunction = interpolator.interpolate( x.getX(), x.getY() );
		
								//Derivate with interpolation and commons-math DerivativeStructures
		DiscreteFunction ds_p = new DiscreteFunction( "Interpolation + DerivativeStructure - " + FeatureId.POSITION_X.name() );
		DiscreteFunction ds_v = new DiscreteFunction( "Interpolation + DerivativeStructure - " + FeatureId.VELOCITY_X.name() );
		DiscreteFunction ds_a = new DiscreteFunction( "Interpolation + DerivativeStructure - " + FeatureId.ACCELERATION_X.name() );
		generateFunctionsWithDerivativeStructures( interpolationFunction, x.getMinX(), x.getMaxX(), ds_p, ds_v, ds_a );
		
								//Derivate with finite differences
		DiscreteFunction p = FunctionUtils.sample( FeatureId.POSITION_X.name(), 
												interpolationFunction, x.getMinX(), x.getMaxX(), 1.0d );
		DiscreteFunction v = derivate( "Finite differences - " + FeatureId.VELOCITY_X.name(), p );
		DiscreteFunction a = derivate( "Finite differences - " + FeatureId.ACCELERATION_X.name(), v );
		
								//Derivate with interpolation and commons-math FiniteDifferencesDifferentiator
		DiscreteFunction ids_v = new DiscreteFunction( "Interpolations + FiniteDifferencesDifferentiator - " + FeatureId.VELOCITY_X.name() );
		DiscreteFunction ids_a = new DiscreteFunction( "Interpolations + FiniteDifferencesDifferentiator - " + FeatureId.ACCELERATION_X.name() );
		generateFunctionsWithFiniteDifferencesDifferentiator( x, ids_v, ids_a );
		
								//Derivate with interpolation and commons-math FiniteDifferencesDifferentiator
		DiscreteFunction fp_v = new DiscreteFunction( "Multiples Interpolations + FiniteDifferencesDifferentiator - " + FeatureId.VELOCITY_X.name() );
		DiscreteFunction fp_a = new DiscreteFunction( "Multiples Interpolations + FiniteDifferencesDifferentiator - " + FeatureId.ACCELERATION_X.name() );
		generateFunctionsWithFiniteDifferencesDifferentiatorAndMultiplesInterpolations( x, fp_v, fp_a );
		
		XYPlot positionPlot = generatePlot( "Position", p, ds_p );
		XYPlot velocityPlot = generatePlot( "Velocity", v, ds_v, ids_v, fp_v );
		XYPlot accelerationPlot = generatePlot( "Accelereation", a, ds_a, ids_a, fp_a );
		
		Application application = new Application();
		application.run( "VerifyInterpolation", Arrays.asList( positionPlot, velocityPlot, accelerationPlot ) );
	}
		
		
	private XYPlot generatePlot( String yName, DiscreteFunction... f ) {
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
			DiscreteFunction p, DiscreteFunction fp_v, DiscreteFunction fp_a ) {
	
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, p.getMinX(), p.getMaxX() );
		
		UnivariateDifferentiableFunction pDerivateFunction = differentiator.differentiate( p.interpolate() );
		UnivariateFunction vFunction = new DerivateWrapFunction(pDerivateFunction);
		DiscreteFunction vSamples = FunctionUtils.sample( fp_v.getName(), vFunction,
												p.getMinX(), p.getMaxX(), 1.0d );
		
		UnivariateDifferentiableFunction vDerivateFunction = differentiator.differentiate( vFunction );
		UnivariateFunction aFunction = new DerivateWrapFunction(vDerivateFunction);
		DiscreteFunction aSamples = FunctionUtils.sample( fp_a.getName(), aFunction, 
												vSamples.getMinX(), vSamples.getMaxX(), 1.0d );
		
		fp_v.addAll( vSamples );
		fp_a.addAll( aSamples );
	}

	
	private void generateFunctionsWithFiniteDifferencesDifferentiatorAndMultiplesInterpolations(
					DiscreteFunction x, DiscreteFunction fp_v, DiscreteFunction fp_a ) {
		
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 6, 0.1d, x.getMinX(), x.getMaxX() );
		
		UnivariateDifferentiableFunction vFunction = differentiator.differentiate( x.interpolate() );
		DiscreteFunction vSamples = FunctionUtils.sample( fp_v.getName(), vFunction, 
											1, x.getMinX(), x.getMaxX(), 1.0d );
		
		UnivariateDifferentiableFunction aFunction = differentiator.differentiate( vSamples.interpolate() );
		DiscreteFunction aSamples = FunctionUtils.sample( fp_a.getName(), aFunction, 
											1, vSamples.getMinX(), vSamples.getMaxX(), 1.0d );
		
		fp_v.addAll( vSamples );
		fp_a.addAll( aSamples );
	}

	
	private void generateFunctionsWithDerivativeStructures(
							PolynomialSplineFunction interpolationFunction, double minX,
							double maxX, DiscreteFunction ds_p, DiscreteFunction ds_v,
							DiscreteFunction ds_a) {
		for( double i = minX; i<maxX; i+=1.0d ) {
			DerivativeStructure dt = new DerivativeStructure(1, 2, 0, i);
			DerivativeStructure dx = interpolationFunction.value(dt);
			
			ds_p.add( i, dx.getValue() );
			ds_v.add( i, dx.getPartialDerivative(1) );
			ds_a.add( i, dx.getPartialDerivative(2) );
		}
	}

	private DiscreteFunction derivate( String name, DiscreteFunction function) {
		DiscreteFunction ret = new DiscreteFunction( name );
		DiscreteFunction.Point previous = null;
		for ( DiscreteFunction.Point p : function.getPoints() ) {
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
			File inputFile = new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442748589.json" );
			VerifyDerivative prueba = new VerifyDerivative( inputFile );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
