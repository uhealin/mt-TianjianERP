package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;


public class _1052_0 extends AbstractAreaFunction {

	/**
	 * 
	 * 横向刷出1051公式；
	 * 
	 * 计算费用的方法：
	 * 最终返回值就是 A1-A2
	 *
	 * 主要参数：
	 * 1、一个subjectid，提供的话取指定科目编号的发生逐月累计其发生额；
	 *    不指定的话取ds科目，逐月累计其发生额；
	 * 2、还有一个month,不给取项目结束月;=01到12,取指定月 
	 * 
	 * 次要参数：
	 *    和其他1039衍生公式不同，去掉了year=-1&thisyear=1这2个参数
	 * 
	 * 公式使用举例：
	 * 取指定科目主营业务收入的；
	 * 
  		=取行公式覆盖(1052, "公式", "formula","&cs=累计折旧&ds=制造费用&dt=-1")
  		分析 累计折旧 记贷时 的所有对方科目 每个月（至项目结束月）的对应发生额
	 * 
	 * 主要用于63年审模板，主营业务收入等审定表
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		ASFuntion asf=new ASFuntion();
		
		String accpackageid=(String)args.get("curAccPackageID");
		String projectid=(String)args.get("curProjectid");
		
		
		ASFuntion CHF = new ASFuntion();
		
		//分割符
		String s=(String)args.get("s");
		if (s==null || "".equals(s)){
			s=";";
		}
		
		//第一个科目（下面称借方科目）
		String ds=(String)args.get("ds");
		String myds="";
		
		//第二个科目（下面称贷方科目）
		String cs=(String)args.get("cs");
		cs=asf.replaceStr(cs, s, "','");
		String mycs="";
		
		//方向
		String dt=(String)args.get("dt"),ptype="";
		if (dt!=null && dt.equals("-1")){
			ptype="-1";
		}else{
			ptype="1";
		}
		
		//String accpackageid = accpackageid;
		//String ssID = CHF.showNull(request.getParameter("ssID"));
		
		
		//  String ssID2 = CHF.showNull(request.getParameter("ssID2"));//科目编号
		//  String cvc_begyear = CHF.showNull(request.getParameter("cvc_begyear"));
		//  String direction=

		//  String cvc_begmonth = CHF.showNull(request.getParameter("cvc_begmonth"));
		//  String cvc_endmonth = CHF.showNull(request.getParameter("cvc_endmonth"));

		  
		int i = 0;
		
		String sql="",strResult="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
			
				st=conn.createStatement();
				
				String[] result=getProjectAuditAreaStringByProjectid(conn,projectid);
				
				//取项目起止日期，在本公式中有作用的只是结束月
				String beginyear =result[0];
				String beginmonth =result[1];
				String endyear=result[2];
				String endmonth=result[3]; 
				
				//补零
				if (beginmonth.length()==1)
					beginmonth="0"+beginmonth;
				
	            if (endmonth.length()==1)
	            	endmonth="0"+endmonth;
				
	            String begindate=result[0]+"-"+result[1]+"-01";
	            String enddate=result[2]+"-"+endmonth+"-31";
	    		
	            
	            int strBegin = Integer.parseInt(result[0]) * 12 + Integer.parseInt(result[1]);
	            int strEnd = Integer.parseInt(result[2]) * 12 + Integer.parseInt(result[3]);
	            
	            
				//分析贷方科目
	            if ("1".equals(ptype)){
	            	
	            	//分析借方科目
		            sql=" select  \n" 
						+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
						+" from ( select distinct subjectid from  c_account \n" 
						+" where isleaf1=1 \n" 
						+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
						+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
						+" and subjectfullname2 like '"+ds+"%' \n"
						+" ) a";
		            System.out.println("qwh:sql1="+sql);
					rs=st.executeQuery(sql);
					if(rs.next()){
						myds=rs.getString(1);
					}else{
						myds="";
					}
	            	
	            	sql = "select distinct a.subjectid,a.subjectname  from c_accpkgsubject  a,c_accpkgsubject b,( \n" 
	            			+"select distinct subjectid,subjectname1 from ("
				   			+" select  a.subjectid,a.subjectname1 \n"
					  		+ " from c_subjectentry a, \n"
					  		+ " ( \n"
					  		+ " select  distinct voucherid,subjectid  from  \n"
					  		+ " c_subjectentry  \n"
					  		+ " where vchdate>='"+begindate+"'  \n"
					  		+ " and vchdate<='"+enddate+"'  \n"
					  		+ " and subjectid in ('"+myds+"') \n"
					  		+ " and dirction >0 and occurvalue>0 \n"
					  		+ " )b \n"
					  		+ " where ((a.dirction<0  and  a.occurvalue>0)or (a.dirction>0 and  a.occurvalue<0 and a.subjectid not in ('"+myds+"'))) \n"
					  		+ " and a.voucherid=b.voucherid   \n"
					  		+ "  "
					  		+ " union all "
					  		+ " select distinct a.subjectid,a.subjectname1 \n"
			  				+ " from c_subjectentry a, \n"
			  				+ " ( \n"
			  				+ " select  distinct voucherid,subjectid  from  \n"
			  				+ " c_subjectentry  \n"
			  				+ " where vchdate>='"+begindate+"'  \n"
			  				+ " and vchdate<='"+enddate+"'  \n"
			  				+ " and subjectid in ('"+myds+"') \n"
			  				+ " and dirction >0 and occurvalue<0 \n"
			  				+ " )b \n"
			  				+ " where ((a.dirction<0 and   a.occurvalue<0)or (a.dirction>0 and  a.occurvalue>0 and a.subjectid not in ('"+myds+"'))) \n"
			  				+ " and a.voucherid=b.voucherid   \n"
			  				+ " )a \n"
			  				
			  				+ " )c \n"
			  				+ " where a.accpackageid='"+accpackageid+"' and a.level0=1 \n"
			  				+ " and b.accpackageid='"+accpackageid+"' and b.subjectid=c.subjectid \n"
			  				+ " and (a.subjectfullname=b.subjectfullname or b.subjectfullname like concat(a.subjectfullname,'%')) order by 1";
	            	
	            	System.out.println("qwh:sql2="+sql);
					rs=st.executeQuery(sql);
					while (rs.next()){
						//=取行公式覆盖(1052, "公式", "formula","&cs=累计折旧&ds=制造费用&dt=-1")
						strResult += " union select '=取列公式覆盖(1053, \""+rs.getString(2)+"\", \"occ\",\"&cs="+rs.getString(2)+"&ds="+ds+"&dt="+ptype+"\")'";
					}
					rs.close();
				
			  }else{
				  
				  //分析贷方科目
		            sql=" select  \n" 
						+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
						+" from ( select distinct subjectid from  c_account \n" 
						+" where isleaf1=1 \n" 
						+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
						+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
						+" and subjectfullname2 like '"+cs+"%' \n"
						+" ) a";
		            System.out.println("qwh:sql1="+sql);
					rs=st.executeQuery(sql);
					if(rs.next()){
						mycs=rs.getString(1);
					}else{
						mycs="";
					}
					rs.close();
				  
					  sql = "select distinct a.subjectid,a.subjectname  from c_accpkgsubject  a,c_accpkgsubject b,( \n" 
						+"select distinct subjectid,subjectname1 from ("
					    +" select  a.subjectid,a.subjectname1 \n"
				  		+ " from c_subjectentry a, \n"
				  		+ " ( \n"
				  		+ " select  distinct voucherid,subjectid   from  \n"
				  		+ " c_subjectentry  \n"
				  		+ " where vchdate>='"+begindate+"'  \n"
				  		+ " and vchdate<='"+enddate+"'  \n"
				  		+ " and subjectid in ('"+mycs+"') \n"
				  		+ " and dirction <0  and occurvalue>0 \n"
				  		+ " )b \n"
				  		+ " where ((a.dirction>0 and  a.occurvalue>0)or (a.dirction<0 and  a.occurvalue<0 and a.subjectid not in ('"+mycs+"'))) \n"
				  		+ " and a.voucherid=b.voucherid  \n"
				  		+ "  "
				 		+ " union all "
			  			+ " select  a.subjectid,a.subjectname1 \n"
						+ " from c_subjectentry a, \n"
						+ " ( \n"
						+ " select  distinct voucherid,subjectid  from  \n"
						+ " c_subjectentry  \n"
						+ " where vchdate>='"+begindate+"'  \n"
						+ " and vchdate<='"+enddate+"'  \n"
						+ " and subjectid in ('"+mycs+"') \n"
						+ " and dirction <0 and occurvalue<0 \n"
						+ " )b \n"
						+ " where ((a.dirction>0 and   a.occurvalue<0)or (a.dirction<0 and  a.occurvalue>0 and a.subjectid not in ('"+mycs+"'))) \n"
						+ " and a.voucherid=b.voucherid   \n"
						+ " )a \n"
		  				
		  				+ " )c \n"
		  				+ " where a.accpackageid='"+accpackageid+"' and a.level0=1 \n"
		  				+ " and b.accpackageid='"+accpackageid+"' and b.subjectid=c.subjectid \n"
		  				+ " and (a.subjectfullname=b.subjectfullname or b.subjectfullname like concat(a.subjectfullname,'%')) order by 1";
					
					System.out.println("qwh:sql2="+sql);
					rs=st.executeQuery(sql);
					while (rs.next()){
						//=取行公式覆盖(1052, "公式", "formula","&cs=累计折旧&ds=制造费用&dt=-1")
						strResult += " union select '=取列公式覆盖(1053, \""+rs.getString(2)+"\", \"occ\",\"&ds="+rs.getString(2)+"&cs="+cs+"&dt="+ptype+"\")'";
					}
					rs.close();
			  }
	        
            
			//最后的组装
			sql="select * from (select 'aaa' as formula)t where 1=2 " + strResult; 
			System.out.println("qwh:sql3="+sql);
			rs=st.executeQuery(sql);
				
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
