package com.vkb.alg;

import java.util.List;

import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class GenericSignatureValidator implements SignatureValidator, CapturedDataValidator, SignatureBuilder {
	private SignatureValidator impl;
	private SignatureBuilder signatureBuilder;
	
	public GenericSignatureValidator( SignatureBuilder signatureBuilder, 
										SignaturePatternBasedValidator validator,
										List<CapturedData> capturedDatas ) throws Exception {
		this.signatureBuilder = signatureBuilder;
		
		List<Signature> patternTraces = new SignaturesBuilder(signatureBuilder).buildSignatures(capturedDatas);
		validator.setPattern(patternTraces);
		
		this.impl = validator;
	}
	
	public GenericSignatureValidator( SignatureBuilder signatureBuilder, SignatureValidator impl ) throws Exception {
		this.signatureBuilder = signatureBuilder;
		this.impl = impl;
	}
	
	@Override
	public boolean check( CapturedData capturedData ) throws Exception {
		Signature signature = signatureBuilder.buildSignature( capturedData );
		return check( signature );
	}
	
	@Override
	public boolean check( Signature signature ) throws Exception {
		return impl.check( signature );
	}

	@Override
	public Signature buildSignature(CapturedData capturedData) throws Exception {
		return signatureBuilder.buildSignature(capturedData);
	}
}
