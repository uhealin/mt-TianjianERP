package com.matech.audit.work.oa.userLevel;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.OAexamine.ExamineService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>
 * Title: 人员考核
 * </p>
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * <p>
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author zyq 2008-10-08
 */
public class UserLevelAction extends MultiActionController {

	private final String _strLevelHistory = "/oa/userLevel/UserLevelHistory.jsp";

	private final String _strUserLevel = "/oa/userLevel/UserLevel.jsp";

	private final String _strUserLevelList = "/oa/userLevel/UserLevelList.jsp";

	/**
	 * 人员考核历史记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView levelHistory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strLevelHistory);

		ASFuntion CHF = new ASFuntion();
		HttpSession session = request.getSession();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String mysql = "";
		String all = CHF.showNull(request.getParameter("all"));
		try {

			conn = new DBConnect().getConnect("");
			String myUserid = (String) request.getSession().getAttribute(
					"myUserid");

			String userid = "";
			if ("".equals(myUserid) || myUserid == null) {
				userid = "";
			} else {
				mysql = " select distinct loginid from k_user where id='"
						+ myUserid + "' ";
				ps = conn.prepareStatement(mysql);
				rs = ps.executeQuery();
				if (rs.next()) {
					userid = rs.getString(1);
				} else {
					userid = "";
				}

			}

			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
						javax.servlet.http.HttpServletRequest request,
						javax.servlet.http.HttpServletResponse response)
						throws Exception {

					String userName = this.getRequestValue("userName");
					String recorder = this.getRequestValue("recorder");
					String examineTime = this.getRequestValue("examineTime");

					if (userName != null && !userName.equals("")) {
						userName = "and username like '" + userName + "%' ";
					}

					if (recorder != null && !recorder.equals("")) {
						recorder = "and recorder like '" + recorder + "%' ";
					}

					if (examineTime != null && !examineTime.equals("")) {
						examineTime = "and examtime like '" + examineTime
								+ "%' ";
					}

					this.setOrAddRequestValue("userName", userName);
					this.setOrAddRequestValue("recorder", recorder);
					this.setOrAddRequestValue("examineTime", examineTime);

				}
			};

			pp.setTableID("levellist");
			pp.setInputType("radio");

			pp.setWhichFieldIsValue(7);

			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setTrActionProperty(true);
//			pp.setTrAction("style=\"cursor:hand;\" onDBLclick=\"goSort('${recordtime}');\" ");

			pp.setPrintColumnWidth("20,20,20,20,20,20,20");
			pp.setPrintTitle("人员考核历史记录");

			pp.addColumn("姓名", "username");
			pp.addColumn("分数", "scoure");
			pp.addColumn("级别", "userlevel");
			pp.addColumn("提交人", "recorder");
			pp.addColumn("提交时间", "recordtime");
			pp.addColumn("审批人", "examiner");
			pp.addColumn("审批时间", "examtime");

			String sql = "";
			if ("".equals(userid) || userid == null || "all".equals(all)) {
				sql = "select * from oa_userlevel where 1=1 ${userName} ${recorder} ${examineTime}";
			} else {
				sql = "select * from oa_userlevel where userid='" + userid
						+ "' ${userName} ${recorder} ${examineTime}";
				request.setAttribute("userid", userid);
			}

			pp.setSQL(sql);

			pp.setOrderBy_CH("recordtime");
			pp.setDirection("desc");

			pp.addSqlWhere("userName", " ${userName} ");
			pp.addSqlWhere("recorder", " ${recorder} ");
			pp.addSqlWhere("examineTime", " ${examineTime} ");

			session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		} catch (Exception e) {

		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		model.addObject("all", all);
		return model;
	}

	/**
	 * 设置人员等级
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strUserLevel);

		ASFuntion CHF = new ASFuntion();

		String myUserid = (String) request.getSession()
				.getAttribute("myUserid");
		String userid = CHF.showNull(request.getParameter("userid"));
		String all = CHF.showNull(request.getParameter("all"));

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		List list = new ArrayList();
		List listlast = new ArrayList();
		try {
			conn = new DBConnect().getConnect("");

			if ("".equals(myUserid) || myUserid == null) {
				userid = "";
			} else {
				sql = " select distinct loginid from k_user where id='"
						+ myUserid + "' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					userid = rs.getString(1);
				} else {
					userid = "";
				}

			}

			if (!"".equals(CHF.showNull(request.getParameter("userid")))) {
				userid = CHF.showNull(request.getParameter("userid"));
			}

			String sql1 = "select count(*) from asdb.oa_examinelibrary where ctype = '人员考核' and isenable='有效' and property='定性'";
			int num = new DbUtil(conn).queryForInt(sql1);
			System.out.println("zyq:"+num);
			ExamineService ems = new ExamineService(conn);

			list = ems.Calculate("人员考核", userid, "人员");
			for(int i=1;i<=num;i++) {
				listlast.add(list.get(list.size() - i));
			}
//			listlast.add(list.get(list.size() - 1));
//			listlast.add(list.get(list.size() - 2));
			for(int i=1;i<=num;i++) {
				list.remove(list.size() - 1);
			}
//			list.remove(list.size() - 1);
//			list.remove(list.size() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		if ("all".equals(all)) {
			userid = request.getParameter("userid");
		}
		model.addObject("userid", userid);
		model.addObject("userExamineList", list);
		model.addObject("userExamineDX", listlast);

		String toall = request.getParameter("toall");
		if (!"".equals(toall) && toall != null) {
			model.addObject("toall", toall);
		}
		if (!"".equals(all) && all != null) {
			model.addObject("all", all);
		}

		return model;
	}

	/**
	 * 人员考核保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveUserLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");
		String username = userSession.getUserName();

		ASFuntion CHF = new ASFuntion();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = new DBConnect().getConnect("");

			String justlook = request.getParameter("justlook");
			String toall = CHF.showNull(request.getParameter("toall"));
			String all = CHF.showNull(request.getParameter("all"));

			// 系统当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowtime = sdf.format(new Date());

			String myUserid = "";

			if ("all".equals(all) && !"".equals(request.getParameter("userid"))) {
				myUserid = request.getParameter("userid");
			} else {
				myUserid = (String) request.getSession().getAttribute(
						"myUserid");
			}
			String userid = "";

			String totalscore = request.getParameter("totalscores");

			String sql = "";

			if ("".equals(myUserid) || myUserid == null) {
				userid = "";
			} else {
				sql = " select distinct loginid from k_user where id='"
						+ myUserid + "' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					userid = rs.getString(1);
				} else {
					userid = "";
				}

			}

			if ("".equals(userid)) {
				userid = CHF.showNull(request.getParameter("userid"));
			}

			if ("".equals(justlook) || justlook == null) {

				String[] cnames = request.getParameter("cnames").split(",");
				String[] objvalues = request.getParameter("objvalues").split(
						",");
				String[] sysscores = request.getParameter("sysscores").split(
						",");
				String[] userscores = request.getParameter("userscores").split(
						",");

				sql = "insert into oa_examineresult (groupid,examinetype,customerid,examinename,examinevalue,result1,result2,recorder,starttime,examiner) values(?,?,?,?,?,?,?,?,?,?)";

				for (int i = 0; i < cnames.length; i++) {
					ps = conn.prepareStatement(sql);

					ps.setString(1, nowtime);
					ps.setString(2, "人员考核");
					ps.setString(3, userid);
					ps.setString(4, cnames[i]);
					ps.setString(5, objvalues[i]);

					if (sysscores[i].length() > 5) {
						ps.setString(6, sysscores[i].substring(0, 5));
					} else {
						ps.setString(6, sysscores[i]);
					}

					if (userscores[i].length() > 5) {
						ps.setString(7, userscores[i].substring(0, 5));
					} else {
						ps.setString(7, userscores[i]);
					}

					ps.setString(8, username);
					ps.setString(9, "");
					ps.setString(10, "");

					ps.execute();
					ps.close();
				}

				String myusername = "";

				sql = "select name from k_user where loginid='" + userid + "' ";
				ps = conn.prepareStatement(sql);

				rs = ps.executeQuery();

				while (rs.next()) {
					myusername = rs.getString(1);
				}

				rs.close();
				ps.close();

				sql = "insert into oa_userlevel(userid,username,scoure,userlevel,recorder,recordtime,examiner,examtime) values(?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, userid);
				ps.setString(2, myusername);

				if (totalscore.length() > 5) {
					ps.setString(3, totalscore.substring(0, 5));
				} else {
					ps.setString(3, totalscore);
				}

				ps.setString(4, "");
				ps.setString(5, username);
				ps.setString(6, nowtime);
				ps.setString(7, "");
				ps.setString(8, "");

				ps.execute();

			}

			if ("true".equals(toall)) {
				response
						.sendRedirect("userLevel.do?method=levelHistory&all="
								+ all + "");
			} else {
				response
						.sendRedirect("userLevel.do?method=levelHistory&userid="
								+ userid + "&all=" + all);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 人员考核记录明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitUserLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strUserLevelList);
		HttpSession session = request.getSession();

		ASFuntion CHF = new ASFuntion();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String userid = request.getParameter("userid");

		String all = CHF.showNull(request.getParameter("all"));

		String myuserid = "";
		String userlevel = "";
		String memo = "";
		String recorder = "";// 提交人
		String starttime = "";// 审批时间
		String examiner = "";// 审批人
		try {

			conn = new DBConnect().getConnect("");

			DataGridProperty pp = new DataGridProperty();

			pp.setTableID("clevellist");

			pp.setWhichFieldIsValue(1);

			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);

			pp.setPrintColumnWidth("20,20,50,20,20,20");
			pp.setPrintTitle("人员考核记录");

			pp.setCancelPage(true);// 取消分页

			pp.addColumn("姓名", "name");
			pp.addColumn("考核性质", "examinetype");
			pp.addColumn("指标名称", "examinename");
			pp.addColumn("人员指标值", "examinevalue");
			pp.addColumn("参考分数", "result1");
			pp.addColumn("最终分数", "result2");

			String sql = "select a.*,b.name from oa_examineresult a left join k_user b on a.customerid=b.loginid where groupid='"
					+ userid + "'";

			pp.setSQL(sql);

			pp.setOrderBy_CH("autoid");
			pp.setDirection("asc");

			session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

			String onlylook = request.getParameter("onlylook");

			request.setAttribute("onlylook", onlylook);
			request.setAttribute("recode", userid);

			sql = "select distinct userid,userlevel,memo,recorder,recordtime,examiner from oa_userlevel where recordtime ='"
					+ userid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {
				myuserid = CHF.showNull(rs.getString(1));
				userlevel = CHF.showNull(rs.getString(2));
				memo = CHF.showNull(rs.getString(3));
				recorder = CHF.showNull(rs.getString(4));
				starttime = CHF.showNull(rs.getString(5));
				examiner = CHF.showNull(rs.getString(6));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		model.addObject("userid", myuserid);
		model.addObject("userlevel", userlevel);
		model.addObject("memo", memo);
		model.addObject("recorder", recorder);
		model.addObject("starttime", starttime);
		model.addObject("examiner", examiner);

		String toall = request.getParameter("toall");
		if (!"".equals(toall) && toall != null) {
			model.addObject("toall", toall);
		}
		if (!"".equals(all) && all != null) {
			model.addObject("all", all);
		}

		return model;
	}

	/**
	 * 人员考核通过
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView passUserLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");
		String username = userSession.getUserName();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String mysql = "";
		ASFuntion CHF = new ASFuntion();

		try {

			conn = new DBConnect().getConnect("");

			// 系统当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime = sdf.format(new Date());

			String myUserid = (String) request.getSession().getAttribute(
					"myUserid");
			String userid = "";

			String all = CHF.showNull(request.getParameter("all"));

			if ("".equals(myUserid) || myUserid == null) {
				userid = "";
			} else {
				mysql = " select distinct loginid from k_user where id='"
						+ myUserid + "' ";
				ps = conn.prepareStatement(mysql);
				rs = ps.executeQuery();
				if (rs.next()) {
					userid = rs.getString(1);
				} else {
					userid = "";
				}

			}

			String recordtime = request.getParameter("recordtime");
			String userlevel = request.getParameter("userlevel");
			String memo = request.getParameter("memo");

			String sql = "update oa_examineresult set starttime=?,examiner=?,memo=? "
					+ " where starttime ='' and examiner = ''"
					+ " and groupid= ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, nowtime);
			ps.setString(2, username);
			ps.setString(3, memo);
			ps.setString(4, recordtime);

			ps.execute();
			ps.close();

			sql = "update oa_userlevel set examiner=?,examtime=?,userlevel=?,memo=? where recordtime=?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, nowtime);
			ps.setString(3, userlevel);
			ps.setString(4, memo);
			ps.setString(5, recordtime);

			ps.execute();

			if ("".equals(all)) {
				response
						.sendRedirect("userLevel.do?method=levelHistory&userid="
								+ userid);
			} else {
				response
						.sendRedirect("userLevel.do?method=levelHistory&all=all");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 人员考核验证
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkUserLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = new DBConnect().getConnect("");

			String userid = request.getParameter("userid");

			String sql = "select count(*) as result from oa_examineresult"
					+ " where starttime =''"
					+ " and customerid= ? and examinetype='人员考核' ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);

			rs = ps.executeQuery();

			String resultset = "";
			while (rs.next()) {
				resultset = rs.getString("result");
			}

			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();

			if ("0".equals(resultset)) {
				out.print("yes");
			} else {
				out.print("no");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 判断是否通过审批
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getestate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");

			String autoid = request.getParameter("autoid");

			PrintWriter out = response.getWriter();

			String sql = "select examiner from oa_userlevel where recordtime='"
					+ autoid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String estate = "";
			while (rs.next()) {
				estate = rs.getString(1);
			}

			if (!"".equals(estate)) {
				out.print("yes");
			} else {
				out.print("no");
			}
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

}
