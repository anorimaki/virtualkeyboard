package com.vkb.alg;

import java.util.ArrayList;
import java.util.List;

import com.vkb.RawTrace;
import com.vkb.TracePoint;

public class Normalizer {
	public List<RawTrace> normalize( List<RawTrace> traces ) {
		List<RawTrace> ret = new ArrayList<RawTrace>();
		for( RawTrace trace : traces ) {
			ret.add( normalize(trace) );
		}
		return ret;
	}
	
	public RawTrace normalize( RawTrace trace ) {
		if ( trace.size() == 0 )
			return trace;
		
		long offsetX = trace.get(0).getX();
		long offsetY = trace.get(0).getY();
		
		RawTrace ret = new RawTrace();
		for( TracePoint point : trace ) {
			TracePoint p = new TracePoint( point.getX() - offsetX, 
											-(point.getY() - offsetY),
											point.getTime() );
			ret.add( p );
		}
		return ret;
	}
}
