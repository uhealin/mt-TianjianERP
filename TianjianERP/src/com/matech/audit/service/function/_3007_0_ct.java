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

public class _3007_0_ct extends AbstractCtPathFunction {

	public String process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String result = "/AuditSystem/account.do";
			
			String areaid = (String) args.get("areaid");
			String taskid = (String) args.get("taskid");

			String accpackageid = (String) args.get("curAccPackageID");
			if (accpackageid == null || "".equals(accpackageid) || "null".equals(accpackageid)) {
				accpackageid = userSession.getCurAccPackageId();
			}
			String projectid = (String) args.get("curProjectid");
			if (projectid == null || "".equals(projectid) || "null".equals(projectid)) {
				projectid = userSession.getCurProjectId();
			}

			String subjectname = (String) args.get("subjectname");
			if (subjectname == null || subjectname.equals("")) {
				String manuid = (String) args.get("manuid");
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

			String ct_subjectname = (String) args.get("ct_subjectname");
			
			String subjects = getSubjectIDByTaskSubectName(conn,accpackageid,subjectname);
			
			if(ct_subjectname.indexOf("/") >-1){
				String allassitem = (String) args.get("allassitem");
				String [] res = ct_subjectname.split("/");
				String [] result1 = getCtAssItemIDandAccID(conn,accpackageid,subjects,res[res.length-1],allassitem);
				
				result = "/AuditSystem/assitementryacc.do?SubjectID1="+ result1[0] + "&AssItemID1=" + result1[2] + "&AssPro1=" + result1[1] + "&AssPro2=" + result1[1];
				
			}else{
				result += "?subjectID="+getAccPkgSubjectID(conn,accpackageid,subjects,ct_subjectname);
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
