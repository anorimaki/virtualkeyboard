package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.MinMaxNormalizationMethod;

public class GeneralDTW implements FunctionWarper {
	private MatrixCostFunction costMatrixCalculator;
	
	public GeneralDTW( MatrixCostFunction costMatrixCalculator ) {
		this.costMatrixCalculator = costMatrixCalculator;
	}
	
	@Override
	public Result align( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception {
		DiscreteVectorFunction normalizedF1 = new DiscreteVectorFunction( serie1 );
		normalizedF1.normalizeValues( MinMaxNormalizationMethod.INSTANCE );
		
		DiscreteVectorFunction normalizedF2 = new DiscreteVectorFunction( serie2 );
		normalizedF2.normalizeValues( MinMaxNormalizationMethod.INSTANCE );
		
		MatrixCost costMatrix = costMatrixCalculator.calculate( normalizedF1, normalizedF2 );
		WarpPath path = costMatrix.calculateWarpPath();
		
		return new Result( path, costMatrix.get( serie1.size()-1, serie2.size()-1 ) );
	}
}
