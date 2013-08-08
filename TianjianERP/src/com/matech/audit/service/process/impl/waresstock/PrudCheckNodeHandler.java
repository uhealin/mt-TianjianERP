package com.matech.audit.service.process.impl.waresstock;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.BaseNodeHandler;
import com.matech.audit.service.waresStock.model.WaresPrucVO;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.audit.service.waresStock.model.WaresStreamVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class PrudCheckNodeHandler extends BaseNodeHandler {

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
	    WaresPrucVO waresPrucVO=null;
		//response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresPrucVO=dbUtil.load(WaresPrucVO.class, uuid);
			waresPrucVO.setState("已审核");
			dbUtil.update(waresPrucVO);
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
	}

	
}
