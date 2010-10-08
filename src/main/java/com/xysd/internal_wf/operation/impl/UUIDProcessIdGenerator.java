package com.xysd.internal_wf.operation.impl;

import java.io.Serializable;
import java.util.UUID;

import com.xysd.internal_wf.operation.ProcessIdGenerator;

public class UUIDProcessIdGenerator implements
		ProcessIdGenerator {

	public String generateProcessInstanceId() {
		return UUID.randomUUID().toString();
	}

	public String generateForkId() {
		
		return "fork_"+UUID.randomUUID().toString();
	}

	public Serializable generateLockerId() {
		
		return "locker_"+UUID.randomUUID().toString();
	}

	public Serializable generateLogId() {
		
		return "logger_"+UUID.randomUUID().toString();
	}

}
