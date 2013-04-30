package com.vkb.math.dtw;

import com.fastdtw.util.EuclideanDistance;
import com.vkb.math.DiscreteVectorFunction;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;

public class FunctionFeatureComparator {
	private FunctionComparator functionComparator;
	
	public FunctionFeatureComparator() {
	//	functionComparator = new OriginalDTW( new EuclideanCellCostFunction() );
		functionComparator = new FastDTW( new EuclideanDistance() );
	}
	
	public double distance( FunctionFeatureDatas features1, FunctionFeatureDatas features2 ) throws Exception {
		return functionComparator.distance( features1.getSamples(), features2.getSamples() );
	}
	
	public double distance( FunctionFeatureData feature1, FunctionFeatureData feature2 ) throws Exception {
		DiscreteVectorFunction function1 = new DiscreteVectorFunction( feature1.getSamples() );
		DiscreteVectorFunction function2 = new DiscreteVectorFunction( feature2.getSamples() );
		return functionComparator.distance( function1, function2 );
	}
}
