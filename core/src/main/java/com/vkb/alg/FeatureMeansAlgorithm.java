package com.vkb.alg;

import java.util.List;

import com.vkb.RawTrace;
import com.vkb.Trace;
import com.vkb.TracePattern;

public class FeatureMeansAlgorithm {
	private TracePattern pattern;
	private TraceBuilder traceBuilder;
	private double threshold;
	
	public FeatureMeansAlgorithm( List<RawTrace> patternTraces, double threshold ) {
		pattern = generatePattern( patternTraces );
		traceBuilder = new TraceBuilder();
		this.threshold = threshold;
	}
	
	public boolean check( RawTrace rawTrace ) {
		Trace trace = traceBuilder.build( rawTrace );
		double insidersRate = pattern.compare(trace);
		
		return insidersRate > threshold;
	}

	private TracePattern generatePattern(List<RawTrace> rawTraces) {
		List<Trace> patternTraces = traceBuilder.build( rawTraces );
		
		return new TracePattern(patternTraces);
	}
}
