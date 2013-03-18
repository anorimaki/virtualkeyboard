package com.vkb.math.dtw;

public class MatrixCost {
	private double[][] data; 
	
	public MatrixCost( int columns, int rows ) {
		data = new double[columns][rows];
	}
	
	public void set( int column, int row, double value ) {
		data[column][row] = value;
	}
	
	public double get( int column, int row ) {
		return data[column][row];
	}
	
	public WarpPath calculateWarpPath() {
		int column = data.length - 1;
		int row = data[0].length - 1;
		
		WarpPath ret = new WarpPath();
		
		while( (column>0) && (row>0) ) {
			double diagonalCost = data[column-1][row-1];
			double leftCost = data[column-1][row];
			double downCost = data[column][row-1];
			
			if ( (leftCost<downCost) || ((leftCost==downCost) && (row<column)) ) {
				--column;
				if ( diagonalCost<=leftCost ) {
					--row;
				}
			}
			else {
				--row;
				if ( diagonalCost<=downCost ) {
					--column;
				}
			}
			
			ret.pushFront( column, row );
		}
		
		while( row>=0 ) {
			ret.pushFront( 0, row-- );
		}
		
		while( column>=0 ) {
			ret.pushFront( column--, 0 );
		}
		
		return ret;
	}
}
