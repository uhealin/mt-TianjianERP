package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.framework.pub.util.ASFuntion;

public class _3025_0 extends AbstractAreaFunction {

	/**
	 * 
	 * 公式的设置方法： 
	 * =取列公式覆盖(3025, "", "vchdate")
	 * 
	 * 参数：
	 * vchdate　        凭证日期
	 * oldvoucher　     凭证号
	 * creditsubjects	贷方发生的科目
	 * voucherCreditOcc 贷方科目的发生额
	 * debitsubjects	借方发生的科目
	 * voucherDebitOcc	借方科目的发生额
	 * Summary			凭证摘要
	 * 
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		ASFuntion CHF = new ASFuntion();

		String projectid = (String) args.get("curProjectid");
	
		ProjectService projectService = new ProjectService(conn);

		int[] ProjectAuditArea = projectService
				.getProjectAuditAreaByProjectid(projectid);

		String beginDate = "";
		if (ProjectAuditArea[1] < 10) {
			beginDate = ProjectAuditArea[0] + "-0" + ProjectAuditArea[1]
					+ "-01";
		} else {

			beginDate = ProjectAuditArea[0] + "-" + ProjectAuditArea[1] + "-01";
		}
		String endDate = ProjectAuditArea[2] + "-" + ProjectAuditArea[3]
				+ "-01";
		
		//   PreparedStatement ps = null;
	
		args.put("beginDate", beginDate);
		args.put("endDate", endDate);
		try {
			
		String resultSql = "";
		resultSql = getSql();
		Statement st = null;
		ResultSet rs = null;
		//最终查询结果
		resultSql = this.setSqlArguments(resultSql, args);
	
		System.out.println("yzm:sql=" + resultSql);

		st = conn.createStatement();
		rs = st.executeQuery(resultSql);

		return rs;
	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(e.getMessage());
	}
	}
		public String getSql() {
			return "select vchdate,concat(oldvoucherid,'-',typeid) as oldvoucher,creditsubjects,voucherCreditOcc,debitsubjects,voucherDebitOcc,Summary \n"
					+" from c_subjectentry where vchdate>='${beginDate}' and vchdate<='${endDate}'";
		
		}
		
		
}
