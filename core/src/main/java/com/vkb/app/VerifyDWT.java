package com.vkb.app;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fastdtw.dtw.TimeWarpInfo;
import com.vkb.Feature;
import com.vkb.RawTrace;
import com.vkb.Trace;
import com.vkb.alg.TraceBuilder;
import com.vkb.alg.TraceComparator;
import com.vkb.alg.TracesComparators;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.TracesParser;

public class VerifyDWT {
	private static final String BLANKS = "                                ";
	private static final File INPUT_FOLDER = new File( "src/resources/jig" );

	private File inputFolder;
	
	public VerifyDWT( File inputFile ) {
		this.inputFolder = inputFile;
	}
	
	
	private void run() throws Exception {
		TracesParser parser = new TracesParser();
		List<RawTrace> rawTraces = parser.parse( inputFolder) ;
		
		TraceBuilder traceBuilder = new TraceBuilder();
		List<Trace> traces = traceBuilder.build(rawTraces);
		
		TracesComparators tracesComparator  = new TracesComparators();
		TracesComparators.Result result = tracesComparator.compare(traces);
		
		for( int i=0; i<traces.size(); ++i ) {
			System.out.println( "Trace " + i + ": " );
			print( result.trace(i) );
		}
		
		printTraces(traces);
	}
	
	
	private void print( TraceComparator.Result[] results ) {
		for( int i=0; i<results.length; ++i ) {
			TraceComparator.Result result = results[i];
			if ( result != null ) {
				System.out.println( tab(1) + "- With trace " + i );
				
				for( Map.Entry<Feature, TimeWarpInfo> featureResult : result.getPartialResults().entrySet() ) {
					System.out.println( tab(2) + featureResult.getKey() + ": " + 
							featureResult.getValue().getDistance() );
				}
				
				System.out.println( tab(2) + "Global: " +
									result.getGlobalResult().getDistance() );
								
				System.out.println();
			}
		}
	}
	
	
	private static String tab(int i) {
		return BLANKS.substring(0, i*2);
	}


	private void printTraces( List<Trace> traces ) {
		final Color colors[] = { Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE };
		//final Feature featuresToPrint[] = { Feature.VELOCITY_X, Feature.VELOCITY_Y, Feature.ACCELERATION_X, Feature.ACCELERATION_Y };
		final Feature featuresToPrint[] = { Feature.ACCELERATION_X, Feature.ACCELERATION_Y };
		
		NumberAxis xAxis = new NumberAxis("Time");
		xAxis.setAutoRangeIncludesZero(false);
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		
		int featureIndex=0;
		for( Feature feature : featuresToPrint ) {
			XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
			
			Color color = colors[featureIndex];
			XYSeriesCollection seriesCollection = new XYSeriesCollection();
        	
			for( int traceIndex=0; traceIndex<traces.size(); ++traceIndex ) {
				renderer.setSeriesPaint(traceIndex, color);
				
				XYSeries series = DataConvert.getXYSeries( traces.get(traceIndex).getFeature(feature) );
				series.setKey( feature.getName() + "_" + traceIndex );
				seriesCollection.addSeries( series );
			}
			
			plot.setRangeAxis( featureIndex, new NumberAxis(feature.getName()) );
			plot.setRenderer( featureIndex, renderer );
			plot.setDataset( featureIndex, seriesCollection );
			plot.mapDatasetToRangeAxis( featureIndex, featureIndex );
			
			++featureIndex;
		}
        
        Application application = new Application("VerifyDWT");
		application.run(plot);
	}
		

	public static void main(String[] args) {
		try {
			VerifyDWT prueba = new VerifyDWT( INPUT_FOLDER );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
