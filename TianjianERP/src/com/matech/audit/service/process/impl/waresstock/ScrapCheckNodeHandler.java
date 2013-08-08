package com.matech.audit.service.process.impl.waresstock;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.BaseNodeHandler;
import com.matech.audit.service.waresStock.model.WaresStockDetails;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.audit.service.waresStock.model.WarestockdetailsVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class ScrapCheckNodeHandler extends BaseNodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		super.nodeStart(execution);
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		super.nodeEnd(execution);
		String uuid=(String)execution.getVariable("uuid");
		Connection conn=null;
		DbUtil dbUtil=null;
		
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    WarestockdetailsVO waresStockDetails=dbUtil.load(WarestockdetailsVO.class, uuid);
		    waresStockDetails.setStatus("已审核");
		    waresStockDetails.setScrap_time(StringUtil.getCurDateTime());
		    dbUtil.update(waresStockDetails);
		    WaresStockVO waresStockVO=dbUtil.load(WaresStockVO.class,waresStockDetails.getWaresStockId());
		    int scrap=Integer.parseInt(waresStockVO.getScrappedStock());
		    scrap+=Integer.parseInt(waresStockDetails.getQuantity());
		    waresStockVO.setScrappedStock(String.valueOf(scrap));
		    dbUtil.update(waresStockVO);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
	        DbUtil.close(conn);
		}
		
	}

}
