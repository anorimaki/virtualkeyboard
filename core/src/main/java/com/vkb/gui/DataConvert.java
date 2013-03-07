package com.vkb.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.math.DiscreteFunction;
import com.vkb.model.FunctionFeatureData;

public class DataConvert {
	public static XYDataset getDataset( FunctionFeatureData... features ) {
		return getDatasetFromFeatures( Arrays.asList(features) );
	}
	
	public static XYDataset getDatasetFromFeatures( List<FunctionFeatureData> features ) {
		List<DiscreteFunction> aux = new ArrayList<DiscreteFunction>();
		for( FunctionFeatureData feature : features ) {
			aux.add( feature.getSamples() );
		}
		return getDatasetFromFunctions( aux );
	}

	public static XYDataset getDataset( DiscreteFunction... functions ) {
		return getDatasetFromFunctions( Arrays.asList(functions) );
	}
	
	public static XYDataset getDatasetFromFunctions( List<DiscreteFunction> functions ) {
		XYSeriesCollection ret = new XYSeriesCollection();
		for( DiscreteFunction f : functions ) {
			String name = f.getName();
			
			int index = 1;
			while ( ret.getSeriesIndex(name) != -1 ) {
				name = f.getName() + "(" + index + ")" ;
			}
			
			ret.addSeries( getXYSeries( name, f.getPoints() ) );
		}
		return ret ;
	}
	
	public static XYSeries getXYSeries(FunctionFeatureData f) {
		return getXYSeries( f.getSamples() );
	}
	
	public static XYSeries getXYSeries( DiscreteFunction function ) {
		return getXYSeries( function.getName(), function.getPoints() );
	}
	
	public static XYDataset getDataset( String name, Iterable<DiscreteFunction.Point> points ) {
		return new XYSeriesCollection(getXYSeries(name, points));
	}
	
	public static XYSeries getXYSeries( String name, Iterable<DiscreteFunction.Point> points ) {
		XYSeries xySeries = new XYSeries(name, false);
		for( DiscreteFunction.Point p : points ) {
			xySeries.add( p.getX(), p.getY() );
		}
		return xySeries;
	}
}
