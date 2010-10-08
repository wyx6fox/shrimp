package com.xysd.internal_wf.operation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessNodeTask;
import com.xysd.internal_wf.domain.ProcessTaskInstance;
import com.xysd.internal_wf.domain.ProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeProcessLog;
import com.xysd.internal_wf.domain.impl.StrTypeProcessVariable;
import com.xysd.internal_wf.domain.impl.StrTypeWorkflowTask;

public class PersistProcessVariablesTest extends TestCase {

	public void testSaveProcessVars() {

		ProcessConfig pf = new ProcessConfig("test_process",
				StrTypeWorkflowTask.class.getName(),
				StrTypeProcessVariable.class.getName(),
				StrTypeProcessVariable.class.getName(), StrTypeProcessLog.class
						.getName());
		pf.begin("begin").addProcedureNodes("work").end("end");
		pf.setTask("begin", "task1");
		pf.setTask("work", "task2");
		pf.transit("default").from("begin").to("work", new Action[] {});
		pf.transit("default2").from("work").to("end", new Action[] {});
		ProcessEngine engine = new ProcessEngine(pf);
		engine.setProcessIdGenerator(new IncrementNumProcessIdGenerator());
		HashMapPersister persister = new HashMapPersister();
		engine.setProcessPersistence(persister);
		Serializable taskId = "1";
		Map<String, Object> userVariables = new HashMap<String, Object>();
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTOR, "user2");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATOR, "user1");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTID,
				"project1");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTTYPE,
				"projectType1");
		engine.beginProcess(userVariables);
		ProcessTaskInstance task = engine.getTaskById(taskId);

		ProcessVariable v = engine.findProcessVariable(task
				.getProcessInstanceId(), "var1", "22", true);

		engine.saveProcessVariable(v);

		assertSame(v, engine.findProcessVariable(v.getProcessInstanceId(), v
				.getVarName(), v.getUniqueId(), true));

	}

}
