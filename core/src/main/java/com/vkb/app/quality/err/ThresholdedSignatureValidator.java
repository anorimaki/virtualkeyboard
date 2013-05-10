package com.vkb.app.quality.err;

import com.vkb.model.Signature;

public interface ThresholdedSignatureValidator {
	public void setThreshold( double threshold );
	public boolean check( Signature signature ) throws Exception;
}
