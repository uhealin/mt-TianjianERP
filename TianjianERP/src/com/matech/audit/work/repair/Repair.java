package com.matech.audit.work.repair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.function.SubjectResultService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.UTILString;

public class Repair {

	private Connection conn;

	public Repair(Connection conn) {
		this.conn = conn;
	}
//	 得到科目级别
	public String getSubjectGrade(String acc, String projectID) throws Exception {		
		ResultSet rs = null;
		PreparedStatement ps = null;
	
		try {			
			ps = conn.prepareStatement("select DISTINCT level0 from (select level0 FROM c_accpkgsubject where accpackageid='"
				+ acc
				+ "' union select level0 from z_usesubject where projectID='"
				+ projectID
				+ "' and accpackageID='"
				+ acc
				+ "' ) a order by level0");
			rs = ps.executeQuery();
			StringBuffer sbf = new StringBuffer("");
			int i = 0;
			while (rs.next()) {
				i++;
				sbf.append("<option value='" + rs.getInt(1) + "'>" + i + "级科目</option>");
			}			
			return sbf.toString();
		} catch (Exception e) {
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		} 
	}
		
	public String getVocationID(String acc)throws Exception{		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "0";
		try {
			String sql = "select count(*) from k_customer t1,k_standsubject t2 " +
			 "where t1.departid='" + acc.substring(0, 6) + "' " +
			 "and t2.level0=1 " +
     		 "and t1.vocationid=t2.vocationid ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			rs.next();
			if(rs.getInt(1) <= 0){
				result = "0";
			}else{
				sql = "select * from k_customer where departid='" + acc.substring(0, 6) + "'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				rs.next();
				result = rs.getString("VocationID");
			}
			
			return result;
		}catch (Exception e) {
			org.util.Debug.prtOut("异常．．．．．．");
			e.printStackTrace();
			return "异常．．．．．．";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String newData(String acc, String projectID, String SubjectID) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from (select subjectid from z_usesubject where projectID='"
					+ projectID
					+ "' and accpackageID='"
					+ acc
					+ "' union select subjectid from c_accpkgsubject where accpackageID='"
					+ acc
					+ "') a where 1=1 and subjectid = ? and subjectid in( select DISTINCT subjectid from z_subjectentryrectify where projectID='"
					+ projectID + "' and accpackageID='" + acc + "' ) limit 1";
			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				return "1";
			}

			return "0";
		} catch (Exception e) {
			org.util.Debug.prtOut("异常．．．．．．");
			e.printStackTrace();
			return "异常．．．．．．";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String deleteData(String acc, String projectID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String result = "";
			int ii = 0;
			String sql = "select a.* from z_usesubject a where a.projectID = '"+projectID+"' order by a.subjectID desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String SubjectID = rs.getString("SubjectID");
				sql = "select * from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and subjectid = ? and subjectid in( select DISTINCT subjectid from z_subjectentryrectify where projectID='"+projectID+"' and accpackageID='"+acc+"' )";
				ps = conn.prepareStatement(sql);
				ps.setString(1, SubjectID);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					ii++;
				}else{
					sql = "select * from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and ParentSubjectId =?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, SubjectID);
					rs1 = ps.executeQuery();
					if(rs1.next()){
						ii ++ ;
					}else{
						sql = "select ParentSubjectId ,count(ParentSubjectId) from z_usesubject where accpackageid='"+acc+"' and projectid='"+projectID+"' and ParentSubjectId=(select ParentSubjectId from z_usesubject where accpackageid='"+acc+"' and projectid='"+projectID+"' and subjectid=?) group by ParentSubjectId";
						ps = conn.prepareStatement(sql);
						ps.setString(1, SubjectID);
						rs1 = ps.executeQuery();
						if(rs1.next()){
							if(rs1.getInt(2)==1){
								sql = "update z_usesubject set isleaf=1 where accpackageid='"+acc+"' and projectid='"+projectID+"' and subjectID = '"+rs.getString(1)+"'";
								ps = conn.prepareStatement(sql);
								ps.execute();
							}
						}
					
						sql = "delete from c_assitementryacc where  accpackageID='"+acc+"' and accid =?";
						
						ps = conn.prepareStatement(sql);
						ps.setString(1, SubjectID);
						ps.execute();
						
						sql = "delete from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and SubjectId =?";
						ps = conn.prepareStatement(sql);
						ps.setString(1, SubjectID);
						ps.execute();
					}
				}
			}
			
			/**
			 * 删除所有没有调整的新增核算
			 */
			sql = "delete a  " +
			" from c_assitementryacc a " +
			" left join c_assitem b  " +
			" on b.accpackageId = ? " +
			" and a.accid = b.accid and a.assitemid=b.assitemid " +
			" left join z_assitementryrectify c " +
			" on c.ProjectID =? " +
			" and  a.accid=c.subjectid and a.assitemid=c.assitemid " +
			" where a.accpackageId = ? " +
			" and b.accpackageId is null " +
			" and c.ProjectID is null";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, projectID);
			ps.setString(3, acc);
			ps.execute();
			
			if(ii!=0)
				result = "该项目有［"+ii+"］科目已进行过调整或已有下级科目，不能删除；\n如要删除，请先删除调整或下级科目！";
			else
				result = "删除成功！";
			
//			new ManuacCountService(conn).deleteOne(projectID);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String deleteData(String acc, String projectID, String SubjectID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "select * from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and subjectid = ? and subjectid in( select DISTINCT subjectid from z_subjectentryrectify where projectID='"+projectID+"' and accpackageID='"+acc+"' )";
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				return "该科目已进行过调整，不能删除；\n如要删除，请先删除调整！";
			}
			sql = "select * from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and ParentSubjectId =?";
			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				return "该科目已有下级科目，不能删除；\n如要删除，请先删除下级科目";
			}
			
			sql = "select ParentSubjectId ,count(ParentSubjectId) from z_usesubject where accpackageid='"+acc+"' and projectid='"+projectID+"' and ParentSubjectId=(select ParentSubjectId from z_usesubject where accpackageid='"+acc+"' and projectid='"+projectID+"' and subjectid='"+SubjectID+"') group by ParentSubjectId";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(2)==1){
					sql = "update z_usesubject set isleaf=1 where accpackageid='"+acc+"' and projectid='"+projectID+"' and subjectID = '"+rs.getString(1)+"'";
					ps = conn.prepareStatement(sql);
					ps.execute();
				}
			}
		
			sql = "delete from c_assitementryacc where  accpackageID='"+acc+"' and accid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			ps.execute();
			
			sql = "delete from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and SubjectId =?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			ps.execute();
						
