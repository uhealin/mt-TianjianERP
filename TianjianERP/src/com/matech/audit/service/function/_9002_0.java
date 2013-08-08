package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import com.matech.audit.service.customer.CustomerService;
//import com.matech.audit.service.project.ProjectService;
//import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class _9002_0 extends AbstractAreaFunction {
	
	/**
	 * 核算体系
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));		//账套编号
//	        String projectid = CHF.showNull((String) args.get("curProjectid"));		//项目编号
//	        
//	        String customerid=acc.substring(0,6);
//	        
//	        String vocationid = new CustomerService(conn).getCustomer(customerid).getVocationId();		//会计制度
//			
//	        String display = CHF.showNull((String)args.get("显示内容")); //科目对照、科目体系、核算体系
//	        
//	        Project project = new ProjectService(conn).getProjectById(projectid);
//	        
//	        String allYear = CHF.showNull((String)args.get("比较年份"));
//	        
//	        String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
//			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
//			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
//			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
//			
//			String strStartYearMonth="",strEndYearMonth="";
//			if("".equals(allYear)||allYear==null){
//				allYear="0";
//			}
//			strStartYearMonth = String.valueOf((Integer.parseInt(begin)+Integer.parseInt(allYear))*12+Integer.parseInt(bMonth));
//			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
			
			sql = getSql3(acc);
			
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
	
	/**
	 * 核算体系
	 * @param strStartYearMonth
	 * @param strEndYearMonth
	 * @return
	 */
	public String getSql3(String acc){
		return "" +
		" select accid,assitemid,assitemname, AssTotalName1,direction2,Level1,IsLeaf1 " +
		" from c_assitementryacc " +
		" where AccPackageID = "+acc+" " +
		" and submonth = 1 " +
		" and isleaf1 = 1 " +
		" order by accid,assitemid";
	}
}
