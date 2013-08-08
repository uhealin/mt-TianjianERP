package com.matech.audit.service.process.decisionImpl.base.waresstock;

import java.sql.Connection;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.audit.service.waresStock.model.WaresPrucVO;
import com.matech.audit.service.waresStock.model.WaresStockGrantVO;
import com.matech.audit.service.waresStock.model.WaresStreamVO;
import com.matech.framework.pub.db.DbUtil;

public class ApplyCheckDescisionHandler extends BaseDecisionHandler {

	
	/*
	 * 2000及2000以下 -----部门主管， 
2000—5000（含）----分管总裁助理
5000—20000（含）---分管副主任会计师
20000—100000（含）---分管副总裁或执行总裁
100000以上---主任会计师
	 * (non-Javadoc)
	 * @see com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler#decide(org.jbpm.api.model.OpenExecution)
	 */
	@Override
	public String decide(OpenExecution arg0) {
		// TODO Auto-generated method stub
        Map map=this.getVariableMap(arg0);
		
		String uuid=(String)map.get("uuid");
		Connection conn=null;
		DbUtil dbUtil=null;
		int eff=0;
		String re="部门主管";
		WaresPrucVO waresPrucVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresPrucVO=dbUtil.load(WaresPrucVO.class, uuid);
			if(waresPrucVO.getExpect_amount()<=2000){
				re="部门主管";
			}else if(waresPrucVO.getExpect_amount()<=5000){
				re="分管总裁助理";
			}else if(waresPrucVO.getExpect_amount()<=20000){
				re="分管副主任会计师";
			}else if(waresPrucVO.getExpect_amount()<=100000){
				re="分管副总裁或执行总裁";
			}else if(waresPrucVO.getExpect_amount()> 100000){
				re="主任会计师";
			}
			
		}catch(Exception ex){
			//re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		return "to "+re;
	}

}
