package com.vkb.quality.farfrr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.model.Signature;

class ThresholdTask implements Callable<ThresholdTaskResult> {
	private ThresholdedSignatureValidator validator;
	private List<List<Signature>> signaturesList;
	private double[] thresholdsToCheck ;
	
	public ThresholdTask( ThresholdedSignatureValidator validator, 
					List<List<Signature>> signaturesList,
					double[] thresholdsToCheck ) {
		this.validator = validator;
		this.signaturesList = signaturesList;
		this.thresholdsToCheck = thresholdsToCheck;
	}

	@Override
	public ThresholdTaskResult call() throws Exception {
		ThresholdTaskResult result = new ThresholdTaskResult();
		
		for ( double thresholdToCheck : thresholdsToCheck ) {
			validator.setThreshold( thresholdToCheck );
			
			List<boolean[]> threadholdResult = new ArrayList<boolean[]>();
			for( List<Signature> signatures : signaturesList ) {
				
				boolean[] userResult = new boolean[signatures.size()];
				for( int i = 0; i<signatures.size(); ++i ) {
					userResult[i] = validator.check( signatures.get(i) );
				}
				threadholdResult.add( userResult );
			}
			
			result.add( threadholdResult );
		}
		
		return result;
	}

}
