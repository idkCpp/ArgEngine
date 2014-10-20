package com.rundumsweb.servers.argengine;

public class TypeMismatchException extends ArgEngineException {
	private static final long serialVersionUID = 4384406334834906409L;

	public TypeMismatchException() {
		super();
	}
	
	public TypeMismatchException(Throwable t) {
		super(t);
	}
	
	public TypeMismatchException(String s) {
		super(s);
	}
	
	public TypeMismatchException(String s, Throwable t) {
		super(s,t);
	}
}
