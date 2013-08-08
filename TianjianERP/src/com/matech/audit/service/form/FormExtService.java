package com.matech.audit.service.form;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class FormExtService {
	
	private FormExtInterface formExtClass = null;
	protected FormExtService() {
		
	}
	
	public FormExtService(String className) throws Exception{
		
		if(className != null && !"".equals(className)) {
			Class clazz = Class.forName(className);
			this.formExtClass = (FormExtInterface)clazz.newInstance();
		}
	}
	
	public String beforeAdd(Connection conn, String formId, HttpServletRequest req,HttpServletResponse res) throws Exception {
		if(this.formExtClass == null) {
			return "";
		}
		return this.formExtClass.beforeAdd(conn, formId, req,res);
	}

	public String afterAdd(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception  {
		if(this.formExtClass == null) {
			return "";
		}
		return	this.formExtClass.afterAdd(conn, formId, dataUUID, req,res);
			
		
		
	}

	public String beforeUpdate(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
		if(this.formExtClass == null) {
			return "";
		}
		  return	this.formExtClass.beforeUpdate(conn, formId, dataUUID, req,res);
		
	}

	public String afterUpdate(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception  {
		if(this.formExtClass == null) {
			return "";
		}
		return	this.formExtClass.afterUpdate(conn, formId, dataUUID, req,res);
		
		
	}

	public String beforeDelete(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception  {
		if(this.formExtClass == null) {
			return "";
		}
		return	this.formExtClass.beforeDelete(conn, formId, dataUUID, req,res);
		
		
	}

	public String afterDelete(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception  {
		if(this.formExtClass == null) {
			return "";
		}
		   return	this.formExtClass.afterDelete(conn, formId, dataUUID, req,res);
		
		
	}
	
	public void beforeView(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res,ModelAndView modelAndView) throws Exception  {
		if(this.formExtClass == null) {
			return ;
		}
		 this.formExtClass.beforeView(conn, formId, dataUUID, req, res,modelAndView);
		
	}
}
