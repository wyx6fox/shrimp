package com.xysd.internal_wf.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Transition  {
	
	protected Log logger = LogFactory.getLog(this.getClass()); 

	private ProcessConfig processConfig;

	private ProcessNode src;

	private ProcessNode target;
	
	private String targetNodeId;

	private String transitName;

	private List<Action> actions = new ArrayList<Action>();

	private String conditionInvoker; // 条件为true是可以迁移

	

	public String getConditionInvoker() {
		return conditionInvoker;
	}

	public void setConditionInvoker(String conditionInvoker) {
		this.conditionInvoker = conditionInvoker;
	}

	public ProcessNode getSrc() {
		return src;
	}

	public void setSrc(ProcessNode src) {
		this.src = src;
	}

	public ProcessNode getTarget() {
		return target;
	}

	public void setTarget(ProcessNode target) {
		this.target = target;
	}

	public String getTransitName() {
		return transitName;
	}

	public void setTransitName(String transitName) {
		this.transitName = transitName;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> callbacks) {
		this.actions = callbacks;
	}

	public void execute(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("execute this transit:"+this+" with context:"+context);
		}
		// processNode.leave()后被触发 transit.execute()
		for (Action callback : this.actions) {
			if(callback.getInvoker()!=null)
				context.getProcessEngine().invoke(callback.getInvoker(), context);
		}
		// transit完成后
		this.target.transitDerived(this, context);

	}

	public ProcessConfig getProcessConfig() {
		return processConfig;
	}

	public void setProcessConfig(ProcessConfig processConfig) {
		this.processConfig = processConfig;
	}

	public Transition from(String nodeId) {
		ProcessNode pn = this.getProcessConfig().getProcessNodeById(nodeId);
		if (pn == null)
			throw new RuntimeException(
					"can not found any processNode by this nodeId:" + nodeId);
		this.src = pn;
		pn.getTransitions().put(this.transitName, this);
		return this;
	}

	public Transition to(String nodeId, Action[] callbacks) {
		ProcessNode pn = this.getProcessConfig().getProcessNodeById(nodeId);
		if (pn == null)
			throw new RuntimeException(
					"can not found any processNode by this nodeId:" + nodeId);
		this.target = pn;
		if (callbacks != null) {
			for (Action callback : callbacks) {
				this.actions.add(callback);
			}
		}
		return this;
	}
	
	public String toString(){
		return "{ transit:src:"+src+"->target:"+target+"}";
	}

	public String getTargetNodeId() {
		return targetNodeId;
	}

	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

}
