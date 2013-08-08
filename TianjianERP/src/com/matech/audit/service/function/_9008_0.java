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
 * 3、总账发生额对应分析 
 * 
 * 参数
 * 	科目名称: --标准科目,可以不填(如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；)
 * 	年度:0,-1,-2 (用于取值的范围) 默认为本项目
 * 	
 * 返回
 * 	3、总账发生额对应分析 
 * 		subjectid : 科目编号
 * 		subjectfullname : 科目名称
 * 
 * 		bbsubjectid : 对应科目编号(借方分析)
 * 		bbsubjectfullname : 对应科目名称(借方分析)
 * 		boccurvalue : 金额(借方分析)
 * 		bperValue : 占总发生额百分比(借方分析)
 * 		bcountvalue : 凭证张数(借方分析)
 * 		bmaxvalue : 最大单笔金额(借方分析)
 * 
 * 		bsubjectid : 对应科目编号(贷方分析)
 * 		bsubjectfullname : 对应科目名称(贷方分析)
 * 		occurvalue : 金额(贷方分析)
 * 		perValue : 占总发生额百分比(贷方分析)
 * 		countvalue : 凭证张数(贷方分析)
 * 		maxvalue : 最大单笔金额(贷方分析)
 */
public class _9008_0 extends AbstractAreaFunction {
	 
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
			
			sql = getSql2(conn,args);	

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
	 * 3、总账发生额对应分析
	 */
	public String getSql2(Connection conn, Map args) throws Exception{
		try {
			String Subjects = (String)args.get("Subjects");	//科目编号
			int strStartYearMonth = Integer.parseInt((String)args.get("strStartYearMonth"));	
			int strEndYearMonth = Integer.parseInt((String)args.get("strEndYearMonth"));	

			this.tempTable = "tt_"+DELUnid.getCharUnid();
			String tmpName1 = this.tempTable + "_1";
			
			CorrespondVoucher cv=new CorrespondVoucher(conn);
			cv.create(this.tempTable);
			cv.tmpSubjectEntry(tmpName1, strStartYearMonth, strEndYearMonth ,Subjects);
			cv.tmpSubjectEntry(this.tempTable, tmpName1);
			
			cv.DelTempTable(tmpName1);
			
			String sql = "SELECT * FROM ( \n" +
			"		SELECT a.accpackageid,a.subjectid,a.subjectfullname,a.orderid,a.voucherids, \n" +
			"		a.bsubjectid,a.bsubjectfullname,a.occurvalue,a.perValue,a.countvalue,a.avgvalue,a.maxvalue, \n" +
			"		b.voucherids AS bvoucherids," +
			"		b.bsubjectid AS bbsubjectid,b.bsubjectfullname AS bbsubjectfullname,b.occurvalue AS boccurvalue,b.perValue as bperValue,b.countvalue AS bcountvalue,b.avgvalue AS bavgvalue,b.maxvalue AS bmaxvalue \n" +
			"		FROM "+this.tempTable+" a \n" +
			"		LEFT JOIN "+this.tempTable+" b  \n" +
			"		ON a.opt = 1 \n" +
			"		AND b.opt = -1 \n" +
			"		AND a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid \n" +
			"		AND a.bsubjectid = b.bsubjectid \n" +
			"		AND a.orderid = b.orderid \n" +
			"		WHERE 1=1 AND a.opt = 1 and a.orderid <> 3 \n" +
			"		UNION  \n" +
			"		SELECT b.accpackageid,b.subjectid,b.subjectfullname,b.orderid,a.voucherids, \n" +
			"		a.bsubjectid,a.bsubjectfullname,a.occurvalue,a.perValue,a.countvalue,a.avgvalue,a.maxvalue, \n" +
			"		b.voucherids AS bvoucherids," +
			"		b.bsubjectid AS bbsubjectid,b.bsubjectfullname AS bbsubjectfullname,b.occurvalue AS boccurvalue,b.perValue as bperValue,b.countvalue AS bcountvalue,b.avgvalue AS bavgvalue,b.maxvalue AS bmaxvalue \n" +
			"		FROM "+this.tempTable+" a \n" +
			"		RIGHT JOIN "+this.tempTable+" b \n" + 
			"		ON a.opt = 1 \n" +
			"		AND b.opt = -1 \n" +
			"		AND a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid \n" +
			"		AND a.bsubjectid = b.bsubjectid \n" +
			"		AND a.orderid = b.orderid \n" +
			"		WHERE 1=1 AND b.opt = -1 and b.orderid <> 3 \n" +
			"	) a \n" +
			"	where 1=1 \n" +
			"	and a.subjectid in ('"+Subjects+"')" +
			"	AND IF(orderid = 2 AND IFNULL(countvalue,0) = 0 AND IFNULL(bcountvalue,0) =0 ,1,0) = 0 \n" +
			"	ORDER BY subjectid,IFNULL(bsubjectid,bbsubjectid),orderid";
			
			return sql;
		} catch (Exception e) {
			throw e;
		}
		
	}
}
