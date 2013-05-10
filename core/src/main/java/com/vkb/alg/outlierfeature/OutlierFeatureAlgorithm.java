package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.CapturedDataValidator;
import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.GenericSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureValidator;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class OutlierFeatureAlgorithm implements SignatureValidator, CapturedDataValidator  {
	private GenericSignatureValidator impl;
	private OutlierFeatureSignatureValidator determiner;
	
	public OutlierFeatureAlgorithm( List<CapturedData> capturedDatas ) throws Exception {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		
		determiner = new OutlierFeatureSignatureValidator();
		impl = new GenericSignatureValidator( preprocessor, featuresExtractor, determiner, capturedDatas );
	}
	
	public void setThreshold( double th ) {
		determiner.setThreshold( th );
	}
	
	public OutlierFeatureSignatureValidator getValidator() {
		return determiner;
	}
	
	@Override
	public boolean check( CapturedData capturedData ) throws Exception {
		return impl.check( capturedData );
	}
	
	@Override
	public boolean check( Signature signature ) throws Exception {
		return impl.check( signature );
	}
}
