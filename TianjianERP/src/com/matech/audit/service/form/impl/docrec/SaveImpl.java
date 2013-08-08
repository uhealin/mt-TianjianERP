package com.matech.audit.service.form.impl.docrec;

import java.sql.Connection;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.doc.DocRecService;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.framework.pub.db.DbUtil;

public class SaveImpl implements FormExtInterface {

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
        docRecVO.setHandler_ids(docRecVO.getRoam_range_ids());
        docRecVO.setHandler_names(docRecVO.getRoam_range_names());
        docRecVO.setState("发起");
        int i= dbUtil.update(docRecVO);

		if(i==1){
		return MessageFormat.format("文号 {0} 新增成功", docRecVO.getRec_doc_no());
		}
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
		DbUtil dbUtil=new DbUtil(conn);
		DocRecService docRecService=new DocRecService(conn);
		DocRecVO docRecVO=dbUtil.load(DocRecVO.class, dataUUID);
        docRecVO.setHandler_ids(docRecVO.getRoam_range_ids());
        docRecVO.setHandler_names(docRecVO.getRoam_range_names());
        docRecVO.setState("发起");
        int i= dbUtil.update(docRecVO);

		if(i==1){
		return MessageFormat.format("文号 {0} 修改成功", docRecVO.getRec_doc_no());
		}
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
