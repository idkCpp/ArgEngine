package com.rundumsweb.servers.argengine;

public class TooManyArgumentsException extends ArgEngineException {
	private static final long serialVersionUID = 1428299227869610713L;

	public TooManyArgumentsException() {
		super();
	}
	
	public TooManyArgumentsException(Throwable t) {
		super(t);
	}
	
	public TooManyArgumentsException(String s) {
		super(s);
	}
	
	public TooManyArgumentsException(String s, Throwable t) {
		super(s,t);
	}
}
