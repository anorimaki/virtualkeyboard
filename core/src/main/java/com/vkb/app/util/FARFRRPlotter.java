package com.vkb.app.util;

import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.quality.farfrr.FARFRRCalculator;

public class FARFRRPlotter {
	public XYPlot plot( double[] thresholds, List<FARFRRCalculator.Result> results ) throws Exception {
		XYPlot tracesPlot = generateBasePlot();
		XYSeriesCollection plotTraces = generatePlotSeries( thresholds, results );
		tracesPlot.setDataset( 0, plotTraces );
		return tracesPlot;
	}
	
	private XYSeriesCollection generatePlotSeries( double[] thresholds,
										List<FARFRRCalculator.Result> results ) throws Exception{
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
}
