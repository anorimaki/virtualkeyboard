package com.vkb.quality.farfrr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.vkb.alg.SignatureValidator;
import com.vkb.model.Signature;

class Task implements Callable<TaskResult> {
	private SignatureValidator validator;
	private List<List<Signature>> signaturesList;
	
	public Task( SignatureValidator validator, 
					List<List<Signature>> signaturesList ) {
		this.validator = validator;
		this.signaturesList = signaturesList;
	}

	@Override
	public TaskResult call() throws Exception {
		List<boolean[]> list = new ArrayList<boolean[]>();
		for( List<Signature> signatures : signaturesList ) {
				
			boolean[] userResult = new boolean[signatures.size()];
			for( int i = 0; i<signatures.size(); ++i ) {
				userResult[i] = validator.check( signatures.get(i) );
			}
			list.add( userResult );
		}
		
		return new TaskResult( list );
	}

}
