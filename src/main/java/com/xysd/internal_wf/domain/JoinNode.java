package com.xysd.internal_wf.domain;

import java.util.List;

/**
 * Join等待节点。 用户可以自定义等待行为：waitInvoker
 * 
 * @author HP
 * 
 */
public class JoinNode extends ProcessNode {

	private String waitInvoker; // 用户自己指定是否允许通过的规则 例子：

	// canEnter="bean.canEnter"

	public JoinNode(String processNodeId) {
		super(processNodeId);

	}

	// 先判断从属于当前ForkId的所有Task是否均已关闭
	public void transitDerived(Transition transit, ProcessContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("derived to processNode:" + this + " with transit:"
					+ transit + "  with context:" + context);
		}
		context.setCurrProcessNode(this);
		// 登记日志
		context.getProcessEngine().registerNodeLogIfNecessary(context,
				ProcessLog.EVENT_DERIVED);
		// 考虑到并发的因素，因为需要根据查询结果来做决定是否可以enter，所以需要加上事务锁，以保证在这里的串行动作
		String lockerKey = context.getCurrProcessInstanceId() + "_"
				+ this.getProcessNodeId();
		if (context.getCurrLockers().get(lockerKey) == null) {
			// 有可能自身循环进来，避免因此引起的死锁，需要考虑一旦获得锁，以后的锁都无需获得了
			// 当提交时，锁自动被去掉
			ProcessLocker locker = context.getProcessEngine()
					.getProcessPersistence().lock(
							context.getProcessEngine().getProcessPersistence()
									.findProcessLocker(
											context.getCurrProcessInstanceId(),
											this.getProcessNodeId()));
			context.getCurrLockers().put(lockerKey, locker);
		}
		// 这里需要保证forkId被传递到每个fork以后生成的TaskInstace
		if (context.getForkId() == null)
			throw new RuntimeException("forkId should not be null");
		context.clearTransit();
		boolean canEnter = false;
		if (waitInvoker == null) {
			canEnter = context.getProcessEngine().getProcessPersistence()
					.findOpenTaskCountByProcessInstanceIdAndForkId(
							context.getCurrProcessInstanceId(),
							context.getForkId()) == 0;
		} else {
			// 返回false表示不用等待了。
			Object result = context.getProcessEngine().invoke(this.waitInvoker,
					context);
			canEnter = "false".equals(result + "");
		}
		// 默认行为就是执行enter
		if (canEnter) {
			List<ProcessLog> leaveLogs = context.getProcessEngine()
					.getProcessPersistence().findProcessLogs(
							context.getCurrProcessInstanceId(),
							this.getProcessNodeId(), ProcessLog.EVENT_LEAVE,
							context.getForkId());
			if (leaveLogs.isEmpty())
				this.enter(context);
			else {
				// 表明当前fork分支（forkId代表的分支实例）已经离开joinNode
				logger.warn(" processInstance:"
						+ context.getCurrProcessInstanceId() + " forkId:"
						+ context.getForkId() + " has leaved join node:"
						+ this.getProcessNodeId());
			}
		}
	}

	public void enter(ProcessContext context) {
		// 结束 ownTransit==forkId的所有forkTask
		finishAllForkTasks(context);
		super.enter(context);

	}

	public void leave(ProcessContext context) {
		ProcessTaskInstance forkTask = context.getProcessEngine()
				.getProcessPersistence()
				.findForkTaskByProcessInstanceIdAndOwnForkId(
						context.getCurrProcessInstanceId(),
						context.getForkId(), ProcessTaskInstance.STATUS_CLOSE);
		if (forkTask != null) {
			this.callLeaveInvokerIfNcessary(context);
			// 循环fork-join，需要重复进入joinNode,这里也表示离开当前JoinNode的意思
			// 表示一个离开事件，注意此时登记的log.forkId表示当前的forkId,而不是父级forkId
			context.getProcessEngine().registerNodeLogIfNecessary(context,
					ProcessLog.EVENT_LEAVE);
			// 离开当前fork实例进入父级fork实例，此时记得把父级forkId记录到context中
			context.setForkId(forkTask.getForkId());
			Transition transit = new Transition();
			transit.setSrc(this);
			transit.setTarget(this);
			transit.setTransitName("cycle_fork");
			this.transitDerived(transit, context);
		} else {
			super.leave(context);
		}

	}

	private void finishAllForkTasks(ProcessContext context) {
		ProcessTaskInstance forkTask = context.getProcessEngine()
				.getProcessPersistence()
				.findForkTaskByProcessInstanceIdAndOwnForkId(
						context.getCurrProcessInstanceId(),
						context.getForkId(), ProcessTaskInstance.STATUS_OPEN);
		if (forkTask != null) {
			forkTask.finish(context);
		}
	}

	public String getWaitInvoker() {
		return waitInvoker;
	}

	public void setWaitInvoker(String waitInvoker) {
		this.waitInvoker = waitInvoker;
	}

}
