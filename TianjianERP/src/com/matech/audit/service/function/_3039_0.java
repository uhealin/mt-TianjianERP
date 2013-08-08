package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.matech.framework.pub.util.ASFuntion;

/**
 *
 * @author 铭太E审通团队,ESPIERALY THANKS WINNERQ AND PENGYONG
 * @version 1.0
 */

/**
 *  需要的参数是：
	会计制度名称或编号
 * 返回的列包括：
 	标准科目编号，标准科目名称  
 *  =取列公式插入(3039,"","标准科目名称","&会计制度名称或编号=企业")
 * 
 * 
 */

public class _3039_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		String vocation = (String) args.get("vocation");
		if("".equals(vocation)){
			vocation = "-1";
		}
		String resultSql = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			resultSql = getSql(vocation);
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
	
	/**
	 * @return String
	 */
	public String getSql(String industryname) {
		String sql = "select subjectid,subjectname from k_standsubject a inner join k_industry b \n"
					+" on b.industryid = a.VocationID \n"
					+" where b.industryname not like '%众泰%' \n"
					+" and (b.industryname  like '%"+industryname+"%' or b.industryid = '"+industryname+"') \n"
					+" and a.level0=1 \n"
					+" order by subjectid \n";

		return sql;

	}

}