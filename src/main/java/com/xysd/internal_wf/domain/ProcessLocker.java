package com.xysd.internal_wf.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
@MappedSuperclass
public abstract class ProcessLocker {
	
	protected String processInstanceId;
	
	protected String processNodeId;
	
	protected byte locker;

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessNodeId() {
		return processNodeId;
	}

	public void setProcessNodeId(String processNodeId) {
		this.processNodeId = processNodeId;
	}

	public byte getLocker() {
		return locker;
	}

	public void setLocker(byte locker) {
		this.locker = locker;
	}
	@Transient
	public abstract Serializable getId_Internal();
	
	public abstract void setId_Internal(Serializable id);
	
	

}
