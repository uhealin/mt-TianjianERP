package com.matech.framework.servlet.extgrid;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;
import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 返回前台EXTGRID所需异步加载的数据的后台类
 * @author asus-0331
 *
 */
public class ExtGridServlet  extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		Connection conn = null ;
		try{
			String tableId = request.getParameter("tableId") ;
			ASFuntion CHF = new ASFuntion();
			String xml=CHF.showNull(request.getParameter("xml"));
			String vl=CHF.showNull(request.getParameter("vl"));
			String anode=CHF.showNull(request.getParameter("anode"));
			String summary = CHF.showNull(request.getParameter("summary_"+tableId));
			
			String bodyWidth = CHF.showNull(request.getParameter("bodyWidth")) ;
			
			HttpSession session = request.getSession() ;
			out=response.getWriter() ;	
			DataGridProperty pp = (DataGridProperty)session.getAttribute(ExtGrid.sessionPre+tableId);
			String outputStr ="" ;
			ExtGrid extgrid = new ExtGrid(request,response,pp) ;
			pp.setBodyWidth(bodyWidth) ;
			if(!"".equals(anode)) {
				//treeGrid行展开，调用onExpan方法
				conn = new DBConnect().getConnect(extgrid.getCustomerId());
				ResultSet rs = pp.onExpand(session, request, response,conn) ;
				outputStr = extgrid.printTreeGridExpandData(rs) ;
			}else {
				if("head".equals(request.getParameter(tableId+"_head"))) {
					pp.setPage_xml(xml,"",vl,session,request,response); //只在加载表头时调pp的goSearch方法
					outputStr = extgrid.printGridHead();
//					System.out.println("我是表头:" + outputStr);
				}else if("summary".equals(summary)){
					outputStr = extgrid.printGridSummary();
				}else {
					outputStr = extgrid.printGridData(); 
					//System.out.println("我是数据:" + outputStr);  
				}
			}
			out.write(outputStr);
			
		}catch(Exception e){
			if (out!=null){
				try{
				out.close();
				}catch(Exception e1){}
			}
			e.printStackTrace();
		}finally{
			DbUtil.close(conn) ;
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doPost(request,response);
	}
	
	
	
}
