package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.autotoken.AutoTokenService;
import com.matech.audit.service.keys.KeyValue;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.util.UTILString;

public abstract class AbstractAreaFunction implements AreaFunction {
	
	/**
	 * 临时表的表名
	 */
	public String tempTable = "";
	public String getTempTable() {
		return tempTable;
	}

	public abstract ResultSet process(HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			Connection conn, Map args) throws Exception;

	public ResultSet getResultSetByFormulary(HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			Connection conn, Map args) {

		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;

		try {
			String s3 = "select strsql from k_areafunction where id= ? and typeid in( 0,?) order by typeid desc";
			preparedstatement = conn.prepareStatement(s3);
			preparedstatement.setString(1, (String) args.get("_id"));
			preparedstatement.setString(2, (String) args.get("_typeid"));
			resultset = preparedstatement.executeQuery();

			String strsql = "";
			if (resultset.next()) {
				strsql = resultset.getString("strsql");

				strsql = setSqlArguments(strsql, args);
			} else {
				throw new Exception("未找到areaid=" + (String) args.get("_id")
						+ "typeid=" + (String) args.get("_typeid") + "设置的列公式");
			}

			//执行指定的SQL

			preparedstatement = conn.prepareStatement(strsql);
			resultset = preparedstatement.executeQuery();

			return resultset;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String setSqlArguments(String sql, Map args) throws Exception {

		String t1[] = UTILString.getVaribles(sql);
		String strParam = "";

		for (int i = 0; i < t1.length; i++) {
			strParam = (String) args.get(t1[i]);
			if (strParam == null || strParam.equals("")) {
				throw new Exception("公式［" + (String) args.get("_id") + "_"
						+ (String) args.get("_typeid") + "］访问参数设置有误，出现" + t1[i]
						+ "未赋值!");
			}
			sql = sql.replaceAll("\\$\\{" + t1[i] + "\\}", strParam);

		}
		return sql;
	}

	//根据标准科目名，找出客户的科目id
	public String getClientIDByStandName(Connection conn, String apkID,
			String prjID, String subjectName) throws Exception {
		Statement st = conn.createStatement();
		String sql = "  select subjectid from c_account where accpackageid="
				+ apkID + "  \n" + "  and subjectfullname2='" + subjectName
				+ "' \n" + "  union  \n"
				+ "  select subjectid from z_usesubject where  accpackageid="
				+ apkID + " and projectid=" + prjID + " and subjectfullname ='"
				+ subjectName + "'  \n";
		String subjectid = "";
		ResultSet rs = st.executeQuery(sql);
		if (rs.next()) {
			subjectid = rs.getString(1);
		} else {
			subjectid = "null";
		}
		return subjectid;
	}

	/**
	 *
	 * @param conn Connection
	 * @param apkID String
	 * @param prjID String
	 * @param subjectName String
	 * @return String
	 * @throws Exception
	 */
	public String[] getClientIDAndDirectionByStandName(Connection conn,
			String apkID, String prjID, String subjectName) throws Exception {
		String[] result = { "", "" };
		Statement st = conn.createStatement();
		String sql = "  select subjectid,direction2 from c_account where accpackageid="
				+ apkID
				+ "  \n"
				+ "  and subjectfullname2='"
				+ subjectName
				+ "' \n"
				+ "  union  \n"
				+ "select subjectid,case when property=1 then 1 when property=2 then -1 else 1 end as direction from z_usesubject where  accpackageid="
				+ apkID
				+ " and projectid="
				+ prjID
				+ " and subjectfullname ='"
				+ subjectName + "'  \n";
		ResultSet rs = st.executeQuery(sql);
		if (rs.next()) {
			result[0] = rs.getString(1);
			result[1] = rs.getString(2);
		} else {
			result[0] = "null";
			result[1] = "1";
		}
		return result;
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
			String sql = "select distinct projectid from z_task where manuid="
					+ manuid;
			st = conn.createStatement();
//			rs = st.executeQuery(sql);
//			if (rs.next()) {
//				new KeyValue().updateTaskToSubjectName(conn, rs.getString(1));
//			}
//			rs.close();

			sql = "select subjectname from z_task where manuid=" + manuid;

			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return subjectname;
	}

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

//			new KeyValue().updateTaskToSubjectName(conn, projectid);

			st = conn.createStatement();
			String sql = "select subjectname from z_task where projectid="
					+ projectid + " and taskcode='" + taskcode
					+ "' and isleaf=1";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return subjectname;
	}

	/**
	 * 根据项目编号，获取项目的起始年月和结束年月信息，放到4个字符串的数组中返回
	 * result[0]:起始年
	 * result[1]:起始月
	 * result[1]:结束年
	 * result[1]:结束月
	 * @param conn Connection
	 * @param projectid String
	 * @return String[]
	 * @throws Exception
	 */
	public int[] getProjectAuditAreaByProjectid(Connection conn,
			String projectid) throws Exception {
		int[] result = { 0, 0, 0, 0 };
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "select audittimebegin,audittimeend from asdb.z_project where projectid="
					+ projectid;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				String strStart = rs.getString(1);
				String strEnd = rs.getString(2);

				if (strStart != null && strStart.length() == 10) {
					result[0] = Integer.parseInt(strStart.substring(0, 4));
					result[1] = Integer.parseInt(strStart.substring(5, 7));
				}

				if (strEnd != null && strEnd.length() == 10) {
					result[2] = Integer.parseInt(strEnd.substring(0, 4));
					result[3] = Integer.parseInt(strEnd.substring(5, 7));
				}
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return result;
	}

	public String[] getProjectAuditAreaStringByProjectid(Connection conn,
			String projectid) throws Exception {
		String[] result = { "","","","" };
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "select audittimebegin,audittimeend from asdb.z_project where projectid="
					+ projectid;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				String strStart = rs.getString(1);
				String strEnd = rs.getString(2);

				if (strStart != null && strStart.length() == 10) {
					result[0] = strStart.substring(0, 4);
					result[1] = strStart.substring(5, 7);
				}

				if (strEnd != null && strEnd.length() == 10) {
					result[2] = strEnd.substring(0, 4);
					result[3] = strEnd.substring(5, 7);
				}
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return result;
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

			//System.out.println("yzm:sql="+sql);

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
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}

	}

