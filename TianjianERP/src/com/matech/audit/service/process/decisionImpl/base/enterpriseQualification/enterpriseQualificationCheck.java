package com.matech.audit.service.process.decisionImpl.base.enterpriseQualification;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class enterpriseQualificationCheck extends BaseDecisionHandler{

	private static final long serialVersionUID = 12134312432561L;
	private static final String areaid = "1100";//总所的区域ID
	
	@Override
	public String decide(OpenExecution arg0) {
		Connection conn = null;
		try {
			Map map= this.getVariableMap(arg0);
			System.out.println(map);
			String result="";
			
			String uuid = (String)map.get("uuid") ;
			
			conn = new DBConnect().getConnect("");
			String sql = "select 1 from k_enterpriseborrow a,k_user b,k_department c where a.uuid = ? and c.areaid=? and a.borrowuserid = b.id and b.departmentid = c.autoid ";

			String flag = StringUtil.showNull(new DbUtil(conn).queryForString(sql, new String[]{uuid,areaid}));
			if("1".equals(flag)){
				//总所
				result = "to 证书管理员审核";
			}else{
				//分所
				result = "to 分所主管审核";
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "to 分所主管审核";
		} finally {
			DbUtil.close(conn);
		}
		

	}

}
