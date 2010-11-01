package com.xysd.internal_wf.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * 
 * @author wyx6fox
 *
 */
@MappedSuperclass
public abstract class ProcessTaskInstance {
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
	public static final String ACTOR_TYPE_USER="user";
	public static final String ACTOR_TYPE_ROLE="role";
	public static final String ACTOR_TYPE_DEPT="dept";
	public static final String ACTOR_TYPE_ORG="org";
	
	public static final byte STATUS_OPEN=1;
	public static final byte STATUS_CLOSE=0;
	

	public static final String TASK_TYPE_USER="user";
	public static final String TASK_TYPE_FORK="fork";
	
	public static final String EXPIRED_TASK_INSTANCE="EXPIRED_TASK_INSTANCE";
	// id 必须自己定义
	
	
	protected String creatorType=ACTOR_TYPE_USER;
	
	
	protected String actorType=ACTOR_TYPE_USER;
	
	protected Date createTime; //任务触发时间
	
	protected Date actTime; // 动作事件
	
	protected String processNode; //流程节点
	
	protected String taskName; //任务名称
	
	protected byte status=STATUS_OPEN; //任务状态

	protected String projectType; //所关联业务对象类型
	
	protected String processInstanceId; 
	
	protected String forkId; //fork之后创建的task需要记录
	
	protected String ownForkId; //嵌套的fork创建forkTaskInstance时将自己的forkId作为ownForkId保存
	
	protected String taskType;
	
	protected String waitingProcessInstanceId; 
	//子流程时用到，子流程设计思想：子流程Node是一个forkNode，ProcessEndNode所关联的任务finish
	//时，会搜索所有waitingProcessInstanceId==currProcessInstanceId&&status=='open'的tasks，然后依次finsih这些task。
	
	//task.finish时，会检查所关联的processNode,
	//如果所关联的processNode所拥有的所有任务都已执行完毕（查询返回0： from task t where t.processNode=:currNode and t.processInstanceId=:currprocessInstanceId and t.status='open')
	//并且所关联的processNode是自动迁移的Node，则自动迁移。
	
	protected String taskClassName;
	
	protected Date limitDate; 
	
	protected Date extensionDate;
	
	protected Date realLimitDate;
	
	protected String userStatus; //用户字段
	
	
	
	

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public Date getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}
	@Transient
	public void limit(Date limitDate){
		this.limitDate = limitDate;
		this.realLimitDate = limitDate;
	}
	
	@Transient
	public void extension(Date extensionDate){
		this.extensionDate = extensionDate;
		this.realLimitDate = extensionDate;
	}

	public Date getExtensionDate() {
		return extensionDate;
	}

	public void setExtensionDate(Date extensionDate) {
		this.extensionDate = extensionDate;
	}

	public Date getRealLimitDate() {
		return realLimitDate;
	}

	public void setRealLimitDate(Date realLimitDate) {
		this.realLimitDate = realLimitDate;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	//所属业务实体对象
	@Transient
	public abstract Serializable getProjectId_Internal();
	
	public abstract void setProjectId_Internal(Serializable projectId);
	
	 //任务触发者
	@Transient
	public abstract Serializable getCreator_Internal();
	
	public abstract void setCreator_Internal(Serializable creator);
	
	//任务执行者
	@Transient
	public abstract Serializable getActor_Internal() ;
	@Transient
	public abstract void setActor_Internal(Serializable actor) ;
	
	

	
	//触发者类型
	public String getCreatorType() {
		return this.creatorType;
	}

	public void setCreatorType(String creatorType) {
		this.creatorType = creatorType;
	}
	//执行者类型
	public String getActorType() {
		return actorType;
	}

	public void setActorType(String actorType) {
		this.actorType = actorType;
	}

	

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getWaitingProcessInstanceId() {
		return waitingProcessInstanceId;
	}

	public void setWaitingProcessInstanceId(String waitingProcessInstanceId) {
		this.waitingProcessInstanceId = waitingProcessInstanceId;
	}

	public Date getActTime() {
		return actTime;
	}

	public void setActTime(Date actTime) {
		this.actTime = actTime;
	}

	public String getProcessNode() {
		return processNode;
	}

	public void setProcessNode(String processNode) {
		this.processNode = processNode;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}
	
	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	@Transient
	public abstract Serializable getId_Internal();

	@Transient
	public abstract void setId_Internal(Serializable id);

	protected void finish(ProcessContext context) {
		this.status = STATUS_CLOSE;
		this.actTime = new Date();
		logger.debug("finish task:"+this+" with context:"+context);
	}
	

	public String toString(){
		return "{taskId:"+this.getId_Internal()+" taskType:"+this.taskType+" taskName:"+this.taskName+" actor:"+this.getActor_Internal()+" actorType:"+this.actorType+" processInstanceId:"+this.processInstanceId+" processNodeId:"+this.processNode+" creator:"+this.getCreator_Internal()+" creatorType:"+this.creatorType+" createTime:"+this.createTime+" actTime:"+this.actTime+" status:"+this.status+" parentLogId:"+this.getParentLogId_Internal()+" }";
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getForkId() {
		return forkId;
	}

	public void setForkId(String forkId) {
		this.forkId = forkId;
	}

	public String getOwnForkId() {
		return ownForkId;
	}

	public void setOwnForkId(String ownForkId) {
		this.ownForkId = ownForkId;
	}
	@Transient
	public abstract Serializable getParentLogId_Internal() ;
	@Transient
	public abstract void setParentLogId_Internal(Serializable parentLogId);
	
	
}
