package com.matech.audit.service.process.impl.jobPlan;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class JobPlanFlow extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
        String uuid=(String)execution.getVariable("uuid");

        String sqlUser="UPDATE k_jobplan_item SET state='已审核' WHERE mainformid='"+uuid+"'";

        Connection conn=null;
		DbUtil dbUtil=null;
		try{ 
			conn=new DBConnect().getConnect();
			conn.setAutoCommit(false);
			dbUtil=new DbUtil(conn);
//			dbUtil.execute(sqlUser);
			int i=dbUtil.executeUpdate(sqlUser);
//			int j=dbUtil.queryForInt(sql);
		    conn.commit();
		    
		   
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		
	}

}
