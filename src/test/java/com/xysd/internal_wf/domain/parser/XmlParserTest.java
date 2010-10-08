package com.xysd.internal_wf.domain.parser;

import java.io.File;

import com.xysd.internal_wf.domain.BeginNode;
import com.xysd.internal_wf.domain.EndNode;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessNode;

import junit.framework.TestCase;

public class XmlParserTest extends TestCase {
	
	public void testSimple(){
		File f = new File(this.getClass().getResource("process1.xml").getFile());
		XmlParser xmlParser = new XmlParser(f);
		ProcessConfig pf = xmlParser.parse();
		
		assertEquals("test",pf.getProcessName());
		assertEquals("1",pf.getTaskClassName());
		assertEquals("2",pf.getProcessVariableClassName());
		assertEquals("3",pf.getProcessLockerClassName());
		assertEquals("4",pf.getProcessLogClassName());
		
		BeginNode begin = pf.getBeginNode();
		assertEquals("begin",begin.getProcessNodeId());
		
		EndNode end = pf.getEndNode();
		assertEquals("end",end.getProcessNodeId());
		
		assertEquals("task1",begin.getTask().getName());
		assertEquals("service.limit",begin.getTask().getLimitInvoker());
		assertEquals("service.expire",begin.getTask().getExpireInvoker());
		assertEquals("actor1",begin.getTask().getActorVar());
		assertEquals("actorType1",begin.getTask().getActorTypeVar());
		assertEquals("creator1",begin.getTask().getCreatorVar());
		assertEquals("creatorType1",begin.getTask().getCreatorTypeVar());
		assertEquals("projectId1",begin.getTask().getProjectIdVar());
		assertEquals("projectType1",begin.getTask().getProjectTypeVar());
		ProcessNode work1 = pf.getProcedureNodes().get("work");
		assertEquals("enterInvoker1",work1.getEnterInvoker());
		assertEquals("leaveInvoker1",work1.getLeaveInvoker());
	}
	
	public void testForkJoin(){
		File f = new File(this.getClass().getResource("forkJoin.xml").getFile());
		XmlParser xmlParser = new XmlParser(f);
		ProcessConfig pf = xmlParser.parse();
		
		assertEquals("test2",pf.getProcessName());
	}

}
