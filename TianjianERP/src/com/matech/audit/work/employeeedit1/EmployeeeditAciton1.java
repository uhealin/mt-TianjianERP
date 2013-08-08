package com.matech.audit.work.employeeedit1;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employeeedit1.model.EmployeeeditVO1;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class EmployeeeditAciton1 extends MultiActionController{

	private String EDIT="/employee1/edit.jsp";
	private String VIEW="/employee1/view.jsp";
	private String VIEWEDIT="/employee1/viewedit.jsp";
	//保存
	
	
	//查询第一个
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelandview = new ModelAndView(VIEW);	
		ModelAndView modelandview1 = new ModelAndView(VIEWEDIT);	
		
		String opt = request.getParameter("opt");
		WebUtil webutil = null;
		
		Connection conn = null;
		DbUtil dbutil = null;
		List<EmployeeeditVO1> employeeeditVO1s=new ArrayList<EmployeeeditVO1>();
		try{
			conn=new DBConnect().getConnect();
			dbutil=new DbUtil(conn);
			employeeeditVO1s=dbutil.select(EmployeeeditVO1.class, "select * from hr_edit1");
			
			request.setAttribute("employeeedit1",employeeeditVO1s.get(0));
			//modelandview.addObject("employeeedit",employeeeditVOs.get(0));
			if("1".equals(opt)){
				return modelandview;
			}
			
		}catch(Exception ex){
			
		}finally{
			DbUtil.close(conn);
		}
		return modelandview1;
	}
	
	
	
	
	//跳到修改页面
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		ModelAndView modelandview = new ModelAndView(EDIT);	
		String uuid = request.getParameter("uuid");
		
		WebUtil webutil = null;
		
		Connection conn = null;
		DbUtil dbutil = null;
		EmployeeeditVO1 e = null;
		try{
			conn=new DBConnect().getConnect();
			dbutil=new DbUtil(conn);
			e = dbutil.load(EmployeeeditVO1.class,uuid);
			
			
			
			request.setAttribute("employeeedit1",e);
			//modelandview.addObject("employeeedit",employeeeditVOs.get(0));
			
			System.out.println(e);
		}catch(Exception ex){
			
		}finally{
			DbUtil.close(conn);
		}
		return modelandview;
	}
	
	
	//修改保存
	public ModelAndView addSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(VIEW);	
		
		response.setContentType("text/html;charset=utf-8");
		
		String uuid = request.getParameter("uuid");
		String content = request.getParameter("content");
		System.out.println(uuid);
		
		WebUtil webutil = null;
		EmployeeeditVO1 employeeedit1 = null;
		Connection conn = null;
		DbUtil dbutil = null;
		int i = 0;
	try{	
		webutil = new WebUtil(request, response);
		employeeedit1 = webutil.evalObject(EmployeeeditVO1.class);
		conn = new DBConnect().getConnect();
		dbutil = new DbUtil(conn);
		i = dbutil.update(employeeedit1);
		if(i == 1){
				webutil.alert("保存成功");
		     }else{
		    	 webutil.alert("保存失败，请重新修改");
		     }
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		DbUtil.close(conn);
	}
	
		return list(request,response);
	}
}
