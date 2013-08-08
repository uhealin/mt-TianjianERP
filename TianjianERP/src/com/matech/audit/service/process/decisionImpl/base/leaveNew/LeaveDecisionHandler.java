package com.matech.audit.service.process.decisionImpl.base.leaveNew;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jbpm.api.model.OpenExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.leave.model.LeaveNewVO;
import com.matech.audit.service.process.decisionImpl.base.BaseDecisionHandler;
import com.matech.framework.pub.db.DbUtil;

public class LeaveDecisionHandler extends BaseDecisionHandler {

	@Override
	public String decide(OpenExecution arg0) {
		
		Connection conn=null;
		DbUtil dbUtil=null;
		String result = "to 部门主管审核1";
		try {
			Map map = this.getVariableMap(arg0) ;
			
			//String uuid=(String)arg0.getVariable("UUID");
			String uuid = (String)map.get("uuid") ;
			
			//String leave_begin_time = (String)map.get("leave_begin_time") ;
			//String leave_end_time = (String)map.get("leave_end_time") ;
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date end = null;
			Date start = null;
			
			LeaveNewVO leaveNewVO=null;
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			leaveNewVO=dbUtil.load(LeaveNewVO.class,uuid);
			start = df.parse(leaveNewVO.getLeave_begin_time());
		    end = df.parse(leaveNewVO.getLeave_end_time());

		    long total = end.getTime()-start.getTime();
		    long fifteen = 24*60*60*1000*2;
		    if(total > fifteen) {
		    	result = "to 部门主管审核1";
		    } else {
		    	result = "to 部门主管审核2";
		    }
		   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
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
















