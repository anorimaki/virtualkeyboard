package com.vkb.alg.outlierfeature;

import java.util.List;

import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class OutlierFeatureAlgorithmFactory implements SignatureValidatorFactory<OutlierFeatureAlgorithm>  {
	private OutlierFeatureAlgorithmTraits algorithmTraits;
	
	public OutlierFeatureAlgorithmFactory( OutlierFeatureAlgorithmTraits algorithmTraits ) {
		this.algorithmTraits = algorithmTraits;
	}
	
	@Override
	public OutlierFeatureAlgorithm generateValidatorFromCaptures( List<CapturedData> patternSamples ) throws Exception {
		return new OutlierFeatureAlgorithm( patternSamples, algorithmTraits );
	}

	@Override
	public OutlierFeatureAlgorithm generateValidatorFromSignatures( List<Signature> patternSamples ) throws Exception {
		OutlierFeaturePatternGenerator patternGenerator = new OutlierFeaturePatternGenerator( 
				algorithmTraits.getThreshold(), algorithmTraits.getFunctionFeatureComparator() );
		OutlierFeaturePatternGenerator.Result patternResult = patternGenerator.generate( patternSamples );
		return new OutlierFeatureAlgorithm( patternResult.getPattern(), algorithmTraits );
	}
}
