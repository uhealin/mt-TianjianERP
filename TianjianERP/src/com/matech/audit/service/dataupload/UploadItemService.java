package com.matech.audit.service.dataupload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.service.print.PrintSetup;

public class UploadItemService {
	private String accpackageid = "";

	private Connection conn = null;

	public UploadItemService(Connection conn, String accpackageid)
			throws Exception {
		if (accpackageid == null || accpackageid.equals("")) {
			throw new Exception("��������accpackageid����");
		}
		this.accpackageid = accpackageid;
		this.conn = conn;
	}

	/**
	 * 检查固定资产表是否存在
	 * 
	 * @return
	 * @throws Exception
	 */
	public int ItemExist() throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select count(*) from fa_account where AccPackageID=?");
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查固定资产是否存在失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 检查固定资产临时表是否存在
	 * 
	 * @return
	 * @throws Exception
	 */
	public int checkItemExist() throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select count(*) from t_fItem where AccPackageID=?");
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查固定资产临时表失败:" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 原来的getColorRow，换了名字：
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList getItemFields() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {
			ps = conn
					.prepareStatement("select * from f_Item where AccPackageID=?");
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			while (rs.next()) {
				ArrayList alrs = new ArrayList();
				alrs.add(rs.getString("ItemCode"));
				alrs.add(rs.getString("ItemName"));
				al.add(alrs);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 代码:包含 名称： 包含 类别： 包含 类别或类型 预计使用期间(工作总量)： 预计使用期间(工作总量) 原值：包含原值； 累计折旧：
	 * 严格等于累计折旧 年折旧率： 严格等于 残值： 包含
	 * 
	 * @return
	 * @throws Exception
	 */
	public void autoControl() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {
			if (ItemExist() != 0) {
				// 判断固定资产表是否存在
				sql = "select * from " + "("
						+ "select ? as 'accid','代码' as 'itemname1'" + ") a "
						+ "left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname like concat('%',a.itemname1) "
						+ "or b.itemname like concat('%','编号')" + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','名称' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid "
						+ " where  b.itemname like concat('%',a.itemname1) "
						+ " union " + "select * from " + "("
						+ "select ? as 'accid','类别' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname like concat('%',a.itemname1) "
						+ "or b.itemname like '%类型'" + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','预计使用期间(工作总量)' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname = '预计使用期间(工作总量)' " + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','累计折旧' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname = a.itemname1 " + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','年折旧率' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname = a.itemname1 " + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','预计净残值' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where b.itemname like '%残值' " + " union "
						+ "select * from " + "("
						+ "select ? as 'accid','原值' as 'itemname1'"
						+ ") a left join  f_item b "
						+ "on a.accid=b.accpackageid  "
						+ "where  b.itemname like concat('%',a.itemname1) ";

				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				ps.setString(2, accpackageid);
				ps.setString(3, accpackageid);
				ps.setString(4, accpackageid);
				ps.setString(5, accpackageid);
				ps.setString(6, accpackageid);
				ps.setString(7, accpackageid);
				ps.setString(8, accpackageid);
				rs = ps.executeQuery();

				while (rs.next()) {
					PreparedStatement ps1 = null;
					String Itemname1 = rs.getString("Itemname1");
					String ItemName = rs.getString("ItemName");
					String sql1 = "update f_item set StandupName=? where itemname=? and accpackageid=?";
					ps1 = conn.prepareStatement(sql1);
					ps1.setString(1, Itemname1);
					ps1.setString(2, ItemName);
					ps1.setString(3, accpackageid);
					ps1.executeUpdate();
					ps1.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public static void main(String[] args) throws Exception {
		Connection conn = new DBConnect().getConnect("100002");
		UploadItemService uploadItemService = new UploadItemService(conn,
				"1000022000");
		uploadItemService.autoControl();
	}

	/**
	 * 判断指定的帐套是否有固定资产
	 * 
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public int getName(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int hasCard = 0;

		try {

			boolean bool = new DisposeTableService(conn).checkTableExist("fa_"
					+ acc);
			String str = acc.substring(6);
			if (bool)
				str = acc;

			String sql = "select 1 from asdb_" + acc.substring(0, 6) + ".fa_" + str;
		
			try {
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					hasCard = 1;
				}
				hasCard = 1;
			} catch (Exception e) {
				hasCard = 0;
			}
			
			return hasCard;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 折旧测试－替代程序
	 * 
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public String replace(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		UploadItemService uis = new UploadItemService(conn, acc);
		String excelName = "";
		String[] strSqls = new String[] { "", "" };
		String[] columnName = new String[8];
		String[] columnName1 = new String[8];
		String columnName2 = " ";
		String[] columnName3 = new String[] { "代码", "名称", "类别", "预计使用期间(工作总量)",
				"累计折旧", "年折旧率", "预计净残值", "原值" };

		try {

			if (uis.getName(acc) == 1) {// 有固定资产，并把固定资产清单-利用采集卡片的数据刷出来
				excelName = "固定资产清单-利用采集卡片.xls";
				int i = 0;
				int j = 0;
				String sql = "select itemcode,standupname from f_item where accpackageid="
						+ acc
						+ " and standupname is not null and standupname <>''";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					columnName[i] = rs.getString(1);
					columnName1[j] = rs.getString(2);
					i++;
					j++;
				}

				for (int k = 0; k < 8; k++) {
					for (int p = 0; p < 8; p++) {
						if (columnName3[k].equals(columnName1[p])) {
							columnName2 += columnName[p] + ",";
						}
					}
				}
				columnName2 = columnName2
						.substring(0, columnName2.length() - 1);

				boolean bool = new DisposeTableService(conn)
						.checkTableExist("fa_" + acc);
				String str = acc.substring(6);
				if (bool)
					str = acc;

				strSqls[0] = "select " + columnName2 + " from fa_" + str
						+ " where accpackageid = " + acc + "";
				// System.out.println("strsqls="+strSqls[0]);
				strSqls[1] = "select 1";

			} else {// 没有固定资产，打开固定资产折旧提取计算表-没有采集卡片给用户输入
				excelName = "固定资产折旧提取计算表-没有采集卡片.xls";

				strSqls[0] = "select 1";
				strSqls[1] = "select 1";

			}
			HashMap varMap = new HashMap();

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrQuerySqls(strSqls);
			printSetup.setVarMap(varMap);
			printSetup.setCharColumn(new String[] { "1", "1" });
			printSetup.setSeriesColumnUpd(new int[] { 1, 1 });
			printSetup.setStrExcelTemplateFileName(excelName);

			String filename = printSetup.getExcelFile();

			return filename;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 检查固定资产的科目是否存在
	 * 
	 * @return
	 * @throws Exception
	 */
	public int SubjectExist(String subjectName) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "select count(*) from c_account where (subjectfullname2 like '"+subjectName+"/%' or subjectfullname2='"+subjectName+"') and AccPackageID=?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查固定资产的科目是否存在失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 检查固定资产核算是否存在
	 * 
	 * @return
	 * @throws Exception
	 */
	public int AssitemExist(String assitemName,String subjectName) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String subjectid = "";
		String sql = "";
		try {
			sql = " select ifnull(group_concat(distinct subjectid SEPARATOR \"','\"),-1) from c_account \n"
				 +"	where (subjectfullname2 like '"+subjectName+"/%' or subjectfullname2='"+subjectName+"') \n"
				 +"	and accpackageid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			if(rs.next()) {
				if("-1".equals(rs.getString(1))) {
					return 0;
				} else {
					subjectid = "'"+rs.getString(1)+"'";
				}
			}
			if(rs!=null) {
				rs.close();
			}
			if(ps!=null) {
				ps.close();
			}
			sql = "select count(*) from c_assitementryacc where asstotalname1 like '%"+assitemName+"%' and accid in (" +subjectid+ ") and asstotalname1 not like '%部门%' and AccPackageID=?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.accpackageid);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查固定资产核算是否存在失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}