package com.xysd.internal_wf.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity(name="xywf_ProcessInstance")
@Table(name = "wf_process")
public class ProcessInstance {
	
	
	private String processInstanceId;
	
	private String processDefinitionId;
	@Id
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	

}
