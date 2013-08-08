package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.matech.audit.pub.db.DBConnect;


public class _3839_0 extends AbstractAreaFunction {
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		
		String projectId = (String)args.get("curProjectid");	//当前项目编号
		
		if(projectId == null || "".equals(projectId)){
			 projectId = request.getParameter("projectId");
			
		}
		
		String taskCode = (String)args.get("taskCode");	//底稿索引号

		if(taskCode == null || "".equals(taskCode)) {
			taskCode = request.getParameter("curTaskCode");
		}
		
		String opt = request.getParameter("opt");
		if(opt == null || "".equals(opt)){
			opt = "0";
			
		}

		PreparedStatement ps = null;
		ResultSet rs = null;


		try {

			//切换数据库连接
			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			
			
			//根据sql取出需要的列
			String sql = "";
			if("1".equals(opt)){
				sql = "select  group_concat(b.name,'[',a.Role,']') name from z_auditpeople a,k_user b  where a.userid = b.id and projectid = " + projectId ;
				
			}else{
				sql = "select  a.Role,b.name from z_auditpeople a,k_user b  where a.userid = b.id and projectid = " + projectId ;
				
			}
		

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();

		} catch (Exception e) {
			throw e;
		}

		return rs;
	}


}