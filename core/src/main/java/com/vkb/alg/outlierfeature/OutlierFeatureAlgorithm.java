package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.CapturedDataValidator;
import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.alg.GenericSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class OutlierFeatureAlgorithm implements ThresholdedSignatureValidator, CapturedDataValidator, SignatureBuilder  {
	private GenericSignatureValidator impl;
	private OutlierFeatureSignatureValidator validator;
	
	public OutlierFeatureAlgorithm( List<CapturedData> capturedDatas ) throws Exception {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		GenericSignatureBuilder signatureBuilder = new GenericSignatureBuilder( preprocessor, featuresExtractor );
		
		validator = new OutlierFeatureSignatureValidator();
		impl = new GenericSignatureValidator( signatureBuilder, validator, capturedDatas );
	}
	
	@Override
	public void setThreshold( double th ) {
		validator.setThreshold( th );
	}
	
	@Override
	public boolean check( CapturedData capturedData ) throws Exception {
		return impl.check( capturedData );
	}
	
	@Override
	public boolean check( Signature signature ) throws Exception {
		return impl.check( signature );
	}

	@Override
	public Signature buildSignature(CapturedData capturedData) throws Exception {
		return impl.buildSignature( capturedData );
	}
}
