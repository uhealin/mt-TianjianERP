package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
*
* <p>Title: 取余额明细记录的函数，供批量刷新调用，相当于原来的1007和1029</p>
* <p>Description:
* 1、分析余额表以及辅助核算余额表，有后者就优先刷后者，否则刷前者；
* 2、刷新出的列内容包括：
* 	a1、1年以内
* 	a2、1-2年
* 	a3、2-3年
* 	a4、3-4年
* 	a5、4-5年
* 	a6、5年以上
* 3、支持自定义科目；
* 4、参数录入说明：
*    I、区间支持2个参数（起始月startmonth、结束月endmonth），若没有提供，就从项目的审计区间取；
*    II、对应科目参数：subjectname,如果科目参数为空，则从底稿的对应科目取名称；
*    III、科目余额方向参数：ptype,如果没有设置则取标准科目（或自增科目）的科目方向；
*    IV、辅助核算范围参数：allassitem，如果没有设置，则只扫描【客户、供应商、往来】，设置了，就扫描全部辅助核算；
* </p>
*
* <p>Copyright: Copyright (c) 2007 matech LTD.</p>
*
* <p>Company: matech </p>
*
* @author 铭太E审通团队,ESPIERALY THANKS WINNERQ AND PENGYONG
* @version 1.0
*/

public class _3015_0 extends AbstractAreaFunction {
	
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		String accpackageid = (String) args.get("curAccPackageID");
        String projectid = (String) args.get("curProjectid");
        String resultSql = "";

        Statement st = null;
        ResultSet rs = null;
        try {
        	 String subjectname = (String) args.get("subjectname");
             if (subjectname==null || subjectname.equals("")){
                 String manuid=(String)args.get("manuid");
                 if (manuid==null || manuid.equals("")){
                     subjectname=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                 }else{
                     //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                     subjectname = getTaskSubjectNameByManuID(conn, manuid);
                 }
//                 args.put("subjectname",subjectname);
             }
             
             
             String sName = changeSubjectName(conn,projectid,subjectname);
             if(!"".equals(sName)){
             	subjectname = sName; 
             }            
             args.put("subjectname",subjectname);
             
             
             String strStartYearMonth = "", strEndYearMonth = "";
             String startmonth = (String) args.get("startmonth");
             String endmonth = (String) args.get("endmonth");
             String strYears = "",strLastYear="";
             if (startmonth == null || startmonth.equals("")
                 || endmonth == null || endmonth.equals("")) {
                 //如果前台没有提供这个参数，就从项目取；
                 int[] result = getProjectAuditAreaByProjectid(conn, projectid);
                 strStartYearMonth = String.valueOf(result[0] * 12 + result[1]);
                 strEndYearMonth = String.valueOf(result[2] * 12 + result[3]);

                 if (result[0] == result[2]) {
                     strYears = " = " + result[0];
                 } else {
                     for (int i = result[0]; i <= result[2]; i++) {
                         strYears += "," + String.valueOf(i);
                     }
                     if (strYears.length() > 0) {
                         //去掉最开始得,
                         strYears = " in (" + strYears.substring(1) + ")";
                     }
                 }
                 endmonth = String.valueOf(result[3]);
             } else {
                 strStartYearMonth = String.valueOf(Integer.parseInt(
                         accpackageid.substring(6)) * 12 +
                         Integer.parseInt(startmonth));
                 strEndYearMonth = String.valueOf(Integer.parseInt(accpackageid.
                         substring(6)) * 12 + Integer.parseInt(endmonth));
                 strYears = " = " + accpackageid.substring(6);
             }
             
             strLastYear = " = " +accpackageid.substring(6);
             
             //关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
             String correlation=request.getParameter("correlation");
             if (correlation==null){
             	//没有提供
             	correlation=" ";
             }else{
             	//提供了
             	if ( correlation.equals("1")){
             		//只显示关联客户的；
             		correlation=" having exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+accpackageid.substring(0, 6)+" and connectcompanysname=corname\n        )   \n";
             	}else{
             		//显示非关联客户的
             		correlation=" having not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+accpackageid.substring(0, 6)+" and connectcompanysname=corname\n        )   \n";
             	}
             }
             args.put("correlation",correlation);
             
             args.put("endmonth", endmonth);
             
             args.put("StartYearMonth", strStartYearMonth);
             args.put("EndYearMonth", strEndYearMonth);
             args.put("Years", strYears);
             args.put("LastYear",strLastYear);
             
