package com.matech.audit.service.inventory;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.db.GetResult;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.project.ProjectService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>Title: 随机抽样的service</p>
 * <p>Description:提供各类抽样的方法</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author k
 * 2007-6-29
 */
public class InventorySamplingService {
	private Connection conn;
	private String voucherDate="";

	private String cnString;

	public String getCnString() {
		return cnString;
	}

	public void setCnString(String cnString) {
		this.cnString = cnString;
	}

	public InventorySamplingService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 分层抽样
	 * @param entryIds
	 * @param section
	 * @param projectId
	 * @return
	 */
	public String getSampling3(String entryIds, String section, String projectId)
			throws Exception {

		String sqlWhere = "";
		if (entryIds != null && !"".equals(entryIds)) {
			sqlWhere = " and autoid in(" + entryIds + ")";
		}

		//数据操作。
		GetResult gr = new GetResult(conn);

		//区间数据。。,MIN ,MAX ,VALUE
		String[] sections = section.split(";");
		String[] simgleSection = null;

		//结果
		String[] results = null;
		String result = "";

		//第个人TABLE，没有UNOIN
		simgleSection = sections[0].split(",");

		String[] auditArea = getProjectAuditArea(projectId);

		String sql = " (select billid from (select billid, "
			+ " 		sum(occurvalue) as occurvalue "
			+ " 		from c_inventoryentry "
			+ " 		where InventoryDate >='" + auditArea[0] + "' and InventoryDate <='" + auditArea[1] + "' "
				+ sqlWhere
				+ " 		group by billid ) a where 1=1 ";

		if (simgleSection[0].indexOf("[") < 0) {
			sql += " and occurvalue >" + simgleSection[0];
		}
		if (simgleSection[1].indexOf("[") < 0) {
			sql += " and occurvalue <=" + simgleSection[1];
		}

		sql += "  order by rand() limit " + simgleSection[2] + ") \n";

		//除了第一个的其它TABLE
		for (int i = 1; i < sections.length; i++) {

			simgleSection = sections[i].split(",");

			//如果用户需要抽零条，则直接跳过。
			if (simgleSection[2].equals("0")) {
				continue;
			}

			sql += " union ( select billid from (select billid, "
				+ " 		sum(occurvalue) as occurvalue "
				+ " 		from c_inventoryentry "
				+ " 		where InventoryDate >='" + auditArea[0] + "' and InventoryDate <='" + auditArea[1] + "' "
					+ sqlWhere
					+ " 		group by billid ) a where 1=1 ";

			if (simgleSection[0].indexOf("[") < 0) {
				sql += " and occurvalue >" + simgleSection[0];
			}
			if (simgleSection[1].indexOf("[") < 0) {
				sql += " and occurvalue <=" + simgleSection[1];
			}

			sql += "  order by rand() limit " + simgleSection[2] + ") \n";

		}

		try {
			String customerId = new CustomerService(conn).getCustomerIdByProjectId(projectId);
			String[][] temp = gr.getStringArrayBySQL(sql, customerId);
			results = new String[temp.length];

			for (int i = 0; i < temp.length; i++) {
				results[i] = temp[i][0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < results.length; i++) {
			result += (results[i] + ",");
		}

		if (result.lastIndexOf(",") == (result.length() - 1)) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	/**
	 * 分层抽样
	 * @param entryIds
	 * @param section
	 * @param projectId
	 * @return
	 */
	public String getSampling3ByAssitem(String entryIds, String section, String projectId)
			throws Exception {

		String sqlWhere = "";
		if (entryIds != null && !"".equals(entryIds)) {
			sqlWhere = " and autoid in(" + entryIds + ")";
		}

		//数据操作。
		GetResult gr = new GetResult(conn);

		//区间数据。。,MIN ,MAX ,VALUE
		String[] sections = section.split(";");
		String[] simgleSection = null;

		//结果
		String[] results = null;
		String result = "";

		//第个人TABLE，没有UNOIN
		simgleSection = sections[0].split(",");
		String[] auditArea = getProjectAuditArea(projectId);

		String sql = " (select voucherid from (select voucherid,subjectid, "
				+ " 		if(sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)) + sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0))>=0,sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)),-(sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0)))) as occurvalue "
				+ " 		from c_assitementry "
				+ " 		where VchDate >='" + auditArea[0] + "' and VchDate <='" + auditArea[1] + "' "
				+ sqlWhere
				+ " 		group by voucherid ) a where 1=1 ";

		if (simgleSection[0].indexOf("[") < 0) {
			sql += " and occurvalue >" + simgleSection[0];
		}
		if (simgleSection[1].indexOf("[") < 0) {
			sql += " and occurvalue <=" + simgleSection[1];
		}

		sql += "  order by rand() limit " + simgleSection[2] + ") \n";

		//除了第一个的其它TABLE
		for (int i = 1; i < sections.length; i++) {

			simgleSection = sections[i].split(",");

			//如果用户需要抽零条，则直接跳过。
			if (simgleSection[2].equals("0")) {
				continue;
			}

			sql += " union ( select voucherid from (select voucherid,subjectid, "
				+ " 		if(sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)) + sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0))>=0,sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)),-(sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0)))) as occurvalue "
				+ " 		from c_assitementry "
				+ " 		where VchDate >='" + auditArea[0] + "' and VchDate <='" + auditArea[1] + "' "
				+ sqlWhere
				+ " 		group by voucherid ) a where 1=1 ";

			if (simgleSection[0].indexOf("[") < 0) {
				sql += " and occurvalue >" + simgleSection[0];
			}
			if (simgleSection[1].indexOf("[") < 0) {
				sql += " and occurvalue <=" + simgleSection[1];
			}

			sql += "  order by rand() limit " + simgleSection[2] + ") \n";

		}

		try {
			String customerId = new CustomerService(conn).getCustomerIdByProjectId(projectId);
			String[][] temp = gr.getStringArrayBySQL(sql, customerId);
			results = new String[temp.length];

			for (int i = 0; i < temp.length; i++) {
				results[i] = temp[i][0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < results.length; i++) {
			result += (results[i] + ",");
		}

		if (result.lastIndexOf(",") == (result.length() - 1)) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	/**
	 * 返回样本总体量
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getSampling4(Map map, String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "-1";

		try {

			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			StringBuffer sql = new StringBuffer();

			ASFuntion asf = new ASFuntion();

			String subjectId = asf.showNull((String) map.get("subjectId")); // 科目编号
			String assItemId = asf.showNull((String) map.get("assItemId")); // 核算编号
			String typeId = asf.showNull((String) map.get("typeId")); // 凭证字
			String voucherId = asf.showNull((String) map.get("voucherId")); // 凭证编号
			String moneyItem = asf.showNull((String) map.get("moneyItem")); // 金额方向
			String moneyLogic = asf.showNull((String) map.get("moneyLogic")); // 金额逻辑： <,>,=,>=,<=,
			String money = asf.showNull((String) map.get("money")); // 金额
			String summary = asf.showNull((String) map.get("summary")); // 凭证摘要
			String summaryContain = asf.showNull((String) map.get("summaryContain")); // 摘要条件,包含,不包含
			String startMonth = asf.showNull((String) map.get("startMonth")); // 开始日期
			String endMonth = asf.showNull((String) map.get("endMonth")); // 结束日期
			String pandl = asf.showNull((String) map.get("pandl")); //是否包含结转
			String voucherDate =  asf.showNull((String) map.get("voucherDate"));
			String afterTotal =  asf.showNull((String) map.get("afterTotal"));
			String beforeTotal =  asf.showNull((String) map.get("beforeTotal"));


			String property = "";
			//是否包含结转凭证
			if("yes".equals(pandl)) {
				property = " and a.property not like '%2%'";
			}

			// 如果核算编号不为空,则在核算分录表进行查找,否则从科目分录表中查找
			if (!"".equals(assItemId)) {
				sql.append(" select distinct a.voucherId ");
				sql.append(" from c_assitementry a ");
				sql.append("  where a.property like '1%'  ");
				sql.append("  and a.SubjectID like '" + subjectId + "%' ");
				sql.append("  and a.assitemid like '" + assItemId + "%' ");

			} else if (!"".equals(subjectId)) {
				// 根据科目号
				sql.append(" select distinct a.voucherId ");
				sql.append(" from c_subjectentry a ");
				sql.append("  where a.property like '1%'  ");
				sql.append("  and a.SubjectID like '" + subjectId + "%' ");
				sql.append(property);
			}

			if (!"".equals(money)) {
				if ("".equals(assItemId)) {
					if ("DebitValue".equals(moneyItem)) {
						// 按照科目来查找金额
						money = "(dirction = 1 and occurvalue " + moneyLogic
								+ " " + money + ")";
					} else {
						money = "(dirction = (-1) and occurvalue " + moneyLogic
								+ " " + money + ")";
					}
				} else {
					// 按照核算来查找金额
					if ("DebitValue".equals(moneyItem)) {
						money = " (dirction = 1 and assitemsum " + moneyLogic
								+ " " + money + ")";
					} else {
						money = "(dirction = (-1) and assitemsum " + moneyLogic
								+ " " + money + ")";
					}
				}

				sql.append(" and " + money);
			}

			// 如果凭证字不为空
			if (!"".equals(typeId)) {
				sql.append(" and a.typeid ='" + typeId + "' ");
			}

			// 如果凭证号不为空
			if (!"".equals(voucherId)) {
				sql.append(" and a.oldvoucherid='" + voucherId + "' ");
			}

			// 如果摘要不为空
			if (!"".equals(summary) && !"".equals(summaryContain)) {
				sql.append(" and a.Summary " + summaryContain + " '%" + summary
						+ "%' ");
			}

			// 如果凭证日期不为空
			if (!"".equals(startMonth) || !"".equals(endMonth)) {
				sql.append(" and a.VchDate >='" + startMonth
						+ "'  and a.VchDate <='" + endMonth + "'  ");
			}

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				result += "," + rs.getString(1);
			}

			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" 	select voucherid from ( ");
			sbSql.append(" 		select voucherid from ( ");
			sbSql.append(" 			select  DISTINCT voucherid ");
			sbSql.append("			from c_subjectentry ");
			sbSql.append("			where voucherid in(" + result +") ");
			sbSql.append(" 			and vchdate > '" + voucherDate + "' ");
			sbSql.append(" 			order by vchdate asc,typeid asc,oldvoucherId asc " );
			sbSql.append(" 		) as a limit " + afterTotal);
			sbSql.append(" 	) a ");
			sbSql.append(" 		union ");
			sbSql.append(" 	select voucherid from ( ");
			sbSql.append(" 		select voucherid from ( ");
			sbSql.append(" 			select  DISTINCT voucherid ");
			sbSql.append("			from c_subjectentry ");
			sbSql.append("			where voucherid in(" + result +") ");
			sbSql.append(" 			and vchdate <= '" + voucherDate + "' " );
			sbSql.append("			order by vchdate desc,typeid desc,oldvoucherId desc ");
			sbSql.append(" 		) as b limit " + beforeTotal);
			sbSql.append(" 	) b ");

			ps = conn.prepareStatement(sbSql.toString());
			System.out.println("s4:" + sbSql.toString());
			rs = ps.executeQuery();

			result = "-1";

			while (rs.next()) {
				result += "," + rs.getString(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return result;

	}

	/**
	 * 根据金额抽样分录
	 * @param autoIds
	 * @param moneyPercent
	 * @param departID
	 * @return
	 */
	public String getSampling2(String ppSql,double moneyPercent,String departID) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		double debitSum = 0;
		double crebitSum = 0;
		double debitTotal = 0;
		double crebitTotal = 0;

		String resultStr = "";

		try {
			if(moneyPercent >= 100) {
				String sql = "select ifnull(group_concat(distinct billid),-1) from ( " + ppSql + ") a ";
				new DBConnect().changeDataBase(departID, conn);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()) {
					return rs.getString(1);
				}
				
				rs.close();
				ps.close();
			}
			
			

			//求出总额
			String sql = "select  sum(a_OccurValue),sum(b_OccurValue) from ("
						+ ppSql
						+ ") a";
			new DBConnect().changeDataBase(departID, conn);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				debitSum = rs.getDouble(1) * moneyPercent / 100;
				crebitSum = rs.getDouble(2) * moneyPercent / 100;
			}

			
			rs.close();
			ps.close();
			//随机排序,计算出借方的所有凭证的凭证号和金额
			sql = "select billid,sum(a_OccurValue) from ( "
				+ ppSql + ") a "
				+ " where a_OccurValue != 0 "
				+ " group by billid "
				+ " order by rand()";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()) {
				resultStr += rs.getString(1) + ",";
				debitTotal += rs.getDouble(2);
				if(debitTotal >= debitSum) {
					break;
				}
			}

			
			rs.close();
			ps.close();
			//随机排序,计算出贷方的所有凭证的凭证号和金额
			sql = "select billid,sum(b_OccurValue) from ( "
				+ ppSql + ") a "
				+ " where b_OccurValue != 0 "
				+ " group by billid "
				+ " order by rand()";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()) {
				resultStr += rs.getString(1) + ",";
				crebitTotal += rs.getDouble(2);
				if(crebitTotal >= crebitSum) {
					break;
				}
			}
			
			rs.close();
			ps.close();
			System.out.println("resultStr:" + resultStr);

			System.out.println("debitSum:" + new java.text.DecimalFormat("#0.00").format(debitSum));
			System.out.println("crebitSum:" + new java.text.DecimalFormat("#0.00").format(crebitSum));

			System.out.println("debitTotal:" + new java.text.DecimalFormat("#0.00").format(debitTotal));
			System.out.println("crebitTotal:" + new java.text.DecimalFormat("#0.00").format(crebitTotal));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return resultStr;

	}

	//以String[]的形式返回SQL语句的第一条记录
	public String[] getSQLResult(String sql, String departID) {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String[] result = null;

		try {
			new DBConnect().changeDataBase(departID, conn);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			rs.beforeFirst();
			result = new String[rsmd.getColumnCount()];

			if (rs.next()) {
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					result[i] = asf.showNull(rs.getString(i + 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	//    按张数抽样
	public void updateSampling(String sqlStr, String type, String projectId, String createor, String subjectid,String typeSummary) {

		if("".equals(voucherDate))
			voucherDate=sqlStr;
		PreparedStatement ps = null;
		sqlStr = sqlStr.substring(0, sqlStr.length() - 1);

		ASFuntion asf = new ASFuntion();
		String date = asf.getCurrentDate();
		String flowId = "";	//抽凭过程ID

		try {

			//根据项目id切换数据库。
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			// 重新设置凭证的抽
			if (type.equals("2")) {
				String deleteSQL = " delete from z_inventorypotcheck "
								+ " where projectid=? \n "
								+ " and Createor=? \n "
								+ " and Inventoryid = ?  \n "
								+ " and judge like '随机抽凭%' ";
				ps = conn.prepareStatement(deleteSQL);
				ps.setString(1, projectId);
				ps.setString(2, createor);
				ps.setString(3, subjectid);
				ps.execute();
			}

			try {

				flowId = DELUnid.getNumUnid();

				//记录随机抽凭过程
				String sql = " insert into z_inventorysampleflow "
						+ " (flowId,sampleDate,userId,projectId,sampleFlow,sampleMethod,selectSample,inventoryid,property) "
						+ " values " + " (?, now(),?,?,?,?,?,?,'随机抽样') ";
				ps = conn.prepareStatement(sql);

				ps.setString(1, flowId);
				ps.setString(2, createor);
				ps.setString(3, projectId);
				ps.setString(4, getCnString());
				ps.setString(5, "随机抽凭" + typeSummary);
				ps.setString(6, typeSummary);
				ps.setString(7, subjectid);

				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}

			//	insert sql must be changed.
			StringBuffer insertSQL2 = new StringBuffer();

	
			insertSQL2.append(" Insert Into z_inventorypotcheck(																	\n ");
			insertSQL2.append(" 	ProjectID, billid, Judge, Createor, QuestDate, entryId,										\n ");	//1
			insertSQL2.append(" 	Property, inventoryid, entryStockId, entryInventoryEntryId, entryInventoryEntryType, entryInventoryDate, 		\n ");	//2
			insertSQL2.append(" 	flowid, entryInventoryInOutType, entrySerail, entryInventoryId, entryInventoryType, entryQuantity,			\n ");	//3
			insertSQL2.append(" 	entryCurrValue, entryCurrency, entryOccurValue,entryInvoiceId,entryoldVoucherID,entryTypeID,	\n ");	//4
			insertSQL2.append(" 	entryVchDate, entrySubjectID, entryFillUser, entryAuditUser, entryKeepUser, entryProperty,entryInventoryName,entryInventoryFullName,entryprices,entryStockName,entryStockFullName,entryUomUnit,entryVendorName ) \n ");	//5
			insertSQL2.append(" select ");
			insertSQL2.append(		projectId + " as ProjectID, b.billid as billid, '随机抽凭"+typeSummary+"' as Judge,'" + createor + "' as createor,'" + date + "' as QuestDate, b.autoid as entryId,					\n ");	//1
			insertSQL2.append(" 	0 as Property, '" + subjectid +"' as inventoryid, b.StockId as entryStockId, b.InventoryEntryId as entryInventoryEntryId, b.InventoryEntryType as entryInventoryEntryType, b.InventoryDate as entryInventoryDate, 		\n ");	//2
			insertSQL2.append(" 	'" + flowId + "' as flowid,b.InventoryInOutType as entryInventoryInOutType, b.Serail as entrySerail,b.InventoryId as entryInventoryId,b.InventoryType as entryInventoryType,b.Quantity as entryQuantity, 			\n ");	//3
			insertSQL2.append(" 	b.CurrValue as entryCurrValue,b.Currency as entryCurrency,b.OccurValue as entryOccurValue, b.InvoiceId as entryInvoiceId, b.oldVoucherID as entryoldVoucherID, b.TypeID as entryTypeID,b.VchDate as entryVchDate, 	\n ");	//4
			insertSQL2.append(" 	 b.SubjectID as entrySubjectID, b.FillUser as entryFillUser, b.AuditUser as entryAuditUser,b.KeepUser as entryKeepUser,b.Property as entryProperty,b.InventoryName as entryInventoryName,b.InventoryFullName as entryInventoryFullName,b.prices as entryprices,b.StockName as entryStockName,b.StockFullName as entryStockFullName,b.UomUnit as entryUomUnit,b.VendorName as entryVendorName 	\n ");	//5
			insertSQL2.append(" from c_inventoryentry b  \n");
//			insertSQL2.append(" 	left join ( select autoid,subjectId ");
//			insertSQL2.append(" 				from c_subjectentry ");
//			insertSQL2.append(" 				where autoid in (" + sqlStr + ") ");
//			insertSQL2.append(" 				and subjectid like concat('" + subjectid + "','%') ) a \n ");
//			insertSQL2.append(" 	on b.autoid=a.autoid \n ");
			insertSQL2.append(" where b.autoid in (" + sqlStr + ")  \n");
			//insertSQL2.append("	and not exists ( \n ");
			//insertSQL2.append("	  			select 1 from z_voucherspotcheck \n ");
			//insertSQL2.append("	  			where projectid='" + projectId + "'   	\n ");
			//insertSQL2.append("	  			and createor='" + createor + "' 		\n ");
			//insertSQL2.append("	  			and subjectid = a.subjectId 	\n ");
			//insertSQL2.append("	  			and entryId in (" + sqlStr + ")  		\n ");
			//insertSQL2.append("	  	) ");

			System.out.println(insertSQL2.toString());
			ps = conn.prepareStatement(insertSQL2.toString());

			ps.execute();

			//删除重复行
			String sql = " delete a from z_inventorypotcheck a  "
						+ " inner join ( "
						+ " 	select createor,inventoryid,entryId "
						+ " 	from z_inventorypotcheck  "
						+ " 	where projectid='" + projectId +"' "
						+ " 	group by createor,inventoryid,entryId "
						+ " 	having count(*) > 1) b "
						+ " 		on a.createor=b.createor "
						+ " 		and a.inventoryid=b.inventoryid "
						+ " 		and a.entryId=b.entryId "
						+ " where a.projectid='" + projectId + "'  "
						+ " and a.flowid='" + flowId + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得sql返回的voucherid
	 * @param sql String
	 * @param voucheridColName String
	 * @return String
	 */
	public String getVoucherID(String sql, String voucheridColName,
			String departID) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		String result = "";

		try {
			new DBConnect().changeDataBase(departID, conn);
			if (conn == null) {
				throw new Exception("获取连接失败！");
			}

			String sqlStr = "select distinct " + voucheridColName + "  from ( "
					+ sql + " ) a ";

			ps = conn.prepareStatement(sqlStr);

			if (ps == null) {
				throw new Exception("获取PreparedStatement失败！");
			}

			rs = ps.executeQuery();

			if (rs == null) {
				throw new Exception("获取ResultSet失败！");
			}

			while (rs.next()) {
				sb.append(rs.getString(1) + "','");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result = sb.toString();

		//去出最后一个","号
		if (result.length() > 0
				&& result.lastIndexOf(",") == (result.length() - 1)) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	/**
	 * 得到分层抽样的区间。
	 * @param projectID 项目编号
	 * @param sction    区间  1000,5000,10000 ...从小到大
	 * @return String
	 */

	public String getSection(String projectID, String sction,
			String vs) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		String[] sctions = null;
		String[] stats = null; //统计
		String stat = "";
		String[] displaySections = null;//显示的条件区间
		String displaySection = "";

		try {

			//如果刚进入页面。则全选数据的区间。
			new DBConnect().changeDataBaseByProjectid(conn, projectID);
			if (sction == null || sction.equals("")) {
				sction = "";
				sql = "select moneyCondition,projectid from z_bigmoney where projectid=? or projectid=0 order by moneyCondition";
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectID);
				rs = ps.executeQuery();
				while (rs.next()) {
					sction += (rs.getString(1) + ",");
				}

				sction = "[-∞]," + sction + "[+∞]";

			}

			//化成数组区间。
			sctions = sction.split(",");

			//统计区间张数。
			sql = " select section , count(distinct billid) from \n"
					+ " (   \n" + "    select\n";
			if (sctions[0].indexOf("[") >= 0 && sctions[1].indexOf("[") >= 0) {
				sql += "     case when 1=1 then '[-∞] - [+∞]' ";
			} else if (sctions[0].indexOf("[") >= 0) {
				sql += "      case when occurvalue <=" + sctions[1]
						+ " then '[-∞] - " + sctions[1] + "' \n";
			} else if (sctions[1].indexOf("[") >= 0) {
				sql += "      case when occurvalue >" + sctions[0] + " then '"
						+ sctions[0] + " - [+∞]' \n";
			} else {
				sql += "      case when (occurvalue <=" + sctions[1]
						+ " and occurvalue > " + sctions[0] + ") then '"
						+ sctions[0] + " - " + sctions[1] + "' \n";
			}
			for (int i = 2; i < sctions.length - 1; i++) {
				sql += "      when occurvalue <= " + sctions[i] + "   then '"
						+ sctions[i - 1] + " - " + sctions[i] + "' \n";
			}
			//last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
			if (sctions.length > 2) {
				if (sctions[sctions.length - 1].indexOf("[") >= 0) {
					sql += "      else '" + sctions[sctions.length - 2]
							+ " - [+∞]' end as section, \n";
				} else {
					sql += "      when occurvalue <="
							+ sctions[sctions.length - 1] + " then '"
							+ sctions[sctions.length - 2] + " - "
							+ sctions[sctions.length - 1]
							+ "' end as section, \n";
				}
			} else {
				sql += " end as section, \n";
			}

			//order by column 构造一个字段，用于排序。
			if (sctions[0].indexOf("[") >= 0 && sctions[1].indexOf("[") >= 0) {
				sql += "     case when 1=1 then 1 ";
			} else if (sctions[0].indexOf("[") >= 0) {
				sql += "      case when occurvalue <=" + sctions[1]
						+ " then 1 \n";
			} else if (sctions[1].indexOf("[") >= 0) {
				sql += "      case when occurvalue >" + sctions[0]
						+ " then 1 \n";
			} else {
				sql += "      case when (occurvalue <=" + sctions[1]
						+ " and occurvalue > " + sctions[0] + ") then 1 \n";
			}
			for (int i = 2; i < sctions.length - 1; i++) {
				sql += "      when occurvalue <= " + sctions[i] + "   then "
						+ (i + 1) + " \n";
			}
			//last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
			if (sctions.length > 2) {
				if (sctions[sctions.length - 1].indexOf("[") >= 0) {
					sql += "      else " + sctions.length + " end as ob, \n";
				} else {
					sql += "      when occurvalue <="
							+ sctions[sctions.length - 1] + " then "
							+ sctions.length + " end as ob, \n";
				}
			} else {
				sql += " end as ob, \n";
			}

			String[] auditArea = getProjectAuditArea(projectID);

			sql += " occurvalue,billid "
				+ " from  (select billid,InventoryId, "
				+ " 		sum(occurvalue) as occurvalue "
				+ " 		from c_inventoryentry "
				+ " 		where InventoryDate >='" + auditArea[0] + "' and InventoryDate <='" + auditArea[1] + "' "
				+ " 		and autoid in(" + vs + ")"
				+ " 		group by billid ) a  "
				+ " ) a where a.section > '' "
				+ " group BY ob order by ob ";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs == null || !rs.next()) {
				result += "<tr>\n"
						+ "<td align=\"center\"> <br>您选择的金额区间没有存货单据</br> </td>\n"
						+ "</tr>\n";
				return " <table id=\"sectionTable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"  >\n"
						+ result + "</table>\n";
			}

			rs.beforeFirst();

			while (rs.next()) {
				stat += (rs.getString(2) + ",");
				displaySection += (rs.getString(1) + ",");
			}

			//	        化成数组
			stats = stat.split(",");
			displaySections = displaySection.split(",");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//构建表格。

		//表头
		result += "<tr>\n" + "<td align=\"center\">金额区间</td>\n"
				+ "<td align=\"center\">总张数</td>\n"
				+ "<td align=\"center\">抽样张数</td>\n" + "</tr>\n";
		for (int i = 0; i < stats.length - 1; i++) {
			result += "<tr>\n" + "<td align=\"center\">"
					+ formatSection(displaySections[i]) + "</td>\n"
					+ "<td align=\"center\"><input value=\"" + stats[i]
					+ "\" readonly=\"true\" /></td>\n"
					+ "<td align=\"center\"><input value=\"0\" beginSection=\""
					+ displaySections[i].split(" - ")[0].trim()
					+ "\" endSection=\""
					+ displaySections[i].split(" - ")[1].trim()
					+ "\" /></td>\n" + "</tr>\n";
		}
		//最后一行。需要自动填上最大张数。
		result += "<tr>\n"
				+ "<td align=\"center\">"
				+ formatSection(displaySections[displaySections.length - 1])
				+ "</td>\n"
				+ "<td align=\"center\"><input value=\""
				+ stats[stats.length - 1]
				+ "\" readonly=\"true\" /></td>\n"
				+ "<td align=\"center\"><input value=\""
				+ stats[stats.length - 1]
				+ "\" beginSection=\""
				+ displaySections[displaySections.length - 1].split(" - ")[0]
						.trim()
				+ "\" endSection=\""
				+ displaySections[displaySections.length - 1].split(" - ")[1]
						.trim() + "\" /></td>\n" + "</tr>\n";

		result = " <table id=\"sectionTable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"  >\n"
				+ result + "</table>\n";
		return result;
	}

	/**
	 * 得到分层抽样的区间。
	 * @param projectID 项目编号
	 * @param sction    区间  1000,5000,10000 ...从小到大
	 * @return String
	 */

	public String getSectionByAssitem(String projectID, String sction,
			String assitemIds) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		String[] sctions = null;
		String[] stats = null; //统计
		String stat = "";
		String[] displaySections = null;//显示的条件区间
		String displaySection = "";

		try {

			//如果刚进入页面。则全选数据的区间。
			new DBConnect().changeDataBaseByProjectid(conn, projectID);

			if (sction == null || sction.equals("")) {
				sction = "";
				sql = "select moneyCondition,projectid from z_bigmoney where projectid=? or projectid=0 order by moneyCondition";
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectID);
				rs = ps.executeQuery();
				while (rs.next()) {
					sction += (rs.getString(1) + ",");
				}

				sction = "[-∞]," + sction + "[+∞]";

			}

			//化成数组区间。
			sctions = sction.split(",");

			//统计区间张数。
			sql = " select section , count(distinct voucherid) from \n"
					+ " (   \n" + "    select\n";
			if (sctions[0].indexOf("[") >= 0 && sctions[1].indexOf("[") >= 0) {
				sql += "     case when 1=1 then '[-∞] - [+∞]' ";
			} else if (sctions[0].indexOf("[") >= 0) {
				sql += "      case when occurvalue <=" + sctions[1]
						+ " then '[-∞] - " + sctions[1] + "' \n";
			} else if (sctions[1].indexOf("[") >= 0) {
				sql += "      case when occurvalue >" + sctions[0] + " then '"
						+ sctions[0] + " - [+∞]' \n";
			} else {
				sql += "      case when (occurvalue <=" + sctions[1]
						+ " and occurvalue > " + sctions[0] + ") then '"
						+ sctions[0] + " - " + sctions[1] + "' \n";
			}
			for (int i = 2; i < sctions.length - 1; i++) {
				sql += "      when occurvalue <= " + sctions[i] + "   then '"
						+ sctions[i - 1] + " - " + sctions[i] + "' \n";
			}
			//last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
			if (sctions.length > 2) {
				if (sctions[sctions.length - 1].indexOf("[") >= 0) {
					sql += "      else '" + sctions[sctions.length - 2]
							+ " - [+∞]' end as section, \n";
				} else {
					sql += "      when occurvalue <="
							+ sctions[sctions.length - 1] + " then '"
							+ sctions[sctions.length - 2] + " - "
							+ sctions[sctions.length - 1]
							+ "' end as section, \n";
				}
			} else {
				sql += " end as section, \n";
			}

			//order by column 构造一个字段，用于排序。
			if (sctions[0].indexOf("[") >= 0 && sctions[1].indexOf("[") >= 0) {
				sql += "     case when 1=1 then 1 ";
			} else if (sctions[0].indexOf("[") >= 0) {
				sql += "      case when occurvalue <=" + sctions[1]
						+ " then 1 \n";
			} else if (sctions[1].indexOf("[") >= 0) {
				sql += "      case when occurvalue >" + sctions[0]
						+ " then 1 \n";
			} else {
				sql += "      case when (occurvalue <=" + sctions[1]
						+ " and occurvalue > " + sctions[0] + ") then 1 \n";
			}
			for (int i = 2; i < sctions.length - 1; i++) {
				sql += "      when occurvalue <= " + sctions[i] + "   then "
						+ (i + 1) + " \n";
			}
			//last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
			if (sctions.length > 2) {
				if (sctions[sctions.length - 1].indexOf("[") >= 0) {
					sql += "      else " + sctions.length + " end as ob, \n";
				} else {
					sql += "      when occurvalue <="
							+ sctions[sctions.length - 1] + " then "
							+ sctions.length + " end as ob, \n";
				}
			} else {
				sql += " end as ob, \n";
			}

			String[] auditArea = getProjectAuditArea(projectID);

			sql += " occurvalue,voucherid "
				+ " from  (select voucherid,subjectid, "
				+ " 		if(sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)) + sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0))>=0,sum(if(dirction*AssItemSum>0,dirction*AssItemSum,0)),-(sum(if(dirction*AssItemSum<0,dirction*AssItemSum,0)))) as occurvalue "
				+ " 		from c_assitementry "
				+ " 		where VchDate >='" + auditArea[0] + "' and VchDate <='" + auditArea[1] + "' "
				+ " 		and autoid in(" + assitemIds + ")"
				+ " 		group by voucherid ) a  "
				+ " ) a where a.section > '' "
				+ " group BY ob order by ob ";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs == null || !rs.next()) {
				result += "<tr>\n"
						+ "<td align=\"center\"> <br>您选择的金额区间没有凭证</br> </td>\n"
						+ "</tr>\n";
				return " <table id=\"sectionTable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"  >\n"
						+ result + "</table>\n";
			}

			rs.beforeFirst();

			while (rs.next()) {
				stat += (rs.getString(2) + ",");
				displaySection += (rs.getString(1) + ",");
			}

			//	        化成数组
			stats = stat.split(",");
			displaySections = displaySection.split(",");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//构建表格。

		//表头
		result += "<tr>\n" + "<td align=\"center\">金额区间</td>\n"
				+ "<td align=\"center\">总张数</td>\n"
				+ "<td align=\"center\">抽样张数</td>\n" + "</tr>\n";
		for (int i = 0; i < stats.length - 1; i++) {
			result += "<tr>\n" + "<td align=\"center\">"
					+ formatSection(displaySections[i]) + "</td>\n"
					+ "<td align=\"center\"><input value=\"" + stats[i]
					+ "\" readonly=\"true\" /></td>\n"
					+ "<td align=\"center\"><input value=\"0\" beginSection=\""
					+ displaySections[i].split(" - ")[0].trim()
					+ "\" endSection=\""
					+ displaySections[i].split(" - ")[1].trim()
					+ "\" /></td>\n" + "</tr>\n";
		}
		//最后一行。需要自动填上最大张数。
		result += "<tr>\n"
				+ "<td align=\"center\">"
				+ formatSection(displaySections[displaySections.length - 1])
				+ "</td>\n"
				+ "<td align=\"center\"><input value=\""
				+ stats[stats.length - 1]
				+ "\" readonly=\"true\" /></td>\n"
				+ "<td align=\"center\"><input value=\""
				+ stats[stats.length - 1]
				+ "\" beginSection=\""
				+ displaySections[displaySections.length - 1].split(" - ")[0]
						.trim()
				+ "\" endSection=\""
				+ displaySections[displaySections.length - 1].split(" - ")[1]
						.trim() + "\" /></td>\n" + "</tr>\n";

		result = " <table id=\"sectionTable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"  >\n"
				+ result + "</table>\n";
		return result;
	}

	/**
	 * 用于分层抽样的格式化输出
	 * @return
	 */
	private String formatSection(String arg) {

		String beginResult = "";
		String endResult = "";
		DecimalFormat df = new DecimalFormat("###,##0.##");
		try {
			if (arg.split(" - ")[0].indexOf("[") >= 0) {
				beginResult = arg.split(" - ")[0];
			} else {
				String temp = arg.split(" - ")[0].trim();
				beginResult = df.format(Double.parseDouble(temp));
			}
			if (arg.split(" - ")[1].indexOf("[") >= 0) {
				endResult = arg.split(" - ")[1];
			} else {
				String temp = arg.split(" - ")[1].trim();
				endResult = df.format(Double.parseDouble(temp));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beginResult + " - " + endResult;
	}

	/**
     * 获得项目的审计区间
     * @param projectId
     * @return
     * @throws Exception
     */
    public String[] getProjectAuditArea(String projectId) throws Exception {
		String[] result = {"", ""};
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

		    String sql = " select audittimebegin,audittimeend "
		    			+ " from asdb.z_project "
		    			+ " where projectid=? ";
		    ps = conn.prepareStatement(sql);
		    ps.setString(1, projectId);
		    rs = ps.executeQuery();

		    if (rs.next()) {
		        String strStart = rs.getString(1);
		        String strEnd = rs.getString(2);

		        if (strStart != null && strStart.length() == 10) {
		        	result[0] = strStart;
		        }

		        if (strEnd != null && strEnd.length() == 10) {
		        	result[1] = strEnd;
		        }
		    }

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return result;
	}

    /**
     * 获得抽凭科目的辅助核算
     * @param projectId
     * @return
     * @throws Exception
     */
    public ResultSet getAssitemTable(String projectId,String accPackageId,String subjectId) throws Exception {

    	String strStartYearMonth = "";
    	String strEndYearMonth = "";
    	String[] strStartVchdates = null;
    	String[] strEndVchdates = null;
    	int[] result = null;
    	PreparedStatement ps = null;
		ResultSet rs = null;
		int i=0;
		try{

		result = new ProjectService(conn)
			.getProjectAuditAreaByProjectid(projectId);

		strStartVchdates = new String[result[2] - result[0] + 1];
		strEndVchdates = new String[result[2] - result[0] + 1];

		strStartYearMonth = String.valueOf(result[0] * 12 + result[1]);
		strEndYearMonth = String.valueOf(result[2] * 12 + result[3]);

		for (i = result[0]; i <= result[2]; i++) {
			if (i == result[0]) {
				if (result[1] < 10)
					strStartVchdates[0] = String.valueOf(i) + "-0"
							+ String.valueOf(result[1]) + "-01";
				else
					strStartVchdates[0] = String.valueOf(i) + "-"
							+ String.valueOf(result[1]) + "-01";
			} else {

				strStartVchdates[i - result[0]] = String.valueOf(i)
						+ "-01-01";
			}

			if (i == result[2]) {
				if (result[3] < 10)
					strEndVchdates[i - result[0]] = String.valueOf(i)
							+ "-0" + String.valueOf(result[3]) + "-31";
				else
					strEndVchdates[i - result[0]] = String.valueOf(i) + "-"
							+ String.valueOf(result[3]) + "-31";
			} else {
				strEndVchdates[i - result[0]] = String.valueOf(i)
						+ "-12-31";
			}


		}

	String 	sql ="select assitemid,assitemname,sum(ifnull(shu,0)) as shu,debitocc,debittotalocc,debitrate,creditocc,credittotalocc,creditrate,ifnull(createor,'') \n"
		 	+ "  from ( "
			+ "select a.assitemid,a.assitemname, \n"
			+ " IFnull(c.debitocc,0.00) debitocc,debittotalocc, \n"
			+ "  abs(IFNULL(c.debitocc/debittotalocc,0.00)*100) as debitrate, \n"
			+ "  ifnull(c.creditocc,0.00) creditocc,credittotalocc, \n"
			+ "  abs(ifnull(c.creditocc/credittotalocc,0.00)*100) as creditrate,d.createor,shu  \n"
			+ "  from  \n"
			+ " (  \n"
			+ "   select assitemid,assitemname ,sum(debitocc) as debittotalocc,sum(creditocc) as credittotalocc \n"
			+ "   from c_assitementryacc  \n"
			+ "   where subyearmonth*12+submonth>="
			+ strStartYearMonth
			+ " and subyearmonth*12+submonth<="
			+ strEndYearMonth
			+" \n"
			+ " and isleaf1=1 and accid like '"+subjectId+"%' group by assitemid \n"
			+ " ) a  \n"
			+ "   left join \n"
			+ "   (   \n"
			+ "   /* 抽凭的统计 */ \n"
			+ "	    select assitemid,sum(debitocc) as debitocc,sum(creditocc) as creditocc,sum(shu) as shu \n"
			+ "	    from   ( \n";

	for (i = result[0]; i <= result[2]; i++) {
		if (i != result[0]) {
			sql += "union \n";
		}

		sql += "     	select    \n"
				+ " assitemid, \n"
				+ "         sum(case dirction when 1 then assitemsum else 0 end) as debitocc, \n"
				+ "         sum(case dirction when -1 then assitemsum else 0 end) as creditocc,count(1) as shu   \n"
				+ "     from c_assitementry a, \n" + "     (   \n"
				+ "       select distinct vchid,subjectid from z_voucherspotcheck \n"
				+ "       where projectid = " + projectId
				+ " and subjectid like '"+subjectId+"%' \n" + "     ) c   \n"
				+ "     where a.vchdate>='"
				+ strStartVchdates[i - result[0]] + "' and a.vchdate<='"
				+ strEndVchdates[i - result[0]] + "' \n"
				+ "     and a.voucherid=c.vchid \n"
				+ "		and a.subjectid=c.subjectid \n"
				+ "     group by assitemid \n";
	}

	sql += "     ) t group by assitemid \n"
			+ "   ) c ON a.assitemid=c.assitemid  \n"
			+ "   left join  \n"
			+ "   (   \n"
			+ "   /* 抽凭的人名统计 */ \n"
			+ "   select assitemid,group_concat(distinct createor) as createor \n"
			+ "	    from ( \n";

	for (i = result[0]; i <= result[2]; i++) {
		if (i != result[0]) {
			sql += "union \n";
		}

		sql += "     select \n"
				+ " assitemid, e.name as createor \n"
				+ "     from c_assitementry a \n"
				+ "     inner join \n"
				+ "     (   \n"
				+ "       select distinct vchid,subjectid,createor from z_voucherspotcheck \n"
				+ "       where projectid = " + projectId
				+ " and subjectid like '"+subjectId+"%' \n" + "     ) c   \n"
				+ "     on a.voucherid=c.vchid \n"
				+ "		and a.subjectid=c.subjectid \n"
				+ "     left join k_user e  \n"
				+ "     on c.createor=e.id  \n" + "     where a.subjectid like '"+subjectId+"%' and a.vchdate>='"
				+ strStartVchdates[i - result[0]] + "' and a.vchdate<='"
				+ strEndVchdates[i - result[0]] + "' \n";
	}

	sql += "      ) t group by assitemid \n" + "     \n"
			+ "   ) d ON a.assitemid=d.assitemid \n"
			+ ")a group by assitemid \n"
			+ "	order by assitemid asc";

		ps=conn.prepareStatement(sql);
		rs=ps.executeQuery();


		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		//	DbUtil.close(ps);
		//	DbUtil.close(conn);
		}

    	return rs;

    }

    /**
     * 删除随机抽凭历史记录
     * @param projectId 项目编号
     * @param flowId 流程编号
     * @throws Exception
     */
    public void removeHistory(String projectId, String flowId) throws Exception {

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		DbUtil dbUtil = new DbUtil(conn);

		Object[] args = new Object[]{projectId, flowId};

		//删除抽凭表
		String strSql = " delete from z_inventorypotcheck "
			   		  + " where projectId=? "
			   		  + " and flowId=? ";


		dbUtil.executeUpdate(strSql, args);
		//删除抽凭流程表
		strSql = " delete from z_inventorysampleflow "
			   + " where projectId=? "
			   + " and flowId=? ";

		dbUtil.executeUpdate(strSql, args);


    }

	public String getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
}
