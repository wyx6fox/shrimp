package com.xysd.internal_wf.operation;

import java.io.Serializable;

import com.xysd.internal_wf.domain.BeanFactory;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessDefinition;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInstance;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;
import com.xysd.internal_wf.domain.parser.XmlParser;

public class ProcessEngineFactory {

	// 流程实例ID生成器
	private ProcessIdGenerator processIdGenerator;
	// 流程查询/持久化接口
	private ProcessPersistence processPersistence;

	private BeanFactory beanFactory;

	private ProcessDeployer processDeployer;

	private boolean useTimer = false;

	private int sleepSeconds = 0; // 以分钟为单位

	private String processVariableClassName=StrTypeProcessVariable.class.getName();

	private String processLockerClassName=StrTypeProcessLocker.class.getName();

	private String processLogClassName=StrTypeProcessLog.class.getName();

	private String taskClassName=StrTypeWorkflowTask.class.getName();

	private boolean initTimer = false;

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public String getProcessVariableClassName() {
		return processVariableClassName;
	}

	public void setProcessVariableClassName(String processVariableClassName) {
		this.processVariableClassName = processVariableClassName;
	}

	public String getProcessLockerClassName() {
		return processLockerClassName;
	}

	public void setProcessLockerClassName(String processLockerClassName) {
		this.processLockerClassName = processLockerClassName;
	}

	public String getProcessLogClassName() {
		return processLogClassName;
	}

	public void setProcessLogClassName(String processLogClassName) {
		this.processLogClassName = processLogClassName;
	}

	public void setProcessPersistence(ProcessPersistence processPersistence) {
		this.processPersistence = processPersistence;
	}

	public ProcessPersistence getProcessPersistence() {
		return processPersistence;
	}

	public boolean isUseTimer() {
		return useTimer;
	}

	public void setUseTimer(boolean useTimer) {
		this.useTimer = useTimer;
	}

	public ProcessEngineFactory(ProcessIdGenerator processIdGenerator,
			ProcessPersistence processPersistence, BeanFactory beanFactory,
			ProcessDeployer processDeployer) {
		super();
		this.processIdGenerator = processIdGenerator;
		this.processPersistence = processPersistence;
		this.beanFactory = beanFactory;
		this.processDeployer = processDeployer;
	}

	private ProcessEngine getProcessEngine(ProcessDefinition processDefinition) {
		XmlParser parser = new XmlParser(processDefinition.getContent());
		ProcessConfig config = parser.parse();
		if (this.taskClassName != null)
			config.setTaskClassName(this.taskClassName);
		if (processLockerClassName != null)
			config.setProcessLockerClassName(this.processLockerClassName);
		if (processLogClassName != null)
			config.setProcessLogClassName(this.processLogClassName);
		if (processVariableClassName != null)
			config.setProcessVariableClassName(this.processVariableClassName);
		config.setProcessDefinitionId(processDefinition.getId());
		ProcessEngine engine = new ProcessEngine(config);
		engine.setProcessIdGenerator(this.processIdGenerator);
		engine.setProcessPersistence(this.processPersistence);
		engine.setProcessDeployer(this.processDeployer);
		engine.setBeanFactory(beanFactory);
		return engine;
	}

	public ProcessEngine getProcessEngine(String processName) {
		ProcessDefinition lastedProcessDefinition = this.processDeployer
				.getLastProcessDefinition(processName);
		return this.getProcessEngine(lastedProcessDefinition);
	}

	public ProcessEngine getProcessEngine(ProcessTaskInstance task) {
		ProcessInstance processInstance = this.processPersistence
				.findProcessInstance(task.getProcessInstanceId());
		ProcessDefinition processDefinition = this.processDeployer
				.findProcessDefinition(processInstance.getProcessDefinitionId());
		if (processDefinition == null)
			throw new RuntimeException("can't found processDefinition by task:"
					+ task);
		return getProcessEngine(processDefinition);

	}

	@SuppressWarnings("unchecked")
	public ProcessEngine getProcessEngine(Class taskClass, Serializable taskId) {
		ProcessTaskInstance task = this.processPersistence.findTaskById(
				taskClass, taskId);
		return getProcessEngine(task);
	}

	public void init() {
		if (this.useTimer && this.sleepSeconds > 0 && !this.initTimer) {
			ProcessTimer timer = new ProcessTimer(this.sleepSeconds, this);
			Thread thread = new Thread(timer);
			thread.start();
			this.initTimer = true;

		}
	}

	public int getSleepSeconds() {
		return sleepSeconds;
	}

	public void setSleepSeconds(int sleepSeconds) {
		this.sleepSeconds = sleepSeconds;
	}

	public ProcessEngineFactory() {
	}

	public ProcessIdGenerator getProcessIdGenerator() {
		return processIdGenerator;
	}

	public void setProcessIdGenerator(ProcessIdGenerator processIdGenerator) {
		this.processIdGenerator = processIdGenerator;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public ProcessDeployer getProcessDeployer() {
		return processDeployer;
	}

	public void setProcessDeployer(ProcessDeployer processDeployer) {
		this.processDeployer = processDeployer;
	}

	public boolean isInitTimer() {
		return initTimer;
	}

	public void setInitTimer(boolean initTimer) {
		this.initTimer = initTimer;
	}

}
