package com.vkb.gui;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.FeatureFunction;
import com.vkb.FunctionPoints;
import com.vkb.Point;

public class DataConvert {
	public static XYDataset getDataset( FeatureFunction... functions ) {
		XYSeriesCollection ret = new XYSeriesCollection();
		for( FeatureFunction f : functions ) {
			ret.addSeries( getXYSeries(f) );
		}
		return ret ;
	}
	
	public static XYSeries getXYSeries(FeatureFunction f) {
		return getXYSeries( f.getSamples() );
	}

	public static XYDataset getDataset( FunctionPoints... functions ) {
		XYSeriesCollection ret = new XYSeriesCollection();
		for( FunctionPoints f : functions ) {
			ret.addSeries( getXYSeries(f) );
		}
		return ret ;
	}
	
	public static XYSeries getXYSeries( FunctionPoints points ) {
		return getXYSeries( points.getName(), points.getPoints() );
	}
	
	public static XYDataset getDataset( String name, Iterable<Point> points ) {
		return new XYSeriesCollection(getXYSeries(name, points));
	}
	
	public static XYSeries getXYSeries( String name, Iterable<Point> points ) {
		XYSeries xySeries = new XYSeries(name, false);
		for( Point p : points ) {
			xySeries.add( p.getX(), p.getY() );
		}
		return xySeries;
	}
}
