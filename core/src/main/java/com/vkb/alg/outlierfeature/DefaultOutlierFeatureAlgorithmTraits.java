package com.vkb.alg.outlierfeature;

import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.math.dtw.DefaultFunctionFeatureComparator;


public class DefaultOutlierFeatureAlgorithmTraits {
	private static OutlierFeatureAlgorithmTraits instance = buildInstance();
	
	public static OutlierFeatureAlgorithmTraits getInstance() {
		return instance;
	}

	private static OutlierFeatureAlgorithmTraits buildInstance() {
		ConfigurableOutlierFeatureAlgorithmTraits ret = new ConfigurableOutlierFeatureAlgorithmTraits();
		ret.setThreshold( 0.4d );
		ret.setFunctionFeatureComparator( new DefaultFunctionFeatureComparator() );
		ret.setSignatureBuilder( new DefaultSignatureBuilder() );
		return ret;
	}
}
