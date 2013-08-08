package com.matech.audit.service.form;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface FormExtInterface {

	/**
	 * 通用表单新增前调用
	 * @param args
	 * @return
	 */
	public String beforeAdd(Connection conn, String formId, HttpServletRequest req,HttpServletResponse res) throws Exception ;
	
	/** 
	 * 通用表单新增后调用
	 * @param args
	 * @return
	 */
	public String afterAdd(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception ;
	
	/**
	 * 通用表单修改前调用
	 * @param args
	 * @return
	 */
	public String beforeUpdate(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception ;
	
	/**
	 * 通用表单修改后调用
	 * @param args
	 * @return
	 */
	public String afterUpdate(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception ;
	
	/**
	 * 通用表单删除前调用
	 * @param args
	 * @return
	 */
	public String beforeDelete(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception ;
	
	/**
	 * 通用表单删除后调用
	 * @param args
	 * @return
	 */
	public String afterDelete(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception ;

	
	public void beforeView(Connection conn, String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res,ModelAndView modelAndView)  ;
}
