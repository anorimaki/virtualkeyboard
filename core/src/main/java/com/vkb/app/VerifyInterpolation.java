package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.function.Constant;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.FixedXIntervalFunctionParser;
import com.vkb.io.CapturedDataFilesHelper;
import com.vkb.io.CapturedDataParser;
import com.vkb.math.DiscreteFunction;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Features;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Trace;

public class VerifyInterpolation {
	
	private File inputFile;
	
	public VerifyInterpolation( File inputFile ) {
		this.inputFile = inputFile;
	}
	
	
	private void run() throws Exception {
		CapturedData capturedData = new CapturedDataParser().parse( inputFile );
		DiscreteFunction jigInterpolatedX = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedX", 
										CapturedDataFilesHelper.getInterpolatedFile( inputFile, "X" ) );
		DiscreteFunction jigInterpolatedY = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedY", 
										CapturedDataFilesHelper.getInterpolatedFile( inputFile, "Y" ) );
		
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		Features features = featuresExtractor.extract(capturedData);
		Signature signature = new Signature( capturedData, features ); 
		
		XYPlot componentsPlot = generateComponentsPlot( signature, jigInterpolatedX, jigInterpolatedY );
		XYPlot tracesPlot = generateTracePlot( signature, jigInterpolatedX, jigInterpolatedY );
        
        Application application = new Application();
		application.run( "VerifyInterpolation", Arrays.asList( componentsPlot, tracesPlot ) );
	}

	
	private XYPlot generateTracePlot(Signature signature, DiscreteFunction jigInterpolatedX,
									DiscreteFunction jigInterpolatedY) throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
        xAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis yAxis = new NumberAxis("Y");
        
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer( renderer );
        
        Trace originalTrace = signature.getCapturedData().getTrace();
        DiscreteFunction originalPositionFunction = originalTrace.getPositionFunction();
        originalPositionFunction = originalPositionFunction.multiply( new Constant(-1) );
        
        FunctionFeatureData xFeatureData = signature.getFeature( FeatureId.POSITION_X ).getData();
        DiscreteFunction interpolatedX = xFeatureData.getSamples();
        FunctionFeatureData yFeatureData = signature.getFeature( FeatureId.POSITION_Y ).getData();
        DiscreteFunction interpolatedY = yFeatureData.getSamples();
        interpolatedY = interpolatedY.multiply( new Constant(-1) );
        List<DiscreteFunction.Point> interpolatedTrace = composeXY( interpolatedX, interpolatedY );
        
        jigInterpolatedY = jigInterpolatedY.multiply( new Constant(-1) );
        List<DiscreteFunction.Point> jigInterpolatedTrace = composeXY( jigInterpolatedX, jigInterpolatedY );
        
        XYSeriesCollection compleTraces = new XYSeriesCollection();
        compleTraces.addSeries( DataConvert.getXYSeries( "Original", originalPositionFunction.getPoints() ) );
        compleTraces.addSeries( DataConvert.getXYSeries( "JIG Interpolated", jigInterpolatedTrace ) );
        compleTraces.addSeries( DataConvert.getXYSeries( "Interpolated", interpolatedTrace ) );		
        plot.setDataset( 0, compleTraces );
        
        return plot;
	}
	

	private List<DiscreteFunction.Point> composeXY( DiscreteFunction x, DiscreteFunction y ) throws Exception {
		if ( x.size() != y.size() )
			throw new Exception( "Can't compose funcion sample with different length" );
		List<DiscreteFunction.Point> ret = new ArrayList<DiscreteFunction.Point>();
		for( int i = 0; i<x.size(); ++i ) {
			DiscreteFunction.Point xp = x.get(i);
			DiscreteFunction.Point yp = y.get(i);
			if ( xp.getX() != xp.getX() ) {
				throw new Exception( "Funtions to compose missmatch X value at position "
									+ i + "(" + xp.getX() + "!=" + yp.getY() );
			}
			ret.add( new DiscreteFunction.Point( xp.getY(), yp.getY() ) );
		}
		return ret;
	}


	private XYPlot generateComponentsPlot(Signature signature, DiscreteFunction jigInterpolatedX,
									DiscreteFunction jigInterpolatedY) throws Exception {

		NumberAxis xAxis = new NumberAxis("time");
        xAxis.setAutoRangeIncludesZero(false);
		
        NumberAxis yAxis = new NumberAxis("value");
        
        XYPlot plotComponents = new XYPlot();
        plotComponents.setDomainAxis(xAxis);
        plotComponents.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plotComponents.setRenderer( renderer );

        Trace trace0 = signature.getCapturedData().getTrace();
        
        DiscreteFunction originalX = trace0.getXFunction();
        FunctionFeatureData xFeatureData = signature.getFeature( FeatureId.POSITION_X ).getData();
        DiscreteFunction interpolatedX = xFeatureData.getSamples();
        plotComponents.setDataset( 0, DataConvert.getDataset(originalX, jigInterpolatedX, interpolatedX) );
        
        DiscreteFunction originalY = trace0.getYFunction();
        FunctionFeatureData yFeatureData = signature.getFeature( FeatureId.POSITION_Y ).getData();
        DiscreteFunction interpolatedY = yFeatureData.getSamples();
        plotComponents.setDataset( 1, DataConvert.getDataset(originalY, jigInterpolatedY, interpolatedY) );

        return plotComponents;
	}

	public static void main(String[] args) {
		try {
			File inputFile = new File( Environment.RESOURCES_DIR, "old/user1/A_192.168.7.13_1358442748589.json" );
			VerifyInterpolation prueba = new VerifyInterpolation( inputFile );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	
}
