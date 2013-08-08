package com.matech.audit.service.process.impl.waresstock;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.BaseNodeHandler;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.audit.service.waresStock.model.WaresStreamVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class ApplyCheckNodeHandler extends BaseNodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		super.nodeStart(execution);
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		super.nodeEnd(execution);
		Connection conn=null;
		DbUtil dbUtil=null;
		String uuid=(String)execution.getVariable("uuid");
		//WebUtil webUtil=new WebUtil(request, response);
		//UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
	    WaresStreamVO waresStreamVO=null;
		//response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresStreamVO=dbUtil.load(WaresStreamVO.class, uuid);
			waresStreamVO.setStatus("已审核");
			dbUtil.update(waresStreamVO);
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
	}

	
}
