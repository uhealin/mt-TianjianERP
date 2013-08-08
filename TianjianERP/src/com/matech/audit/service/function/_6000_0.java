package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author zyq
 * 
 * 重大事项汇报取数 如果类别ctype="已解决"刷已解决的 如果ctype="未解决"刷未解决的 如果ctype=""刷全部
 * 输出：科目名称：subjectfullname1；底稿编号：taskCodeList；事项内容：matter；
 * 解决方案：solvematter；索引：solvecode；提出日期：createtime；解决日期：solvetime
 */

public class _6000_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		String curProjectid = (String) args.get("curProjectid");
		String ctype = (String) args.get("ctype");// 原值或累计折旧

		if (!"已解决".equals(ctype) && !"未解决".equals(ctype)) {
			ctype = "";
		}
		String resultSql = "";
		Statement st = null;
		ResultSet rs = null;

		args.put("curProjectid", curProjectid);
		try {
			st = conn.createStatement();
			// 最终查询结果
			if (!"".equals(ctype)) {
				if ("已解决".equals(ctype)) {// 已解决
					resultSql = getSuAffairreport(ctype);
				} else {// 未解决
					resultSql = getUnffairreport(ctype);
				}

			} else {
				resultSql = getAllAffairreport();
			}
			resultSql = this.setSqlArguments(resultSql, args);
			System.out.println("resultSql1=" + resultSql);
			st.executeQuery("set   charset   gbk;");
			rs = st.executeQuery(resultSql);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public String getSuAffairreport(String ctype) {// 刷指定是已解决的重大事项汇报
		String resultSql = "";
		resultSql = " select subjectfullname1,taskCodeList,substring(matter,1,locate('&nbsp;&nbsp;&nbsp;',matter)-1) as matter,substring(solvematter,1,locate('&nbsp;&nbsp;&nbsp;',solvematter)-1) as solvematter,solvecode,createtime,solvetime ,id \n"
				+ " from( \n"
				+ " select b.subjectfullname1 as subjectfullname1 ,b.taskCodeList as taskCodeList,replace(b.Matter,'<BR>','') as Matter,replace(a.Matter,'<BR>','') as solvematter,a.taskCodeList as solvecode,b.createtime as createtime,a.createtime as solvetime ,b.ID as id \n"
				+ " from z_affairreport b left join z_affairreport a on b.ID = a.PID  \n"
				+ " where 1=1 "
				+ " and a.status='已解决' \n"
				+ " and b.PID=0 and b.executer ='' and  a.projectid='${curProjectid}' and  b.projectid='${curProjectid}' \n"
				+ " ) a";
		return resultSql;
	}

	public String getUnffairreport(String ctype) {// 刷指定是未解决的重大事项汇报
		String resultSql = "";
		resultSql = " select subjectfullname1,taskCodeList,Matter,solvematter,solvecode,createtime,solvetime ,id \n"
				+ " from( \n"
				+ " select b.subjectfullname1 as subjectfullname1,b.taskCodeList as taskCodeList,b.Matter as Matter,'' as solvematter,'' as solvecode,b.createtime as createtime,'' as solvetime,b.ID as id \n"
				+ " from z_affairreport b   \n"
				+ " where b.status='未解决' and b.PID=0 and b.executer ='' and projectid='${curProjectid}' \n"
				+ " ) a ";
		return resultSql;
	}

	public String getAllAffairreport() {// 刷所有重大事项汇报
		String resultSql = "";
		String resultSql1 = "";
		String resultSql2 = "";
		resultSql1 = " select subjectfullname1,taskCodeList,substring(matter,1,locate('&nbsp;&nbsp;&nbsp;',matter)-1) as matter,substring(solvematter,1,locate('&nbsp;&nbsp;&nbsp;',solvematter)-1) as solvematter,solvecode,createtime,solvetime ,id \n"
				+ " from( \n"
				+ " select b.subjectfullname1 as subjectfullname1 ,b.taskCodeList as taskCodeList,replace(b.Matter,'<BR>','') as Matter,replace(a.Matter,'<BR>','') as solvematter,a.taskCodeList as solvecode,b.createtime as createtime,a.createtime as solvetime ,b.ID as id \n"
				+ " from z_affairreport b left join z_affairreport a on b.ID = a.PID  \n"
				+ " where 1=1 "
				+ " and a.status='已解决' \n"
				+ " and b.PID=0 and b.executer ='' and  a.projectid='${curProjectid}' and  b.projectid='${curProjectid}' \n"
				+ " ) a";

		resultSql2 = " select subjectfullname1,taskCodeList,Matter,solvematter,solvecode,createtime,solvetime ,id \n"
				+ " from( \n"
				+ " select b.subjectfullname1 as subjectfullname1,b.taskCodeList as taskCodeList,b.Matter as Matter,'' as solvematter,'' as solvecode,b.createtime as createtime,'' as solvetime,b.ID as id \n"
				+ " from z_affairreport b   \n"
				+ " where b.status='未解决' and b.PID=0 and b.executer ='' and projectid='${curProjectid}' \n"
				+ " ) b ";

		resultSql = resultSql1 + " union " + resultSql2;
		return resultSql;
	}

}