package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _1036_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
//		&subjectname=应收帐款&starmonth=1&endmonth=1&ptype=1
		
		ASFuntion asf=new ASFuntion();
		
		String apkID=(String)args.get("curAccPackageID");
		String prjID=(String)args.get("curProjectid");
		
		//分割符
		String s=(String)args.get("s");
		//第一个科目（下面称借方科目）
		String ds=(String)args.get("ds");
		//第一个科目的下级科目
		String dss=(String)args.get("dss");
		//第二个科目（下面称贷方科目）
		String cs=(String)args.get("cs");
		//统计科目 1 为第一个，2为第二个
		String dt=(String)args.get("dt");
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
			st=conn.createStatement();

//　　　　　找出参数科目在客户中的名字
			
			//借方科目
			sql=""
				+" select  \n" 
				+" 	group_concat(accname SEPARATOR '\\',\\'')  \n" 
				
				+"  \n"
				+" from ( select accname from  c_account \n" 
				+" where level1=1 \n" 
				+" and submonth=1 \n" 
				+" and accpackageid="+apkID+" \n" 
				+" and subjectfullname2 in ('"+asf.replaceStr(ds, s, "','")+"') \n"
				
				+" union  \n"
				
				+"  	select substr(subjectfullname,1, \n" 
					+" 			if(locate('/',subjectfullname) = 0, \n" 
					+" 					length(subjectfullname), \n" 
					+" 					locate('/',subjectfullname) -1 ) \n" 
					+"             ) as accname \n"
				+"  	from   \n" 
				+"  	z_subjectentryrectify a  \n" 
				+"  	inner join   \n" 
				+"  	c_accpkgsubject b  \n" 
				+"  	on a.subjectid=b.subjectid  \n" 
				+"  	where a.accpackageid="+apkID+"  \n" 
				+"  	  and a.projectid="+prjID+"  \n" 
				+"  	  and b.accpackageid="+apkID+"  \n" 
				+"  	  and a.property in(311,411)  \n" 
				+" and   substr(subjectfullname,1, \n" 
				+" 			if(locate('/',subjectfullname) = 0, \n" 
				+" 					length(subjectfullname), \n" 
				+" 					locate('/',subjectfullname) -1 ) \n" 
				+"             ) in ('"+asf.replaceStr(ds, s, "','")+"') \n" 
				
				
				+" ) a";
				

				
				rs=st.executeQuery(sql);
				if(rs.next()){
					ds=rs.getString(1);
				}else{
					ds="''";
				}
				
				//贷方科目
				
				sql=""
					+" select  \n" 
					+" 	group_concat(accname SEPARATOR '\\',\\'')  \n" 
					+" from ( \n"
					+" select accname from  c_account \n" 
					+" where level1=1 \n" 
					+" and submonth=1 \n" 
					+" and accpackageid="+apkID+" \n" 
					+" and subjectfullname2 in ('"+asf.replaceStr(cs, s, "','")+"') \n"
					
					+" union  \n"
					
					+"  	select substr(subjectfullname,1, \n" 
					+" 			if(locate('/',subjectfullname) = 0, \n" 
					+" 					length(subjectfullname), \n" 
					+" 					locate('/',subjectfullname) -1 ) \n" 
					+"             ) as accname \n"
					+"  	from   \n" 
					+"  	z_subjectentryrectify a  \n" 
					+"  	inner join   \n" 
					+"  	c_accpkgsubject b  \n" 
					+"  	on a.subjectid=b.subjectid  \n" 
					+"  	where a.accpackageid="+apkID+"  \n" 
					+"  	  and a.projectid="+prjID+"  \n" 
					+"  	  and b.accpackageid="+apkID+"  \n" 
					+"  	  and a.property in(311,411)  \n" 
					+" and   substr(subjectfullname,1, \n" 
					+" 			if(locate('/',subjectfullname) = 0, \n" 
					+" 					length(subjectfullname), \n" 
					+" 					locate('/',subjectfullname) -1 ) \n" 
					+"             ) in ('"+asf.replaceStr(cs, s, "','")+"') \n" 

					
					
					+" ) a";
				
				rs=st.executeQuery(sql);
				if(rs.next()){
					cs=rs.getString(1);
				}else{
					cs="''";
				}
			
//			查找该科目在客户中的借方科目的每个月最后一天的凭证
				sql=""
					+" select group_concat(distinct voucherid ) as voucherid, \n"
					+"        group_concat(distinct autoid ) as autoid \n"
					+" from  \n" 
					+" c_subjectentry a \n" 
