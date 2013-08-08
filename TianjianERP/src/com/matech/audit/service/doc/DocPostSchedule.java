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
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.schedule.ScheduleService;
import com.matech.framework.service.schedule.model.ScheduleVO;
import com.matech.framework.work.backtask.DelTask;



public class DocPostSchedule implements Job {

   private	Connection conn=null;
 

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//System.err.println("收文提醒任务："+StringUtil.getCurDateTime());
	    Calendar cal=Calendar.getInstance();
	    int hour=cal.get(Calendar.HOUR_OF_DAY);
	    int minute=cal.get(Calendar.MINUTE);
	    ScheduleService scheduleService=null;
	    
	    	
	    
	    
		List<DocPostVO> docPostVOs  = new ArrayList<DocPostVO>();		
		
		
		DocPostService docPostService=null;
		
		DbUtil dbUtil=null;
		try {
			conn=conn==null?new DBConnect().getConnect():conn;
			dbUtil=new DbUtil(conn);
			scheduleService=new ScheduleService(conn);
			if(!scheduleService.isExcutionTime(this.getClass()))return;
			docPostService=new DocPostService(conn);

			//查找所有还未结束的行政发文（业务发文不提醒）
			docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0} where node_code not in (''end'') and del_ind=0  AND ctype='a' ");
			for(DocPostVO docPostVO : docPostVOs) {
				
				if(this.handlerTimeout(docPostVO)>0){
					dbUtil.update(docPostVO);
				}
				if(hour==8){
					docPostService.remaindTimeoutSigner(docPostVO);
				}
				if(hour==9){
				docPostService.remaindTimeoutCreater(docPostVO);
				} 
			}
	
		} catch (Exception e) {
			
		}finally{
			DbUtil.close(conn);
		}

	}
	
	


	public int handlerTimeout(DocPostVO docPostVO){
		int eff=0;
		SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn=null;
		DbUtil dbUtil=null;
		Date ndate=null;
		try{
			ndate=smdf.parse(StringUtil.getCurDate());
			if(StringUtil.isIn(docPostVO.getNode_code(),new String[]{"hq","xm"})){
				try{
					Date d=smdf.parse(docPostVO.getSignissue_date());
					if(ndate.after(d)){
						docPostVO.setNode_code(Node.qf.name());
						String msg= MessageFormat.format("{0} 超过签发日期，直接进入签发环节",StringUtil.getCurDateTime());
						docPostVO.setNode_remark(msg);
						eff++;			
					}
				}catch(Exception e){System.out.println("签发过期检查出错:"+e.getMessage());}
			}
			
			else if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.qf.name(),Node.hy.name()})) {
				try{
					Date d=smdf.parse(docPostVO.getTimeout_date());
					
					//cal.add(Calendar.DAY_OF_YEAR, 2);
					if(ndate.after(d)){
						
						docPostVO.setNode_code(Node.end.name());
						docPostVO.setNode_remark(MessageFormat.format("{0} 超过过期日期，文件视为默认不核阅，事务所复核流程结束",StringUtil.getCurDateTime()));
						eff++;
					}
				}catch(Exception e){System.out.println("核阅过期检查出错:"+e.getMessage());}
			}
			
			if("FW_HYWJ".equals(docPostVO.getDoc_type())){
				try{
					Date d1=smdf.parse(docPostVO.getSignissue_date());
					Calendar cal=Calendar.getInstance();
					cal.setTime(ndate);
					cal.add(Calendar.DAY_OF_YEAR, -2);
					Date d2=cal.getTime();
					if(d2.after(d1)){
						conn=new DBConnect().getConnect();
						dbUtil=new DbUtil(conn);
						UserVO userVO=dbUtil.load(UserVO.class,Integer.parseInt(docPostVO.getChecker_ids()));
						docPostVO.setCheck_info(userVO.getName()+"(不核阅)");
						docPostVO.setNode_code(Node.end.name());
						docPostVO.setNode_remark(MessageFormat.format("{0} 超过签发日期2日，文件视为默认不核阅，事务所复核流程结束",StringUtil.getCurDateTime()));
						eff++;
					}			
				}catch(Exception e){System.out.println("FW_HYWJ过期检查出错:"+e.getMessage());}
			}
			
			}catch(Exception ex){ex.printStackTrace();}
			
		return eff;
	}
	
	
	

}

