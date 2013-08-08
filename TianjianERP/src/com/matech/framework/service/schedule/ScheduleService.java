package com.matech.framework.service.schedule;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.schedule.model.ScheduleVO;

public class ScheduleService {

    private Connection conn = null;
    private  DbUtil dbUtil=null;
	public ScheduleService(Connection conn){
		this.conn=conn;
		try {
			dbUtil=new DbUtil(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param scheduleVO
	 * @return 
	 */ 
	public boolean isExcutionTime(ScheduleVO scheduleVO){
		boolean bh=false,bm=false;
		String exc_hour=scheduleVO.getExc_hours()==null?"":scheduleVO.getExc_hours();
		String exc_minute=scheduleVO.getExc_minutes()==null?"":scheduleVO.getExc_minutes();

		try {
			
			Calendar calendar=Calendar.getInstance();
			int hour=calendar.get(Calendar.HOUR_OF_DAY);
			int minute=calendar.get(Calendar.MINUTE);
			
			bh=StringUtil.isBlank(exc_hour);
			if(!bh){
				
				String[] exc_hours=exc_hour.split(",");
				bh=StringUtil.isIn(String.valueOf(hour), exc_hours);
				//for (String h : exc_hours) {
				//	int i=Integer.parseInt(h);
				//	if(i==hour){
				//		bh=true;
				//		break;
				//	}
				//}
			}
			bm=StringUtil.isBlank(exc_minute);
			if(bh&&!bm){
				String[] exc_minutes=exc_minute.split(",");
				bm=StringUtil.isIn(String.valueOf(minute), exc_minutes);
				//for (String m : exc_minutes) {
				//	int j=Integer.parseInt(m);
				//	if(j==minute){
				//		bm=true;
				//		break;
				//	}
				//}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!(bh&&bm)){
			System.out.println(MessageFormat.format("{0} 定时任务:{1} {2} 不满足设定 小时:{3}  分钟:{4} ，不执行"
			  ,StringUtil.getCurDateTime()
			  ,scheduleVO.getCnname()
			  ,scheduleVO.getTask()
			  ,exc_hour
			  ,exc_minute
			));
		}else{
			System.out.println(MessageFormat.format("{0} 定时任务:{1} {2} 满足设定 小时:{3}  分钟:{4}"
					  ,StringUtil.getCurDateTime()
					  ,scheduleVO.getCnname()
					  ,scheduleVO.getTask()
					  ,exc_hour
					  ,exc_minute
					));
		}
		return bh&&bm;
	}
	
	 
	/**
	 * 
	 * @param 根据task值来查找s_schedule
	 * @return 
	 */
	public boolean isExcutionTime(Class<? extends Job> cls){
        ScheduleVO scheduleVO=dbUtil.select(ScheduleVO.class, "select * from {0} where task=?", cls.getName()).get(0);
        return isExcutionTime(scheduleVO);
	}
	

}
