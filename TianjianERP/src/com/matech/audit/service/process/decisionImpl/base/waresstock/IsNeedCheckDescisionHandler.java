package com.matech.audit.service.process.decisionImpl.base.waresstock;

import java.sql.Connection;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.audit.service.waresStock.model.WaresStreamVO;
import com.matech.framework.pub.db.DbUtil;

public class IsNeedCheckDescisionHandler extends BaseDecisionHandler {

	@Override
	public String decide(OpenExecution arg0) {
		// TODO Auto-generated method stub
		Map map=this.getVariableMap(arg0);
		
		String uuid=(String)map.get("uuid");
		WaresStreamVO waresStreamVO=null;
		Connection conn=null;
		DbUtil dbUtil=null;
		String re="to 行政部人员发放";
		try{
			//uuid=(String)arg0.getVariable("uuid");
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresStreamVO=dbUtil.load(WaresStreamVO.class, uuid);
			WaresStockVO waresStockVO=dbUtil.load(WaresStockVO.class, waresStreamVO.getWaresStockId());
		    if("是".equals(waresStockVO.getNeed_check_ind())){
		    	re="to 行政主管审批";
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return re;
	}

}
