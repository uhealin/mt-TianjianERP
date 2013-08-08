package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;

public class _3838_0 extends AbstractAreaFunction {
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		String projectId = (String)args.get("curProjectid");	//当前项目编号
		String taskCode = (String)args.get("taskCode");	//底稿索引号

		if(taskCode == null || "".equals(taskCode)) {
			taskCode = request.getParameter("curTaskCode");
		}

		//认定
		String[] cognizances = new String[] {
				"存在","完整","权利和义","计价和分摊","列报",
		};

		PreparedStatement ps = null;
		ResultSet rs = null;


		try {

			//切换数据库连接
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			//根据sql取出需要的列
			String sql = " select a.auditprocedure,TRIM(BOTH ',' FROM a.manuscript) as manuscript,a.executor, "
					   + " if(cognizance like concat('%',?,'%'),'√','') as c1, "
					   + " if(cognizance like concat('%',?,'%'),'√','') as c2, "
					   + " if(cognizance like concat('%',?,'%'),'√','') as c3, "
					   + " if(cognizance like concat('%',?,'%'),'√','') as c4, "
					   + " if(cognizance like concat('%',?,'%'),'√','') as c5 "
					   + " from z_procedure a,z_task b "
					   + " where a.projectId=? "
					   + " and b.taskcode=? "
					   + " and a.State<>'不适用' "
					   + " and b.isleaf=1 "
					   + " and b.parenttaskid=a.taskid "
					   + " and a.projectId=b.projectId "
					   + " order by a.defineid ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, cognizances[0]);
			ps.setString(2, cognizances[1]);
			ps.setString(3, cognizances[2]);
			ps.setString(4, cognizances[3]);
			ps.setString(5, cognizances[4]);
			ps.setString(6, projectId);
			ps.setString(7, taskCode);

			rs = ps.executeQuery();

		} catch (Exception e) {
			throw e;
		}

		return rs;
	}

	public static void main(String[] args) throws Exception {

		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			Map map  = new HashMap();

			map.put("curProjectid", "20081162");
			map.put("taskCode", "FA-1");
			rs = new _3838_0().process(null, null, null, conn, map);

			while(rs.next()) {
				System.out.print(rs.getString(1) + "|");
				System.out.print(rs.getString(2) + "|");
				System.out.print(rs.getString(3) + "|");
				System.out.print(rs.getString(4) + "|");
				System.out.print(rs.getString(5) + "|");
				System.out.print(rs.getString(6) + "|");
				System.out.print(rs.getString(7) + "|");
				System.out.print(rs.getString(8) + "\n");

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
