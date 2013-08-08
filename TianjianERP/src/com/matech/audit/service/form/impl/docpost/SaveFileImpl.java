package com.matech.audit.service.form.impl.docpost;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.doc.model.AutoCodeUsedVO;
import com.matech.audit.service.doc.model.DocPostFileVO;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class SaveFileImpl implements FormExtInterface {

	@Override
	public String beforeAdd(Connection conn, String formId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterAdd(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		DbUtil dbUtil=new DbUtil(conn);
		DocPostFileVO docPostFileVO=dbUtil.load(DocPostFileVO.class, dataUUID);
		DELAutocode delAutocode=DELAutocode.getInstant();
		AutoCodeUsedVO autoCodeUsedVO=delAutocode.bookAutoCode(conn, docPostFileVO.getDoc_type(), "all", new String[]{}, docPostFileVO.getDoc_year(), Integer.parseInt(docPostFileVO.getDoc_seqno()), 0, 0);
		docPostFileVO.setDoc_no(autoCodeUsedVO.getFullnumber());
		dbUtil.update(docPostFileVO);
		return null;
	}

	@Override
	public String beforeUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return afterAdd(conn, formId, dataUUID, req, res);
	}

	@Override
	public String beforeDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeView(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		WebUtil webUtil=new WebUtil(req, res);
		UserSession userSession=webUtil.getUserSession();
		String fd_type=req.getParameter("fd_type");
		fd_type=StringUtil.isBlank(fd_type)?"FW_TJ_"+userSession.getAreaid():fd_type;
		String fd_year=req.getParameter("fd_year");
		fd_year=StringUtil.isBlank(fd_year)?StringUtil.getCurYear():fd_year;
		int fd_seqno=0;
		DbUtil dbUtil=null;
		try {
			dbUtil=new DbUtil(conn);
			String sql="SELECT MIN(NO) FROM k_autocode_seqno WHERE NO NOT IN (SELECT number FROM k_autocodeused WHERE atype =? AND YEAR=? AND state!=0)";
			fd_seqno=dbUtil.queryForInt(sql, new Object[]{fd_type,Integer.parseInt(fd_year)});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    req.setAttribute("fd_type", fd_type);
	    req.setAttribute("fd_year", fd_year);
	    req.setAttribute("fd_seqno", fd_seqno);
	}

}
