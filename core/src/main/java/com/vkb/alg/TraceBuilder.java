package com.vkb.alg;

import java.util.ArrayList;
import java.util.List;

import com.vkb.RawTrace;
import com.vkb.Trace;

public class TraceBuilder {
	private Normalizer normalizer = new Normalizer();
	
	public Trace build( RawTrace rawTrace ) {
		rawTrace = normalizer.normalize(rawTrace);
		return new Trace( rawTrace );
	}
	
	public List<Trace> build( List<RawTrace> rawTraces ) {
		ArrayList<Trace> ret = new ArrayList<Trace>();
		for( RawTrace rawTrace : rawTraces ) {
			ret.add( build(rawTrace) );
		}
		return ret;
	}
}
