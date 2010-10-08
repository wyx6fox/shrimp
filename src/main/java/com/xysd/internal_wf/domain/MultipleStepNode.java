package com.xysd.internal_wf.domain;

/**
 * 自动逐级任务生成节点
 * 
 * @author HP
 * 
 */
public class MultipleStepNode extends ProcessNode {

	public MultipleStepNode(String processNodeId) {
		super(processNodeId);

	}

	public void enter(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug(" enter processNode:" + this + " with context:"
					+ context);
		}
		this.callEnterInvokerIfNcessary(context);
		if (task != null) {
			if (this.canStepOne(context))
				context.getProcessEngine().createTask(context, task);
			else
				this.leave(context);
		}
		else
			this.leave(context);

	}

	// 是否还可以再生成任务？
	private boolean canStepOne(ProcessContext context) {
		if (this.task.getStepInvoker() != null)
			return "true".equals(context.getProcessEngine().invoke(
					task.getStepInvoker(), context)
					+ "");
		else
			return false;
	}

	public void leave(ProcessContext context) {
		if (this.canStepOne(context))
			this.enter(context);
		else
			super.leave(context);

	}

}
