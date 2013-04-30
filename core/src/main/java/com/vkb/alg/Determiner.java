package com.vkb.alg;

import java.util.List;

import com.vkb.model.Signature;

public interface Determiner {
	public void setPattern( List<Signature> signature ) throws Exception;
	public boolean check( Signature signature ) throws Exception;
}

