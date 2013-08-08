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
import com.matech.audit.service.customer.CustomerContractService;
import com.matech.audit.service.customer.model.CustomerContract;
import com.matech.framework.pub.db.DbUtil;

public class CustomerContractAction extends MultiActionController {
	private final String _customerContractList = "/Customer/CustomerContractList.jsp";
	private final String _customerContractEdit = "/Customer/CustomerContractAdd.jsp";
	
	
	/**
	 * 显示客户合同记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_customerContractList);
		HttpSession session = request.getSession();
		
		//客户编号
		String customerId = (String)session.getAttribute("customer");

		DataGridProperty dgProperty = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {
			}
		};

		// 客户合同记录
		dgProperty.setTableID("customerContractList");
		dgProperty.setInputType("radio");

		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintVerTical(false);

		// 打印的列宽
		dgProperty.setPrintColumnWidth("15,15,15,15,15,15,50,15,15");
		// 打印的表名
		dgProperty.setPrintTitle("客户合同记录列表");

		dgProperty.addColumn("合同编号", "contractId");
		dgProperty.addColumn("合同人", "contractMan");
		dgProperty.addColumn("签定日期", "contractDate");
		dgProperty.addColumn("基本工资", "salory");
		dgProperty.addColumn("有效期限", "validTime");
		dgProperty.addColumn("合同附件", "contractAdjunct");
		dgProperty.addColumn("备注", "mome");
		dgProperty.addColumn("记录人", "recoder");
		dgProperty.addColumn("记录时间", "recodeTime");

		String sql = "select * from oa_customercontract where customerid=" + customerId;

		dgProperty.setSQL(sql.toString());

		dgProperty.setOrderBy_CH("autoid");
		dgProperty.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		return modelAndView;
	}

	/**
	 * 增加客户合同记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addCustomerContract(HttpServletRequest request,
			HttpServletResponse response,CustomerContract customerContract) throws Exception {

		Connection conn = null;
		
		HttpSession session = request.getSession();
		
		//客户编号
		String customerId = (String)session.getAttribute("customer");
		
		try {
			 conn = new DBConnect().getConnect("");
						
			 CustomerContractService customerContractService = new CustomerContractService(conn);
			 //增加客户合同记录
			 customerContractService.addLatencyCustomer(customerContract,customerId);		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/customerContract.do");
				
		return null;
	}

	/**
	 * 修改客户合同记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateCustomerContract(HttpServletRequest request,
			HttpServletResponse response,CustomerContract customerContract) throws Exception {
		
		Connection conn = null;
	
		try {
			conn = new DBConnect().getConnect("");
			//要修改的客户合同记录的autoid
			String autoid = request.getParameter("autoid");
						
			CustomerContractService customerContractService = new CustomerContractService(conn);
			//修改客户合同记录
			customerContractService.updateLatencyCustomer(customerContract, autoid);		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		response.sendRedirect("/AuditSystem/customerContract.do");
		
		return null;
	}

	/**
	 * 删除客户合同记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeCustomerContract(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect("");
			//要删除的客户合同记录的autoid
			String autoid = request.getParameter("autoid");
						
			CustomerContractService customerContractService = new CustomerContractService(conn);
			//删除客户合同记录
			customerContractService.removeLatencyCustomer(autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
				
		response.sendRedirect("/AuditSystem/customerContract.do");
		
		return null;
	}

	/**
	 * 编辑客户合同记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitCustomerContract(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(_customerContractEdit);
			
		CustomerContract customerContract = new CustomerContract();

		//要修改的客户合同记录的autoid
		String autoid = request.getParameter("autoid");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
						
			CustomerContractService customerContractService = new CustomerContractService(conn);
			//根据contractId获得客户合同记录			
			customerContract = customerContractService.getCustomerContract(autoid);			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("CustomerContract",customerContract);

		return modelAndView;		
	}
}
