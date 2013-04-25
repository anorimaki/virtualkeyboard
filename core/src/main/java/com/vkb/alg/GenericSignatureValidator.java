package com.vkb.alg;

import java.util.List;

import com.vkb.alg.determine.OutlierFeatureDeterminer;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;
import com.vkb.alg.determine.PatternsStatistics;

public class GenericSignatureValidator {
	private Determiner determiner;
	private final double Th = 0.8;
	
	
	public GenericSignatureValidator() throws Exception {
		determiner = new OutlierFeatureDeterminer( this.Th );
	}

	
	public GenericSignatureValidator( double Th ) throws Exception {
		determiner = new OutlierFeatureDeterminer( Th );
	}
	
	
	public boolean check( CapturedData capturedData, PatternsStatistics pS ) throws Exception {

		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		
		Signature signature = traceBuilder.build( capturedData );
		return determiner.check( signature, pS );
	}
	
	
	public double checkRate( CapturedData capturedData, PatternsStatistics pS  ) throws Exception {

		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		Signature signature = traceBuilder.build( capturedData );
		return determiner.checkRate( signature, pS );
	}
}
