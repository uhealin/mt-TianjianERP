package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class _3100_0 extends AbstractAreaFunction {
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
             }

             String sName = changeSubjectName(conn,projectid,subjectname);
             if(!"".equals(sName)){
             	subjectname = sName; 
             }            
             args.put("subjectname",subjectname);
             
			String strEndYearMonth = "";
			String endmonth = (String) args.get("endmonth");
			if(endmonth == null || endmonth.equals("")) {
				int[] result = getProjectAuditAreaByProjectid(conn, projectid);
				strEndYearMonth = String.valueOf(result[2] * 12 + result[3]);
				endmonth = String.valueOf(result[3]);
			}else{
				strEndYearMonth = String.valueOf(Integer.parseInt(accpackageid.
                        substring(6)) * 12 + Integer.parseInt(endmonth));
			}
			args.put("endmonth", endmonth);
			args.put("EndYearMonth", strEndYearMonth);
			
			st = conn.createStatement();
			
			resultSql = getSql();
			
			resultSql = this.setSqlArguments(resultSql, args);
			System.out.println("resultSql="+resultSql);
            rs = st.executeQuery(resultSql);
            return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} 		
	}
	
	public String getSql(){
		return "" +
			" select a.name, case a.DataName when '0' then '人民币' else a.DataName end DataName, " +
			" Balance, ifnull(exchangerate,case when a.dataname = '0' then 1 end)*Balance as Balance1," +
			" funcOccur, ifnull(exchangerate,case when a.dataname = '0' then 1 end)*funcOccur as funcOccur1," +
			" factOcc, ifnull(exchangerate,case when a.dataname = '0' then 1 end)*factOcc as factOcc1," +
			" letOccour, ifnull(exchangerate,case when a.dataname = '0' then 1 end)*letOccour as letOccour1," +
			" ifnull(exchangerate,case when a.dataname = '0' then 1 end)*(Balance-letOccour) occ," +
			" case  isReplace when 0 then '否' else '是' end isReplace," +
			" case  isReplace when 0 then '×' else '√' end isReplace1," +
			" case  hasreturn when 1 then '√' else '×' end hasreturn," +
			" ifnull(memo,'') memo,returntime " +
			" from z_letters a left join " +  
			" (" +
			
			"	select distinct subjectid,'' assitemid,'0' DataName,0 Balance," +
			"	ifnull(concat(c.standkey,substring(a.subjectfullname,CHAR_LENGTH(userkey)+1)),a.subjectfullname) subjectfullname2" +
			"	from z_usesubject a " +
			"	left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))" +
			"	where projectid = '${curProjectid}' and isleaf=1   " +
			
			"	union "+ 
			
			" 	select subjectid,'' assitemid,DataName,direction2*Balance Balance,subjectfullname2 " + 
			" 	from c_account where AccPackageID='${curAccPackageID}' and submonth=${endmonth} and isleaf1=1 " +  
			" 	union " +  
			" 	select subjectid,'' assitemid, DataName,direction2*Balance Balance,subjectfullname2 " + 
			" 	from c_accountall where AccPackageID='${curAccPackageID}' and submonth=${endmonth} and isleaf1=1 and AccSign=1 " +
			" 	union " +  
			" 	select accid,assitemid, a.DataName,a.direction2*a.Balance Balance ,b.subjectfullname2 " +
			" 	from c_assitementryacc a" +
			" 	left join c_account b on b.submonth=${endmonth} and a.AccPackageID=b.AccPackageID and a.accid=b.subjectid" +
			"	where a.AccPackageID='${curAccPackageID}' and a.submonth=${endmonth} and a.isleaf1=1 " +
			" 	union " + 
			" 	select accid,assitemid, a.DataName,a.direction2*a.Balance Balance ,b.subjectfullname2 " +
			" 	from c_assitementryaccall a" +
			" 	left join c_account b on b.submonth=${endmonth} and a.AccPackageID=b.AccPackageID and a.accid=b.subjectid" +
			"	where a.AccPackageID='${curAccPackageID}' and a.submonth=${endmonth} and a.isleaf1=1 and a.AccSign=1 " + 
			" ) b on a.subjectid=b.subjectid and a.assitemid=b.assitemid and a.DataName=b.DataName " +
			" left join z_exchangerate c  " +
			" on a.projectid = '${curProjectid}' and c.projectid = '${curProjectid}' and case when a.DataName ='人民币' then '0' else a.DataName end=c.currname" +
			" where a.projectid='${curProjectid}' " +
			" and (b.subjectfullname2 like '${subjectname}/%' or b.subjectfullname2= '${subjectname}')";
	}
			
}
