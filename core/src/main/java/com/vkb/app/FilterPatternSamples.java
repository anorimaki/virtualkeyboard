package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeaturePatternGenerator;
import com.vkb.app.util.Environment;
import com.vkb.io.CapturedDataFilesHelper;
import com.vkb.io.CapturedDataParser;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class FilterPatternSamples {
	private static final double FILTER_THRESHOLD = 0.4d;
	
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user_a/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_doh/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_egg/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_fj/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_jig/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_ma/pattern" ),
		new File( Environment.RESOURCES_DIR, "user_xf/pattern" ) };
	
	private File[] inputFolders;
	
	public FilterPatternSamples( File[] inputFolders ) throws Exception {
		this.inputFolders = inputFolders;
	}
	
	public void run() throws Exception {
		SignatureBuilder signatureBuilder = OutlierFeatureAlgorithm.generateSignatureBuilder();
		
		for( File inputFolder : inputFolders ) {
			System.out.println( "- Processign folder " + inputFolder.getPath() + ":" );
			run( inputFolder, signatureBuilder );
		}
	}
	
	private void run( File inputFolder, SignatureBuilder signatureBuilder ) throws Exception {
		CapturedDataParser parser = new CapturedDataParser();
		
		File[] traceFiles = CapturedDataFilesHelper.getTraceFiles( inputFolder );
		List<Signature> signatures = new ArrayList<Signature>();
		for ( File traceFile : traceFiles ) {
			CapturedData capturedData = parser.parse( traceFile ); 
			signatures.add( signatureBuilder.buildSignature(capturedData) );
		}
		
		OutlierFeaturePatternGenerator patternGenerator = new OutlierFeaturePatternGenerator( FILTER_THRESHOLD );
		
		OutlierFeaturePatternGenerator.Result result = patternGenerator.generate(signatures);
		for( Signature signature : result.getUnusedSignatures() ) {
			int pos = signatures.indexOf(signature);
			if  ( pos != -1 ) {
				System.out.println( "    * File " + traceFiles[pos].getPath() + " discarded." );
			}
		}
	}

	public static void main(String[] args) {
		try {
			FilterPatternSamples app = new FilterPatternSamples( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
