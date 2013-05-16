package com.vkb.quality.farfrr.ui;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;

import com.vkb.quality.farfrr.FARFRRCalculator;

public class FARFRRPrinter {
	private Writer output;
	private DecimalFormat numFormatter;
	
	public FARFRRPrinter() {
		output = new OutputStreamWriter( System.out );
		numFormatter = new DecimalFormat( "##0.0" );
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
				double itemResult = matrix.get( i, j ) + 100.0d;
				output.write( numFormatter.format(itemResult) + "   " );
			}
			output.write( "\n" );
		}
		
		output.write( "-> FRR:" + errResult.getFRR() + "   FAR:" + errResult.getFAR() + "\n\n" );
		output.flush();
	}
}
