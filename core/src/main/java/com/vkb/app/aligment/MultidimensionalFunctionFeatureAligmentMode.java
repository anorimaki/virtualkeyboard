package com.vkb.app.aligment;

import java.util.Set;

import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.dtw.CellCostFunction;
import com.vkb.math.dtw.FunctionWarper;
import com.vkb.math.dtw.FunctionWarper.Result;
import com.vkb.math.dtw.OriginalDTW;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;
import com.vkb.model.Signature;

public class MultidimensionalFunctionFeatureAligmentMode implements FunctionFeatureAligmentMode {
	private CellCostFunction cellCostFunction;
	
	public MultidimensionalFunctionFeatureAligmentMode(CellCostFunction cellCostFunction) {
		this.cellCostFunction = cellCostFunction;
	}

	@Override
	public Result apply( Signature signature1, Signature signature2 ) throws Exception {
		Set<FunctionFeatureData> featuresFunctions1 = 
				signature1.getFeatures().getAllDatasByModel( FunctionFeatureData.class );
		FunctionFeatureDatas features1 = new FunctionFeatureDatas(featuresFunctions1);
		DiscreteVectorFunction functions1 = features1.getSamples();
		
		Set<FunctionFeatureData> featuresFunctions2 = 
				signature2.getFeatures().getAllDatasByModel( FunctionFeatureData.class );
		FunctionFeatureDatas features2 = new FunctionFeatureDatas(featuresFunctions2);
		DiscreteVectorFunction functions2 = features2.getSamples();
	
		FunctionWarper functionAligner = new OriginalDTW( cellCostFunction );
		return functionAligner.align( functions1, functions2 );
	}

}
