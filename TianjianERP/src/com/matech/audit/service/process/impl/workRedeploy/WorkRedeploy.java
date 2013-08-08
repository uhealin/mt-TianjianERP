package com.matech.audit.service.process.impl.workRedeploy;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class WorkRedeploy extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
        String uuid=(String)execution.getVariable("uuid");

        String former_rank=(String)execution.getVariable("former_rank");

        String userid=(String)execution.getVariable("name_cn");
        String former_department=(String)execution.getVariable("former_department");
        String redeploy_rank=(String)execution.getVariable("redeploy_rank");
        String redeploy_department=(String)execution.getVariable("redeploy_department");
        String sql="select userId from k_leaveOfficeTJ where uuid='"+uuid+"'";
        String sqlUser="UPDATE k_user SET departmentid ='"+redeploy_department+"',rank='"+redeploy_rank+"' WHERE id="+userid;
        
        
        System.out.println("former_rank:"+former_rank+"== former_department: "+former_department+" ==redeploy_rank："+redeploy_rank+" ==redeploy_department: "+redeploy_department+" : "+userid);
        System.out.println(sqlUser);
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
