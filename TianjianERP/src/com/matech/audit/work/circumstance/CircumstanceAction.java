package com.matech.audit.work.circumstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.del.DelEncrypt;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.circumstance.CircumstanceService;
import com.matech.audit.service.circumstance.model.Circumstance;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.ReadSysProperty;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class CircumstanceAction extends MultiActionController {

	private String _circumstance = "/circumstance/Circumstance.jsp";
	private String _circumstanceEdit = "/circumstance/Circumstanceadd.jsp";

	ASFuntion CHF = new ASFuntion();

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(_circumstance);

		HttpSession session = request.getSession();

		DataGridProperty pp = new DataGridProperty();

		pp.setCustomerId("");

		pp.setTableID("CircumstanceList");

		pp.setInputType("radio");

		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		pp.setPrintColumnWidth("20,20,20,20,20");
		pp.setPrintTitle("环境设置");

		pp.addColumn("参数名称 ", "sname");
		pp.addColumn("参数设定", "svalue");
		//pp.addColumn("222", "sautoid");
		//pp.addColumn("111", "multiselect");
		pp.addColumn("参数用途说明", "smemo");
		pp.addColumn("最后修改人", "upuser");
		pp.addColumn("最后修改时间", "uptime");

		String sql = "select autoid,sname,svalue,sautoid,multiselect,smemo,upuser,uptime from s_config where property='user'";

		pp.setSQL(sql);

		pp.setOrderBy_CH("autoid");
		pp.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return modelandview;

	}

	//修改

	public ModelAndView updatecircumstance(HttpServletRequest request,
			HttpServletResponse response, Circumstance circumstance)
			throws Exception {

		HttpSession session = request.getSession();
		UserSession us = (UserSession) session.getAttribute("userSession");
		Connection conn = null;

		try {

			conn = new DBConnect().getConnect("");

			CircumstanceService cs = new CircumstanceService(conn);

			String autoid = request.getParameter("autoid");

			circumstance.setUpuser(us.getUserName());

			circumstance.setUptime(CHF.getCurrentDate());

			cs.updatecircumstance(circumstance, autoid);

			//刷新后台数据
			refresh(request,response);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}



		response.sendRedirect(request.getContextPath()+"/circumstance.do");

		return null;
	}

	//获取

	public ModelAndView exitcircumstance(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_circumstanceEdit);

		Circumstance circumstance = new Circumstance();

		//要修改的劳动合同的autoid
		String autoid = request.getParameter("autoid");//获取前台传过来的值　autoid

		PreparedStatement ps = null;
		ResultSet rs = null;

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			String sql = "select autoid,sname,svalue,sautoid,multiselect,smemo,upuser,uptime from s_config where autoid='"
					+ autoid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {

				circumstance.setSname(CHF.showNull(rs.getString("sname")));
				circumstance.setSvalue(CHF.showNull(rs.getString("svalue")));

				circumstance.setSautoid(CHF.showNull(rs.getString("sautoid")));

				circumstance.setMultiselect(CHF.showNull(rs
						.getString("multiselect")));

				circumstance.setSmemo(CHF.showNull(rs.getString("smemo")));

				circumstance.setUpuser(CHF.showNull(rs.getString("upuser")));

				circumstance.setUptime(CHF.showNull(rs.getString("uptime")));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		modelAndView.addObject("autoid", autoid);//传值
		modelAndView.addObject("circumstance", circumstance);//同上

		return modelAndView;

	}

	/**
	 * 刷新环境变量
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView refresh(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {
			ReadSysProperty.readFromDB(UTILSysProperty.SysProperty);
			
			ReadSysProperty.createOfficeIni() ;

			Iterator it = UTILSysProperty.SysProperty.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				Debug.print(key + ":" + value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
