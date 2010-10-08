package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
@MappedSuperclass
public abstract class ProcessLog {
	
	public static final String EVENT_DERIVED="derive_node";
	public static final String EVENT_LEAVE="leave_node";
	public static final String EVENT_CREATE_TASK="task_created";
	public static final String EVENT_FINISH_TASK="task_finished";
	
	protected String processInstanceId;
	
	protected Date logTime;
	
	protected String processNodeId;
	
	protected String event;
	
	protected String forkId;
	
	public String getForkId() {
		return forkId;
	}

	public void setForkId(String forkId) {
		this.forkId = forkId;
	}
	@Transient
	protected abstract Serializable getTaskId_Internal();
	@Transient
	protected abstract void setTaskId_Internal(Serializable taskId);
	@Transient
	public abstract Serializable getLogId_Internal();
	@Transient
	public abstract void setLogId_Internal(Serializable id);
	@Transient
	public abstract Serializable getParentLogId_Internal();
	@Transient
	public abstract void setParentLogId_Internal(Serializable parentLogId);

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getProcessNodeId() {
		return processNodeId;
	}

	public void setProcessNodeId(String processNodeId) {
		this.processNodeId = processNodeId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
	public String toString(){
		return "{logId:"+this.getLogId_Internal()+" processInstanceId:"+this.processInstanceId+" processNodeId:"+this.processNodeId+" parentLogId:"+this.getParentLogId_Internal()+" event:"+this.event+" taskId:"+this.getTaskId_Internal()+" logTime:"+this.logTime+" }";
	}
	
	

}
