package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
 *  本公式将 抽凭轨迹过度到底稿
 *  对应BUG：  4938	
 *  
 *  需要的参数是：
	科目名称，抽凭类别
 * 返回的列包括：
 	抽凭方法、抽凭科目、抽样张数或者抽样占比、抽样时间、抽样人
 *  公式使用举例如下：
 *  
 *  取列公式插入(3027,"","抽凭科目","&抽凭类别=随机抽凭")
 * 
 * 
 */

public class _3027_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		String subjectname = (String) args.get("subjectName");
		String sampleMethod = (String) args.get("sampleMethod")+"";
		String projectid = (String) args.get("curProjectid");
		String accpackageid = (String) args.get("curAccPackageID");
		
		
	Set set = args.keySet();

	
	for (Iterator iter = set.iterator(); iter.hasNext();) {
		String element = (String) iter.next();
		
		System.out.println("yzm:"+element+"="+args.get(element));
		
	}

		if(!"null".equals(sampleMethod)){
			
			sampleMethod = " and b.sampleMethod like '%"+sampleMethod+"%'";
		}else{
			
			sampleMethod = "";
		}
		
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

			args.put("subjectid1", subjectid);
			args.put("projectid", projectid);

			resultSql = getSql(sampleMethod);

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
	public String getSql(String sampleMethod) {
		String sql = " select b.sampleMethod,replace(replace(replace(replace(replace(b.sampleFlow,'<strong>',''),'</strong>',''),'&nbsp;',''),'<br/>','  '),'<br>','  ') as sampleFlow,a.name,b.sampledate,b.subjectId,ifnull(c.SubjectFullName,'') as SubjectFullName "
			   + " from k_user a,z_vouchersampleflow b left join c_accpkgsubject c "
			   + " on c.subjectId = b.subjectId "
			   + " where b.userId=a.id "
			   + " and projectid='${projectid}' "
	 		   + " and (b.property='随机抽样' or b.property='批量抽凭' or b.property='点选抽凭')"
	 		   + " and b.subjectId like '${subjectid1}%'"
			   + sampleMethod ;

		return sql;

	}

}