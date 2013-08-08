package com.matech.audit.service.form.buttonImpl.waresStock;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.waresStock.model.WaresStockGrantVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ReceiveGrantImpl implements FormButtonExtInterface {

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String uuid=request.getParameter("uuid"),result="";
		DbUtil dbUtil=new DbUtil(conn);
		WaresStockGrantVO waresStockGrantVO=null;
		WebUtil webUtil=null;
		UserSession userSession=null;
		int i=0;
		try{
			webUtil=new WebUtil(request, response);
			userSession=webUtil.getUserSession();
			waresStockGrantVO=dbUtil.load(WaresStockGrantVO.class, uuid);
			if(!userSession.getUserId().equals(waresStockGrantVO.getGiveto_id())){
			   result="你不属于签收人，不能签收";
			}
			else if(!"已发放".equals(waresStockGrantVO.getState())){
				result="发放物品未发放，不能签收";
			}else{
				waresStockGrantVO.setState("已签收");
				waresStockGrantVO.setSigning_time(StringUtil.getCurDateTime());
				i+=dbUtil.update(waresStockGrantVO);
				if(i==1){
					result="签收成功";
				}else{
					result="签收失败";
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
