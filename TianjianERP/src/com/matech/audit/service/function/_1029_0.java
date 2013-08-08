package com.matech.audit.service.function;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class _1029_0 extends AbstractAreaFunction {


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
			String subjectid=getClientIDByStandName(conn, apkID, prjID, subjectname);
			
			
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
		+"        IF(b2.assitemid is null,a2.subjectid,b2.assitemid) as subjectid,  \n" 
		+"        IF(b2.assitemid is null,a2.accname,concat(a2.accname,'/',b2.assitemname)) as subjectname,  \n" 
		+"          \n" 
		+"        IF(b2.assitemid is null,a2.initbalance + a2.initsov,b2.initbalance + b2.initsov) as initbalance,  \n" 
		+"        IF(b2.assitemid is null,a2.balance,b2.balance) as balance,  \n" 
		+"        IF(b2.assitemid is null,a2.balance+a2.sov3,b2.balance+b2.sov3) as sdbalance,  \n" 
		+"  \n" 
		+"        IF(b2.assitemid is null,a2.debitocc,b2.debitocc) as debitocc,  \n" 
		+"        IF(b2.assitemid is null,a2.creditocc,b2.creditocc) as creditocc,  \n" 
		+"        IF(b2.assitemid is null,a2.debittotalocc,b2.debittotalocc) as debittotalocc,  \n" 
		+"        IF(b2.assitemid is null,a2.credittotalocc,b2.credittotalocc) as credittotalocc,  \n" 
		+"          \n" 
		+"  \n" 
		+"                               \n" 
		+"        IF(b2.assitemid is null,a2.sov1,b2.sov1) * a2.rectifySign as sov1,  \n" 
		+"        IF(b2.assitemid is null,a2.sov2,b2.sov2) * a2.rectifySign as sov2,  \n" 
		+"        IF(b2.assitemid is null,a2.sov3,b2.sov3) * a2.rectifySign as sov3, \n" 
		+"        IF(b2.assitemid is null,a2.debitsov1,b2.debitsov1) * a2.rectifySign as debitsov1,  \n" 
		+"        IF(b2.assitemid is null,a2.creditsov1,b2.creditsov1) * a2.rectifySign as creditsov1,  \n" 
		+"        IF(b2.assitemid is null,a2.debitsov2,b2.debitsov2) * a2.rectifySign as debitsov2,  \n" 
		+"        IF(b2.assitemid is null,a2.creditsov2,b2.creditsov2) * a2.rectifySign as creditsov2, \n" 
		+"        IF(b2.assitemid is null,a2.debitsov3,b2.debitsov3) * a2.rectifySign as debitsov3,  \n" 
		+"        IF(b2.assitemid is null,a2.creditsov3,b2.creditsov3) * a2.rectifySign as creditsov3, \n" 
		+"         \n" 
		+"          \n" 
		+"        IF(b2.assitemid is null,1,2) as orderid       \n" 
		+"   \n" 
		+"   \n" 
		+" FROM  \n" 
		+" (   /* a2是余额表的数据 */  \n" 
		+"     select   \n" 
		+"            a.*,b.accid, \n" 
		+"              \n" 
		+"            ifnull(e.sov1,0) as sov1,  \n" 
		+"            ifnull(e.sov2,0) as sov2,  \n" 
		+"            ifnull(e.debitsov1,0) as debitsov1,  \n" 
		+"            ifnull(e.creditsov1,0) as creditsov1,  \n" 
		+"            ifnull(e.debitsov2,0) as debitsov2,  \n" 
		+"            ifnull(e.creditsov2,0) as creditsov2,  \n" 
		+"              \n" 
		+"            ifnull(e.sov1+e.sov2,0) as sov3,  \n" 
		+"            ifnull(e.debitsov1+e.debitsov2,0) as debitsov3,  \n" 
		+"            ifnull(e.creditsov1+e.creditsov2,0) as creditsov3, \n" 
		+"             \n" 
		+"            ifnull(e.initsov,0) as initsov \n" 
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
		+"     ( /*判断取辅助核算还是科目，就看这个表了。*/  \n" 
		+"      \n" 
		+"       select  \n" 
		+"             distinct accid  \n" 
		+"       from c_assitementryacc  \n" 
		+"       where accpackageid=${curPackageid}  \n" 
		+"       and submonth=${starmonth} \n" 
		+"       and isleaf1=1  \n"  
		+"     ) b  \n" 
		+"     on a.subjectid=b.accid  \n" 
		+"  \n" 
		+"     /* 调整 */  \n" 
		+"     left join  \n" 
		+"     (  \n" 
		+"         select   \n" 
		+"                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n" 
		+"                (debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n" 
		+"                debittotalocc1  * (${ptype})  as debitsov1,  \n" 
		+"                credittotalocc1  * (${ptype})  as creditsov1,  \n" 
		+"                debittotalocc2  * (${ptype})  as debitsov2,  \n" 
		+"                credittotalocc2   * (${ptype}) as creditsov2, \n" 
		+"                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n" 
		+"         from z_accountrectify  \n" 
		+"         where accpackageid = ${curPackageid} /**/  \n" 
		+"         and projectid = ${curProjectid}  \n" 
		+"         and isleaf=1 "
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
		+"            ifnull(c.sov1,0) as sov1,  \n" 
		+"            ifnull(c.sov2,0) as sov2,  \n" 
		+"            ifnull(c.debitsov1,0) as debitsov1,  \n" 
		+"            ifnull(c.creditsov1,0) as creditsov1,  \n" 
		+"            ifnull(c.debitsov2,0) as debitsov2,  \n" 
		+"            ifnull(c.creditsov2,0) as creditsov2,  \n" 
		+"             \n" 
		+"            ifnull(c.sov1+c.sov2,0) as sov3,  \n" 
		+"            ifnull(c.debitsov1+c.debitsov2,0) as debitsov3,  \n" 
		+"            ifnull(c.creditsov1+c.creditsov2,0) as creditsov3, \n" 
		+"             \n" 
		+"            ifnull(c.initsov,0) as initsov \n" 
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

		+"          \n" 
		+"     ) a   \n" 
		+"     /* 调整 */  \n" 
		+"     left join   \n" 
		+"     (     \n" 
		+"         select subjectid,assitemid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,(debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n" 
		+"         debittotalocc1 * (${ptype})  as debitsov1,  \n" 
		+"         credittotalocc1  * (${ptype})  as creditsov1,  \n" 
		+"         debittotalocc2   * (${ptype}) as debitsov2,  \n" 
		+"         credittotalocc2   * (${ptype}) as creditsov2, \n" 
		+"         (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n" 
		+"         from z_assitemaccrectify  \n" 
		+"         where accpackageid = ${curPackageid} /**/  \n" 
		+"         and projectid =  ${curProjectid}/**/  \n" 
		+"  \n" 
		+"     ) c   \n" 
		+"     on a.accid =c.subjectid and a.assitemid=c.assitemid  \n" 
		+" ) b2  \n" 
		+" on a2.accid=b2.accid  \n" 
		+" order by orderid,a2.subjectid,b2.assitemid \n" 
		+"";
	}
}