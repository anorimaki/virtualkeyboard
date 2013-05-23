package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.math.Statistics;
import com.vkb.math.dtw.DefaultFunctionFeatureComparator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.CapturedData;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;


public interface FunctionFeatureDeterminer {
	public void setFunctionComparator( FunctionFeatureComparator comparator );
	public void setThreshold( double threshold );
	public boolean check( FunctionFeatureData featureData ) throws Exception;
}

// CANVIAR-HO A LÍNIA 108 alg.outlierfeature.OutlierFeatureSignaturePattern.java