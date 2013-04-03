package com.vkb.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.vkb.model.CapturedData;
import com.vkb.model.Trace;
import com.vkb.model.TracePoint;

public class CapturedDataParser {
	private enum Version { V1, V2 }; 
	private static final String V1_BEGINING = "{\"format\"";
	
	public CapturedData parse( File file ) throws Exception {
		Version version = getVersion( file );
		if ( version == Version.V1 ) {
			return parseV1( file );
		}
		return parseV2( file );
	}
	
	
	private Version getVersion(File file) throws Exception {
		Reader reader = new FileReader( file );
		char[] data = new char[V1_BEGINING.length()];
		reader.read(data);
		reader.close();
		if ( V1_BEGINING.equals( new String(data) ) )
			return Version.V1;
		return Version.V2;
	}
	
	
	private CapturedData parseV2(File file) throws Exception {
		Gson gson = new Gson();
		
		Reader reader = new FileReader( file );
		DataV2 data = gson.fromJson(reader, DataV2.class);
		reader.close();
		
		CapturedData ret = new CapturedData();
		for ( DataV2.Traces traces : data.training ) {
			Trace trace = getTrace( traces.sample, file );
			ret.add( trace );
		}
		return ret;
	}
		

	static private class DataV2 {
		static private class Traces {
			TracePoint[] sample;
		}
		static private class Gir {
			@SuppressWarnings("unused") double alfa;
			@SuppressWarnings("unused") double beta;
			@SuppressWarnings("unused") double gamma;
			@SuppressWarnings("unused") int timeStamp;
		}
		Traces[] training;
		@SuppressWarnings("unused") Gir[] gir;
		@SuppressWarnings("unused") String id;
		@SuppressWarnings("unused") String time;
		@SuppressWarnings("unused") String device;
	}
	

	private CapturedData parseV1( File file ) throws Exception {
		Gson gson = new Gson();
		
		Reader reader = new FileReader( file );
		DataV1 data = gson.fromJson(reader, DataV1.class);
		reader.close();
		
		CapturedData ret = new CapturedData();
		Trace trace = getTrace( data.data, file );
		ret.add( trace );
		return ret;
	}
	
	
	static private class DataV1 {
		@SuppressWarnings("unused") String format;
		TracePoint[] data;
	}
	
	
	private Trace getTrace( TracePoint[] tracePoints, File file ) throws Exception {
		Trace trace = new Trace();
		TracePoint previous = null;
		for ( TracePoint p : tracePoints ) {
			if ( (previous == null) || (p.getTime() != previous.getTime()) ) {
				trace.add(p);
			}
			else {
				if ( !p.equals( previous ) ) {
					throw new InvalidInputException( "Time " + p.getTime() + " repeated in file " + file.getPath() );
				}
			}
			previous = p;
		}
		return trace;
	}
}
