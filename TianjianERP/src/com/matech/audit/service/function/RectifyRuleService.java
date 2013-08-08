package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.rule.RuleService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.rule.Project;
import com.matech.rule.ProjectUtil;
import com.matech.rule.RulePO;

public class RectifyRuleService {

	private Connection myConn = null;

	public RectifyRuleService(Connection conn) {
		this.myConn = conn;
	}

	public RectifyRuleService() {

	}

	/***
	 * 得到未定事项的值
	 * @return
	 * @throws Exception
	 */
	public static String getValue(String projectid, String apellation)
			throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String reslut = "否";
		try {
			conn = new DBConnect().getConnect("");
			String sql = "select cost from z_contingencies where apellation = '"
					+ apellation + "' and projectid='" + projectid + "' ";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {

				reslut = rs.getString(1);

			}

			return reslut;
		} catch (Exception e) {
			e.printStackTrace();
			return "否";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

	}

	/***
	 * 得到判断函数的值
	 * @param Type
	 * @param ProjectOrCustomer
	 * @param SubjectFullName
	 * @return
	 * @throws Exception
	 */
	public static String getFinllyValue(String expression, String isTrue,
			String isFalse) throws Exception {

		try {

			String finllyStr = "";

			if ("true".equals(expression)) {
				finllyStr = isTrue;
			} else {
				finllyStr = isFalse;
			}

			return finllyStr;
		} catch (Exception e) {
			e.printStackTrace();
			return "0.00";
		}

	}

