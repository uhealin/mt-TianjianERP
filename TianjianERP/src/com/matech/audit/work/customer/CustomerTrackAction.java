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
import com.matech.audit.service.customer.CustomerTrackService;
import com.matech.audit.service.customer.model.CustomerTrack;
import com.matech.framework.pub.db.DbUtil;

public class CustomerTrackAction extends MultiActionController {

	private final String _customerTrackList = "/Customer/CustomerTrackList.jsp";
	private final String _customerTrackEdit = "/Customer/CustomerTrackAdd.jsp";

	/**
	 * 显示客户追踪记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_customerTrackList);
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

		// 客户追踪记录
		dgProperty.setTableID("customerTrackList");
		dgProperty.setInputType("radio");

		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintVerTical(false);

		// 打印的列宽
		dgProperty.setPrintColumnWidth("25,25,10,15,15,10,25,25,25,25,10,15");
		// 打印的表名
		dgProperty.setPrintTitle("客户追踪记录列表");

		dgProperty.addColumn("单位名称", "companyName");
		dgProperty.addColumn("项目名称", "projectName");
		dgProperty.addColumn("单位联系人", "linkman");
		dgProperty.addColumn("联系人电话", "telPhone");
		dgProperty.addColumn("联系人QQ", "linkmanQQ");
		dgProperty.addColumn("来致电", "giveCall");
		dgProperty.addColumn("致电主题", "callTopic");
		dgProperty.addColumn("已解决问题", "fixedQuestion");
		dgProperty.addColumn("未解决问题", "unfixQuestion");
		dgProperty.addColumn("完成情况", "fixedInstance");
		dgProperty.addColumn("记录人", "recoder");
		dgProperty.addColumn("记录时间", "recodeTime");

		String sql = "";
		if("".equals(customerId)||customerId==null){
			sql = "select * from oa_customertrack " ;
		}else{
			sql = "select * from oa_customertrack where customerid=" + customerId;
			request.setAttribute("customerid", customerId);
		}
		
		dgProperty.setSQL(sql.toString());

		dgProperty.setOrderBy_CH("recodeTime");
		dgProperty.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);
		
		return modelAndView;
	}

	/**
	 * 增加客户追踪记录
	 * @param request
	 * @param response
	 * @param customerTrack
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addCustomerTrack(HttpServletRequest request,
			HttpServletResponse response, CustomerTrack customerTrack)
			throws Exception {

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
						
			 CustomerTrackService customerTrackService = new CustomerTrackService(conn);
			 //增加客户追踪记录
			 customerTrackService.addCustomerTrack(customerTrack,customerId);		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
        
		if(flag){
			response.sendRedirect("/AuditSystem/customerTrack.do");
		}else{
			response.sendRedirect("/AuditSystem/customerTrack.do?customerid="+customerId);
		}
						
		return null;

	}

	/**
	 * 修改客户追踪记录
	 * @param request
	 * @param response
	 * @param customerTrack
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateCustomerTrack(HttpServletRequest request,
			HttpServletResponse response, CustomerTrack customerTrack)
			throws Exception {

		Connection conn = null;
		
		String customerId = request.getParameter("customerid");
		
		try {
			conn = new DBConnect().getConnect("");
			//要修改的客户追踪记录的autoid
			String autoid = request.getParameter("autoid");
									
			CustomerTrackService customerTrackService = new CustomerTrackService(conn);
			//修改客户追踪记录
			customerTrackService.updateCustomerTrack(customerTrack,autoid);		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		if("".equals(customerId)||customerId==null){
			response.sendRedirect("/AuditSystem/customerTrack.do");
		}else{
			response.sendRedirect("/AuditSystem/customerTrack.do?customerid="+customerId);
		}
		
		
		return null;

	}

	/**
	 * 删除客户追踪记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		
		String customerId = request.getParameter("customerid");
		try {
			conn = new DBConnect().getConnect("");
			//要删除的客户追踪记录的autoid
			String autoid = request.getParameter("autoid");
				
			CustomerTrackService customerTrackService = new CustomerTrackService(conn);
			//修改客户追踪记录
			customerTrackService.removeCustomerTrack(autoid);		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		if("".equals(customerId)||customerId==null){
			response.sendRedirect("/AuditSystem/customerTrack.do");
		}else{
			response.sendRedirect("/AuditSystem/customerTrack.do?customerid="+customerId);
		}
		
		
		
		return null;

	}
	
	/**
	 * 编辑客户追踪记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(_customerTrackEdit);
		
		CustomerTrack customerTrack = new CustomerTrack();

		//要修改的客户追踪记录的autoid
		String autoid = request.getParameter("autoid");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
						
			CustomerTrackService customerTrackService = new CustomerTrackService(conn);
			//根据autoid获得客户追踪记录			
			customerTrack = customerTrackService.getCustomerTrack(autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("CustomerTrack",customerTrack);

		return modelAndView;		
	}
}
