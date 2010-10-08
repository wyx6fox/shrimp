package com.xysd.internal_wf.domain.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xysd.internal_wf.domain.ProcessLocker;
@Entity
@Table(name = "wf_proclock")
public class StrTypeProcessLocker extends ProcessLocker {
	
	private String id;
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Transient
	public Serializable getId_Internal() {
		
		return this.id;
	}

	@Transient
	public void setId_Internal(Serializable id) {
		this.id = (id+"");

	}

}
