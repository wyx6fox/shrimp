package com.xysd.internal_wf.operation;

import java.util.HashMap;
import java.util.Map;

import com.xysd.internal_wf.domain.ProcessNodeTask;

public class TestHelper {
	
	public static Map<String,Object> buildUserVars(){
		Map<String, Object> userVariables = new HashMap<String, Object>();
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTOR, "user2");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_ACTORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATOR, "user1");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_CREATORTYPE, "user");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTID,
				"project1");
		userVariables.put(ProcessNodeTask.TASK_DEFAULT_VAR_PROJECTTYPE,
				"projectType1");
		return userVariables;
	}

}