	/***
	 * 解析判断表达式
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static void getFinllyValue(String expression) throws Exception {

	}

	/**
	 * 全元素
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public String getAllElement(String title, String projectid)
			throws Exception {

		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (title == null) {
			title = "";
		}
		String[] titles = title.split(",");
		String aaa = "";
		try {

			for (int i = 0; i < titles.length; i++) {
				//	System.out.println("yzm:titles{"+i+"]="+titles[i]);
				titles[i] = titles[i].trim();
				if ("".equals(titles[i]))
					continue;

				String sql = "select refer1 from z_subjectentryrectify where title=? and projectid =?";
				ps = myConn.prepareStatement(sql);
				ps.setString(1, titles[i]);
				ps.setString(2, projectid);
				rs = ps.executeQuery();
				String refer1 = "";
				if (rs.next()) {
					refer1 = rs.getString(1);
				}
				//System.out.println("yzm:refer1="+refer1);
				if (refer1 == null || "".equals(refer1)) {
					continue;
				}
				rs.close();
				ps.close();
				//System.out.println("yzm:refer1="+refer1);
				String sql1 = "select ifnull(group_concat(title),'') from z_subjectentryrectify where autoid in ("
						+ refer1 + ")";
				//System.out.println("yzm:sql1="+sql1);
				ps = myConn.prepareStatement(sql1);
				rs = ps.executeQuery();
				if (rs.next()) {
					aaa = aaa + getAllElement(rs.getString(1), projectid) + ",";
				} else {
					break;
				}
				rs.close();
				ps.close();
			}

			System.out.println("yzm:aaa=" + aaa + title);
			//System.out.println("yzm:title="+title);
			return aaa + title;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
	}

	/**
	 * 根据Title求AutoId
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public String getAutoIdByTitle(String title) throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (title == null) {
			title = "";
		}
		String[] titles = title.split(",");
		String autoids = "";
		try {
			for (int i = 0; i < titles.length; i++) {

				titles[i] = titles[i].trim();
				if ("".equals(titles[i]))
					continue;
				String sql = "select autoid from z_subjectentryrectify where title = '"
						+ titles[i] + "'";
				ps = myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				String autoid = "";
				if (rs.next()) {
					autoid = rs.getString(1);
					autoids = autoids + autoid + ",";
				}

				rs.close();
				ps.close();
			}

			if (autoids.endsWith(",")) {
				if (autoids.equals(",")) {

					autoids = "";
				} else {

					autoids = autoids.substring(0, autoids.length() - 1);
				}
			}
			System.out.println("yzm:autoids=" + autoids);
			//System.out.println("yzm:autoids="+autoids);
			//System.out.println("yzm:title="+title);
			return autoids;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 插入公式调整的临时表
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public void addExpressions(int id, String values, String expressions,
			String direction) throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;

		try {

			ps = myConn
					.prepareStatement("insert into t_z_Expressions(id,values1,expressions,direction) VALUES(?,?,?,?)");

			ps.setString(1, "d" + id);
			ps.setString(2, values);
			ps.setString(3, expressions);
			ps.setString(4, direction);

			ps.executeUpdate();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 对调整公式临时表进行解析
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public ArrayList parseExpressions() throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		RuleService ruleService = new RuleService(myConn);
		try {

			ArrayList arrayList = new ArrayList();

			String selectSql = "select id,values1,expressions,direction from t_z_Expressions order by abs(substr(id,2))";

			ps = myConn.prepareStatement(selectSql);

			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(1) + "";
				String values1 = rs.getString(2) + "";
				String expressions = rs.getString(3) + "";
				String direction = rs.getString(4) + "";

				if (!"".equals(expressions)) {
					String[] expressionss = expressions.split("d");
					String tempStr = "";
					if (expressionss.length > 1) {
						for (int i = 0; i < expressionss.length; i++) {
							if (!"".equals(expressionss[i])) {
								tempStr = tempStr + "d"
										+ expressionss[i].substring(0, 1) + ",";
							}
						}

						tempStr = ruleService.delElements(tempStr);

						//	System.out.println("yzm:tempStr="+tempStr);

						String[] tempStrs = tempStr.split(",");

						for (int i = 0; i < tempStrs.length; i++) {

							String tempsql = "select * from t_z_Expressions where id = '"
									+ tempStrs[i] + "'";
							expressions = expressions.replaceAll(tempStrs[i],
									"(" + parseExpressions1(tempsql) + ")");
						}
						arrayList.add(expressions + "`" + direction);
					} else {
						arrayList.add(expressions + "`" + direction);
					}
				} else {
					arrayList.add(values1 + "`" + direction);
				}

			}
			return arrayList;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 对调整公式临时表进行解析
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public String parseExpressions1(String sql) throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		RuleService ruleService = new RuleService(myConn);
		try {

			ps = myConn.prepareStatement(sql);

			rs = ps.executeQuery();

			while (rs.next()) {

				String values1 = rs.getString(2) + "";
				String expressions = rs.getString(3) + "";

				if (!"".equals(expressions)) {
					String[] expressionss = expressions.split("d");
					String tempStr = "";
					if (expressionss.length > 1) {
						for (int i = 0; i < expressionss.length; i++) {
							if (!"".equals(expressionss[i])) {
								tempStr = tempStr + "d"
										+ expressionss[i].substring(0, 1) + ",";
							}
						}

						tempStr = ruleService.delElements(tempStr);

						//		System.out.println("yzm:tempStr="+tempStr);

						String[] tempStrs = tempStr.split(",");

						for (int i = 0; i < tempStrs.length; i++) {

							String tempsql = "select * from t_z_Expressions where id = '"
									+ tempStrs[i] + "'";
							expressions = expressions.replaceAll(tempStrs[i],
									parseExpressions1(tempsql));
						}

						return expressions;
					} else {
						return expressions;
					}
				} else {
					return values1;
				}

			}
			return "";

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 对调整公式临时表进行解析
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public ArrayList parseExpressions2(String VoucherID) throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		RuleService ruleService = new RuleService(myConn);
		try {

			ArrayList arrayList = new ArrayList();

			String selectSql = "select autoid,OccurValue,ifnull(if(Dirction=1,DebitExpressions,LenderExpressions),0.00) as expressions from z_subjectentryrectify where VoucherID='"
					+ VoucherID + "' order by Serail";

			ps = myConn.prepareStatement(selectSql);

			rs = ps.executeQuery();

			while (rs.next()) {

				String autoid = rs.getString(1) + "";
				String OccurValue = rs.getString(2) + "";
				String expressions = rs.getString(3) + "";

				if (!"".equals(expressions)) {
					String[] expressionss = expressions.split("d");
					String tempStr = "";
					if (expressionss.length > 1) {
						for (int i = 0; i < expressionss.length; i++) {
							if (!"".equals(expressionss[i])) {
								tempStr = tempStr
										+ expressionss[i].substring(0, 1) + ",";
							}
						}

						tempStr = ruleService.delElements(tempStr);

						//	System.out.println("yzm:tempStr="+tempStr);

						String[] tempStrs = tempStr.split(",");

						for (int i = 0; i < tempStrs.length; i++) {

							String tempsql = "select OccurValue,ifnull(if(Dirction=1,DebitExpressions,LenderExpressions),0.00) as expressions from z_subjectentryrectify where VoucherID='"
									+ VoucherID
									+ "' and Serail = '"
									+ tempStrs[i] + "'";
							expressions = expressions.replaceAll("d"
									+ tempStrs[i], "("
									+ parseExpressions3(tempsql, VoucherID)
									+ ")");
						}
						arrayList.add(expressions + "`" + autoid);
					} else {
						arrayList.add(expressions + "`" + autoid);
					}
				} else {
					arrayList.add(OccurValue + "`" + autoid);
				}

			}
			return arrayList;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 对调整公式临时表进行解析
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public String parseExpressions3(String sql, String VoucherID)
			throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		RuleService ruleService = new RuleService(myConn);
		try {

			ps = myConn.prepareStatement(sql);

			rs = ps.executeQuery();

			while (rs.next()) {

				String values1 = rs.getString(1) + "";
				String expressions = rs.getString(2) + "";

				if (!"".equals(expressions)) {
					String[] expressionss = expressions.split("d");
					String tempStr = "";
					if (expressionss.length > 1) {
						for (int i = 0; i < expressionss.length; i++) {
							if (!"".equals(expressionss[i])) {
								tempStr = tempStr
										+ expressionss[i].substring(0, 1) + ",";
							}
						}

						tempStr = ruleService.delElements(tempStr);

						//		System.out.println("yzm:tempStr="+tempStr);

						String[] tempStrs = tempStr.split(",");

						for (int i = 0; i < tempStrs.length; i++) {

							String tempsql = "select OccurValue,ifnull(if(Dirction=1,DebitExpressions,LenderExpressions),0.00) as expressions from z_subjectentryrectify where VoucherID='"
									+ VoucherID
									+ "' and Serail = '"
									+ tempStrs[i] + "'";
							expressions = expressions.replaceAll("d"
									+ tempStrs[i], parseExpressions3(tempsql,
									VoucherID));
						}

						return expressions;
					} else {
						return expressions;
					}
				} else {
					return values1;
				}

			}
			return "";

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 对调整公式临时表进行解析
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public void updateOcc(Object d_xye, String autoid) throws Exception {
		DbUtil.checkConn(myConn);
		PreparedStatement ps = null;

		try {

			String sql = "update z_subjectentryrectify set OccurValue = '"
					+ d_xye + "' where autoid = '" + autoid + "'";
			ps = myConn.prepareStatement(sql);
			ps.executeUpdate();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {

			DbUtil.close(ps);
		}
	}

	/**
	 * 
	 * 小杨调整公式专用(原来的)
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @return
	 * @throws Exception
	 */
	
