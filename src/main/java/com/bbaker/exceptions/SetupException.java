package com.bbaker.exceptions;

public class SetupException extends Throwable {

	public SetupException(String messageTemplate, String... args) {
		super(String.format(messageTemplate, args));
	}
	
}
