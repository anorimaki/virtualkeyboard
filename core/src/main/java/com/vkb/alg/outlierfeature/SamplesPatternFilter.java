package com.vkb.alg.outlierfeature;

import java.util.ArrayList;
import java.util.List;

import com.vkb.alg.SignaturePatternBasedValidator;
import com.vkb.model.Signature;

public class SamplesPatternFilter {
	public static class Result {
		private List<Signature> passedSignatures;
		private List<Signature> unpassedSignatures;
		
		public Result( List<Signature> passedSignatures, List<Signature> unpassedSignatures ) {
			this.passedSignatures = passedSignatures;
			this.unpassedSignatures = unpassedSignatures;
		}
		
		public List<Signature> getPassedSignatures() {
			return passedSignatures;
		}
		
		public List<Signature> getUnpassedSignatures() {
			return unpassedSignatures;
		}
	}
	
	private SignaturePatternBasedValidator validator;
	
	public SamplesPatternFilter( SignaturePatternBasedValidator validator ) {
		this.validator = validator;
	}
	
	public Result filter( List<Signature> all ) throws Exception {
		List<Signature> passedSignatures = new ArrayList<Signature>();
		List<Signature> unpassedSignatures = new ArrayList<Signature>();
		for ( int i=0; i<all.size(); ++i ) {
			Signature signautreToCheck = all.get(i);
			List<Signature> patternToCheck = extract( all, i );
			
			validator.setPattern(patternToCheck);
			boolean pass = validator.check( signautreToCheck );
			
			if ( pass ) {
				passedSignatures.add( signautreToCheck );
			}
			else {
				unpassedSignatures.add( signautreToCheck );
			}
		}
		return new Result( passedSignatures, unpassedSignatures );
	}

	private List<Signature> extract( List<Signature> all, int pos ) {
		List<Signature> ret = new ArrayList<Signature>();
		for ( int i=0; i<all.size(); ++i ) {
			if ( i != pos ) {
				ret.add( all.get(i) );
			}
		}
		return ret;
	}
}
