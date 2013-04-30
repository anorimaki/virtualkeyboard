package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public interface FunctionComparator {
	public double distance( DiscreteVectorFunction f1, DiscreteVectorFunction f2 ) throws Exception;
}
