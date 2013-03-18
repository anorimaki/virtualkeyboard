package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public interface MatrixCostFunction {
	public MatrixCost calculate( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception;
}
