package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.UTILString;

public class _1015_0_ct extends AbstractCtPathFunction {
	public String process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String result = "/AuditSystem/voucherquery/SortList.jsp";
			
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
					subjectname = getTaskSubjectNameByTaskCode(conn, projectid,(String) request.getParameter("curTaskCode"));
				} else {
					subjectname = getTaskSubjectNameByManuID(conn, manuid);
				}

			}
			String sName = changeSubjectName(conn, projectid, subjectname);

			if (!"".equals(sName)) {
				subjectname = sName;
			}
			
			String subjects = getSubjectIDByTaskSubectName(conn,accpackageid,subjectname);
			
			String ct_vchdate = (String) args.get("ct_vchdate");
			String ct_typenumber = (String) args.get("ct_typenumber");
			ct_vchdate = UTILString.formatDate(ct_vchdate);
			
			String sql = "select distinct VoucherID from c_subjectentry where concat(typeid,'-',oldvoucherid) = ? and vchdate = ? and AccPackageID =? limit 1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ct_typenumber);
			ps.setString(2, ct_vchdate);
			ps.setString(3, accpackageid);
			rs = ps.executeQuery();
			if(rs.next()){
				result += "?AccPackageID="+accpackageid+"&VoucherID="+rs.getString(1) ;
			}else{
				result = "/AuditSystem/VoucherQuery.do";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

}
