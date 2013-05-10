package com.vkb.alg;

import java.util.List;

import com.vkb.model.Signature;

public interface SignaturePatternBasedValidator extends SignatureValidator {
	public void setPattern( List<Signature> patternSignatures ) throws Exception;
}

