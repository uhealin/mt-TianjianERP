package com.matech.framework.servlet.extgrid;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;


/**
 * 返回前台EXTGRID所需异步加载的数据的后台类
 * @author asus-0331
 *
 */
public class ExtGridPrintServlet  extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
	
		try{
			String tableId = request.getParameter("tableId") ;
		
			HttpSession session = request.getSession() ;
			DataGridProperty pp = (DataGridProperty)session.getAttribute(ExtGrid.sessionPre+tableId);
			out=response.getWriter() ;
			ExtGrid extgrid = new ExtGrid(request,response,pp) ;
			if(pp.isPrintEnable()) {
				//设置打印参数 
				extgrid.setPrint(out) ; 
			}else {
				out.println("");
			}
			
	
		}catch(Exception e){
			if (out!=null){
				try{
				out.close();
				}catch(Exception e1){}
			}
			e.printStackTrace();
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doPost(request,response);
	}
	
	
	
}
