package com.matech.audit.service.form.buttonImpl.docrec;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.doc.DocRecService;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class CheckImpl implements FormButtonExtInterface {

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		String uuid=request.getParameter("uuid");
	    DocRecVO vo=new DocRecVO();
	    DbUtil dbUtil=null;
	    WebUtil webUtil=new WebUtil(request, response);
	    DocRecService docRecService=null;
	    DocRecVO docRecVO=null;
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docRecService=new DocRecService(conn);
			docRecVO=dbUtil.load(DocRecVO.class, uuid);
			return docRecService.doCheck(docRecVO, webUtil.getUserSession()).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}

}
