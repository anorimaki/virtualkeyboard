package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

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

public class ScalarFeatureDispersion {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" ),new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),new File( Environment.RESOURCES_DIR, "user4" ),new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),new File( Environment.RESOURCES_DIR, "user7" )};
			
	private File[] inputFolders;
	private Map<String,UserStatistic> results= new HashMap<String, UserStatistic>();
	private final int Nh = 10;

	public ScalarFeatureDispersion( File[] inputFolders) {
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
			results.put(inputFolder.getName(), aux.getStatistic(FeatureId.VELOCITY_Y_AVG));
		}
		
		
		
		FastScatterPlot dispPlot = generateDispersionPlot("user3");
		/*
		PiePlot featuresPlot = generateFeaturesPlot(results);
		PiePlot entropyPlot = generateFeaturesPlot(resultsE);
		Application application = new Application();
		//application.run( "Feature Quality", featuresPlot,"MANOVA Feature Quality Compare" );
		*/
		String[] titles={"Dispersion Y'_AVG"};
		ArrayList<FastScatterPlot> plots=new ArrayList<FastScatterPlot>();
		
		plots.add(dispPlot);
		Application application = new Application();
		application.run( "Feature Quality", plots,titles);		
	
	}
	
	private FastScatterPlot generateDispersionPlot(String userId) throws Exception {
		// http://www.java2s.com/Code/Java/Chart/JFreeChartFastScatterPlotDemo.htm
		
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		float[][] ds = createDataset(userId);

		FastScatterPlot plot = new FastScatterPlot(ds,xAxis,yAxis);
		
		//XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		//plot.setRenderer( renderer );
		
		return plot;
	}
	
	 private float[][] createDataset(String userId){
	        float[][] ds= new float[2][Nh];
	        
	        UserStatistic uS;
			FeatureStatistics aux;
			ArrayList<Double> h;
	        
	        uS=results.get(userId);
			double min=uS.getMin();
			double inc=uS.getHistogramInc(Nh);
			h=uS.getHistogram(Nh);
			//System.out.println("H("+usuari+" | "+inc+"): "+h.toString());
			for(int i=0;i<h.size();i++){
			  	ds[0][i]=(float)min;
			   	min+=inc;
			   	ds[1][i]=h.get(i).floatValue();
			 }
				        
	        return ds;
	    }

	

	public static void main(String[] args) {
		try {
			ScalarFeatureDispersion prueba = new ScalarFeatureDispersion( INPUT_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