//			new ManuacCountService(conn).deleteOne(projectID);
			
			return "删除成功！";
		}catch (Exception e) {
			org.util.Debug.prtOut("异常．．．．．．");
			e.printStackTrace();
			return "异常．．．．．．";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String deleteData(String acc, String projectID, String SubjectID, String AssItemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from z_assitementryrectify where ProjectID = ? and SubjectID = ? and AssItemID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, SubjectID);
			ps.setString(3, AssItemID);
			rs = ps.executeQuery();
			if(rs.next()){
				return "该核算已进行过调整，不能删除；\n如要删除，请先删除调整！";
			}
			
			sql = "delete from c_assitementryacc where  accpackageID=? and accid =? and AssItemID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, SubjectID);
			ps.setString(3, AssItemID);
			ps.execute();
			
			return "删除成功！";
		}catch (Exception e) {
			e.printStackTrace();
			return "异常．．．．．．";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String isSave(String acc,String proid,String aName,String vid)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {		
			sql = "select 1 from k_standsubject where VocationID = '"+vid+"' and subjectname='"+aName+"' and level0=1 limit 1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select 1 from (select subjectid,subjectname from c_accpkgsubject where accpackageid='"+acc+"' and level0=1 union select subjectid,subjectname from z_usesubject where accpackageid='"+acc+"' and projectid='"+proid+"' and level0=1 ) a where subjectname = '"+aName+"' limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					return "2";
				}
				String key = new SubjectResultService(conn,acc).getTextKey(aName);
				org.util.Debug.prtOut("isSave: "+key);
				
				sql = "select * from (select subjectid,subjectname from c_accpkgsubject where accpackageid='"+acc+"' and level0=1 union select subjectid,subjectname from z_usesubject where accpackageid='"+acc+"' and projectid='"+proid+"' and level0=1 ) a where subjectname in  "+key;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					return "2";
				}else{
					return "0";
				}
			}else{
				return "1";
			}

		}catch (Exception e) {
			org.util.Debug.prtOut("异常．．．．．．");
			e.printStackTrace();
			return "3";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public String insertData(String acc, String projectID, String SubjectID,
			String ParentSubjectId, String SubjectName, String SubjectFullName,
			String Property) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "";
			
			/**
			 * 检查是否已经建过［调整科目］，有就返回，无就新增
			 */
			sql = "select 1 from z_usesubject where projectID=? and ParentSubjectId=? and SubjectName = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, ParentSubjectId);
			ps.setString(3, SubjectName);
			rs = ps.executeQuery();
			if(rs.next()){
				return "";
			}
			
			sql = "select * from (select subjectid FROM c_accpkgsubject where accpackageid='"+acc+"' and subjectid=? union select subjectid from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and subjectid=? ) a ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, SubjectID);
			ps.setString(2, SubjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				return "科目编号重复，请重新输入！";
			}
			
			String TID = "";
			int level =0;
			int isleaf = 1;
			if(!"".equals(ParentSubjectId)){
				sql = "select DISTINCT subjectid from (select subjectid FROM c_accpkgsubject where accpackageid='"+acc+"' and '"+ParentSubjectId+"' like concat(SubjectId,'%') and level0=1 union select TipSubjectId from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and SubjectId='"+ParentSubjectId+"' ) a";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					TID = rs.getString(1);
				}
				
				sql = "select DISTINCT level0 from (select level0 FROM c_accpkgsubject where accpackageid='"+acc+"' and SubjectId ='"+ParentSubjectId+"' union select level0 from z_usesubject where projectID='"+projectID+"' and accpackageID='"+acc+"' and SubjectId='"+ParentSubjectId+"' ) a";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					level = rs.getInt(1)+1;
				}
				sql = "update z_usesubject set isleaf=0 where accpackageid='"+acc+"' and projectid='"+projectID+"' and subjectID = '"+ParentSubjectId+"'";
				ps = conn.prepareStatement(sql);
				ps.execute();
			}else{
				TID = SubjectID;
				level = 1;
			}
	
			sql ="insert into z_usesubject(projectID,accpackageID,subjectID,ParentSubjectId,TipSubjectId,SubjectName,SubjectFullName,`level0`,Property,isleaf) value (?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, acc);
			ps.setString(3, SubjectID);
			ps.setString(4, ParentSubjectId);
			ps.setString(5, TID);
			ps.setString(6, SubjectName);
			ps.setString(7, SubjectFullName);
			ps.setInt(8, level);
			ps.setString(9, Property);
			ps.setInt(10, isleaf);
			ps.execute();
						
