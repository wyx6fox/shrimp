package com.xysd.internal_wf.operation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessLog;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class ProcessLogTest extends TestCase {

	public void testForkJoinWorkflow() {

		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),
				StrTypeProcessLocker.class.getName(), StrTypeProcessLog.class
						.getName());
		pf.begin("begin").addProcedureNodes("fork:fork1", "work1", "work2",
				"join:join1", "work3").end("end");
		pf.setTask("begin", "task1");
		pf.setTask("work1", "task2");
		pf.setTask("work2", "task3");
		pf.setTask("work3", "task4");
		pf.transit("default").from("begin").to("fork1",
				new Action[] { TestAction.testAction });
		pf.transit("trans1").from("fork1").to("work1",
				new Action[] { TestAction.testAction });
		pf.transit("trans2").from("fork1").to("work2",
				new Action[] { TestAction.testAction });
		pf.transit("trans3").from("work1").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("trans4").from("work2").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("trans5").from("join1").to("work3",
				new Action[] { TestAction.testAction });

		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);

		Map<String, Object> userVariables = TestHelper.buildUserVars();
		String processInstanceId = engine.beginProcess(userVariables);
		ProcessLog begin_log = engine.getProcessPersistence().findProcessLogs(
				processInstanceId, "begin").get(0);
		assertNotNull(begin_log);

		// 生成一个“进入beginNode”的日志
		// 第一个任务ID
		Serializable taskId = "1";
		ProcessTaskInstance task = engine.getTaskById(taskId);
		assertEquals(begin_log.getLogId_Internal(), task
				.getParentLogId_Internal());
		engine.finishTask(task, userVariables);
		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks.size());
		ProcessLog leave_begin_log = engine.getProcessPersistence()
				.findProcessLogs(processInstanceId, "begin",
						ProcessLog.EVENT_LEAVE).get(0);
		ProcessLog fork1_log = engine.getProcessPersistence().findProcessLogs(
				processInstanceId, "fork1").get(0);

		ProcessLog leave_fork1_log = engine.getProcessPersistence()
				.findProcessLogs(processInstanceId, "fork1",
						ProcessLog.EVENT_LEAVE).get(0);
		ProcessLog work1_log = engine.getProcessPersistence().findProcessLogs(
				processInstanceId, "work1").get(0);

		ProcessLog work2_log = engine.getProcessPersistence().findProcessLogs(
				processInstanceId, "work2").get(0);

		assertNotNull(fork1_log);
		assertNotNull(work1_log);
		assertNotNull(work2_log);

		assertEquals(leave_begin_log.getLogId_Internal(), fork1_log
				.getParentLogId_Internal());
		assertEquals(leave_fork1_log.getLogId_Internal(), work1_log
				.getParentLogId_Internal());
		assertEquals(leave_fork1_log.getLogId_Internal(), work2_log
				.getParentLogId_Internal());
		ProcessTaskInstance task_work = tasks.get(0);
		ProcessTaskInstance task_work2 = tasks.get(1);
		engine.finishTask(task_work, userVariables);
		engine.finishTask(task_work2, userVariables);

		ProcessLog leave_work1_log = engine.getProcessPersistence()
				.findProcessLogs(processInstanceId, "work1",
						ProcessLog.EVENT_LEAVE).get(0);
		ProcessLog leave_work2_log = engine.getProcessPersistence()
				.findProcessLogs(processInstanceId, "work2",
						ProcessLog.EVENT_LEAVE).get(0);
		List<ProcessLog> logs = engine.getProcessPersistence().findProcessLogs(
				processInstanceId, "join1");
		assertEquals(3, logs.size());
		assertEquals(leave_work1_log.getLogId_Internal(), logs.get(0)
				.getParentLogId_Internal());
		assertEquals(leave_work2_log.getLogId_Internal(), logs.get(1)
				.getParentLogId_Internal());

		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());

		// assertEquals(2, tester.count1);

	}

}
