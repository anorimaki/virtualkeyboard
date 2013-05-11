package com.vkb.alg;


import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public interface SignatureBuilder {
	public Signature buildSignature( CapturedData capturedData ) throws Exception;
}
