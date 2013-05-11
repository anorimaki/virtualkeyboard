package com.vkb.app.util;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;

public class DefaultSignatureBuilder extends GenericSignatureBuilder {

	public DefaultSignatureBuilder() {
		super( new EmptyPreprocessor(), new DefaultFeaturesExtractor() );
	}
}
