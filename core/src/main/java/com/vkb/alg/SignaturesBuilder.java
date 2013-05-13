package com.vkb.alg;

import java.util.ArrayList;
import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class SignaturesBuilder {
	SignatureBuilder impl;
	
	public SignaturesBuilder( SignatureBuilder impl ) {
		this.impl = impl;
	}
	
	public List<Signature> buildSignatures( List<CapturedData> capturesDatas ) throws Exception {
		ArrayList<Signature> ret = new ArrayList<Signature>();
		for( CapturedData rawTrace : capturesDatas ) {
			ret.add( impl.buildSignature(rawTrace) );
		}
		return ret;
	}
	
	public static List<Signature> build( SignatureBuilder impl, List<CapturedData> capturesDatas ) throws Exception {
		return new SignaturesBuilder( impl ).buildSignatures( capturesDatas );
	}
}
