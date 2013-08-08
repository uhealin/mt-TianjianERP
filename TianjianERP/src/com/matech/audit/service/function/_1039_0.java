package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class _1039_0 extends AbstractAreaFunction {


	
	/**
	 * 先找出目标科目(ds 新公式的这个参数已经与科目借贷无关了)的全年发生额 A1= 借－贷，
	 * 再找出同时有目标科目和另一个参照科目（cs）的凭证里面该目标科目的发生额 A2= 借 - 贷
	 * 最终返回值就是 A1-A2
	 */
	
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		String cs=(String)args.get("cs");
		if(cs.indexOf("本年利润")>-1 && cs.indexOf("利润分配")>-1){
			return process2( session,  request,  response,  conn,  args);
		}else{
			return process1( session,  request,  response,  conn,  args);
		}
	}
	
	/**
	 * 结转数
	 * @param session
	 * @param request
	 * @param response
	 * @param conn
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public ResultSet process2(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		Statement st=null;
		ResultSet rs=null;
		try {
			ASFuntion asf=new ASFuntion();
			
			String accpackageid=(String)args.get("curAccPackageID");
			String projectid=(String)args.get("curProjectid");
			
			//分割符
			String s=(String)args.get("s");
			//第一个科目（下面称借方科目）
			String ds=(String)args.get("ds");
			String myds="";
			
			//第二个科目（下面称贷方科目）
			String cs=(String)args.get("cs");
			cs=asf.replaceStr(cs, s, "','");

			//余额方向 1 
			String dt=(String)args.get("dt"),ptype="";
			if (dt!=null && dt.equals("-1")){
				ptype="-1";
			}else{
				ptype="1";
			}
			
			//取列时的月份
			String month=request.getParameter("month");
			if (month==null){
				month="";
			}
			
			//年份划定符,-1表示取去年和今年，1表示今年和下年
			String year=request.getParameter("year");
			
//			取项目起止日期，在本公式中有作用的只是结束月
            String endmonth = request.getParameter("endmonth");
			//统计年份，1表示当前年，-1表示另1参考年
			String thisyear=request.getParameter("thisyear");
			if (thisyear==null || thisyear.equals("")){
				thisyear="1";
			}
			if(!"1".equals(thisyear)){
				if(endmonth == null || "".equals(endmonth) ){
					endmonth = "12";
				}
			}
			
			//另一个年份的账套ID
			String anotherApkID="";
			
			//统计的账套编号
			String nowAccpackageid="";
			
			/**
			 * accpackageid : 当前项目的帐套编号
			 * anotherApkID ：比较年份的帐套编号，可以上一年，也可以下一年
			 * nowAccpackageid ：当前刷新的帐套编号，是前面2个帐套编号中的某一个；
			 */
			String sql="";
			

            String[] result=getProjectAuditAreaStringByProjectid(conn,projectid);
            if (endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                endmonth=String.valueOf(result[3]);
            }
            int strBegin = Integer.parseInt(result[0]) * 12 + Integer.parseInt(result[1]);
            int strEnd = Integer.parseInt(result[2]) * 12 + Integer.parseInt(result[3]);
            
            String begindate=result[0]+"-"+result[1]+"-01";
            String enddate=result[2]+"-"+endmonth+"-31";
            
			anotherApkID=String.valueOf((Integer.parseInt(accpackageid)+Integer.parseInt(year)));
			
			if("1".equals(thisyear)){
				nowAccpackageid=accpackageid;
			}else{
				nowAccpackageid=anotherApkID;
				
//				屈总说上年数是开始、结束年都减去1
				strBegin = (Integer.parseInt(result[0]) - 1) * 12 + Integer.parseInt(result[1]);
	            strEnd = (Integer.parseInt(result[2]) - 1) * 12 + Integer.parseInt(endmonth);
	            begindate=String.valueOf(Integer.parseInt(result[0]) - 1)+"-"+result[1]+"-01";
	            enddate=String.valueOf(Integer.parseInt(result[2]) - 1)+"-"+endmonth+"-31";
				
			}
			st=conn.createStatement();
			
			boolean bHasEntry=false;
			sql="select 1 from c_subjectentry where accpackageid="+nowAccpackageid + " limit 1" ;
			rs=st.executeQuery(sql);
			if(rs.next()){
				bHasEntry=true;
			}
			DbUtil.close(rs);
			
			sql=" select  \n" 
				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+"	from ( select distinct subjectid from  c_account \n" 
				+"	where isleaf1=1 \n" 
				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
				+" 	and (subjectfullname2 = '" + ds + "' or subjectfullname2 like '" + ds + "/%') \n"
				+" ) a";
			rs=st.executeQuery(sql);
			if(rs.next()){
				myds=rs.getString(1);
			}else{
				myds="''";
			}
			
			DbUtil.close(rs);
			
