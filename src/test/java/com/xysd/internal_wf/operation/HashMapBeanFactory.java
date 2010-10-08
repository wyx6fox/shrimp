package com.xysd.internal_wf.operation;

import java.util.HashMap;
import java.util.Map;

import com.xysd.internal_wf.domain.BeanFactory;

public class HashMapBeanFactory implements BeanFactory {
	
	public Map<String, Object> beans = new HashMap<String, Object>();

	public Object getBean(String beanId) {
		
		return beans.get(beanId);
	}

}
