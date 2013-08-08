package com.matech.audit.service.industry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class IndustryService {

	private Connection conn = null;

	public IndustryService(Connection conn) {
		this.conn = conn;
	}

	public boolean delAMenu(int id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (id != 0) {

				ps = conn
						.prepareStatement("select VocationId from k_customer where VocationId="
								+ id + "");
				rs = ps.executeQuery();
				if (rs.next()) {
					return false;
				} else {
					ps = conn
							.prepareStatement("delete from k_Industry where IndustryID="
									+ id + "");
					ps.execute();
					ps.execute("Flush tables");
					return true;
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	/*
	 * public boolean checkDefineIndustry(){ Connection conn = null;
	 * PreparedStatement ps = null; try{ ps = conn.prepareStatement("select *
	 * from k_Industry where IsDefault=1"); ResultSet rs = null; rs =
	 * ps.executeQuery(); if(rs.next()){ return true; } }catch(Exception e){
	 * e.printStackTrace(); }finally{ try { conn.close(); ps.close(); } catch
	 * (SQLException e) { e.printStackTrace(); } } return false; }
	 */
	public void AddOrModifyAMenu(String XML, String act) throws Exception {
		DbUtil.checkConn(conn);
		ASFuntion asf = new ASFuntion();

		PreparedStatement ps = null;
		// org.util.Debug.prtOut("XML =" + XML);
		int i = 1;
		String str = "";
		try {

			int is = Integer.parseInt(asf.getXMLData(XML, "isdefault"));
			if (is == 1) {
				str = "update  k_Industry set isdefault = 0 where isdefault = 1";
				ps = conn.prepareStatement(str);
				ps.execute();
				ps.execute("Flush tables");
			}
			if (act.equals("ad")) {
				// org.util.Debug.prtOut("XML =" + XML);
				str = "INSERT INTO k_Industry(IndustryID,IndustryName,IsDefault) VALUES(?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML,
						"industryid")));
				ps.setString(i++, asf.getXMLData(XML, "industryname"));
				ps.setInt(i++, is);
				ps.execute();
				ps.execute("Flush tables");

			} else {
				i = 1;
				ps = conn
						.prepareStatement("update k_Industry set IndustryName=?,IsDefault=? where IndustryID=?");

				ps.setString(i++, asf.getXMLData(XML, "industryname"));
				ps.setInt(i++, is);
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML,
						"industryid")));
				ps.execute();
				ps.execute("Flush tables");

			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			// DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public boolean CheckIndustry(String industryName, String industryId)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try { 
			String sql = "select * from k_Industry where IndustryName='"+ industryName + "' and industryId !='" + industryId+"'";
			ps = conn.prepareStatement(sql);

			ResultSet rs = null;
			rs = ps.executeQuery();
			if (rs.next())
				return false;
			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			// DbUtil.close(rs);
			DbUtil.close(ps);
		}

		// return false;
	}

	/**
	 * 获取默认行业ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getDefIndustry() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String temp = null;
		try {

			ps = conn
					.prepareStatement("select IndustryID,Industryname from k_Industry where IsDefault=1");
			rs = ps.executeQuery();
			if (rs.next()) {
				temp = rs.getString("IndustryID") + "`"
						+ rs.getString("Industryname");
			} else {
				ps = conn
						.prepareStatement("select IndustryID,Industryname from k_Industry where 1=1 limit 1");
				rs = ps.executeQuery();
				if (rs.next()) {
					temp = rs.getString("IndustryID") + "`"
							+ rs.getString("Industryname");
				}
			}
			return temp;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		// return temp;
	}

	public String getAMenuDetail(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String str = "";

			ps = conn
					.prepareStatement("select * from k_Industry where IndustryID='"
							+ id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {

				str = "<industryid>" + rs.getString("industryid")
						+ "</industryid><industryname>"
						+ rs.getString("industryname")
						+ "</industryname><isdefault>"
						+ rs.getString("isdefault") + "</isdefault>";
			}
			// org.util.Debug.prtOut(str);
			return str;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根

	}

}
