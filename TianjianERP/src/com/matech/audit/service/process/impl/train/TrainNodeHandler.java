package com.matech.audit.service.process.impl.train;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.education.model.TrainVO;
import com.matech.audit.service.official.model.Official;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.pub.db.DbUtil;

public class TrainNodeHandler extends NodeHandler{

	@Override
	public void nodeStart(EventListenerExecution execution) {
		
		
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		String uuid=(String)execution.getVariable("uuid");
        
        Connection conn=null;
		DbUtil dbUtil=null;
		TrainVO trainVO = null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
			trainVO = dbUtil.load(TrainVO.class, uuid);
			trainVO.setState("已审核");
			dbUtil.update(trainVO);
		    		   
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
	}
	
}
