package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;


import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.GenericSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.determine.PatternsStatistics;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.app.quality.UserStatistic;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.app.quality.*;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class ScalarFeatureDispersionHisto {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" ),new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),new File( Environment.RESOURCES_DIR, "user4" ),new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),new File( Environment.RESOURCES_DIR, "user7" )};
			
	private File[] inputFolders;
	private Map<String,UserStatistic> results= new HashMap<String, UserStatistic>();
	
	// PARAMETRES HISTOGRAMA
	private final int Nh = 8; // Nombre de columnes (estats discrets)
	private final FeatureId FEATURE = FeatureId.VELOCITY_Y_AVG;

	public ScalarFeatureDispersionHisto( File[] inputFolders) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		List<CapturedData> inputData=null;
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		List<Signature> patternTraces;
		FeatureStatistics aux;
		
				
		for ( File inputFolder : inputFolders ) {
			inputData = inputDataParser.parse(inputFolder);
			System.out.println("Fitxers llegits a "+inputFolder.getAbsolutePath()+": "+inputData.size());
			patternTraces = traceBuilder.build( inputData );
			aux=new FeatureStatistics(inputFolder.getName(),patternTraces);
			results.put(inputFolder.getName(), aux.getStatistic(FEATURE));
		}
		
		
		
		XYPlot dispPlot = generateDispersionPlot();
		String[] titles={"Dispersion "+FEATURE};
		ArrayList<XYPlot> plots=new ArrayList<XYPlot>();
		
		plots.add(dispPlot);
		Application application = new Application();
		application.run( "Feature Quality", plots,titles);		
	
	}
	
	private XYPlot generateDispersionPlot() throws Exception {
		// http://code.google.com/p/test-chart/source/browse/trunk/src/main/java/es/efor/plandifor/demo/HistogramDemo1.java?r=2
		
		 JFreeChart jfreechart = ChartFactory.createHistogram("Histogram", null, null, createDataset(), PlotOrientation.VERTICAL, true, true, false);
         XYPlot plot = (XYPlot)jfreechart.getPlot();
         plot.setForegroundAlpha(0.85F);
         XYBarRenderer xybarrenderer = (XYBarRenderer)plot.getRenderer();
         xybarrenderer.setDrawBarOutline(false);

		return plot;
	}
	
	private IntervalXYDataset createDataset(){
		UserStatistic uS;
		ArrayList<Double> samples;
		String user;
		double ad[];
		 
		HistogramDataset histogramdataset = new HistogramDataset();
         
		Iterator<String> it=results.keySet().iterator();
		while (it.hasNext()){
			user = it.next();
			uS=results.get(user);
			samples=uS.getSamples();
			
			// Creem una serie per cada usuari, amb color diferents
			System.out.println("Mostre("+user+"):"+samples.toString());
			ad=new double[(int)samples.size()];
			for (int i=0;i<samples.size();i++)
				ad[i]=samples.get(i);
			
			histogramdataset.addSeries(user, ad, Nh, uS.getMin(), uS.getMax());
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
