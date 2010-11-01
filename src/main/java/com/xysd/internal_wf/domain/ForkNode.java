package com.xysd.internal_wf.domain;
/**
 *
 * 表示一个分支节点，每一个ForkNode在运行时生成一个唯一的forkId。
 * TaskInstance有两个关键属性：forkId,ownForkId,其中forkId表示
 * 任务创建时所在fork-join流程中的当前的forkId，
 * ownForkId属性比较特殊，它仅仅用于为嵌套的Fork节点创建一个“待结束”fork任务实例，逻辑如下：
 * 在forkNode.enter方法中，在检测到当前fork嵌套于某个fork中时，生成一个特殊的的forktaskInstance,
 * 这个forktaskInstance的forkId为父级fork的ForkId，ownForkId为当前fork的forkId.
 * 
 * 在JoinNode.transitDerived方法作为enter方法的一个先决判断步骤，如果判断可以进入enter，则直接调用enter方法，否则返回。
 * 判断可以进入enter的条件：
 * 	 所有从属于当前forkId的taskInstances列表均已完成。为了避免并发冲突，这里查询逻辑使用了行级事务锁。
 * 
 * 进入enter后，如果存在由当前fork节点生成的forkTaskInstance，则直接结束它。
 * @author wyx6fox
 *
 */
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
