package com.xysd.internal_wf.operation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class CycleForkJoinTest extends TestCase {

	public void testOneCycle() {
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
		// cycle
		pf.transit("backToFork1").from("work1").to("fork1",
				new Action[] { TestAction.testAction });

		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);

		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks.size());
		// work1 节点的待处理任务
		ProcessTaskInstance task_work = tasks.get(0);

		// work2节点上的待处理任务
		ProcessTaskInstance task_work2 = tasks.get(1);

		// 先结束work2的待处理任务
		engine.finishTask(task_work2, userVariables);

		// work1的待处理任务退回到fork1
		engine.finishTask(task_work, userVariables, "backToFork1");

		// work1上有一个任务,work2上有一个任务,forkId已经递增
		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks3.size());
		assertEquals("task2", tasks3.get(0).getTaskName());
		assertEquals("work1", tasks3.get(0).getProcessNode());
		assertEquals("2", tasks3.get(0).getForkId());
		assertEquals("task3", tasks3.get(1).getTaskName());
		assertEquals("work2", tasks3.get(1).getProcessNode());
		assertEquals("2", tasks3.get(1).getForkId());

		engine.finishTask(tasks3.get(0), userVariables, "trans3");
		engine.finishTask(tasks3.get(1), userVariables);

		tasks3 = engine.findAllOpenTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());
		// assertEquals(2, tester.count1);
	}

	public void testWaitOnOneCycle() {
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
		// cycle
		pf.transit("backToFork1").from("work1").to("fork1",
				new Action[] { TestAction.testAction });

		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);
		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks.size());
		// work1 节点的待处理任务
		ProcessTaskInstance task_work = tasks.get(0);

		// work2节点上的待处理任务
		ProcessTaskInstance task_work2 = tasks.get(1);

		// work1的待处理任务退回到fork1
		engine.finishTask(task_work, userVariables, "backToFork1");

		// work1上有一个任务,work2上有一个任务,forkId已经递增
		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(3, tasks3.size());
		assertSame(task_work2, tasks3.get(0));
		assertEquals("task2", tasks3.get(1).getTaskName());
		assertEquals("work1", tasks3.get(1).getProcessNode());
		assertEquals("2", tasks3.get(1).getForkId());
		assertEquals("task3", tasks3.get(2).getTaskName());
		assertEquals("work2", tasks3.get(2).getProcessNode());
		assertEquals("2", tasks3.get(2).getForkId());

		engine.finishTask(tasks3.get(1), userVariables, "trans3");
		engine.finishTask(tasks3.get(2), userVariables);
		// 仍然有任务2，不能产生任务4，意味着流程仍然停止在join1上
		tasks3 = engine.findAllOpenTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertSame(task_work2, tasks3.get(0));
		assertEquals("work2", tasks3.get(0).getProcessNode());

		engine.finishTask(task_work2, userVariables);

		// 现在可以进入 work3 节点了
		tasks3 = engine.findAllOpenTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertSame("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());

		persister.printAllLogs();
	}

	public void testTwoNestedCycle() {

		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),
				StrTypeProcessLocker.class.getName(), StrTypeProcessLog.class
						.getName());
		pf.begin("begin").addProcedureNodes("fork:fork1", "work0",
				"fork:fork2", "work1", "work2", "join:join2", "join:join1",
				"work3").end("end");
		pf.setTask("begin", "task1");
		pf.setTask("work0", "task0");
		pf.setTask("work1", "task2");
		pf.setTask("work2", "task3");
		pf.setTask("work3", "task4");
		pf.transit("a").from("begin").to("fork1",
				new Action[] { TestAction.testAction });
		pf.transit("b").from("fork1").to("work0",
				new Action[] { TestAction.testAction });
		pf.transit("c").from("fork1").to("fork2",
				new Action[] { TestAction.testAction });
		pf.transit("d").from("fork2").to("work1",
				new Action[] { TestAction.testAction });
		pf.transit("e").from("fork2").to("work2",
				new Action[] { TestAction.testAction });
		pf.transit("f").from("work1").to("join2",
				new Action[] { TestAction.testAction });
		pf.transit("g").from("work2").to("join2",
				new Action[] { TestAction.testAction });
		pf.transit("h").from("join2").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("i").from("work0").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("i").from("join1").to("work3",
				new Action[] { TestAction.testAction });
		pf.transit("back_w1fork1").from("work1").to("fork1",
				new Action[] { TestAction.testAction });
		pf.transit("back_w2fork1").from("work2").to("fork1",
				new Action[] { TestAction.testAction });
		pf.transit("back_fork2").from("work1").to("fork2",
				new Action[] { TestAction.testAction });

		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);
		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(3, tasks.size());

		// work0 节点的待处理任务
		ProcessTaskInstance task_work0 = tasks.get(0);

		// work1 节点的待处理任务
		ProcessTaskInstance task_work = tasks.get(1);

		// work1的待处理任务退回到fork1
		engine.finishTask(task_work, userVariables, "back_fork2");

		// work1上有一个任务,work2上有一个任务,forkId已经递增
		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(4, tasks3.size());
		assertSame(task_work0, tasks3.get(0));
		assertEquals("task3", tasks3.get(1).getTaskName());
		assertEquals("work2", tasks3.get(1).getProcessNode());
		assertEquals("2", tasks3.get(1).getForkId());
		assertEquals("task2", tasks3.get(2).getTaskName());
		assertEquals("work1", tasks3.get(2).getProcessNode());
		assertEquals("task3", tasks3.get(3).getTaskName());
		assertEquals("work2", tasks3.get(3).getProcessNode());

		engine.finishTask(tasks3.get(2), userVariables, "back_w1fork1");
		engine.finishTask(tasks3.get(3), userVariables);
		// 仍然有任务2，不能产生任务4，意味着流程仍然停止在join1上
		tasks3 = engine.findAllOpenTasks(task.getProcessInstanceId());
		assertEquals(5, tasks3.size());

		for (ProcessTaskInstance t : tasks3)
			engine.finishTask(t, userVariables);

		tasks3 = engine.findAllOpenTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());
		persister.printAllLogs();

	}

}
