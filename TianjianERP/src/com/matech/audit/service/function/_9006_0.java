package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.checkInfo.CorrespondVoucher;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 用于整成E审通查询的公式
 * 1、摘要汇总
 * 
 * 参数
 * 	科目名称: --标准科目,可以不填(如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；)
 * 	年度:0,-1,-2 (用于取值的范围) 默认为本项目
 * 	
 * 返回
 * 	1、摘要汇总
 * 		summary : 摘要
 * 		hj : 频度
 * 		debitocc : 借方发生额
 * 		debitpercent : 借方比重(%)
 * 		creditocc : 贷方发生额
 * 		creditpercent : 贷方比重(%)
 * 
 */
public class _9006_0 extends AbstractAreaFunction {
	 
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
	        
	        String SubjectName = CHF.showNull((String)args.get("科目名称"));	//科目名称
	        String Year = CHF.showNull((String)args.get("年度"));		//年度
			
	        /**
	         * 预处理
	         */
	        if("".equals(Year)) Year = "0";
	        
			if (SubjectName==null || SubjectName.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    SubjectName=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(conn, manuid);
                }
                
            }
			String sName1 = changeSubjectName(conn,projectid,SubjectName);
            if(!"".equals(sName1)){
            	SubjectName = sName1; 
            }            
            args.put("SubjectName",SubjectName);
            args.put("科目名称",SubjectName);
            
            String strStartYearMonth="",strEndYearMonth=""; //开始年月、结束年月
            Project project = new ProjectService(conn).getProjectById(projectid);
			args.put("project", project);
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			strStartYearMonth = String.valueOf(Integer.parseInt(begin)*12 + Integer.parseInt(bMonth));
			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12 + Integer.parseInt(eMonth));

			int iYearMonthArea = Integer.parseInt(strEndYearMonth) - Integer.parseInt(strStartYearMonth) + 1;
			
			strStartYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + iYearMonthArea * Integer.parseInt(Year));
			strEndYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) + iYearMonthArea * Integer.parseInt(Year));
			
			args.put("strStartYearMonth",strStartYearMonth); //开始年月
			args.put("strEndYearMonth",strEndYearMonth); //结束年月
			
			
			/**
			 * 输出SQL
			 */
			//求出对应的科目编号
			String Subjects = "";
			sql = "			select ifnull(group_concat(DISTINCT a.subjectid SEPARATOR \"','\"),'')  as Subjects \n" +
			"			FROM c_account a \n" +
			"			WHERE a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' AND a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" + 
			"			AND (a.subjectfullname2 LIKE '"+SubjectName+"/%' OR a.subjectfullname2 = '"+SubjectName+"')   \n" ;
			rs = st.executeQuery(sql);
			if(rs.next()){
				Subjects = rs.getString("Subjects");
			}
			args.put("Subjects",Subjects); 
			
			sql = getSql(args);

			/**
			 * 返回rs
			 */
			System.out.println(sql);
			rs = st.executeQuery(sql);
			
//			System.out.println(this.tempTable);
//			this.tempTable = "";
			
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
	 * 1、摘要汇总
	 */
	public String getSql(Map args) throws Exception{
		String Subjects = (String)args.get("Subjects");	//科目编号
		String strStartYearMonth = (String)args.get("strStartYearMonth");	
		String strEndYearMonth = (String)args.get("strEndYearMonth");	
		
		String sql = "select summary , count(1) as hj , \n" +
		"	sum(if(dirction>0,occurvalue,0)) as debitocc , \n" + 
		"	ROUND(sum(if(dirction>0,occurvalue,0))/b.debitocc * 100,2) as debitpercent , \n" + 
		"	sum(if(dirction<0,occurvalue,0)) as creditocc ,  \n" +
		"	ROUND(sum(if(dirction<0,occurvalue,0)) /b.creditocc * 100,2) as creditpercent \n" + 
		"	from c_subjectentry a , ( \n" +

		"		select sum(if(dirction>0,occurvalue,0)) as debitocc,sum(if(dirction<0,occurvalue,0)) as creditocc \n" + 
		"		from c_subjectentry  \n" +
		"		where 1=1 " +
		"		and year(vchDate) * 12 + month(vchDate) >= '"+strStartYearMonth+"' \n" +
		"		and year(vchDate) * 12 + month(vchDate) <= '"+strEndYearMonth+"' \n" +
		"		and subjectid in ('"+Subjects+"') \n" +
		"	)b \n" + 
		"	where 1=1 " +
		"	and year(vchDate) * 12 + month(vchDate) >= '"+strStartYearMonth+"' \n" +
		"	and year(vchDate) * 12 + month(vchDate) <= '"+strEndYearMonth+"' \n" +
		"	and subjectid in ('"+Subjects+"')   \n" +
		"	Group by summary  \n" +
		"	order by hj desc ";
		
		return sql;
	}
	
}
