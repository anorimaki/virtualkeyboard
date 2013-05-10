package com.vkb.alg;

import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class GenericSignatureValidator implements SignatureValidator, CapturedDataValidator {
	private SignaturePatternBasedValidator impl;
	private SignatureBuilder signatureBuilder;
	
	public GenericSignatureValidator( Preprocessor preprocessor, 
							FeaturesExtractor featuresExtractor, 
							SignaturePatternBasedValidator pattern,
							List<CapturedData> capturedDatas ) throws Exception {
		signatureBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
		List<Signature> patternTraces = signatureBuilder.build( capturedDatas );
		
		this.impl = pattern;
		impl.setPattern(patternTraces);
	}
	
	@Override
	public boolean check( CapturedData capturedData ) throws Exception {
		Signature signature = signatureBuilder.build( capturedData );
		return check( signature );
	}
	
	@Override
	public boolean check( Signature signature ) throws Exception {
		return impl.check( signature );
	}
}
