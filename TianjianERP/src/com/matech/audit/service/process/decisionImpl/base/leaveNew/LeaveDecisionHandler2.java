package com.matech.audit.service.process.decisionImpl.base.leaveNew;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.framework.pub.db.DbUtil;

public class LeaveDecisionHandler2 extends BaseDecisionHandler {

	@Override
	public String decide(OpenExecution arg0) {
		// TODO Auto-generated method stub
		
		Map map = this.getVariableMap(arg0) ;
		String uuid = (String)map.get("uuid") ;
		
		String sql="SELECT autoid FROM k_rank WHERE name =(SELECT rank FROM k_user WHERE id=(select userid from k_leave_new where uuid='"+uuid+"') limit 0,1) ";
		String result="";
        Connection conn=null;
		DbUtil dbUtil=null;
		int j=0;
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);

			j=dbUtil.queryForInt(sql);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(j<=3004){
			result="to 主任会计师审核";
		} else{
			result="to 记录归档";
		}
		
		return result;
	}
	
	
	public static void main(String[] args) throws ParseException {
		
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	   Date end = df.parse("2004-02-20 13:31:41");
	   Date start = df.parse("2004-02-05 13:31:40");

	   long total = end.getTime()-start.getTime();
	   long fifteen = 24*60*60*1000*15;
	   
	   if(total > fifteen) {
		   System.out.println("大于15天");
	   } else {
		   System.out.println("小于15天");
	   }
	}

	
	
}
















