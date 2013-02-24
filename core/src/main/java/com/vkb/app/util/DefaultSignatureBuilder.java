package com.vkb.app.util;

import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;

public class DefaultSignatureBuilder extends SignatureBuilder {

	public DefaultSignatureBuilder() {
		super( new EmptyPreprocessor(), new DefaultFeaturesExtractor() );
	}
}
