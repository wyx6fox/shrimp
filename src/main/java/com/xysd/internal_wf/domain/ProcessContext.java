package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessContext {

	private String currProcessInstanceId;

	private ProcessNode currProcessNode;

	private ProcessEngine processEngine;

	private Map<String, Object> userVariables = new LinkedHashMap<String, Object>();

	private String userTransit;

	private Transition currTransit;

	private String forkId;
	
	private Map<String,ProcessLocker> currLockers = new HashMap<String, ProcessLocker>(); //不能随便清除
	
	private Serializable parentLogId;
	
	private ProcessTaskInstance prevTaskInstance;

	public ProcessTaskInstance getPrevTaskInstance() {
		return prevTaskInstance;
	}

	public void setPrevTaskInstance(ProcessTaskInstance prevTaskInstance) {
		this.prevTaskInstance = prevTaskInstance;
	}

	public Serializable getParentLogId() {
		return parentLogId;
	}

	public void setParentLogId(Serializable parentLogId) {
		this.parentLogId = parentLogId;
	}

	public Map<String, ProcessLocker> getCurrLockers() {
		return currLockers;
	}

	public void setCurrLockers(Map<String, ProcessLocker> currLockers) {
		this.currLockers = currLockers;
	}

	public String getForkId() {
		return forkId;
	}

	public void setForkId(String forkId) {
		this.forkId = forkId;
	}

	public Map<String, Object> getUserVariables() {
		return userVariables;
	}

	public void setUserVariables(Map<String, Object> userVariables) {
		this.userVariables = userVariables;
	}

	public String getCurrProcessInstanceId() {
		return currProcessInstanceId;
	}

	public ProcessNode getCurrProcessNode() {
		return currProcessNode;
	}

	private ProcessContext(ProcessEngine processEngine) {
		super();
		this.processEngine = processEngine;
	}

	public final static ProcessContext build(ProcessEngine processEngine,
			String processInstanceId, ProcessNode processNode,
			Map<String, Object> userVariables,String forkId) {
		ProcessContext context = new ProcessContext(processEngine);
		if (processInstanceId == null)
			throw new RuntimeException("processInstanceId should not be null");
		if (processNode == null)
			throw new RuntimeException("processNode should not be null");
		context.userVariables = userVariables;
		context.currProcessInstanceId = processInstanceId;
		context.currProcessNode = processNode;
		context.forkId=forkId;
		return context;
	}

	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public String toString() {
		return "{  currProcessInstanceId:" + this.currProcessInstanceId
				+ " currProcessNode:" + this.currProcessNode + " forkId:"
				+ this.forkId + " currTransit:" + this.currTransit
				+ " userVariables:" + this.userVariables + "  }";

	}

	public void setCurrProcessNode(ProcessNode currProcessNode) {
		this.currProcessNode = currProcessNode;
	}

	public ProcessContext copy() {
		ProcessContext newContext = new ProcessContext(this.processEngine);
		this.copyAllFields(this, newContext);
		return newContext;
	}

	private void copyAllFields(ProcessContext src, ProcessContext target) {
		Field[] fields = src.getClass().getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			try {
				f.set(target, f.get(src));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Transition getCurrTransit() {
		return currTransit;
	}

	public void setCurrTransit(Transition currTransit) {
		this.currTransit = currTransit;
	}

	public String getUserTransit() {
		return userTransit;
	}

	public void setUserTransit(String userTransit) {
		this.userTransit = userTransit;
	}
	
	public void clearTransit(){
		setUserTransit(null);
		setCurrTransit(null);
	}

}
