package com.xysd.internal_wf.operation;

import com.xysd.internal_wf.domain.ProcessInvokerContext;

public interface IAction {
	
	public Object execute(ProcessInvokerContext context);

}
