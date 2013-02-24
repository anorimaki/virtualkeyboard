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
import com.vkb.alg.SignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.SignatureComparator;
import com.vkb.app.util.SignaturesComparators;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureType;
import com.vkb.model.Signature;

public class VerifyDWT {
	private static final String BLANKS = "                                ";
	private static final File INPUT_FOLDER = new File( Environment.RESOURCES_DIR, "user1" );

	private File inputFolder;
	
	public VerifyDWT( File inputFile ) {
		this.inputFolder = inputFile;
	}
	
	
	private void run() throws Exception {
		CapturedDatasParser parser = new CapturedDatasParser();
		List<CapturedData> rawTraces = parser.parse( inputFolder) ;
		
		SignatureBuilder traceBuilder = new DefaultSignatureBuilder();
		List<Signature> traces = traceBuilder.build(rawTraces);
		
		SignaturesComparators tracesComparator  = new SignaturesComparators();
		SignaturesComparators.Result result = tracesComparator.compare(traces);
		
		for( int i=0; i<traces.size(); ++i ) {
			System.out.println( "Trace " + i + ": " );
			print( result.trace(i) );
		}
		
		printTraces(traces);
	}
	
	
	private void print( SignatureComparator.Result[] results ) {
		for( int i=0; i<results.length; ++i ) {
			SignatureComparator.Result result = results[i];
			if ( result != null ) {
				System.out.println( tab(1) + "- With trace " + i );
				
				for( Map.Entry<FeatureType, TimeWarpInfo> featureResult : result.getPartialResults().entrySet() ) {
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


	private void printTraces( List<Signature> traces ) {
		final Color colors[] = { Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE };
		//final Feature featuresToPrint[] = { Feature.VELOCITY_X, Feature.VELOCITY_Y, Feature.ACCELERATION_X, Feature.ACCELERATION_Y };
		final FeatureType featuresToPrint[] = { FeatureType.ACCELERATION_X, FeatureType.ACCELERATION_Y };
		
		NumberAxis xAxis = new NumberAxis("Time");
		xAxis.setAutoRangeIncludesZero(false);
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		
		int featureIndex=0;
		for( FeatureType feature : featuresToPrint ) {
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
