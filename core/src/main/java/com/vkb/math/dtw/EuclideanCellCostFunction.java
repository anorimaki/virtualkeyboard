package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public class EuclideanCellCostFunction implements CellCostFunction {
	private DiscreteVectorFunction serie1;
	private DiscreteVectorFunction serie2;
	
	@Override
	public void init( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception {
		if ( serie1.countDimensions() != serie2.countDimensions() ) {
	         throw new InvalidDataException("ERROR:  cannot calculate the distance "
	                                 + "between vectors of different sizes.");
		}
		this.serie1 = serie1;
		this.serie2 = serie2;
	}

	@Override
	public double calculateCost( int col, int row ) {
		DiscreteVectorFunction.Point p1 = serie1.get( col );
		DiscreteVectorFunction.Point p2 = serie2.get( row );
		
		double sqSum = 0.0;
		for (int x=0; x<p1.countDimensions(); x++) {
			double diff = p1.getValue(x)-p2.getValue(x);
			sqSum += (diff*diff);
		}

		return Math.sqrt(sqSum);
	}
}
