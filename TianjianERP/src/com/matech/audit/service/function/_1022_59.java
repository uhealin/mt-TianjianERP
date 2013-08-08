package com.matech.audit.service.function;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _1022_59 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
//		&subjectname=应收帐款&starmonth=1&endmonth=1&ptype=1
		
		
		String apkID=(String)args.get("curAccPackageID");
		String prjID=(String)args.get("curProjectid");
		
		String subjectname=(String)args.get("subjectname");
		String starmonth=(String)args.get("starmonth");
		String endmonth=(String)args.get("endmonth");
		String ptype=(String)args.get("ptype");
		

		
		String resultSql="";
		
		
		Statement st=null;
		ResultSet rs=null;
		try{
			st=conn.createStatement();

//			查找该科目在客户中的科目id
			String sql="";
			String subjectid=this.getClientIDByStandName(conn, apkID, prjID, subjectname);

			
			
//			判断该科目是否叶子并且有自增科目。
			sql=""
			+" select * from  \n" 
			+" c_account a \n" 
			+" inner join \n" 
			+" z_usesubject b \n" 
			+" on a.subjectid=b.tipsubjectid \n" 
			+" where a.accpackageid='"+apkID+"' \n" 
			+"   and a.subjectfullname2='"+subjectname+"' \n" 
			+"   and a.submonth=1 \n" 
			+"   and a.isleaf1=1 \n" 
			+"   and b.accpackageid='"+apkID+"' \n" 
			+"   and b.projectid='"+prjID+"' \n" ;
			rs=st.executeQuery(sql);
			
			if(rs.next()){
				resultSql=getSql("0",subjectid);
			}else{
				resultSql=getSql("1",subjectid);
			}
			
			//最终查询结果
			resultSql=this.setSqlArguments(resultSql, args);
			org.util.Debug.prtErr(resultSql);
			rs=st.executeQuery(resultSql);
			
			
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
			
		}

	}
	
	public String getSql(String rectifySign,String subjectid){
		return ""
		+" select   \n" 
		+"        IF(b2.assitemid is null,a2.bo,b2.bo) as bo,  \n"  
		+"          \n" 
		+"        IF(b2.assitemid is null,1,2) as orderid       \n" 
		+"   \n" 
		+"   \n" 
		+" FROM  \n" 
		+" (   /* a2是余额表的数据 */  \n" 
		+"     select   \n" 
		+"            a.*,b.accid, \n" 
		+"             \n" 
		+"            ifnull(e.bo,0) as bo \n" 
		+"              \n" 
		+"     from   \n" 
		+"     (  \n" 
		+"       select   \n" 
		+"              subjectid,accname,subjectfullname2,balance * (${ptype}) as balance,  \n" 
		+"              (debitremain+creditremain) * (${ptype}) /**/ as initbalance,  \n" 
		+"              debitocc,creditocc,debittotalocc,credittotalocc,"+rectifySign+" as rectifySign \n" 
		+"       from c_account   \n" 
		+"       where submonth=${starmonth}  \n" 
		+"       and isleaf1=1  \n" 
		+"       and accpackageid=${curPackageid} /**/  \n" 
		+"       and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')   \n" 
		+"        \n" 
		+"       union \n" 
		+"        \n" 
		+"       select \n" 
		+"            subjectid, \n" 
		+"            subjectname as accname, \n" 
		+"            subjectfullname as subjectfullname2, \n" 
		+"            0 as balance, \n" 
		+"            0 as initbalance,  \n" 
		+"            0 as  debitocc, \n" 
		+"            0 as  creditocc, \n" 
		+"            0 as debittotalocc, \n" 
		+"            0 as credittotalocc,      \n" 
		+"            1 as rectifySign      \n" 
		+"      from z_usesubject \n" 
		+"      where projectid=${curProjectid}  \n" 
		+"      and accpackageid=${curPackageid} \n" 
		+"      and tipsubjectid='"+subjectid+"' \n" 
		+"      and isleaf=1 \n" 
		+"     ) a  \n" 
		+"       \n" 

		+"       \n" 
		+"     left join  \n" 
		+"     ( /*判断取辅助核算还是科目，就看这个表了。条件是${starmonth}月份的余额是否相等*/  \n" 
		+"      \n" 
		+"       select  \n" 
		+"             distinct accid  \n" 
		+"       from c_assitementryacc  \n" 
		+"       where accpackageid=${curPackageid}  \n" 
		+"       and submonth=${starmonth} \n" 
		+"       and isleaf1=1  \n" 
		+"       and  \n" 
		+"       ( \n" 
		+"           asstotalname1 like '%客户%' \n" 
		+"           or asstotalname1 like '%供应商%' \n" 
		+"           or asstotalname1 like '%往来%' \n" 
		+"       ) \n" 
		+"     ) b  \n" 
		+"     on a.subjectid=b.accid  \n" 
		+"  \n" 
		+"     /* 调整 */  \n" 
		+"     left join  \n" 
		+"     (  \n" 
		+"   select distinct a.subjectid,  \n" 
		+"          case when IFNULL(a.balance-a.occ,-1)=-1 then 0 when a.balance-a.occ <=0 THEN 0 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ,-1)=-1 then 1 when a.balance-a.occ-b.occ<=0 then 1 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ,-1)=-1 then 2 when a.balance-a.occ-b.occ-c.occ<=0 then 2 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ<=0,-1)=-1 then 3 when a.balance-a.occ-b.occ-c.occ-d.occ<=0 then 3 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then 4 when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ<=0 then 4 else 5  \n" 
		+"          end end END end end bo  \n" 
		+"          from (select subjectid,accname,direction*balance balance,subjectfullname1,if(direction=1,debittotalocc,credittotalocc) occ from c_account where accpackageid = '${curPackageid}' and submonth>=${starmonth} and submonth<=${endmonth} and CONCAT(balance) >'' and isleaf1=1 ) a  \n" 
		+"          left join (select subjectid,accname,subjectfullname1,if(direction=1,debittotalocc,credittotalocc) occ from c_account where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-1) and submonth>=${starmonth} and submonth<=${endmonth}) b on a.subjectfullname1=b.subjectfullname1  \n" 
		+"          left join (select subjectid,accname,subjectfullname1,if(direction=1,debittotalocc,credittotalocc) occ from c_account where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-2) and submonth>=${starmonth} and submonth<=${endmonth}) c on a.subjectfullname1=c.subjectfullname1  \n" 
		+"          left join (select subjectid,accname,subjectfullname1,if(direction=1,debittotalocc,credittotalocc) occ from c_account where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-3) and submonth>=${starmonth} and submonth<=${endmonth}) d on a.subjectfullname1=d.subjectfullname1  \n" 
		+"          left join (select subjectid,accname,subjectfullname1,if(direction=1,debittotalocc,credittotalocc) occ from c_account where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-4) and submonth>=${starmonth} and submonth<=${endmonth}) e on a.subjectfullname1=e.subjectfullname1  \n" 
		+"        \n" 

		+"     ) e   \n" 
		+"     on a.subjectid =e.subjectid  \n" 
		+"       \n" 
		+"       \n" 
		+" ) a2  \n" 
		+" left join  \n" 
		+" (  \n" 
		+"     /* b2 是辅助核算 */  \n" 
		+"     SELECT   \n" 
		+"            a.*,  \n" 
		+"              \n" 
		
		+"             \n" 
		+"            ifnull(c.bo,0) as bo \n" 
		+"     FROM  \n" 
		+"     (  \n" 
		+"         select   \n" 
		+"                accid,assitemid,assitemname,  \n" 
		+"                balance  * (${ptype}) as balance,  \n" 
		+"                (debitremain+creditremain) * (${ptype}) /**/ as initbalance,  \n" 
		+"                debitocc,creditocc,debittotalocc,credittotalocc  \n" 
		+"         from c_assitementryacc  \n" 
		+"         where submonth=${starmonth}  \n" 
		+"         and isleaf1=1  \n" 
		+"         and accpackageid=${curPackageid} /**/  \n" 
		+"         and  \n" 
		+"         ( \n" 
		+"           asstotalname1 like '%客户%' \n" 
		+"           or asstotalname1 like '%供应商%' \n" 
		+"           or asstotalname1 like '%往来%' \n" 
		+"         ) \n" 
		+"          \n" 
		+"     ) a   \n" 
		+"     /* 调整 */  \n" 
		+"     left join   \n" 
		+"     (     \n" 
		+"          select distinct a.accid,a.assitemid,  \n" 
		+"          case when IFNULL(a.balance-a.occ,-1)=-1 then 0 when a.balance-a.occ <=0 THEN 0 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ,-1)=-1 then 1 when a.balance-a.occ-b.occ<=0 then 1 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ,-1)=-1 then 2 when a.balance-a.occ-b.occ-c.occ<=0 then 2 else  \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ,-1)=-1 then 3 when a.balance-a.occ-b.occ-c.occ-d.occ<=0 then 3 else   \n" 
		+"          CASE when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then 4 when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ<=0 then 4 else 5  \n" 
		+"          end end end end end bo  \n" 
		+"          from (select accid,assitemid,direction*balance balance,if(direction=1,debittotalocc,credittotalocc) occ from c_assitementryacc  where accpackageid = '${curPackageid}' and submonth>=${starmonth} and submonth<=${endmonth} and CONCAT(balance) > '' and isleaf1=1) a left join (select accid,assitemid,if(direction=1,debittotalocc,credittotalocc) occ from c_assitementryacc  where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-1) and submonth>=${starmonth} and submonth<=${endmonth}) b on a.accid=b.accid and a.assitemid=b.assitemid  \n" 
		+"          left join (select accid,assitemid,if(direction=1,debittotalocc,credittotalocc) occ from c_assitementryacc  where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-2) and submonth>=${starmonth} and submonth<=${endmonth}) c on a.accid=c.accid and a.assitemid=c.assitemid  \n" 
		+"          left join (select accid,assitemid,if(direction=1,debittotalocc,credittotalocc) occ from c_assitementryacc  where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-3) and submonth>=${starmonth} and submonth<=${endmonth}) d on a.accid=d.accid and a.assitemid=d.assitemid  \n" 
		+"          left join (select accid,assitemid,if(direction=1,debittotalocc,credittotalocc) occ from c_assitementryacc  where accpackageid = CONCAT(SUBSTRING('${curPackageid}',1,6),SUBSTRING('${curPackageid}',7)-4) and submonth>=${starmonth} and submonth<=${endmonth}) e on a.accid=e.accid and a.assitemid=e.assitemid  \n" 

		+"     ) c   \n" 
		+"     on a.accid =c.accid and a.assitemid=c.assitemid  \n" 
		+" ) b2  \n" 
		+" on a2.accid=b2.accid  \n" 
		+" order by orderid,a2.subjectid,b2.assitemid \n" 
		+"";
	}
}

