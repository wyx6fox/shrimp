package com.xysd.internal_wf.domain.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xysd.internal_wf.domain.ProcessVariable;
@Entity
@Table(name = "wf_procvar")
public class StrTypeProcessVariable extends ProcessVariable {
	
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
		return id;
	}

	@Transient
	public void setId_Internal(Serializable id) {
		this.id = id+"";

	}

}