//			new ManuacCountService(conn).insertOne(projectID);
			
		return "保存成功！";
		} catch (Exception e) {
			org.util.Debug.prtOut("异常．．．．．．");
			e.printStackTrace();
			return "异常．．．．．．";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据“项目调整允许调整到1级”属性，增加1级科目的［调整科目］
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	public void insertData(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			
			/**
			 * 建项时无条件在一级科目下级增加一个新增科目［调整科目］
			 * 1.	一级科目有下级科目，无条件增加［调整科目］
			 * 2.	一级科目无下级科目，分有核算和无核算两种
			 * 		2.1	无核算：不用自动新增下级	//增加下级科目［调整科目］
			 * 		2.2 有核算：自动为每一类核算增加一个［调整核算］
			 * 注意：有核算的所有科目都不能新增下级科目
			 */
			
			String sql = "";
//			sql = "select 1 from s_config where sname='项目调整允许调整到1级' and svalue = '允许'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
				sql = "select * from c_accpkgsubject where  AccPackageID=? and level0=1  and isleaf=0 order by subjectid";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				rs = ps.executeQuery();
				while(rs.next()){
					String SubjectID = "";
					String ParentSubjectId = rs.getString("SubjectID");
					String SubjectName = rs.getString("SubjectName");
					String SubjectFullName = rs.getString("SubjectFullName"); 
					String Property = rs.getString("Property"); 
					
					sql = "select subjectID from c_accpkgsubject where accpackageID = ? and subjectfullname like concat(?,'/%') and level0 = 2 " +
					" union " +
					" select subjectID from z_usesubject where projectID = ? and subjectfullname like concat(?,'/%') and level0 = 2 " + 
					" order by subjectid desc limit 1";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setString(2, SubjectFullName);
					ps.setString(3, projectID);
					ps.setString(4, SubjectFullName);
					rs1 = ps.executeQuery();
					if(rs1.next()){
						SubjectID = rs1.getString(1);
					}
					DbUtil.close(rs1);
//					SubjectID = UTILString.getNewTaskCode(SubjectID);
					SubjectID = getNewID( SubjectID,  ParentSubjectId,  "1");
					
					insertData(AccPackageID,projectID,SubjectID,ParentSubjectId,"调整科目",SubjectFullName + "/调整科目",Property);
				}
			
				/**
				 * 增加核算调整
				 */
				insertAssitem( AccPackageID, projectID);
			
//			}	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 补全用户科目没有的标准科目
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	public void insertSubject(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String sql = "";
			
			int i =1;
			
			sql = "select *,substring(a.subjectid,1,1) as sid,substring(a.property,1,1) as pid from (" +
			"	select * from k_standsubject a " +
			"	where vocationid in (" +
			"		select vocationid from k_customer where departid = ? " + 
			"	) and level0 = 1" +
			") a left join (" +
			"	select subjectfullname2 from c_account where accpackageid =? and submonth=1 and level1=1	" +
			"	union 	" +
			"	select subjectfullname from z_usesubject where projectid = ? and level0=1 " +
			") b on a.subjectfullname = b.subjectfullname2 " +
			"where b.subjectfullname2 is null " +
			"order by a.subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID.substring(0, 6));
			ps.setString(i++, AccPackageID);
			ps.setString(i++, projectID);
			rs = ps.executeQuery();
			while(rs.next()){
				
				String SubjectID = "";
				
				String SubjectID1 = rs.getString("SubjectID");
				String ParentSubjectId = rs.getString("ParentSubjectId");
				String SubjectName = rs.getString("SubjectName");
				String SubjectFullName = rs.getString("SubjectFullName"); 
				String Property = "0" + rs.getString("pid"); 
				
				String sid = rs.getString("sid"); 
				
				sql = "select subjectID from c_accpkgsubject where accpackageID = ? and subjectid like concat(?,'%') and level0 = 1 " +
				" union " +
				" select subjectID from z_usesubject where projectID = ? and subjectid like concat(?,'%') and level0 = 1 " + 
				" order by subjectid desc limit 1";
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, sid);
				ps.setString(3, projectID);
				ps.setString(4, sid);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					SubjectID = rs1.getString(1);
				}
				DbUtil.close(rs1);
				if("".equals(SubjectID)){
					SubjectID = SubjectID1;
				}else{
					SubjectID = getNewID( SubjectID,  ParentSubjectId,  "1");
				}
				insertData(AccPackageID,projectID,SubjectID,ParentSubjectId,SubjectName,SubjectFullName,Property);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
	}
	
	/**
	 * 新增［调整核算］
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	public void insertAssitem(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String sql = "";
			
			int i =1;
			
			sql = "select * " +
			"from c_accpkgsubject a , (	" +
			"	select distinct accid,assitemid from c_assitem where accpackageid=? and level0 =1 and isleaf = 0" +
			")  b " +
			"where  accpackageid=? " +
			"and level0=1  " +
			"and isleaf=1 " +
			"and a.subjectid = b.accid " +
			"order by subjectid ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			rs = ps.executeQuery();
			while(rs.next()){
				String newAssitemID = "";
				
				String accid = rs.getString("accid");
				String assitemid = rs.getString("assitemid");
				
				sql = "select b.assitemid from c_assitem a,c_assitem b " +
					"where a.accpackageid=? and b.accpackageid=? " +
					"and a.accid = ? and a.assitemid = ? " +
					"and a.assitemid = b.parentassitemid " +
					"order by b.assitemid desc limit 1";
				i =1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, AccPackageID);
				ps.setString(i++, AccPackageID);
				ps.setString(i++, accid);
				ps.setString(i++, assitemid);
//				ps.setString(i++, accid);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					String assid = rs1.getString("assitemid");
//					newAssitemID = getNewID( assid,  assitemid,  "1");
					newAssitemID = UTILString.getNewTaskCode(assid);
				}
				DbUtil.close(rs1);
				DbUtil.close(ps);
				
				insertAssitem( AccPackageID,  projectID, accid, newAssitemID , assitemid, "调整核算");
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertAssitem(String acc, String projectID,String subjectID,String assitemID ,String parentAssitemID,String assitemName)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "";
			
			int i =1;
			
			/**
			 * 检查是否已经建过［调整核算］，有就返回，无就新增
			 */
			sql = "select 1 from c_assitem where accpackageid=? and accid=? and ParentAssItemId  =?  and assitemname = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, subjectID);
			ps.setString(i++, parentAssitemID);
			ps.setString(i++, assitemName);
			rs = ps.executeQuery();
			if(rs.next()){
				return;
			}
			
			/**
			 * 1、求出父核算的值，并改变父核算的isleaf 
			 */
			sql = "select * from c_assitem where accpackageid=? and accid=? and assitemid=? ";
			i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, subjectID);
			ps.setString(i++, parentAssitemID);
			rs = ps.executeQuery();
			if(rs.next()){
				String asstotalname = rs.getString("asstotalname");
				int level0 = rs.getInt("level0");
				String property = rs.getString("property");
				
				sql = "INSERT into c_assitem (AccPackageID,accid,assitemid,assitemname,asstotalname,parentassitemid,isleaf,level0,property) values (?,?,?,?,?, ?,1,?,?) " ;
				i =1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, subjectID);
				ps.setString(i++, assitemID);
				ps.setString(i++, assitemName);
				ps.setString(i++, asstotalname + "/" + assitemName);

				ps.setString(i++, parentAssitemID);
				ps.setInt(i++, level0 + 1);
				ps.setString(i++, property);
				ps.execute();
				DbUtil.close(ps);
				
//				i =1;
//				sql = "update c_assitem set isleaf = 0 where accpackageid=? and accid=? and assitemid=? ";
//				ps = conn.prepareStatement(sql);
//				ps.setString(i++, acc);
//				ps.setString(i++, subjectID);
//				ps.setString(i++, parentAssitemID);
//				ps.execute();
				
				/*删除新插入的核算体系*/
				i =1;
				sql = "delete from c_assitementryacc where accpackageid=? and accid=? and assitemid=? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, subjectID);
				ps.setString(i++, assitemID);
				ps.execute();
				DbUtil.close(ps);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			i =1;
			sql = "select 1 from c_assitementryacc where accpackageid=? and accid=? and assitemid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, subjectID);
			ps.setString(i++, assitemID);
			rs = ps.executeQuery();
			if(!rs.next()){
				sql = "";
				sql+=" insert into c_assitementryacc \n"; 
				sql+=" ( \n"; 
				sql+="        accpackageid,accid,assitemid,assitemname,subyearmonth,submonth, \n"; 
				sql+="        debitremain,creditremain,debitocc,creditocc, \n"; 
				sql+="        balance,debittotalocc,credittotalocc, \n"; 
				sql+="        direction,debitbalance,creditbalance,isleaf1,level1,asstotalname1,direction2,dataname \n"; 
				sql+=" ) \n"; 
				sql+=" SELECT accpackageid,accid,assitemid,assitemname,substring(accpackageid,7) as subyearmonth,submonth, \n"; 
				sql+="        0,0,0,0, \n"; 
				sql+="        0,0,0, \n"; 
				sql+="        direction,0,0,isleaf,`level0`,asstotalname,direction2,0 \n"; 
				sql+=" from   c_assitem a,k_month b, \n"; 
				sql+=" ( \n"; 
				sql+="     select  a.subjectid, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, \n";
				sql+="     ifnull(direction2,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end) direction2 \n";
				sql+="     FROM (select AccPackageID,subjectID,property from c_accpkgsubject where AccPackageID=? union select AccPackageID,subjectID,property from z_usesubject where AccPackageID=? and projectID=?) a \n";
				sql+="     left join c_account b on  b.accpackageid=?  and a.subjectid=b.subjectid and submonth=1 \n";
				sql+="     where a.accpackageid=?  \n"; 
				sql+="     and a.subjectid=?  \n"; 
				sql+=" ) c \n"; 
				sql+=" where accpackageid=?  \n"; 
				sql+="   and accid=? \n"; 
				sql+="   and assitemid=? \n"; 
				sql+="   and a.accid=c.subjectid \n"; 
				sql+="   and monthtype=12 \n"; 

				i =1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, acc);
				ps.setString(i++, projectID);
				
				ps.setString(i++, acc);
				ps.setString(i++, acc);
				ps.setString(i++, subjectID);
				
				ps.setString(i++, acc);
				ps.setString(i++, subjectID);
				ps.setString(i++, assitemID);
				ps.execute();
				DbUtil.close(ps);
			
				/*删除新插入的核算体系*/
				sql = "delete from c_assitem where accpackageid=? and accid=? and assitemid=? ";
				i =1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, subjectID);
				ps.setString(i++, assitemID);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
