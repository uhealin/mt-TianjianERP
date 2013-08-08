package com.matech.audit.service.questionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.questionType.model.QuestionTypeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class QuestionTypeMan {
	private Connection conn = null;

	public QuestionTypeMan(Connection conn) {
		this.conn = conn;
	}

	public String delAQuestionType(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			if (id != null) {

				sql = "select COUNT(*) from p_Questiontype WHERE ParentID="
						+ id;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					if (Integer.parseInt(rs.getString(1)) > 0) {
						return "分类存在下级分类,不能删除分类！";
					} else {
						sql = "select COUNT(*) from p_Question where Questiontype ='"
								+ id + "'";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if (rs.next()) {
							if (Integer.parseInt(rs.getString(1)) > 0) {
								return "分类中存在问题,不能删除分类！";
							} else {
								sql = "delete from p_Questiontype where id='"
										+ id + "'";
								ps = conn.prepareStatement(sql);
								ps.execute();
								return "分类删除成功！";
							}
						}
					}
				}
			}
			return "";
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

	public void AddOrModifyAQuestionType(QuestionTypeTable pt, String act)
			throws Exception {
		ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			int i = 1;

			if (asf.showNull(act).equals("ad")) {
				//if("question".equals(tableType)){
					ps = conn.prepareStatement("INSERT INTO p_Questiontype(TypeName,ParentID,Question_DB,IsLeaf) VALUES(?,?,?,?)");
					//}else if("case".equals(tableType)){
					//ps = conn.prepareStatement("INSERT INTO p_casestype(TypeName,ParentID,Cases_DB,IsLeaf) VALUES(?,?,?,?)");
					//}else{
					
					//	}
				ps.setString(i++, pt.getTypeName());
				ps.setInt(i++, pt.getParentID());
				ps.setInt(i++, 1);
				ps.setInt(i++, pt.getIsLeaf());
				ps.execute();
				// return true;
			} else if (asf.showNull(act).equals("ed")) {
				i = 1;
				//if("question".equals(tableType)){
					ps = conn .prepareStatement("update p_Questiontype set TypeName=?,ParentID=?,Question_DB=?,IsLeaf=? where id=?");
					//	}else if("case".equals(tableType)){
					//		ps = conn .prepareStatement("update p_casestype set TypeName=?,ParentID=?,Cases_DB=?,IsLeaf=? where id=?");
					//	}else{
					
					//}
				ps.setString(i++, pt.getTypeName());
				ps.setInt(i++, pt.getParentID());
				ps.setInt(i++, 1);
				ps.setInt(i++, pt.getIsLeaf());
				ps.setInt(i++, pt.getId());
				ps.execute();
				// return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return false;
		} finally {
			if (ps != null)
				ps.close();

		}

	}

	public QuestionTypeTable getAQuestionTypeDetail(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		QuestionTypeTable pt = new QuestionTypeTable();
		try {
			String str = "";
			String sql = "select * from p_Questiontype where id='"+ id + "'"
						 +" union "
						 +"select * from p_casestype where id='"+ id + "'";
			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			if (rs.next()) {
				pt.setId(rs.getInt(1));
				pt.setTypeName(rs.getString(2));
				pt.setParentID(rs.getInt(3));
				pt.setQuestion_DB(rs.getInt(4));
				pt.setIsLeaf(rs.getInt(5));
			}
			return pt;
		} catch (Exception e) {
			e.printStackTrace();
			return pt;
		} finally {
			if (ps != null)
				ps.close();

		}

	}
}
