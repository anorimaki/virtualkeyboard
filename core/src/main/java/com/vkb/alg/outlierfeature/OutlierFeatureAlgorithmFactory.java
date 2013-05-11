package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.model.CapturedData;

public class OutlierFeatureAlgorithmFactory implements SignatureValidatorFactory<OutlierFeatureAlgorithm>  {

	@Override
	public OutlierFeatureAlgorithm generateValidator( List<CapturedData> patternSamples ) throws Exception {
		return new OutlierFeatureAlgorithm(patternSamples);
	}
}
