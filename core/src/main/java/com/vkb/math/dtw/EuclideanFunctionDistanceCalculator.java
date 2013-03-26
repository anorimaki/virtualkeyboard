package com.vkb.math.dtw;

import java.util.Iterator;

import com.vkb.math.DiscreteFunction;

public class EuclideanFunctionDistanceCalculator {
	public EuclideanFunctionDistanceCalculator() {}
	
	public double calculate( DiscreteFunction f1, DiscreteFunction f2 ) throws Exception {
		if ( f1.size() != f2.size() )
			throw new InvalidDataException( "Distance can be calculated over discrete function with different sizes" );
		
		Iterator<DiscreteFunction.Point> it1 = f1.getPoints().iterator();
		Iterator<DiscreteFunction.Point> it2 = f2.getPoints().iterator();
		
		double ret = 0.0d;
		while( it1.hasNext() ) {
			DiscreteFunction.Point p1 = it1.next();
			DiscreteFunction.Point p2 = it2.next();
			
			if ( p1.getX() != p2.getX() )
				throw new InvalidDataException( "Distance can be calculated over discrete function with different domain points" );
			
			ret = ret + Math.abs( p1.getY() - p2.getY() );
		}
		
		return ret;
	}
}
