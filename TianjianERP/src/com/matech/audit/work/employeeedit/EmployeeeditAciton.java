package com.matech.audit.work.employeeedit;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employeeedit.model.EmployeeeditVO;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class EmployeeeditAciton extends MultiActionController{

	private String EDIT="/employee/edit.jsp";
	private String VIEW="/employee/view.jsp";
	private String VIEWEDIT="/employee/viewedit.jsp";
	//保存
	
	
	//查询第一个
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelandview = new ModelAndView(VIEW);	
		ModelAndView modelandview1 = new ModelAndView(VIEWEDIT);	
		
		String opt = request.getParameter("opt");
		
		System.out.println(opt);
		
		WebUtil webutil = null;
		
		Connection conn = null;
		DbUtil dbutil = null;
		List<EmployeeeditVO> employeeeditVOs=new ArrayList<EmployeeeditVO>();
		try{
			conn=new DBConnect().getConnect();
			dbutil=new DbUtil(conn);
			employeeeditVOs=dbutil.select(EmployeeeditVO.class, "select * from {0}");
			
			request.setAttribute("employeeedit",employeeeditVOs.get(0));
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
		EmployeeeditVO e = null;
		try{
			conn=new DBConnect().getConnect();
			dbutil=new DbUtil(conn);
			e = dbutil.load(EmployeeeditVO.class,uuid);
			
			
			
			request.setAttribute("employeeedit",e);
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

	//	ModelAndView modelandview = new ModelAndView("employeeedit.do?opt=1");	
		
		response.setContentType("text/html;charset=utf-8");
		
		String uuid = request.getParameter("uuid");
		String content = request.getParameter("content");
		System.out.println(uuid);
		
		WebUtil webutil = null;
		EmployeeeditVO employeeedit = null;
		Connection conn = null;
		DbUtil dbutil = null;
		int i = 0;
	try{	
		webutil = new WebUtil(request, response);
		employeeedit = webutil.evalObject(EmployeeeditVO.class);
		conn = new DBConnect().getConnect();
		dbutil = new DbUtil(conn);
		i = dbutil.update(employeeedit);
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
