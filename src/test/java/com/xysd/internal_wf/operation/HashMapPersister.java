package com.xysd.internal_wf.operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xysd.internal_wf.domain.ProcessInstance;
import com.xysd.internal_wf.domain.ProcessLocker;
import com.xysd.internal_wf.domain.ProcessLog;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.ProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class HashMapPersister implements ProcessPersistence {

	Map<String, ProcessTaskInstance> store = new LinkedHashMap<String, ProcessTaskInstance>();

	Map<String, ProcessVariable> vars = new LinkedHashMap<String, ProcessVariable>();

	Map<String, ProcessLog> logs = new LinkedHashMap<String, ProcessLog>();

	Map<String, ProcessInstance> processes = new LinkedHashMap<String, ProcessInstance>();

	int num;

	@SuppressWarnings("unchecked")
	public List find(String hql, Object... values) {

		throw new UnsupportedOperationException("no support HQL query");
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> entityClass, Serializable id) {

		return (T) store.get(entityClass + "_" + id);
	}

	public void saveTask(ProcessTaskInstance task) {
		if (task.getId_Internal() == null) {
			num++;
			task.setId_Internal(num + "");
		}
		store.put(task.getClass() + "_" + task.getId_Internal(), task);

	}

	public List<ProcessTaskInstance> getAllOpenTasks() {
		List<ProcessTaskInstance> result = new ArrayList<ProcessTaskInstance>();
		for (ProcessTaskInstance t : store.values()) {
			if (ProcessTaskInstance.STATUS_OPEN == t.getStatus())
				result.add(t);
		}
		return result;
	}

	public List<ProcessTaskInstance> findAllOpenTasks(String processInstanceId) {
		List<ProcessTaskInstance> tasks = new ArrayList<ProcessTaskInstance>();
		for (ProcessTaskInstance t : store.values()) {
			if (processInstanceId.equals(t.getProcessInstanceId())
					&& ProcessTaskInstance.STATUS_OPEN == t.getStatus()
					&& ProcessTaskInstance.TASK_TYPE_USER.equals(t
							.getTaskType()))
				tasks.add(t);
		}
		return tasks;
	}

	public List<ProcessTaskInstance> findAllOpenSystemTasks(
			String processInstanceId) {
		List<ProcessTaskInstance> tasks = new ArrayList<ProcessTaskInstance>();
		for (ProcessTaskInstance t : store.values()) {
			if (processInstanceId.equals(t.getProcessInstanceId())
					&& ProcessTaskInstance.STATUS_OPEN == t.getStatus()
					&& !ProcessTaskInstance.TASK_TYPE_USER.equals(t
							.getTaskType()))
				tasks.add(t);
		}
		return tasks;
	}

	public int findOpenTaskCountByProcessInstanceIdAndForkId(
			String currProcessInstanceId, String forkId) {
		if (forkId == null)
			throw new RuntimeException("forkId should not be null");
		List<ProcessTaskInstance> tasks = this.findAllTasksWithSystemTasks(
				currProcessInstanceId, ProcessTaskInstance.STATUS_OPEN);
		int c = 0;
		for (ProcessTaskInstance t : tasks) {
			if (forkId.equals(t.getForkId()))
				c++;
		}
		return c;
	}

	public ProcessTaskInstance findForkTaskByProcessInstanceIdAndOwnForkId(
			String currProcessInstanceId, String forkId, byte status) {
		List<ProcessTaskInstance> tasks = this.findAllTasksWithSystemTasks(
				currProcessInstanceId, status);
		for (ProcessTaskInstance t : tasks) {
			if (ProcessTaskInstance.TASK_TYPE_FORK.equals(t.getTaskType())
					&& forkId.equals(t.getOwnForkId())) {
				return t;
			}
		}
		return null;
	}

	public List<ProcessTaskInstance> findAllTasksWithSystemTasks(
			String processInstanceId, byte status) {
		List<ProcessTaskInstance> tasks = new ArrayList<ProcessTaskInstance>();
		for (ProcessTaskInstance t : store.values()) {
			if (processInstanceId.equals(t.getProcessInstanceId())
					&& status == t.getStatus())
				tasks.add(t);
		}
		return tasks;
	}

	public String toString() {
		return this.store + "";
	}

	public ProcessVariable findProcessVariable(String processInstanceId,
			String varName, String uniqueId) {

		return this.vars
				.get(processInstanceId + "_" + varName + "_" + uniqueId);
	}

	public void saveProcessVariable(ProcessVariable var) {
		this.vars.put(var.getProcessInstanceId() + "_" + var.getVarName() + "_"
				+ var.getUniqueId(), var);

	}

	public List<ProcessTaskInstance> findAllOpenTasksWithSystemTasks(
			String processInstanceId) {

		return this.findAllTasksWithSystemTasks(processInstanceId,
				ProcessTaskInstance.STATUS_OPEN);
	}

	public ProcessLocker lock(ProcessLocker lock) {
		return null;

	}
	
	public ProcessLocker findProcessLocker(String currProcessInstanceId, String processNodeId){
		return null;
	}

	public void saveProcessLocker(ProcessLocker lock) {

	}

	public void saveProcessLog(ProcessLog logRec) {
		this.logs.put(logRec.getLogId_Internal() + "", logRec);

	}

	public List<ProcessLog> findProcessLogs(String processInstanceId,
			String nodeId) {
		List<ProcessLog> result = new ArrayList<ProcessLog>();
		String k = processInstanceId + "_" + nodeId;
		for (ProcessLog l : this.logs.values()) {
			if (k.equals(l.getProcessInstanceId() + "_" + l.getProcessNodeId())) {
				result.add(l);
			}
		}
		return result;

	}
	
	public List<ProcessLog> findProcessLogs(String processInstanceId,
			String nodeId,String event) {
		List<ProcessLog> result = new ArrayList<ProcessLog>();
		String k = processInstanceId + "_" + nodeId;
		for (ProcessLog l : this.logs.values()) {
			if (k.equals(l.getProcessInstanceId() + "_" + l.getProcessNodeId())&&event.equals(l.getEvent())) {
				result.add(l);
			}
		}
		return result;

	}

	public void printAllLogs() {

		for (ProcessLog l : logs.values()) {
			System.out.println(l);
		}
	}

	public ProcessInstance findProcessInstance(String processInstanceId) {

		return processes.get(processInstanceId);
	}

	@SuppressWarnings("unchecked")
	public ProcessTaskInstance findTaskById(Class taskClass, Serializable taskId) {
		return (ProcessTaskInstance) this.get(taskClass, taskId);
	}

	public List<ProcessVariable> findProcessVarsByProcessInstanceIdAndVarName(
			String processInstanceId, String varName) {
		List<ProcessVariable> vars = new ArrayList<ProcessVariable>();
		for (ProcessVariable v : this.vars.values()) {
			if (processInstanceId.equals(v.getProcessInstanceId())
					&& varName.equals(v.getVarName()))
				vars.add(v);
		}
		return vars;
	}

	public void saveProcessInstance(ProcessInstance processInstance) {
		this.processes.put(processInstance.getProcessInstanceId(),
				processInstance);

	}

	public void lockTask(ProcessTaskInstance task) {
		
		
	}

	public boolean isFinishedTask(Serializable taskId) {
		
		return ProcessTaskInstance.STATUS_CLOSE == this.get(StrTypeWorkflowTask.class, taskId).getStatus();
	}

	public List<ProcessLog> findProcessLogs(String currProcessInstanceId,
			String processNodeId, String event, String forkId) {
		List<ProcessLog> logs = this.findProcessLogs(currProcessInstanceId, processNodeId,event);
		List<ProcessLog> result = new ArrayList<ProcessLog>();
		for(ProcessLog l:logs){
			if(forkId!=null&&forkId.equals(l.getForkId()))
				result.add(l);
		}
		return result;
	}

	public List<ProcessTaskInstance> findExpiredLimitedOpenTasks() {
		List<ProcessTaskInstance> tasks = new ArrayList<ProcessTaskInstance>();
		Date now = new Date();
		for(ProcessTaskInstance task:this.store.values()){
			if(ProcessTaskInstance.STATUS_OPEN==task.getStatus()&&ProcessTaskInstance.TASK_TYPE_USER.equals(task.getTaskType())){
				if(task.getRealLimitDate()!=null&&task.getRealLimitDate().before(now))
					tasks.add(task);
			}
		}
		return tasks;
	}

}
