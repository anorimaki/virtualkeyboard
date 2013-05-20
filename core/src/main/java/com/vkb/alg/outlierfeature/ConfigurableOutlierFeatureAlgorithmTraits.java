package com.vkb.alg.outlierfeature;

import com.vkb.alg.SignatureBuilder;
import com.vkb.math.dtw.FunctionFeatureComparator;

public class ConfigurableOutlierFeatureAlgorithmTraits implements OutlierFeatureAlgorithmTraits {
	private double initialThreshold;
	private double patternFilterThreshold;
	private SignatureBuilder signatureBuilder;
	private FunctionFeatureComparator functionFeatureComparator;
	
	public ConfigurableOutlierFeatureAlgorithmTraits( double initialThreshold, double patternFilterThreshold,
			SignatureBuilder signatureBuilder, FunctionFeatureComparator functionFeatureComparator ) {
		setThreshold( initialThreshold );
		setPatternFilterThreshold( patternFilterThreshold );
		setSignatureBuilder( signatureBuilder );
		setFunctionFeatureComparator( functionFeatureComparator );
	}
	
	

	public ConfigurableOutlierFeatureAlgorithmTraits() {}
	
	public ConfigurableOutlierFeatureAlgorithmTraits( OutlierFeatureAlgorithmTraits other ) {
		setThreshold( other.getThreshold() );
		setPatternFilterThreshold( other.getPatternFilterThreshold() );
		setSignatureBuilder( other.getSignatureBuilder() );
		setFunctionFeatureComparator( other.getFunctionFeatureComparator() );
	}

	@Override
	public double getThreshold() {
		return initialThreshold;
	}
	
	@Override
	public double getPatternFilterThreshold() {
		return patternFilterThreshold;
	}

	@Override
	public SignatureBuilder getSignatureBuilder() {
		return signatureBuilder;
	}

	@Override
	public FunctionFeatureComparator getFunctionFeatureComparator() {
		return functionFeatureComparator;
	}

	public void setThreshold(double d) {
		initialThreshold = d;
	}
	
	public void setPatternFilterThreshold(double d) {
		patternFilterThreshold = d;
	}
	
	public void setSignatureBuilder(SignatureBuilder signatureBuilder) {
		this.signatureBuilder = signatureBuilder;
	}
	
	public void setFunctionFeatureComparator(FunctionFeatureComparator functionFeatureComparator) {
		this.functionFeatureComparator = functionFeatureComparator;
	}
}
