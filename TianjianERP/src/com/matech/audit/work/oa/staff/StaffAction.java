package com.matech.audit.work.oa.staff;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.staff.staffService;
import com.matech.audit.service.oa.staff.model.StaffTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class StaffAction extends MultiActionController {
	private final String _strList = "oa/staff/List.jsp";

	private final String _strListDo = "/AuditSystem/staff.do";

	private final String _AddandEdit = "oa/staff/AddandEdit.jsp";

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

		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("staff");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String sql = " select 	autoid, cname, sex, department, post,mobilephone,email, \n"
				+ " identitycard,  \n"
				+ " nation,  \n"
				+ " familytelephone, \n"
				+ " officetelephone,  \n"
				+ " birthday,  \n"
				+ " marriage,  \n"
				+ " workstarttime, \n"
				+ " workstate,  \n"
				+ " graduateschool, \n"
				+ " speciality,  \n"
				+ " schoolage,  \n"
				+ " duty,  \n"
				+ " government, \n"
				+ " health,  \n"
				+ " consortname, \n"
				+ " childname,  \n"
				+ " bank,  \n"
				+ " bankaccounts, \n"
				+ " dakcoding,  \n"
				+ " address,  \n"
				+ " dimissiontime, \n"
				+ " hukou,  \n"
				+ " qq,  \n"
				+ " nativeplace, \n"
				+ " property \n" + " from  \n" + " oa_staff ";

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("员工详细信息记录");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		pp.addColumn("真实姓名", "cname");
		pp.addColumn("性别", "sex");
		pp.addColumn("部门名称", "department");
		pp.addColumn("岗位", "post");
		pp.addColumn("联系电话", "mobilephone");
		pp.addColumn("Email", "email");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(_strList);
	}
/**
 * 添加信息
 * @param req
 * @param res
 * @param st
 * @return
 * @throws Exception
 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res,
			StaffTable st) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			staffService ss = new staffService(conn);
			ss.add(st);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
		// return new ModelAndView(_strList);

		return null;
	}
/**
 * 删除信息
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
			staffService ss = new staffService(conn);
			ss.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}

		return new ModelAndView(_strList);
	}
/**
 * 修改信息
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
		StaffTable st = new StaffTable();
		String autoid = "";
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			staffService ss = new staffService(conn);
			st = ss.getStaff(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("StaffTable", st);
		modelAndView.addObject("autoid", autoid);
		return modelAndView;
	}
/**
 * 更新信息
 * @param req
 * @param res
 * @param st
 * @return
 * @throws Exception
 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			StaffTable st) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			staffService ss = new staffService(conn);
			ss.update(st);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}

}
