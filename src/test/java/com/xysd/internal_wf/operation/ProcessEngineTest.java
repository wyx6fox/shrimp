package com.xysd.internal_wf.operation;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInvokerContext;
import com.xysd.internal_wf.domain.ProcessNodeTask;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class ProcessEngineTest extends TestCase {

	private File getFile(String fileName) {
		return new File(this.getClass().getResource(fileName).getFile());
	}

	/**
	 * 测试简单的流程中转
	 */
	public void testSimpleWorkflow() {
		ProcessDeployer processDeployer = new HashMapProcessDeployer();
		processDeployer.deployProcesses(new File[] { this
				.getFile("simple1.xml") });

		final Tester tester = new Tester();

		final IAction runner = new IAction() {

			public Object execute(ProcessInvokerContext context) {
				tester.count1++;
				return null;

			}
		};
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("tester", runner);
		HashMapPersister persister = new HashMapPersister();
		ProcessEngineFactory engineFactory = new ProcessEngineFactory(
				new IncrementNumProcessIdGenerator(), persister, bf,
				processDeployer);

		ProcessEngine engine = engineFactory.getProcessEngine("test_process");

		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);

		engine = engineFactory.getProcessEngine(StrTypeWorkflowTask.class,
				taskId);
		ProcessTaskInstance task = engine.getTaskById(taskId);
		// 任务已经获得流程实例ID
		assertEquals("1", task.getProcessInstanceId());
		// 任务已经保留所属流程节点ID
		assertEquals("begin", task.getProcessNode());
		// 任务已经保留Actor
		assertEquals("user2", task.getActor_Internal());
		// 任务已经保留ActorType
		assertEquals("user", task.getActorType());

		// 任务已经保留creator
		assertEquals("user1", task.getCreator_Internal());

		// 任务已经保留creatorType
		assertEquals("user", task.getCreatorType());

		// 任务已经保存业务数据ID
		assertEquals("project1", task.getProjectId_Internal());
		// 任务已经保存业务数据类型
		assertEquals("projectType1", task.getProjectType());

		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTOR, "user3");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATOR, "user2");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTID,
				"project1");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTTYPE,
				"projectType1");
		engine.finishTask(task, userVariables);
		assertEquals(1, tester.count1);

		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(1, tasks.size());
		ProcessTaskInstance task_work = tasks.get(0);
		assertEquals("task2", task_work.getTaskName());
		assertEquals("work", task_work.getProcessNode());

		engine.finishTask(task_work, userVariables);
		assertEquals(2, tester.count1);

	}

	public void testForkJoinWorkflow() {

		ProcessEngine engine = getProcessEngine("simple2.xml", "test_process");
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);
		assertEquals(Integer.valueOf(1), Tester.counts2.get("default"));

		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks.size());
		ProcessTaskInstance task_work = tasks.get(0);
		assertEquals("1", task_work.getForkId());
		assertEquals("task2", task_work.getTaskName());
		assertEquals("work1", task_work.getProcessNode());

		ProcessTaskInstance task_work2 = tasks.get(1);
		assertEquals("1", task_work2.getForkId());
		assertEquals("task3", task_work2.getTaskName());
		assertEquals("work2", task_work2.getProcessNode());

		engine.finishTask(task_work, userVariables);

		List<ProcessTaskInstance> tasks2 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(1, tasks2.size());
		assertEquals("task3", tasks2.get(0).getTaskName());
		assertEquals("work2", tasks2.get(0).getProcessNode());

		engine.finishTask(task_work2, userVariables);

		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());

		// assertEquals(2, tester.count1);

	}

	private ProcessEngine getProcessEngine(String configFile, String processName) {
		ProcessDeployer processDeployer = new HashMapProcessDeployer();
		processDeployer
				.deployProcesses(new File[] { this.getFile(configFile) });
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("testAction", new TestAction());
		HashMapPersister persister = new HashMapPersister();
		ProcessEngineFactory engineFactory = new ProcessEngineFactory(
				new IncrementNumProcessIdGenerator(), persister, bf,
				processDeployer);
		return engineFactory.getProcessEngine(processName);
	}

	public void testNestedJoinWorkflow() {

		ProcessEngine engine = getProcessEngine("simple3.xml", "test_process");

		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("testAction", new TestAction());
		engine.setBeanFactory(bf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);
		assertEquals(Integer.valueOf(1), Tester.counts2.get("default"));

		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(3, tasks.size());
		ProcessTaskInstance task_work = tasks.get(0);
		assertEquals("1", task_work.getForkId());
		assertEquals("task2", task_work.getTaskName());
		assertEquals("work1", task_work.getProcessNode());

		ProcessTaskInstance task_work2 = tasks.get(1);
		assertEquals("2", task_work2.getForkId());
		assertEquals("task11", task_work2.getTaskName());
		assertEquals("work11", task_work2.getProcessNode());

		ProcessTaskInstance task_work3 = tasks.get(2);
		assertEquals("2", task_work3.getForkId());
		assertEquals("task12", task_work3.getTaskName());
		assertEquals("work12", task_work3.getProcessNode());

		engine.finishTask(task_work, userVariables);

		List<ProcessTaskInstance> tasks2 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks2.size());
		assertEquals("task11", tasks2.get(0).getTaskName());
		assertEquals("work11", tasks2.get(0).getProcessNode());
		assertEquals("task12", tasks2.get(1).getTaskName());
		assertEquals("work12", tasks2.get(1).getProcessNode());

		engine.finishTask(tasks2.get(0), userVariables);
		engine.finishTask(tasks2.get(1), userVariables);

		List<ProcessTaskInstance> tasks3 = engine.getProcessPersistence()
				.findAllOpenTasksWithSystemTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task3", tasks3.get(0).getTaskName());
		assertEquals("work2", tasks3.get(0).getProcessNode());
		System.out.println(engine.getProcessPersistence());
		// assertEquals(2, tester.count1);

	}

	public void testForkMultipleNodeJoinWorkflow() {

		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),
				StrTypeProcessLocker.class.getName(), StrTypeProcessLog.class
						.getName());
		pf.begin("begin").addProcedureNodes("fork:fork1", "work1", "work2",
				"multiple:mul1", "join:join1", "work3").end("end");
		pf.setTask("begin", "task1");
		pf.setTask("work1", "task2");
		pf.setTask("work2", "task3");
		pf.setTask("work3", "task4");
		ProcessNodeTask taskConfig = pf.setTask("mul1", "mul1_task");
		taskConfig.setActorTypeVar("mul1_actorTypes");
		taskConfig.setActorVar("mul1_actors");

		pf.transit("default").from("begin").to("fork1",
				new Action[] { TestAction.testAction });
		pf.transit("trans1").from("fork1").to("work1",
				new Action[] { TestAction.testAction });
		pf.transit("trans2").from("fork1").to("work2",
				new Action[] { TestAction.testAction });
		pf.transit("trans3").from("fork1").to("mul1",
				new Action[] { TestAction.testAction });
		pf.transit("trans4").from("work1").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("trans5").from("work2").to("join1",
				new Action[] { TestAction.testAction });
		pf.transit("trans6").from("join1").to("work3",
				new Action[] { TestAction.testAction });
		pf.transit("trans6").from("mul1").to("join1",
				new Action[] { TestAction.testAction });

		ProcessEngine engine = new ProcessEngine(pf);
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("testAction", new TestAction());
		engine.setBeanFactory(bf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();

		userVariables.put("mul1_actors", new String[] { "mul1_actor1",
				"mul2_actor2" });

		userVariables.put("mul1_actorTypes", new String[] { "type1", "type2" });
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		engine.finishTask(task, userVariables);
		assertEquals(Integer.valueOf(1), Tester.counts2.get("default"));

		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(4, tasks.size());
		ProcessTaskInstance task_work = tasks.get(0);
		assertEquals("1", task_work.getForkId());
		assertEquals("task2", task_work.getTaskName());
		assertEquals("work1", task_work.getProcessNode());

		ProcessTaskInstance task_work2 = tasks.get(1);
		assertEquals("1", task_work2.getForkId());
		assertEquals("task3", task_work2.getTaskName());
		assertEquals("work2", task_work2.getProcessNode());

		ProcessTaskInstance task_work3 = tasks.get(2);
		assertEquals("1", task_work3.getForkId());
		assertEquals("mul1_task", task_work3.getTaskName());
		assertEquals("mul1", task_work3.getProcessNode());

		ProcessTaskInstance task_work4 = tasks.get(3);
		assertEquals("1", task_work4.getForkId());
		assertEquals("mul1_task", task_work4.getTaskName());
		assertEquals("mul1", task_work4.getProcessNode());

		engine.finishTask(task_work, userVariables);

		engine.finishTask(task_work2, userVariables);

		List<ProcessTaskInstance> tasks3 = engine.findAllOpenTasks(task
				.getProcessInstanceId());
		assertEquals(2, tasks3.size());
		assertEquals("mul1_task", tasks3.get(0).getTaskName());
		assertEquals("mul1", tasks3.get(0).getProcessNode());
		assertEquals("mul1_task", tasks3.get(1).getTaskName());
		assertEquals("mul1", tasks3.get(1).getProcessNode());

		engine.finishTask(tasks3.get(0), userVariables);
		engine.finishTask(tasks3.get(1), userVariables);

		tasks3 = engine.getProcessPersistence()
				.findAllOpenTasksWithSystemTasks(task.getProcessInstanceId());
		assertEquals(1, tasks3.size());
		assertEquals("task4", tasks3.get(0).getTaskName());
		assertEquals("work3", tasks3.get(0).getProcessNode());

		// assertEquals(2, tester.count1);

	}

}
