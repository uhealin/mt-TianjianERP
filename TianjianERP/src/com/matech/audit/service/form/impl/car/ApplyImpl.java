package com.matech.audit.service.form.impl.car;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.form.FormExtInterface;

public class ApplyImpl implements FormExtInterface{

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