//			最后的组装
			if (month.equals("xxx")){
//				取调整数
				sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname,if(b.occurvalue is null,0,b.occurvalue)* ("+ptype+") as occ \n"
					+"from \n"
					+"( \n"
					+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
					+"	from c_account  \n"
					+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
					+"	and isleaf1=1 \n"
					+"	and (subjectfullname2 = '" + ds + "' or subjectfullname2 like '" + ds + "/%') \n"
					+")a  \n"
					+"left join \n" 
					+"( \n"
					+"	select b.subjectfullname,(DebitTotalOcc1+DebitTotalOcc2-CreditTotalOcc1-CreditTotalOcc2) as occurvalue \n"
					+"	from z_accountrectify a   \n"
					+"	inner join    \n"
					+"	c_accpkgsubject b \n"  
					+"	on a.subjectid=b.subjectid \n"  
					+"	where a.projectid="+projectid+" \n"
					+"	  and b.accpackageid  = "+accpackageid+" \n"
					+"	and   b.subjectid in ('"+myds+"') \n" 
					+")b \n"
					+"on a.subjectfullname=b.subjectfullname \n"
					+"order by subjectname";
				
			}else{
				if (bHasEntry){		//有凭证
					sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname, (if(b.occ1 is null,0,b.occ1)-if(c.occ2 is null,0,c.occ2))* ("+ptype+") as occ \n"
					+"from \n"
					+"( \n"
					+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
					+"	from c_account  \n"
					+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
					+"	and isleaf1=1 \n"
					+"	and (subjectfullname2 = '" + ds + "' or subjectfullname2 like '" + ds + "/%') \n"
					+")a  \n"
					+"left join \n" 
					+"( \n"
					+"	select subjectfullname1 ,sum(DebitOcc)-sum(CreditOcc) as occ1 \n"
					+"	from c_account  \n"
					+"	where 1=1 \n "
					+"	and SubYearMonth*12+SubMonth>="+strBegin+" \n"
					+"	and SubYearMonth*12+SubMonth<="+strEnd+" \n"
					+"	and isleaf1=1 \n"
					+"	and subjectid in ('"+myds+"') \n"
					+"	group by subjectfullname1"
					+")b \n"
					+"on a.subjectfullname=b.subjectfullname1 \n"
					+"left join \n"
					+"( \n"
					+"	select a.subjectfullname1,sum(a.occurvalue* a.dirction) as occ2 \n"
					+"	from ("
					+"		select distinct a.* "	
					+"		from c_subjectentry a  \n"
					+"		where 1=1 and a.property like '%2%' \n "
					+"		and a.vchdate >= '"+begindate+"' \n"
					+"		and a.vchdate <= '"+enddate+"' \n"
					+"		and a.subjectid in ('"+myds+"') \n"
					+"	)a"
					+"	group by a.subjectfullname1 \n"
					+")c \n"
					+"on a.subjectfullname=c.subjectfullname1 \n"
					+"order by subjectname";
				}else{				//无凭证
					sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname, ifnull(case occ1 when 0 then debittotalocc else occ1 * direction2 end,0) as occ \n"
						+"from \n"
						+"( \n"
						+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
						+"	from c_account  \n"
						+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
						+"	and isleaf1=1 \n"
						+"	and (subjectfullname2 = '" + ds + "' or subjectfullname2 like '" + ds + "/%') \n"
						+")a  \n"
						+"left join \n" 
						+"( \n"
						+"	select subjectfullname1 ,debittotalocc-credittotalocc as occ1,debittotalocc,direction2 \n"
						+"	from c_account  \n"
						+"	where 1=1 \n"
						+"	and SubYearMonth*12+SubMonth>="+strBegin+" \n"
						+"	and SubYearMonth*12+SubMonth<="+strEnd+" \n"
						+"	and submonth="+endmonth+" \n"
						+"	and isleaf1=1 \n"
						+"	and subjectid in ('"+myds+"') \n"
						+")b \n"
						+"on a.subjectfullname=b.subjectfullname1 \n"
						+"order by subjectname";
				}
			}
				
			System.out.println("新SQL:"+sql);
			rs=st.executeQuery(sql);
			
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
	}
	
	/**
	 * 原来的
	 * @param session
	 * @param request
	 * @param response
	 * @param conn
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public ResultSet process1(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		ASFuntion asf=new ASFuntion();
		
		String accpackageid=(String)args.get("curAccPackageID");
		String projectid=(String)args.get("curProjectid");
		
		//分割符
		String s=(String)args.get("s");
		//第一个科目（下面称借方科目）
		String ds=(String)args.get("ds");
		String myds="";
		
		//第二个科目（下面称贷方科目）
		String cs=(String)args.get("cs");
		cs=asf.replaceStr(cs, s, "','");

		String mycs="";
		
		//余额方向 1 
		String dt=(String)args.get("dt"),ptype="";
		if (dt!=null && dt.equals("-1")){
			ptype="-1";
		}else{
			ptype="1";
		}
		
		//取列时的月份
		String month=(String)args.get("month");
		if (month==null){
			month="";
		}
		
		//年份划定符,-1表示取去年和今年，1表示今年和下年
		String year=(String)args.get("year");
		
		//统计年份，1表示当前年，-1表示另1参考年
		String thisyear=(String)args.get("thisyear");
		
		//另一个年份的账套ID
		String anotherApkID="";
		
		//统计的账套编号
		String nowAccpackageid="";
		
		
		/**
		 * accpackageid : 当前项目的帐套编号
		 * anotherApkID ：比较年份的帐套编号，可以上一年，也可以下一年
		 * nowAccpackageid ：当前刷新的帐套编号，是前面2个帐套编号中的某一个；
		 */
		
		
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
//			取项目起止日期，在本公式中有作用的只是结束月
            String endmonth = (String) args.get("endmonth");
            String[] result=getProjectAuditAreaStringByProjectid(conn,projectid);
            if (endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                endmonth=String.valueOf(result[3]);
            }
            int strBegin = Integer.parseInt(result[0]) * 12 + Integer.parseInt(result[1]);
            int strEnd = Integer.parseInt(result[2]) * 12 + Integer.parseInt(result[3]);
            
            String begindate=result[0]+"-"+result[1]+"-01";
            String enddate=result[2]+"-"+endmonth+"-31";
            
			anotherApkID=String.valueOf((Integer.parseInt(accpackageid)+Integer.parseInt(year)));
			
			if("1".equals(thisyear)){
				nowAccpackageid=accpackageid;
			}else{
				nowAccpackageid=anotherApkID;
				
//				屈总说上年数是开始、结束年都减去1
				strBegin = (Integer.parseInt(result[0]) - 1) * 12 + Integer.parseInt(result[1]);
	            strEnd = (Integer.parseInt(result[2]) - 1) * 12 + Integer.parseInt(endmonth);
	            begindate=String.valueOf(Integer.parseInt(result[0]) - 1)+"-"+result[1]+"-01";
	            enddate=String.valueOf(Integer.parseInt(result[2]) - 1)+"-"+endmonth+"-31";
				
			}
			st=conn.createStatement();

			
			
			//找出参数科目在客户中的底层科目的科目编号
			sql=" select  \n" 
			+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
			+" from ( select distinct subjectid from  c_account \n" 
			+" where isleaf1=1 \n" 
