package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
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
 0003469 Bug 标题 立信羊城--底稿是ZA-2-20 大额查验.xls
 *  
 *  需要的参数是：
 *  currency
 subjectname
 creditValue
 debitValue
 * 返回的列包括：
 subjects,oldvoucherid,vchdate,Dirction,OccurValue,creditValue,debitValue,currency,summary
 *  公式使用举例如下：
 *  
 *  取列公式覆盖(3024,"凭证字","oldvoucherid","&subjectname=现金&currency=人民币&creditValue=10&debitValue=10")
 * 
 * 
 */

/**
 * 参数：
 *  subjectname / 科目全名称 : 科目全路径+核算全路径(可空) 是以余额表的科目名称为主，不支持标准科目转换  
 * 	currency / 币种 : 外币名称
 *  creditValue / 贷方大于 : 贷方发生金额大于等于，例：贷方大于1000，贷发生1000(true)，贷发生-1000(false)
 *  debitValue / 借方大于 : 借方发生金额大于等于，例：借方大于1000，借发生1000(true)，借发生-1000(false)
 *  贷方绝对值大于 : 贷方发生绝对值金额大于等于，例：贷方大于1000，贷发生1000(true)，贷发生-1000(true)
 *  借方绝对值大于 : 借方发生绝对值金额大于等于，例：借方大于1000，借发生1000(true)，借发生-1000(true)
 *  完整凭证 : 是(表示显示与科目全名称有关完整凭证)，否/空(表示只显示与科目全名称有关的凭证分录)--默认
 *  方向 : 1 为借，-1为贷 
 * 返回值：
 * 	typeid / 凭证类型
 * 	oldvoucherid1 / 凭证号 	
 * 	serail / 分录序号	
 * 	oldvoucherid / 凭证记号	
 * 	vchdate / 凭证日期	
 * 	currency / 币种	
 * 	summary / 摘要	
 * 	subjectfullname1 / 科目全路径 	
 * 	subjectname1 / 一级科目	
 * 	subSubjects / 明细科目	
 * 	Dirction / 科目方向
 * 	OccurValue / 发生金额 	
 * 	creditValue / 贷方金额	
 * 	debitValue / 借方金额	
 * 	subjects / 对方科目	
 * 	subjectfullnames / 对方明细科目  
 *    
 */

//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','凭证类型','凭证类型','typeid',NULL,'10',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','凭证号','凭证号','oldvoucherid1',NULL,'20',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','分录序号','分录序号','serail',NULL,'30',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','凭证记号','凭证记号','oldvoucherid',NULL,'40',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','凭证日期','凭证日期','vchdate',NULL,'50',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','币种','币种','currency',NULL,'60',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','摘要','摘要','summary',NULL,'70',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','科目全路径','科目全路径','subjectfullname1',NULL,'80',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','一级科目','一级科目','subjectname1',NULL,'90',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','明细科目','明细科目','subSubjects',NULL,'100',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','发生金额','发生金额','OccurValue',NULL,'110',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','贷方金额','贷方金额','creditValue',NULL,'120',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','借方金额','借方金额','debitValue',NULL,'130',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','对方科目','对方科目','subjects',NULL,'140',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','对方明细科目','对方明细科目','subjectfullnames',NULL,'150',NULL);
//insert into `k_areafunctionfields` (`areaid`, `typeid`, `fieldname`, `fieldvalue`, `evalue`, `groupid`, `orderid`, `property`) values('3024','0','科目方向','科目方向','Dirction',NULL,'160',NULL);


