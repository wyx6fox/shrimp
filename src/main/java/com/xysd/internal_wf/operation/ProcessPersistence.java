package com.xysd.internal_wf.operation;

import java.io.Serializable;
import java.util.List;

import com.xysd.internal_wf.domain.ProcessInstance;
import com.xysd.internal_wf.domain.ProcessLocker;
import com.xysd.internal_wf.domain.ProcessLog;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.ProcessVariable;

public interface ProcessPersistence {

	public void saveTask(ProcessTaskInstance task);


	public List<ProcessTaskInstance> findAllOpenTasks(String processInstanceId);

	

	public int findOpenTaskCountByProcessInstanceIdAndForkId(
			String currProcessInstanceId, String forkId);

	public ProcessVariable findProcessVariable(String processInstanceId,
			String varName, String uniqueId);

	public void saveProcessVariable(ProcessVariable var);

	public ProcessTaskInstance findForkTaskByProcessInstanceIdAndOwnForkId(
			String currProcessInstanceId, String forkId, byte status);

	public List<ProcessTaskInstance> findAllTasksWithSystemTasks(
			String processInstanceId, byte status);

	public List<ProcessTaskInstance> findAllOpenTasksWithSystemTasks(
			String processInstanceId);

	public ProcessLocker lock(ProcessLocker lock);
	public ProcessLocker findProcessLocker(String currProcessInstanceId, String processNodeId);

	public void saveProcessLocker(ProcessLocker lock);

	public void saveProcessLog(ProcessLog logRec);

	public List<ProcessLog> findProcessLogs(String processInstanceId, String nodeId);
	
	public List<ProcessLog> findProcessLogs(String processInstanceId, String nodeId,String event);

	@SuppressWarnings("unchecked")
	public ProcessTaskInstance findTaskById( Class taskClass,Serializable taskId);


	public ProcessInstance findProcessInstance(
			String processInstanceId);


	public List<ProcessVariable> findProcessVarsByProcessInstanceIdAndVarName(
			String processInstanceId, String varName);


	public void saveProcessInstance(ProcessInstance processInstance);


	public void lockTask(ProcessTaskInstance task);


	public boolean isFinishedTask(Serializable taskId);


	public List<ProcessLog> findProcessLogs(String currProcessInstanceId,
			String processNodeId, String event, String forkId);


	public List<ProcessTaskInstance> findExpiredLimitedOpenTasks();

}
