package com.matech.audit.service.process.impl.official;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;

import com.matech.audit.service.official.model.Official;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.pub.db.DbUtil;

public class OfficialNodeHandler extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {

	}
	@Override
	public void nodeEnd(EventListenerExecution execution) {
		 String uuid=(String)execution.getVariable("uuid");
         
	        Connection conn=null;
			DbUtil dbUtil=null;
			Official official = null;
			try{
				conn=new DBConnect().getConnect();
				dbUtil=new DbUtil(conn);
				
				official = dbUtil.load(Official.class, uuid);
				official.setState("审核完成");
				dbUtil.update(official);
			    		   
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
	}

}
