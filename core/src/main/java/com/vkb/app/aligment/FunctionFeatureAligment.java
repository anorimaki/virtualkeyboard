package com.vkb.app.aligment;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.gui.LineStroke;
import com.vkb.io.CapturedDatasParser;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.dtw.EuclideanFunctionDistanceCalculator;
import com.vkb.math.dtw.FunctionWarper;
import com.vkb.math.dtw.WarpPath;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class FunctionFeatureAligment {
	private File inputFolder1;
	private File inputFolder2;
	private FeatureId featureId;
	
	public FunctionFeatureAligment( File inputFolder1, File inputFolder2, 
								FeatureId featureId ) {
		this.inputFolder1 = inputFolder1;
		this.inputFolder2 = inputFolder2;
		this.featureId = featureId;
	}
	

	public void run( Pair<String,FunctionFeatureAligmentMode>... modes ) throws Exception {
		run( Arrays.asList(modes) );
	}
		
	public void run( Iterable<Pair<String,FunctionFeatureAligmentMode>> modes ) throws Exception {
		CapturedDatasParser parser = new CapturedDatasParser();
		List<CapturedData> capturedDatas1 = parser.parse( inputFolder1 ) ;
		List<CapturedData> capturedDatas2 = parser.parse( inputFolder2 ) ;
		
		GenericSignatureBuilder traceBuilder = new DefaultSignatureBuilder();
		List<Signature> traces1 = traceBuilder.buildSignatures(capturedDatas1);
		List<Signature> traces2 = traceBuilder.buildSignatures(capturedDatas2);
		
		Application application = new Application();
		for( Pair<String,FunctionFeatureAligmentMode> aligmentMode : modes ) {
			List<XYPlot> plots = new ArrayList<XYPlot>();
	//		plots.add( apply( aligmentMode.getSecond(), traces1.get(0), traces1.get(1) ) );		//Same user
	//		plots.add( apply( aligmentMode.getSecond(), traces1.get(0), traces2.get(0) ) );		//Different user
			plots.add( apply( aligmentMode.getSecond(), traces2.get(1), traces2.get(2) ) );		//Same user
			plots.add( apply( aligmentMode.getSecond(), traces1.get(2), traces2.get(2) ) );		//Different user

			application.start( aligmentMode.getFirst(), plots );
		}
		
		application.join();
	}
	
		
	private XYPlot apply( FunctionFeatureAligmentMode aligmentMode, Signature signature1,
							Signature signature2 ) throws Exception {
		FunctionFeatureData fv1 = signature1.getFeature( featureId ).getData();
		DiscreteFunction f1 = fv1.getSamples();
		FunctionFeatureData fv2 = signature2.getFeature( featureId ).getData();
		DiscreteFunction f2 = fv2.getSamples();
		
		FunctionWarper.Result result = aligmentMode.apply( signature1, signature2 );
		
		return generatePlotAndShow( f1, f2, result );
	}
	
	
	private XYPlot generatePlotAndShow( DiscreteFunction f1, DiscreteFunction f2, FunctionWarper.Result result ) throws Exception {
		Pair<DiscreteFunction, DiscreteFunction> warpedFunctions = result.getPath().warp(f1, f2);
		
		DiscreteFunction warpedF1 = warpedFunctions.getFirst();
		DiscreteFunction warpedF2 = warpedFunctions.getSecond();

		double newDistance = new EuclideanFunctionDistanceCalculator().calculate( warpedF1, warpedF2 );
		
		int idealPathLength = Math.max( f1.size(), f2.size() );
		
		System.out.println( "********************************************" );
		System.out.println( "WT distance: " + result.getDistance() );
		System.out.println( "Calculated distance: " + newDistance );
		System.out.println( "F1 size: " + f1.size() );
		System.out.println( "F2 size: " + f2.size() );
		System.out.println( "Max path length: " + (f1.size()+f2.size()) );
		System.out.println( "Ideal path length: " + idealPathLength );
		System.out.println( "Path length: " + result.getPath().size() );
	//	System.out.println( "Warp cost: " + result.getWarpCost() );
		
		return generatePlot( Arrays.asList( f1, f2 ), Arrays.asList( warpedF1, warpedF2 ),
							result.getPath() );
	}
	
	
	private XYPlot generatePlot( List<DiscreteFunction> functions,
								List<DiscreteFunction> warppedFunctions,
								WarpPath path ) {
		
		XYPlot plot = new XYPlot();
		
		setFuntionsData( plot, functions, warppedFunctions );
		setWarpPathData( plot, path );
		
		return plot;
	}
	
	
	private void setWarpPathData(XYPlot plot, WarpPath path) {
		NumberAxis xAxis = new NumberAxis("I");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("J");
		xAxis.setAutoRangeIncludesZero(false);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		
		XYSeries xySeries = new XYSeries( "WarpPath", false);
		for( WarpPath.Cell cell :  path.getCells() ) {
			xySeries.add( cell.getColumn(), cell.getRow() );
		}
		
		XYSeriesCollection data2 = new XYSeriesCollection();
		data2.addSeries( xySeries );

		plot.setDomainAxis( 2, xAxis );
		plot.setRangeAxis( 2, yAxis );
		plot.setRenderer( 2, renderer );
		plot.setDataset( 2, data2 );
		plot.mapDatasetToRangeAxis( 2, 2 );
		plot.mapDatasetToDomainAxis( 2, 2 );
	}


	private void setFuntionsData(XYPlot plot, List<DiscreteFunction> functions,
									List<DiscreteFunction> warppedFunctions) {
		final Color colors[] = { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE };
		
		NumberAxis xAxis = new NumberAxis("Time");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis( functions.get(0).getName() );
		xAxis.setAutoRangeIncludesZero(false);
		
		XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer(true, false);
		for ( int i=0; i<functions.size(); i++ ) {
			renderer0.setSeriesPaint( i, colors[i] );
			renderer0.setSeriesStroke( i, LineStroke.SOLID.getStroke() );
		}
		renderer0.setDrawSeriesLineAsPath(true);
		XYDataset data0 = DataConvert.getDatasetFromFunctions( functions );
		plot.setDomainAxis( 0, xAxis );
		plot.setRangeAxis( 0, yAxis);
		plot.setRenderer( 0, renderer0 );
		plot.setDataset( 0, data0 );
		plot.mapDatasetToRangeAxis( 0, 0 );
		
		XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
		for ( int i=0; i<functions.size(); i++ ) {
			renderer1.setSeriesPaint( i, colors[i] );
			renderer1.setSeriesStroke( i, LineStroke.DOTTED.getStroke() );
		}
		renderer1.setDrawSeriesLineAsPath(true);
		XYDataset data1 = DataConvert.getDatasetFromFunctions( warppedFunctions );
		plot.setDomainAxis( 1, xAxis );
		plot.setRangeAxis( 1, yAxis);
		plot.setRenderer( 1, renderer1 );
		plot.setDataset( 1, data1);
		plot.mapDatasetToRangeAxis( 1, 1 );
	}
}
