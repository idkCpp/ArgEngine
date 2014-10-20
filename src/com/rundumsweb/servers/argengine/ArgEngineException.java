package com.rundumsweb.servers.argengine;

public class ArgEngineException extends Exception {
	private static final long serialVersionUID = -3920545938621958247L;

	public ArgEngineException() {
		super();
	}
	
	public ArgEngineException(Throwable t) {
		super(t);
	}
	
	public ArgEngineException(String s) {
		super(s);
	}
	
	public ArgEngineException(String s, Throwable t) {
		super(s,t);
	}
}
