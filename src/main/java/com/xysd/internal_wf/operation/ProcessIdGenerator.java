package com.xysd.internal_wf.operation;

import java.io.Serializable;

public interface ProcessIdGenerator {
	
	public String generateProcessInstanceId();
	
	public String generateForkId();

	public Serializable generateLockerId();

	public Serializable generateLogId();

}
