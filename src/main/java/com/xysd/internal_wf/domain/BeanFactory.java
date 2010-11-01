package com.xysd.internal_wf.domain;
/**
 * 
 * 负责解析 Action中的invoker的IOC容器的接口，具体实现由具体的IOC容器负责处理。
 * 
 * 
 * 
 * @author wyx6fox
 * 
 */
public interface BeanFactory {
	
	public Object getBean(String beanId);

}
