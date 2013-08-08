package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class _9999_0_ct extends AbstractCtPathFunction {
	public String process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.println("9999公式穿透开始");
			st = conn.createStatement();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String result = "/AuditSystem/account.do";
			
			String areaid = CHF.showNull((String) args.get("areaid"));
			String taskid = CHF.showNull((String) args.get("taskid"));

			
			
			String accpackageid = CHF.showNull((String) args.get("curAccPackageID"));
			if (accpackageid == null || "".equals(accpackageid) || "null".equals(accpackageid)) {
				accpackageid = userSession.getCurAccPackageId();
			}
			String projectid = CHF.showNull((String) args.get("curProjectid"));
			if (projectid == null || "".equals(projectid) || "null".equals(projectid)) {
				projectid = userSession.getCurProjectId();
			}

			String subjectname = CHF.showNull((String) args.get("subjectname"));
			if("".equals(subjectname)){
				subjectname = CHF.showNull((String) args.get("科目名称"));
			}
			
			if (subjectname == null || subjectname.equals("")) {
				String manuid = CHF.showNull((String) args.get("manuid"));
				if (manuid == null || manuid.equals("")) {
					subjectname = getTaskSubjectNameByTaskCode(conn, projectid,(String) args.get("curTaskCode") );
				} else {
					subjectname = getTaskSubjectNameByManuID(conn, manuid);
				}

			}
			String sName = changeSubjectName(conn, projectid, subjectname);

			if (!"".equals(sName)) {
				subjectname = sName;
			}

			String ct_subjectname = CHF.showNull((String) args.get("ct_subjectname"));
			
			int bTime = Integer.parseInt(userSession.getCurProjectBeginYear()) * 12 + Integer.parseInt(userSession.getCurProjectBeginMonth());
			int eTime = Integer.parseInt(userSession.getCurProjectEndYear()) * 12 + Integer.parseInt(userSession.getCurProjectEndMonth());
			
			int opt = 1;
			
			if(ct_subjectname == null || "".equals(ct_subjectname) || "null".equals(ct_subjectname)){
				ct_subjectname  =  CHF.showNull((String) args.get("ct_科目名称"));
				if(ct_subjectname == null || "".equals(ct_subjectname) || "null".equals(ct_subjectname)){
					ct_subjectname  =  CHF.showNull((String) args.get("ct_科目全名称"));
					
					opt = 2;
				}
			}
			
			if(ct_subjectname == null){
				ct_subjectname = "";
			}
			
			System.out.println(ct_subjectname);
			
			String subjects = getSubjectIDByTaskSubectName(conn,bTime,eTime,subjectname);
			String allassitem = CHF.showNull((String) args.get("allassitem"));
			if("".equals(allassitem)){
				String isAssItem = CHF.showNull((String)args.get("包含核算"));
				allassitem = CHF.showNull((String)args.get("核算名称"));
				if("否".equals(isAssItem) && "".equals(allassitem)){
					
				}else if("是".equals(isAssItem) && "".equals(allassitem)){
					
				}else if("".equals(allassitem)){
					allassitem="客户;供应商;关联;往来";
				}
			}
			
			if(ct_subjectname.indexOf("/") >-1){
				String [] res = ct_subjectname.split("/");				
				if(opt == 2){
					String string = getAccPkgSubjectID(conn,bTime,eTime,subjects,res[res.length-1]);
					if("".equals(string)){
						String [] result1 = getCtAssItemIDandAccID(conn,accpackageid,bTime,eTime,subjects,res[res.length-1],allassitem);
						result = "/AuditSystem/assitementryacc.do?SubjectID1="+ result1[0] + "&AssItemID1=" + result1[2] + "&AssPro1=" + result1[1] + "&AssPro2=" + result1[1];
					}else{
						result += "?subjectID="+string;	
					}
				}else{
					String [] result1 = getCtAssItemIDandAccID(conn,accpackageid,bTime,eTime,subjects,res[res.length-1],allassitem);
					result = "/AuditSystem/assitementryacc.do?SubjectID1="+ result1[0] + "&AssItemID1=" + result1[2] + "&AssPro1=" + result1[1] + "&AssPro2=" + result1[1];
				}
				
				
			}else{
				String string = getAccPkgSubjectID(conn,bTime,eTime,subjects,ct_subjectname);
				if("".equals(string)){
					String [] result1 = getCtAssItemIDandAccID(conn,accpackageid,bTime,eTime,subjects,ct_subjectname,allassitem);
					result = "/AuditSystem/assitementryacc.do?SubjectID1="+ result1[0] + "&AssItemID1=" + result1[2] + "&AssPro1=" + result1[1] + "&AssPro2=" + result1[1];
				}else{
					result += "?subjectID="+string;	
				}
				
			}
			
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		
	}
}
