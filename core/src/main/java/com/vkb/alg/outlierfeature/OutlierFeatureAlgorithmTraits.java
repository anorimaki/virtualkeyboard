package com.vkb.alg.outlierfeature;

import com.vkb.alg.SignatureBuilder;
import com.vkb.math.dtw.FunctionFeatureComparator;

public interface OutlierFeatureAlgorithmTraits {
	public double getThreshold();
	public double getPatternFilterThreshold();
	public SignatureBuilder getSignatureBuilder();
	public FunctionFeatureComparator getFunctionFeatureComparator();
}