public class _3024_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.createStatement();
			
			String projectid = (String) args.get("curProjectid");
			String accpackageid = (String) args.get("curAccPackageID");
			String currency = CHF.showNull((String) args.get("currency"));//币种
			if("".equals(currency)){
				currency = CHF.showNull((String) args.get("币种")); 
			}
			String creditValue = CHF.showNull((String) args.get("creditValue"));//贷方大于
			if("".equals(creditValue)){
				creditValue = CHF.showNull((String) args.get("贷方大于")); 
			}
			String debitValue = CHF.showNull((String) args.get("debitValue"));//借方大于
			if("".equals(debitValue)){
				debitValue = CHF.showNull((String) args.get("借方大于")); 
			}
			//可以是科目【应收账款】，也可以科目+核算【应收账款/人民币/客户/大中华区/大中华五矿】
			//一级科目名称可以是标准科目，也可以用户科目
			String subjectname = CHF.showNull((String) args.get("subjectname"));//科目全名称
			if("".equals(subjectname)){
				subjectname = CHF.showNull((String) args.get("科目全名称")); 
			}
			String creditAbsValue = CHF.showNull((String) args.get("贷方绝对值大于"));//贷方绝对值大于
			String debitAbsValue = CHF.showNull((String) args.get("借方绝对值大于"));//借方绝对值大于
			
			String direction = CHF.showNull((String) args.get("方向"));//借方绝对值大于
