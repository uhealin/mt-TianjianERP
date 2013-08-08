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

public abstract class AbstractCtPathFunction implements CtPathFunction {
	public abstract String process(HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			Connection conn, Map args) throws Exception;
	
	
	/**
	 * 根据底稿任务编号和年项目编号获取对应的标准科目名称
	 * @param conn Connection
	 * @param projectid String
	 * @param taskcode String
	 * @return String
	 * @throws Exception
	 */
	public String getTaskSubjectNameByTaskCode(Connection conn,
			String projectid, String taskcode) throws Exception {
		String subjectname = "";
		Statement st = null;
		ResultSet rs = null;
		try {

			st = conn.createStatement();
			String sql = "select subjectname from z_task where projectid="
					+ projectid + " and taskcode='" + taskcode
					+ "' and isleaf=1";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return subjectname;
	}
	
	/**
	 * 根据底稿编号获取对应任务的对应标准科目名
	 * @param conn Connection
	 * @param manuid String
	 * @return String
	 * @throws Exception
	 */
	public String getTaskSubjectNameByManuID(Connection conn, String manuid)
			throws Exception {
		String subjectname = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "";
			st = conn.createStatement();
			sql = "select subjectname from z_task where manuid=" + manuid;

			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return subjectname;
	}
	
	
	public String changeSubjectName(Connection conn, String projectID,
			String subjectName) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "";
			sql = "select * from z_project a,k_customer b where projectid='"
					+ projectID + "' and b.DepartID=a.customerid";

			rs = st.executeQuery(sql);

			String dpID = "";
			String VocationID = "";
			String acc = "";
			if (rs.next()) {
				dpID = rs.getString("customerid");
				VocationID = rs.getString("VocationID");
				acc = rs.getString("AccPackageID");
			}

			sql = "select * from c_account where AccPackageID='"+acc+"' and subjectfullname2 = '"+subjectName+"' and submonth=1";
			rs = st.executeQuery(sql);
			if(rs.next()){
				return rs.getString("subjectfullname2");
			}
			
			
			sql = "select a.* from k_standsubject a ,("
					+ " 	select a.subjectname,replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2) exSubjectName"
					+ " 	from (    "
					+ " 		select '"
					+ subjectName
					+ "' as subjectName "
					+ " 	) a,k_key b"
					+ " 	where  b.departid in ('0','"
					+ dpID
					+ "') "
					+ "	and a.subjectname like concat('%',b.key1,'%') "
					+

					" 	union"
					+

					"	select distinct a.subjectname,TRIM(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2))  exSubjectName"
					+ " 	from (    "
					+ " 		select '"
					+ subjectName
					+ "' as subjectName "
					+ "	) a,k_key b,k_key c"
					+ "	where  b.departid in ('0','"
					+ dpID
					+ "') "
					+ " 	and  c.departid in ('0','"
					+ dpID
					+ "') "
					+ "	and a.subjectname like concat('%',b.key1,'%')  "
					+ "	and a.subjectname like concat('%',c.key1,'%') "
					+

					"	union "
					+

					"	select distinct a.subjectname,TRIM(replace(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))  exSubjectName"
					+ " 	from (    " + " 		select '" + subjectName
					+ "' as subjectName " + "	) a,k_key b,k_key c,k_key d  "
					+ "	where  b.departid in ('0','" + dpID + "') "
					+ " 	and  c.departid in ('0','" + dpID + "') "
					+ " 	and  d.departid in ('0','" + dpID + "') "
					+ "	and a.subjectname like concat('%',b.key1,'%')  "
					+ "	and a.subjectname like concat('%',c.key1,'%')  "
					+ "	and a.subjectname like concat('%',d.key1,'%') "
					+ " ) b where VocationID=" + VocationID
					+ " and  a.subjectname = b.exSubjectName";

			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getString("subjectname");
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}

	}

	/**
	 * 根据底稿的对应科目得到用户帐套的本级以及下级科目编号
	 * @param conn
	 * @param acc
	 * @param subjectname
	 * @return
	 * @throws Exception
	 */
	public String getSubjectIDByTaskSubectName(Connection conn,String acc,String subjectname) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select group_concat(\"'\",subjectid,\"'\") from c_account where AccPackageID =? and (subjectfullname2 =? or subjectfullname2 like concat(?,'/%')) and submonth=1";
//			org.util.Debug.prtOut("py getSubjectIDByTaskSubectName : " + sql);
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, subjectname);
			ps.setString(3, subjectname);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}else{
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getSubjectIDByTaskSubectName(Connection conn,int bTime,int eTime,String subjectname) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select group_concat(distinct \"'\",subjectid,\"'\") from c_account where SubYearMonth*12+SubMonth >=? and SubYearMonth*12+SubMonth <=? and (subjectfullname2 =? or subjectfullname2 like concat(?,'/%')) ";
//			org.util.Debug.prtOut("py getSubjectIDByTaskSubectName : " + sql);
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, bTime);
			ps.setInt(2, eTime);
			ps.setString(3, subjectname);
			ps.setString(4, subjectname);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}else{
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据ct_subjectname的值得到用户帐套的穿透科目编号
	 * @param conn
	 * @param acc
	 * @param subjects
	 * @param ct_subjectname
	 * @return
	 * @throws Exception
	 */
	public String getAccPkgSubjectID(Connection conn,String acc,String subjects,String ct_subjectname) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_account where AccPackageID =? and subjectid in ("+subjects+") and AccName =? and submonth=1";
//			org.util.Debug.prtOut("py getAccPkgSubjectID : " + sql);
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, ct_subjectname);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("subjectid");
			}else{
				return "";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getAccPkgSubjectID(Connection conn,int bTime,int eTime,String subjects,String ct_subjectname) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct subjectid from c_account where SubYearMonth*12+SubMonth >=? and SubYearMonth*12+SubMonth <=? and subjectid in ("+subjects+") and AccName =? ";
//			org.util.Debug.prtOut("py getAccPkgSubjectID : " + sql);
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, bTime);
			ps.setInt(2, eTime);
			ps.setString(3, ct_subjectname);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("subjectid");
			}else{
				return "";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据ct_subjectname的值得到用户帐套的穿透科目编号和核算编号
	 * result[0] : 科目编号
	 * result[1] : 核算编号
	 * result[2] : 核算类型
	 * @param conn
	 * @param acc
	 * @param subjects
	 * @param ct_subjectname
	 * @param allassitem	: 用于标志是否是往来核算
	 * @return
	 * @throws Exception
	 */
	public String [] getCtAssItemIDandAccID(Connection conn,String acc,String subjects,String ct_subjectname,String allassitem) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String [] result = new String[]{"","",""}; 
			String sql = "";
			String sqlstring = "";
			
			//往来核算
			if (allassitem==null || allassitem.equals("")){
				sql = "select distinct asstotalname from c_assitem where accpackageid='" + acc + "' and level0=1 " +
	    		" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
			    
			    while(rs.next()){
			    	sqlstring += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
			    }
			    if(!"".equals(sqlstring)){
			    	sqlstring = " and ( " + sqlstring.substring(0,sqlstring.length()-2)+ ") ";
			    }
			    
			}
			
			sql = "select * from c_assitementryacc where AccPackageID =? and accid in ("+subjects+") and AssItemName =? and submonth=1 " +sqlstring;
//			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, ct_subjectname);
			rs = ps.executeQuery();
			String assfullname = "";
			if(rs.next()){
				result[0] = rs.getString("accid"); 
				result[1] = rs.getString("assitemid"); 
				assfullname = rs.getString("AssTotalName1");
			}
			
			sql = "select * from c_assitem where AccPackageID =? and '"+assfullname+"' like concat(AssTotalName,'/%') and level0 = 1 and accid in ("+subjects+") ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			rs = ps.executeQuery();
			if(rs.next()){
				result[2] = rs.getString("assitemid"); 
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String [] getCtAssItemIDandAccID(Connection conn,String acc,int bTime,int eTime,String subjects,String ct_subjectname,String allassitem) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String [] result = new String[]{"","",""}; 
			String sql = "";
			String sqlstring = "";
			
			//往来核算
			if (allassitem==null || allassitem.equals("")){
				sql = "select distinct asstotalname from c_assitem where accpackageid='" + acc + "' and level0=1 " +
	    		" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
			    
			    while(rs.next()){
			    	sqlstring += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
			    }
			    if(!"".equals(sqlstring)){
			    	sqlstring = " and ( " + sqlstring.substring(0,sqlstring.length()-2)+ ") ";
			    }
			    
			}
			
			sql = "select distinct accid,assitemid,AssTotalName1 from c_assitementryacc where SubYearMonth*12+SubMonth >=? and SubYearMonth*12+SubMonth <=? and accid in ("+subjects+") and AssItemName =? and submonth=1 " +sqlstring;
//				System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, bTime);
			ps.setInt(2, eTime);
			ps.setString(3, ct_subjectname);
			rs = ps.executeQuery();
			String assfullname = "";
			if(rs.next()){
				result[0] = rs.getString("accid"); 
				result[1] = rs.getString("assitemid"); 
				assfullname = rs.getString("AssTotalName1");
			}
			
			sql = "select distinct assitemid from c_assitem where accpackageid=? and '"+assfullname+"' like concat(AssTotalName,'/%') and level0 = 1 and accid in ("+subjects+") ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			rs = ps.executeQuery();
			if(rs.next()){
				result[2] = rs.getString("assitemid"); 
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
