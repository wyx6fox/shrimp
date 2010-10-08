package com.xysd.internal_wf.domain;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.ForkNode;
import com.xysd.internal_wf.domain.JoinNode;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;
import com.xysd.internal_wf.operation.TestAction;

public class ProcessConfigTest extends TestCase {

	public void testConfigSimpleProcessNodes() {
		ProcessConfig pf = new ProcessConfig("test_process",StrTypeWorkflowTask.class.getName(),StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		pf.begin("begin").addProcedureNodes("work").end("end");
		
		assertEquals("begin",pf.getBeginNode().getProcessNodeId());
		assertEquals("end",pf.getEndNode().getProcessNodeId());
		assertEquals("work",pf.getProcedureNodes().get("work").getProcessNodeId());
		
		assertEquals(1,pf.getProcedureNodes().size());
		

	}
	
	public void testConfigForkProcessNodes() {
		ProcessConfig pf = new ProcessConfig("test_process",StrTypeWorkflowTask.class.getName(),StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		pf.begin("begin").addProcedureNodes("work","fork:fork1").end("end");
		
		assertEquals("begin",pf.getBeginNode().getProcessNodeId());
		assertEquals("end",pf.getEndNode().getProcessNodeId());
		assertEquals("work",pf.getProcedureNodes().get("work").getProcessNodeId());
		assertEquals("fork1",pf.getProcedureNodes().get("fork1").getProcessNodeId());
		assertEquals(ForkNode.class,pf.getProcedureNodes().get("fork1").getClass());
		assertEquals(2,pf.getProcedureNodes().size());
		

	}
	
	public void testConfigJoinProcessNodes() {
		ProcessConfig pf = new ProcessConfig("test_process",StrTypeWorkflowTask.class.getName(),StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		pf.begin("begin").addProcedureNodes("work","fork:fork1","join:join1").end("end");
		
		assertEquals("begin",pf.getBeginNode().getProcessNodeId());
		assertEquals("end",pf.getEndNode().getProcessNodeId());
		assertEquals("work",pf.getProcedureNodes().get("work").getProcessNodeId());
		assertEquals("fork1",pf.getProcedureNodes().get("fork1").getProcessNodeId());
		assertEquals("join1",pf.getProcedureNodes().get("join1").getProcessNodeId());
		assertEquals(ForkNode.class,pf.getProcedureNodes().get("fork1").getClass());
		assertEquals(JoinNode.class,pf.getProcedureNodes().get("join1").getClass());
		assertEquals(3,pf.getProcedureNodes().size());
		

	}
	
	
	public void testConfigDecisionProcessNodes() {
		ProcessConfig pf = new ProcessConfig("test_process",StrTypeWorkflowTask.class.getName(),StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		pf.begin("begin").addProcedureNodes("work","fork:fork1","join:join1","decision:dec1").end("end");
		
		assertEquals("begin",pf.getBeginNode().getProcessNodeId());
		assertEquals("end",pf.getEndNode().getProcessNodeId());
		assertEquals("work",pf.getProcedureNodes().get("work").getProcessNodeId());
		assertEquals("fork1",pf.getProcedureNodes().get("fork1").getProcessNodeId());
		assertEquals("join1",pf.getProcedureNodes().get("join1").getProcessNodeId());
		
		assertEquals(ForkNode.class,pf.getProcedureNodes().get("fork1").getClass());
		assertEquals(JoinNode.class,pf.getProcedureNodes().get("join1").getClass());
		
		assertEquals(4,pf.getProcedureNodes().size());
		

	}
	
	public void testConfigTransitions(){
		ProcessConfig pf = new ProcessConfig("test_process",StrTypeWorkflowTask.class.getName(),StrTypeProcessVariable.class.getName(),StrTypeProcessLocker.class.getName(),StrTypeProcessLog.class.getName());
		pf.begin("begin").addProcedureNodes("work").end("end");
		pf.transit("transit1").from("begin").to("work",new Action[]{TestAction.testAction});
		assertEquals(1,pf.getProcessNodeById("begin").getTransitions().size());
		assertEquals("begin",pf.getProcessNodeById("begin").getTransitions().get("transit1").getSrc().getProcessNodeId());
		assertEquals("work",pf.getProcessNodeById("begin").getTransitions().get("transit1").getTarget().getProcessNodeId());
		assertEquals(1,pf.getProcessNodeById("begin").getTransitions().get("transit1").getActions().size());
	}

}