//           查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
             String[] result = this.getClientIDAndDirectionByStandName(conn,
                     accpackageid, projectid,
                     subjectname);
             String subjectid = result[0];

             //如果没有提供方向这个参数，则取科目余额方向
             String ptype = (String) args.get("ptype");
             if (ptype == null || ptype.equals("")) {
                 args.put("ptype", result[1]);
             }
             
//           判断该科目是否叶子并且有自增科目。
             st = conn.createStatement();
             resultSql = ""
                   + " select 1 from  \n"
                   + " c_account a \n"
                   + " inner join \n"
                   + " z_usesubject b \n"
                   + " on a.subjectid=b.tipsubjectid \n"
                   + " where a.accpackageid='" + accpackageid + "' \n"
                   + "   and a.subjectfullname2='" + subjectname + "' \n"
                   + "   and a.submonth=1 \n"
                   + "   and a.isleaf1=1 \n"
                   + "   and b.accpackageid='" + accpackageid + "' \n"
                   + "   and b.projectid='" + projectid + "' \n";
             rs = st.executeQuery(resultSql);

             if (rs.next()) {
                 resultSql = getSql("0", subjectid);
             } else {
                 resultSql = getSql("1", subjectid);
             }
             rs.close();
             
             String sqlassitem = "select distinct asstotalname from c_assitem where accpackageid='" + accpackageid + "' and Level0=1 " +
     			" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";
     
             rs = st.executeQuery(sqlassitem);
             String sqlstring = "";
             while(rs.next()){
            	 sqlstring += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
             }
             rs.close();
             
             if(!"".equals(sqlstring)){
            	 sqlstring = " and ( " + sqlstring.substring(0,sqlstring.length()-2)+ ") ";
             }else{
				sqlstring = " and 1=2 ";
             }
             //设置辅助核算扫描范围的参数
             String allassitem=request.getParameter("allassitem");
             if (allassitem==null || allassitem.equals("")){
                 //只扫描往来、客户、供应商
//                 args.put("allassitem","       and  \n       ( \n           asstotalname1 like '%客户%' \n           or asstotalname1 like '%供应商%' \n      or asstotalname1 like '%关联%' \n        or asstotalname1 like '%往来%' \n       ) \n");
                 args.put("allassitem",sqlstring);
             }else{
                 //扫描全部辅助核算
                 args.put("allassitem"," ");
             }

             sqlassitem = "select group_concat(distinct \"'\",subjectid,\"'\") from c_account where  subyearmonth*12+submonth="+strEndYearMonth+"  and (subjectfullname2 like '"+subjectname+"/%' or subjectfullname2 = '"+subjectname+"')  ";
             
             rs = st.executeQuery(sqlassitem);
             if(rs.next()){
            	 String s = rs.getString(1);
            	 if(s== null || "".equals(s)) {
            		 args.put("Subjects","''");
            	 }else{
            		 args.put("Subjects",rs.getString(1));
            	 }
             }else{
            	 args.put("Subjects","''");
             }
             rs.close();
             
             //最终查询结果
             resultSql = this.setSqlArguments(resultSql, args);
             System.out.println("resultSql="+resultSql);
             rs = st.executeQuery(resultSql);

             return rs;
		} catch (Exception e) {
			e.printStackTrace();
			 throw new Exception(e.getMessage());
		} 		
	}
		
	 /**
    *
    * @param rectifySign String
    *     这个rectifySign不是余额方向，而是为了避免把多极科目调整重复汇总的标志，有下级就为0，否则为1
    * @param subjectid String
    * @return String
    */
   public String getSql(String rectifySign,String subjectid){
	   return "" +
	   		"\n select   " +
	   		"\n IF(b2.assitemid is null,a2.a1,b2.a1) as a1," +
	   		"\n IF(b2.assitemid is null,a2.a2,b2.a2) as a2," +
	   		"\n IF(b2.assitemid is null,a2.a3,b2.a3) as a3," +
	   		"\n IF(b2.assitemid is null,a2.a4,b2.a4) as a4," +
	   		"\n IF(b2.assitemid is null,a2.a5,b2.a5) as a5," +
	   		"\n IF(b2.assitemid is null,a2.a6,b2.a6) as a6," +
	   		"\n IF(b2.assitemid is null,1,2) as orderid ," +
	   		"\n IF(b2.assitemid is null,'',b2.assitemid) as assitemid ," +
	   		"\n IF(b2.assitemid is null,a2.subjectid,b2.accid) as subjectid ,    " +
	   		
	   		"\n IF(b2.assitemid is null,a2.accname,b2.assitemname) as corname     " +
	   		
	   		"\n FROM " +
	   		"\n (   /* a2是余额表的数据 */ " +
	   		"\n	select  d.a1,d.a2,d.a3,d.a4,d.a5,d.a6,b.accid,a.subjectid,a.AccName  " +
	   		"\n	from  " +
	   		"\n	( " +
	   		"\n		select distinct subjectid,subjectfullname1,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0))  as balance,AccName" +
	   		"\n		from c_account  " +
	   		"\n		where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} " +
	   		"\n		and isleaf1=1 " +
	   		"\n		and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')  group by subjectid " +
	   		"\n		union " +
	   		"\n		select subjectid, subjectfullname,  0 as balance,subjectname " +
	   		"\n		from z_usesubject " +
	   		"\n		where projectid=${curProjectid}" +
	   		"\n		and tipsubjectid='"+subjectid+"' " +
	   		"\n		and isleaf=1 " +
	   		"\n	) a " +
	   		"\n	left join" +
	   		"\n	( /*判断取辅助核算还是科目，就看这个表了。条件是1月份的余额是否相等*/" +
	   		"\n		select distinct accid " +
	   		"\n		from c_assitementryacc " +
	   		"\n		where subyearmonth ${LastYear}" +
	   		"\n		and submonth=1" +
	   		"\n		and isleaf1=1  ${allassitem}" +
	   		"\n	) b" +
	   		"\n	on a.subjectid=b.accid " +
	   		"\n	 /* 账龄 */ " +
	   		"\n	left join ( " +
	   		"\n		select distinct a.subjectid,a.balance," +
	   		"\n		if(abs(a.a1)<=a.balance,a.a1,a.balance) a1," +
	   		"\n		if(abs(a.a1)+abs(a.a2)<=a.balance,a.a2,0) a2," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)<=a.balance,a.a3,0) a3," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)<=a.balance,a.a4,0) a4," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)+abs(a.a5)<=a.balance,a.a5,0) a5," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)+abs(a.a5)+abs(a.a6)<=a.balance,a.a6,0) a6" +
	   		"\n		from (" +
	   		"\n			select distinct a.subjectid, balance," +
	   		"\n			case when IFNULL(a.balance-a.occ,-1)=-1 then ifnull(a.balance,0) when a.balance-a.occ <=0 then if(a.balance>0,a.balance,0) else a.occ end a1," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ,-1)=-1 then ifnull(if(a.balance-a.occ>0,a.balance-a.occ,0),0) when a.balance-a.occ-b.occ <=0 then if(a.balance-a.occ>0,a.balance-a.occ,0) else b.occ end a2," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ>0,a.balance-a.occ-b.occ,0),0) when a.balance-a.occ-b.occ-c.occ <=0 then if(a.balance-a.occ-b.occ>0,a.balance-a.occ-b.occ,0) else c.occ end a3," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ>0,a.balance-a.occ-b.occ-c.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ <=0 then if(a.balance-a.occ-b.occ-c.occ>0,a.balance-a.occ-b.occ-c.occ,0) else d.occ end a4," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ-d.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ <=0 then if(a.balance-a.occ-b.occ-c.occ-d.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ,0) else e.occ end a5," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ >0 then a.balance-a.occ-b.occ-c.occ-d.occ-e.occ else 0 end a6" +
	   		
