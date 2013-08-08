package com.matech.audit.service.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ContractTemplateService {
	
	private Connection conn = null;	
	ASFuntion CHF = new ASFuntion();

	public ContractTemplateService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 获得合同模版名称
	 * @return
	 * @throws Exception
	 */
	public String getTemplates() throws Exception{
				
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer templateNames = new StringBuffer("");
		try {
			String sql = "select typeid,typename from oa_contracttemplate order by typeid";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				templateNames.append("<option value='"+rs.getString(1)+"'>"+rs.getString(2)+"</option>");
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return templateNames.toString();
	}
	
}
