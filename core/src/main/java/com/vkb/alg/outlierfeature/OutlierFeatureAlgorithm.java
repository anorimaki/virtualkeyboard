package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.CapturedDataValidator;
import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.alg.GenericSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.SignatureValidator;
import com.vkb.alg.SignaturesBuilder;
import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class OutlierFeatureAlgorithm implements ThresholdedSignatureValidator, CapturedDataValidator, SignatureBuilder  {
	private GenericSignatureValidator impl;
	private WorkValidator validator;
	private static final double DEFAULT_THRESHOLD = 0.8d;
	
	public OutlierFeatureAlgorithm( List<CapturedData> capturedDatas, double threshold ) throws Exception {
		GenericSignatureBuilder signatureBuilder = generateSignatureBuilder();
		
		List<Signature> patternSignatures = new SignaturesBuilder(signatureBuilder).buildSignatures(capturedDatas);
		
		OutlierFeaturePatternGenerator patternGenerator = new OutlierFeaturePatternGenerator( threshold );
		OutlierFeaturePatternGenerator.Result patternResult = patternGenerator.generate(patternSignatures);
		
		init( signatureBuilder, patternResult.getPattern(), threshold );
	}

	public OutlierFeatureAlgorithm( OutlierFeatureSignaturePattern pattern, double threshold ) throws Exception {
		GenericSignatureBuilder signatureBuilder = generateSignatureBuilder();
		init( signatureBuilder, pattern, threshold );
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
	
	public OutlierFeatureSignaturePattern getPattern() {
		return validator.getPattern();
	}
	
	public static GenericSignatureBuilder generateSignatureBuilder() {
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		GenericSignatureBuilder signatureBuilder = new GenericSignatureBuilder( preprocessor, featuresExtractor );
		return signatureBuilder;
	}
	
	
	private void init( SignatureBuilder signatureBuilder, OutlierFeatureSignaturePattern pattern,
						double threshold ) throws Exception {
		validator = new WorkValidator( pattern );
		validator.setThreshold(DEFAULT_THRESHOLD);
		
		impl = new GenericSignatureValidator( signatureBuilder, validator );
	}
	
	
	private static class WorkValidator implements SignatureValidator {
		private OutlierFeatureSignaturePattern pattern;
		private double threshold;
		
		public WorkValidator( OutlierFeatureSignaturePattern pattern ) {
			this.pattern = pattern;
		}
		
		public void setThreshold( double th ) {
			threshold = th;
		}
		
		public OutlierFeatureSignaturePattern getPattern() {
			return pattern;
		}
		
		@Override
		public boolean check( Signature signature ) throws Exception {
			double insidersRate = pattern.compare(signature);
			return insidersRate > threshold;
		}
	}
	
	
	
}
