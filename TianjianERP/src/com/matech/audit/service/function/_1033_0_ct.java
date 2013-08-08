package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

public class _1033_0_ct extends AbstractCtPathFunction {
	public String process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String URL = "/AuditSystem/VoucherQuery.do";
			
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
			
			String ds = (String) args.get("ds");
			
			String ct_subjectname = (String) args.get("ct_subjectname");
			String ct_subjectfullname = (String) args.get("ct_subjectfullname");
			
			String subjects = getSubjectIDByTaskSubectName(conn,accpackageid,ds); 
			
			String subjectid = "";
			if(ct_subjectfullname == null || "".equals(ct_subjectfullname.trim()) || "null".equals(ct_subjectfullname.trim())){
				subjectid = getAccPkgSubjectID(conn,accpackageid,subjects,ct_subjectname);
			}else{
				String sql = "select distinct subjectid from c_account where accpackageid = ? and SubjectFullName1 = ? and submonth =1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				ps.setString(2, ct_subjectfullname);
				rs = ps.executeQuery();
				if(rs.next()){
					subjectid = rs.getString(1);
				}
			}
			if(!"".equals(subjectid)) URL += "?moneysql= and subjectid = '"+subjectid+"'" ;
			
			return URL;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
