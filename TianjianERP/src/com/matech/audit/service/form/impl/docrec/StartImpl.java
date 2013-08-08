package com.matech.audit.service.form.impl.docrec;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.doc.DocRecService;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class StartImpl implements FormExtInterface {

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
		DocRecService docRecService=new DocRecService(conn);
		DocRecVO docRecVO=dbUtil.load(DocRecVO.class, dataUUID);
		WebUtil webUtil=new WebUtil(req, res);
		docRecService.remindStart( docRecVO,false);
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
		return null;
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

	}

}