	public String changeSubjectFullName(Connection conn, String projectID,
			String subjectFullName) throws Exception {
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
			if (rs.next()) {
				dpID = rs.getString("customerid");
				VocationID = rs.getString("VocationID");
			}

			sql = "select a.* from k_standsubject a ,("
					+ " 	select a.subjectname,replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2) exSubjectName"
					+ " 	from (    "
					+ " 		select '"
					+ subjectFullName
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
					+ subjectFullName
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
					+ " 	from (    "
					+ " 		select '"
					+ subjectFullName
					+ "' as subjectName "
					+ "	) a,k_key b,k_key c,k_key d  "
					+ "	where  b.departid in ('0','"
					+ dpID
					+ "') "
					+ " 	and  c.departid in ('0','"
					+ dpID
					+ "') "
					+ " 	and  d.departid in ('0','"
					+ dpID
					+ "') "
					+ "	and a.subjectname like concat('%',b.key1,'%')  "
					+ "	and a.subjectname like concat('%',c.key1,'%')  "
					+ "	and a.subjectname like concat('%',d.key1,'%') "
					+ " ) b where VocationID="
					+ VocationID
					+ " and  (a.subjectname = b.exSubjectName or a.subjectfullname = b.exSubjectName)";

			//System.out.println("yzm:sql="+sql);

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
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}

	}
	
	/**
	 * 通过科目名称找科目编号
	 * 支持跨年
	 * @param conn
	 * @param projectID
	 * @param subjectFullName
	 * @return
	 * @throws Exception
	 */
	
	public String[] getSubjectIdBySubjectName(Connection conn, String projectID,
			String subjectName) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String[] subjectids = null;
		try {
			
	
			ProjectService projectService = new ProjectService(conn);
			Project project = projectService.getProjectById(projectID);
			
			 String strStart = project.getAuditTimeBegin();
             String strEnd = project.getAuditTimeEnd();

             int strStartYear = -1;
             int strEndYear = -1;
             if (strStart != null && strStart.length() == 10) {
            	 strStartYear = Integer.parseInt(strStart.substring(0, 4));
             }

             if (strEnd != null && strEnd.length() == 10) {
            	 strEndYear = Integer.parseInt(strEnd.substring(0, 4));
             }
		
			String customerId = project.getCustomerId();
			String accpackageId =  project.getAccPackageId();
			
			String sql = "select subjectfullname1 from c_account where AccPackageID = '"+accpackageId+"' and subjectfullname2='"+subjectName+"'";
			st = conn.createStatement();
			rs = st.executeQuery(sql); 
			
			if (rs.next()) {	
				subjectName = rs.getString(1)+"";	
			}
			rs.close();
			st.close();
	
			sql = "select a.subjectid from c_accpkgsubject a, \n"
						+" ( \n"
						+" select distinct b.subjectid  from  \n"
						+" ( \n"
						+" select accpackageid,stockid,subjectid,InventoryInOutType,InventoryEntryId,InventoryDate,sum(occurvalue) as occurvalue,oldVoucherID,TypeID,VchDate from  c_inventoryentry where accpackageid = "+accpackageId+" group by InventoryInOutType,oldVoucherID,TypeID,VchDate \n"
						+" )a left join  \n"
						+" c_subjectentry b \n"
						+" on  \n"
						+" a.accpackageid = b.accpackageid \n"
						+" and a.oldVoucherID = b.oldVoucherID  \n"
						+" and a.TypeID = b.TypeID  \n"
						+" and a.VchDate = b.VchDate \n"
						+" )b \n"
						+" where a.accpackageid = "+accpackageId+" \n"
						+" and (a.subjectfullname = '"+subjectName+"' or subjectfullname like concat('"+subjectName+"','/%')) \n"
						+" and a.subjectid=b.subjectid \n";
			st = conn.createStatement();
			rs = st.executeQuery(sql); 
			
			String subjectid = "";
			if (rs.next()) {	
				subjectid = rs.getString(1)+"";	
			}

			
			AutoTokenService autoTokenService = new AutoTokenService(conn);
			
			
			if(strStartYear!=strEndYear){
				
				subjectids = autoTokenService.getsubjectIdsBySubjectId(customerId, strStartYear, strEndYear, subjectid);
			
			}else{
				subjectids = new String[]{subjectid};
			}
			
			return subjectids;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		
		return subjectids;

	}
	
	
	/**
	 * 为［远方、远光、浪潮］做的判断
	 * @param conn
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public boolean isFunctionType(Connection conn,String acc) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_accpackage a ,k_dic b where accpackageid="+acc+" and ctype = 'functionType' and SoftVersion like concat('%',b.value,'%')";
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				return true;
			}else{
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
	}

}
