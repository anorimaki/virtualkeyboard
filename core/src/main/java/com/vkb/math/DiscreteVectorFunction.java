package com.vkb.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.vkb.math.dtw.InvalidDataException;

public class DiscreteVectorFunction {
	public class Point {
		private double x;
		private double[] values;
		
		public Point( double x, double[] values ) {
			this.x = x;
			this.values = values;
		}
		
		public double getX() {
			return x;
		}
		
		public double[] getValues() {
			return values;
		}
		
		public double getValue(int position) {
			return values[position];
		}
		
		public int countDimensions() {
			return values.length;
		}
		
		@Override
		public String toString() {
			return "(" + x + "," + values.toString() + ")";
		}
	}
	
	public static final String ANONYMOUS = "anonymous";
	public static final double DEFAULT_INTERPOLATION_INCREMENT = 1.0d;
	
	private List<String> dimensionLabels;
	private List<Point> points;
	
	public DiscreteVectorFunction( String... dimensionLabels ) {
		this( Arrays.asList(dimensionLabels) );
	}
	
	public DiscreteVectorFunction( List<String> dimensionLabels ) {
		this.dimensionLabels = dimensionLabels;
		this.points = new ArrayList<Point>();
	}

	public DiscreteVectorFunction( DiscreteVectorFunction other ) {
		dimensionLabels = new ArrayList<String>( other.dimensionLabels );
		points = new ArrayList<Point>( other.points );
	}
	
	public DiscreteVectorFunction( DiscreteFunction... others ) throws Exception {
		this( Arrays.asList(others), DiscreteFunction.class );
	}
	
	public DiscreteVectorFunction( Collection<DiscreteFunction> others, Class<DiscreteFunction> dummy ) throws Exception {
		initFromDiscreteFunctions( others );
	}
	
	public int countDimensions() {
		return dimensionLabels.size();
	}
	
	public List<String> getDimensions() {
		return dimensionLabels;
	}
	
	public DiscreteFunction getDimension( String dimension ) throws Exception {
		int position = dimensionLabels.indexOf(dimension);
		if ( position == -1 )
			throw new InvalidDimensionException( "Dimension " +  dimension + " not found.");

		DiscreteFunction ret = new DiscreteFunction( dimension );
		
		for( Point p : points ) {
			ret.add( p.getX(), p.getValue(position) );
		}
		
		return ret;
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public List<StatisticalSummary> getValuesStatistics() {
		List<StatisticalSummary> ret = new ArrayList<StatisticalSummary>();
		for( int i=0; i<countDimensions(); ++i ) {
			SummaryStatistics current = new SummaryStatistics();
			for( Point p : points ) {
				current.addValue( p.getValue(i) );
			}
			ret.add( current );
		}
		return ret;
	}
	
	public void add( double x, double[] values ) {
		points.add( new Point( x, values ) );
	}
	
	public void add( Point p ) throws Exception {
		if ( p.countDimensions() != dimensionLabels.size() )
			throw new InvalidPointException( "Can't add a point with " + p.countDimensions() + 
								" dimensions to a function with " + dimensionLabels.size() + " dimensions" );
		points.add( p );
	}
	
	public int size() {
		return points.size();
	}
	
	public Point get( int index ) {
		return points.get(index);
	}
	
	public double getMinX() {
		return get(0).getX();
	}
	
	public double getMaxX() {
		return get( size()-1 ).getX();
	}
	
	
	@Override
	public String toString() {
		return dimensionLabels.toString() + ": "  + points.toString();
	}
	
	private void initFromDiscreteFunctions( Collection<DiscreteFunction> functions ) throws Exception {
		dimensionLabels = new ArrayList<String>();
		for( DiscreteFunction function : functions ) {
			dimensionLabels.add( function.getName() );
		}
		
		Iterator<DiscreteFunction> functionIt = functions.iterator();
		
		points = new ArrayList<Point>();
		DiscreteFunction function0 = functionIt.next();
		for( int i=0; i<function0.size(); ++i ) {
			DiscreteFunction.Point p = function0.get(i);
			
			double x = p.getX();
			double[] values = new double[functions.size()];
			values[0] = p.getY();
			
			functionIt = functions.iterator();
			functionIt.next();
			int j=1;
			while( functionIt.hasNext() ) {
				DiscreteFunction functionJ = functionIt.next();
				DiscreteFunction.Point otherP = functionJ.get(i);
				if ( x != otherP.getX() )
					throw new InvalidDataException( "Discrete function must be sampled in same intervals" );
				values[j++] = otherP.getY();
			}
					
			points.add( new Point( x, values ) );
		}
	}
}
