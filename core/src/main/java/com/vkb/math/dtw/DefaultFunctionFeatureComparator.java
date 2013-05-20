package com.vkb.math.dtw;

import com.fastdtw.util.EuclideanDistance;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;

public class DefaultFunctionFeatureComparator implements FunctionFeatureComparator {
	private FunctionComparator functionComparator;
	
	public DefaultFunctionFeatureComparator() {
	//	functionComparator = new OriginalDTW( new EuclideanCellCostFunction() );
		functionComparator = new FastDTW( new EuclideanDistance() );
	}
	

	@Override
	public double distance( FunctionFeatureDatas features1, FunctionFeatureDatas features2 ) throws Exception {
		return functionComparator.distance( features1.getSamples(), features2.getSamples() );
	}
	

	@Override
	public double distance( FunctionFeatureData feature1, FunctionFeatureData feature2 ) throws Exception {
		return functionComparator.distance( feature1.getVectorFunction(), feature2.getVectorFunction() );
	}
}
