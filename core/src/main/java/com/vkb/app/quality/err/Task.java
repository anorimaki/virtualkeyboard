package com.vkb.app.quality.err;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.vkb.model.Signature;

class Task implements Callable<TaskResult> {
	private ThresholdedSignatureValidator validator;
	private List<List<Signature>> signaturesList;
	private double[] thresholdsToCheck ;
	
	public Task( ThresholdedSignatureValidator validator, 
					List<List<Signature>> signaturesList,
					double[] thresholdsToCheck ) {
		this.validator = validator;
		this.signaturesList = signaturesList;
		this.thresholdsToCheck = thresholdsToCheck;
	}

	@Override
	public TaskResult call() throws Exception {
		TaskResult result = new TaskResult();
		
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
