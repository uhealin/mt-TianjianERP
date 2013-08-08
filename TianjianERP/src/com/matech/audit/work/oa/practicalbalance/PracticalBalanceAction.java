package com.matech.audit.work.oa.practicalbalance;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPlaform.AuditPlaformService;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.oa.practicalbalance.PracticalBalanceService;
import com.matech.audit.service.oa.practicalbalance.model.PracticalBalanceTable;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

/**
 * @author Administrator
 * 
 */

public class PracticalBalanceAction extends MultiActionController {
	private final String _strList = "oa/practicalbalance/List.jsp";

	private final String _strListDo = "/AuditSystem/practicalbalance.do";

	private final String _AddandEdit = "oa/practicalbalance/AddandEdit.jsp";

	private final String _goonAdd = "/AuditSystem/practicalbalance.do?method=edit";

	/**
	 * 跳转到人事档案缴费管理列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion asf = new ASFuntion();
		String cid = "";
		String sqlWhere = "";
		ModelAndView modelAndView = new ModelAndView(_strList);
		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("practicalbalance");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		cid = asf.showNull(req.getParameter("cid"));
		if (!"".equals(cid)) {
			sqlWhere = " and a.cid = " + cid + " ";
		}

		// sql设置
		String sql = "";
		sql = " select a.autoid, a.cid,b.contractname, a.firstparty, a.secondparty, \n"
				+ " a.bargaindate,a.bargaintype,a.bargainmoney,a.invoicenumber, \n"
				+ " a.bargainplan,a.loginname,a.logindate,a.property \n"
				+ " from oa_practicalbalance a left join oa_contract b on a.cid = b.bargainid where 1=1 "
				+ sqlWhere;

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("合同实际结算登记");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		pp.addColumn("合同编号", "cid");
		pp.addColumn("合同名称", "contractname");
		pp.addColumn("甲方", "firstparty");
		pp.addColumn("乙方", "secondparty");
		pp.addColumn("实际结算日期", "bargaindate");
		pp.addColumn("实际结算金额", "bargainmoney");
		pp.addColumn("实际结算方式", "bargaintype");
		pp.addColumn("发票号", "invoicenumber");
		pp.addColumn("对应结算计划", "bargainplan");
		pp.addColumn("登记人", "loginname");
		pp.addColumn("登记时间", "logindate");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}

	/**
	 * 添加信息
	 * 
	 * @param req
	 * @param res
	 * @param pbt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res,
			PracticalBalanceTable pbt) throws Exception {

		Connection conn = null;
		String goon = req.getParameter("goon");
		try {
			conn = new DBConnect().getConnect("");
			UserSession us = (UserSession) req.getSession().getAttribute("userSession");
			PracticalBalanceService pbs = new PracticalBalanceService(conn);
			pbt.setLoginid(us.getUserId());
			pbt.setLoginName(us.getUserName());
			pbs.add(pbt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		if ("1".equals(goon)) {
			res.sendRedirect(_goonAdd + "&cid=" + pbt.getCid());
		} else {
			res.sendRedirect(_strListDo);
		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}

	/**
	 * 删除信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			String opt = CHF.showNull(req.getParameter("opt"));
			PracticalBalanceService pbs = new PracticalBalanceService(conn);
			pbs.del(autoid);
			
			if("1".equals(opt)){
				res.sendRedirect(req.getContextPath() + "/practicalbalance.do?method=nlist");
			}else{
				res.sendRedirect(req.getContextPath() + "/practicalbalance.do");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		return null;
	}

	/**
	 * 修改信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String bargainid = "";
		String sql = "";
		String sql1 = "";
		String firstparty = "";
		String secondparty = "";
		String bargainmoney = "";
		String bargainmoney1 = "";
		String contractmoney = "";

		String tip = "0";
		double bargainmoney2 = 0;
		PracticalBalanceTable pbt = new PracticalBalanceTable();
		try {
			conn = new DBConnect().getConnect("");
			bargainid = CHF.showNull(req.getParameter("cid"));
			// 合同的甲，乙方，金额
			sql = " select armour,second,bargainmoney from oa_contract where bargainid='"+ bargainid + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				firstparty = rs.getString(1);
				secondparty = rs.getString(2);
				bargainmoney = rs.getString(3);
				contractmoney = rs.getString(3);
			}
			// 同一合同的金额汇总
			sql1 = "select sum(bargainmoney) from oa_practicalbalance where cid='"+ bargainid + "' ";
			bargainmoney1 = new DbUtil(conn).queryForString(sql1);
			if ("".equals(bargainmoney) || bargainmoney == null) {
				bargainmoney = "0";
			}
			if ("".equals(bargainmoney1) || bargainmoney1 == null) {
				bargainmoney1 = "0";
			}
			bargainmoney2 = Double.parseDouble(CHF.showNull(bargainmoney))
					- Double.parseDouble(CHF.showNull(bargainmoney1));
			if (bargainmoney2 < 0) {
				tip = "1";
			} else {
				tip = "0";
			}
			if ("1".equals(tip)) {
				bargainmoney2 = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		pbt.setFirstparty(firstparty);
		pbt.setSecondparty(secondparty);
		pbt.setBargainmoney(String.valueOf(bargainmoney2));
		pbt.setCid(bargainid);
		modelAndView.addObject("bargainid", bargainid);
		modelAndView.addObject("pbt", pbt);
		modelAndView.addObject("contractmoney", contractmoney);
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param pbt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			PracticalBalanceTable pbt) throws Exception {

		Connection conn = null;
		String goon = req.getParameter("goon");
		UserSession us = (UserSession) req.getSession().getAttribute("userSession");
		try {
			conn = new DBConnect().getConnect("");
			PracticalBalanceService pbs = new PracticalBalanceService(conn);
			pbt.setLoginid(us.getUserId());
			pbt.setAutoid(req.getParameter("autoid"));
			pbs.update(pbt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		if ("1".equals(goon)) {
			res.sendRedirect(_goonAdd + "&cid=" + pbt.getCid());
		} else {
			res.sendRedirect(_strListDo);
		}

		return new ModelAndView(_strList);
	}

	/**
	 * 显示修改信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit1(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PracticalBalanceTable pbt = null;
		PracticalBalanceService pbs = null;
		String autoid = CHF.showNull(req.getParameter("autoid"));
		String contractmoney = "";
		String sql = "";
		try {
			conn = new DBConnect().getConnect("");
			pbt = new PracticalBalanceTable();
			pbs = new PracticalBalanceService(conn);
			pbt = pbs.getPracticalBalance(autoid);
			sql = " select bargainmoney from oa_contract where bargainid="
					+ pbt.getCid();
			contractmoney = new DbUtil(conn).queryForString(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		modelAndView.addObject("pbt", pbt);
		modelAndView.addObject("bargainid", pbt.getCid());
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("contractmoney", contractmoney);

		return modelAndView;
	}

	/**
	 * 用于判断提交的值是否超过剩余的值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkMoney(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			String bargainid = request.getParameter("bargainid");
			String bargainmoney = request.getParameter("bargainmoney");
			String autoid = request.getParameter("autoid");
			String bargainmoney1 = "";// 合同金额
			String bargainmoney2 = "";// 已付金额
			String bargainmoney3 = "";// 修改的那个金额

			double resultMoney = 0;
			conn = new DBConnect().getConnect("");
			// 合同金额
			String sql = " select bargainmoney from oa_contract where bargainid='"
					+ bargainid + "' ";
			// 实际提交合同的汇总值
			String sql1 = " select sum(bargainmoney) from oa_practicalbalance where cid='"
					+ bargainid + "' ";
			// 修改时自己的金额
			String sql2 = " select bargainmoney from oa_practicalbalance where autoid = '"
					+ autoid + "' ";
			bargainmoney1 = new DbUtil(conn).queryForString(sql);// 合同金额
			bargainmoney2 = new DbUtil(conn).queryForString(sql1);// 已付金额
			bargainmoney3 = new DbUtil(conn).queryForString(sql2);// 修改的那个金额
			
			if("".equals(bargainmoney1) || bargainmoney1 == null) {
				bargainmoney1 = "0";
			}
			
			if("".equals(bargainmoney2) || bargainmoney2 == null) {
				bargainmoney2 = "0";
			}
			
			if("".equals(bargainmoney3) || bargainmoney3 == null) {
				bargainmoney3 = "0";
			}

			if (Double.parseDouble(bargainmoney1)
					- Double.parseDouble(bargainmoney2) < Double
					.parseDouble(bargainmoney)) {
				resultMoney = Double.parseDouble(bargainmoney)
						- (Double.parseDouble(bargainmoney1) - Double
								.parseDouble(bargainmoney2));
			}
			if (!"".equals(autoid)) {
				resultMoney = resultMoney - Double.parseDouble(bargainmoney3);
			}
			System.out.println("resultMoney:" + resultMoney);
			if (resultMoney <= 0) {// 返回0则通过，否则返回超过的值
				resultMoney = 0;
			}

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			PrintWriter out = response.getWriter();

			out.print(resultMoney);
			out.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "读取科目信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	
	/**
	 * 中石油新设计:收款与发票登记
	 * alter table `asdb`.`oa_practicalbalance` add column `projectid` varchar (100)  NULL ;
	 * alter table `asdb`.`oa_practicalbalance` add column `billMoney` decimal (20,2)  NULL;
	 * alter table `asdb`.`oa_practicalbalance` add column `recipient` varchar (100)  NULL ;
	 * http://127.0.0.1:8080/AuditSystem/practicalbalance.do?method=nlist
	 */
	private final String nlist = "oa/practicalbalance/nlist.jsp";
	private final String nedit = "oa/practicalbalance/nedit.jsp";
	
