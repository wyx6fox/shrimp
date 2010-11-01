package com.xysd.internal_wf.domain;

/**
 * 
 * 流程流转过程中的动作类。是流程作用于业务逻辑的桥梁。 业务逻辑的代码可以通过action注册到流程中的每一个环节中。
 * Action只有一个invoker的字符串描述，invoker描述信息格式如下： {beanName}.{methodName} ,
 * 引擎的注册的IOC容器将负责解析BeanName并调用相应的方法。 {beanName}.{methodName}的方法代码通常就是业务逻辑的代码。
 * 
 * Action的invoker的method必须是只含有一个参数：ProcessInvokerContext的方法。通过ProcessInvokerContext提供的方法，
 * 可以通过ProcessInvokerContext的方法获得流程引擎相关信息，包括当前流程ID，当前节点信息，当前流程变量等等。
 * 
 * 
 * @author wyx6fox
 * 
 */
public class Action {

	private String invoker; // {beanName}.{method} , method是sigature如下： method(ProcessInvokerContext context}

	public Action(String invoker) {
		super();
		this.invoker = invoker;
	}

	public String getInvoker() {
		return invoker;
	}

	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}

}
