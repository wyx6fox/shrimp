package com.xysd.internal_wf.operation;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xysd.internal_wf.domain.ProcessContext;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessNode;
import com.xysd.internal_wf.domain.ProcessTaskInstance;

public class ProcessTimer implements Runnable {
	private Log logger = LogFactory.getLog(this.getClass());

	private int sleepSeconds;

	private ProcessEngineFactory engineFactory;

	public ProcessTimer(int sleepSeconds, ProcessEngineFactory engineFactory) {
		super();
		this.sleepSeconds = sleepSeconds;
		if (sleepSeconds < 1)
			throw new RuntimeException("timer.sleepMinutes must great than 0!");
		this.engineFactory = engineFactory;
	}

	@SuppressWarnings("static-access")
	public void run() {
		while (true) {
			try {
				Thread.currentThread().sleep(sleepSeconds * 1000);
			} catch (InterruptedException e) {
				logger.warn(e);
			}
			if (logger.isDebugEnabled())
				logger
						.debug("begin run processTimer to invoke expire all expired taskInstances!");
			List<ProcessTaskInstance> tasks = this.engineFactory
					.getProcessPersistence().findExpiredLimitedOpenTasks();
			if(tasks==null)
				continue;
			for (ProcessTaskInstance task : tasks) {
				try {
					ProcessEngine engine = this.engineFactory
							.getProcessEngine(task);
					ProcessNode pn = engine.getProcessConfig()
							.getProcessNodeById(task.getProcessNode());
					if (pn.getTask() != null
							&& pn.getTask().getExpireInvoker() != null) {
						ProcessContext context = ProcessContext
								.build(engine, task.getProcessInstanceId(), pn,
										new HashMap<String, Object>(), task
												.getForkId());
						// 为了跟踪流程流转日志，必须记录任务的父级日志ID
						context.setParentLogId(task.getParentLogId_Internal());
						context.getUserVariables().put(ProcessTaskInstance.EXPIRED_TASK_INSTANCE, task);
						engine.invoke(pn.getTask().getExpireInvoker(), context);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}

	}

}
