package com.rundumsweb.servers.argengine;

public class RequirementUnsatisfiedException extends ArgEngineException {
	private static final long serialVersionUID = 1422223439479442375L;

	public RequirementUnsatisfiedException() {
		super();
	}
	
	public RequirementUnsatisfiedException(Throwable t) {
		super(t);
	}
	
	public RequirementUnsatisfiedException(String s) {
		super(s);
	}
	
	public RequirementUnsatisfiedException(String s, Throwable t) {
		super(s,t);
	}
}
