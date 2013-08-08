package com.matech.audit.service.process.decisionImpl.base.leaveCompany;

import java.sql.Connection;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.framework.pub.db.DbUtil;

public class LeaveCompanyCheckImpl extends BaseDecisionHandler {

	@Override
	public String decide(OpenExecution arg0) {
		// TODO Auto-generated method stub
		
		Map map = this.getVariableMap(arg0) ;
		String uuid = (String)map.get("uuid") ;
		System.out.println("0--------------dicede---"+uuid);
		String sql="SELECT autoid FROM k_rank WHERE name =(SELECT rank FROM k_user WHERE id=(select userId from k_leaveOfficeTJ where uuid='"+uuid+"')) ";
		String result="";
        Connection conn=null;
		DbUtil dbUtil=null;
		int j=0;
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);

			j=dbUtil.queryForInt(sql);
			System.out.println("----------check---j:"+j);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(j<=3004){
			System.out.println("--------------to 分管领导审核1-");
			result="to 分管领导审核1";
		} else{
			System.out.println("-------------to end1-");

			result="to 分管领导审核2";
		}
		
		return result;
	}

	
	
}
