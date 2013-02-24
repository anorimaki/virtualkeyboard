package com.vkb.alg.preprocess;

import java.util.ArrayList;
import java.util.List;

import com.vkb.model.Trace;
import com.vkb.model.TracePoint;

public class Normalizer {
	public List<Trace> normalize( List<Trace> traces ) {
		List<Trace> ret = new ArrayList<Trace>();
		for( Trace trace : traces ) {
			ret.add( normalize(trace) );
		}
		return ret;
	}
	
	public Trace normalize( Trace trace ) {
		if ( trace.size() == 0 )
			return trace;
		
		long offsetX = trace.get(0).getX();
		long offsetY = trace.get(0).getY();
		
		Trace ret = new Trace();
		for( TracePoint point : trace ) {
			TracePoint p = new TracePoint( point.getX() - offsetX, 
											-(point.getY() - offsetY),
											point.getTime() );
			ret.add( p );
		}
		return ret;
	}
}
