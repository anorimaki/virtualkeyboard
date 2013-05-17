package com.vkb.quality.farfrr.ui;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.vkb.quality.farfrr.FARFRRCalculator;

public class FARFRRPrinter {
	private Writer output;
	
	public FARFRRPrinter() {
		output = new OutputStreamWriter( System.out );
	}
	
	public void print( double[] thresholds, List<FARFRRCalculator.Result> results ) throws Exception {
		for( int i=0; i<thresholds.length; ++i ) {
			display( thresholds[i], results.get(i) );
		}
	}

	private void display( double d, FARFRRCalculator.Result errResult ) throws Exception {
		FARFRRCalculator.Result.Matrix matrix = errResult.getMatrix();
		
		output.write("Threshold:" + d + "\n" );
		output.write("-------------------------------\n");
		for ( int i=0; i<matrix.size(); ++i ) {
			for ( int j=0; j<matrix.size(); ++j ) {
				double itemResult = matrix.get( i, j ) * 100.0d;
				output.write( format(itemResult) + "   " );
			}
			output.write( "\n" );
		}
		
		output.write( "-> FRR:" + errResult.getFRR() + "   FAR:" + errResult.getFAR() + "\n\n" );
		output.flush();
	}
	
	private String format( double d ) {
		return String.format( "%3.1f", d );
	}
}