//					+" inner join \n" 
//					+" ( \n" 
//					+" -- 每个月的最后一天 \n" 
//					+" 	select max(vchdate) as lastDay from c_subjectentry \n" 
//					+"  where accpackageid="+apkID+" \n" 
//					+" 	group by substr(vchdate,1,7) \n" 
//					+" ) b \n" 
					+" -- substr　是取该科目的一级科目名称 \n" 
					+" where accpackageid="+apkID+" \n" 
					+" and   substr(subjectfullname1,1, \n" 
					+" 			if(locate('/',subjectfullname1) = 0, \n" 
					+" 					length(subjectfullname1), \n" 
					+" 					locate('/',subjectfullname1) -1 ) \n" 
					+"             ) in ('"+cs+"') \n" 
//					+" and a.vchDate=b.lastDay \n" 
					;
				rs=st.executeQuery(sql);
				
				//借方科目的最后一天凭证编号
				String cvid="";
				//借方科目的最后一天分录编号，到最后是结果的分录号。
				String creditResult="";
				if(rs.next()){
					cvid=rs.getString("voucherid");
					creditResult=rs.getString("autoid");
				}else{
					cvid="null";
					creditResult="null";
				}
				
				sql=""
				+" select \n"
				+"      group_concat(distinct voucherid ) as voucherid,  \n"
				+"      group_concat(distinct autoid ) as autoid \n"
				+" from  \n" 
				+" c_subjectentry a \n" 
				+" -- substr　是取该科目的一级科目名称 \n" 
				+" where accpackageid="+apkID+" \n" 
				+" and a.voucherid in("+cvid+") \n"
				+" and a.autoid not in("+creditResult+") \n"
				+" and   substr(subjectfullname1,1, \n" 
				+" 			if(locate('/',subjectfullname1) = 0, \n" 
				+" 					length(subjectfullname1), \n" 
				+" 					locate('/',subjectfullname1) -1 ) \n" 
				+"             ) in ('"+ds+"') \n" 
				;
				
				rs=st.executeQuery(sql);
				//每个月的最后一天，包含借方科目ds并且包含贷方科目cs的凭证号。
				String resultvid="";
				//最后贷方的所有分录id
				String debitResult="";
				if(rs.next()){
					resultvid=rs.getString("voucherid");
					debitResult=rs.getString("autoid");
				}else{
					resultvid="null";
					debitResult="null";
				}
				
				sql=" select group_concat(distinct autoid ) from c_subjectentry \n"
				   +" where accpackageid="+apkID+"  \n"
				   +" and voucherid in ("+resultvid+") \n"
				   +" and autoid in ("+debitResult+") \n";
				
				rs=st.executeQuery(sql);
				//最后结果需要的借方分录编号
				if(rs.next()){
					creditResult=rs.getString(1);
				}else{
					creditResult="null";
				}
			
			
