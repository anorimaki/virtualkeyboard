package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

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
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class ScalarFeatureQuality {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" ),new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),new File( Environment.RESOURCES_DIR, "user4" ),new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),new File( Environment.RESOURCES_DIR, "user7" )};
			
	private File[] inputFolders;
	private int K;
	private int N;
	
	private Map<String,UsersStatistics> statistics= new HashMap<String, UsersStatistics>();
	
		
	public ScalarFeatureQuality( File[] inputFolders) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		List<CapturedData> inputData=null;
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		List<Signature> patternTraces;
		UsersStatistics aux;
		
		// Cal crear un hash <usuari,UsersStatistics> per cada lectura de directori
		K=inputFolders.length;
		N=0;
		for ( File inputFolder : inputFolders ) {
			inputData = inputDataParser.parse(inputFolder);
			N=N+inputData.size();
			System.out.println("Fitxers llegits a "+inputFolder.getAbsolutePath()+": "+inputData.size());
			patternTraces = traceBuilder.build( inputData );
			aux=new UsersStatistics(inputFolder.getName(),patternTraces);
			statistics.put(inputFolder.getName(), aux);
		}
		
		System.out.println("K:"+K+", N:"+N);
		

	}
	
	
	public static void main(String[] args) {
		try {
			ScalarFeatureQuality prueba = new ScalarFeatureQuality( INPUT_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
