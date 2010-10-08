package com.xysd.internal_wf.domain.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xysd.internal_wf.domain.ProcessLog;
@Entity
@Table(name = "wf_proclog")
public class StrTypeProcessLog  extends ProcessLog{
	
	private String logId;
	
	private String parentLogId;
	
	private String taskId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Id
	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getParentLogId() {
		return parentLogId;
	}

	public void setParentLogId(String parentLogId) {
		this.parentLogId = parentLogId;
	}

	@Transient
	public Serializable getLogId_Internal() {
		
		return this.logId;
	}

	@Transient
	public Serializable getParentLogId_Internal() {
		
		return this.parentLogId;
	}

	@Transient
	public void setLogId_Internal(Serializable id) {
		this.logId=id+"";
		
	}

	@Transient
	public void setParentLogId_Internal(Serializable parentLogId) {
		this.parentLogId = (parentLogId+"");
		
	}

	@Transient
	protected Serializable getTaskId_Internal() {
		
		return this.taskId;
	}

	@Transient
	protected void setTaskId_Internal(Serializable taskId) {
		this.taskId = (taskId+"");
		
	}

}
