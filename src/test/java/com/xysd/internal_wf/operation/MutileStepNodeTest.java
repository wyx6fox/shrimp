package com.xysd.internal_wf.operation;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInvokerContext;
import com.xysd.internal_wf.domain.ProcessNodeTask;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLocker;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class MutileStepNodeTest extends TestCase {

	private int stepnum = 0;

	private int taskNum = 5;

	public void testSimpleWorkflow() {

		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),
				StrTypeProcessLocker.class.getName(), StrTypeProcessLog.class
						.getName());
		pf.begin("begin").addProcedureNodes("multiple_step:step1").end("end");
		ProcessNodeTask taskConfig = pf.setTask("step1", "task1");
		pf.transit("default").from("begin").to("step1", null);
		pf.transit("default2").from("step1").to("end", null);
		taskConfig.setStepInvoker("testBean.canStep");
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("testBean", this);
		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		engine.setBeanFactory(bf);
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		String processInstanceId = engine.beginProcess(userVariables);
		for (int i = 0; i < taskNum + 100; i++) {
			List<ProcessTaskInstance> tasks = engine
					.findAllOpenTasks(processInstanceId);
			if (tasks.size() == 1) {
				assertEquals("step1", tasks.get(0).getProcessNode());
				stepnum++;
				engine.finishTask(tasks.get(0), userVariables);
			} else
				break;
		}

		assertEquals(taskNum, stepnum);

	}

	public boolean canStep(ProcessInvokerContext context) {

		return stepnum < taskNum;
	}

}
