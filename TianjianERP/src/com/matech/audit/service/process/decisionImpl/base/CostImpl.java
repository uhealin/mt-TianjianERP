package com.matech.audit.service.process.decisionImpl.base;

import java.util.Map;

import org.jbpm.api.model.OpenExecution;

public class CostImpl extends BaseDecisionHandler{

	@Override
	public String decide(OpenExecution arg0) {
		
		Map map= this.getVariableMap(arg0);
		String bs = (String) map.get("bills_class");
		String result="";
		if("业务费用".equals(bs)){
			
			result="to 费用报销审核";
		}else if("行政费用".equals(bs)){
			result = "to task3";
		}
		
		return result;
	}

}
