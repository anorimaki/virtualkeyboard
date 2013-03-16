package com.vkb.alg;

import java.util.List;

import com.vkb.alg.determine.OutlierFeatureDeterminer;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class GenericSignatureValidator {
	private Determiner determiner;
	private SignatureBuilder traceBuilder;
	
	public GenericSignatureValidator( List<CapturedData> capturedDatas ) throws Exception {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		List<Signature> patternTraces = traceBuilder.build( capturedDatas );
		
		determiner = new OutlierFeatureDeterminer( patternTraces, 0.80d );
	}
	
	public boolean check( CapturedData capturedData ) throws Exception {
		Signature signature = traceBuilder.build( capturedData );
		return determiner.check( signature );
	}
}
