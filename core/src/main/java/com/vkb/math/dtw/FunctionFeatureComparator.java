package com.vkb.math.dtw;

import com.vkb.model.FunctionFeatureData;
import com.vkb.model.FunctionFeatureDatas;

public interface FunctionFeatureComparator {
	public double distance(FunctionFeatureDatas features1, FunctionFeatureDatas features2) throws Exception;
	public double distance(FunctionFeatureData feature1, FunctionFeatureData feature2) throws Exception;
}