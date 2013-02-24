package com.vkb.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vkb.RawTrace;

public class TracesParser {
	public List<RawTrace> parse( File inputFolder ) throws Exception {
		File[] traceFiles = TraceFilesHelper.getTraceFiles( inputFolder );
		
		TraceParser parser = new TraceParser();
		List<RawTrace> traces = new ArrayList<RawTrace>();
		for ( File traceFile : traceFiles ) {
			traces.add( parser.parse( traceFile ) );
		}
		
		return traces;
	}
}
