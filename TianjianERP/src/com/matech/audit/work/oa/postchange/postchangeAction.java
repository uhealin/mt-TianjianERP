package com.matech.audit.work.oa.postchange;

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
import com.matech.audit.service.oa.postchange.postchangeService;
import com.matech.audit.service.oa.postchange.model.postchangeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class postchangeAction extends MultiActionController {
	private final String _strList = "oa/postchange/List.jsp";

	private final String _strListDo = "/AuditSystem/postchange.do";

	private final String _AddandEdit = "oa/postchange/AddandEdit.jsp";

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
		
		ModelAndView modelAndView = new ModelAndView(_strList);
		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("postchange");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);
		
		String worker = asf.showNull(req.getParameter("worker")); 
		String workDepart = asf.showNull(req.getParameter("workDepart")); 
		String sqlWhere = "";
		
		if(!"".equals(worker)) {
			sqlWhere +=" and userid like '%"+worker+"%' ";
		}
		
		if(!"".equals(workDepart)) {
			sqlWhere +=" and (formerlypost like '%"+workDepart+"%' or adjustpost  like '%"+workDepart+"%') ";
		}

		// sql设置
		String all = req.getParameter("all");
		String sql = "";
		String myUserid = (String) req.getSession().getAttribute("myUserid");
		if ("all".equals(all)) {// 显示所有人时的信息
			sql = " select a.*," +
				"	b.name username,c.departname as fdepartname,d.departname as adepartname " +
				"	from  oa_postchange a " +
				"	left join k_user b on a.userid=b.id " +
				"	left join k_department c on a.fdepartmentid=c.autoid " +
				"	left join k_department d on a.adepartmentid=d.autoid " +
				"	where 1=1 "+sqlWhere;

		} else {// 显示具体某个人的信息
			sql = " select a.*," +
					"	b.name username,c.departname as fdepartname,d.departname as adepartname " +
					"	from  oa_postchange a " +
					"	left join k_user b on a.userid=b.id " +
					"	left join k_department c on a.fdepartmentid=c.autoid " +
					"	left join k_department d on a.adepartmentid=d.autoid " +
					"	where userid=" + myUserid;
		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("工作岗位变动");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		if ("all".equals(all)) {// 显示所有人时，需显示姓名
			pp.addColumn("姓名", "username");
		}
		pp.addColumn("原部门", "fdepartname");
		pp.addColumn("原岗位", "formerlypost");
		pp.addColumn("调整后部门", "adepartname");
		pp.addColumn("调整后岗位", "adjustpost");
		pp.addColumn("调整时间", "starttime");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 添加信息
	 * 
	 * @param req
	 * @param res
	 * @param pt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res,
			postchangeTable pt) throws Exception {

		Connection conn = null;
		String myUserid = "";
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			postchangeService pcs = new postchangeService(conn);
			all = req.getParameter("all");
			if ("all".equals(all)) {// 所有人
				myUserid = req.getParameter("userid");
			} else {// 具体某个人
				myUserid = (String) req.getSession().getAttribute("myUserid");
			}

			if (myUserid != null) {
				pt.setUserid(myUserid);
			}

			pcs.add(pt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
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
		ModelAndView modelAndView = new ModelAndView(_strList);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			postchangeService pcs = new postchangeService(conn);
			pcs.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("all", all);
		return modelAndView;
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
		String autoid = "";
		String all = "";
		postchangeTable pt = new postchangeTable();
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			postchangeService pcs = new postchangeService(conn);
			pt = pcs.getPostchange(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("pt", pt);
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param pt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			postchangeTable pt) throws Exception {

		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			all = req.getParameter("all");
			if ("all".equals(all)) {// 显示所有人时
				pt.setUserid(req.getParameter("userid"));
			} else {// 显示某个具体的人时
				pt
						.setUserid((String) req.getSession().getAttribute(
								"myUserid"));
			}
			postchangeService pcs = new postchangeService(conn);
			pcs.update(pt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}

	/**
	 * 添加的时候前岗位为现在岗位
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addDefault(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String autoid = "";
		String all = "";
		String sql = "";
		String userid = "";
		postchangeTable pt = new postchangeTable();
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			if (!"all".equals(all)) {
				userid = (String) req.getSession().getAttribute("myUserid");
				sql = "select adjustpost,current_date,adepartmentid from oa_postchange \n"
						+ " where userid=" + userid + " \n"
						+ " order by autoid desc \n" + " limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					pt.setFormerlypost(rs.getString(1));
					pt.setStarttime(rs.getString(2));
					pt.setFdepartmentid(rs.getString(3));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("pt", pt);
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("all", all);
		return modelAndView;
	}

}
