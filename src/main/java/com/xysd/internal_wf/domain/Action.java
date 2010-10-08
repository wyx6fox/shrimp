package com.xysd.internal_wf.domain;

public class Action {
	
	private String invoker;

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
