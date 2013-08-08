package com.matech.audit.service.rectify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SumListService {
	
	private Connection conn = null;
	private String SQL;
	private int sqlNum;
	public SumListService(Connection conn) {
		
		this.conn = conn;

	}

	public String getSQL() {
		return SQL;
	}

	public int getSqlNum() {
		return sqlNum;
	}
	public String getSortEntry(String acc, String proid, String property,
			String vchdate) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		ASFuntion asf = new ASFuntion();
		StringBuffer sbf = new StringBuffer("");
		try {
			
			if (!"".equals(acc) && !"".equals(proid)) {
				if (!"".equals(property) && !"00".equals(property)) {
					property = " and b.property ='" + property + "' ";
				} else {
					property = "";
				}
				if (!"".equals(vchdate) && !"00".equals(vchdate)) {
					if ("0".equals(vchdate)) {
						vchdate = " and b.vchdate LIKE '%-01-01' ";
					} else {
						vchdate = " and b.vchdate LIKE '%-12-31' ";
					}
				} else {
					vchdate = "";
				}
				sql = "select b.summary,a.subjectid s1,b.subjectid s2,"
						+ " if(SUBSTRING(b.subjectid,1,1) <=3,if(dirction=1,occurvalue,0),0) occ1,"
						+ " if(SUBSTRING(b.subjectid,1,1) <=3,if(dirction=-1,occurvalue,0),0) occ2,"
						+ " if(SUBSTRING(b.subjectid,1,1) =5,if(dirction=1,occurvalue,0),0) occ3,"
						+ " if(SUBSTRING(b.subjectid,1,1) =5,if(dirction=-1,occurvalue,0),0) occ4 "
						+ " from (select accpackageid,subjectid,subjectname,subjectfullname,level0 from c_accpkgsubject where accpackageid='"
						+ acc
						+ "' UNION select accpackageid,subjectid,subjectname,subjectfullname,level0 from z_usesubject a where accpackageid='"
						+ acc
						+ "' and projectid='"
						+ proid
						+ "') a,z_subjectentryrectify b "
						+ " where a.accpackageid='"
						+ acc
						+ "' and a.level0=1 and b.accpackageid='"
						+ acc
						+ "' and b.projectid='"
						+ proid
						+ "' and substring(b.Property,1,1)<6 "
						+ property
						+ vchdate
						+ " and b.subjectid like CONCAT(a.subjectid,'%') order by b.subjectid ";
				this.SQL = sql;
				org.util.Debug.prtOut(sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				int opt = 0;
				String bgColor = "";
				while (rs.next()) {
					opt++;
					if (opt % 2 == 0) {
						bgColor = "#F3F5F8";
					} else {
						bgColor = "#ffffff";
					}
					sbf.append("<tr height=18 onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='"
									+ bgColor
									+ "';\"  bgColor=\""
									+ bgColor
									+ "\">");
					sbf.append("<td align=\"center\">" + opt + "</td>");
					sbf.append("<td align=\"left\">" + rs.getString("summary")
							+ "</td>");
					sbf.append("<td align=\"center\">" + rs.getString("s1")
							+ "</td>");
					sbf.append("<td align=\"center\">" + rs.getString("s2")
							+ "</td>");
					sbf.append(asf.showMoney(rs.getString("occ1")));
					sbf.append(asf.showMoney(rs.getString("occ2")));
					sbf.append(asf.showMoney(rs.getString("occ3")));
					sbf.append(asf.showMoney(rs.getString("occ4")));
					sbf.append("</tr>");
				}
				this.sqlNum = opt;
				sbf.append("<tr height=10  bgColor=\"#FFFFFF\"><td colspan=8></td></tr>");
				sql = " select SUM(occ1) s1,SUM(occ2) s2,SUM(occ3) s3,SUM(occ4) s4 FROM( "
						+ sql + ") a";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					bgColor = bgColor.equals("#F3F5F8") ? "#ffffff" : "#F3F5F8";
					sbf.append("<tr height=18 bgColor=\"" + bgColor + "\">");
					sbf.append("<td></td>");
					sbf.append("<td align=\"left\"><font color='blue'><b>��    ��</b></font></td>");
					sbf.append("<td></td>");
					sbf.append("<td></td>");
					sbf.append(asf.showMoney(rs.getString("s1")));
					sbf.append(asf.showMoney(rs.getString("s2")));
					sbf.append(asf.showMoney(rs.getString("s3")));
					sbf.append(asf.showMoney(rs.getString("s4")));
					sbf.append("</tr>");
				}
			}
			return sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

}