	public static String getProject3(String projectID, String subname,String res) throws Exception {
		String result = "";
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String fx = "";
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {

			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByProjectid(conn, projectID);

			String acc = new ProjectService(conn).getProjectById(projectID)
					.getAccPackageId();

			FunctionService fs = new FunctionService(conn, projectID, acc, "",
					"", "username", null);
			String skm = fs.changeSubjectName(subname);
			if (!"".equals(skm)) {
				subname = skm;
			}

			sql = "select sum(DebitRemain) DebitRemain,sum((-1)*CreditRemain) CreditRemain,\n"
					+ " sum(if(direction2=0,initbalance,direction2*initbalance)) remain,\n"
					+ " sum(DebitTotalOcc) DebitOcc,sum(CreditTotalOcc) CreditOcc,sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc - CreditTotalOcc))) Occ,\n"
					+ " sum(DebitBalance) DebitBalance,sum((-1)*CreditBalance) CreditBalance,\n"
					+ " sum(if(direction2=0,Balance,direction2*Balance)) Balance, \n"
					+

					" sum(ifnull(DebitTotalOcc6,0)) DebitTotalOcc6,sum(ifnull(CreditTotalOcc6,0)) CreditTotalOcc6,sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0))) rectify6,\n"
					+ " sum(ifnull(DebitTotalOcc4,0)) DebitTotalOcc4,sum(ifnull(CreditTotalOcc4,0)) CreditTotalOcc4,sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0),ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0))) rectify4,\n"
					+ " sum(ifnull(DebitTotalOcc5,0)) DebitTotalOcc5,sum(ifnull(CreditTotalOcc5,0)) CreditTotalOcc5,sum(if(direction2=0,ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0))) rectify5,\n"
					+ " sum(ifnull(DebitTotalOcc0,0)) DebitTotalOcc0,sum(ifnull(CreditTotalOcc0,0)) CreditTotalOcc0,sum(if(direction2=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull(direction2*(DebitTotalOcc0-CreditTotalOcc0),0))) rectify0,\n"
					+

					" sum(ifnull(DebitTotalOcc1,0)) DebitTotalOcc1,sum(ifnull(CreditTotalOcc1,0)) CreditTotalOcc1,sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0))) rectify1,\n"
					+ " sum(ifnull(DebitTotalOcc2,0)) DebitTotalOcc2,sum(ifnull(CreditTotalOcc2,0)) CreditTotalOcc2,sum(if(direction2=0,ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0))) rectify2,\n"
					+ " sum(ifnull(DebitTotalOcc3,0)) DebitTotalOcc3,sum(ifnull(CreditTotalOcc3,0)) CreditTotalOcc3,sum(if(direction2=0,ifnull((DebitTotalOcc3-CreditTotalOcc3),0),ifnull(direction2*(DebitTotalOcc3-CreditTotalOcc3),0))) rectify3,\n"
					+

					" sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0) + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0) + ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0) + ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0) )) qcrectify,\n"
					+ " sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) qcrectify1,\n"
					+ " sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) qcrectify2, \n "
					+

					" sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0) + ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0) )) qmrectify, \n"
					+ " sum(ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) qmrectify1, \n"
					+ " sum(ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) qmrectify2, \n"
					+

					" sum(if(direction2=0,initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  ,direction2 * (initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0) ))) examine1,\n"
					+ " sum(debitremain + ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) examine11, \n"
					+ " sum((-1)*creditremain + ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) examine12,\n"
					+

					" sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0) ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0)))) examine2, \n"
					+ " sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0) + DebitBalance + ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) examine21, \n"
					+ " sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance + ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) examine22, \n"
					+

					" sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance  ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance ))) examine3,  \n"
					+ " sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)  + DebitBalance  ) examine31,  \n"
					+ " sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance  ) examine32  \n"
					+

					" from z_manuaccount where  assitemid = '' and projectID ='"
					+ projectID + "' ";

			SubjectResultService srs = new SubjectResultService(conn, acc);
			String stropt = srs.getTextKeyAll(subname, projectID);
			String[] ss = stropt.split("\\|");
			String str = ss.length <= 1 ? subname + "','" : ss[1].replaceAll(
					"`", "','");
			subname = "'" + str.substring(0, str.length() - 2);
			sql += " and subjectid in (" + subname + ") ";

			org.util.Debug.prtOut("getProject:=| " + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new Exception("取项目数出错！");
				}
				//					result = CHF.showNull(rs.getString(1));
				if ("项目期初".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitRemain"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditRemain"));
					} else {
						result = CHF.showNull(rs.getString("remain"));
					}
				} else if ("项目期末".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitBalance"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditBalance"));
					} else {
						result = CHF.showNull(rs.getString("Balance"));
					}
				} else if ("项目发生".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitOcc"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditOcc"));
					} else {
						result = CHF.showNull(rs.getString("Occ"));
					}
				}

				else if ("账表不符".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc6"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc6"));
					} else {
						result = CHF.showNull(rs.getString("rectify6"));
					}
				} else if ("期初调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc4"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc4"));
					} else {
						result = CHF.showNull(rs.getString("rectify4"));
					}
				} else if ("期初重分类".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc5"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc5"));
					} else {
						result = CHF.showNull(rs.getString("rectify5"));
					}
				} else if ("期初不符未调".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc0"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc0"));
					} else {
						result = CHF.showNull(rs.getString("rectify0"));
					}
				} else if ("期末调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc1"));
					} else {
						result = CHF.showNull(rs.getString("rectify1"));
					}
				} else if ("期末重分类".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc2"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc2"));
					} else {
						result = CHF.showNull(rs.getString("rectify2"));
					}
				} else if ("期末不符未调".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc3"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc3"));
					} else {
						result = CHF.showNull(rs.getString("rectify3"));
					}

				} else if ("期初审定".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine11"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine12"));
					} else {
						result = CHF.showNull(rs.getString("examine1"));
					}

				} else if ("期末审定".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine21"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine22"));
					} else {
						result = CHF.showNull(rs.getString("examine2"));
					}

				}

				else if ("期初总调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("qcrectify1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("qcrectify2"));
					} else {
						result = CHF.showNull(rs.getString("qcrectify"));
					}

				} else if ("期末总调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("qmrectify1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("qmrectify2"));
					} else {
						result = CHF.showNull(rs.getString("qmrectify"));
					}

				} else if ("期末未审".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine31"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine32"));
					} else {
						result = CHF.showNull(rs.getString("examine3"));
					}

				}

			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

	}
	
	public static String getProject2(String projectID, String subname,String res) throws Exception {
		String result = "";
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String fx = "";
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {

			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByProjectid(conn, projectID);

			String acc = new ProjectService(conn).getProjectById(projectID).getAccPackageId();

			FunctionService fs = new FunctionService(conn, projectID, acc, "","", "username", null);
			String skm = fs.changeSubjectName(subname);
			if (!"".equals(skm)) {
				subname = skm;
			}

			String bz = "0";
			String sTab = "";
			String table1 = "";
			String table2 = "";
			String sql1 = "";
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account a right join z_accountrectify b  on b.projectid='"+projectID+"' and a.subjectid=b.subjectid";
				bz = "0";
				table1 = "sum(ifnull(DebitTotalOcc6,0)) DebitTotalOcc6,sum(ifnull(CreditTotalOcc6,0)) CreditTotalOcc6,sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0))) rectify6,";
				table2 = "+ ifnull((DebitTotalOcc6-CreditTotalOcc6),0) "; 
			} 
			else {
				sTab = " c_AccountAll a right join z_accountallrectify b  on b.projectid='"+projectID+"' and a.subjectid=b.subjectid  and a.accsign=1 and a.DataName=b.DataName ";
			}
			String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			sql = "select * from z_project where projectid='"+projectID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				AuditTimeBegin = rs.getString("AuditTimeBegin");
				AuditTimeEnd = rs.getString("AuditTimeEnd");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			SubjectResultService SRS = new SubjectResultService(conn, acc);
			String stropt = SRS.getTextKeyAll(subname, projectID);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);			
			sql1 +=" and a.subjectid in ("+subname+") ";
			
			sql = "select sum(DebitRemain) DebitRemain,sum((-1)*CreditRemain) CreditRemain," +
			"\n sum(if(direction2=0,initbalance,direction2*initbalance)) remain," +
			"\n sum(DebitTotalOcc) DebitOcc,sum(CreditTotalOcc) CreditOcc,sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc - CreditTotalOcc))) Occ," +
			"\n sum(DebitBalance) DebitBalance,sum((-1)*CreditBalance) CreditBalance," +
			"\n sum(if(direction2=0,Balance,direction2*Balance)) Balance, " +
			
			"\n sum(ifnull(DebitTotalOcc6,0)) DebitTotalOcc6,sum(ifnull(CreditTotalOcc6,0)) CreditTotalOcc6,sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0))) rectify6," + 
			"\n sum(ifnull(DebitTotalOcc4,0)) DebitTotalOcc4,sum(ifnull(CreditTotalOcc4,0)) CreditTotalOcc4,sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0),ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0))) rectify4," +
			"\n sum(ifnull(DebitTotalOcc5,0)) DebitTotalOcc5,sum(ifnull(CreditTotalOcc5,0)) CreditTotalOcc5,sum(if(direction2=0,ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0))) rectify5," +
			"\n sum(ifnull(DebitTotalOcc0,0)) DebitTotalOcc0,sum(ifnull(CreditTotalOcc0,0)) CreditTotalOcc0,sum(if(direction2=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull(direction2*(DebitTotalOcc0-CreditTotalOcc0),0))) rectify0," +
			
			"\n sum(ifnull(DebitTotalOcc1,0)) DebitTotalOcc1,sum(ifnull(CreditTotalOcc1,0)) CreditTotalOcc1,sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0))) rectify1," +
			"\n sum(ifnull(DebitTotalOcc2,0)) DebitTotalOcc2,sum(ifnull(CreditTotalOcc2,0)) CreditTotalOcc2,sum(if(direction2=0,ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0))) rectify2," +
			"\n sum(ifnull(DebitTotalOcc3,0)) DebitTotalOcc3,sum(ifnull(CreditTotalOcc3,0)) CreditTotalOcc3,sum(if(direction2=0,ifnull((DebitTotalOcc3-CreditTotalOcc3),0),ifnull(direction2*(DebitTotalOcc3-CreditTotalOcc3),0))) rectify3," +
			
			"\n sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0) + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0) + ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0) + ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0) )) qcrectify," +
			"\n sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) qcrectify1," +
			"\n sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) qcrectify2, \n " +

			"\n sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0) + ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0) )) qmrectify, "+
			"\n sum(ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) qmrectify1, " +
			"\n sum(ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) qmrectify2, " +
			
			"\n sum(if(direction2=0,initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  ,direction2 * (initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0) ))) examine1," +
			"\n sum(debitremain + ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) examine11, " +
			"\n sum((-1)*creditremain + ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) examine12," +
			
			"\n sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0) ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0)))) examine2, " +
			"\n sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0) + DebitBalance + ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) examine21, " +
			"\n sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance + ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) examine22, " +
			
			"\n sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance  ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance ))) examine3,  " +
			"\n sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)  + DebitBalance  ) examine31,  " +
			"\n sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance  ) examine32  " +

			"\n from (" +
			"\n 	select direction2," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),DebitRemain,0)) DebitRemain," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(-1)*CreditRemain,0)) CreditRemain," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(DebitRemain+CreditRemain),0)) initbalance," +
			"\n	sum(DebitOcc) DebitTotalOcc, sum(CreditOcc) CreditTotalOcc, (sum(DebitOcc) - sum(CreditOcc)) Occ," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),DebitBalance,0)) DebitBalance," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),(-1)*CreditBalance,0)) CreditBalance," +
			"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),Balance,0)) Balance," +
			"\n	b.* " +
			"\n	from "+sTab+" where substring(a.AccPackageID,1,6) = '"+acc.substring(0,6)+"'" +
			"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) >= substring('"+AuditTimeBegin+"',1,7) " +
			"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) <= substring('"+AuditTimeEnd+"',1,7)" +
			"\n	and DataName='"+bz+"'" + sql1 + 
			"\n	group by b.subjectid" +
			"\n ) a where 1=1 ";
			
