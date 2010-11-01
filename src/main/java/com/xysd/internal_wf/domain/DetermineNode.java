package com.xysd.internal_wf.domain;
/**
 * 
 * 表示一个条件过滤节点，根据条件从多个transit中选择一条。
 * 
 * 每个transit声明有一个 条件选择器： conditionInvoker ，声明格式如下：
 * 
 * <transit id=... conditionInvoker={beanName}.{methodName}>...</transit>
 * 
 * 
 * 
 * @author wyx6fox
 *
 */
public class DetermineNode extends ProcessNode {

	public DetermineNode(String processNodeId) {
		
		super(processNodeId);

	}

	public void enter(ProcessContext context) {
		Transition determinedTrans = null;
		for (Transition transit : this.getTransitions().values()) {
			if (transit.getConditionInvoker() != null) {
				boolean canTransit = ("true".equalsIgnoreCase(context
						.getProcessEngine().invoke(
								transit.getConditionInvoker(), context)
						+ ""));
				if(canTransit){
					determinedTrans = transit;
					break;
				}
			}
		}
		if(determinedTrans==null)
			throw new RuntimeException("must have one determined transit in this node:"+this+" ,context"+context);
		else{
			context.setUserTransit(determinedTrans.getTransitName());
			super.enter(context);
		}

	}

	/**
	 * DetermineNode不支持生成任务
	 */
	public void setTask(ProcessNodeTask task) {
		throw new UnsupportedOperationException(
				"ForkNode can not have taskConfig!");
	}

}
