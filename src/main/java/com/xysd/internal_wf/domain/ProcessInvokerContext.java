package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Map;

public class ProcessInvokerContext {
	
	private ProcessContext context;
	public ProcessInvokerContext(ProcessContext context) {
		super();
		this.context = context;
	}

	public ProcessTaskInstance getPrevTaskInstance() {
		return context.getPrevTaskInstance();
	}

	

	public Serializable getParentLogId() {
		return context.getParentLogId();
	}

	

	public String getForkId() {
		return context.getForkId();
	}

	

	public Map<String, Object> getUserVariables() {
		return context.getUserVariables();
	}

	public String getCurrProcessInstanceId() {
		return context.getCurrProcessInstanceId();
	}

	public ProcessNode getCurrProcessNode() {
		return context.getCurrProcessNode();
	}




	public ProcessEngine getProcessEngine() {
		return context.getProcessEngine();
	}

	public String toString() {
		return "{ProcessInvokerContext : "+this.context+"}";

	}

	

	public Transition getCurrTransit() {
		return context.getCurrTransit();
	}
}