//			+ "	and submonth=1 \n"
//			+ " and accpackageid =" + nowAccpackageid + " \n"
			+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
			+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
			
			+" and subjectfullname2 like '"+ds+"%' \n"
			+" ) a";
			rs=st.executeQuery(sql);
			if(rs.next()){
				myds=rs.getString(1);
			}else{
				myds="''";
			}
			
			//找出冲销参数科目在客户中的底层科目的科目编号
			sql=" select  \n" 
				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+" from ( select distinct subjectid from  c_account \n" 
				+" where isleaf1=1 \n" 
//				+" and submonth=1 \n" 
//				+" and accpackageid ="+nowAccpackageid+" \n" 
				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
				
				+" and substr(subjectfullname2,1, \n" 
				+" 			if(locate('/',subjectfullname2) = 0, \n" 
				+" 					length(subjectfullname2), \n" 
				+" 					locate('/',subjectfullname2) -1 ) \n" 
				+"             ) in ('"+asf.replaceStr(cs, s, "','")+"')  \n" 
				+" ) a";
			rs=st.executeQuery(sql);
			if(rs.next()){
				mycs=rs.getString(1);
			}else{
				mycs="''";
			}
			
			String string = "";
			if(isFunctionType(conn,nowAccpackageid)){
				string = " and a.occurvalue * a.dirction * b.occurvalue * b.dirction <=0 ";
			}
			
			boolean bHasEntry=false;
			sql="select 1 from c_subjectentry where accpackageid="+nowAccpackageid + " limit 1" ;
			rs=st.executeQuery(sql);
			if(rs.next()){
				bHasEntry=true;
			}
			
			
			//最后的组装
			if (month.equals("xxx")){
				//取调整数
				sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname,if(b.occurvalue is null,0,b.occurvalue)* ("+ptype+") as occ \n"
					+"from \n"
					+"( \n"
					+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
					+"	from c_account  \n"
					+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
					+"	and isleaf1=1 \n"
					+"	and subjectfullname2 like '"+ds+"%' \n"
					+")a  \n"
					+"left join \n" 
					+"( \n"
					+"	select b.subjectfullname,(DebitTotalOcc1+DebitTotalOcc2-CreditTotalOcc1-CreditTotalOcc2) as occurvalue \n"
					+"	from z_accountrectify a   \n"
					+"	inner join    \n"
					+"	c_accpkgsubject b \n"  
					+"	on a.subjectid=b.subjectid \n"  
					+"	where a.projectid="+projectid+" \n"
					+"	  and b.accpackageid  = "+accpackageid+" \n"
					+"	and   b.subjectid in ('"+myds+"') \n" 
					+")b \n"
					+"on a.subjectfullname=b.subjectfullname \n"
					+"order by subjectname";
			}else{
				
				if (bHasEntry){
				//取余额表发生数（本年的或者上年/下年的）
				sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname, (if(b.occ1 is null,0,b.occ1)-if(c.occ2 is null,0,c.occ2))* ("+ptype+") as occ \n"
					+"from \n"
					+"( \n"
					+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
					+"	from c_account  \n"
					+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
					+"	and isleaf1=1 \n"
					+"	and subjectfullname2 like '"+ds+"%' \n"
					+")a  \n"
					+"left join \n" 
					+"( \n"
					+"	select subjectfullname1 ,sum(DebitOcc)-sum(CreditOcc) as occ1 \n"
					+"	from c_account  \n"
					+"	where 1=1 \n "
//					+"	and accpackageid="+nowAccpackageid+" \n"
//					+"	and submonth="+endmonth+" \n"
					+"	and SubYearMonth*12+SubMonth>="+strBegin+" \n"
					+"	and SubYearMonth*12+SubMonth<="+strEnd+" \n"
					+"	and isleaf1=1 \n"
					+"	and subjectid in ('"+myds+"') \n"
					+"	group by subjectfullname1"
					+")b \n"
					+"on a.subjectfullname=b.subjectfullname1 \n"
					+"left join \n"
					+"( \n"
					+"	select a.subjectfullname1,sum(a.occurvalue* a.dirction) as occ2 \n"
					+"	from ("
					+"		select distinct a.* "	
					+"		from c_subjectentry a, c_subjectentry b \n"
					+"		where 1=1 \n "
//					+"		and a.accpackageid="+nowAccpackageid+" \n"
					
					+"		and a.vchdate >= '"+begindate+"' \n"
					+"		and a.vchdate <= '"+enddate+"' \n"
					
					+"		and a.subjectid in ('"+myds+"') \n"
					
//					+"		and b.accpackageid="+nowAccpackageid+" \n"
					+"		and b.vchdate >='"+begindate+"' \n"
					+"		and b.vchdate <='"+enddate+"' \n"
					
					+"		and b.subjectid in ('"+mycs+"') \n"
					+"		and a.voucherid=b.voucherid \n"
					+ string 
					+"	)a"
					+"	group by a.subjectfullname1 \n"
					+")c \n"
					+"on a.subjectfullname=c.subjectfullname1 \n"
					+"order by subjectname";
				}else{
//					没有记录的情况下，先考虑借发生－贷发生，为0的话就取同方向的发生
					
					sql="select if(a.level1>=2,replace(a.subjectfullname,substr(a.subjectfullname,1,if(locate('/',a.subjectfullname) = 0,length(a.subjectfullname),locate('/',a.subjectfullname))),''),a.subjectfullname) as subjectname,a.subjectfullname, case occ1 when 0 then debittotalocc else occ1 * direction2 end as occ \n"
					+"from \n"
					+"( \n"
					+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname,level1 \n"
					+"	from c_account  \n"
					+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
					+"	and isleaf1=1 \n"
					+"	and subjectfullname2 like '"+ds+"%' \n"
					+")a  \n"
					+"left join \n" 
					+"( \n"
					+"	select subjectfullname1 ,debittotalocc-credittotalocc as occ1,debittotalocc,direction2 \n"
					+"	from c_account  \n"
					+"	where accpackageid="+nowAccpackageid+" \n"
					+"	and submonth="+endmonth+" \n"
					+"	and isleaf1=1 \n"
					+"	and subjectid in ('"+myds+"') \n"
					+")b \n"
					+"on a.subjectfullname=b.subjectfullname1 \n"
					+"order by subjectname";
				}
			}
			System.out.println("qwh:sql3="+sql);
			rs=st.executeQuery(sql);
				
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