//			String isVoucher = CHF.showNull((String)args.get("完整凭证"));	 //完整凭证
			
			ProjectService projectService = new ProjectService(conn);
		//	System.out.println("yzm:sql="); 
			int[] ProjectAuditArea = projectService.getProjectAuditAreaByProjectid(projectid);
			String beginDate = "",endDate = "";
			if (ProjectAuditArea[1] < 10) {
				beginDate = ProjectAuditArea[0] + "-0" + ProjectAuditArea[1]+ "-01";
			} else {
				beginDate = ProjectAuditArea[0] + "-" + ProjectAuditArea[1] + "-01";
			}
			if (ProjectAuditArea[3] < 10) {
				endDate = ProjectAuditArea[2] + "-0" + ProjectAuditArea[3]+ "-31";
			} else {
				endDate = ProjectAuditArea[2] + "-" + ProjectAuditArea[3]+ "-31";
			}

			String resultSql = "",sql = "";
			
			if (subjectname == null || subjectname.equals("")) {
				String manuid = (String) args.get("manuid");
				if (manuid == null || manuid.equals("")) {
					subjectname = getTaskSubjectNameByTaskCode(conn, projectid,(String) request.getParameter("curTaskCode"));
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
			String[] result = this.getClientIDAndDirectionByStandName(conn,accpackageid, projectid, subjectname);
			String subjectid = result[0];
			
			if(subjectid == null || "".equals(subjectid) || "null".equals(subjectid)){
				sql = "SELECT * FROM ( \n" +
				"		SELECT a.subjectid,IFNULL(b.assitemid,'') AS assitemid,a.subjectfullname1,IFNULL(b.asstotalname1,'') AS asstotalname1, \n" +
				"		CONCAT(a.subjectfullname1,IF(b.asstotalname1 IS NULL,'','/'),IFNULL(b.asstotalname1,'')) AS fullname, \n" +
				"		IF(b.assitemid IS NULL,'c_subjectentry','c_assitementry') AS isSubject \n" +
				"		FROM c_account a , c_assitementryacc b \n" +
				"		WHERE a.AccPackageID = '"+accpackageid+"' \n" +
				"		AND b.AccPackageID = '"+accpackageid+"' \n" +
				"		AND a.submonth = 1 \n" +
				"		AND b.submonth = 1 \n" +
				"		AND b.isleaf1 = 1 \n" +
				"		AND a.subjectid = b.accid \n" +	
				"		UNION  \n" +
				"		SELECT a.subjectid,'' AS assitemid,a.subjectfullname1,'' AS asstotalname1, \n" +
				"		a.subjectfullname1 AS fullname, \n" +
				"		'c_subjectentry' AS isSubject \n" +
				"		FROM c_account a  \n" +
				"		WHERE a.AccPackageID = '"+accpackageid+"' \n" +
				"		AND a.submonth = 1	 \n" +
				"	) a  \n" +
				"	WHERE a.fullname = '"+subjectname+"' \n" +
				"	ORDER BY a.subjectid,a.assitemid";
				System.out.println(sql);
				rs = st.executeQuery(sql);
				if(rs.next()){
					args.put("subjectid", rs.getString("subjectid"));
					args.put("assitemid", rs.getString("assitemid"));
					args.put("subjectfullname1", rs.getString("subjectfullname1"));
					args.put("asstotalname1", rs.getString("asstotalname1"));
					args.put("isSubject", rs.getString("isSubject"));
				}else{
					args.put("subjectid", "");
					args.put("assitemid", "");
					args.put("subjectfullname1", "");
					args.put("asstotalname1", "");
					args.put("isSubject", "");
				}
				DbUtil.close(rs);
			}else{
				sql = "select * from c_accpkgsubject where AccPackageID = '"+accpackageid+"' and subjectid = '"+subjectid+"' ";
				rs = st.executeQuery(sql);
				if(rs.next()){
					args.put("subjectfullname1", rs.getString("subjectfullname"));
				}else{
					args.put("subjectfullname1", subjectname);
				}
				DbUtil.close(rs);
				args.put("subjectid", subjectid);	
				args.put("assitemid", "");
				args.put("asstotalname1", "");
				args.put("isSubject", "c_subjectentry");
			}
			
			args.put("beginDate", beginDate);
			args.put("endDate", endDate);
			args.put("projectid", projectid);

			args.put("currency", currency);
			args.put("creditValue", creditValue);
			args.put("debitValue", debitValue);
			args.put("creditAbsValue", creditAbsValue);
			args.put("debitAbsValue", debitAbsValue);
			args.put("direction", direction);
			
//			if("".equals((String)args.get("isSubject"))) return null; //没有找到对应的科目或核算
				
			resultSql = getSql( args);	
//				resultSql = getSql(currency, creditValue, debitValue);	

			//最终查询结果
//			resultSql = this.setSqlArguments(resultSql, args);

			System.out.println("3024:sql=" + resultSql);
			
			rs = st.executeQuery(resultSql);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	//新的凭证取数
	public String getSql(Map args) {
		ASFuntion CHF = new ASFuntion();
		String isVoucher = CHF.showNull((String)args.get("完整凭证"));
		String isSubject =  CHF.showNull((String)args.get("isSubject"));

		String subjectfullname1 =  CHF.showNull((String)args.get("subjectfullname1")).trim();
		String beginDate =  CHF.showNull((String)args.get("beginDate")).trim();
		String endDate =  CHF.showNull((String)args.get("endDate")).trim();
		String subjectid =  CHF.showNull((String)args.get("subjectid")).trim();
		String assitemid =  CHF.showNull((String)args.get("assitemid")).trim();
		
		String currency =  CHF.showNull((String)args.get("currency")).trim();
		String creditValue =  CHF.showNull((String)args.get("creditValue")).trim();
		String debitValue =  CHF.showNull((String)args.get("debitValue")).trim();
		String creditAbsValue =  CHF.showNull((String)args.get("creditAbsValue")).trim();
		String debitAbsValue =  CHF.showNull((String)args.get("debitAbsValue")).trim();
		
		String direction =  CHF.showNull((String)args.get("direction")).trim();
		if("借".equals(direction)) direction = "1";
		if("贷".equals(direction)) direction = "-1";
		
		String sql = "",strWhere = "";

		//金额条件
		if(!"".equals(creditValue)) strWhere += " OR (a.dirction = -1 AND a.occurvalue >='"+creditValue+"' ) ";
		if(!"".equals(debitValue)) strWhere += " OR (a.dirction = 1 AND a.occurvalue >='"+debitValue+"' ) ";
		if(!"".equals(creditAbsValue)) strWhere += " OR (a.dirction = -1 AND ABS(a.occurvalue) >='"+creditAbsValue+"' ) ";
		if(!"".equals(debitAbsValue)) strWhere += " OR (a.dirction = 1 AND ABS(a.occurvalue) >='"+debitAbsValue+"' ) ";
		if(!"".equals(strWhere)) strWhere = " AND (1=2 " + strWhere + ") ";
		//外币条件
		if(!"".equals(currency) && !"0".equals(currency) && !"本位币".equals(currency)) strWhere += " and a.currency = '"+currency+"' ";
		//方向条件
		if("1".equals(direction) || "-1".equals(direction)) strWhere += " and a.dirction = '"+direction+"' ";
		
		if("c_subjectentry".equals(isSubject)){
			//科目
			if("是".equals(isVoucher)){
				//完整凭证
				sql = "	SELECT a.autoid,a.voucherid,a.typeid,a.oldvoucherid as oldvoucherid1,a.serail, CONCAT(a.typeid,'-',a.oldvoucherid) AS oldvoucherid,a.vchdate,a.currency,a.summary, \n" +
				"	a.subjectfullname1, a.subjectname1, \n" +
				"	IF(LOCATE('/',a.subjectfullname1)=0,a.subjectfullname1,SUBSTR(a.subjectfullname1,1,LOCATE('/',a.subjectfullname1)-1)) subSubjects, \n" +
				"	IF(a.Dirction=1,'借','贷') AS Dirction,a.OccurValue, \n" +
				"	IF(a.Dirction=-1,a.OccurValue,0.00) creditValue,IF(a.Dirction=1,a.OccurValue,0.00) debitValue, \n" +
				"	GROUP_CONCAT(DISTINCT IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1))) subjects, \n" +
				"	GROUP_CONCAT(DISTINCT IF(b.subjectname1!=b.subjectfullname1 && CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 ,CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/../',b.subjectname1)  ,b.subjectfullname1)) subjectfullnames  \n" +
				"	FROM ( \n" +
				"		SELECT b.* FROM (" +
				"			SELECT * FROM c_subjectentry a \n" +
				"			WHERE (subjectfullname1 = '"+subjectfullname1+"' OR subjectfullname1 LIKE '"+subjectfullname1+"/%') \n" +	
				"			AND vchdate >= '"+beginDate+"' \n" +
				"			AND vchdate <= '"+endDate+"' \n" + strWhere + 
				"		) a LEFT JOIN c_subjectentry b \n" +
				"		ON 1=1 \n" +
				"		AND b.vchdate >= '"+beginDate+"' \n" +
				"		AND b.vchdate <= '"+endDate+"' \n" +
				"		AND a.voucherid=b.voucherid  \n" +
				"	) a LEFT JOIN c_subjectentry b \n" +
				"	ON 1=1 \n" +
				"	AND b.vchdate >= '"+beginDate+"' \n" +
				"	AND b.vchdate <= '"+endDate+"' \n" +
				"	AND a.voucherid=b.voucherid  \n" +
				"	AND IF(a.Dirction*a.occurvalue<0,1,-1) = IF(b.Dirction*b.occurvalue<0,-1,1) \n" +
				"	GROUP BY a.autoid" +
				"	order by a.vchdate,a.voucherid";
			}else{
				//只刷分录
				sql = "	SELECT a.autoid,a.voucherid,a.typeid,a.oldvoucherid as oldvoucherid1,a.serail, CONCAT(a.typeid,'-',a.oldvoucherid) AS oldvoucherid,a.vchdate,a.currency,a.summary, \n" +
				"	a.subjectfullname1, a.subjectname1, \n" +
				"	IF(LOCATE('/',a.subjectfullname1)=0,a.subjectfullname1,SUBSTR(a.subjectfullname1,1,LOCATE('/',a.subjectfullname1)-1)) subSubjects, \n" +
				"	IF(a.Dirction=1,'借','贷') AS Dirction,a.OccurValue, \n" +
				"	IF(a.Dirction=-1,a.OccurValue,0.00) creditValue,IF(a.Dirction=1,a.OccurValue,0.00) debitValue, \n" +
				"	GROUP_CONCAT(DISTINCT IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1))) subjects, \n" +
				"	GROUP_CONCAT(DISTINCT IF(b.subjectname1!=b.subjectfullname1 && CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 ,CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/../',b.subjectname1)  ,b.subjectfullname1)) subjectfullnames  \n" +
				"	FROM ( \n" +
				"		SELECT * FROM c_subjectentry a \n" +
				"		WHERE (subjectfullname1 = '"+subjectfullname1+"' OR subjectfullname1 LIKE '"+subjectfullname1+"/%') \n" +	
				"		AND vchdate >= '"+beginDate+"' \n" +
				"		AND vchdate <= '"+endDate+"' \n" + strWhere + 
				"	) a LEFT JOIN c_subjectentry b \n" +
				"	ON 1=1 \n" +
				"	AND b.vchdate >= '"+beginDate+"' \n" +
				"	AND b.vchdate <= '"+endDate+"' \n" +
				"	AND a.voucherid=b.voucherid  \n" +
				"	AND IF(a.Dirction*a.occurvalue<0,1,-1) = IF(b.Dirction*b.occurvalue<0,-1,1) \n" +
				"	GROUP BY a.autoid" +
				"	order by a.vchdate,a.voucherid";
			}
		}else{
			//核算
			if("是".equals(isVoucher)){
				//完整凭证
				sql = "	SELECT a.autoid,a.voucherid,a.typeid,a.oldvoucherid as oldvoucherid1,a.serail, CONCAT(a.typeid,'-',a.oldvoucherid) AS oldvoucherid,a.vchdate,a.currency,a.summary, \n" +
				"	a.subjectfullname1, a.subjectname1, \n" +
				"	IF(LOCATE('/',a.subjectfullname1)=0,a.subjectfullname1,SUBSTR(a.subjectfullname1,1,LOCATE('/',a.subjectfullname1)-1)) subSubjects, \n" +
				"	IF(a.Dirction=1,'借','贷') AS Dirction,a.OccurValue, \n" +
				"	IF(a.Dirction=-1,a.OccurValue,0.00) creditValue,IF(a.Dirction=1,a.OccurValue,0.00) debitValue, \n" +
				"	GROUP_CONCAT(DISTINCT IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1))) subjects, \n" +
				"	GROUP_CONCAT(DISTINCT IF(b.subjectname1!=b.subjectfullname1 && CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 ,CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/../',b.subjectname1)  ,b.subjectfullname1)) subjectfullnames  \n" +
				"	FROM ( \n" +
				"		SELECT b.* FROM (" +
				"			SELECT distinct a.* FROM c_assitementry b ,c_subjectentry a \n" +
				"			WHERE 1=1 \n" +
				"			AND b.subjectid = '"+subjectid+"' AND b.assitemid = '"+assitemid+"' \n" +	
				"			AND a.vchdate >= '"+beginDate+"' \n" +
				"			AND a.vchdate <= '"+endDate+"' \n" + strWhere +
				"			AND b.vchdate >= '"+beginDate+"' \n" +
				"			AND b.vchdate <= '"+endDate+"' \n" +
				"			AND a.VoucherID = b.VoucherID AND a.serail = b.serail \n" + 
				"		) a LEFT JOIN c_subjectentry b \n" +
				"		ON 1=1 \n" +
				"		AND b.vchdate >= '"+beginDate+"' \n" +
				"		AND b.vchdate <= '"+endDate+"' \n" +
				"		AND a.voucherid=b.voucherid  \n" +
				"	) a LEFT JOIN c_subjectentry b \n" +
				"	ON 1=1 \n" +
				"	AND b.vchdate >= '"+beginDate+"' \n" +
				"	AND b.vchdate <= '"+endDate+"' \n" +
				"	AND a.voucherid=b.voucherid  \n" +
				"	AND IF(a.Dirction*a.occurvalue<0,1,-1) = IF(b.Dirction*b.occurvalue<0,-1,1) \n" +
				"	GROUP BY a.autoid" +
				"	order by a.vchdate,a.voucherid";
			}else{
				//只刷分录
				sql = "	SELECT a.autoid,a.voucherid,a.typeid,a.oldvoucherid as oldvoucherid1,a.serail, CONCAT(a.typeid,'-',a.oldvoucherid) AS oldvoucherid,a.vchdate,a.currency,a.summary, \n" +
				"	a.subjectfullname1, a.subjectname1, \n" +
				"	IF(LOCATE('/',a.subjectfullname1)=0,a.subjectfullname1,SUBSTR(a.subjectfullname1,1,LOCATE('/',a.subjectfullname1)-1)) subSubjects, \n" +
				"	IF(a.Dirction=1,'借','贷') AS Dirction,a.OccurValue, \n" +
				"	IF(a.Dirction=-1,a.OccurValue,0.00) creditValue,IF(a.Dirction=1,a.OccurValue,0.00) debitValue, \n" +
				"	GROUP_CONCAT(DISTINCT IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1))) subjects, \n" +
				"	GROUP_CONCAT(DISTINCT IF(b.subjectname1!=b.subjectfullname1 && CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 ,CONCAT(IF(LOCATE('/',b.subjectfullname1)=0,b.subjectfullname1,SUBSTR(b.subjectfullname1,1,LOCATE('/',b.subjectfullname1)-1)),'/../',b.subjectname1)  ,b.subjectfullname1)) subjectfullnames  \n" +
				"	FROM ( \n" +
				"		SELECT distinct a.* FROM c_assitementry b ,c_subjectentry a \n" +
				"		WHERE 1=1 \n" +
				"		AND b.subjectid = '"+subjectid+"' AND b.assitemid = '"+assitemid+"' \n" +	
				"		AND a.vchdate >= '"+beginDate+"' \n" +
				"		AND a.vchdate <= '"+endDate+"' \n" + strWhere +
				"		AND b.vchdate >= '"+beginDate+"' \n" +
				"		AND b.vchdate <= '"+endDate+"' \n" +
				"		AND a.VoucherID = b.VoucherID AND a.serail = b.serail \n" + 
				"	) a LEFT JOIN c_subjectentry b \n" +
				"	ON 1=1 \n" +
				"	AND b.vchdate >= '"+beginDate+"' \n" +
				"	AND b.vchdate <= '"+endDate+"' \n" +
				"	AND a.voucherid=b.voucherid  \n" +
				"	AND IF(a.Dirction*a.occurvalue<0,1,-1) = IF(b.Dirction*b.occurvalue<0,-1,1) \n" +
				"	GROUP BY a.autoid" +
				"	order by a.vchdate,a.voucherid";
			}
		}
		
		return sql;
		
	}
	
	/**
	 *	旧的大额取数
	 * @return String
	 */
//	public String getSql(String currency, String creditValue, String debitValue) {
//		String sql = ""
//			+ " select a.oldvoucherid, a.vchdate, a.Dirction, a.OccurValue, a.creditValue, a.debitValue, a.currency,group_concat(distinct b.subSubjects) as subjects,group_concat(distinct b.standname) as standnames,group_concat(distinct b.subjectfullname2) as subjectfullnames,summary from  \n"
//			+ " (  \n"
//
//				// +"create table t_c_subjectentry as \n"
//				+ " select * from ( \n"
//				+ " select autoid,voucherid,REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',SubjectID,','),','),',,',',') subjects,\n"
//				+ " concat(typeid,'-',oldvoucherid) as oldvoucherid,vchdate,if(Dirction=1,'借','贷') as Dirction,OccurValue,\n"
//				+ " if(Dirction=-1,OccurValue,0.00) creditValue,if(Dirction=1,OccurValue,0.00) debitValue,currency,summary \n"
//				+ " from c_subjectentry \n"
//				+ " where subjectid like '"+subjectid+"%' and vchdate>='"+beginDate+"' and vchdate<='"+endDate+"'\n"
//				+ ")a where 1=1 \n";
//
//		if (!"".equals(currency) && !"人民币".equals(currency) && currency.indexOf("本位")<0  && !"0".equals(currency)) {
//			sql += " and currency ='" + currency+"' \n";
//		}
//		if (!"".equals(creditValue) && !"".equals(debitValue)) {
//			sql += " and (creditValue >='" + creditValue + "' or debitValue>='"
//					+ debitValue+"')";
//		} else if (!"".equals(debitValue) && "".equals(creditValue)) {
//			sql += " and debitValue >='" + debitValue +"'";
//		} else if ("".equals(debitValue) && !"".equals(creditValue)) {
//			sql += " and creditValue >='" + creditValue +"'";
//
//		}
//		sql+=""
//		+ " )a inner join  \n"
//		+ " ( \n"
//		+ " 		select  subjectid,standname,subjectfullname2,if(locate('/',subjectfullname2) = 0,subjectfullname2,substring(subjectfullname2,1,locate('/',subjectfullname2)-1)) as subSubjects,SubYearMonth from c_account where SubYearMonth>='"+beginDate+"' and SubYearMonth<='"+endDate+"' and submonth='1' \n"
//		+ " )b \n"
//
//		+ " where a.subjects like concat('%,',b.subjectid,',%') and b.SubYearMonth = subString(a.vchdate,1,4) \n"
//		+ " group by a.autoid \n";
//		sql+= " order by vchdate,oldvoucherid \n";
//		return sql;
//
//	}

}