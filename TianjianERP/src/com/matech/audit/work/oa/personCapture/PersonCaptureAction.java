package com.matech.audit.work.oa.personCapture;

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
import com.matech.audit.service.oa.personCapture.personCaptureService;
import com.matech.audit.service.oa.personCapture.model.PersonCaptureTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class PersonCaptureAction extends MultiActionController {
	private final String _strList = "oa/personCapture/List.jsp";

	private final String _strListDo = "/AuditSystem/personCapture.do";

	private final String _AddandEdit = "oa/personCapture/AddandEdit.jsp";

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
		DataGridProperty pp = new DataGridProperty() {
		};

		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PersonCaptureTable pct = new PersonCaptureTable();
		String autoid = "";
		String all = "";
		String sql1 = "";
		all = req.getParameter("all");
		String myUserid = (String) req.getSession().getAttribute("myUserid");
		conn = new DBConnect().getConnect("");
		if ("all".equals(all)) {// 全部人
			sql1 = "select max(autoid) from oa_personcapture where 1=1 ";
		} else {// 具体指定某个人
			sql1 = "select max(autoid) from oa_personcapture where userid="
					+ myUserid;
		}

		ps = conn.prepareStatement(sql1);
		rs = ps.executeQuery();
		if (rs.next()) {
			autoid = rs.getString(1);
		}
		personCaptureService pcs = new personCaptureService(conn);
		pct = pcs.getPerson(autoid);
		modelAndView.addObject("pct", pct);
		modelAndView.addObject("all", all);

		// 必要设置
		pp.setTableID("personcapture");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		
		String capturePerson = asf.showNull(req.getParameter("capturePerson"));
		String captureDepart = asf.showNull(req.getParameter("captureDepart"));
		String sqlWhere = "";
		
		if(!"".equals(capturePerson)) {
			sqlWhere += " and userid like '%"+capturePerson+"%' ";
		}
		
		if(!"".equals(captureDepart)) {
			sqlWhere += " and units like '%"+captureDepart+"%' ";
		}
		
		// sql设置
		String sql = "";

		if ("all".equals(all)) {// 全部人时显示信息
			sql = " select autoid,b.name username,userid,units,starttime,endtime,capturemoney,endcapturetime,booker,checkintime,property \n"
					+ " from oa_personcapture a left join k_user b on a.userid=b.id where 1=1 "+sqlWhere;

		} else {// 具体指定某个人时的显示信息
			sql = " select autoid,userid,units,starttime,endtime,capturemoney,endcapturetime,booker,checkintime,property \n"
					+ " from oa_personcapture where userid=" + myUserid;

		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("人事档案缴费记录");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if ("all".equals(all)) {// 显示全部人时需显示：缴费人
			pp.addColumn("缴费人", "username");
		}

		pp.addColumn("托管单位", "units");
		pp.addColumn("入托时间", "starttime");
		pp.addColumn("到期时间", "endtime");
		pp.addColumn("最后缴费金额", "capturemoney","showMoney");
		pp.addColumn("最后缴费时间", "endcapturetime");
		pp.addColumn("登记人", "booker");
		pp.addColumn("登记日期", "checkintime");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		DbUtil.close(rs);
		DbUtil.close(ps);
		DbUtil.close(conn);
		return modelAndView;
	}

	/**
	 * 添加信息
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
		String all = "";
		String userid = "";
		try {
			UserSession usersession = (UserSession) req.getSession()
					.getAttribute("userSession");
			conn = new DBConnect().getConnect("");
			all = CHF.showNull(req.getParameter("all"));
			if ("all".equals(all)) {
				userid = CHF.showNull(req.getParameter("userid"));
			} else {
				userid = (String) req.getSession().getAttribute("myUserid");
			}

			String units = CHF.showNull(req.getParameter("units"));
			String starttime = CHF.showNull(req.getParameter("starttime"));
			String endtime = CHF.showNull(req.getParameter("endtime"));
			String capturemoney = CHF
					.showNull(req.getParameter("capturemoney"));
			String endcapturetime = CHF.showNull(req
					.getParameter("endcapturetime"));
			PersonCaptureTable pct = new PersonCaptureTable();
			pct.setUserid(userid);
			pct.setUnits(units);
			pct.setStarttime(starttime);
			pct.setEndtime(endtime);
			pct.setCapturemoney(capturemoney);
			pct.setEndcapturetime(endcapturetime);
			pct.setBooker(usersession.getUserName());
			personCaptureService pcs = new personCaptureService(conn);
			pcs.add(pct);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
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

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			personCaptureService pcs = new personCaptureService(conn);
			pcs.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}

	/**
	 * 显示信息记录信息
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
		PersonCaptureTable pct = new PersonCaptureTable();
		String myEdit = "";
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			myEdit = CHF.showNull(req.getParameter("myEdit"));
			all = CHF.showNull(req.getParameter("all"));
			personCaptureService pcs = new personCaptureService(conn);
			pct = pcs.getPerson(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("myEdit", myEdit);
		modelAndView.addObject("pct", pct);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param pct
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			PersonCaptureTable pct) throws Exception {

		Connection conn = null;
		String all = "";
		String userid = "";
		try {
			conn = new DBConnect().getConnect("");
			all = req.getParameter("all");
			if ("all".equals(all)) {
				userid = req.getParameter("userid");
			} else {
				userid = (String) req.getSession().getAttribute("myUserid");
			}
			pct.setUserid(userid);
			personCaptureService pcs = new personCaptureService(conn);
			pcs.update(pct);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}

}
