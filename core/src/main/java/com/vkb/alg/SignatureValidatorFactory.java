package com.vkb.alg;

import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public interface SignatureValidatorFactory<T> {
	T generateValidatorFromCaptures( List<CapturedData> patternSamples ) throws Exception ;
	T generateValidatorFromSignatures( List<Signature> patternSamples ) throws Exception ;
}
