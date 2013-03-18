package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public class OriginalDTWMatrixCostFunction implements MatrixCostFunction {
	private CellCostFunction cellCostFunction;
	
	public OriginalDTWMatrixCostFunction( CellCostFunction cellCostFunction ) {
		this.cellCostFunction = cellCostFunction;
	}
	
	
	@Override
	public MatrixCost calculate( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception {
			
		MatrixCost costMatrix = new MatrixCost( serie1.size(), serie2.size() );
		
		cellCostFunction.init( serie1, serie2 );
		
		costMatrix.set( 0, 0, cellCostFunction.calculateCost( 0, 0 ) );
		
		//First Column calculus
		for ( int row=1; row<serie2.size(); row++ ) {
			costMatrix.set( 0, row, costMatrix.get(0, row-1) + cellCostFunction.calculateCost( 0, row ) );
		}
	         
	    //First Row calculus
	 	for ( int col=1; col<serie1.size(); col++ ) {
	 		costMatrix.set( col, 0, costMatrix.get(col-1, 0) + cellCostFunction.calculateCost( col, 0 ) );
	 	}
	 	
	 	//Complete matrix
	 	for ( int col=1; col<serie1.size(); col++ ) {
	 		for( int row=1; row<serie2.size(); ++row ) {
	 			double minPredecessor = Math.min( costMatrix.get(col, row-1),
							               Math.min( costMatrix.get(col-1, row), costMatrix.get(col-1, row-1) ) );
	 			
	 			costMatrix.set( col, row, minPredecessor + cellCostFunction.calculateCost( col, row ) );
	 		}
	 	}
		
		return costMatrix;
	}
}
