package com.matech.audit.service.form.buttonImpl.docpost;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.doc.DocPostService;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class CounterSignImpl implements FormButtonExtInterface{

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		DbUtil dbUtil=new DbUtil(conn);
		WebUtil webUtil=new WebUtil(request, response);
		DocPostService docPostService=new DocPostService(conn);
		DocPostVO vo=webUtil.evalObject(DocPostVO.class);
		vo=dbUtil.load(vo, vo.getUuid());
		UserSession userSession=webUtil.getUserSession();
	    List<String> results=docPostService.doCounterSign(vo, userSession,"");
		return results.get(0);
	}

}
