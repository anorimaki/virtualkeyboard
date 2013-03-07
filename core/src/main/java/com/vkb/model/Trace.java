package com.vkb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vkb.math.DiscreteFunction;

public class Trace implements Iterable<TracePoint> {
	private List<TracePoint> data;
	
	public Trace() {
		this( Collections.<TracePoint> emptyList() );
	}
	
	public Trace( List<TracePoint> data ) {
		this.data = new ArrayList<TracePoint>(data);
	}
	
	public void add( TracePoint point ) {
		data.add( point );
	}
	
	public TracePoint get( int index ) {
		return data.get(index);
	}
	
	public int size() {
		return data.size();
	}
	
	public DiscreteFunction getXFunction() {
		DiscreteFunction ret = new DiscreteFunction("X(t)");
		for( TracePoint p : data ) {
			ret.add( p.getTime(), p.getX() );
		}
		return ret;
	}
	
	public DiscreteFunction getYFunction() {
		DiscreteFunction ret = new DiscreteFunction("Y(t)");
		for( TracePoint p : data ) {
			ret.add( p.getTime(), p.getY() );
		}
		return ret;
	}
	
	public DiscreteFunction getPositionFunction() {
		DiscreteFunction ret = new DiscreteFunction("Y(x)");
		for( TracePoint p : data ) {
			ret.add( p.getX(), p.getY() );
		}
		return ret;
	}
	
	@Override
	public Iterator<TracePoint> iterator() {
		return data.iterator();
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
