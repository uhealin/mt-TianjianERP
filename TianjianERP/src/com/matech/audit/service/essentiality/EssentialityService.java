package com.matech.audit.service.essentiality;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.matech.framework.pub.db.DbUtil;

/**
 * 重要性水平维护的SERVICE
 * @author LuckyStar
 *
 */
public class EssentialityService {

	private Connection conn = null;

	public EssentialityService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 科目级别的重要性水平保存
	 * 一次保存完全部内容
	 * @param sbjID
	 * @param projectID
	 * @param accpackageID
	 * @param e1
	 * @param e2
	 * @param e3
	 * @throws Exception
	 */
	public void updateEssentialityLevel(String sbjID, String projectID,
			String accpackageID, String e1, String e2, String e3) 
			throws Exception{
		
		DbUtil.checkConn(conn);
		
		String[] sbjIDs = sbjID.split(",");
		String[] e1s = e1.split(",", sbjIDs.length + 1);
		String[] e2s = e2.split(",", sbjIDs.length + 1);
		String[] e3s = e3.split(",", sbjIDs.length + 1);

		try {
			for (int i = 0; i < sbjIDs.length; i++) {
				this.updateEssentialityLevel2_1( sbjIDs[i], projectID,
						accpackageID, e1s[i], e2s[i], e3s[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存重要性水平设置失败！",e);
		}

	}

	//	是 updateEssentialityLevel2 调用的方法
	private void updateEssentialityLevel2_1(String sbjID,
			String projectID, String accpackageID, String e1, String e2,
			String e3) throws Exception{
		
		DbUtil.checkConn(conn);
		
		if (e3 == null || e3.equals("")) {
			e3 = "null";
		}
		
		PreparedStatement ps = null;
		try {
			String sql = " update z_essentiality set essentiality1=?,essentiality2=?,essentiality3=? where AccpackageID=? and subjectid=? and projectid=? and property=2 ";
			String insertSql = "insert into z_essentiality(essentiality1,essentiality2,essentiality3,AccpackageID,subjectid,projectid,property) values(?,?,?,?,?,?,2)";
			ps = conn.prepareStatement(sql);
			if (e1 == null || e1.equals("")) {
				ps.setNull(1, java.sql.Types.NULL);
			} else {
				ps.setString(1, e1);
			}

			if (e2 == null || e2.equals("")) {
				ps.setNull(2, java.sql.Types.NULL);
			} else {
				ps.setString(2, e2);
			}

			if (e3.equals("null")) {
				ps.setNull(3, java.sql.Types.NULL);
			} else {
				ps.setString(3, e3);
			}
			ps.setString(4, accpackageID);
			ps.setString(5, sbjID);
			ps.setString(6, projectID);

			if (ps.executeUpdate() <= 0) {
				ps = conn.prepareStatement(insertSql);
				if (e1 == null || e1.equals("")) {
					ps.setNull(1, java.sql.Types.NULL);
				} else {
					ps.setString(1, e1);
				}

				if (e2 == null || e2.equals("")) {
					ps.setNull(2, java.sql.Types.NULL);
				} else {
					ps.setString(2, e2);
				}

				if (e3.equals("null")) {
					ps.setNull(3, java.sql.Types.NULL);
				} else {
					ps.setString(3, e3);
				}
				ps.setString(4, accpackageID);
				ps.setString(5, sbjID);
				ps.setString(6, projectID);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存单个重要性失败",e);
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 报表级别的重要性水平保存
	 * @param sbjID
	 * @param projectID
	 * @param accpackageID
	 * @param essentiality
	 * @param t
	 * @throws Exception
	 */
	public void updatek_essentiality(String sbjID, String projectID,
			String accpackageID, String essentiality, String t) 
				throws Exception{
		
		DbUtil.checkConn(conn);
		
		if (essentiality == null || essentiality.equals("")) {
			essentiality = "null";
		}
		
		PreparedStatement ps = null;
		try {

			String sqlInsert = " insert into z_essentiality (AccpackageID,subjectid,essentiality?,projectid,property) values(?,?,?,?,1) ";
			String sql = " update z_essentiality set essentiality?=? where AccpackageID=? and subjectid=? and projectid=? and property=1";

			String sqlUpdate2 = " update z_essentiality set essentiality3=0 where AccpackageID=? and projectid=? and property=1";

			if (t.equals("3")) {
				ps = conn.prepareStatement(sqlUpdate2);
				ps.setString(1, accpackageID);
				ps.setString(2, projectID);
				ps.execute();
			}

			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(t));
			if (essentiality.equals("null")) {
				ps.setNull(2, java.sql.Types.NULL);
			} else {
				ps.setString(2, essentiality);
			}

			ps.setString(3, accpackageID);
			ps.setString(4, sbjID);
			ps.setString(5, projectID);

			if (ps.executeUpdate() <= 0) {
				ps = conn.prepareStatement(sqlInsert);
				ps.setInt(1, Integer.parseInt(t));
				ps.setString(2, accpackageID);
				ps.setString(3, sbjID);
				ps.setString(4, essentiality);
				ps.setString(5, projectID);
				ps.execute();
			}

			//更新对应的子科目
			//	        ps=conn.prepareStatement(sqlUpdate);
			//	        ps.setInt(1,Integer.parseInt(t));
			//	        ps.setString(2,essentiality);
			//	        ps.setString(3,accpackageID);
			//	        ps.setString(4,sbjID);
			//	        ps.execute();

		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存单个重要性失败",e);
		} finally {
			DbUtil.close(ps);
		}
	}
}
