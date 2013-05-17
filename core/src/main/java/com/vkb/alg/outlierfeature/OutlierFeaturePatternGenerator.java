package com.vkb.alg.outlierfeature;

import java.util.List;
import java.util.Map;

import com.vkb.alg.SignaturePatternBasedValidator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.math.dtw.PreCalculatedFunctionFeatureComparator;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;

public class OutlierFeaturePatternGenerator {
	public static class Result {
		private OutlierFeatureSignaturePattern pattern;
		private List<Signature> unusedSignatures;
		
		public Result( OutlierFeatureSignaturePattern pattern, List<Signature> unusedSignatures ) {
			this.unusedSignatures = unusedSignatures;
			this.pattern = pattern;
		}
		
		public List<Signature> getUnusedSignatures() {
			return unusedSignatures;
		}
		
		public OutlierFeatureSignaturePattern getPattern() {
			return pattern;
		}
	}
	
	private double filterThreshold;
	private FunctionFeatureComparator normalfunctionComparator;
	

	public OutlierFeaturePatternGenerator( double filterThreshold, 
									FunctionFeatureComparator functionFeatureComprator ) {
		this.filterThreshold = filterThreshold;
		this.normalfunctionComparator = functionFeatureComprator;
	}
	
	
	public Result generate( List<Signature> patternSignatures ) throws Exception {
		PreCalculatedFunctionFeatureComparator preCalculatedFunctionComparator =
							compareFunctionFeatures( patternSignatures );

		SamplesPatternFilter.Result filteredSignatures = filterPatternSignatures( patternSignatures, preCalculatedFunctionComparator );
		
		OutlierFeatureSignaturePattern pattern =
							new OutlierFeatureSignaturePattern( filteredSignatures.getPassedSignatures(), 
															preCalculatedFunctionComparator );
		pattern.setFunctionComparator( normalfunctionComparator );	
		return new Result( pattern, filteredSignatures.getUnpassedSignatures() );
	}
	
	
	private PreCalculatedFunctionFeatureComparator compareFunctionFeatures(
								List<Signature> patternSignatures ) throws Exception {
		PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();

		Map<FeatureId, List<FunctionFeatureData>> featuresDatas = 
					Signatures.extractFeatureDatasByModel( patternSignatures, FunctionFeatureData.class );
		for( Map.Entry<FeatureId, List<FunctionFeatureData>> featureDatas : featuresDatas.entrySet() ) {
			ret.put( normalfunctionComparator, featureDatas.getValue() );
		}
		return ret;
	}
	
	
	private SamplesPatternFilter.Result filterPatternSignatures( List<Signature> patternSignatures, 
					FunctionFeatureComparator preCalculatedFunctionComparator ) throws Exception {
		PatternFilterValidator patternFilterValidator = 
					new PatternFilterValidator( preCalculatedFunctionComparator, filterThreshold );
		SamplesPatternFilter patternFilter = new SamplesPatternFilter( patternFilterValidator );
		return patternFilter.filter( patternSignatures );
	}

	
	private static class PatternFilterValidator implements SignaturePatternBasedValidator {
		private OutlierFeatureSignaturePattern pattern;
		private FunctionFeatureComparator functionComparator;
		private double threshold;
		
		public PatternFilterValidator( FunctionFeatureComparator functionComparator, double threshold ) {
			this.functionComparator = functionComparator;
			this.threshold = threshold;
		}
		
		@Override
		public void setPattern( List<Signature> patternSignatures ) throws Exception {
			pattern = new OutlierFeatureSignaturePattern( patternSignatures, functionComparator );
		}
		
		@Override
		public boolean check( Signature signature ) throws Exception {
			double insidersRate = pattern.compare(signature);
			return insidersRate >= threshold;
		}
	}
}