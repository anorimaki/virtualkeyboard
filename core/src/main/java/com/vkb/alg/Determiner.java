package com.vkb.alg;

import com.vkb.model.Signature;

public interface Determiner {
	public boolean check( Signature signature ) throws Exception;;
	public double checkRate( Signature signature ) throws Exception;;
}

