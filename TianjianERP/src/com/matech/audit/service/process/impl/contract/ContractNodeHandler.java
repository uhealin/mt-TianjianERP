package com.matech.audit.service.process.impl.contract;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.contract.model.ContractVO;
import com.matech.audit.service.oa.labor.model.LaborVO;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class ContractNodeHandler extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {

	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		
		String uuid=(String) execution.getVariable("uuid");
		
			Connection conn=null;
			
			DbUtil dbUtil=null;
			
			LaborVO laborVO=null;
			
			ContractVO contractVO=null;
			
			try{
				
				conn=new DBConnect().getConnect();
	
				dbUtil=new DbUtil(conn);
				
				String sql="select * from oa_subset_labourcont  where userid=? order by C9405 DESC LIMIT 0,1";
				
				String sqlStr="update oa_subset_labourcont set status2=? where uuid=?";
				contractVO=dbUtil.load(ContractVO.class,uuid);
				
				List<LaborVO> list=dbUtil.select(LaborVO.class, sql,new Object[]{contractVO.getUserid()});
				
				LaborVO labor=null;
				if(list.size()>0){
					labor=list.get(0);
					String status="已办理";
					dbUtil.executeUpdate(LaborVO.class, sqlStr,new Object[]{status,labor.getUuid()});
				}
				
				//本次合同期限
				String limit=contractVO.getAgain_sign();
				
				
				
				if(!"到期终止".equals(limit)){
					laborVO=new LaborVO();
					laborVO.setUuid(StringUtil.getUUID());
					laborVO.setUserid(contractVO.getUserid());
					laborVO.setDepartmentid(contractVO.getDepartmentId());
					String lastContractEnd=StringUtil.showNull(contractVO.getLastContractEnd());
					
					int currentLimit=0;
					int month=0;
					String flag="year";
					if("续约1年".equals(limit)){
						laborVO.setC9402("1");
						currentLimit=1;
						
					}else if("续签3年".equals(limit)){
						laborVO.setC9402("3");
						currentLimit=3;
						
					}else if("续签2.5年".equals(limit)){
						laborVO.setC9402("2.5");
						month=30;
						flag="month";
					}else {
						laborVO.setC9402("1.5");
						month=18;
						flag="month";
					}
					
					
					//判断上次合同是否为空
					if(!"".equals(lastContractEnd)){
						String currentStart="";
						String currentEnd="";
						
						if("year".equals(flag)){
							
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
							Date d=df.parse(lastContractEnd);
							 
							Calendar cal=Calendar.getInstance();
							cal.setTime(d);
							cal.add(Calendar.DATE,1);
							Date date=cal.getTime();
							//本次合同开始日期
							currentStart=df.format(date);
							
							cal.setTime(d);
							cal.add(Calendar.YEAR,currentLimit);
							Date dt=cal.getTime();
							//本次合同结束日期
							currentEnd=df.format(dt);
							
							
						}else{
							
							if(lastContractEnd.substring(8,10).equals("31")){
								SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
								
								Date d=df.parse(lastContractEnd);
								 
								Calendar cal=Calendar.getInstance();
								cal.setTime(d);
								cal.add(Calendar.DATE,1);
								Date date=cal.getTime();
								//本次合同开始日期
								currentStart=df.format(date);
								
								cal.setTime(d);
								cal.add(Calendar.MONTH,month);
								Date dt=cal.getTime();
								//本次合同结束日期
								currentEnd=df.format(dt);
							}else{
								SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
								
								Date d=df.parse(lastContractEnd);
								 
								Calendar cal=Calendar.getInstance();
								cal.setTime(d);
								cal.add(Calendar.DATE,1);
								Date date=cal.getTime();
								//本次合同开始日期
								currentStart=df.format(date);
								
								cal.setTime(d);
								cal.add(Calendar.MONTH,month);
								cal.add(Calendar.DAY_OF_MONTH,1);
								Date dt=cal.getTime();
								//本次合同结束日期
								currentEnd=df.format(dt);
							}
							
							
						}
						laborVO.setC9403(currentStart);
						laborVO.setC9405(currentEnd);
					}else{
						laborVO.setC9403("");
						laborVO.setC9405("");
					}
					//合同状态
					laborVO.setSTATUS("0000000000");
					//合同类型
					laborVO.setC9401("劳动合同");
					dbUtil.insert(laborVO);
					
				}
				
				
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
	}
	/**
	 */
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String lastContractEnd="2012-06-30";
		Date d=df.parse(lastContractEnd);
		
		System.out.println(lastContractEnd.substring(8,10));
		 
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE,1);
		Date date=cal.getTime();
		//本次合同开始日期
		String currentStart=df.format(date);
		
		cal.setTime(d);
		cal.add(Calendar.MONTH,18);
		cal.add(Calendar.DAY_OF_MONTH,1);
		Date dt=cal.getTime();
		//本次合同结束日期
		String currentEnd=df.format(dt);
		
		System.out.println(currentStart);
		System.out.println(currentEnd);
	}

}
