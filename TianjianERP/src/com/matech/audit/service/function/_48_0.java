package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import com.ASSys.ASpubFuntion.Func.ASFuntion;

public class _48_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		// &subjectname1=管理费用&subjectname2=累计折旧&direction1=1&direction2=-1&inside1=%25
		// ${subjectname1},${subjectname2},${direction1},${direction2},${inside1}
		// ASFuntion asf=new ASFuntion();

		String apkID = (String) args.get("curAccPackageID");
		String prjID = (String) args.get("curProjectid");

		// 统计科目名称(下面称其为借方)
		String subjectname1 = (String) args.get("subjectname1");
		// 对方科目名称(下面称其为贷方)
		String subjectname2 = (String) args.get("subjectname2");
		// 统计科目方向
		String direction1 = (String) args.get("direction1");
		// 对方科目方向
		String direction2 = (String) args.get("direction2");
		// 统计科目的下级科目名称
		String inside1 = (String) args.get("inside1");
		String sql = "";

		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();

			// 找出参数科目在客户中的名字

			// 借方科目
			sql = "" + " select  group_concat(subjectid SEPARATOR '\\',\\'')  \n"
					+ " from c_account \n" 
					+ " where accpackageid=" + apkID	+ " \n" 
					+ " and submonth=1 \n" 
					+ " and (subjectfullname2 like '"+subjectname1+"/%'  or subjectfullname2 ='"+subjectname1+"')";
			System.out.println("111:"+sql);
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname1 = rs.getString(1);
			} else {
				subjectname1 = "";
			}
			rs.close();

			// 贷方科目
			sql = "" + " select  group_concat(subjectid SEPARATOR '\\',\\'')  \n"
					+ " from c_account \n" 
					+ " where accpackageid=" + apkID +"\n"
					+ " and submonth=1 \n"
					+ " and (subjectfullname2 like '"+subjectname2+"/%'  or subjectfullname2 ='"+subjectname2+"')";
			//System.out.println("222:"+sql);
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname2 = rs.getString(1);
			} else {
				subjectname2 = "";
			}
			rs.close();

			// 查找该科目在客户中的借方科目的凭证
			if (!"%".equals(inside1)) {
				inside1 = " ( \n"
						+ "             select '"+ inside1+ "' as gs \n"
						+ "             union \n"
						+ "             select TRIM(replace(replace(replace(replace(CONCAT('"+ inside1+ "','         '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2),'`','')) \n"
						+ "             from k_key b,k_key c,k_key d \n"
						+ "             where '" + inside1+ "' like concat('%',b.key1,'%') \n"
						+ "             and '" + inside1+ "' like concat('%',c.key1,'%') \n"
						+ "             and '" + inside1+ "' like concat('%',d.key1,'%') \n" + "  \n"
						+ "     ) c \n";

				sql = " select group_concat(distinct voucherid ) as voucherid, \n"
						+ "        group_concat( autoid ) as autoid \n"
						+ " from  \n" + " c_subjectentry a \n"
						+ " inner join \n" + inside1
						//+ " -- substr　是取该科目的一级科目名称 \n" 
						+ " where accpackageid="	+ apkID + " \n" 
						+ " and dirction=" + direction1 + "  \n" 
						+ "and subjectid in ('"+subjectname1 + "') \n"
						+ " and subjectfullname1 LIKE CONCAT('%',c.gs,'%')";

			} else {

				sql = " select group_concat(distinct voucherid ) as voucherid, \n"
						+ "        group_concat( autoid ) as autoid \n"
						+ " from  \n" + " c_subjectentry a \n"
						// " -- substr　是取该科目的一级科目名称 \n" 
						+ " where accpackageid="+ apkID + " \n" 
						+ " and dirction=" + direction1 + "  \n" 
						+ "and subjectid in ('"+subjectname1 + "')";

			}

			//System.out.println("333:"+sql);
			rs = st.executeQuery(sql);

			// 借方科目的凭证编号
			String dvid = "";
			// 借方科目的分录编号，到最后是结果的借方分录号。
			String debitResult = "";
			if (rs.next()) {
				dvid = rs.getString("voucherid");
				debitResult = rs.getString("autoid");
			} else {
				dvid = "null";
				debitResult = "null";
			}
			rs.close();
			
			sql = " select \n"
					+ "      group_concat(distinct voucherid ) as voucherid,  \n"
					+ "      group_concat( autoid ) as autoid \n"
					+ " from  \n" + " c_subjectentry a \n"
					//+ " -- substr　是取该科目的一级科目名称 \n" 
					+ " where accpackageid=" + apkID + " \n" 
					+ " and a.voucherid in(" + dvid + ") \n"
					+ " and dirction=" + direction2 + "  \n"
					//+ " and a.autoid not in(" + debitResult + ") \n"
					+ "and subjectid in ('"+subjectname2 + "')";
			//System.out.println("444:"+sql);
			rs = st.executeQuery(sql);
			// 包含借方科目subjectname1并且包含贷方科目subjectname2的凭证号。
			String resultvid = "";
			// 最后贷方的所有分录id
			String creditResult = "";
			if (rs.next()) {
				resultvid = rs.getString("voucherid");
				creditResult = rs.getString("autoid");
			} else {
				resultvid = "null";
				creditResult = "null";
			}
			rs.close();
			
			sql = " select group_concat( autoid ) from c_subjectentry \n"
					+ " where accpackageid="
					+ apkID
					+ "  \n"
					+ " and voucherid in ("
					+ resultvid
					+ ") \n"
					+ " and autoid in (" + debitResult + ") \n";
			//System.out.println("555:"+sql);
			rs = st.executeQuery(sql);
			// 最后结果需要的借方分录编号
			if (rs.next()) {
				debitResult = rs.getString(1);
			} else {
				debitResult = "null";
			}
			rs.close();
			
			// =========最后用到的sql

			// debitResult是统计科目，永远都是统计[统计科目]
			String resultAutoID = "";

			resultAutoID = debitResult;

			sql = ""
					+ " 	select \n"
					+ " 		sum(if(a.occurvalue < b.occurvalue,a.occurvalue,b.occurvalue))  as totaloccur \n"
					+ "  from (\n"

					+ "  	select sum(occurvalue) as occurvalue ,voucherid \n"
					+ " 		from c_subjectentry \n" 
					+ " 		where accpackageid = "	+ apkID + " \n" 
					+ " 		and autoid in(" + debitResult + ") \n" 
					+ "      group by voucherid \n"
					+ "  ) a inner join \n"

					+ "  (\n"
					+ "  	select sum(occurvalue) as occurvalue ,voucherid \n"
					+ " 		from c_subjectentry \n" 
					+ " 		where accpackageid = "	+ apkID + " \n" 
					+ " 		and autoid in(" + creditResult	+ ") \n" 
					+ "      group by voucherid \n"
					+ "  ) b on a.voucherid=b.voucherid \n";
			//System.out.println("666:"+sql);
			rs = st.executeQuery(sql);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}

}
