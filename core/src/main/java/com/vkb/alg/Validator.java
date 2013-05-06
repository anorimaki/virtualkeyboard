package com.vkb.alg;

import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public interface Validator {
	public boolean check( CapturedData capturedData ) throws Exception;
	public boolean check( Signature Signature ) throws Exception;
}
