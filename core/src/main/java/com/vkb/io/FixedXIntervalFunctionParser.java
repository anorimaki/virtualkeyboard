package com.vkb.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.vkb.math.DiscreteFunction;

public class FixedXIntervalFunctionParser {
	private long interval;
	
	public FixedXIntervalFunctionParser( long interval ) {
		this.interval = interval;
	}
	
	public DiscreteFunction parse( String name, File file ) throws Exception {
		DiscreteFunction ret = new DiscreteFunction(name);
		parse( file, ret );
		return ret;
	}
	
	public DiscreteFunction parse( File file, DiscreteFunction ret ) throws Exception {
		Gson gson = new Gson();
		
		Reader reader = new FileReader( file );
		double[] data = gson.fromJson(reader, double[].class);
		reader.close();
		
		long time = 0;
		for( double current : data ) {
			ret.add( time, current );
			time += interval;
		}
		
		return ret;
	}

}
