package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 
 * 流程流转日志记录。
 * 记录几个关键数据：流程实例ID（processInstanceId），当前节点ID（processNodeId），事件（event），分支的ID(forkId)
 * 抽象属性： taskId,logId,parentLogId 
 * 其中taskId表示生成任务日志的任务标识，parentLogId表示这次当前这次流转过程中的前面一次动作的日志。
 * 通过parentLogId的记录，能够通过日志表跟踪得到一次流程流转过程中的事件发生过程。
 * 
 * 对于一个fork-join组合，在 fork.enter方法中将生成一个enter的Processlog实例，其中event="enter",forkId等于当前fork节点
 * 被分配的一个唯一标识。 在join.leave方法中将生成"leave"事件的日志，其中的forkId等于前面fork节点生成的forkId
 * 
 * 
 * 
 * @author wyx6fox
 * 
 */
@MappedSuperclass
public abstract class ProcessLog {

	public static final String EVENT_DERIVED = "derive_node";
	public static final String EVENT_LEAVE = "leave_node";
	public static final String EVENT_CREATE_TASK = "task_created";
	public static final String EVENT_FINISH_TASK = "task_finished";

	protected String processInstanceId; // 流程实例ID

	protected Date logTime;

	protected String processNodeId; // 当前节点ID

	protected String event; // 事件

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

	public String toString() {
		return "{logId:" + this.getLogId_Internal() + " processInstanceId:"
				+ this.processInstanceId + " processNodeId:"
				+ this.processNodeId + " parentLogId:"
				+ this.getParentLogId_Internal() + " event:" + this.event
				+ " taskId:" + this.getTaskId_Internal() + " logTime:"
				+ this.logTime + " }";
	}

}
