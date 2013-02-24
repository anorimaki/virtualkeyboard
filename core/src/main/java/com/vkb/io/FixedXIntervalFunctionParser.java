package com.vkb.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.vkb.FunctionPoints;
import com.vkb.Point;

public class FixedXIntervalFunctionParser {
	private long interval;
	
	public FixedXIntervalFunctionParser( long interval ) {
		this.interval = interval;
	}
	
	public FunctionPoints parse( String name, File file ) throws Exception {
		FunctionPoints ret = new FunctionPoints(name);
		parse( file, ret );
		return ret;
	}
	
	public FunctionPoints parse( File file, FunctionPoints ret ) throws Exception {
		Gson gson = new Gson();
		
		Reader reader = new FileReader( file );
		double[] data = gson.fromJson(reader, double[].class);
		reader.close();
		
		long time = 0;
		for( double current : data ) {
			ret.add( new Point( time, current ) );
			time += interval;
		}
		
		return ret;
	}

}
