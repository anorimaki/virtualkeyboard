package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;

public class FunctionFeatureWarper {
	private FunctionWarper functionWarper;
	
	public FunctionFeatureWarper() {
		functionWarper = new OriginalDTW( new EuclideanCellCostFunction() );
	}
	
	public FunctionWarper.Result align( FunctionFeatureDatas features1, FunctionFeatureDatas features2 ) throws Exception {
		return functionWarper.align( features1.getSamples(), features2.getSamples() );
	}
	
	public FunctionWarper.Result align( FunctionFeatureData feature1, FunctionFeatureData feature2 ) throws Exception {
		DiscreteVectorFunction function1 = new DiscreteVectorFunction( feature1.getSamples() );
		DiscreteVectorFunction function2 = new DiscreteVectorFunction( feature2.getSamples() );
		return functionWarper.align( function1, function2 );
	}
}
