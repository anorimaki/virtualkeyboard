package com.vkb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public class FunctionPoints implements Iterable<Point> {
	public static final String ANONYMOUS = "anonymous";
	public static final double DEFAULT_INTERPOLATION_INCREMENT = 1.0d;
	
	private String name;
	private List<Point> points;
	
	public FunctionPoints( String name ) {
		this.points = new ArrayList<Point>();
		this.name = name;
	}
	
	public FunctionPoints() {
		this( ANONYMOUS );
	}
	
	public FunctionPoints( String name, Collection<Point> points ) {
		this.points = new ArrayList<Point>( points );
		this.name = name;
	}
	
	public FunctionPoints( Collection<Point> points ) {
		this( ANONYMOUS, points );
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
	
	public void addAll( FunctionPoints other ) {
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
		UnivariateInterpolator interpolator = new LoessInterpolator( 0.05d, 0, LoessInterpolator.DEFAULT_ACCURACY );
		return interpolator.interpolate(getX(), getY());
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
	public Iterator<Point> iterator() {
		return points.iterator();
	}
	
	@Override
	public String toString() {
		return name + ": "  + points.toString();
	}
}
