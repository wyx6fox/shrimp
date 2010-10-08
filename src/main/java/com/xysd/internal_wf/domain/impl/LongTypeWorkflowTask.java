package com.xysd.internal_wf.domain.impl;

import java.io.Serializable;

import javax.persistence.Transient;

import com.xysd.internal_wf.domain.ProcessTaskInstance;

public class LongTypeWorkflowTask extends ProcessTaskInstance {
	
	private Long id;

	private Long actor;

	private Long trigger;
	
	private Long projectId;
	
	private Long parentLogId;

	public Long getParentLogId() {
		return parentLogId;
	}

	public void setParentLogId(Long parentLogId) {
		this.parentLogId = parentLogId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getId() {

		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getActor() {
		return actor;
	}

	public void setActor(Long actor) {
		this.actor = actor;
	}

	public Long getTrigger() {
		return trigger;
	}

	public void setTrigger(Long trigger) {
		this.trigger = trigger;
	}

	@Transient
	public Serializable getActor_Internal() {

		return this.actor;
	}

	@Transient
	public Serializable getCreator_Internal() {
		
		return this.trigger;
	}

	@Transient
	public void setActor_Internal(Serializable actor) {
		this.actor=new Long(actor+"");

	}


	@Transient
	public void setCreator_Internal(Serializable trigger) {
		this.trigger = new Long(trigger+"");

	}

	@Transient
	public Serializable getProjectId_Internal() {
		
		return this.projectId;
	}

	@Transient
	public void setProjectId_Internal(Serializable projectId) {
		this.projectId = new Long(projectId+"");
		
	}
	
	@Override
	public Serializable getId_Internal() {
		
		return this.id;
	}

	@Override
	public void setId_Internal(Serializable id) {
		this.id = new Long(id+"");
		
	}

	@Override
	public Serializable getParentLogId_Internal() {
		return this.parentLogId;
	}

	@Override
	public void setParentLogId_Internal(Serializable parentLogId) {
		this.parentLogId = new Long(parentLogId+"");
		
	}

}
