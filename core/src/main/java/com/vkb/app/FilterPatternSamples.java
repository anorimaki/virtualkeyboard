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
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1/pattern" ),
		new File( Environment.RESOURCES_DIR, "user2/pattern" ),
		new File( Environment.RESOURCES_DIR, "user3/pattern" ),
		new File( Environment.RESOURCES_DIR, "user4/pattern" ),
		new File( Environment.RESOURCES_DIR, "user5/pattern" ),
		new File( Environment.RESOURCES_DIR, "user6/pattern" ),
		new File( Environment.RESOURCES_DIR, "user7/pattern" ) };
	
	private File[] inputFolders;
	
	public FilterPatternSamples( File[] inputFolders ) throws Exception {
		this.inputFolders = inputFolders;
	}
	
	public void run() throws Exception {
		SignatureBuilder signatureBuilder = OutlierFeatureAlgorithm.generateSignatureBuilder();
		
		for( File inputFolder : inputFolders ) {
			System.out.println( "- Porcessign folder " + inputFolder.getPath() + ":" );
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
		
		OutlierFeaturePatternGenerator patternGenerator = new OutlierFeaturePatternGenerator( 0.3d );
		
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
