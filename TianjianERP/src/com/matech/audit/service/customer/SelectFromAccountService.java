package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Set;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SelectFromAccountService {

	private Connection conn = null;

	public SelectFromAccountService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	public DataGridProperty getSubjectName(String AccPackageID, String search) {

		String customerid = "";
		AccPackageID = new ASFuntion().showNull(AccPackageID);
		if(AccPackageID.length()>=6){
			customerid = AccPackageID.substring(0, 6);
		}

		String sql = "";

		if ("".equals(search) || null == search) {
			sql = "select *,if(company='','','checked') as checked,if(company='','',SubjectName) as value from ( \n"
					+ "select a.SubjectName as SubjectName, a.SubjectID as SubjectID, a.AssItemID as AssItemID, \n"
					+ "case ifnull(b.connectcompanysname,'1') when '1' then '' else '[是关联客户]' end as company from \n"
					+ "(select SubjectName, SubjectID, '' as AssItemID from \n"
					+ "c_accpkgsubject where AccPackageID = '"
					+ AccPackageID
					+ "' and AssistCode = '1' and IsLeaf = '1' \n"
					+ "and not exists (select 1 from c_assitementryacc where 1=1 \n"
					+ "and accpackageid='"
					+ AccPackageID
					+ "' and SubMonth = 1 and isleaf1=1 and c_accpkgsubject.subjectid=accid ) \n"
					+ "union \n"
					+ "select AssItemName, AccID, AssItemID from c_assitem where AccPackageID = '"
					+ AccPackageID
					+ "' and IsLeaf = '1' \n"
					+ " and AccID in (select  SubjectID from c_accpkgsubject where AccPackageID = '"+AccPackageID+"' and AssistCode = '1' and IsLeaf = '1' ) "
//					+ "and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%') \n"
					+ ") a left join asdb.k_connectcompanys b \n"
					+ "on b.customerid = '"
					+ customerid
					+ "' and (a.SubjectName = b.connectcompanysname or a.AssItemID = b.connectcompanysname)\n"
					+ "order by SubjectID, AssItemID" + ")a";
		} else {
			sql = "select *,if(company='','','checked') as checked,if(company='','',SubjectName) as value from ( \n"
					+ "select a.SubjectName as SubjectName, a.SubjectID as SubjectID, a.AssItemID as AssItemID, \n"
					+ "case ifnull(b.connectcompanysname,'1') when '1' then '' else '[是关联客户]' end as company from \n"
					+ "(select SubjectName, SubjectID, '' as AssItemID from \n"
					+ "c_accpkgsubject where AccPackageID = '"
					+ AccPackageID
					+ "' and AssistCode = '1' and IsLeaf = '1' \n"
					+ "and SubjectName like '%"
					+ search
					+ "%' \n"
					+ "and not exists (select 1 from c_assitementryacc where 1=1 \n"
					+ "and accpackageid='"
					+ AccPackageID
					+ "' and SubMonth = 1 and isleaf1=1 and c_accpkgsubject.subjectid=accid ) \n"
					+ "union \n"
					+ "select AssItemName, AccID, AssItemID from c_assitem where AccPackageID = '"
					+ AccPackageID
					+ "' and IsLeaf = '1' \n"
					+ " and AccID in (select  SubjectID from c_accpkgsubject where AccPackageID = '"+AccPackageID+"' and AssistCode = '1' and IsLeaf = '1' ) "
					+ "and AssItemName like '%"
					+ search
					+ "%' \n"
//					+ "and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%') \n"
					+ ") a left join asdb.k_connectcompanys b \n"
					+ "on b.customerid = '"
					+ customerid
					+ "' and (a.SubjectName = b.connectcompanysname or a.AssItemID = b.connectcompanysname)\n"
					+ "order by SubjectID, AssItemID" + ")a";
		}

		DataGridProperty dgProperty = new DataGridProperty() {

			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {

			}
		};

		try {
			dgProperty.setCustomerId(customerid);

			dgProperty.setTableID("SelectFromAccount");

			dgProperty.setInputAttribute("${checked} value='${value}'");

			dgProperty.setInputType("checkbox");

			dgProperty.setCancelOrderby(true);

			dgProperty.setUseBufferGrid(false);
			
			dgProperty.setPrintEnable(true);
			dgProperty.setWhichFieldIsValue(1) ;

			dgProperty.setPrintVerTical(false);
//			dgProperty.setPage_CH("100") ;
			dgProperty.setPageSize_CH(100);
			dgProperty.addColumn("往来类科目编号", "SubjectID");
			dgProperty.addColumn("辅助核算编号", "AssItemID");
			dgProperty.addColumn("往来类科目名称/辅助核算名称", "SubjectName");
			dgProperty.addColumn("是否关联客户", "company");

			dgProperty.setCancelAjaxSynchronization(false);
			dgProperty.setInputAction(" onclick='selectOrNot(this);' ");

			dgProperty.setTrActionProperty(true);
			dgProperty.setTrAction("style=\"cursor:hand;\" ondblclick='goSort();'");
			dgProperty.setSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dgProperty;
	}

	public void save(String AccPackageID, Set set) {

		String sql = "insert into k_connectcompanys (customerid, connectcompanysname) values(?,?)";

		String CustomerID = AccPackageID.substring(0, 6);

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			Iterator it = set.iterator();
			while (it.hasNext()) {
				String SubjectName = (String) it.next();
				ps.setString(1, CustomerID);
				ps.setString(2, SubjectName);

				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改为使用Ajax保存
	 * @param CustomerID
	 * @param set
	 */
	public void saveRelateCustomer(String CustomerID,String customerName) {

		String sql = "insert into k_connectcompanys (customerid, connectcompanysname) values(?,?)";

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			ps.setString(2, customerName);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void deleteRelateCustomer(String customerID, String customerName) {

		String sql = "delete from k_connectcompanys where customerid=? and connectcompanysname=?";

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerID);
			ps.setString(2, customerName);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void clear(String AccPackageID, String search_condition, Set set) {

		String sql = "select b.connectcompanysname as company from \n"
				+ "(select SubjectName, SubjectID, '' as AssItemID from \n"
				+ "c_accpkgsubject where AccPackageID = ? and AssistCode = '1' and IsLeaf = '1' \n"
				+ "and not exists (select 1 from c_assitementryacc where 1=1 "
				+ "and accpackageid=? and SubMonth = 1 and isleaf1=1 and c_accpkgsubject.subjectid=accid ) \n"
				+ "union \n"
				+ "select AssItemName, AccID, AssItemID from c_assitem where AccPackageID = ? and IsLeaf = '1' \n"
//				+ "and (asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%') \n"
				+ ") a left join asdb.k_connectcompanys b \n"
				+ "on b.customerid = ? and a.SubjectName = b.connectcompanysname \n"
				+ "where b.connectcompanysname is not null";

		String CustomerID = AccPackageID.substring(0, 6);

		System.out.println(AccPackageID);

		System.out.println(CustomerID);

		String condition = "(";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.setString(4, CustomerID);

			rs = ps.executeQuery();

			while (rs.next()) {
				condition += "'" + rs.getString("company") + "',";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		try {
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String SubjectName = (String) it.next();
				if (condition.length() == 1) {
					if (search_condition.equals("")) {
						sql = "delete from asdb.k_connectcompanys where customerid = ? and connectcompanysname=? ";
					} else {
						sql = "delete from asdb.k_connectcompanys where customerid = ? \n"
								+ "and connectcompanysname like '%"
								+ search_condition
								+ "%'  and connectcompanysname=? ";
					}
				} else {
					condition = condition.substring(0, condition.length() - 1)
							+ ")";

					if (search_condition.equals("")) {
						sql = "delete from asdb.k_connectcompanys where customerid = ? \n"
								+ "and connectcompanysname in "
								+ condition
								+ "  and connectcompanysname=? ";
					} else {
						sql = "delete from asdb.k_connectcompanys where customerid = ? \n"
								+ "and connectcompanysname like '%"
								+ search_condition
								+ "%' \n"
								+ "and connectcompanysname in "
								+ condition
								+ "  and connectcompanysname=? ";
					}
				}

				System.out.println("1.HZH: condition=" + condition);
				System.out.println("2.HZH: search_condition="
						+ search_condition);

				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.setString(2, SubjectName);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void allClear(String customerid) {

		String sql = "delete from k_connectcompanys where customerid=?";
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);

			ps.setString(1, customerid);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
}
