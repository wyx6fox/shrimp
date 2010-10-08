package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 多任务并行创建节点
 * 
 * @author HP
 * 
 */
public class MultipleNode extends ProcessNode {

	

	public MultipleNode(String processNodeId) {
		super(processNodeId);

	}

	@SuppressWarnings("unchecked")
	public void enter(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug(" enter processNode:" + this + " with context:"
					+ context);
		}
		if (context.getForkId() == null)
			throw new RuntimeException(
					"forkId should not be null! be sure this node is after a forkNode!");
		super.callEnterInvokerIfNcessary(context);
		if (task != null) {

			if (task.getGeneratorInvoker() == null) {
				createTasksByVars(context);
			} else {
				createTasksByInvoker(context);
			}

		}
		else
			this.leave(context);

	}

	@SuppressWarnings("unchecked")
	private void createTasksByInvoker(ProcessContext context) {
		List<ProcessTaskInstance> tasks = (List<ProcessTaskInstance>) context
				.getProcessEngine().invoke(task.getGeneratorInvoker(), context);
		for (ProcessTaskInstance t : tasks) {
			t.setForkId(context.getForkId());
			t.setProcessInstanceId(context.getCurrProcessInstanceId());
			t.setTaskClassName(context.getProcessEngine().getTaskClazz().getName());
			t.setCreateTime(new Date());
			t.setProcessNode(this.getProcessNodeId());
			t.setTaskType(ProcessTaskInstance.TASK_TYPE_USER);
			t.setParentLogId_Internal(context.getParentLogId());
			context.getProcessEngine().getProcessPersistence().saveTask(t);
		}
	}

	private void createTasksByVars(ProcessContext context) {
		ProcessNodeTask taskConfig = this.getTask();
		Serializable[] actors = (Serializable[]) context.getUserVariables()
				.get(taskConfig.getActorVar());
		String[] actorTypes = (String[]) context.getUserVariables().get(
				taskConfig.getActorTypeVar());
		if (actors == null)
			throw new RuntimeException(
					"actors should not be null for this task:" + taskConfig
							+ " context:" + context);
		if (actorTypes == null)
			throw new RuntimeException(
					"actorTypes should not be null for this task:" + taskConfig
							+ " context:" + context);
		if (actors.length != actorTypes.length)
			throw new RuntimeException(
					"actors.length should  be equal with actorTypes.length for this task:"
							+ taskConfig + " context:" + context);
		for (int i = 0; i < actors.length; i++) {
			context.getUserVariables().put(taskConfig.getActorVar(), actors[i]);
			context.getUserVariables().put(taskConfig.getActorTypeVar(),
					actorTypes[i]);
			context.getProcessEngine().createTask(context, taskConfig);
		}

		context.getUserVariables().put(taskConfig.getActorVar(), actors);
		context.getUserVariables()
				.put(taskConfig.getActorTypeVar(), actorTypes);

	}

}
