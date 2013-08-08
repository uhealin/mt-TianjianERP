package com.matech.audit.service.process.impl.enterpriseQualification;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.api.listener.EventListenerExecution;


import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;


public class EnterpriseQualificationImpl  extends NodeHandler {

	private static final long serialVersionUID = 869979587109348791L;
	private  Map<String, String> curNodeNameToChangeState = new HashMap<String, String>();
	
	public EnterpriseQualificationImpl() {
		curNodeNameToChangeState.put("借阅申请", "1");
		curNodeNameToChangeState.put("分所主管审核", "2");
		curNodeNameToChangeState.put("证书管理员审核", "3");
		curNodeNameToChangeState.put("行政部借出", "4");
	}
	
	@Override
	public void nodeStart(EventListenerExecution execution) {
		
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			String uuid = (String) execution.getVariable("uuid");
			String currentNodeName = (String) execution.getVariable("curNodeName");
			String nextStatus = curNodeNameToChangeState.get(currentNodeName);
			System.out.println("审核：|"+uuid+"|"+currentNodeName+"|"+nextStatus);
			
			DbUtil db = new DbUtil(conn);
			String sql = "";
			
			sql = "update k_enterpriseborrow set processstate = ? where uuid = ? ";
			db.execute(sql, new String[]{nextStatus,uuid});
			
			if("4".equals(nextStatus)){
				
				sql = "update k_enterpriseborrow set lendertime = ?,property=? where uuid = ? ";
				db.execute(sql, new String[]{StringUtil.getCurDateTime(),"借出",uuid});
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

	
}
