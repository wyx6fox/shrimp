package com.xysd.internal_wf.domain.exception;

import com.xysd.internal_wf.domain.ProcessTaskInstance;

public class TaskFinishedException extends ProcessTaskInstanceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3945395142863872851L;
	
	public TaskFinishedException(ProcessTaskInstance task){
		super(task);
	}

}
