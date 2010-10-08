package com.xysd.internal_wf.operation;

import java.io.File;

import com.xysd.internal_wf.domain.ProcessDefinition;

public interface ProcessDeployer {

	public void deployProcesses(File[] files);

	public ProcessDefinition getLastProcessDefinition(String processName);

	public ProcessDefinition findProcessDefinition(String processDefinitionId);

}
