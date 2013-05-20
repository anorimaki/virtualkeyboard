package com.vkb.io;

import java.util.ArrayList;
import java.util.List;

import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;
import com.vkb.model.User;

public class NoOpUserLoaderTraits {
	public static class Factory implements SignatureValidatorFactory<Validator> {
		private DefaultSignatureBuilder signatureBuilder;

		public Factory() {
			this.signatureBuilder = new DefaultSignatureBuilder();
		}

		@Override
		public Validator generateValidatorFromCaptures( List<CapturedData> patternSamples ) throws Exception {
			List<Signature> patternSignatures = signatureBuilder.buildSignatures( patternSamples );
			return new Validator( patternSignatures, signatureBuilder );
		}

		@Override
		public Validator generateValidatorFromSignatures( List<Signature> patternSamples) throws Exception {
			return new Validator( patternSamples, signatureBuilder );
		}
	}

	public static class Validator implements SignatureBuilder {
		private List<Signature> signatures ;
		private SignatureBuilder signatureBuilder;

		public Validator( List<Signature> signatures, SignatureBuilder signatureBuilder ) {
			this.signatureBuilder = signatureBuilder;
			this.signatures = signatures;
		}	


		public List<Signature> getPatternSignatures() {
			return signatures;
		}


		@Override
		public Signature buildSignature(CapturedData capturedData) throws Exception {
			return signatureBuilder.buildSignature(capturedData);
		}
	}
	
	public static <T> List<User<T>> convert( List<User<NoOpUserLoaderTraits.Validator>> users,
								SignatureValidatorFactory<T> validatorFactory ) throws Exception {
		
		List<User<T>> ret = new ArrayList<User<T>>();
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			T validator = validatorFactory.generateValidatorFromSignatures(
											user.getValidator().getPatternSignatures() );
			User<T> newUser = new User<T>( user.getId(), validator, user.getOwnSignatures() );
			ret.add( newUser );
		}
		return ret;
	}
}
