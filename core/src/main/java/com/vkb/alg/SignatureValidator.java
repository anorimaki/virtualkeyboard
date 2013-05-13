package com.vkb.alg;

import com.vkb.model.Signature;

public interface SignatureValidator {
	public boolean check( Signature signature ) throws Exception;
}
