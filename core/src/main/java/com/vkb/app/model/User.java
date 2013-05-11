package com.vkb.app.model;

import java.util.List;

import com.vkb.model.Signature;

public class User<T> {
	private T validatorAlgorithm;
	private List<Signature> ownSignatures;
	
	public User( T validatorAlgorithm, List<Signature> ownSignatures ) throws Exception {
		this.validatorAlgorithm = validatorAlgorithm;
		this.ownSignatures = ownSignatures;
	}
	
	public List<Signature> getOwnSignatures() {
		return ownSignatures;
	}
	
	public T getValidator() {
		return validatorAlgorithm;
	}
}
