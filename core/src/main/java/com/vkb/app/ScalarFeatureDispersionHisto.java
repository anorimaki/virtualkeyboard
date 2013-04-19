package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.app.quality.ScalarFeaturesStatistics;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class ScalarFeatureDispersionHisto {
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
			
	private File[] inputFolders;
	
	// PARAMETRES HISTOGRAMA
	private static final int Nh = 8; // Nombre de columnes (estats discrets)
	private static final FeatureId[] scalarFeatures = { 
		FeatureId.POSITION_X_AVG, 
		FeatureId.POSITION_Y_AVG,
		FeatureId.VELOCITY_X_AVG, 
		FeatureId.VELOCITY_Y_AVG,
		FeatureId.ACCELERATION_X_AVG, 
		FeatureId.ACCELERATION_Y_AVG,
		FeatureId.AREA_X, FeatureId.AREA_Y, FeatureId.RELATION_AREA
	};

	public ScalarFeatureDispersionHisto( File[] inputFolders) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		Map<String, ScalarFeaturesStatistics> featuresStatistics = new HashMap<String, ScalarFeaturesStatistics>();
		for ( File inputFolder : inputFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(inputFolder);
			System.out.println("Fitxers llegits a "+inputFolder.getAbsolutePath()+": "+inputData.size());
			List<Signature> patternTraces = traceBuilder.build( inputData );
			ScalarFeaturesStatistics aux = new ScalarFeaturesStatistics( patternTraces );
			featuresStatistics.put( inputFolder.getName(), aux );
		}
		
		
		ArrayList<XYPlot> plots=new ArrayList<XYPlot>();
		List<String> titles = new ArrayList<String>();
		for( FeatureId feature : scalarFeatures ) {
			XYPlot dispPlot = generateDispersionPlot( featuresStatistics, feature );
			plots.add(dispPlot);
			
			titles.add( "Dispersion " + feature.getName() );
		}

		Application application = new Application();
		application.run( "Feature Quality", plots, titles.toArray( new String[0] ) );		
	
	}


	private XYPlot generateDispersionPlot( Map<String, ScalarFeaturesStatistics> featuresStatistics,
										FeatureId feature ) throws Exception {
		// http://code.google.com/p/test-chart/source/browse/trunk/src/main/java/es/efor/plandifor/demo/HistogramDemo1.java?r=2
		
		 JFreeChart jfreechart = ChartFactory.createHistogram( "Histogram", null, null,
				 				createDataset( featuresStatistics, feature ), 
				 				PlotOrientation.VERTICAL, true, true, false );
         XYPlot plot = (XYPlot)jfreechart.getPlot();
         plot.setForegroundAlpha(0.85F);
         XYBarRenderer xybarrenderer = (XYBarRenderer)plot.getRenderer();
         xybarrenderer.setDrawBarOutline(false);

		return plot;
	}
	
	private IntervalXYDataset createDataset( Map<String, ScalarFeaturesStatistics> featuresStatistics,
											FeatureId feature ){
		HistogramDataset histogramdataset = new HistogramDataset();
		for( Map.Entry<String, ScalarFeaturesStatistics> entry : featuresStatistics.entrySet() ) {
			DescriptiveStatistics statistics = entry.getValue().get(feature);
			String user = entry.getKey();
			histogramdataset.addSeries( user, statistics.getValues(), Nh, statistics.getMin(), statistics.getMax() );
		}
		
		return histogramdataset;
	}

	public static void main(String[] args) {
		try {
			ScalarFeatureDispersionHisto prueba = new ScalarFeatureDispersionHisto( INPUT_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
