package com.matech.audit.service.process;

import org.jbpm.api.jpdl.DecisionHandler;
import org.jbpm.pvm.internal.model.ActivityImpl;

public abstract class DecisionHandlerImpl implements DecisionHandler {

	protected ActivityImpl activityImpl;
	protected String user;
	
	public  void setApplyUserId(String user){
		this.user=user;
	}
	
	public void setCurActivity(ActivityImpl activityImpl ){
		this.activityImpl=activityImpl;
	}
}
