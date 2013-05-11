package com.vkb.alg;

import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Features;
import com.vkb.model.Signature;

public class GenericSignatureBuilder implements SignatureBuilder {
	private Preprocessor preprocessor;
	private FeaturesExtractor featuresExtractor;
	private SignaturesBuilder signaturesBuilder;
	
	public GenericSignatureBuilder( Preprocessor preprocessor, FeaturesExtractor featuresExtractor ) {
		this.preprocessor = preprocessor;
		this.featuresExtractor = featuresExtractor;
		signaturesBuilder = new SignaturesBuilder( this );
	}
	
	@Override
	public Signature buildSignature( CapturedData capturedData ) throws Exception {
		capturedData = preprocessor.preprocess(capturedData);
		
		Features features = featuresExtractor.extract( capturedData );
		
		return new Signature( capturedData, features );
	}
	
	public List<Signature> buildSignatures( List<CapturedData> capturedDatas ) throws Exception {
		return signaturesBuilder.buildSignatures(capturedDatas);
	}
}
