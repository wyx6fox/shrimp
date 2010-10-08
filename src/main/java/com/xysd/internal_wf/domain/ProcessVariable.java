package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
@MappedSuperclass
public abstract class ProcessVariable {
	protected String processInstanceId;
	
	protected String varName;
	
	protected String uniqueId;
	
	protected Long long1;
	
	protected Long long2;
	
	protected String str1;
	
	protected String str2;
	
	protected Date date1;
	
	protected Date date2;
	
	protected Double double1;
	
	protected Double double2;
	
	
	
	

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Long getLong1() {
		return long1;
	}

	public void setLong1(Long long1) {
		this.long1 = long1;
	}

	public Long getLong2() {
		return long2;
	}

	public void setLong2(Long long2) {
		this.long2 = long2;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}
	@Transient
	public abstract void setId_Internal(Serializable id);
	@Transient
	public abstract Serializable getId_Internal();
	
	

}
