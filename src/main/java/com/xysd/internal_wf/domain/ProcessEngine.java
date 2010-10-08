package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xysd.internal_wf.domain.exception.TaskFinishedException;
import com.xysd.internal_wf.operation.ProcessDeployer;
import com.xysd.internal_wf.operation.ProcessIdGenerator;
import com.xysd.internal_wf.operation.ProcessPersistence;

public class ProcessEngine {
	protected Log logger = LogFactory.getLog(this.getClass());
	// 流程实例ID生成器
	private ProcessIdGenerator processIdGenerator;
	// 流程查询/持久化接口
	private ProcessPersistence processPersistence;

	private BeanFactory beanFactory;

	// 流程配置
	private ProcessConfig processConfig;
	
	private ProcessDeployer processDeployer;

	@SuppressWarnings("unchecked")
	private Class taskClazz;
	@SuppressWarnings("unchecked")
	private Class processVariableClazz;

	@SuppressWarnings("unchecked")
	private Class processLockerClazz;
	@SuppressWarnings("unchecked")
	private Class processLogClazz;

	private boolean log = true;

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	@SuppressWarnings("unchecked")
	public Class getProcessLogClazz() {
		return processLogClazz;
	}

	@SuppressWarnings("unchecked")
	public Class getProcessVariableClazz() {
		return processVariableClazz;
	}

	@SuppressWarnings("unchecked")
	public Class getTaskClazz() {
		return taskClazz;
	}

	public ProcessConfig getProcessConfig() {
		return processConfig;
	}

	public void setProcessConfig(ProcessConfig processConfig) {
		this.processConfig = processConfig;
	}

	public ProcessEngine(ProcessConfig processConfig) {
		this.processConfig = processConfig;
		this.taskClazz = loadClassByName(processConfig.getTaskClassName());
		this.processVariableClazz = loadClassByName(processConfig
				.getProcessVariableClassName());
		this.processLockerClazz = loadClassByName(processConfig
				.getProcessLockerClassName());
		this.processLogClazz = loadClassByName(processConfig
				.getProcessLogClassName());
	}

