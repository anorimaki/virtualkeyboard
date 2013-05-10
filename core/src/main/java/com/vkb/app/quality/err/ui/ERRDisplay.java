package com.vkb.app.quality.err.ui;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.app.quality.err.ERRResult;

public class ERRDisplay {
	private Writer output;
	private DecimalFormat numFormatter;
	
	public ERRDisplay() {
		output = new OutputStreamWriter( System.out );
		numFormatter = new DecimalFormat( "000.00%" );
	}
	
	public void print( double[] thresholds, List<ERRResult> results ) throws Exception {
		for( int i=0; i<thresholds.length; ++i ) {
			display( thresholds[i], results.get(i) );
		}
	}
	
	public XYPlot plot( double[] thresholds, List<ERRResult> results ) throws Exception {
		XYPlot tracesPlot = generateBasePlot();
		XYSeriesCollection plotTraces = generatePlotSeries( thresholds, results );
		tracesPlot.setDataset( 0, plotTraces );
		return tracesPlot;
	}
	
	private XYPlot generateBasePlot() throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		// Linies i punts visibles
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
		plot.setRenderer( renderer );
		
		return plot;
	}

	
	private XYSeriesCollection generatePlotSeries( double[] thresholds, List<ERRResult> results ) throws Exception{
		XYSeries xySeriesFar = new XYSeries("FAR", false);
		XYSeries xySeriesFrr = new XYSeries("FRR", false);
		
		for ( int i=0; i<thresholds.length; ++i ) {
			xySeriesFar.add( thresholds[i], results.get(i).getFAR() );
			xySeriesFrr.add( thresholds[i], results.get(i).getFRR() );
		}
		
		XYSeriesCollection ret = new XYSeriesCollection();
		ret.addSeries(xySeriesFar);
		ret.addSeries(xySeriesFrr);
		return ret;
	}
	
	private void display( double d, ERRResult errResult ) throws Exception {
		ERRResult.Matrix matrix = errResult.getMatrix();
		
		output.write("Threshold:" + d + "\n" );
		output.write("-------------------------------\n");
		for ( int i=0; i<matrix.size(); ++i ) {
			for ( int j=0; j<matrix.size(); ++j ) {
				double itemResult = matrix.get( i, j );
				output.write( numFormatter.format(itemResult) + "   " );
			}
			output.write( "\n" );
		}
		
		output.write( "-> FRR:" + errResult.getFRR() + "   FAR:" + errResult.getFAR() + "\n\n" );
		output.flush();
	}
}
