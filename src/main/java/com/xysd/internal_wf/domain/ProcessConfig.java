package com.xysd.internal_wf.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessConfig {
	
	private String processDefinitionId;

	private String processName;

	private BeginNode beginNode;

	private EndNode endNode;
	
	private String taskClassName;
	
	private String processVariableClassName;
	
	private String processLockerClassName;
	
	private String processLogClassName;

	private Map<String, ProcessNode> procedureNodes = new LinkedHashMap<String, ProcessNode>();

	public ProcessConfig(String processName,String taskClassName,String processVariableClassName,String processLockerClassName,String processLogClassName) {
		super();
		this.processName = processName;
		this.taskClassName = taskClassName;
		this.processVariableClassName=processVariableClassName;
		this.processLockerClassName = processLockerClassName;
		this.processLogClassName = processLogClassName;
	}

	public ProcessConfig begin(String processNodeId) {
		beginNode = new BeginNode(processNodeId);

		return this;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public BeginNode getBeginNode() {
		return beginNode;
	}

	public void setBeginNode(BeginNode beginNode) {
		this.beginNode = beginNode;
	}

	public EndNode getEndNode() {
		return endNode;
	}


	public Map<String, ProcessNode> getProcedureNodes() {
		return procedureNodes;
	}

	public void setProcedureNodes(Map<String, ProcessNode> procedureNodes) {
		this.procedureNodes = procedureNodes;
	}

	public ProcessConfig addProcedureNodes(String... procedureNodes) {
		if (procedureNodes != null) {
			for (String node : procedureNodes) {
				if (node != null) {
					ProcessNode pn = null;
					String realNodeName = node;
					if(node.startsWith("fork:")){
						realNodeName = node.replaceFirst("fork:", "");
						pn = new ForkNode(realNodeName);
					}
					else if(node.startsWith("join:")){
						realNodeName = node.replaceFirst("join:", "");
						pn = new JoinNode(realNodeName);
					}
					else if(node.startsWith("multiple:")){
						realNodeName = node.replaceFirst("multiple:", "");
						pn = new MultipleNode(realNodeName);
					}
					else if(node.startsWith("multiple_step:")){
						realNodeName = node.replaceFirst("multiple_step:", "");
						pn = new MultipleStepNode(realNodeName);
					}
					else
						pn = new ProcessNode(realNodeName);
					if(pn!=null)
						this.procedureNodes.put(realNodeName, pn);
				}
			}
		}
		return this;
	}

	public ProcessConfig end(String nodeName) {
		endNode = new EndNode(nodeName);
		return this;
	}
	
	public ProcessNode getProcessNodeById(String nodeId){
		if(nodeId==null)
			return null;
		ProcessNode pn = null;
		if(nodeId.equals(this.getBeginNode().getProcessNodeId()))
			pn =  this.getBeginNode();
		if(pn==null)
			pn=this.procedureNodes.get(nodeId);
		if(pn==null){
			if(nodeId.equals(this.getEndNode().getProcessNodeId()))
				pn =  this.getEndNode();
		}
		return pn;
		
	}

	
	public Transition transit(String transitName) {
		Transition trans = new Transition();
		trans.setProcessConfig(this);
		trans.setTransitName(transitName);
		return trans;
	}

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public void setEndNode(EndNode endNode) {
		this.endNode = endNode;
	}
	/**
	 * 想processNodeId为参数nodeId的processNode中添加task.
	 * task的名字为{taskName},其他的设置为默认配置。
	 * @param nodeId
	 * @param taskName
	 */
	public ProcessNodeTask setTask(String nodeId, String taskName) {
		ProcessNode pn = this.getProcessNodeById(nodeId);
		if(pn==null)
			throw new RuntimeException("can not found processNode by NodeId:"+nodeId);
		ProcessNodeTask taskConfig = new ProcessNodeTask();
		taskConfig.setName(taskName);
		pn.setTask(taskConfig);
		return taskConfig;
		
	}

	public String getProcessVariableClassName() {
		return processVariableClassName;
	}

	public String getProcessLockerClassName() {
		return processLockerClassName;
	}

	public String getProcessLogClassName() {
		return processLogClassName;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setProcessVariableClassName(String processVariableClassName) {
		this.processVariableClassName = processVariableClassName;
	}

	public void setProcessLockerClassName(String processLockerClassName) {
		this.processLockerClassName = processLockerClassName;
	}

	public void setProcessLogClassName(String processLogClassName) {
		this.processLogClassName = processLogClassName;
	}
}
