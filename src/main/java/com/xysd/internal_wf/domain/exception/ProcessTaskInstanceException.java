package com.xysd.internal_wf.domain.exception;

import com.xysd.internal_wf.domain.ProcessTaskInstance;

public class ProcessTaskInstanceException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6983790243256294973L;
	protected ProcessTaskInstance task;

	public ProcessTaskInstanceException(ProcessTaskInstance task) {
		super("Error when processing task:"+task);
		this.task = task;
	}

	public ProcessTaskInstance getTask() {
		return task;
	}

}
