package com.vkb.math.dtw;

import java.util.List;

import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.DiscreteVectorFunction.Point;

public class DerivativeCellCostFunction implements CellCostFunction {
	private CellCostFunction underlayerFunction;
	
	public DerivativeCellCostFunction( CellCostFunction underlayerFunction ) {
		this.underlayerFunction = underlayerFunction;
	}
	
	@Override
	public void init( DiscreteVectorFunction serie1, DiscreteVectorFunction serie2 ) throws Exception {
		if ( serie1.countDimensions() != serie2.countDimensions() ) {
	         throw new InvalidDataException("ERROR:  cannot calculate the distance "
	                                 + "between vectors of different sizes.");
		}
		DiscreteVectorFunction serie1Derivated = derivate( serie1 );
		DiscreteVectorFunction serie2Derivated = derivate( serie2 );
		underlayerFunction.init( serie1Derivated, serie2Derivated );
	}

	@Override
	public double calculateCost( int col, int row ) throws Exception {
		return underlayerFunction.calculateCost( col, row );
	}

	private static DiscreteVectorFunction derivate( DiscreteVectorFunction serie ) throws Exception {
		List<DiscreteVectorFunction.Point> points = serie.getPoints();
		
		
		DiscreteVectorFunction ret = new DiscreteVectorFunction( serie.getDimensions() );
		ret.add( derivate( serie.get(0), serie.get(1), serie.get(2) ) );
		for ( int i=1; i<(points.size()-1); ++i ) {
			ret.add( derivate( serie.get(i-1), serie.get(i), serie.get(i+1) ) );
		}
		ret.add( derivate( serie.get(points.size()-3), serie.get(points.size()-2), serie.get(points.size()-1) ) );
		
		return ret;
	}

	private static Point derivate( Point previous, Point current, Point next ) {
		double values[] = new double[current.countDimensions()];
		for( int i=0; i<current.countDimensions(); ++i ) {
			values[i] = ((current.getValue(i) - previous.getValue(i)) + ((next.getValue(i)-previous.getValue(i))/2)) / 2;
		}
		return new Point( current.getX(), values );
	}
}
