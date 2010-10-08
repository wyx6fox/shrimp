package com.xysd.internal_wf.operation;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.ProcessInvokerContext;

public class TestAction {
	
	public static final Action testAction = new Action("testAction.execute");

	public void execute(ProcessInvokerContext context) {
		
		Tester.counts2.put(context.getCurrTransit().getTransitName(), 1);
	}

}
