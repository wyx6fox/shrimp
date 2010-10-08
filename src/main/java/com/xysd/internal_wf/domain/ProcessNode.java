package com.xysd.internal_wf.domain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessNode implements ProcessNodeListener {

	protected Log logger = LogFactory.getLog(this.getClass());

	protected String processNodeId;

	protected Map<String, Transition> transitions = new LinkedHashMap<String, Transition>();

	protected ProcessNodeTask task; // 只支持一个Task的生成
	
	protected String enterInvoker;
	
	protected String leaveInvoker;

	

	public String getLeaveInvoker() {
		return leaveInvoker;
	}

	public void setLeaveInvoker(String leaveInvoker) {
		this.leaveInvoker = leaveInvoker;
	}

	public String getEnterInvoker() {
		return enterInvoker;
	}

	public void setEnterInvoker(String enterInvoker) {
		this.enterInvoker = enterInvoker;
	}

	public String getProcessNodeId() {
		return processNodeId;
	}

	public void setProcessNodeId(String processNodeId) {
		this.processNodeId = processNodeId;
	}

	public Map<String, Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(Map<String, Transition> transitions) {
		this.transitions = transitions;
	}

	public ProcessNodeTask getTask() {
		return task;
	}

	public void setTask(ProcessNodeTask task) {
		this.task = task;
	}

	public ProcessNode(String processNodeId) {
		super();
		this.processNodeId = processNodeId;
	}
	
	protected void callEnterInvokerIfNcessary(ProcessContext context){
		if(this.enterInvoker!=null)
			context.getProcessEngine().invoke(this.enterInvoker, context);
	}
	
	protected void callLeaveInvokerIfNcessary(ProcessContext context){
		if(this.leaveInvoker!=null)
			context.getProcessEngine().invoke(this.leaveInvoker, context);
	}

	public void enter(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug( " enter processNode:" + this+" with context:"+context );
		}
		this.callEnterInvokerIfNcessary(context);
		if (task != null) {
			context.getProcessEngine().createTask(context, task);
		}
		else{
			//没有任务就自动离开
			this.leave(context);
		}

	}

	public void leave(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug(" now leave:" + this + " with context:"+context);
		}
		//登记日志
		context.getProcessEngine().registerNodeLogIfNecessary(context,ProcessLog.EVENT_LEAVE);
		this.callLeaveInvokerIfNcessary(context);
		String transitName = context.getUserTransit();
		Transition transit = null;
		if (transitName != null) {
			transit = this.transitions.get(transitName);
		} else {
			Iterator<Transition> iter = this.transitions.values().iterator();
			if (iter.hasNext())
				transit = iter.next();
		}
		if (transit != null) {
			context.getProcessEngine().doTransit(context, transit);
		} else{
			if(transitName==null)
				throw new RuntimeException("no transit found in node:" + this);
			else
				throw new RuntimeException("no transit found by name:"
						+ transitName + " in node:" + this);
		}

	}

	public void transitDerived(Transition transit, ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("derived to processNode:" + this+" with transit:"+transit+"  with context:"+context );
		}
		context.setCurrProcessNode(this);
		//登记日志
		context.getProcessEngine().registerNodeLogIfNecessary(context,ProcessLog.EVENT_DERIVED);
		//抵达之后，记得清空transitName
		context.clearTransit();
		//默认行为就是执行enter
		this.enter(context);
	}

	public String toString() {
		return this.processNodeId;
	}

}
