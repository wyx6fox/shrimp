package com.xysd.internal_wf.operation;

import java.io.Serializable;

public class IncrementNumProcessIdGenerator implements
		ProcessIdGenerator {
	
	private int num=0;
	
	private int forkNum=0;
	
	private int lockNum=0;
	
	private int logNum=0;

	public String generateProcessInstanceId() {
		num++;
		return num+"";
	}

	public String generateForkId() {
		
		forkNum++;
		return forkNum+"";
	}

	public Serializable generateLockerId() {
		lockNum++;
		return lockNum+"";
	}

	public Serializable generateLogId() {
		logNum++;
		return logNum+"";
	}

}
