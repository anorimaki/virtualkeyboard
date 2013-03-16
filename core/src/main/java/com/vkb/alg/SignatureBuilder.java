package com.vkb.alg;

import java.util.ArrayList;
import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Features;
import com.vkb.model.Signature;

public class SignatureBuilder {
	private Preprocessor preprocessor;
	private FeaturesExtractor featuresExtractor;
	
	public SignatureBuilder( Preprocessor preprocessor, FeaturesExtractor featuresExtractor ) {
		this.preprocessor = preprocessor;
		this.featuresExtractor = featuresExtractor;
	}
	
	public Signature build( CapturedData capturedData ) throws Exception {
		capturedData = preprocessor.preprocess(capturedData);
		
		Features features = featuresExtractor.extract( capturedData );
		
		return new Signature( capturedData, features );
	}
	
	public List<Signature> build( List<CapturedData> capturesDatas ) throws Exception {
		ArrayList<Signature> ret = new ArrayList<Signature>();
		for( CapturedData rawTrace : capturesDatas ) {
			ret.add( build(rawTrace) );
		}
		return ret;
	}
}
