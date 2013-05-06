package com.vkb.app;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.OutlierFeatureSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class AppEvaluation {
	private static final File OUTPUT_FILE = null; //new File("src/main/resources/quality.txt");
		
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
	
	private static final File CHECK_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "CapturaUser1" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser2" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser3" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser4" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser5" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser6" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser7" ) };
	
	private File[] inputFolders;
	private File[] checkFolders;
	
	private Writer output;
	private static double ThresholdsToCheck[] = { 0.05d, 0.1d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.45d,
											0.5d, 0.55d, 0.60d, 0.65d, 0.7d, 0.75d, 0.8d, 0.85d, 0.9d, 0.95d };
	
	private SignatureBuilder signatureBuilder;
	private static int NTHREADS = 8;
	
		
	public AppEvaluation( File[] inputFolders, File[] checkFolders ) throws Exception {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
		
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		signatureBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
	}
	
	private static class SignaturesChecker implements Callable<List<boolean[]>> {
		private OutlierFeatureSignatureValidator validator;
		private List<Signature> signaturesToCheck;
		
		private SignaturesChecker( OutlierFeatureSignatureValidator validator, List<Signature> signaturesToCheck ) {
			this.validator = validator;
			this.signaturesToCheck = signaturesToCheck;
			
		}

		@Override
		public List<boolean[]> call() throws Exception {
			List<boolean[]> result = new ArrayList<boolean[]>();
			
			for ( int i = 0; i<ThresholdsToCheck.length; ++i ) {
				boolean[] partialResult = new boolean[signaturesToCheck.size()];
				validator.setThreshold(ThresholdsToCheck[i]);
				
				for ( int j = 0; j<signaturesToCheck.size(); ++j ) {
					partialResult[j] = validator.check( signaturesToCheck.get(j) );
				}
				result.add(partialResult);
			}
			
			return result;
		}
	}
	
	private static class ResultAccesor {
		private List<Future<List<boolean[]>>> data;
		
		public ResultAccesor( List<Future<List<boolean[]>>> data ) {
			this.data = data;
		}
		
		public boolean get( int thresholdIndex, int validatorIndex, int signatureToCheckIndex ) throws Exception {
			return data.get( validatorIndex ).get().get( thresholdIndex )[ signatureToCheckIndex ];
		}
	}
	
	
	// La idea es generar una matriu on cada usuari es validi contra 
	// totes les signatures. Per cada usuari suposarem un intent que sera
	// el primer fitxer del seu directori de signatures...
	private void run() throws Exception {
		if ( OUTPUT_FILE != null ) {
			output = new FileWriter(OUTPUT_FILE);
		}
		else {
			output = new OutputStreamWriter( System.out );
		}
		
		long startedTime = System.currentTimeMillis();

		ExecutorService executor = Executors.newFixedThreadPool( NTHREADS );
		
		List<OutlierFeatureSignatureValidator> validators = generateValidators( executor );
		List<Signature> signaturesToCheck = generateSignaturesToCheck();
		
		System.out.println("Inici calcul FAR/FRR");
		System.out.println("---------------------");
		
		List<Future<List<boolean[]>>> futures  = new ArrayList<Future<List<boolean[]>>>();
		for ( OutlierFeatureSignatureValidator validator : validators ) {
			futures.add( executor.submit( new SignaturesChecker(validator, signaturesToCheck) ) );
		}
		ResultAccesor result = new ResultAccesor( futures );

		// Generacio de les grafiques
		XYPlot tracesPlot = generateBasePlot();
		XYSeriesCollection plotTraces = processResutls( result );
		
		long executionTime = System.currentTimeMillis() - startedTime;
		System.out.println("Tiempo de ejecicion: "+executionTime);
		
		if ( OUTPUT_FILE != null ) {
			output.close();
		}
		
		tracesPlot.setDataset( 0, plotTraces );
		Application application = new Application();
		application.run( "FAR/FRR Graphics", tracesPlot );
	}
	
	//Index in result: pattern, threshold, checkedSignature 
	private XYSeriesCollection processResutls( ResultAccesor result ) throws Exception{
		XYSeries xySeriesFar = new XYSeries("FAR", false);
		XYSeries xySeriesFrr = new XYSeries("FRR", false);
		
		for ( int i=0; i<ThresholdsToCheck.length; ++i ) {
			double currentThreshold = ThresholdsToCheck[i];
			
			double far = calculateFar( result, i );
			double frr = calculateFrr( result, i );
			
			displayMatrix( result, i, far, frr );
			
			xySeriesFar.add( currentThreshold, far );
			xySeriesFrr.add( currentThreshold, frr );
		}
		
		XYSeriesCollection ret = new XYSeriesCollection();
		ret.addSeries(xySeriesFar);
		ret.addSeries(xySeriesFrr);
		return ret;
	}


	private double calculateFrr( ResultAccesor result, int thresholdIndex ) throws Exception {
		double frr=0.0;
		for ( int i=0; i<inputFolders.length; i++ ) {
			if( !result.get( thresholdIndex, i, i ) ) {
				frr++;
			}
		}
		frr = frr/(double)inputFolders.length;
		return frr;
	}
	

	private double calculateFar( ResultAccesor result, int thresholdIndex ) throws Exception {
		double far=0.0;
		for ( int i=0; i<inputFolders.length; i++ ) {
			for ( int j=0; j<checkFolders.length; ++j ) {
				if( (i!=j) && result.get( thresholdIndex, i, j ) ) {
					far++;
				}
			}
		}
		far = far/(double)((inputFolders.length-1)*checkFolders.length);
		return far;
	}

	
	private void displayMatrix( ResultAccesor result, int thresholdIndex, double far, double frr ) throws Exception {
		final String ONE  = "1  ";
		final String ZERO = "0  ";
		
		output.write("Matriu identificacions Th:" + ThresholdsToCheck[thresholdIndex] + "\n" );
		output.write("-------------------------------\n");
		
		for ( int i=0; i<inputFolders.length; ++i ) {
			for ( int j=0; j<checkFolders.length; ++j ) {
				boolean itemResult = result.get( thresholdIndex, i, j );
				output.write( itemResult ? ONE : ZERO );
			}
			output.write( "\n" );
		}
		
		output.write( "-> FRR:" + frr + "   FAR:" + far + "\n\n" );
		output.flush();
	}

	
	private List<OutlierFeatureSignatureValidator> generateValidators( ExecutorService executor ) throws Exception {
		final CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		final List<Future<OutlierFeatureSignatureValidator>> futures = 
					new ArrayList<Future<OutlierFeatureSignatureValidator>>();
		for ( int i=0; i<inputFolders.length; i++ ){
			final int userIndex = i; 
			
			Callable<OutlierFeatureSignatureValidator> validatorGenerator =
							new Callable<OutlierFeatureSignatureValidator>() {
					@Override
					public OutlierFeatureSignatureValidator call() throws Exception {
						File folder = inputFolders[userIndex];
						List<CapturedData> inputData = inputDataParser.parse(folder);
						
						return new OutlierFeatureSignatureValidator(inputData);
					}
				};
				
			futures.add( executor.submit(validatorGenerator) );
		}
	
		List<OutlierFeatureSignatureValidator> ret = new ArrayList<OutlierFeatureSignatureValidator>();
		for( Future<OutlierFeatureSignatureValidator> future : futures ) {
			ret.add( future.get() );
		}
		return ret;
	}
	
	
	private List<Signature> generateSignaturesToCheck() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		List<Signature> ret = new ArrayList<Signature>();
		for (int i=0; i<checkFolders.length; i++){
			File folder=checkFolders[i];
			List<CapturedData> checkData = inputDataParser.parse(folder);
			
			Signature signature = signatureBuilder.build( checkData.get(0) );
			ret.add( signature );
		}
		return ret;
	}
	

	private XYPlot generateBasePlot() throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		// Linies i punts visibles
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
		plot.setRenderer( renderer );
		
		return plot;
	}

	
	public static void main(String[] args) {
		try {
			AppEvaluation prueba = new AppEvaluation( INPUT_FOLDERS, CHECK_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
