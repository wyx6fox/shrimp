package com.xysd.internal_wf.operation;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInvokerContext;
import com.xysd.internal_wf.domain.ProcessTaskInstance;

public class JoinWaitInvokerTest extends TestCase {

	private File getFile(String fileName) {
		return new File(this.getClass().getResource(fileName).getFile());
	}

	/**
	 * 测试简单的流程中转
	 */
	public void testSimpleWorkflow() {
		ProcessDeployer processDeployer = new HashMapProcessDeployer();
		processDeployer.deployProcesses(new File[] { this
				.getFile("waitInvoker.xml") });

		final Tester tester = new Tester();

		final IAction runner = new IAction() {

			public Object execute(ProcessInvokerContext context) {
				tester.count1++;
				return null;

			}
		};
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("tester", runner);
		bf.beans.put("waiter", this);
		HashMapPersister persister = new HashMapPersister();
		ProcessEngineFactory engineFactory = new ProcessEngineFactory(
				new IncrementNumProcessIdGenerator(), persister, bf,
				processDeployer);

		ProcessEngine engine = engineFactory.getProcessEngine("wait_invoker");

		Serializable taskId = "1";
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);
		engine.finishTask(task, userVariables);
		
		ProcessTaskInstance task2 = engine.getTaskById("2");
		engine.finishTask(task2, userVariables);
		ProcessTaskInstance task3 = engine.getTaskById("3");
		engine.finishTask(task3, userVariables);
		
		assertEquals(1,engine.findAllOpenTasks("1").size());
		assertEquals("task4",engine.findAllOpenTasks("1").get(0).getTaskName());
		assertEquals("work3",engine.findAllOpenTasks("1").get(0).getProcessNode());
	}

	public boolean wait(ProcessInvokerContext context) {
		return false;
	}

}