//	/**
//	 * 新增科目
//	 * @param AccPackageID
//	 * @param projectID
//	 * @param subjectID
//	 * @throws Exception
//	 */
//	public void insertData(String AccPackageID,String projectID,String subjectID) throws Exception {
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		ResultSet rs1 = null;
//		try {
//			String sql = "select 1 from s_config where sname='项目调整允许调整到1级' and svalue = '允许'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				sql = "select * from c_accpkgsubject where  AccPackageID=? and subjectid =? and level0=1  and isleaf=0 order by subjectid";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, AccPackageID);
//				ps.setString(2, subjectID);
//				rs = ps.executeQuery();
//				if(rs.next()){
//					String SubjectID = "";
//					String ParentSubjectId = rs.getString("SubjectID");
//					String SubjectName = rs.getString("SubjectName");
//					String SubjectFullName = rs.getString("SubjectFullName"); 
//					String Property = rs.getString("Property"); 
//					
//					sql = "select subjectID from c_accpkgsubject where accpackageID = ? and subjectfullname like concat(?,'/%') and level0 = 2 " +
//					" union " +
//					" select subjectID from z_usesubject where projectID = ? and subjectfullname like concat(?,'/%') and level0 = 2 " + 
//					" order by subjectid desc limit 1";
//					ps = conn.prepareStatement(sql);
//					ps.setString(1, AccPackageID);
//					ps.setString(2, SubjectFullName);
//					ps.setString(3, projectID);
//					ps.setString(4, SubjectFullName);
//					rs1 = ps.executeQuery();
//					if(rs1.next()){
//						SubjectID = rs1.getString(1);
//					}
//					DbUtil.close(rs1);
////					SubjectID = UTILString.getNewTaskCode(SubjectID);
//					SubjectID = getNewID( SubjectID,  ParentSubjectId,  "1");
//					
//					insertData(AccPackageID,projectID,SubjectID,ParentSubjectId,"调整科目",SubjectFullName + "/调整科目",Property);
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			DbUtil.close(rs1);
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//		}
//	}
	
	public boolean isConfig()throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean bool = false;
			String sql = "select 1 from s_config where sname='项目调整允许调整到1级' and svalue = '允许'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public boolean isRepair(String AccPackageID,String projectID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean bool = false;
			String sql = "select * from c_accpkgsubject a left join z_usesubject b " +
				" on  a.AccPackageID=? and a.level0=1  and a.isleaf=0 " +
				" and b.projectID = ? and a.subjectID = b.ParentSubjectId " +
				" where 1=1 and a.level0=1  and a.isleaf=0 " +
				" and b.subjectID is null ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, projectID);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = false;
			}else{
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getSubjectid (String projectID,String subjectid) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result = "";
			String sql = "select * from c_accpkgsubject where subjectid = ? and level0 =1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, subjectid);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select * from z_usesubject where projectID = ? and ParentSubjectId = ? limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectID);
				ps.setString(2, subjectid);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getString("subjectid");
				}
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
	
	
	//重用项目的科目体系
	public String getSubjectHtml (String oldProjectId,String newProjectId,String accpackageID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null ;
		ResultSet rs2 = null ;
		ResultSet rs3 = null ;
		ResultSet rs4 = null ;
		String sql = "" ;
		String subjectHtml = "" ;
		try {
			
			ProjectService pss = new ProjectService(conn) ; 
			Project project = pss.getProjectById(oldProjectId) ;
			String customerId = project.getCustomerId() ;
			
			if("".equals(customerId) || customerId == null) { 
				//获取客户信息失败
				return null ;
			}
	
			/**
			 * 重用项目的新增科目原则
			 * 1、本项目一定要存在相同的1级科目，是因为如果本项目是没有一级，新增的科目也没有意义
			 * 2、重用项目新增的一级不用新增，是因为有可能因为会计制度的不同，新增的一级也有可能不同
			 * 3、如果本项目的科目有核算，也不用重用此科目的下级新增
			 * 4、如果本项目的科目已经有调整了，也不用重用此科目的下级新增
			 */
			
//			sql = "select * from asdb_"+customerId+".z_usesubject where projectId=? and accpackageID=? order by ParentSubjectId" ;
			
			/**
			 * 求出所有非新增一级的新增科目
			 * pfullname1　为父科目的全路径（用户）
			 * pfullname　为	父科目的标准科目全路径
			 * cfullname  为一级科目的标准科目名
			 */
			sql = "select a.*,b.subjectfullname1 as pfullname1 ,b.subjectfullname2 as pfullname ,c.subjectfullname2 as cfullname " +
			" from asdb_"+customerId+".z_usesubject a,asdb_"+customerId+".c_account b,asdb_"+customerId+".c_account c " +
			" where a.projectId=? " +
			" and b.AccPackageID = ? " +
			" and c.AccPackageID = ? " +
			" and b.submonth =1 " +
			" and c.submonth =1 " +
			" and c.level1=1" +
			" and a.tipsubjectid = c.subjectid" +
			" and a.parentsubjectid = b.subjectid" +
			" order by a.ParentSubjectId ";
			
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, oldProjectId) ;
			ps.setString(2,project.getAccPackageId()) ;
			ps.setString(3,project.getAccPackageId()) ;
			rs = ps.executeQuery() ;
			int i = 1,j=1;
			String ParentSubjectId = "-1" ;
			String sSubjectID1 = "";
			while(rs.next()) {
				
				String SubjectID = rs.getString("SubjectID");
				String ParentSubjectID = rs.getString("ParentSubjectId");
				String SubjectName = rs.getString("SubjectName");
				String SubjectFullName = rs.getString("SubjectFullName");
				String Property = rs.getString("Property");
				int level0 = rs.getInt("level0");
				
				String pfullname1 = rs.getString("pfullname1");
				String pfullname = rs.getString("pfullname");
				String cfullname = rs.getString("cfullname");
				
				int ii = 1;
				/**
				 * 1、已经存在，全路径一样的
				 */
				sql = "select * from (" +
				" 	select SubjectID,ParentSubjectId,SubjectName,SubjectFullName,Property,level0 from c_accpkgsubject where AccPackageID = ? and SubjectFullName = ? " +
				" 	union" +
				" 	select SubjectID,ParentSubjectId,SubjectName,SubjectFullName,Property,level0 from z_usesubject where projectId = ?  and SubjectFullName = ? " +
				" ) b where 1=1 " ;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, SubjectFullName);
				ps.setString(ii++, newProjectId);
				ps.setString(ii++, SubjectFullName);
				rs2 = ps.executeQuery();
				if(rs2.next()){
					continue;
				}
				DbUtil.close(rs2);
				
				/**
				 * 2、存在，父科目的标准科目与科目名称都一样
				 */
				sql = "select * from (" +
				" 	select 1 from c_account where AccPackageID = ? and submonth =1 and SubjectFullName2 like concat(?,'/%') and accname = ? " +
				" 	union" +
				" 	select 1 from z_usesubject a,c_account b where a.projectId = ? and b.AccPackageID = ? and b.submonth =1 and b.SubjectFullName2 = ? and a.SubjectName = ? and a.parentsubjectid = b.subjectid " +
				" ) b where 1=1 " ;
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, pfullname);
				ps.setString(ii++, SubjectName);
				
				ps.setString(ii++, newProjectId);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, pfullname);
				ps.setString(ii++, SubjectName);
				rs2 = ps.executeQuery();
				if(rs2.next()){
					continue;
				}
				DbUtil.close(rs2);
				
				/**
				 * 开始插入
				 */
				//求出父科目的科目编号和科目,暂只支持用户科目为父科目
				sql = "select * from (" +
				" 	select SubjectID,AccName,SubjectFullName1,level1 from c_account where AccPackageID = ? and submonth =1 and SubjectFullName2 = ? " +
				" ) b where 1=1 " ;
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, pfullname);
				
				rs2 = ps.executeQuery();
				String sid = "";
				String sname = "";
				String sfullname = "";
				int level1 = 0;
				if(rs2.next()){
					sid = rs2.getString("SubjectID");
					sname = rs2.getString("AccName");
					sfullname = rs2.getString("SubjectFullName1");
					level1 = rs2.getInt("level1");
				}else{
					continue; //找不到父科目，不用新增
				}
				DbUtil.close(rs2);
				
				//求父科目有没有核算，有就不用新增　
				sql = "select 1 from c_assitem where accpackageID = ? and accid = ? ";
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, sid);
				rs2 = ps.executeQuery();
				if(rs2.next()){
					continue;
				}
				DbUtil.close(rs2);
				
				//求父科目有没有做过调整，有就不用新增
				sql = "select * from z_subjectentryrectify where projectid = ? and subjectid = ? ";
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, newProjectId);
				ps.setString(ii++, sid);
				rs2 = ps.executeQuery();
				if(rs2.next()){
					continue;
				}
				DbUtil.close(rs2);
				
				//求出新增科目的最新科目编号
				sql = "select subjectID from c_accpkgsubject where accpackageID = ? and subjectid like concat(?,'%') and level0 <= ?  " +
				" union " +
				" select subjectID from z_usesubject where projectID = ? and subjectid like concat(?,'%') and level0 <= ?  " + 
				" order by subjectid desc limit 1";
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, accpackageID);
				ps.setString(ii++, sid);
				ps.setInt(ii++, level1 + 1);
				
				ps.setString(ii++, newProjectId);
				ps.setString(ii++, sid);
				ps.setInt(ii++, level1 + 1);
				
				rs2 = ps.executeQuery();
				String sSubjectID = "";
				if(rs2.next()){
					sSubjectID = rs2.getString(1);
				}
				DbUtil.close(rs2);
				
				sSubjectID = getNewID( sSubjectID,  sid,  "1");
				if(sid.equals(ParentSubjectId)){
					sSubjectID = getNewID( sSubjectID1,  sid,  "1");
				}
				
				String sSubjectName =SubjectName;
				String sSubjectFullName =sfullname + "/" +SubjectName;
				
				subjectHtml += "\n<tr height=\"15\" bgColor=\"#F3F5F8\" onMouseOver=\"this.bgColor='#E4E8EF';\"onMouseOut=\"this.bgColor='#F3F5F8';\">" ;
				subjectHtml += "\n<td align=\"center\" nowrap=\"true\"><input type=\"checkbox\" id=\"choose_subjectValue"+i+"\" name=\"choose_subjectValue\" checked value=\""+SubjectID+"\"></td>" ;
				
				subjectHtml += "\n<td align=\"center\" nowrap=\"true\">";
				subjectHtml += "\n<input type=\"text\" name=\"subjectId"+SubjectID+"\" id=\"subjectId"+SubjectID+"\" size=\"10\"  value=\""+sSubjectID+"\">" ;
				subjectHtml += "\n<input type=\"hidden\" name=\"SubjectName"+SubjectID+"\" id=\"SubjectName"+SubjectID+"\" size=\"10\"  value=\""+SubjectName+"\">" ;
				subjectHtml += "\n<input type=\"hidden\" id=\"subjectFullName"+SubjectID+"\" name=\"subjectFullName"+SubjectID+"\" value=\""+sSubjectFullName+"\">";
				
				subjectHtml += "\n<input type=\"hidden\" name=\"ParentSubjectId"+SubjectID+"\" id=\"ParentSubjectId"+SubjectID+"\" size=\"10\"  value=\""+sid+"\">" ;
				subjectHtml += "\n<input type=\"hidden\" name=\"Property"+SubjectID+"\" id=\"Property"+SubjectID+"\" size=\"10\"  value=\""+Property+"\">" ;
				
				subjectHtml += "\n</td>" ;
				
				subjectHtml += "\n<td align=\"left\" nowrap=\"true\">"+sid+"</td>" ;
				subjectHtml += "\n<td align=\"left\" nowrap=\"true\">"+sSubjectName+"</td>" ;
				subjectHtml += "\n<td align=\"left\" nowrap=\"true\">"+sSubjectFullName+"</td>" ;
				if(Property.indexOf("1") > 0) {
					subjectHtml += "\n<td align=\"center\" nowrap=\"true\">借</td>" ;
				}else {
					subjectHtml += "\n<td align=\"center\" nowrap=\"true\">贷</td></tr>" ;
				}
				
				i++ ;
				
				ParentSubjectId = sid;
				sSubjectID1 = sSubjectID;
				
				
				
				/**
				 * 以下是小陆的代码
				 */
				//如果父级科目与上条记录相同（按父级科目排序）则j加1，不同j变为1，最后科目编号加上j
