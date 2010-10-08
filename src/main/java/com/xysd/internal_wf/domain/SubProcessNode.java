package com.xysd.internal_wf.domain;
/**
 * 子流程
 * @author HP
 *
 */
public class SubProcessNode extends ProcessNode {

	public SubProcessNode(String processNodeId) {
		super(processNodeId);
		
	}
	
	public void enter(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug( " enter processNode:" + this+" with context:"+context );
		}
		if (task != null) {
			context.getProcessEngine().createTask(context, task);
		}
		else{
			//没有任务就自动离开
			this.leave(context);
		}

	}
	

}
