package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.Feature;
import com.vkb.FunctionPoints;
import com.vkb.Point;
import com.vkb.RawTrace;
import com.vkb.Trace;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.FixedXIntervalFunctionParser;
import com.vkb.io.TraceFilesHelper;
import com.vkb.io.TraceParser;

public class VerifyInterpolation {
	
	private File inputFile;
	
	public VerifyInterpolation( File inputFile ) {
		this.inputFile = inputFile;
	}
	
	
	private void run() throws Exception {
		RawTrace rawTrace = new TraceParser().parse( inputFile );
		FunctionPoints jigInterpolatedX = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedX", 
										TraceFilesHelper.getInterpolatedFile( inputFile, "X" ) );
		FunctionPoints jigInterpolatedY = 
				new FixedXIntervalFunctionParser(1).parse( "interpolatedY", 
										TraceFilesHelper.getInterpolatedFile( inputFile, "Y" ) );
		
		Trace trace = new Trace(rawTrace); 
		
		XYPlot componentsPlot = generateComponentsPlot( trace, jigInterpolatedX, jigInterpolatedY );
		XYPlot tracesPlot = generateTracePlot( trace, jigInterpolatedX, jigInterpolatedY );
        
        Application application = new Application("VerifyInterpolation");
		application.run(componentsPlot, tracesPlot );
	}

	
	private XYPlot generateTracePlot(Trace trace, FunctionPoints jigInterpolatedX,
									FunctionPoints jigInterpolatedY) throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
        xAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis yAxis = new NumberAxis("Y");
        
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer( renderer );
        
        RawTrace originalTrace = trace.getRawTrace();
        FunctionPoints interpolatedX = trace.getFeature(Feature.POSITION_X).getSamples();
        FunctionPoints interpolatedY = trace.getFeature(Feature.POSITION_Y).getSamples();
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


	private XYPlot generateComponentsPlot(Trace trace, FunctionPoints jigInterpolatedX,
									FunctionPoints jigInterpolatedY) throws Exception {

		NumberAxis xAxis = new NumberAxis("time");
        xAxis.setAutoRangeIncludesZero(false);
		
        NumberAxis yAxis = new NumberAxis("value");
        
        XYPlot plotComponents = new XYPlot();
        plotComponents.setDomainAxis(xAxis);
        plotComponents.setRangeAxis(yAxis);
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plotComponents.setRenderer( renderer );

        FunctionPoints originalX = trace.getRawTrace().getXFunction();
        FunctionPoints interpolatedX = trace.getFeature(Feature.POSITION_X).getSamples();
        plotComponents.setDataset( 0, DataConvert.getDataset(originalX, jigInterpolatedX, interpolatedX) );
        
        FunctionPoints originalY = trace.getRawTrace().getYFunction();
        FunctionPoints interpolatedY = trace.getFeature(Feature.POSITION_Y).getSamples();
        plotComponents.setDataset( 1, DataConvert.getDataset(originalY, jigInterpolatedY, interpolatedY) );

        return plotComponents;
	}

	public static void main(String[] args) {
		try {
			VerifyInterpolation prueba = 
					new VerifyInterpolation( new File( "src/resources/jig/A_192.168.7.13_1358442748589.json" ) );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	
}
