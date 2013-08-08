package com.matech.audit.work.oa.bargainbalance;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.bargainbalance.BargainBalanceService;
import com.matech.audit.service.oa.bargainbalance.model.BargainBalanceTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class BargainBalanceAction extends MultiActionController {

	private String _Blan = "/oa/bargainbalance/list.jsp";

	private String _BlanEdit = "/oa/bargainbalance/balanceadd.jsp";

	ASFuntion CHF = new ASFuntion();

	/**
	 * 显示列表
	 * 
	 * @param request
	 * @param rssponse
	 * @return
	 * @throws Exception
	 */

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse rssponse) throws Exception {

		ModelAndView modelandview = new ModelAndView(_Blan);

		HttpSession session = request.getSession();

		DataGridProperty pp = new DataGridProperty();

		pp.setCustomerId("");

		pp.setTableID("bargainbalance");

		pp.setInputType("radio");

		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		String bargainid = "";
		String sqlWhere = "";
		bargainid = CHF.showNull(request.getParameter("bargainid"));
		if (!"".equals(bargainid)) {
			sqlWhere = " and a.bargainid = " + bargainid;
		}
		 
		/*
		 * 中天粤
		pp.addColumn("合同编号", "bargainid");
		pp.addColumn("合同名称", "contractname");
		pp.addColumn("甲方", "firstparty");
		pp.addColumn("乙方", "secondparty");
		pp.addColumn("计划结算日期", "plandate");
		pp.addColumn("计划计算金额", "planmoney");
		pp.addColumn("计划结算方式", "planfashion");
		pp.addColumn("结算条件", "plancondition");
		pp.addColumn("登记人", "checkinname");
		pp.addColumn("登记时间", "checkintime");

		String sql = " select a.autoid,a.bargainid,b.contractname,a.firstparty,a.secondparty,a.plandate,a.planmoney,a.planfashion,a.plancondition,a.checkinname,a.checkintime "
				+ " from oa_bargainbalance a left join oa_contract b on a.bargainid = b.bargainid where 1=1 "
				+ sqlWhere;*/
		
		//大华
		pp.addColumn("项目编号", "bargainid");
		pp.addColumn("项目名称", "projectname");
		//pp.addColumn("甲方", "firstparty");
		//pp.addColumn("乙方", "secondparty");
		pp.addColumn("计划结算日期", "plandate");
		pp.addColumn("计划计算金额", "planmoney");
		pp.addColumn("计划结算方式", "planfashion");
		pp.addColumn("结算条件", "plancondition");
		pp.addColumn("登记人", "checkinname");
		pp.addColumn("登记时间", "checkintime");

		String sql = " select a.autoid,a.bargainid,b.projectname,a.firstparty,a.secondparty,a.plandate,a.planmoney,a.planfashion,a.plancondition,a.checkinname,a.checkintime "
					+ " from oa_bargainbalance a left join z_project b on a.bargainid = b.projectId where 1=1 "
					+ sqlWhere;
		pp.setSQL(sql);

		// 按时间和金额排序
		pp.setOrderBy_CH("plandate,planmoney");
		pp.setDirection("desc,desc");

		session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return modelandview;
	}

	/**
	 * 增加记录
	 * 
	 * @param request
	 * @param response
	 * @param bbt
	 * @return
	 * @throws Exception
	 */

	public ModelAndView addblan(HttpServletRequest request,
			HttpServletResponse response, BargainBalanceTable bbt)
			throws Exception {

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		Connection conn = null;

		ModelAndView modelAndView = new ModelAndView(_Blan);

		// 用于判断是否提交后继续添加，为1为继续增加
		String goon = request.getParameter("goon");
		try {

			conn = new DBConnect().getConnect("");
			BargainBalanceService bbs = new BargainBalanceService(conn);
			bbt.setCheckinname(userSession.getUserName());
			bbt.setCheckId(userSession.getUserId());
			bbt.setCheckintime(CHF.getCurrentDate());
			bbs.addblan(bbt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}
		if ("1".equals(goon)) {// 确定并追加
			response.sendRedirect("/AuditSystem/bargainbalance.do?method=edit&bargainid="+bbt.getBargainid());
		} else {// 确定
			response.sendRedirect("/AuditSystem/bargainbalance.do");
		}

		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param request
	 * @param response
	 * @param blan
	 * @return
	 * @throws Exception
	 */

	public ModelAndView updateblan(HttpServletRequest request,
			HttpServletResponse response, BargainBalanceTable blan)
			throws Exception {

		Connection conn = null;

		String goon = "";
		try {

			conn = new DBConnect().getConnect("");

			goon = request.getParameter("goon");
			BargainBalanceService bbs = new BargainBalanceService(conn);

			String autoid = request.getParameter("autoid");

			bbs.updateblan(blan, autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}
		if ("1".equals(goon)) {// 确定并追加
			response.sendRedirect("/AuditSystem/bargainbalance.do?method=edit&bargainid="+blan.getBargainid());
		} else {// 确定
			response.sendRedirect("/AuditSystem/bargainbalance.do");
		}

		return null;
	}

	/**
	 * 显示修改信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public ModelAndView exitblan(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_BlanEdit);

		BargainBalanceTable bbt = new BargainBalanceTable();

		// 要修改的合同结算计划的autoid
		String autoid = request.getParameter("autoid");// 获取前台传过来的值 autoid

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String planmoney = "";
		String contractmoney = "";
		String sql = "";
		try {
			conn = new DBConnect().getConnect("");
			BargainBalanceService bbs = new BargainBalanceService(conn);
			bbt = bbs.getBargainBalance(autoid);
			sql = "select bargainmoney from oa_contract where bargainid="+ bbt.getBargainid();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				contractmoney = rs.getString(1);
			}
			// 显示合同金额
//			contractmoney = new DbUtil(conn).queryForString(sql);
			
			sql = " SELECT a.businessCost-IFNULL(b.gatheringSum,0) AS gatheringMoney  FROM `z_projectbusiness` a \n"
				  +" LEFT JOIN  ( \n"
				  +"	 SELECT SUM(receiceMoney) AS gatheringSum,projectId FROM k_getFunds WHERE projectId='"+bbt.getBargainid()+"' GROUP BY projectId \n"
				  +"  )b ON a.projectId = b.projectId \n"
				  +"   WHERE a.projectId='"+bbt.getBargainid()+"'";
			planmoney = new DbUtil(conn).queryForString(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		modelAndView.addObject("autoid", autoid);// 自增编号
		modelAndView.addObject("planmoney", planmoney);// 合同金额
		modelAndView.addObject("bbt", bbt);// BargainBalanceTable对像
		modelAndView.addObject("bargainid", bbt.getBargainid());// 合同编号
		modelAndView.addObject("contractmoney", contractmoney);// 合同金额

		return modelAndView;
	}

	/**
	 * 删除记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public ModelAndView removeblan(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = request.getParameter("autoid");

			BargainBalanceService bbs = new BargainBalanceService(conn);
			bbs.removeblan(autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/bargainbalance.do");

		return null;
	}

	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_BlanEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String bargainid = "";
		String sql = "";
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String firstparty = "";
		String secondparty = "";
		String bargainmoney = "";
		String bargainmoney1 = "";
		String contractmoney = "";
		String planmoney ="";
		String tip = "0";
		double bargainmoney2 = 0;
		BargainBalanceTable bbt = new BargainBalanceTable();
		try {
			conn = new DBConnect().getConnect("");
			bargainid = CHF.showNull(req.getParameter("bargainid"));
			// 查询合同的甲方，乙方和合同金额
			sql = " select armour,second,bargainmoney from oa_contract where bargainid='"
					+ bargainid + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				firstparty = rs.getString(1);// 甲方
				secondparty = rs.getString(2);// 乙方
				bargainmoney = rs.getString(3);// 合同金额
			}
			// 汇总指定合同编号的合同结算计划的金额
			sql1 = "select sum(planmoney) from oa_bargainbalance where bargainid='"
					+ bargainid + "' ";
			// 合同金额
			sql2 = " select bargainmoney from oa_contract where bargainid='"
					+ bargainid + "'";
			sql3 = " SELECT a.businessCost-IFNULL(b.gatheringSum,0) AS gatheringMoney  FROM `z_projectbusiness` a \n"
			  +" LEFT JOIN  ( \n"
			  +"	 SELECT SUM(receiceMoney) AS gatheringSum,projectId FROM k_getFunds WHERE projectId='"+bargainid+"' GROUP BY projectId \n"
			  +"  )b ON a.projectId = b.projectId \n"
			  +"   WHERE a.projectId='"+bargainid+"'";
			bargainmoney1 = new DbUtil(conn).queryForString(sql1);// 汇总指定合同编号的合同结算计划的金额
			contractmoney = new DbUtil(conn).queryForString(sql2);// 合同金额
			planmoney = new DbUtil(conn).queryForString(sql3);
			if ("".equals(bargainmoney) || bargainmoney == null) {
				bargainmoney = "0";
			}
			if ("".equals(bargainmoney1) || bargainmoney1 == null) {
				bargainmoney1 = "0";
			}
			bargainmoney2 = Double.parseDouble(CHF.showNull(bargainmoney))
					- Double.parseDouble(CHF.showNull(bargainmoney1));
			if (bargainmoney2 < 0) {// 如果剩余金额小小0则显示0，否同原样显示
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
		bbt.setFirstparty(firstparty);
		bbt.setSecondparty(secondparty);
		bbt.setPlanmoney(String.valueOf(bargainmoney2));
		bbt.setBargainid(bargainid);
		modelAndView.addObject("bargainid", bargainid);// 合同编号
		modelAndView.addObject("bbt", bbt);// 对象
		modelAndView.addObject("contractmoney", contractmoney);// 合同金额
		modelAndView.addObject("planmoney", planmoney);
		return modelAndView;
	}

	/**
	 * 根据项目编号查询收款
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView loadMoney(HttpServletRequest req, HttpServletResponse res)
	throws Exception {
		ModelAndView modelAndView = new ModelAndView(_BlanEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String bargainid = "";
		String sql = "";
		String planmoney = "";
		String contractmoney = "";
		conn = new DBConnect().getConnect("");
		bargainid = CHF.showNull(req.getParameter("projectId"));
		// 查询合同的甲方，乙方和合同金额
		sql = " SELECT a.businessCost-IFNULL(b.gatheringSum,0) AS gatheringMoney  FROM `z_projectbusiness` a \n"
			  +" LEFT JOIN  ( \n"
			  +"	 SELECT SUM(receiceMoney) AS gatheringSum,projectId FROM k_getFunds WHERE projectId='"+bargainid+"' GROUP BY projectId \n"
			  +"  )b ON a.projectId = b.projectId \n"
			  +"   WHERE a.projectId='"+bargainid+"'";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		if (rs.next()) {
			planmoney = rs.getString("gatheringMoney");
		}
			 
		modelAndView.addObject("bargainid", bargainid);// 合同编号
		modelAndView.addObject("planmoney", planmoney);// 业务约定收费金额
		modelAndView.addObject("contractmoney", contractmoney);// 合同金额
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
			//合同金额
			String sql = " select bargainmoney from oa_contract where bargainid='"
					+ bargainid + "' ";
			//同一合同提交的汇总金额
			String sql1 = " select sum(planmoney) from oa_bargainbalance where bargainid='"
					+ bargainid + "' ";
			//修改时自己的金额
			String sql2 = " select planmoney from oa_bargainbalance where autoid = '"
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
					.parseDouble(bargainmoney)) {// 计算金额
				resultMoney = Double.parseDouble(bargainmoney)
						- (Double.parseDouble(bargainmoney1) - Double
								.parseDouble(bargainmoney2));
			}
			if (!"".equals(autoid)) {// 修改的时候，要减去本身的金额
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

}
