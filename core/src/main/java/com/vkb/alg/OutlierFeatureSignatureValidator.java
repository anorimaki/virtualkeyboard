package com.vkb.alg;

import java.util.List;

import com.vkb.alg.determine.OutlierFeatureDeterminer;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class OutlierFeatureSignatureValidator {
	private GenericSignatureValidator impl;
	private OutlierFeatureDeterminer determiner;
	
	public OutlierFeatureSignatureValidator( List<CapturedData> capturedDatas ) throws Exception {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		determiner = new OutlierFeatureDeterminer();
		impl = new GenericSignatureValidator( preprocessor, featuresExtractor, determiner, capturedDatas );
	}
	
	public void setThreshold( double th ) {
		determiner.setThreshold( th );
	}
	
	public boolean check( CapturedData capturedData ) throws Exception {
		return impl.check( capturedData );
	}
	
	public boolean check( Signature signature ) throws Exception {
		return impl.check( signature );
	}
}
