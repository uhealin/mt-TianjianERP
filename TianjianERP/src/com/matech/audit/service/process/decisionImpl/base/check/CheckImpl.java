package com.matech.audit.service.process.decisionImpl.base.check;

import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;

public class CheckImpl extends BaseDecisionHandler {

	@Override
	public String decide(OpenExecution arg0) {
		// TODO Auto-generated method stub
		
		Map map = this.getVariableMap(arg0) ;
		String amount = (String)map.get("amount") ;
		
		double d=Double.parseDouble(amount);
		
		String result="";
		if(d <= 2000){
			result= "to 部门主管";
		}else if(2000 <d  && d < 50000){
			result= "to 总裁助理";
		}else if(5000 <d && d < 20000 ){
			result= "to 副主任会计师";
			
		}else if(20000 <d && d < 100000){
			result= "to 副总裁或执行总裁";
			
		}else if(d > 100000){
			result= "to 主任会计师";
		}
		
		return result;
	}

	
	
}
