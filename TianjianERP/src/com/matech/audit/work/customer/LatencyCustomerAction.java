package com.matech.audit.work.customer;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.LatencyCustomerService;
import com.matech.audit.service.customer.model.LatencyCustomer;
import com.matech.framework.pub.db.DbUtil;

public class LatencyCustomerAction extends MultiActionController {

	private final String _latencyCustomerList = "/Customer/LatencyCustomerList.jsp";
	private final String _latencyCustomerEdit = "/Customer/LatencyCustomerAdd.jsp";

	/**
	 * 显示客户潜在项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_latencyCustomerList);
		HttpSession session = request.getSession();

		//客户编号
		String customerId = request.getParameter("customerid");
		
		DataGridProperty dgProperty = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {
			}
		};

		// 客户潜在项目
		dgProperty.setTableID("latencyCustomerList");
		dgProperty.setInputType("radio");

		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintVerTical(false);

		// 打印的列宽
		dgProperty.setPrintColumnWidth("13,60,15,22,11,14,37,12,12,15");
		// 打印的表名
		dgProperty.setPrintTitle("潜在客户列表");

		dgProperty.addColumn("项目编号", "projectId");
		dgProperty.addColumn("项目信息", "projectInformation");
		dgProperty.addColumn("预计时间", "planTime");
		dgProperty.addColumn("可行性评估", "viable");
		dgProperty.addColumn("记录人", "recoder");
		dgProperty.addColumn("记录时间", "recodeTime");
		dgProperty.addColumn("后继跟踪指示", "nextDenote");
		dgProperty.addColumn("继跟踪责任人", "nextPrincipal");
		dgProperty.addColumn("指示人", "denotePerson");
		dgProperty.addColumn("指示时间", "denoteTime");

		
		String sql = "";
		if("".equals(customerId)||customerId==null){
			sql = "select * from oa_latencyCustomer " ;
		}else{
			sql = "select * from oa_latencyCustomer where customerid=" + customerId;
			request.setAttribute("customerid", customerId);
		}
		
		dgProperty.setSQL(sql.toString());

		dgProperty.setOrderBy_CH("autoid");
		dgProperty.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		return modelAndView;
	}

	/**
	 * 增加客户潜在项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addLatencyCustomer(HttpServletRequest request,
			HttpServletResponse response,LatencyCustomer latencyCustomer) throws Exception {

		Connection conn = null;
	
		//客户编号
		String customerId = "";
		boolean flag = false;
		
		String CustomerID = request.getParameter("CustomerID");
		
		if("".equals(CustomerID)||CustomerID==null){
			customerId = request.getParameter("customerid");			
		}else{
			customerId = CustomerID;
			flag = true;
		}
		
		try {
			 conn = new DBConnect().getConnect("");
						
			 LatencyCustomerService latencyCustomerService = new LatencyCustomerService(conn);
			 //增加客户潜在项目
			 latencyCustomerService.addLatencyCustomer(latencyCustomer,customerId);			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		if(flag){
			response.sendRedirect("latencyCustomer.do");
		}else{
			response.sendRedirect("latencyCustomer.do?customerid="+customerId);
		}
				
		return null;
	}

	/**
	 * 修改客户潜在项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateLatencyCustomer(HttpServletRequest request,
			HttpServletResponse response,LatencyCustomer latencyCustomer) throws Exception {
		
		Connection conn = null;
		
		String customerId = request.getParameter("customerid");
		try {
			conn = new DBConnect().getConnect("");
			//要修改的客户潜在项目的autoid
			String autoid = request.getParameter("autoid");
						
			LatencyCustomerService latencyCustomerService = new LatencyCustomerService(conn);
			//修改客户潜在项目
			latencyCustomerService.updateLatencyCustomer(latencyCustomer, autoid);			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		if("".equals(customerId)||customerId==null){
			response.sendRedirect("latencyCustomer.do");
		}else{
			response.sendRedirect("latencyCustomer.do?customerid="+customerId);
		}
		
		return null;
	}

	/**
	 * 删除客户潜在项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeLatencyCustomer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null;
		
		String customerId = request.getParameter("customerid");
		try {
			conn = new DBConnect().getConnect("");
			//要删除的客户潜在项目的autoid
			String autoid = request.getParameter("autoid");
						
			LatencyCustomerService latencyCustomerService = new LatencyCustomerService(conn);
			//删除客户潜在项目
			latencyCustomerService.removeLatencyCustomer(autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
				
		if("".equals(customerId)||customerId==null){
			response.sendRedirect("latencyCustomer.do");
		}else{
			response.sendRedirect("latencyCustomer.do?customerid="+customerId);
		}
		
		return null;
	}

	/**
	 * 编辑客户潜在项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitLatencyCustomer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(_latencyCustomerEdit);
			
		LatencyCustomer latencyCustomer = new LatencyCustomer();

		//要修改的客户潜在项目的autoid
		String autoid = request.getParameter("autoid");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
						
			LatencyCustomerService latencyCustomerService = new LatencyCustomerService(conn);
			//根据customerId获得客户潜在项目			
			latencyCustomer = latencyCustomerService.getLatencyCustomer(autoid);			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("LatencyCustomer",latencyCustomer);

		return modelAndView;		
	}
	
}
