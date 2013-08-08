package com.matech.audit.service.form.hr;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;

public class LookImpl implements FormButtonExtInterface {

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String uuid = request.getParameter("uuid");
	
		
		
	
		return "formDefine.do?method=formListView&uuid=9934f462-bdcf-4fa7-822d-ca79853716bd";
	}

}
