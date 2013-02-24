package com.vkb.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.vkb.model.CapturedData;
import com.vkb.model.Trace;
import com.vkb.model.TracePoint;

public class CapturedDataParser {
	
	public CapturedData parse( File file ) throws Exception {
		Gson gson = new Gson();
		
		Reader reader = new FileReader( file );
		Data data = gson.fromJson(reader, Data.class);
		reader.close();
		
		Trace trace = new Trace();
		TracePoint previous = null;
		for ( TracePoint p : data.data ) {
			if ( (previous == null) || (p.getTime() != previous.getTime()) ) {
				trace.add(p);
			}
			else {
				if ( !p.equals( previous ) ) {
					throw new InvalidInputException( "Time " + p.getTime() + " repeated in file " + file .getPath() );
				}
			}
			previous = p;
		}
		
		return new CapturedData( trace );
	}
	
	static private class Data {
		@SuppressWarnings("unused")
		String format;
		TracePoint[] data;
	}
}
