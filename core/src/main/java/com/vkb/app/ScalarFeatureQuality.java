package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.app.quality.FeatureQualityEntropy;
import com.vkb.app.quality.FeatureQualityManova;
import com.vkb.app.quality.ScalarFeaturesStatistics;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class ScalarFeatureQuality {
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
			
	private File[] inputFolders;
	private Map<FeatureId,Double> results= new HashMap<FeatureId, Double>();
	private Map<FeatureId,Double> resultsE= new HashMap<FeatureId, Double>();
	
	private static final FeatureId[] scalarFeatures = { 
		FeatureId.POSITION_X_AVG, FeatureId.POSITION_Y_AVG,
		FeatureId.VELOCITY_X_AVG, FeatureId.VELOCITY_Y_AVG,
		FeatureId.ACCELERATION_X_AVG, FeatureId.ACCELERATION_Y_AVG,
		FeatureId.AREA_X, FeatureId.AREA_Y, FeatureId.RELATION_AREA
	};
			
	public ScalarFeatureQuality( File[] inputFolders) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		FeatureQualityManova fQM = new FeatureQualityManova();
		FeatureQualityEntropy fQE = new FeatureQualityEntropy();
		
		// Cal crear un hash <usuari,UsersStatistics> per cada lectura de directori
		for ( File inputFolder : inputFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(inputFolder);
			System.out.println("Fitxers llegits a "+inputFolder.getAbsolutePath()+": "+inputData.size());
			List<Signature> patternTraces = traceBuilder.build( inputData );
			ScalarFeaturesStatistics aux = new ScalarFeaturesStatistics( patternTraces );
			fQM.add( inputFolder.getName(), aux );
			fQE.add( inputFolder.getName(), aux );
		}
		
		double f=0.0;
		for( FeatureId feature : scalarFeatures ) {
			f=fQM.calculate(feature);
			results.put(feature, new Double(f));
			System.out.println("Feature Quality "+feature+": "+f);
		}
		
		double r=0.0;
		for( FeatureId feature : scalarFeatures ) {
			r=fQE.calculate(feature);
			resultsE.put(feature, new Double(Math.abs(r)));
			System.out.println("Feature Entropy "+feature+": "+r);
		}
	
		PiePlot featuresPlot = generateFeaturesPlot(results);
		PiePlot entropyPlot = generateFeaturesPlot(resultsE);
		
		ArrayList<PiePlot> plots=new ArrayList<PiePlot>();
		plots.add(featuresPlot);
		plots.add(entropyPlot);
		
		String[] titles={"MANOVA Feature Quality Compare","ENTROPY  FeatureQuality Compare"};
		Application application = new Application();
		application.run( "Feature Quality", plots,titles);		
	}
	
	
	private PiePlot generateFeaturesPlot(Map<FeatureId, Double> resultsP) throws Exception {
		PiePlot plot = new PiePlot();
		PieDataset pD = createDataset(resultsP); 

		plot.setDataset(pD);
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
		return plot;
	}
	

    private PieDataset createDataset(Map<FeatureId, Double> resultsP) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for( FeatureId feature : scalarFeatures ) {
        	dataset.setValue( feature.toString(), resultsP.get(feature) );	
        }
        return dataset;        
    }

	
	public static void main(String[] args) {
		try {
			ScalarFeatureQuality prueba = new ScalarFeatureQuality( INPUT_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
