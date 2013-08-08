package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.util.ASFuntion;

/**
 *
 * @author 铭太E审通团队,ESPIERALY THANKS WINNERQ AND PENGYONG
 * @version 1.0
 */

/**
 * 
 *  本公式将 大额凭证的抽查过度到底稿
 *  对应BUG：  
 *  
 *  需要的参数是：
subjectname
 * 返回的列包括：
 entrycount
debitocc
creditocc
 *  公式使用举例如下：
 *  
 *  取列公式覆盖(3024,"","entrycount")
 * 
 * 
 */

public class _3026_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		String subjectname = (String) args.get("subjectname");

		String projectid = (String) args.get("curProjectid");
		String accpackageid = (String) args.get("curAccPackageID");

		//UserSession usersession = (UserSession)request.getSession().getAttribute("userSession");
		
		//String creator = usersession.getUserId();
		
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

		String resultSql = "";

		Statement st = null;
		//   PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (subjectname == null || subjectname.equals("")) {
				String manuid = (String) args.get("manuid");
				if (manuid == null || manuid.equals("")) {
					subjectname = getTaskSubjectNameByTaskCode(conn, projectid,
							(String) request.getParameter("curTaskCode"));

				} else {
					//如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
					subjectname = getTaskSubjectNameByManuID(conn, manuid);

				}

				//                args.put("subjectname",subjectname);  
			}

			String sName = changeSubjectName(conn, projectid, subjectname);
			if (!"".equals(sName)) {
				subjectname = sName;
			}

			//查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
			String[] result = this.getClientIDAndDirectionByStandName(conn,
					accpackageid, projectid, subjectname);
			String subjectid = result[0];

			args.put("subjectid", subjectid);
			//args.put("creator", creator);
			args.put("projectid", projectid);

			resultSql = getSql();

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
	 *
	 * @return String
	 */
	public String getSql() {
		String sql = ""
			+" select count(1) as entrycount,sum(debitValue) as debitocc,sum(creditValue) as creditocc from \n"
			+" ( \n"
			+" select '${subjectid}' as '科目',count(1),sum(if(Dirction=1,OccurValue,'0')) as debitValue, sum(if(Dirction=-1,OccurValue,'0')) as creditValue from ( \n"
			+" select distinct vchid,subjectid,entryid from z_voucherspotcheck \n"
			+" where 1=1 \n"
			+" and projectid = '${projectid}' \n"
			//+" and createor = '19' \n"
			+" and subjectid like '${subjectid}%' \n"
			+" and subjectid  = entrysubjectid \n"
			 
			+" )a \n"
			+" left join  \n"
			+" c_subjectentry b \n"
			+" on a.entryid = b.autoid \n"
			+" group by vchid \n"
			+" )a \n"
			+" group by '科目' \n";


		return sql;

	}

}