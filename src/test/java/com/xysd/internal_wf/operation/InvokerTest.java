package com.xysd.internal_wf.operation;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInvokerContext;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class InvokerTest extends TestCase {

	public void testInvoker() {
		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		ProcessEngine engine = new ProcessEngine(pf);
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("testbean", this);
		engine.setBeanFactory(bf);
		assertEquals("mygod",engine.invoke("testbean.myInvoke", null));
	}
	
	
	public String myInvoke(ProcessInvokerContext context){
		return "mygod";
	}

}
