package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public class OriginalDTW implements FunctionWarper, FunctionComparator {
	private GeneralDTW impl;
	
	public OriginalDTW( CellCostFunction cellCostFunction ) {
		this.impl = new GeneralDTW( new OriginalDTWMatrixCostFunction(cellCostFunction) );
	}
	
	@Override
	public Result align( DiscreteVectorFunction f1, DiscreteVectorFunction f2 ) throws Exception {
		return impl.align( f1,  f2 );
	}

	@Override
	public double distance(DiscreteVectorFunction f1, DiscreteVectorFunction f2) throws Exception {
		return impl.distance( f1,  f2 );
	}
}
