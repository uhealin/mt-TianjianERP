package com.matech.audit.service.waresStock;


import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;

import com.matech.audit.service.process.BaseAssignment;

public class WaresStockAssign extends BaseAssignment{
	
	private static final long serialVersionUID = -1460512789043634760L;
	
	@Override
	public void assign(Assignable assignable, OpenExecution openExecution) {
		
		String creator = (String)openExecution.getVariable("Marketing");
		System.out.println("#######"+creator);
		String[] assignUser = getUser(creator) ;
		for(String user:assignUser) {
			assignable.addCandidateUser(user) ;
		}
	}
}
