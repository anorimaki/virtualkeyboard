package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public interface CellCostFunction {
	public void init( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception;
	public double calculateCost( int col, int row ) throws Exception;
}
