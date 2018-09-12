package com.bbaker.discord.swrpg.exceptions;

public class SetupException extends Throwable {

	public SetupException(String messageTemplate, String... args) {
		super(String.format(messageTemplate, args));
	}
	
}
