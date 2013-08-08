package com.matech.audit.service.process.impl.cadet;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.cadet.model.CadetCheckVO;
import com.matech.audit.service.cadet.model.CadetVO;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.pub.db.DbUtil;

public class HrCheckNodeHandler extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		
		String uuid=(String)execution.getVariable("uuid");
		Connection conn=null;
		DbUtil dbUtil=null;
		CadetCheckVO cadetCheckVO=null;
		CadetVO cadetVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			cadetCheckVO=dbUtil.load(CadetCheckVO.class,uuid);
			cadetVO=dbUtil.load(CadetVO.class,cadetCheckVO.getPracticeId());
		//　　　　录用----将实习状态改为【考核通过待体检】      不录用---将实习状态改为【考核不通过】
		  // 延期考核-将实习状态改为【延期考核】
			if("录用".equals(cadetCheckVO.getDirector_opinion())){
				cadetVO.setCadet_state("考核通过待体检");
			}else if("不录用".equals(cadetCheckVO.getDirector_opinion())){
				cadetVO.setCadet_state("考核不通过");
			}else if("延期考核".equals(cadetCheckVO.getDirector_opinion())){
				cadetVO.setCadet_state("延期考核");
				
			}
			dbUtil.update(cadetVO);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		
	}

}
