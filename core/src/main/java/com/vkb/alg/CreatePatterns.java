package com.vkb.alg;

import java.util.List;

import com.vkb.alg.determine.OutlierFeatureDeterminer;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;
import com.vkb.alg.determine.PatternsStatistics;

public class CreatePatterns {

	public PatternsStatistics createPatterns(List<CapturedData> capturedDatas) throws Exception {
		
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		SignatureBuilder traceBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		List<Signature> patternTraces = traceBuilder.build( capturedDatas );
		
		return new PatternsStatistics (patternTraces);
	}
	
}
