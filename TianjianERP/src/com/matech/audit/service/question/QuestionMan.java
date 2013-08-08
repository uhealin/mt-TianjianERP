package com.matech.audit.service.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.matech.audit.service.question.model.QuestionTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class QuestionMan {
	private Connection conn = null;

	public QuestionMan(Connection conn) {
		this.conn = conn;
	}

	public ArrayList getTaskConn() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		ArrayList al = new ArrayList();
		try {

			sql = "select DISTINCT taskname from k_tasktemplate where isleaf=0";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				al.add(rs.getString(1));
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}

	}

	public String getParentConn(String pid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		String sql = "";
		ArrayList al = new ArrayList();
		try {

			al.add(pid);
			for (int i = 0; i < al.size(); i++) {
				sql = "select id from p_Questiontype where parentid='"
						+ (String) al.get(i) + "'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					al.add(rs.getString(1));
				}
			}
			result = "('" + (String) al.get(0);
			for (int i = 1; i < al.size(); i++) {
				result += "','" + (String) al.get(i);
			}
			result += "')";
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}

	public void updateViewCount(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			if (id != null) {

				ps = conn
						.prepareStatement("update p_Question set ViewCount=ViewCount+1 where id='"
								+ id + "'");
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}

	public void delAQuestion(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			if (id != null) {

				ps = conn.prepareStatement("delete from p_Question where id='"
						+ id + "'");
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}

	public String findQuestionTpyeNameById(int id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String QuestionType = "";
		try {

			ps = conn
					.prepareStatement("select typename from p_Questiontype where id='"
							+ id + "'");
			rs = null;
			rs = ps.executeQuery();
			if (rs.next()) {
				QuestionType = rs.getString(1);
			}
			return QuestionType;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

	}

	public void AddOrModifyAQuestion(QuestionTable pt, String act,String userId)
			throws Exception {
		ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			int i = 1;

			if (asf.showNull(act).equals("ad")) {
				ps = conn
						.prepareStatement("INSERT INTO p_Question(QuestionType,Title,Context,Author,KeyValue,GreateDate,createUserId) VALUES(?,?,?,?,?,?,?)");
				ps.setInt(i++, pt.getQuestionType());
				ps.setString(i++, pt.getTitle());
				ps.setString(i++, pt.getContext());
				ps.setString(i++, pt.getAuthor());
				ps.setString(i++, pt.getKeyValue());
				ps.setString(i++, pt.getGreateDate());
				ps.setString(i++, userId);
				ps.executeUpdate();

			} else if (asf.showNull(act).equals("ed")) {
				i = 1;
				ps = conn
						.prepareStatement("update p_Question set QuestionType=?,Title=?,Context=?,Author=?,KeyValue=?,GreateDate=? where id=?");
				ps.setInt(i++, pt.getQuestionType());
				ps.setString(i++, pt.getTitle());
				ps.setString(i++, pt.getContext());
				ps.setString(i++, pt.getAuthor());
				ps.setString(i++, pt.getKeyValue());
				ps.setString(i++, pt.getGreateDate());

				ps.setInt(i++, pt.getId());
				ps.execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}

	}

	public QuestionTable getAQuestionDetail(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		QuestionTable pt = new QuestionTable();
		try {

			ps = conn.prepareStatement("select * from p_Question where id='"
					+ id + "'");

			rs = ps.executeQuery();
			if (rs.next()) {
				pt.setId(rs.getInt("ID"));
				pt.setQuestionType(rs.getInt("QuestionType"));
				pt.setTitle(rs.getString("Title"));
				pt.setContext(rs.getString("Context"));
				pt.setAuthor(rs.getString("Author"));
				pt.setKeyValue(rs.getString("KeyValue"));
				pt.setGreateDate(rs.getString("GreateDate"));
			}
			return pt;
		} catch (Exception e) {
			e.printStackTrace();
			return pt;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}

}
