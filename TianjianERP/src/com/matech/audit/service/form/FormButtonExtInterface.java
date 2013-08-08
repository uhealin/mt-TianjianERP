package com.matech.audit.service.form;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.model.FormButton;

public interface FormButtonExtInterface {
	public String handle(Connection conn,FormButton formButton, HttpServletRequest request,HttpServletResponse response) throws Exception;
}
