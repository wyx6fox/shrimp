package com.xysd.internal_wf.domain;

public class ForkNode extends ProcessNode {

	public ForkNode(String processNodeId) {
		super(processNodeId);

	}
	
	public void enter(ProcessContext context) {
		//首先生成一个ForkId
		String parentForkId = context.getForkId();
		String ownForkId = context.getProcessEngine().getProcessIdGenerator().generateForkId();
		//如果存在父亲Fork，则生成一个等待当前fork的join节点来结束的fork-task。为了让join
		//节点找到这个task，需要记录ownForkId. 同时为了避免父亲Fork的join往前执行，forkId记录为父亲的forkID
		if(parentForkId!=null){
			context.getProcessEngine().createForkTask(context, parentForkId, ownForkId);
		}
		context.setForkId(ownForkId);
		super.enter(context);
		

	}

	/**
	 * ForkNode不支持生成任务
	 */
	public void setTask(ProcessNodeTask task) {
		throw new UnsupportedOperationException(
				"ForkNode can not have taskConfig!");
	}

	public void leave(ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug(" now leave:" + this + " with context:" + context);
		}
		context.getProcessEngine().registerNodeLogIfNecessary(context,ProcessLog.EVENT_LEAVE);
		this.callLeaveInvokerIfNcessary(context);
		String transitName = context.getUserTransit();
		if (transitName != null)
			throw new RuntimeException(
					"forkNode should not specify the transit:" + transitName);
		for(Transition transit:this.transitions.values()){
			
			context.getProcessEngine().doTransit(context, transit);
		}
	}

}
