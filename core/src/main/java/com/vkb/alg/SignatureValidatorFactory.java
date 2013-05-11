package com.vkb.alg;

import java.util.List;

import com.vkb.model.CapturedData;

public interface SignatureValidatorFactory<T> {
	T generateValidator( List<CapturedData> patternSamples ) throws Exception ;
}
