package com.xysd.internal_wf.operation;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xysd.internal_wf.domain.ProcessEngine;
import com.xysd.internal_wf.domain.ProcessInvokerContext;
import com.xysd.internal_wf.domain.ProcessTaskInstance;

import junit.framework.TestCase;

public class LimitTaskTest extends TestCase {
	
	public void test() throws Exception{
		ProcessEngine engine = this.getProcessEngine("limitTask.xml", "test_process");
		Map<String, Object> userVariables = TestHelper.buildUserVars();
		engine.beginProcess(userVariables);
		ProcessTaskInstance task1 = engine.getTaskById("1");
		engine.finishTask(task1, userVariables);
		
		Thread.sleep(3000);
		
		List<ProcessTaskInstance> tasks = engine.findAllOpenTasks(task1.getProcessInstanceId());
		assertEquals(0,tasks.size());
		
	}
	
	public Date limit(ProcessInvokerContext context){
		return new Date();
	}
	
	public void expire(ProcessInvokerContext context){
		ProcessTaskInstance task = (ProcessTaskInstance) context.getUserVariables().get(ProcessTaskInstance.EXPIRED_TASK_INSTANCE);
		assertEquals("task2",task.getTaskName());
		context.getProcessEngine().finishTask(task, new HashMap<String, Object>());
	}
	
	private File getFile(String fileName) {
		return new File(this.getClass().getResource(fileName).getFile());
	}
	
	private ProcessEngine getProcessEngine(String configFile, String processName) {
		ProcessDeployer processDeployer = new HashMapProcessDeployer();
		processDeployer
				.deployProcesses(new File[] { this.getFile(configFile) });
		HashMapBeanFactory bf = new HashMapBeanFactory();
		bf.beans.put("service", this);
		HashMapPersister persister = new HashMapPersister();
		ProcessEngineFactory engineFactory = new ProcessEngineFactory(
				new IncrementNumProcessIdGenerator(), persister, bf,
				processDeployer);
		engineFactory.setUseTimer(true);
		engineFactory.setSleepSeconds(1);
		engineFactory.init();
		return engineFactory.getProcessEngine(processName);
		
		
		
	}

}
