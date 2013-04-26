package com.vkb.alg;

import com.vkb.alg.determine.PatternsStatistics;
import com.vkb.model.Signature;

public interface Determiner {
	public boolean check( Signature signature, PatternsStatistics pS ) throws Exception;;
	public double checkRate( Signature signature, PatternsStatistics pS ) throws Exception;;
}