//			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new Exception("取项目数出错！");
				}
				//					result = CHF.showNull(rs.getString(1));
				if ("项目期初".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitRemain"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditRemain"));
					} else {
						result = CHF.showNull(rs.getString("remain"));
					}
				} else if ("项目期末".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitBalance"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditBalance"));
					} else {
						result = CHF.showNull(rs.getString("Balance"));
					}
				} else if ("项目发生".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitOcc"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditOcc"));
					} else {
						result = CHF.showNull(rs.getString("Occ"));
					}
				}

				else if ("账表不符".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc6"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc6"));
					} else {
						result = CHF.showNull(rs.getString("rectify6"));
					}
				} else if ("期初调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc4"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc4"));
					} else {
						result = CHF.showNull(rs.getString("rectify4"));
					}
				} else if ("期初重分类".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc5"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc5"));
					} else {
						result = CHF.showNull(rs.getString("rectify5"));
					}
				} else if ("期初不符未调".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc0"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc0"));
					} else {
						result = CHF.showNull(rs.getString("rectify0"));
					}
				} else if ("期末调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc1"));
					} else {
						result = CHF.showNull(rs.getString("rectify1"));
					}
				} else if ("期末重分类".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc2"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc2"));
					} else {
						result = CHF.showNull(rs.getString("rectify2"));
					}
				} else if ("期末不符未调".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("DebitTotalOcc3"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("CreditTotalOcc3"));
					} else {
						result = CHF.showNull(rs.getString("rectify3"));
					}

				} else if ("期初审定".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine11"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine12"));
					} else {
						result = CHF.showNull(rs.getString("examine1"));
					}

				} else if ("期末审定".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine21"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine22"));
					} else {
						result = CHF.showNull(rs.getString("examine2"));
					}

				}

				else if ("期初总调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("qcrectify1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("qcrectify2"));
					} else {
						result = CHF.showNull(rs.getString("qcrectify"));
					}

				} else if ("期末总调整".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("qmrectify1"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("qmrectify2"));
					} else {
						result = CHF.showNull(rs.getString("qmrectify"));
					}

				} else if ("期末未审".equals(res)) {
					if ("1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine31"));
					} else if ("-1".equals(fx)) {
						result = CHF.showNull(rs.getString("examine32"));
					} else {
						result = CHF.showNull(rs.getString("examine3"));
					}

				}

			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

	}

	public static void main(String[] args) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("101841");
			RectifyRuleService rectifyRuleService = new RectifyRuleService(conn);

			String aaa = rectifyRuleService.getAllElement(
					"121-2,乘法1(乘法(国资委是否同意调整),11,乘法1(乘法(国资委是否同意调整),11,22))",
					"2008155118");
			rectifyRuleService.getAutoIdByTitle(aaa);
			conn.close();

			//			Project.regiesterMethod("乘法", "getValue", com.matech.audit.service.function.RectifyRuleService.class ,new Class[] { String.class });
			//			Project.regiesterMethod("乘法1", "getFinllyValue", com.matech.audit.service.function.RectifyRuleService.class ,new Class[] { String.class,String.class,String.class });
			//			HashMap inputs = new HashMap();
			//			inputs.put("客户号", "2");
			//
			//		  //下面演示直接动态创建RULE并运行之；
			//			RulePO rulepo= new RulePO();
			//			rulepo.setRule(" 输出(\"最终结果\");" + "最终结果=乘法1(乘法(国资委是否同意调整),11,乘法1(乘法(国资委是否同意调整),11,22));");
			//			rulepo.setId("1");
			//			rulepo.setName("内部临时规则");
			//
			//
			//			Project project = ProjectUtil.runRuleByPO(rulepo, inputs);
			//			Map outputs = project.getOutputs();
			//
			//			String d_xye = (String)outputs.get("最终结果");
			//			System.out.println(d_xye);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