//	   		"\n			from (select a.subjectid,accname,subjectfullname1,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * direction,0)) + if(b.isleaf=1,ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2+DebitTotalOcc4-CreditTotalOcc4+DebitTotalOcc5-CreditTotalOcc5+DebitTotalOcc6-CreditTotalOcc6) *direction2,0),0) balance,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end + if(b.isleaf=1,ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2) *direction2,0),0) occ from (select subjectid,accname,subjectfullname1,subyearmonth,submonth,balance,direction,direction2,debitocc,creditocc,isleaf1 from c_account union select subjectID,SubjectName,SubjectFullName,substring(AccPackageID,7),'${endmonth}',0 ,case Property when '01' then 1 when '02' then -1 end direction,case Property when '01' then 1 when '02' then -1 end direction2,0,0,isleaf from z_usesubject) a left join z_accountrectify b on projectid=${curProjectid} and a.subjectid=b.subjectid where subyearmonth*12+submonth>${EndYearMonth}-(1)*12 and subyearmonth*12+submonth<=${EndYearMonth} and CONCAT(balance) >'' and isleaf1=1 group by subjectid ) a" +
	   		"\n			from (select a.subjectid,accname,subjectfullname1,balance + if(b.isleaf=1,ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2+DebitTotalOcc4-CreditTotalOcc4+DebitTotalOcc5-CreditTotalOcc5+DebitTotalOcc6-CreditTotalOcc6) *direction2,0),0) balance,occ + if(b.isleaf=1,ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2) *direction2,0),0) occ from (select subjectid,accname,subjectfullname1,direction2,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * direction,0))  balance,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end  occ from c_account where subyearmonth*12+submonth>${EndYearMonth}-(1)*12 and subyearmonth*12+submonth<=${EndYearMonth} and CONCAT(balance) >'' and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and isleaf1=1 group by subjectid union select subjectID,SubjectName,SubjectFullName,case Property when '01' then 1 when '02' then -1 end direction2,0,0 from z_usesubject where projectid=${curProjectid} and isleaf=1) a left join z_accountrectify b on projectid=${curProjectid} and a.subjectid=b.subjectid ) a" +
	   		
	   		"\n			left join (select subjectid,accname,subjectfullname1,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_account where subyearmonth*12+submonth>${EndYearMonth}-(2)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(1)*12 and CONCAT(balance) >'' and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and isleaf1=1 group by subjectid) b on a.subjectfullname1=b.subjectfullname1" +
	   		"\n			left join (select subjectid,accname,subjectfullname1,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_account where subyearmonth*12+submonth>${EndYearMonth}-(3)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(2)*12 and CONCAT(balance) >'' and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and isleaf1=1 group by subjectid) c on a.subjectfullname1=c.subjectfullname1" +
	   		"\n			left join (select subjectid,accname,subjectfullname1,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_account where subyearmonth*12+submonth>${EndYearMonth}-(4)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(3)*12 and CONCAT(balance) >'' and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and isleaf1=1 group by subjectid) d on a.subjectfullname1=d.subjectfullname1" +
	   		"\n			left join (select subjectid,accname,subjectfullname1,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_account where subyearmonth*12+submonth>${EndYearMonth}-(5)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(4)*12 and CONCAT(balance) >'' and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and isleaf1=1 group by subjectid) e on a.subjectfullname1=e.subjectfullname1" +
	   		"\n			order by a.subjectid" +
	   		
	   		"\n		) a " +
	   		"\n	) d on a.subjectid=d.subjectid" +
	   			
	   		"\n ) a2 " +
	   		"\n left join " +
	   		
	   		"\n ( /* b2 是辅助核算 */ " +
	   		"\n	SELECT  b.a1,b.a2,b.a3,b.a4,b.a5,b.a6,a.accid,a.assitemid,a.AssItemName " +
	   		"\n	FROM " +
	   		"\n	(" +
	   		"\n		select  accid,assitemid,AssItemName" +
	   		"\n		from c_assitementryacc " +
	   		"\n		where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} " +
	   		"\n		and isleaf1=1  ${allassitem}" +
	   		"\n		group by accid,assitemid " +
	   		"\n	) a " +
	   		"\n	left join " +
	   		"\n	(" +
	   		"\n		select distinct a.accid,a.assitemid,a.balance," +
	   		"\n		if(abs(a.a1)<=a.balance,a.a1,a.balance) a1," +
	   		"\n		if(abs(a.a1)+abs(a.a2)<=a.balance,a.a2,0) a2," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)<=a.balance,a.a3,0) a3," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)<=a.balance,a.a4,0) a4," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)+abs(a.a5)<=a.balance,a.a5,0) a5," +
	   		"\n		if(abs(a.a1)+abs(a.a2)+abs(a.a3)+abs(a.a4)+abs(a.a5)+abs(a.a6)<=a.balance,a.a6,0) a6" +
	   		"\n		from (" +
	   		"\n			select distinct a.accid,a.assitemid, balance," +
	   		"\n			case when IFNULL(a.balance-a.occ,-1)=-1 then ifnull(a.balance,0) when a.balance-a.occ <=0 then a.balance else a.occ end a1," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ,-1)=-1 then ifnull(if(a.balance-a.occ>0,a.balance-a.occ,0),0) when a.balance-a.occ-b.occ <=0 then if(a.balance-a.occ>0,a.balance-a.occ,0) else b.occ end a2," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ>0,a.balance-a.occ-b.occ,0),0) when a.balance-a.occ-b.occ-c.occ <=0 then if(a.balance-a.occ-b.occ>0,a.balance-a.occ-b.occ,0) else c.occ end a3," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ>0,a.balance-a.occ-b.occ-c.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ <=0 then if(a.balance-a.occ-b.occ-c.occ>0,a.balance-a.occ-b.occ-c.occ,0) else d.occ end a4," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ-d.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ <=0 then if(a.balance-a.occ-b.occ-c.occ-d.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ,0) else e.occ end a5," +
	   		"\n			case when IFNULL(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,-1)=-1 then ifnull(if(a.balance-a.occ-b.occ-c.occ-d.occ-e.occ>0,a.balance-a.occ-b.occ-c.occ-d.occ-e.occ,0),0) when a.balance-a.occ-b.occ-c.occ-d.occ-e.occ >0 then a.balance-a.occ-b.occ-c.occ-d.occ-e.occ else 0 end a6" +
	   		
	   		"\n			from (select a.accid,a.assitemid,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * direction,0)) + ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2+DebitTotalOcc4-CreditTotalOcc4+DebitTotalOcc5-CreditTotalOcc5+DebitTotalOcc6-CreditTotalOcc6) *direction,0) balance,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end + ifnull((DebitTotalOcc1-CreditTotalOcc1+DebitTotalOcc2-CreditTotalOcc2) *direction,0) occ from c_assitementryacc a left join z_assitemaccrectify b on projectid=${curProjectid} and a.accid=b.subjectid and a.AssItemID=b.AssItemID  where subyearmonth*12+submonth>${EndYearMonth}-(1)*12 and subyearmonth*12+submonth<=${EndYearMonth} ${allassitem} and CONCAT(balance) > '' and isleaf1=1 and exists (select 1 from c_account where  subyearmonth*12+submonth=${EndYearMonth}  and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and a.accid = subjectid )  group by a.accid,a.assitemid) a " +
	   		"\n			left join (select accid,assitemid,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_assitementryacc a where subyearmonth*12+submonth>${EndYearMonth}-(2)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(1)*12  ${allassitem} and CONCAT(balance) > '' and isleaf1=1 and exists (select 1 from c_account where  subyearmonth*12+submonth=${EndYearMonth}   and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and a.accid = subjectid)   group by accid,assitemid) b on a.accid=b.accid and a.assitemid=b.assitemid" +
	   		"\n			left join (select accid,assitemid,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_assitementryacc a where subyearmonth*12+submonth>${EndYearMonth}-(3)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(2)*12  ${allassitem} and CONCAT(balance) > '' and isleaf1=1 and exists (select 1 from c_account where  subyearmonth*12+submonth=${EndYearMonth}   and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and a.accid = subjectid)   group by accid,assitemid) c on a.accid=c.accid and a.assitemid=c.assitemid" +
	   		"\n			left join (select accid,assitemid,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_assitementryacc a where subyearmonth*12+submonth>${EndYearMonth}-(4)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(3)*12  ${allassitem} and CONCAT(balance) > '' and isleaf1=1 and exists (select 1 from c_account where  subyearmonth*12+submonth=${EndYearMonth}   and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and a.accid = subjectid)   group by accid,assitemid) d on a.accid=d.accid and a.assitemid=d.assitemid" +
	   		"\n			left join (select accid,assitemid,case when sum(if(direction=1,debitocc,creditocc))>0 then sum(if(direction=1,debitocc,creditocc)) else 0 end occ from c_assitementryacc a where subyearmonth*12+submonth>${EndYearMonth}-(5)*12 and subyearmonth*12+submonth<=${EndYearMonth}-(4)*12  ${allassitem} and CONCAT(balance) > '' and isleaf1=1 and exists (select 1 from c_account where  subyearmonth*12+submonth=${EndYearMonth}   and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') and a.accid = subjectid)   group by accid,assitemid) e on a.accid=e.accid and a.assitemid=e.assitemid" +
	   		"\n		) a" +
	   		"\n	) b" +
	   		"\n	on b.accID =a.accid and a.assitemid=b.assitemid" +
	   		"\n ) b2 " +
	   		"\n on a2.accid=b2.accid " +
	   		"\n ${correlation} order by orderid,a2.subjectid,b2.assitemid ";
   }
}

//  
