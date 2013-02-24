package com.vkb.model;

import java.util.ArrayList;
import java.util.List;

public class CapturedData {
	private List<Trace> traces;
	
	public CapturedData( Trace trace ) {
		traces = new ArrayList<Trace>();
		traces.add(trace);
	}
	
	// By now only one trace is supported
	public Trace getTrace() {
		return traces.get(0);
	}
}
