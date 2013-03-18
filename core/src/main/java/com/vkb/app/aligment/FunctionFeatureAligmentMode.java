package com.vkb.app.aligment;

import com.vkb.math.dtw.FunctionWarper;
import com.vkb.model.Signature;

public interface FunctionFeatureAligmentMode {
	public FunctionWarper.Result apply( Signature signature1, Signature signature2 ) throws Exception;
}
