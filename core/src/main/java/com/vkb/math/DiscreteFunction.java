package com.vkb.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class DiscreteFunction {
	public static class Point {
		private double x;
		private double y;
		
		public Point( double x, double y ) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return x;
		}
		
		public double getY() {
			return y;
		}
		
		@Override
		public String toString() {
			// Canviat ( per [ per compatibilitat amb Matlab i eliminat index
			// return "(" + x + "," + y + ")";
			return ""+y ;
		}
	}

	
	public static final double DEFAULT_INTERPOLATION_INCREMENT = 1.0d;
	
	private String name;
	private List<Point> points;
	
	public DiscreteFunction( String name ) {
		this.points = new ArrayList<Point>();
		this.name = name;
	}
	
	public DiscreteFunction( String name, Collection<Point> points ) {
		this.points = new ArrayList<Point>( points );
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public void add( double x, double y ) {
		points.add( new Point( x, y ) );
	}
	
	public void add( Point p ) {
		points.add( p );
	}
	
	public void addAll( Collection<Point> other ) {
		points.addAll( other );
	}
	
	public void addAll( DiscreteFunction other ) {
		points.addAll( other.points );
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
	
	public UnivariateFunction interpolate() {
		final double MAX_BANDWIDTH = 0.1d;
		final double MIN_BANDWIDTH = 0.05d;
		final double BANDWIDTH_INC = 0.01d;
		
		for ( double bandwidth = MIN_BANDWIDTH; true; bandwidth += BANDWIDTH_INC ) {
			try {
				UnivariateInterpolator interpolator =
							new LoessInterpolator( bandwidth, 0, LoessInterpolator.DEFAULT_ACCURACY );
				return interpolator.interpolate(getX(), getY());
			}
			catch( NumberIsTooSmallException e ) {
				if ( bandwidth > MAX_BANDWIDTH ) {
					throw e;
				}
			}
		}
	}
	
	public DiscreteFunction multiply( UnivariateFunction f ) {
		DiscreteFunction ret = new DiscreteFunction( getName() );
		for ( Point p : points ) {
			double value = f.value( p.getX() ) * p.getY();
			Point newPoint = new Point( p.getX(), value );
			ret.add(newPoint);
		}
		return ret;
	}
	
	public double[] getX() {
		double[] ret = new double[size()];
		int i=0;
		for( Point p : points ) {
			ret[i++] =p.getX(); 
		}
		return ret;
	}
	
	public double[] getY() {
		double[] ret = new double[size()];
		int i=0;
		for( Point p : points ) {
			ret[i++] =p.getY(); 
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return name + ": "  + points.toString();
	}
		

}
