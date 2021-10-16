package com.alkemy.ong.exception;

public class EntityNotFoundException extends ONGException {

	private static final long serialVersionUID = 1L;

	public EntityNotFoundException() {
		super();
	}
	
	public EntityNotFoundException(String message) {
		super(message);
	}
	
}
