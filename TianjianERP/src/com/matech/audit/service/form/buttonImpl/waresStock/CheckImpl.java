package com.matech.audit.service.form.buttonImpl.waresStock;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockVO;
import com.matech.audit.service.waresStock.model.WaresStream;
import com.matech.audit.service.waresStock.model.WaresStreamVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class CheckImpl implements FormButtonExtInterface{

	public int parseInt(String str){
		int i=0;
		try{
			i=Integer.parseInt(str);
		}catch(Exception ex){}
		return i;
	}
	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		String re="";
		DbUtil dbUtil=new DbUtil(conn);
		String uuid=request.getParameter("uuid");
		WaresStreamVO waresStream=dbUtil.load(WaresStreamVO.class,uuid );
		WaresStockVO waresStock=dbUtil.load(WaresStockVO.class,waresStream.getWaresStockId());
		WebUtil webUtil=new WebUtil(request, response);
        int us=parseInt(waresStock.getUsableStock());
        int qs=parseInt(waresStream.getQuantity());
		if("已领取".equals(waresStream.getStatus())){
			re="申请已领取，不能再领取";
		}
		else if(us<qs)
        {
        	re="申请数量超过可用库存数 ";
        }else{
            waresStock.setUsableStock(String.valueOf(us-qs));
            dbUtil.update(waresStock);
            if(waresStream.getPro_type().equals("lpk")){
            	waresStream.setReceive_state("已领取");
            	waresStream.setReceive_time(StringUtil.getCurDateTime());
            	
            }else if(waresStream.getPro_type().equals("jyk")){
            	waresStream.setReceive_state("已领取");
            	waresStream.setReceive_time(StringUtil.getCurDateTime());
            	
            }else if(waresStream.getPro_type().equals("dzk")){
            	waresStream.setReceive_state("已领取");
            	waresStream.setReceive_time(StringUtil.getCurDateTime());
            	
            	
            	
            }else if(waresStream.getPro_type().equals("lyk")){
            	waresStream.setReceive_state("已领取");
            	waresStream.setReceive_time(StringUtil.getCurDateTime());
            	
            }
            re="礼品领取成功";
            waresStream.setApproveDate(StringUtil.getCurDateTime());
            waresStream.setApproveUserId(webUtil.getUserSession().getUserId());
            waresStream.setStatus("已领取");
            dbUtil.update(waresStream);
        }       
		return re;
	}

}
