package com.matech.audit.service.form.buttonImpl.waresStock;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockGrantVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class CheckGrantImpl implements FormButtonExtInterface {

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
		int i=0;
		try{
			webUtil=new WebUtil(request, response);
			userSession=webUtil.getUserSession();
			waresStockGrantVO=dbUtil.load(WaresStockGrantVO.class, uuid);
			if(!userSession.getUserId().equals(waresStockGrantVO.getChecker_id())){
			   result="你不属于审核人，不能审核";
			}
			else if(!"未审核".equals(waresStockGrantVO.getState())){
				result="发放申请已被处理，不能再审核";
			}else{
				waresStockGrantVO.setState("已审核");
				
				waresStockGrantVO.setSigning_time(StringUtil.getCurDateTime());
				i+=dbUtil.update(waresStockGrantVO);
				if(i==1){
					result="审核成功";
				}else{
					result="审核失败";
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
