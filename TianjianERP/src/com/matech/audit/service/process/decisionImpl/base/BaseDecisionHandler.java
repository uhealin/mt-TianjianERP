/**
 * 
 */
package com.matech.audit.service.process.decisionImpl.base;

import java.util.Map;

import org.jbpm.api.jpdl.DecisionHandler;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.pvm.internal.model.ActivityImpl;

/**
 * @author bill
 *
 */
public abstract class BaseDecisionHandler implements DecisionHandler {

	
	private static final long serialVersionUID = 1L;
	private String applyUserId ;
	private ActivityImpl curActivity ;
	private Map variableMap ;
	
	public abstract String decide(OpenExecution arg0) ;
	
	public String getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public ActivityImpl getCurActivity() {
		return curActivity;
	}

	public void setCurActivity(ActivityImpl curActivity) {
		this.curActivity = curActivity;
	}

	public Map getVariableMap(OpenExecution arg0) {
		try {
			Map map = arg0.getVariables() ;
			return map ;
		}catch(Exception e) {
			return this.variableMap ;
		}
	}

	public void setVariableMap(Map variableMap) {
		this.variableMap = variableMap;
	}
	
	

}
