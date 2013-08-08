package com.matech.audit.work.oa.insurancecircs;

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
import com.matech.audit.service.oa.insurancecircs.InsurancecircsService;
import com.matech.audit.service.oa.insurancecircs.model.InsurancecircsTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class InsurancecircsAction extends MultiActionController {
	private final String _strList = "oa/insurancecircs/List.jsp";

	private final String _strListDo = "/AuditSystem/insurancecircs.do";

	private final String _AddandEdit = "oa/insurancecircs/AddandEdit.jsp";

	/**
	 * 跳转到员工培训情况管理列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion asf = new ASFuntion();
		
		DataGridProperty pp = new DataGridProperty() {
		};

		ModelAndView modelAndView = new ModelAndView(_strList);

		// 必要设置
		ASFuntion CHF = new ASFuntion();

		String autoid = CHF.showNull(req.getParameter("autoid1"));
		String all = CHF.showNull(req.getParameter("all"));
		String myUserid = (String) req.getSession().getAttribute("myUserid");

		pp.setTableID("insurancecircs");
		// 基本设置

		String sql = "";
		
		String insurancePerson = asf.showNull(req.getParameter("insurancePerson"));
		String insuranceType = asf.showNull(req.getParameter("insuranceType"));
		String insuranceDepart = asf.showNull(req.getParameter("insuranceDepart"));
		String sqlWhere = "";
		
		if(!"".equals(insurancePerson)) {//缴费人
			sqlWhere += " and userid like '%"+insurancePerson+"%' ";
		}
		
		if(!"".equals(insuranceType)) {//险种
			sqlWhere += " and insurancetype like '%"+insuranceType+"%' ";
		}
		
		if(!"".equals(insuranceDepart)) {//托管单位
			sqlWhere += " and trusteeshipunit like '%"+insuranceDepart+"%' ";
		}

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		if ("".equals(autoid)) {// 显示指定险种的购买保险记录信息
			if ("all".equals(all)) {// 显示所有人员的信息
				sql = " select autoid,b.name username,userid,insurancetype,trusteeshipunit,startdate,enddate,"
						+ " finallymoney,finallydate,checkinperson,checkindate,property  from oa_insurancecircs a "
						+ " left join k_user b on a.userid=b.id "
						+ " where 1=1 "+sqlWhere;

			} else {// 显示指定人员的信息
				sql = " select autoid,userid,insurancetype,trusteeshipunit,startdate,enddate,"
						+ " finallymoney,finallydate,checkinperson,checkindate,property  from oa_insurancecircs "
						+ " where userid=" + myUserid;

			}

		} else {// 显示所有险种的购买保险记录信息
			if ("all".equals(all)) {// 显示所有人员的信息
				sql = " select autoid,b.name username,userid,insurancetype,trusteeshipunit,startdate,enddate,"
						+ " finallymoney,finallydate,checkinperson,checkindate,property  from oa_insurancecircs a "
						+ " left join k_user b on a.userid=b.id "
						+ " where 1=1 "+sqlWhere
						+ " and insurancetype = (select ctype from oa_insurancetype where autoid= "
						+ autoid + ")";

			} else {// 显示指定人员的信息
				sql = " select autoid,userid,insurancetype,trusteeshipunit,startdate,enddate,"
						+ " finallymoney,finallydate,checkinperson,checkindate,property  from oa_insurancecircs "
						+ " where userid="
						+ myUserid
						+ " "
						+ "and insurancetype = (select ctype from oa_insurancetype where autoid= "
						+ autoid + ")";

			}

		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("员工保险情况记录");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if ("all".equals(all)) {
			pp.addColumn("缴费人", "username");
		}

		pp.addColumn("保险类型", "insurancetype");
		pp.addColumn("托管单位", "trusteeshipunit");
		pp.addColumn("入保时间", "startdate");
		pp.addColumn("到期时间", "enddate");
		pp.addColumn("最后缴费金额", "finallymoney","showMoney");
		pp.addColumn("最后缴费时间", "finallydate");
		pp.addColumn("登记人", "checkinperson");
		pp.addColumn("登记日期", "checkindate");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		modelAndView.addObject("autoid1", autoid);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 添加记录信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "";
		String userid = "";
		String insurancetype = "";
		String trusteeshipunit = "";
		String startdate = "";
		String enddate = "";
		String finallymoney = "";
		String finallydate = "";
		String myEdit = "";
		String autoid1 = "";
		String all = "";

		try {
			myEdit = CHF.showNull(req.getParameter("myedit"));
			autoid = CHF.showNull(req.getParameter("autoid"));
			autoid1 = CHF.showNull(req.getParameter("autoid1"));
			System.out.println("autoid1:" + autoid1);
			all = CHF.showNull(req.getParameter("all"));
			if ("all".equals(all)) {// 显示所有人的信息时，userid从页面上取
				userid = CHF.showNull(req.getParameter("userid"));
			} else {// 显示指定人的信息时,userid从Session取
				userid = (String) req.getSession().getAttribute("myUserid");
			}

			insurancetype = CHF.showNull(req.getParameter("insurancetype"));
			trusteeshipunit = CHF.showNull(req.getParameter("trusteeshipunit"));
			startdate = CHF.showNull(req.getParameter("startdate"));
			enddate = CHF.showNull(req.getParameter("enddate"));
			finallymoney = CHF.showNull(req.getParameter("finallymoney"));
			finallydate = CHF.showNull(req.getParameter("finallydate"));

			InsurancecircsTable it = new InsurancecircsTable();
			UserSession usersession = (UserSession) req.getSession()
					.getAttribute("userSession");
			conn = new DBConnect().getConnect("");

			it.setUserid(userid);
			it.setInsurancetype(insurancetype);
			it.setTrusteeshipunit(trusteeshipunit);
			it.setStartdate(startdate);
			it.setEnddate(enddate);
			it.setFinallymoney(finallymoney);
			it.setFinallydate(finallydate);
			it.setCheckinperson(usersession.getUserName());
			InsurancecircsService is = new InsurancecircsService(conn);
			if ("".equals(myEdit)) {// 新增
				is.add(it);
			} else {// 修改
				it.setAutoid(Integer.parseInt(autoid));
				is.update(it);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect("/AuditSystem/insurancecircs.do?autoid1=" + autoid1
				+ "&all=" + all);
		return new ModelAndView(_strList);
	}

	/**
	 * 删除
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strList);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			InsurancecircsService is = new InsurancecircsService(conn);
			is.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 显示修改信息
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
		InsurancecircsTable it = new InsurancecircsTable();
		String autoid1 = "";
		String myedit = "";
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			autoid1 = CHF.showNull(req.getParameter("autoid1"));
			myedit = CHF.showNull(req.getParameter("myedit"));
			all = CHF.showNull(req.getParameter("all"));
			InsurancecircsService is = new InsurancecircsService(conn);
			it = is.getInsurancecircs(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("it", it);
		modelAndView.addObject("autoid1", autoid1);
		modelAndView.addObject("myedit", myedit);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param it
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			InsurancecircsTable it) throws Exception {

		Connection conn = null;
		String all = "";
		String userid = "";
		try {
			conn = new DBConnect().getConnect("");
			all = req.getParameter("all");
			if ("all".equals(all)) {// 显示所有人的时候，userid从页面上取
				userid = req.getParameter("userid");
			} else {// 显示具体某个人的时候，userid从Session中取
				userid = (String) req.getSession().getAttribute("myUserid");
			}
			InsurancecircsService is = new InsurancecircsService(conn);
			it.setUserid(userid);
			is.update(it);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}

	/**
	 * 显示指定险种的或者所有险种的保险记录
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView laterOn(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		InsurancecircsTable it = new InsurancecircsTable();
		String autoid1 = "";
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			autoid1 = CHF.showNull(req.getParameter("autoid1"));// 指定险种的autoid
			all = CHF.showNull(req.getParameter("all"));
			String myUserid = (String) req.getSession()
					.getAttribute("myUserid");

			String myautoid = "";

			String mysql = "";
			if (!"".equals(autoid1)) {// 显示指定险种的记录
				if ("all".equals(all)) {// 显示所有人的保险记录
					mysql = "select autoid from oa_insurancecircs \n"
							+ " where insurancetype = \n"
							+ " (select ctype from oa_insurancetype \n"
							+ " where autoid = '" + autoid1 + "') \n"
							// +" and userid="+myUserid+" \n"
							+ " order by autoid desc \n" + " limit 1";
				} else {// 显示具体某个人的保险记录
					mysql = "select autoid from oa_insurancecircs \n"
							+ " where insurancetype = \n"
							+ " (select ctype from oa_insurancetype \n"
							+ " where autoid = '" + autoid1 + "') \n"
							+ " and userid=" + myUserid + " \n"
							+ " order by autoid desc \n" + " limit 1";
				}
			} else {// 显示所有险种的保险记录
				if ("all".equals(all)) {// 显示所有人的保险记录
					mysql = "select max(autoid) from oa_insurancecircs ";
				} else {// 显示具体某个人的保险记录
					mysql = "select max(autoid) from oa_insurancecircs \n"
							+ " where userid=" + myUserid + " \n";
				}

			}

			ps = conn.prepareStatement(mysql);
			rs = ps.executeQuery();
			if (rs.next()) {
				myautoid = rs.getString(1);
			}

			InsurancecircsService is = new InsurancecircsService(conn);
			it = is.getInsurancecircs(myautoid);

			modelAndView.addObject("it", it);
			modelAndView.addObject("all", all);
			modelAndView.addObject("autoid1", autoid1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);

		}

		return modelAndView;
	}

}
