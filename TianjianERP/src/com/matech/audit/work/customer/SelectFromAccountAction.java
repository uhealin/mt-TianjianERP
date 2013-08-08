package com.matech.audit.work.customer;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.SelectFromAccountService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SelectFromAccountAction extends MultiActionController {
	
	public ModelAndView selectAccount(HttpServletRequest request, 
			HttpServletResponse response) 
			throws Exception {
		ModelAndView modelandview = new ModelAndView("Customer/selectAccount.jsp");
		HttpSession session = request.getSession();
		
		ASFuntion CHF = new ASFuntion();
		
		String lock = "0";
		UserSession userSession = (UserSession)session.getAttribute("userSession");
		String CustomerID = CHF.showNull(request.getParameter("CustomerID"));
		String AccPackageID = CHF.showNull(request.getParameter("AccPackageID"));
		if("".equals(AccPackageID)){
			AccPackageID = CHF.showNull(userSession.getCurChoiceAccPackageId());
			if("".equals(AccPackageID)){
				AccPackageID = CHF.showNull(userSession.getCurAccPackageId());
			}
		}else{
			userSession.setCurChoiceAccPackageId(AccPackageID);				
		}
		if(!"".equals(AccPackageID)){
			if("".equals(CustomerID)){
				CustomerID=AccPackageID.substring(0,6);
			}
		}	
//		if("".equals(CustomerID)) {
//			CustomerID = userSession.getCurChoiceCustomerId();
//		}
//		if("".equals(CustomerID)) {
//			CustomerID = userSession.getCurCustomerId();
//		}
//		if (!CustomerID.equals("")) {
//			
//			userSession.setCurCustomerId(CustomerID);
//			session.setAttribute("userSession", userSession);
//			
//			lock = "1";
//		}
		
		request.setAttribute("lock", lock);
		request.setAttribute("CustomerID", CustomerID);
		modelandview.addObject("CustomerID", CustomerID);
		modelandview.addObject("AccPackageID", AccPackageID);
		
		return modelandview;
	}
	
	public ModelAndView select(HttpServletRequest request, 
							HttpServletResponse response) 
							throws Exception {
		HttpSession session = request.getSession();
		
		ASFuntion CHF = new ASFuntion();
		UserSession userSession = (UserSession)session.getAttribute("userSession");
		String AccPackageID = CHF.showNull(request.getParameter("AccPackageID"));
		
		String Search = CHF.showNull(request.getParameter("search"));				
		String menuid = CHF.showNull(request.getParameter("menuid"));		
		String CustomerID = CHF.showNull(request.getParameter("customerid"));
		
		if("".equals(AccPackageID)){
			AccPackageID = CHF.showNull(userSession.getCurChoiceAccPackageId());
			if("".equals(AccPackageID)){
				AccPackageID = CHF.showNull(userSession.getCurAccPackageId());
			}
		}else{
			userSession.setCurChoiceAccPackageId(AccPackageID);				
		}
		if(!"".equals(AccPackageID)){
			if("".equals(CustomerID)){
				if(AccPackageID.length()>=6){
					CustomerID=AccPackageID.substring(0,6);
				}
			}
		}	
		
		Connection conn = null;
		ModelAndView ModelAndView = new ModelAndView("Customer/selectAccount.jsp?menuid="+menuid);
		try{
			conn = new DBConnect().getConnect("");
			
			SelectFromAccountService service = new SelectFromAccountService(conn);
			
			DataGridProperty dataGrid = service.getSubjectName(AccPackageID,Search);
			
			session.setAttribute(DataGrid.sessionPre + dataGrid.getTableID(),dataGrid);
			
			request.setAttribute("action", "selected");
			
			request.setAttribute("AccPackageID", AccPackageID);
			
			request.setAttribute("CustomerID", AccPackageID.length()>=6?AccPackageID.substring(0, 6):AccPackageID);
			
			request.setAttribute("search", Search);
			
			request.setAttribute("CustomerID", CustomerID);
			
			if(!"".equals(menuid)){
				request.setAttribute("lock", "0");
			}
			else{
				request.setAttribute("lock", "1");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		ModelAndView.addObject("AccPackageID", AccPackageID);
		ModelAndView.addObject("CustomerID", CustomerID);
		ModelAndView.addObject("myselect", "myselect");
		return ModelAndView;
	}
	
	public ModelAndView save(HttpServletRequest request, 
						HttpServletResponse response) 
						throws Exception {
		
		PrintWriter out = response.getWriter();
		
		ASFuntion CHF = new ASFuntion();
		
		String menuid = CHF.showNull(request.getParameter("menuid"));
		
		String AccPackageID = request.getParameter("AccPackageID");
		
		String CustomerID = AccPackageID.substring(0, 6); 
		
		String closeWindow = "<script language='javascript'>window.close();</script>";
		
		String returnBack = "<script language='javascript'>window.location=('/AuditSystem/connectcompanys.do?acts=update&chooseCustomer="+CustomerID+"&menuid="+menuid+"&AccPackageID="+AccPackageID+"&isClose=no');</script>";
	    			
		String condition = CHF.showNull(request.getParameter("condition"));
		
		if(AccPackageID == null || AccPackageID.trim().equals("")){
			throw new Exception("读取账套编号出错！");
		}
		
		Set set = new HashSet();
		
		String[] SubjectNames = request.getParameterValues("choose_SelectFromAccount");
		
		SelectFromAccountService service = null;
		
		Connection conn = null;
		
		if(SubjectNames == null){
			
			out.println(closeWindow);
			
			
			try{
				conn = new DBConnect().getConnect(CustomerID);
				
				service = new SelectFromAccountService(conn);
				service.allClear(CustomerID);
				
			}catch(Exception e){
				throw new Exception("清除数据出错");
			}finally{
				DbUtil.close(conn);
			}
			
			String url = "selectFromAccount.do?method=select&AccPackageID="+AccPackageID+"&customerid="+CustomerID+"&menuid=5222";
			response.sendRedirect(url);
						
			return null;
		}
		
		for(int i=0; i<SubjectNames.length; i++){
			
			if(SubjectNames[i] != null && !SubjectNames[i].trim().equals("")){
				set.add(SubjectNames[i]);
			}
		}
		
		
		try{
			conn = new DBConnect().getConnect(CustomerID);
			
			service = new SelectFromAccountService(conn);
			
			service.clear(AccPackageID, condition,set);
			
		}catch(Exception e){
			throw new Exception("清除数据出错");
		}finally{
			DbUtil.close(conn);
		}
		
		try{
			conn = new DBConnect().getConnect("");
			
			service = new SelectFromAccountService(conn);
			
			service.save(AccPackageID, set);
			
		}catch(Exception e){
			throw new Exception("保存数据出错！");
		}finally{
			DbUtil.close(conn);
		}
		
		if(!menuid.equals("")){
			out.println(returnBack);
		}
		else{
			out.println(closeWindow);
		}
		
		return null;
	}
	
	
	
	public ModelAndView saveRelateCustomer(HttpServletRequest request, 
			HttpServletResponse response) 
			throws Exception {
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {
			String customerId = CHF.showNull(request.getParameter("customerid"));
			String customerName = CHF.showNull(request.getParameter("customerName"));
			
			conn = new DBConnect().getConnect("");
			SelectFromAccountService sfas = new SelectFromAccountService(conn);
			
			String[] customerNames = customerName.split(",");
			
			for(int i=0; i<customerNames.length; i++) {
				sfas.deleteRelateCustomer(customerId, customerNames[i]);
				sfas.saveRelateCustomer(customerId, customerNames[i]);
			}
			
			String menuid = CHF.showNull(request.getParameter("menuid"));
			String AccPackageID = CHF.showNull(request.getParameter("AccID"));
			
			response.sendRedirect("selectFromAccount.do?method=select&customerid="+menuid+"&AccPackageID="+AccPackageID) ;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 使用Ajax删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteRelateCustomer(HttpServletRequest request, 
			HttpServletResponse response) 
			throws Exception {
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {
		String customerId = CHF.showNull(request.getParameter("customerid"));
		String customerName = CHF.showNull(request.getParameter("customerName"));
		conn = new DBConnect().getConnect("");
		SelectFromAccountService sfas = new SelectFromAccountService(conn);
		String[] customerNames = customerName.split(",");
		
		for(int i=0; i<customerNames.length; i++) {
			sfas.deleteRelateCustomer(customerId, customerNames[i]);
		}
		
		String menuid = CHF.showNull(request.getParameter("menuid"));
		String AccPackageID = CHF.showNull(request.getParameter("AccID"));
		
		response.sendRedirect("selectFromAccount.do?method=select&customerid="+menuid+"&AccPackageID="+AccPackageID) ;
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
}
