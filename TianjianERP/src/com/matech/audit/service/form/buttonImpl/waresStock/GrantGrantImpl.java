package com.matech.audit.service.form.buttonImpl.waresStock;

import java.sql.Connection;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.waresStock.WaresStockService;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockDetails;
import com.matech.audit.service.waresStock.model.WaresStockGrantVO;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class GrantGrantImpl implements FormButtonExtInterface {

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		String uuid=request.getParameter("uuid"),result="";
		DbUtil dbUtil=new DbUtil(conn);
		WaresStockGrantVO waresStockGrantVO=null;
		WebUtil webUtil=null;
		UserSession userSession=null;
		WaresStockService waresStockService=new WaresStockService(conn);
		WaresStockVO waresStockVO=null;
		int i=0;
		try{
			webUtil=new WebUtil(request, response);
			userSession=webUtil.getUserSession();
			waresStockGrantVO=dbUtil.load(WaresStockGrantVO.class, uuid);
			if(!userSession.getUserId().equals(waresStockGrantVO.getGranter_id())){
			   result="你不属于发放人，不能发放";
			}
			else if(!"已审核".equals(waresStockGrantVO.getState())){
				result="发放申请未审核，不能发放";
			}else{
				waresStockGrantVO.setState("已发放");
				waresStockGrantVO.setSigning_time(StringUtil.getCurDateTime());
				i+=dbUtil.update(waresStockGrantVO);
				if(i==1){
					result="发放成功";
					waresStockVO=dbUtil.load(WaresStockVO.class, waresStockGrantVO.getWareStockId());
					int userStock=Integer.parseInt(waresStockVO.getScrappedStock());
					waresStockVO.setUsableStock(String.valueOf(userStock-waresStockGrantVO.getQutity()));
					dbUtil.update(waresStockVO);
					WaresStockDetails details=new WaresStockDetails();
					details.setUuid(UUID.randomUUID().toString());
					details.setUserId(userSession.getUserId());
					details.setWaresStockId(waresStockGrantVO.getWareStockId());
					details.setQuantity("1");
					details.setCtype("发放");
					details.setStatus("已发起");
					details.setDate(StringUtil.getCurDateTime());
					waresStockService.addWaresStockDetails(details);
				}else{
					result="发放失败";
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return result;
	}

}
