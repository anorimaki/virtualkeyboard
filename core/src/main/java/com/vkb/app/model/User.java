package com.vkb.app.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class User {
	private static String SAMPLES_FOLDER = "samples";
	private static String PATTERN_FOLDER = "pattern";
	private OutlierFeatureAlgorithm validator;
	private List<Signature> ownSignatures;
	
	public User( File userFolder ) throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		File patternSamplesFolder = new File( userFolder, PATTERN_FOLDER );
		List<CapturedData> patternSamples = inputDataParser.parse(patternSamplesFolder);
		validator = new OutlierFeatureAlgorithm( patternSamples );
		
		File ownSamplesFolder = new File( userFolder, SAMPLES_FOLDER );
		List<CapturedData> checkOwnSamples = inputDataParser.parse(ownSamplesFolder);
		ownSignatures = generateOwnSamples( checkOwnSamples );
	}
	
	public List<Signature> getOwnSignatures() {
		return ownSignatures;
	}
	
	public OutlierFeatureAlgorithm getValidationAlgorithm() {
		return validator;
	}

	private List<Signature> generateOwnSamples( List<CapturedData> checkOwnSamples ) throws Exception {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		SignatureBuilder signatureBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		List<Signature> ret = new ArrayList<Signature>();
		for ( CapturedData capturedData : checkOwnSamples ){
			Signature signature = signatureBuilder.build( capturedData );
			ret.add( signature );
		}
		return ret;
	}
	
	
	
}
