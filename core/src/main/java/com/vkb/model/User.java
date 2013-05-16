package com.vkb.model;

import java.util.List;


public class User<T> {
	private String id;
	private T validatorAlgorithm;
	private List<Signature> ownSignatures;
	
	public User( String id, T validatorAlgorithm, List<Signature> ownSignatures ) throws Exception {
		this.validatorAlgorithm = validatorAlgorithm;
		this.ownSignatures = ownSignatures;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public List<Signature> getOwnSignatures() {
		return ownSignatures;
	}
	
	public T getValidator() {
		return validatorAlgorithm;
	}
}
