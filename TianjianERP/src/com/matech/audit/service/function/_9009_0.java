package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 企业绩效评价标准值
 * 
 * 参数
 * 	年份 : 企业绩效评价标准值的年份，例: 年份=2010
 * 	行业 : 例: 行业=全国国有企业
 * 	规模 : 例: 规模=中型企业
 * 
 * 返回
 *  orderid : 顺序号
 *  year : 年份
 *  vocation : 行业
 *  scale : 规模
 *  project : 项目
 *  excellence : 优秀值
 *  favorable : 良好值
 *  average : 平均值
 *  lower : 较低值
 *  short : 较差值
 */

public class _9009_0 extends AbstractAreaFunction {
	
	
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			st = conn.createStatement();

			String areaid = request.getParameter("areaid");
			String acc = CHF.showNull((String) args.get("curAccPackageID"));
	        String projectid = CHF.showNull((String) args.get("curProjectid"));
	        
	        String year = CHF.showNull((String)args.get("年份"));		
	        String vocation = CHF.showNull((String)args.get("行业"));
	        String scale = CHF.showNull((String)args.get("规模"));
			
	        
	        if("".equals(year)){
	        	//为空，就取项目的结束年份
	        	Project project = new ProjectService(conn).getProjectById(projectid);
	        	year = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
	        }
	        
	        sql = "select * " +
	        "	from asdb.k_performance " +
	        "	where year = '"+year+"' " +
	        "	and vocation = '"+vocation+"' " +
	        "	and scale = '"+scale+"' " +
	        "	order by abs(orderid) ";
	        rs = st.executeQuery(sql);
			
			return rs;
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		} 
		
	}

}
