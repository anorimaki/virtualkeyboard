package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.FixedXIntervalFunctionParser;
import com.vkb.io.CapturedDataFilesHelper;
import com.vkb.io.CapturedDataParser;
import com.vkb.math.FunctionPoints;
import com.vkb.math.Point;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureType;
import com.vkb.model.Features;
import com.vkb.model.Signature;
import com.vkb.model.Trace;

public class VerifyInterpolation {
	
	private File inputFile;
	
	public VerifyInterpolation( File inputFile ) {
		this.inputFile = inputFile;
	}
	
	
	private void run() throws Exception {
		CapturedData capturedData = new CapturedDataParser().parse( inputFile );
		FunctionPoints jigInterpolatedX = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedX", 
										CapturedDataFilesHelper.getInterpolatedFile( inputFile, "X" ) );
		FunctionPoints jigInterpolatedY = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedY", 
										CapturedDataFilesHelper.getInterpolatedFile( inputFile, "Y" ) );
		
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		Features features = featuresExtractor.extract(capturedData);
		Signature signature = new Signature( capturedData, features ); 
		
		XYPlot componentsPlot = generateComponentsPlot( signature, jigInterpolatedX, jigInterpolatedY );
		XYPlot tracesPlot = generateTracePlot( signature, jigInterpolatedX, jigInterpolatedY );
        
        Application application = new Application("VerifyInterpolation");
		application.run(componentsPlot, tracesPlot );
	}

	
	private XYPlot generateTracePlot(Signature signature, FunctionPoints jigInterpolatedX,
									FunctionPoints jigInterpolatedY) throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
        xAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis yAxis = new NumberAxis("Y");
        
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer( renderer );
        
        Trace originalTrace = signature.getCapturedData().getTrace();
        FunctionPoints interpolatedX = signature.getFeature(FeatureType.POSITION_X).getSamples();
        FunctionPoints interpolatedY = signature.getFeature(FeatureType.POSITION_Y).getSamples();
        List<Point> interpolatedTrace = composeXY( interpolatedX, interpolatedY );
        List<Point> jigInterpolatedTrace = composeXY( jigInterpolatedX, jigInterpolatedY );
        
        XYSeriesCollection compleTraces = new XYSeriesCollection();
        compleTraces.addSeries( DataConvert.getXYSeries( "Original", originalTrace.getTracePoints() ) );
        compleTraces.addSeries( DataConvert.getXYSeries( "JIG Interpolated", jigInterpolatedTrace ) );
        compleTraces.addSeries( DataConvert.getXYSeries( "Interpolated", interpolatedTrace ) );		
        plot.setDataset( 0, compleTraces );
        
        return plot;
	}
	

	private List<Point> composeXY( FunctionPoints x, FunctionPoints y ) throws Exception {
		if ( x.size() != y.size() )
			throw new Exception( "Can't compose funcion sample with different length" );
		List<Point> ret = new ArrayList<Point>();
		for( int i = 0; i<x.size(); ++i ) {
			Point xp = x.get(i);
			Point yp = y.get(i);
			if ( xp.getX() != xp.getX() ) {
				throw new Exception( "Funtions to compose missmatch X value at position "
									+ i + "(" + xp.getX() + "!=" + yp.getY() );
			}
			ret.add( new Point( xp.getY(), yp.getY() ) );
		}
		return ret;
	}


	private XYPlot generateComponentsPlot(Signature signature, FunctionPoints jigInterpolatedX,
									FunctionPoints jigInterpolatedY) throws Exception {

		NumberAxis xAxis = new NumberAxis("time");
        xAxis.setAutoRangeIncludesZero(false);
		
        NumberAxis yAxis = new NumberAxis("value");
        
        XYPlot plotComponents = new XYPlot();
        plotComponents.setDomainAxis(xAxis);
        plotComponents.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plotComponents.setRenderer( renderer );

        Trace trace0 = signature.getCapturedData().getTrace();
        
        FunctionPoints originalX = trace0.getXFunction();
        FunctionPoints interpolatedX = signature.getFeature(FeatureType.POSITION_X).getSamples();
        plotComponents.setDataset( 0, DataConvert.getDataset(originalX, jigInterpolatedX, interpolatedX) );
        
        FunctionPoints originalY = trace0.getYFunction();
        FunctionPoints interpolatedY = signature.getFeature(FeatureType.POSITION_Y).getSamples();
        plotComponents.setDataset( 1, DataConvert.getDataset(originalY, jigInterpolatedY, interpolatedY) );

        return plotComponents;
	}

	public static void main(String[] args) {
		try {
			File inputFile = new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442748589.json" );
			VerifyInterpolation prueba = new VerifyInterpolation( inputFile );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	
}
