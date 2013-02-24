package com.vkb.alg.preprocess;

import com.vkb.alg.Preprocessor;
import com.vkb.model.CapturedData;

public class EmptyPreprocessor implements Preprocessor {
	public CapturedData preprocess( CapturedData inputData ) {
		return inputData;
	}
}