	public ModelAndView nlist(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(nlist);
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();			
			ASFuntion CHF = new ASFuntion();
			String flag =CHF.showNull(request.getParameter("flag")); //页面显示控制
			String projectid =CHF.showNull(request.getParameter("projectid"));
			
			DataGridProperty pp = new DataGridProperty() ;
			pp.setTableID("nlistbalance");
			// 基本设置
			pp.setCustomerId("");
			pp.setPageSize_CH(50);

			// sql设置
			String sql = "select a.*,projectname " +
			"	from oa_practicalbalance a " +
			"	inner join z_project b on a.projectid = b.projectid " +
			"	where 1=1 " +
			"	and a.loginid = '"+userid+"' ${projectid} ";
			if(!"".equals(projectid)){
				sql += " and a.projectid = '"+projectid+"'  ";
			}
			
			// 查询设置
			pp.setPrintEnable(true);
			pp.setPrintTitle("收款与发票登记");

			if(!"window".equals(flag)){
				pp.setInputType("radio");
				pp.addColumn("项目名称", "projectname");
				pp.addColumn("客户名称", "firstparty");
			}
			pp.addColumn("收款日期", "bargaindate");
			pp.addColumn("对应发票号码", "invoicenumber");
			pp.addColumn("收款金额", "bargainmoney","showMoney");
			pp.addColumn("发票金额", "billMoney","showMoney");
			pp.addColumn("发票客户接收人", "recipient");
			pp.addColumn("收款登记人", "loginname");
			pp.addColumn("登记日期", "logindate");
			
			
			pp.addSqlWhere("projectid", " and a.projectid = '${projectid}' ");
			
			pp.setSQL(sql);
			pp.setOrderBy_CH("logindate");
			pp.setDirection_CH("desc");

			
			pp.setWhichFieldIsValue(1);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("loginid", userid);
			modelAndView.addObject("flag", flag);
			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	//新增、修改
	//alter table `asdb`.`oa_practicalbalance` add column `receiptState` varchar (20)  NULL  COMMENT '收款状态' after `recipient`, add column `invoiceState` varchar (20)  NULL  COMMENT '开票状态' after `receiptState`
	public ModelAndView nedit(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(nedit);
		Connection conn = null;
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			ASFuntion CHF = new ASFuntion();
			String autoid =CHF.showNull(request.getParameter("autoid")); //收款与发票登记
			PracticalBalanceTable pbt = new PracticalBalanceTable();
			if(!"".equals(autoid)){
				//修改
				conn = new DBConnect().getConnect("");
				pbt = new PracticalBalanceService(conn).getPracticalBalance(autoid);
			}else{
				pbt.setLogindate(CHF.getCurrentDate() + " " + CHF.getCurrentTime());
				pbt.setLoginid(userSession.getUserId());
				pbt.setLoginName(userSession.getUserName());
			}
			modelAndView.addObject("pbt", pbt);
			modelAndView.addObject("autoid", autoid);
			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	//保存
	public ModelAndView nsave(HttpServletRequest request, HttpServletResponse response)throws Exception {
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			String autoid =CHF.showNull(request.getParameter("autoid"));
			String loginid =CHF.showNull(request.getParameter("loginid"));
			String projectid =CHF.showNull(request.getParameter("projectid"));
			String bargaindate =CHF.showNull(request.getParameter("bargaindate"));
			String bargainmoney =CHF.showNull(request.getParameter("bargainmoney"));
			String property =CHF.showNull(request.getParameter("property"));
			String invoicenumber =CHF.showNull(request.getParameter("invoicenumber"));
			String billMoney =CHF.showNull(request.getParameter("billMoney"));
			String recipient =CHF.showNull(request.getParameter("recipient"));
			String loginName =CHF.showNull(request.getParameter("loginName"));
			String logindate =CHF.showNull(request.getParameter("logindate"));
			
			String receiptState =CHF.showNull(request.getParameter("receiptState"));//收款状态
			String invoiceState =CHF.showNull(request.getParameter("invoiceState"));//开票状态
			
			conn = new DBConnect().getConnect("");
			
			Project project = new ProjectService(conn).getProjectById(projectid); //项目信息
			Customer customer = new CustomerService(conn).getCustomer(project.getCustomerId()); //客户信息

			PracticalBalanceTable pbt = new PracticalBalanceTable();
			pbt.setAutoid(autoid);
			pbt.setLoginid(loginid);
			pbt.setProjectid(projectid);
			pbt.setBargaindate(bargaindate);
			pbt.setBargainmoney(bargainmoney);
			pbt.setProperty(property);
			pbt.setInvoicenumber(invoicenumber);
			pbt.setBillMoney(billMoney);
			pbt.setRecipient(recipient);
			pbt.setLoginName(loginName);
			pbt.setLogindate(logindate);
			
			pbt.setReceiptState(receiptState);
			pbt.setInvoiceState(invoiceState);
			
			//客户
			pbt.setFirstparty(customer.getDepartName());
			pbt.setFirstpartyid(customer.getDepartId());
			
			PracticalBalanceService pbs = new PracticalBalanceService(conn);
			if("".equals(autoid)){
				//新增
				pbs.add(pbt);
			}else{
				//修改
				pbs.update(pbt);
			}
			response.sendRedirect(request.getContextPath() + "/practicalbalance.do?method=nlist");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	//得到合同金额
	public ModelAndView price(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		Connection conn=null; 
		try {
			ASFuntion CHF = new ASFuntion();
			String projectid =CHF.showNull(request.getParameter("projectid"));
			
			conn=new DBConnect().getConnect("");
			AuditPlaformService aps = new AuditPlaformService(conn);
			String price = CHF.showNull(aps.getProjectext(projectid,"price"));
			if("".equals(price)) {
				price = "0.00";
			}else{
				price = CHF.showMoney3(price);
			}
			
			out.write(price);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			out.close();
			DbUtil.close(conn);
		}
		return null;
	}
	
}