	@SuppressWarnings("unchecked")
	private final Class loadClassByName(String className) {
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			try {
				clazz = Thread.currentThread().getContextClassLoader()
						.loadClass(className);
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException(e);
			}
		}
		return clazz;
	}

	public ProcessIdGenerator getProcessIdGenerator() {
		return processIdGenerator;
	}

	public void setProcessIdGenerator(
			ProcessIdGenerator processInstanceIdGenerator) {
		this.processIdGenerator = processInstanceIdGenerator;
	}

	public ProcessPersistence getProcessPersistence() {
		return processPersistence;
	}

	public void setProcessPersistence(ProcessPersistence processPersistence) {
		this.processPersistence = processPersistence;
	}

	@SuppressWarnings("unchecked")
	public ProcessTaskInstance getTaskById(Serializable taskId) {

		return (ProcessTaskInstance) this.processPersistence.findTaskById(this.taskClazz,taskId);
	}

	// 开始任务流程，初始化一个唯一的流程实例ID
	public String beginProcess(Map<String, Object> userVariables) {
		// 获得一个唯一的流程实例ID
		String processInstanceId = this.processIdGenerator
				.generateProcessInstanceId();
		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setProcessInstanceId(processInstanceId);
		processInstance.setProcessDefinitionId(this.processConfig.getProcessDefinitionId());
		this.processPersistence.saveProcessInstance(processInstance);
		ProcessContext context = ProcessContext.build(this, processInstanceId,
				this.getProcessConfig().getBeginNode(), userVariables, null);
		// 给所有Join节点加上ProcessLocker记录。
		initProcessLockersIfNecessary(context);
		if (this.log) {
			// 登记流程日志
			this.registerNodeLogIfNecessary(context,ProcessLog.EVENT_DERIVED);
		}
		this.processConfig.getBeginNode().enter(context);
		return processInstanceId;
	}

	private void initProcessLockersIfNecessary(ProcessContext context) {
		for (ProcessNode n : this.processConfig.getProcedureNodes().values()) {
			if (n instanceof JoinNode) {
				ProcessLocker lock = null;
				try {
					lock = (ProcessLocker) this.processLockerClazz
							.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				lock.setId_Internal(this.processIdGenerator.generateLockerId());
				lock.setProcessInstanceId(context.getCurrProcessInstanceId());
				lock.setProcessNodeId(n.getProcessNodeId());
				this.processPersistence.saveProcessLocker(lock);
			}
		}

	}

	protected ProcessTaskInstance intantiateTask() {
		try {
			ProcessTaskInstance taskInstance=  (ProcessTaskInstance) this.taskClazz.newInstance();
			taskInstance.setTaskClassName(this.taskClazz.getName());
			return taskInstance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建并保存任务
	 * 
	 * @param context
	 * @param taskConfig
	 */
	public void createTask(ProcessContext context, ProcessNodeTask taskConfig) {
		if (taskConfig == null)
			throw new RuntimeException("taskConfig should not be null!");
		if (context == null)
			throw new RuntimeException("context should not be null!");
		ProcessTaskInstance taskInstance = this.intantiateTask();
		taskInstance.setTaskName(taskConfig.getName());
		taskInstance.setStatus(ProcessTaskInstance.STATUS_OPEN);
		taskInstance.setCreateTime(new Date());
		taskInstance.setProcessNode(context.getCurrProcessNode()
				.getProcessNodeId());
		taskInstance.setProcessInstanceId(context.getCurrProcessInstanceId());
		// 指定projectId和projectType
		Serializable projectId = (Serializable) context.getUserVariables().get(
				taskConfig.getProjectIdVar());
		String projectType = (String) context.getUserVariables().get(
				taskConfig.getProjectTypeVar());
		taskInstance.setProjectId_Internal(projectId);
		taskInstance.setProjectType(projectType);
		taskInstance.setTaskType(ProcessTaskInstance.TASK_TYPE_USER);
		taskInstance.setForkId(context.getForkId());
		// 记录parentLogId为了保证有效的日志跟踪
		taskInstance.setParentLogId_Internal(context.getParentLogId());
		// 任务分配
		assignmentTask(taskInstance, context, taskConfig);
		//任务限期处理
		if(taskConfig.getLimitInvoker()!=null){
			Object o = this.invoke(taskConfig.getLimitInvoker(), context);
			if(o!=null){
				if(o instanceof Date)
					taskInstance.limit((Date)o);
				else
					throw new RuntimeException("limitInvoker:"+taskConfig.getLimitInvoker()+" must return a Date !");
			}
		}
		// 保存任务
		this.processPersistence.saveTask(taskInstance);

		if (logger.isDebugEnabled()) {
			logger.debug("created taskInstance:" + taskInstance
					+ " with taskConfig:" + taskConfig + " with context:"
					+ context);
		}
		this.callTaskCreatedInvokerIfNecessary(taskConfig, context);
		if (this.log) {
			this.registerTaskLogIfNecessary(taskInstance, context,
					ProcessLog.EVENT_CREATE_TASK);
		}

	}
	
	
	private void callTaskCreatedInvokerIfNecessary(ProcessNodeTask taskConfig,ProcessContext context){
		if(taskConfig.getTaskCreatedInvoker()!=null)
			context.getProcessEngine().invoke(taskConfig.getTaskCreatedInvoker(), context);
	}
	private void callTaskFinishedInvokerIfNecessary(ProcessNodeTask taskConfig,ProcessContext context){
		if(taskConfig.getTaskFinishedInvoker()!=null)
			context.getProcessEngine().invoke(taskConfig.getTaskFinishedInvoker(), context);
	}

	/**
	 * 创建并Fork任务
	 * 
	 * @param context
	 * @param taskConfig
	 */
	public void createForkTask(ProcessContext context, String parentForkId,
			String ownForkId) {

		if (context == null)
			throw new RuntimeException("context should not be null!");

		ProcessTaskInstance taskInstance = this.intantiateTask();
		taskInstance.setTaskName("fork_task");
		taskInstance.setTaskType(ProcessTaskInstance.TASK_TYPE_FORK);
		taskInstance.setStatus(ProcessTaskInstance.STATUS_OPEN);
		taskInstance.setCreateTime(new Date());
		taskInstance.setProcessNode(context.getCurrProcessNode()
				.getProcessNodeId());
		taskInstance.setProcessInstanceId(context.getCurrProcessInstanceId());
		// 设置父亲forkId以及本身生成的forkId
		taskInstance.setForkId(parentForkId);
		taskInstance.setOwnForkId(ownForkId);

		taskInstance.setParentLogId_Internal(context.getParentLogId());

		// 保存任务
		this.processPersistence.saveTask(taskInstance);

		if (logger.isDebugEnabled()) {
			logger.debug("created fork taskInstance:" + taskInstance
					+ " with  context:" + context);
		}

	}

	/**
	 * 指定任务创建者，创建者类型，所属者以及所属者类型。
	 * 
	 * @param taskInstance
	 * @param context
	 * @param taskConfig
	 */

	private void assignmentTask(ProcessTaskInstance taskInstance,
			ProcessContext context, ProcessNodeTask taskConfig) {
		if (taskConfig.getAssignmeInvoker() == null) {
			Serializable creator = (Serializable) context.getUserVariables()
					.get(taskConfig.getCreatorVar());
			String creatorType = (String) context.getUserVariables().get(
					taskConfig.getCreatorTypeVar());
			Serializable actor = (Serializable) context.getUserVariables().get(
					taskConfig.getActorVar());
			String actorType = (String) context.getUserVariables().get(
					taskConfig.getActorTypeVar());
			if (actor == null)
				throw new RuntimeException(
						"actorId should not be null for this task:"
								+ taskConfig + " context:" + context);
			if (actorType == null)
				throw new RuntimeException(
						"actorType should not be null for this task:"
								+ taskConfig + " context:" + context);
			taskInstance.setActor_Internal(actor);
			taskInstance.setActorType(actorType);
			taskInstance.setCreator_Internal(creator);
			taskInstance.setCreatorType(creatorType);

		} else {
			context.getProcessEngine().invoke(taskConfig.getAssignmeInvoker(),
					context);
		}

	}

	@SuppressWarnings("unchecked")
	public List<ProcessVariable> findProcessVariables(String processInstanceId,
			String varName) {
		return this.processPersistence.findProcessVarsByProcessInstanceIdAndVarName(processInstanceId,varName);
		
	}

	public void finishTask(ProcessTaskInstance task,
			Map<String, Object> userVariables) {
		finishTask(task, userVariables, null);

	}
	//不允许并发执行,必须锁定某个任务
	public void finishTask(ProcessTaskInstance task,
			Map<String, Object> userVariables, String transit) {
		//多人可以处理同一个任务时，必须保证对同一个任务串行执行完成动作
		this.processPersistence.lockTask(task);
		if(this.processPersistence.isFinishedTask(task.getId_Internal())){
			throw new TaskFinishedException(task);
		}
		ProcessNode pn = this.processConfig.getProcessNodeById(task
				.getProcessNode());

		ProcessContext context = ProcessContext.build(this, task
				.getProcessInstanceId(), pn, userVariables, task.getForkId());
		// 为了跟踪流程流转日志，必须记录任务的父级日志ID
		context.setParentLogId(task.getParentLogId_Internal());
		context.setUserTransit(transit);
		ProcessNodeTask taskConfig = pn.getTask();
		if (taskConfig == null)
			throw new RuntimeException(
					"no taskConfig founded by this taskInstance:" + task
							+ " in Node:" + pn);
		task.finish(context);
		this.callTaskFinishedInvokerIfNecessary(taskConfig, context);
		this.registerTaskLogIfNecessary(task, context,
				ProcessLog.EVENT_FINISH_TASK);
		context.setPrevTaskInstance(task);
		pn.leave(context);

	}

	public void doTransit(ProcessContext context, Transition transit) {
		boolean canDoTransit = true;
		if (transit.getConditionInvoker() != null) {
			canDoTransit = ("true".equalsIgnoreCase(context.getProcessEngine()
					.invoke(transit.getConditionInvoker(), context)
					+ ""));
		}
		// 针对每个transit新生成一个Context
		ProcessContext newContext = context.copy();
		newContext.setCurrTransit(transit);
		if (canDoTransit) {
			transit.execute(newContext);
		}

	}

	public List<ProcessTaskInstance> findAllOpenTasks(String processInstanceId) {
		return this.processPersistence.findAllOpenTasks(processInstanceId);

	}

	public Object invoke(String invoker, ProcessContext context) {
		if (this.beanFactory == null)
			return null;
		int dotIdx = invoker.indexOf(".");
		String beanId = invoker.substring(0, dotIdx);
		String method = invoker.substring(dotIdx + 1);
		Object bean = this.beanFactory.getBean(beanId);
		if (bean != null) {
			try {
				Method m = bean.getClass().getMethod(method,
						ProcessInvokerContext.class);
				m.setAccessible(true);
				ProcessInvokerContext invokeContext = new ProcessInvokerContext(context);
				return m.invoke(bean, invokeContext);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public ProcessVariable findProcessVariable(String processInstanceId,
			String varName, String uniqueId, boolean createWhenNotExist) {
		ProcessVariable existedVar = this.processPersistence
				.findProcessVariable(processInstanceId, varName, uniqueId);
		if (createWhenNotExist && existedVar == null) {
			try {
				existedVar = (ProcessVariable) this.processVariableClazz
						.newInstance();
				existedVar.setProcessInstanceId(processInstanceId);
				existedVar.setVarName(varName);
				existedVar.setUniqueId(uniqueId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		return existedVar;
	}

	public void saveProcessVariable(ProcessVariable var) {
		this.processPersistence.saveProcessVariable(var);

	}

	public void registerNodeLogIfNecessary(ProcessContext context,String event) {
		if (this.log) {
			try {
				ProcessLog logRec = (ProcessLog) this.processLogClazz
						.newInstance();
				logRec.setLogId_Internal(this.getProcessIdGenerator()
						.generateLogId());
				logRec.setProcessInstanceId(context.getCurrProcessInstanceId());
				logRec.setForkId(context.getForkId());
				logRec.setLogTime(new Date());
				logRec.setEvent(event);
				logRec.setParentLogId_Internal(context.getParentLogId());
				logRec.setProcessNodeId(context.getCurrProcessNode()
						.getProcessNodeId());
				this.getProcessPersistence().saveProcessLog(logRec);
				// 登记完日志后，将父日志ID设置为当前日志ID
				context.setParentLogId(logRec.getLogId_Internal());
				if (logger.isDebugEnabled()) {
					logger.debug("create LogRec:" + logRec);
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

	}

	public void registerTaskLogIfNecessary(ProcessTaskInstance task,
			ProcessContext context, String event) {
		if (this.log) {
			try {
				ProcessLog logRec = (ProcessLog) this.processLogClazz
						.newInstance();
				logRec.setLogId_Internal(this.getProcessIdGenerator()
						.generateLogId());
				logRec.setProcessInstanceId(context.getCurrProcessInstanceId());
				logRec.setForkId(context.getForkId());
				logRec.setLogTime(new Date());
				logRec.setEvent(event);
				logRec.setTaskId_Internal(task.getId_Internal());
				logRec.setParentLogId_Internal(context.getParentLogId());
				logRec.setProcessNodeId(context.getCurrProcessNode()
						.getProcessNodeId());
				this.getProcessPersistence().saveProcessLog(logRec);
				if (logger.isDebugEnabled()) {
					logger.debug("create LogRec:" + logRec);
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

	}

	public ProcessDeployer getProcessDeployer() {
		return processDeployer;
	}

	public void setProcessDeployer(ProcessDeployer processDeployer) {
		this.processDeployer = processDeployer;
	}

}
