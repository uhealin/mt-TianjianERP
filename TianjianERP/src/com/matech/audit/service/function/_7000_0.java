package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.db.DbUtil;

/**
 * 
 * @author zyq
 * 
 * 取本科目借（贷）方的所对应科目的发生额
 * 参数：month：如果有指定月，则按月汇总
 * 		debitsubjectname：借方科目
 * 		creditsubjectname：贷方科目
 * 		total：刷年汇总值，如果不为空，刷年汇总
 * 		directtion:指定按借方还是贷方汇总1为借方-1为贷方
 * 
 * 输出：mymonth:月份；occurvalue：发生额
 * 如：=取列公式覆盖(7000,"","occurvalue","&debitsubjectname=手续费&creditsubjectname=现金&directtion=-1&total=1")
 */

public class _7000_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		String accpackageid = (String) args.get("curAccPackageID");
		String month = (String) args.get("month");// 如果有指定月，则按月汇总
		String debitsubjectname = (String) args.get("debitsubjectname");// 借方科目
		String creditsubjectname = (String) args.get("creditsubjectname");// 贷方科目
		String total = (String) args.get("total");// 刷年汇总值

		String directtion = (String) args.get("directtion");// 指定按借方还是贷方汇总1为借方-1为贷方

		String sql_creditsubjectid = "";
		String sql_debitsubjectid = "";
		String sql = "";
		String sql1 = "";
		String voucherid = "";

		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (!"".equals(month)) {
			if (month.length() == 1) {
				month = "0" + month;
			}
			month = " and vchdate like '%-" + month + "-%' ";
		} else {
			month = "";
		}

		try {
			
			sql_creditsubjectid = " select distinct subjectid from c_account \n"
					+ " where accpackageid='"+accpackageid+"' \n"
					+ " and subjectfullname2 = '"+creditsubjectname+"' \n";
			
			System.out.println("sql_subjectid:" + sql_creditsubjectid);
			ps = conn.prepareStatement(sql_creditsubjectid);
			rs = ps.executeQuery();
			if (rs.next()) {
				sql_creditsubjectid = rs.getString(1);
			} else {
				sql_creditsubjectid = "-1";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			
			
			sql_debitsubjectid = " select distinct subjectid from c_account \n"
				+ " where accpackageid='"+accpackageid+"' \n"
				+ " and subjectfullname2 = '"+debitsubjectname+"'  \n";
		
			
			
			System.out.println("sql_debitsubjectid:" + sql_debitsubjectid);
			ps = conn.prepareStatement(sql_debitsubjectid);
			rs = ps.executeQuery();
			if (rs.next()) {
				sql_debitsubjectid = rs.getString(1);
			} else {
				sql_debitsubjectid = "-1";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			sql = " select group_concat(distinct voucherid SEPARATOR '\\\',\\\'') from \n"
					+ " c_subjectentry b   \n"
					+ " where b.accpackageid='"
					+ accpackageid
					+ "' \n"
					+ " and creditsubjects like '%,"
					+ sql_creditsubjectid
					+ "%' \n"
					+ " and debitsubjects like '%,"
					+ sql_debitsubjectid
					+ "%' \n";
			System.out.println("sql:" + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				voucherid = rs.getString(1);
			}
			if (!"".equals(voucherid) && voucherid != null) {
				voucherid = "'" + voucherid + "'";
			} else {
				voucherid = "-1";
			}

			DbUtil.close(rs);
			DbUtil.close(ps);
			if (!"".equals(total) || !"".equals(month)) {// 指定年或月，刷年汇总或具休某一月
				if ("1".equals(directtion)) {
					sql1 = " select '' as mymonth,sum(occurvalue) occurvalue \n"
							+ " from  c_subjectentry a   \n"
							+ " where accpackageid='"
							+ accpackageid
							+ "' \n"
							+ " and voucherid  \n"
							+ " in \n"
							+ " ( \n"
							+ " "
							+ voucherid
							+ " \n"
							+ " )  and subjectid like '"
							+ sql_debitsubjectid + "%'  " + month + " ";

				} else {
					sql1 = " select '' as mymonth,sum(occurvalue) occurvalue \n"
							+ " from  c_subjectentry a   \n"
							+ " where accpackageid='"
							+ accpackageid
							+ "' \n"
							+ " and voucherid  \n"
							+ " in \n"
							+ " ( \n"
							+ " "
							+ voucherid
							+ " \n"
							+ " ) and subjectid like '"
							+ sql_creditsubjectid + "%'  " + month + " ";

				}

			} else {// 刷十二个月
				if ("1".equals(directtion)) {
					sql1 = "select concat(a.submonth,'月') mymonth,b.occurvalue occurvalue from k_month a left join ( \n"
							+ " select if(substring(vchdate,6,1)=0,substring(vchdate,7,1),substring(vchdate,6,2)) myvchdate,sum(occurvalue) occurvalue \n"
							+ " from  c_subjectentry a    \n"
							+ " where accpackageid='"
							+ accpackageid
							+ "' \n"
							+ " and voucherid  \n"
							+ " in \n"
							+ " ( \n"
							+ " "
							+ voucherid
							+ " \n"
							+ " )  and subjectid like '"
							+ sql_debitsubjectid
							+ "%'  group by myvchdate \n"
							+ " ) b  \n"
							+ " on a.submonth=b.myvchdate \n"
							+ " where a.monthtype=12 \n";

				} else {
					sql1 = "select concat(a.submonth,'月') mymonth,b.occurvalue occurvalue from k_month a left join ( \n"
							+ " select if(substring(vchdate,6,1)=0,substring(vchdate,7,1),substring(vchdate,6,2)) myvchdate,sum(occurvalue) occurvalue \n"
							+ " from  c_subjectentry a    \n"
							+ " where accpackageid='"
							+ accpackageid
							+ "' \n"
							+ " and voucherid  \n"
							+ " in \n"
							+ " ( \n"
							+ " "
							+ voucherid
							+ " \n"
							+ " )  and subjectid like '"
							+ sql_creditsubjectid
							+ "%'  group by myvchdate \n"
							+ " ) b  \n"
							+ " on a.submonth=b.myvchdate \n"
							+ " where a.monthtype=12 \n";
				}

			}

			System.out.println("sql1:" + sql1);
			st = conn.createStatement();
			st.executeQuery("set   charset   gbk;");
			rs = st.executeQuery(sql1);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

}