//				String pid = rs.getString("ParentSubjectId");
//				
//				if(ParentSubjectId.equals(pid)) {
//					j++ ;
//				}else {
//					j = 1 ;
//				}
//				
//				//看本项目中有无这个科目编号的上级科目，没有就放弃
//				boolean bnext = false;
//				if("".equals(pid)){
//					
//					bnext = true;
//				}else{
//					sql = "select 1 from c_accpkgsubject where subjectID=? and AccPackageID=? " ;	
//					ps = conn.prepareStatement(sql) ;
//					
//					ps.setString(1,pid) ;
//					ps.setString(2, accpackageID) ; 
//					rs2 = ps.executeQuery() ;
//					if(rs2.next()) {
//						bnext = true;
//					}	
//				}
//				
//				if(bnext){	
//					String parentSubjectId = rs.getString("ParentSubjectId") ;
//				    sql = "select max(subjectId) from (select subjectid,SubjectName" +
//					" from c_accpkgsubject where accpackageid=? and ParentSubjectId=? union select subjectid,SubjectName" +
//					" from z_usesubject where projectID=? and accpackageID=? and ParentSubjectId=?) a " +
//					" order by subjectid";
//				    
//				    ps = conn.prepareStatement(sql) ;
//				    ps.setString(1,accpackageID) ;
//				    ps.setString(2, parentSubjectId) ; 
//				    ps.setString(3,newProjectId) ;
//				    ps.setString(4,accpackageID) ;
//				    ps.setString(5,parentSubjectId) ;
//				    
//				    rs4 = ps.executeQuery() ;
//					String newSubjectIdTemp =  "" ;
//				    if(rs4.next()) {
//				    	newSubjectIdTemp = rs4.getString(1) ;
//				    }
//					
//                 //查看本项目中有无这个科目编号，有的话就重算。
//					sql = "select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where subjectID=? and AccPackageID=? and projectId=? union " 
//						+ "select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid=? and subjectID=?" ;
//					
//					ps = conn.prepareStatement(sql) ;
//					ps.setString(1,rs.getString("subjectId")) ;
//					ps.setString(2, accpackageID) ; 
//					ps.setString(3,newProjectId) ;
//					ps.setString(4, accpackageID) ; 
//					ps.setString(5,rs.getString("subjectId")) ;
//					
//					rs3 = ps.executeQuery() ;
//					String subjectIdTemp = "" ;
//					boolean isFullNameSame = false ;
//				
//					if(rs3.next()) {
//						if(!(rs3.getString("SubjectFullName").equals(rs.getString("SubjectFullName")))) {
//							
//						    String newSubjectId =  "" ;
//						    
//						    	 newSubjectId = newSubjectIdTemp ;
//						    	 
//						    	if("".equals(newSubjectId) || newSubjectId == null) {
//						    		newSubjectId = parentSubjectId+"01" ;
//						    		newSubjectIdTemp = newSubjectId ;
//						    	}else {
//						    		try {
//						    			newSubjectId = String.valueOf(Integer.parseInt(newSubjectId)+j) ;
//						    		}catch(NumberFormatException e) {
//						    			newSubjectId = parentSubjectId+"01" ;
//						    		}
//						    	}
//						    subjectIdTemp = newSubjectId ;
//						}else {
//							
//							isFullNameSame = true ; //科目编号且全路径相同，不再插入
//						}
//						
//					}else {
//                        //项目中没有这个编号，但有可能被同时插入的其它科目占用了
//						
//						if(!"".equals(rs.getString("ParentSubjectId")) 
//							&& ParentSubjectId.equals(rs.getString("ParentSubjectId"))
//						) {
//							try {
//								subjectIdTemp = String.valueOf(Integer.parseInt(newSubjectIdTemp)+j) ;
//				    		}catch(NumberFormatException e) {
//				    			subjectIdTemp = parentSubjectId+"01" ;
//				    		}
//						}else {
//							subjectIdTemp = rs.getString("subjectId") ;
//						}
//					}
//					
//					if(!isFullNameSame) {
//						subjectHtml += "<tr height=\"15\" bgColor=\"#F3F5F8\" onMouseOver=\"this.bgColor='#E4E8EF';\"onMouseOut=\"this.bgColor='#F3F5F8';\">" ;
//						subjectHtml += "<td align=\"center\" nowrap=\"true\"><input type=\"checkbox\" id=\"choose_subjectValue"+i+"\" name=\"choose_subjectValue\" checked value=\""+rs.getString("subjectId")+"\"></td>" ;
//						
//						subjectHtml += "<td align=\"center\" nowrap=\"true\"><input type=\"text\" name=\"subjectId"+rs.getString("subjectId")+"\" id=\"subjectId"+rs.getString("subjectId")+"\" size=\"10\"  value=\""+subjectIdTemp+"\">" ;
//						subjectHtml += "<input type=\"hidden\" id=\"subjectFullName"+rs.getString("subjectId")+"\" name=\"subjectFullName"+rs.getString("subjectId")+"\" value=\""+rs.getString("SubjectFullName")+"\"></td>" ;
//						subjectHtml += "<td align=\"left\" nowrap=\"true\">"+rs.getString("ParentSubjectId")+"</td>" ;
//						subjectHtml += "<td align=\"left\" nowrap=\"true\">"+rs.getString("SubjectName")+"</td>" ;
//						subjectHtml += "<td align=\"left\" nowrap=\"true\">"+rs.getString("SubjectFullName")+"</td>" ;
//						if(rs.getString("Property").indexOf("1") > 0) {
//							subjectHtml += "<td align=\"center\" nowrap=\"true\">借</td>" ;
//						}else {
//							subjectHtml += "<td align=\"center\" nowrap=\"true\">贷</td></tr>" ;
//						}
//						
//						i++ ;
//					}
//					
//				
//				} 
//				
//				ParentSubjectId = rs.getString("ParentSubjectId") ;
				
				/**
				 * 以上是小陆的代码
				 */
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
			DbUtil.close(rs2);
			DbUtil.close(rs3);
			DbUtil.close(rs4);
		}
		return subjectHtml ;
	}
	
	
	public void reuse(String oldProjectId,String newProjectId,String accpackageID,String oldSubjectId,String newSubjectId) {
		PreparedStatement ps = null ;

		try {
			
			ProjectService pss = new ProjectService(conn) ; 
			Project project = pss.getProjectById(oldProjectId) ; 
			String customerId = project.getCustomerId() ;
			String accId = project.getAccPackageId() ;
			
			String sql = "insert into z_usesubject  select ?,?,?, `ParentSubjectId`,`TipSubjectId`,`SubjectName`," 
				   + "`SubjectFullName`,`Property`,`level0`,`isleaf` from asdb_"+customerId+".z_usesubject where"
				   + " subjectID=? and AccPackageID=? and projectId=?" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, newProjectId);
			ps.setString(2, accpackageID);
			ps.setString(3, newSubjectId);
			ps.setString(4,oldSubjectId) ;
			ps.setString(5,accId) ;
			ps.setString(6,oldProjectId) ;
			ps.execute() ;
			
			insertAssitem(accpackageID, newProjectId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String getNewID(String OldID, String parent, String opt) {
		String newID = "";
		if ("0".equals(opt)) {
			newID = OldID + "01";
		} else {
			if (!OldID.equals(parent)) {
				String oid = OldID.substring(parent.length());
				String o1 = OldID.substring(parent.length(),parent.length() + 1);
				String o2 = OldID.substring(OldID.length() - 1, OldID.length());
				String oo1 = "";
				String oo2 = "";
				try {
					Integer.parseInt(o1);
				} catch (Exception e) {
					oo1 = o1;
				}
				try {
					Integer.parseInt(o2);
				} catch (Exception e) {
					oo2 = o2;
				}
				if ("".equals(oo1) && "".equals(oo2)) {
					oid = String.valueOf((Integer.parseInt(oid) + 1));
				} else {
					String so1 = oo1;
					String so2 = oo2;
					if(".".equals(oo1) || "+".equals(oo1) || "*".equals(oo1) || "?".equals(oo1)){
						so1 = "\\" + oo1;
					}
					if(".".equals(oo2) || "+".equals(oo2) || "*".equals(oo2) || "?".equals(oo2)){
						so2 = "\\" + oo2;
					}
					String str = oid.replaceAll(so1,"").replaceAll(so2, "");
					if("".equals(str)) str = "0";
					try {
						oid = String.valueOf((Integer.parseInt(str) + 1));
					} catch (Exception e) {
						oid = "1";
					}
					
					
				}

				if (oid.length() < 2) {
					oid = "0" + oid;
				}

				newID = parent + oo1 + oid + oo2;
			} else {
				newID = OldID + "01";
			}
		}

		return newID;
	}

	/**
	 * 检查
	 * @param AccPackageID
	 * @param projectID
	 * @return
	 * @throws Exception
	 */
	public String inspect(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int i = 1;
			StringBuffer sb = new StringBuffer("");
			String sql = "select distinct a.subjectid,a.subjectname,c.subjectid as newID,c.subjectname as newName " +
					"from c_accpkgsubject a , (	" +
					"	select distinct accid,assitemid from c_assitem where accpackageid=? and level0 =1 and isleaf = 0" +
					")  b ,z_usesubject c " +
					"where  a.accpackageid=? " +
					"and a.level0=1  " +
					"and a.isleaf=1 " +
					"and c.projectID=? " +
					"and a.subjectid = b.accid " +
					"and a.subjectid = c.ParentSubjectId " +
					"order by a.subjectid ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, projectID);
			rs = ps.executeQuery();
			sb.append("注意：\n");
			i = 0; 
			while(rs.next()){
				i++;
				String subjectid = rs.getString("subjectid");
				String subjectname = rs.getString("subjectname");
				String newID = rs.getString("newID");
				String newName = rs.getString("newName");
				sb.append("［"+subjectid+"］"+subjectname+"科目有核算又有下级科目［"+newID+"］"+newName+"\n");
			}
			sb.append("\n请在初始化结束后，删除该下级科目和对应调整，重新调整\n");
			
			if(i == 0) {
				sb = new StringBuffer("");
			}
			
			return sb.toString();	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 检查有没有做完科目完照性
	 */
	public boolean check(String AccPackageID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean bool = true;
			int i = 1;
			
			String sql = "select * from( " +
			"	select distinct standid as stansubjectid,standkey as stansubjectname,userkey as expsubjectname,level0 from z_keyresult  " +
			") t1  right outer join (  " +
			"	select   " +
			"	ifnull(b.subjectid,a.subjectid) subjectid,   " +
			"	ifnull(b.accname,a.subjectname) accname " +
			"	from c_accpkgsubject a  " +
			"	left join c_account b " +
			"	on a.accpackageid= ? and b.accpackageid=? " +
			"	and b.submonth=1 and a.subjectid=b.subjectid  " +
			"	where a.accpackageid=? and a.level0=1" +
			") t2 on t1.expsubjectname = t2.accname  " +
			"where t1.stansubjectid is null";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = false;
			}
			
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int check(String AccPackageID,String projectID,String SubjectID,String ParentSubjectId,String SubjectName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int i = 1;
			
			String sql = "select 1 from c_accpkgsubject where AccPackageID = ? and SubjectID = ? " +
					" union " +
					" select 1 from z_usesubject where projectid = ? and SubjectID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, projectID);
			ps.setString(i++, SubjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				return 1; //科目编号重复 
			}
			sql ="select 1 from c_accpkgsubject where AccPackageID = ? and level0=1 and SubjectName = ? " +		//1级科目名
			" union " +
			"select 1 from c_accpkgsubject where AccPackageID = ? and ParentSubjectId = ? and SubjectName = ? " +	//下级科目名
			" union " +
			" select 1 from z_usesubject where projectid = ? and ParentSubjectId = ? and SubjectName = ? " + //下级新增科目名
			" union " +
			" select 1 from z_usesubject where projectid = ? and level0=1 and SubjectName = ? ";		//下级新增科目名
			i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectName);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, ParentSubjectId);
			ps.setString(i++, SubjectName);
			
			ps.setString(i++, projectID);
			ps.setString(i++, ParentSubjectId);
			ps.setString(i++, SubjectName);
			
			ps.setString(i++, projectID);
			ps.setString(i++, SubjectName);
			
			rs = ps.executeQuery();
			if(rs.next()){
				return 2; //科目名重复 
			}
			
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int check(String AccPackageID,String projectID,String SubjectID,String AssitemID,String ParentAssitemID,String AssItemName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int i = 1;
			
			String sql = "select 1 from c_assitem where AccPackageID = ? and AccID = ? and AssItemID = ? " +
					" union " +
					" select 1 from c_assitementryacc where AccPackageID = ? and AccID = ? and AssItemID = ? and submonth=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AssitemID);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AssitemID);
			rs = ps.executeQuery();
			if(rs.next()){
				return 1; //科目编号重复 
			}
			
			sql ="select 1 from c_assitem where AccPackageID = ? and AccID = ? and AssItemName = ?  and level0=1  " +		//1级科目名
			" union " +
			" select 1 from c_assitem where AccPackageID = ? and AccID = ? and ParentAssItemId = ?  and AssItemName = ?   " +	//下级科目名
			
			" union " +
			" select 1 from c_assitementryacc where AccPackageID = ? and AccID = ? and AssItemName = ?  and level1=1 and submonth=1  " + //下级新增科目名
			" union " +
			" select 1 from c_assitementryacc a,(" +
			"	select AccID,AssItemID from c_assitem where AccPackageID = ? and accid = ? and ParentAssItemId = ? " +
			" ) b  where AccPackageID = ? and a.AccID = ?  and submonth = 1 and AssItemName = ? and a.AccID = b.AccID and a.AssItemID = b.AssItemID ";		//下级新增科目名
			
			i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AssItemName);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, ParentAssitemID);
			ps.setString(i++, AssItemName);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AssItemName);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, ParentAssitemID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AssItemName);
			
			rs = ps.executeQuery();
			if(rs.next()){
				return 2; //科目名重复 
			}
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public String checkCopy(String acc,String projectID,String subjectID,String assitemID,String oSubjectID,String oAssitemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			if(oAssitemID == null){	//复制科目的核算为空
				sql = "select 1 from c_assitementryacc where AccPackageID = '"+acc+"' and accid = '"+oSubjectID+"' and submonth = 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){	
					return "本科目存在核算，不能作为批量复制的目标科目";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql = "select 1 from z_subjectentryrectify where projectid = '"+projectID+"' and subjectid = '"+oSubjectID+"' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){	
					return "本科目已存在调整，不能作为批量复制的目标科目";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
			}else if(oAssitemID != null){		//复制科目的核算不为空
				sql = "select 1 from c_assitementryacc where AccPackageID = '"+acc+"' and accid = '"+oSubjectID+"' and submonth = 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(!rs.next()){
					return "科目不存在核算，请选择科目到叶子";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql = "select * from c_subjectassitem where AccPackageID = '"+acc+"' and subjectid = '"+oSubjectID+"' and assitemid = '"+oAssitemID+"' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(!rs.next()){
					return "此核算为不披露核算，请重新选择核算类型";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql = "select 1 from z_assitementryrectify where projectid = '"+projectID+"' and subjectid = '"+oSubjectID+"' and assitemid = '"+oAssitemID+"' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){	
					return "本核算已存在调整，不能作为批量复制的目标核算";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				if(assitemID != null && oAssitemID != null && assitemID.equals(oAssitemID)){
					return "同一核算类型是不用复制";
				}
				
			}
			
			return "0";
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 批量新增
	 */
	public void copySave(String acc,String projectID,String subjectID,String assitemID,String oSubjectID,String oAssitemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			
			System.out.println(subjectID +"|" + assitemID + "|" + oSubjectID +"|" + oAssitemID);
			
			/**
			 * 检查
			 */
			String result = checkCopy( acc, projectID, subjectID, assitemID, oSubjectID, oAssitemID);
			System.out.println(result);
			if(!"0".equals(result)){
				return ;
			}
			
			RectifyService rectifyService = new RectifyService(conn);
			if(assitemID == null){
					
				sql = "select a.* " +
				" from c_accpkgsubject a ,c_accpkgsubject b " +
				" where a.AccPackageID = '"+acc+"' " +
				" and b.AccPackageID = '"+acc+"' " +
				" and b.SubjectID = '"+subjectID+"' " +
				" and a.isleaf = 1 " +
				" and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and SubMonth = 1 and isleaf1=1 and a.subjectid=accid ) " +
				" and (a.SubjectFullName = b.SubjectFullName or a.SubjectFullName like concat(b.SubjectFullName ,'/%')) " +
				" order by a.subjectid ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					String SubjectName = rs.getString("SubjectName");
					
					/**
					 * 检查名称是否已经存在
					 */
					sql = "select 1 " +
					" from c_accpkgsubject a ,c_accpkgsubject b " +
					" where a.AccPackageID = '"+acc+"' " +
					" and b.AccPackageID = '"+acc+"' " +
					" and b.SubjectID = '"+oSubjectID+"' " +
					" and a.isleaf = 1 " +
					" and a.SubjectName = '"+SubjectName+"' " +
					" and (a.SubjectFullName = b.SubjectFullName or a.SubjectFullName like concat(b.SubjectFullName ,'/%')) " +
					
					" union " +
					" select 1 from z_usesubject " +
					" where accpackageid = '"+acc+"' " +
					" and ParentSubjectId = '"+oSubjectID+"' " +
					" and SubjectName = '"+SubjectName+"' " ;
					ps = conn.prepareStatement(sql);
					rs1 = ps.executeQuery();
					if(rs1.next()){
						continue;
					}
					DbUtil.close(rs1);
					DbUtil.close(ps);
					
					if(oAssitemID == null){
//							复制科目到科目
						rectifyService.insertData(acc, projectID, SubjectName, oSubjectID);	//新增科目	
					}else{
//							复制科目到核算
						sql = "select AssItemID " +
						" from c_assitementryacc " +
						" where AccPackageID = '"+acc+"' " +
						" and SubMonth = 1 " +
						" and accid = '"+oSubjectID+"' " +
						" and assitemid like concat('"+oAssitemID+"','%') " +
						" and level1 = 2 order by length(AssItemID) desc, AssItemID desc limit 1";
						ps = conn.prepareStatement(sql);
						rs1 = ps.executeQuery();
						String oid = "";
						if(rs1.next()){
							oid = rs1.getString(1);
						}
						DbUtil.close(rs1);
						DbUtil.close(ps);
						oid = UTILString.getNewTaskCode(oid);
						
						rectifyService.insertAssitem( acc,  projectID, oAssitemID, SubjectName, oSubjectID,oid );  //新增核算	
					}
					
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}else if(assitemID != null){
				sql = "select a.* " +
				" from c_assitementryacc a,c_assitementryacc b " +
				" where a.AccPackageID = '"+acc+"' " +
				" and b.AccPackageID = '"+acc+"' " +
				" and b.accid = '"+subjectID+"' " +
				" and b.assitemid = '"+assitemID+"' " +
				" and a.submonth = 1 " +
				" and b.submonth = 1 " +
				" and a.IsLeaf1 = 1 " +
				" and (a.AssTotalName1 = b.AssTotalName1 or a.AssTotalName1 like concat(b.AssTotalName1 ,'/%')) " +
				" order by a.accid,a.assitemid ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					String AssItemName = rs.getString("AssItemName");
					
					sql = "select 1 " +
					" from c_assitementryacc a,c_assitementryacc b " +
					" where a.AccPackageID = '"+acc+"' " +
					" and b.AccPackageID = '"+acc+"' " +
					" and b.accid = '"+oSubjectID+"' " +
					" and b.assitemid = '"+oAssitemID+"' " +
					" and a.submonth = 1 " +
					" and b.submonth = 1 " +
					" and a.IsLeaf1 = 1 " +
					" and a.AssItemName = '"+AssItemName+"'" +
					" and (a.AssTotalName1 = b.AssTotalName1 or a.AssTotalName1 like concat(b.AssTotalName1 ,'/%')) " +
					" order by a.accid,a.assitemid ";
					ps = conn.prepareStatement(sql);
					rs1 = ps.executeQuery();
					if(rs1.next()){
						continue;
					}
					DbUtil.close(rs1);
					DbUtil.close(ps);
					
					if(oAssitemID == null){
//							复制科目到科目
						rectifyService.insertData(acc, projectID, AssItemName, oSubjectID);	//新增科目	
					}else{
//							复制科目到核算
						
						sql = "select AssItemID " +
						" from c_assitementryacc " +
						" where AccPackageID = '"+acc+"' " +
						" and SubMonth = 1 " +
						" and accid = '"+oSubjectID+"' " +
						" and assitemid like concat('"+oAssitemID+"','%') " +
						" and level1 = 2 order by length(AssItemID) desc, AssItemID desc limit 1";
						ps = conn.prepareStatement(sql);
						rs1 = ps.executeQuery();
						String oid = "";
						if(rs1.next()){
							oid = rs1.getString(1);
						}
						DbUtil.close(rs1);
						DbUtil.close(ps);
						oid = UTILString.getNewTaskCode(oid);
						
						rectifyService.insertAssitem( acc,  projectID, oAssitemID, AssItemName, oSubjectID,oid );  //新增核算	
					}
					
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
			}
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
}
