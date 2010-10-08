package com.xysd.internal_wf.domain;

public class ProcessNodeTask {
	

	
	public static final String TASK_DEFAULT_VAR_ACTOR="actor";
	public static final String TASK_DEFAULT_VAR_ACTORTYPE="actorType";
	public static final String TASK_DEFAULT_VAR_CREATOR="creator";
	public static final String TASK_DEFAULT_VAR_CREATORTYPE="creatorType";
	public static final String TASK_DEFAULT_VAR_PROJECTID="projectId";
	public static final String TASK_DEFAULT_VAR_PROJECTTYPE="projectType";
	
	private String name;
	
	
	private String actorVar = TASK_DEFAULT_VAR_ACTOR;
	
	private String actorTypeVar=TASK_DEFAULT_VAR_ACTORTYPE;
	
	private String creatorVar=TASK_DEFAULT_VAR_CREATOR;
	
	private String creatorTypeVar=TASK_DEFAULT_VAR_CREATORTYPE;
	

	private String projectIdVar=TASK_DEFAULT_VAR_PROJECTID;
	
	private String projectTypeVar=TASK_DEFAULT_VAR_PROJECTTYPE;

	private String assignmeInvoker;
	
	private String generatorInvoker;
	
	private String stepInvoker;
	
	private String taskCreatedInvoker;
	
	private String taskFinishedInvoker;
	
	private String expireInvoker; //任务过期时的invoker
	
	private String limitInvoker; //任务限期的invoker
	
	public String getLimitInvoker() {
		return limitInvoker;
	}

	public void setLimitInvoker(String limitInvoker) {
		this.limitInvoker = limitInvoker;
	}

	public String getExpireInvoker() {
		return expireInvoker;
	}

	public void setExpireInvoker(String expireInvoker) {
		this.expireInvoker = expireInvoker;
	}

	public String getTaskCreatedInvoker() {
		return taskCreatedInvoker;
	}

	public void setTaskCreatedInvoker(String taskCreatedInvoker) {
		this.taskCreatedInvoker = taskCreatedInvoker;
	}

	public String getTaskFinishedInvoker() {
		return taskFinishedInvoker;
	}

	public void setTaskFinishedInvoker(String taskFinishedInvoker) {
		this.taskFinishedInvoker = taskFinishedInvoker;
	}

	public String getStepInvoker() {
		return stepInvoker;
	}

	public void setStepInvoker(String stepInvoker) {
		this.stepInvoker = stepInvoker;
	}

	public String getGeneratorInvoker() {
		return generatorInvoker;
	}

	public void setGeneratorInvoker(String generatorInvoker) {
		this.generatorInvoker = generatorInvoker;
	}

	public String getProjectIdVar() {
		return projectIdVar;
	}

	public void setProjectIdVar(String projectIdVar) {
		this.projectIdVar = projectIdVar;
	}

	public String getProjectTypeVar() {
		return projectTypeVar;
	}

	public void setProjectTypeVar(String projectTypeVar) {
		this.projectTypeVar = projectTypeVar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getActorVar() {
		return actorVar;
	}

	public void setActorVar(String actorVar) {
		this.actorVar = actorVar;
	}

	public String getActorTypeVar() {
		return actorTypeVar;
	}

	public void setActorTypeVar(String actorTypeVar) {
		this.actorTypeVar = actorTypeVar;
	}

	public String getCreatorVar() {
		return creatorVar;
	}

	public void setCreatorVar(String creatorVar) {
		this.creatorVar = creatorVar;
	}

	public String getCreatorTypeVar() {
		return creatorTypeVar;
	}

	public void setCreatorTypeVar(String creatorTypeVar) {
		this.creatorTypeVar = creatorTypeVar;
	}

	public String toString(){
		return this.name;
	}

	public String getAssignmeInvoker() {
		return assignmeInvoker;
	}

	public void setAssignmeInvoker(String assignmeInvoker) {
		this.assignmeInvoker = assignmeInvoker;
	}
	
	
	

}
