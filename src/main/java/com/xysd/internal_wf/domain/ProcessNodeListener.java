package com.xysd.internal_wf.domain;

public interface ProcessNodeListener {
	
	public void transitDerived(Transition transit,ProcessContext context); //前面的transition的action作用后触发enter
	
	public void enter(ProcessContext context); // 流程通过这个节点 任务生成时
	
	public void leave(ProcessContext context); //
	
	//by controller: node.leave--> transit.execute-->callbacks.execute-->transit.targetNode.transitDerived()
	
	//default: node.transitDerived()-->node.enter()--->node.createTasks();
	
	//fork: node.transitDerived()00>node.enter()-->node().leave() 
	
	// join: node.transitDerived()-->make decision ( all tasks of previus fork nodes created is all finished)-->node.enter()
	
	

}
