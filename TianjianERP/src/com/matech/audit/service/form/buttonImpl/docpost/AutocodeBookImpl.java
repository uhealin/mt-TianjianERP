package com.matech.audit.service.form.buttonImpl.docpost;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.framework.pub.autocode.DELAutocode;

public class AutocodeBookImpl implements FormButtonExtInterface {

	@Override
	public String handle(Connection conn, FormButton formButton,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		DELAutocode delAutocode=new DELAutocode();
		
		return null;
	}

}
