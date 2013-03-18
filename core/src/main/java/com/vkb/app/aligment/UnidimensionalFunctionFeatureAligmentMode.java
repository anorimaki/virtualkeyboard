package com.vkb.app.aligment;

import com.vkb.math.DiscreteFunction;
import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.dtw.CellCostFunction;
import com.vkb.math.dtw.FunctionWarper;
import com.vkb.math.dtw.FunctionWarper.Result;
import com.vkb.math.dtw.OriginalDTW;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class UnidimensionalFunctionFeatureAligmentMode implements FunctionFeatureAligmentMode {
	private FeatureId featureId;
	private CellCostFunction cellCostFunction;
	
	public UnidimensionalFunctionFeatureAligmentMode( FeatureId featureId, CellCostFunction cellCostFunction ) {
		this.featureId = featureId;
		this.cellCostFunction = cellCostFunction;
	}
	
	@Override
	public Result apply(Signature signature1, Signature signature2) throws Exception {
		FunctionFeatureData fv1 = signature1.getFeature( featureId ).getData();
		DiscreteFunction f1 = fv1.getSamples();
		FunctionFeatureData fv2 = signature2.getFeature( featureId ).getData();
		DiscreteFunction f2 = fv2.getSamples();
		
		FunctionWarper functionAligner = new OriginalDTW( cellCostFunction );
		return functionAligner.align( new DiscreteVectorFunction(f1), new DiscreteVectorFunction(f2) );
	}
}
