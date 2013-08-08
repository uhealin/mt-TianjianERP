package com.matech.audit.service.doc;

import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.schedule.ScheduleService;


public class DocRecSchedule implements Job {

	

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//System.err.println("收文提醒任务："+StringUtil.getCurDateTime());
	
		List<DocRecVO> docRecVOs  = new ArrayList<DocRecVO>();		
		Connection conn=null;
		DbUtil dbUtil=null;
		DocRecService docRecService=null;
		ScheduleService scheduleService=null;
		int minute=Calendar.getInstance().get(Calendar.MINUTE);
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			scheduleService=new ScheduleService(conn);
			if(!scheduleService.isExcutionTime(this.getClass()))return;
			
			docRecService=new DocRecService(conn);
			docRecVOs=dbUtil.select(DocRecVO.class, "select * from {0} where state not in (?,?)","审核完毕","已处理");
			for(DocRecVO docRecVO : docRecVOs) {
				boolean isTimeout= compareDate(docRecVO);
				if(!isTimeout) continue;
				System.out.println(MessageFormat.format("{0} 收文文号 {1} 即将到达截止日期  {2}",
				  StringUtil.getCurDateTime(),
				  docRecVO.getRec_doc_no(),
				  docRecVO.getTimeout_date()
			    ));
                if(minute>=0&&minute<=6){
				docRecService.remindStart(docRecVO,true);
                }else if(minute>=12&&minute<=18)
				docRecService.remindCreater(docRecVO, true);
			}
	
		} catch (Exception e) {
			
		}finally{
			DbUtil.close(conn);
		}

	}
	

	public boolean compareDate(DocRecVO docRecVO) throws Exception {
		
		SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
		String endTime = docRecVO.getTimeout_date();
		Date end = smdf.parse(endTime),today=smdf.parse(StringUtil.getCurDate());
     
		return end.compareTo(today)==0;
	}
	
	public boolean compareDate2(DocRecVO docRecVO) throws Exception {
		
		SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endTime = docRecVO.getTimeout_date() + " 00:00:00";
		Date end = smdf.parse(endTime);
		long l=end.getTime() - new Date().getTime();
		return( l<= 24*3600*1000&&l>0) ;
	}

}

