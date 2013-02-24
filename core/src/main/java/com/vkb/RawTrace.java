package com.vkb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RawTrace implements Iterable<TracePoint> {
	private List<TracePoint> data;
	
	public RawTrace() {
		this( Collections.<TracePoint> emptyList() );
	}
	
	public RawTrace( List<TracePoint> data ) {
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
	
	public FunctionPoints getXFunction() {
		FunctionPoints ret = new FunctionPoints("X(t)");
		for( TracePoint p : data ) {
			ret.add( new Point( p.getTime(), p.getX() ) );
		}
		return ret;
	}
	
	public FunctionPoints getYFunction() {
		FunctionPoints ret = new FunctionPoints("Y(t)");
		for( TracePoint p : data ) {
			ret.add( new Point( p.getTime(), p.getY() ) );
		}
		return ret;
	}
	
	public List<Point> getTracePoints() {
		ArrayList<Point> ret = new ArrayList<Point>();
		for( TracePoint p : data ) {
			ret.add( new Point( p.getX(), p.getY() ) );
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