package com.rundumsweb.servers.argengine;

public class IncompatibleArgumentsException extends ArgEngineException {
	private static final long serialVersionUID = -3647433875243549177L;

	public IncompatibleArgumentsException() {
		super();
	}
	
	public IncompatibleArgumentsException(Throwable t) {
		super(t);
	}
	
	public IncompatibleArgumentsException(String s) {
		super(s);
	}
	
	public IncompatibleArgumentsException(String s, Throwable t) {
		super(s,t);
	}
}
