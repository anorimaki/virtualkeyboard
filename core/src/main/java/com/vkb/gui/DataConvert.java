package com.vkb.gui;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.math.FunctionPoints;
import com.vkb.math.Point;
import com.vkb.model.FunctionFeature;

public class DataConvert {
	public static XYDataset getDataset( FunctionFeature... functions ) {
		XYSeriesCollection ret = new XYSeriesCollection();
		for( FunctionFeature f : functions ) {
			ret.addSeries( getXYSeries(f) );
		}
		return ret ;
	}
	
	public static XYSeries getXYSeries(FunctionFeature f) {
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