//============调整处理
			//借方调整凭证和分录号
			sql=""
				+"  select group_concat(distinct a.voucherid ) as voucherid, \n"
				+"         group_concat(distinct a.autoid ) as autoid \n"
				+"  from   \n" 
				+"  z_subjectentryrectify a  \n" 
				+"  inner join   \n" 
				+"  c_accpkgsubject b  \n" 
				+"  on a.subjectid=b.subjectid  \n" 
				+"  where a.accpackageid="+apkID+"  \n" 
				+"    and a.projectid="+prjID+"  \n" 
				+"    and b.accpackageid="+apkID+"  \n" 
				+"    and a.property in(311,411)  \n" 
				+"    and   substr(b.subjectfullname,1, \n" 
				+" 			  if(locate('/',b.subjectfullname) = 0, \n" 
				+" 					length(b.subjectfullname), \n" 
				+" 					locate('/',b.subjectfullname) -1 ) \n" 
				+"             ) in ('"+ds+"') \n" ;
			rs=st.executeQuery(sql);
			String debitRectifyVoucher="",debitRectifyEntry="";
			if(rs.next()){
				debitRectifyVoucher=rs.getString("voucherid");
				debitRectifyEntry=rs.getString("autoid");
			}else{
				debitRectifyVoucher="null";
				debitRectifyEntry="null";
			}
			
			
			//贷方和借方调整凭证和分录号
			sql=""
				+"  select group_concat(distinct a.voucherid ) as voucherid, \n"
				+"         group_concat(distinct a.autoid ) as autoid \n"
				+"  from   \n" 
				+"  z_subjectentryrectify a  \n" 
				+"  inner join   \n" 
				+"  c_accpkgsubject b  \n" 
				+"  on a.subjectid=b.subjectid  \n" 
				+"  where a.accpackageid="+apkID+"  \n" 
				+"    and a.projectid="+prjID+"  \n" 
				+"    and b.accpackageid="+apkID+"  \n" 
				+"    and a.property in(311,411)  \n" 
				+"    and a.voucherid in ("+debitRectifyVoucher+")  \n"
				+"    and a.autoid not in("+debitRectifyEntry+") \n"
				+"    and   substr(b.subjectfullname,1, \n" 
				+" 			  if(locate('/',b.subjectfullname) = 0, \n" 
				+" 					length(b.subjectfullname), \n" 
				+" 					locate('/',b.subjectfullname) -1 ) \n" 
				+"             ) in ('"+cs+"') \n" ;
			rs=st.executeQuery(sql);
			String creditRectifyVoucher="",creditRectifyEntry="";
			if(rs.next()){
				creditRectifyVoucher=rs.getString("voucherid");
				creditRectifyEntry=rs.getString("autoid");
			}else{
				creditRectifyVoucher="null";
				creditRectifyEntry="null";
			}
			
			//借方最后的分录号
			
			sql=""
				+"  select  \n"
				+"         group_concat(distinct a.autoid ) as autoid \n"
				+"  from   \n" 
				+"  z_subjectentryrectify a  \n" 
				+"  where a.accpackageid="+apkID+"  \n" 
				+"    and a.projectid="+prjID+"  \n" 
				+"    and a.property in(311,411)  \n" 
				+"    and a.voucherid in ("+creditRectifyVoucher+")\n"
				+"    and a.autoid in ("+debitRectifyEntry+")\n"
				+"  \n"
				+"  \n";
			
			rs=st.executeQuery(sql);
			if(rs.next()){
				debitRectifyVoucher=creditRectifyVoucher;
				debitRectifyEntry=rs.getString("autoid");
			}else{
				debitRectifyVoucher="null";
				debitRectifyEntry="null";
			}
			
//=========最后用到的sql
			
//			debitResult是统计科目，永远都是统计[统计科目]
			String resultAutoID="",resultRectify="";
//			if("-1".equals(dt)){
//				resultAutoID=creditResult;
//				resultRectify=creditRectifyEntry;
//			}else
			{
				resultAutoID=debitResult;
				resultRectify=debitRectifyEntry;
			}
			
			sql=""
				+" 	select \n" 
				+" 		sum(a.occurvalue) * ("+dt+") as occ \n" 
				+"  from (\n"
				
				+"  	select subjectfullname1,occurvalue * dirction as occurvalue,vchdate \n"
				+" 		from c_subjectentry \n" 
				+" 		where accpackageid = "+apkID+" \n"
				+" 		and autoid in("+resultAutoID+") \n"
				
				+"  	union all -- 小莫要求增加调整后的数据 \n"
				
				+"  	select b.subjectfullname as subjectfullname1,a.occurvalue * dirction as occurvalue,vchdate \n"
				+"  	from   \n" 
				+"  	z_subjectentryrectify a  \n" 
				+"  	inner join   \n" 
				+"  	c_accpkgsubject b  \n" 
				+"  	on a.subjectid=b.subjectid  \n" 
				+"  	where a.accpackageid="+apkID+"  \n" 
				+"  	  and a.projectid="+prjID+"  \n" 
				+"  	  and b.accpackageid="+apkID+"  \n" 
				+"  	  and a.property in(311,411)  \n" 
				+"  	  and a.autoid in("+resultRectify+") \n"
				
				+"  ) a inner join \n"
				+"  (    \n"
				+"            select '"+dss+"' as gs  \n" 
				+"            union  \n" 
				+"            select TRIM(replace(replace(replace(replace(CONCAT('"+dss+"','         '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2),'`',''))  \n" 
				+"            from k_key b,k_key c,k_key d  \n" 
				+"            where '"+dss+"' like concat('%',b.key1,'%')  \n" 
				+"            and '"+dss+"' like concat('%',c.key1,'%')  \n" 
				+"            and '"+dss+"' like concat('%',d.key1,'%')  \n" 

				+"  ) b on a.subjectfullname1 like concat('"+ds+"/%',b.gs,'%')   \n"
	
				 ;
			rs=st.executeQuery(sql);
			
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}
	
	
}

