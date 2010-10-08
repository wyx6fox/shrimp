package com.xysd.internal_wf.domain.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xysd.internal_wf.domain.ProcessTaskInstance;
@Entity
@Table(name = "wf_task")
public class StrTypeWorkflowTask extends ProcessTaskInstance {

	private String id;

	private String actor;

	private String creator;
	
	private String projectId;
	
	private String parentLogId;

	public String getParentLogId() {
		return parentLogId;
	}

	public void setParentLogId(String parentLogId) {
		this.parentLogId = parentLogId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	@Id
	public String getId() {

		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Transient
	public Serializable getActor_Internal() {

		return this.actor;
	}

	@Transient
	public Serializable getCreator_Internal() {
		
		return this.creator;
	}

	@Transient
	public void setActor_Internal(Serializable actor) {
		this.actor=actor+"";

	}


	@Transient
	public void setCreator_Internal(Serializable trigger) {
		this.creator = trigger+"";

	}

	@Transient
	public Serializable getProjectId_Internal() {
		
		return this.projectId;
	}

	@Transient
	public void setProjectId_Internal(Serializable projectId) {
		this.projectId =  projectId+"";
		
	}

	@Transient
	public Serializable getId_Internal() {
		
		return this.id;
	}
	

	@Transient
	public void setId_Internal(Serializable id) {
		
		this.id=id+"";
	}

	@Transient
	public Serializable getParentLogId_Internal() {
		return this.parentLogId;
	}

	@Transient
	public void setParentLogId_Internal(Serializable parentLogId) {
		this.parentLogId = (parentLogId+"");
		
	}

}
