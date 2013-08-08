package com.matech.audit.service.proclamation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.proclamation.model.Proclamation;
import com.matech.audit.service.proclamation.model.ProclamationFlow;
import com.matech.framework.pub.db.DbUtil;

/**
 * @author YMM
 * 2.通知公告
 *
 */
public class ProclamationService {
	
	Connection conn=null;
	
	public  ProclamationService(Connection conn) {
		this.conn=conn;
	}
	
	/**
	 * 新增
	 * @param proclamation
	 * @return
	 */
	public boolean add(Proclamation proclamation){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `k_proclamation` \n"
		            +"(`uuid`,`title`,`departmentId`,`userId`,`publishDate`,`content`,`fileName`,`property`,status,ctype,up,upDates,goDate,endGoDate) \n"
		            +"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, proclamation.getUuid());
			ps.setString(i++, proclamation.getTitle());
			ps.setString(i++, proclamation.getDepartmentId());
			ps.setString(i++, proclamation.getUserId());
			ps.setString(i++, proclamation.getPublishDate());
			ps.setString(i++, proclamation.getContent());
			ps.setString(i++, proclamation.getFileName());
			ps.setString(i++, proclamation.getProperty());
			ps.setString(i++, "未发起");
			ps.setString(i++, proclamation.getCtype());
			ps.setString(i++, proclamation.getUp());
			ps.setInt(i++, proclamation.getUpDates());
			ps.setString(i++, proclamation.getGoDate());
			ps.setString(i++, proclamation.getEndGoDate());
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("新增公告出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 修改
	 * @param proclamation
	 * @return
	 */
	public boolean update(Proclamation proclamation){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "  UPDATE `asdb`.`k_proclamation` \n"
					 +"	SET  \n"
					 +"	  `title` = ?, \n"
					// +"	  `departmentId` = ?, \n"
					 //+"	  `userId` = ?, \n"
					 +"	  `content` = ?, \n"
					 +"	  `filename` = ?, \n"
					 +"	  `property` = ?, \n"
					 +"    ctype=? , \n"
					 +"    up= ?, \n"
					 +"    upDates= ?,\n "
					 +"    goDate= ? ,\n"
					 +"    endGoDate= ? \n"
					 +"	WHERE `uuid` = ?;";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, proclamation.getTitle());
			//ps.setString(i++, proclamation.getDepartmentId());
			//ps.setString(i++, proclamation.getUserId());
			ps.setString(i++, proclamation.getContent());
			ps.setString(i++, proclamation.getFileName());
			ps.setString(i++, proclamation.getProperty());
			ps.setString(i++, proclamation.getCtype());
			ps.setString(i++, proclamation.getUp());
			ps.setInt(i++, proclamation.getUpDates());
			ps.setString(i++,proclamation.getGoDate());
			ps.setString(i++,proclamation.getEndGoDate());
			ps.setString(i++, proclamation.getUuid());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("修公告件出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 删除
	 * @param uuid
	 * @return
	 */
	public boolean delete(String uuid){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "delete from k_proclamation where uuid=?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("删除公告出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 *得到公告信息 
	 * @param uuid
	 * @return
	 */
	public Proclamation getProclamation(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Proclamation proclamation = new Proclamation();
		
		String sql = "SELECT`uuid`,`title`,`departmentId`,`userId`,`publishDate`,`content`,`fileName`,`readUserId`,`property`,ctype,up,updates,goDate,endGoDate \n"
					+"FROM `k_proclamation` \n"
					+"where uuid=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				proclamation.setUuid(uuid);
				proclamation.setTitle(rs.getString("title"));
				proclamation.setDepartmentId(rs.getString("departmentId"));
				proclamation.setUserId(rs.getString("userId"));
				proclamation.setPublishDate(rs.getString("publishDate"));
				proclamation.setContent(rs.getString("content"));
				proclamation.setFileName(rs.getString("fileName"));
				proclamation.setReadUserId(rs.getString("readUserId"));
				proclamation.setProperty(rs.getString("property"));
				proclamation.setCtype(rs.getString("ctype"));
				proclamation.setUp(rs.getString("up"));
				proclamation.setUpDates(rs.getInt("upDates"));
				proclamation.setGoDate(rs.getString("goDate"));
				proclamation.setEndGoDate(rs.getString("endGoDate"));
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return proclamation;
	}
	
	
	/**
	 * 得到公告信息 (有外键的找出中文)
	 * @param uuid
	 * @return
	 */
	public Proclamation getCNProclamation(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Proclamation proclamation = new Proclamation();
		
		String sql = " SELECT`uuid`,`title`,b.departname AS departmentId,c.name AS userId, \n" 
					+"`publishDate`,`content`,`fileName`,`readUserId`,a.`ctype`,a.`property` \n"
					+"FROM `k_proclamation` a \n"
					+"LEFT JOIN k_department b ON a.departmentId = b.autoid \n"
					+"LEFT JOIN k_user c ON a.userid= c.id \n"
					+"WHERE UUID=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				proclamation.setUuid(uuid);
				proclamation.setTitle(rs.getString("title"));
				proclamation.setDepartmentId(rs.getString("departmentId"));
				proclamation.setUserId(rs.getString("userId"));
				proclamation.setPublishDate(rs.getString("publishDate"));
				proclamation.setContent(rs.getString("content"));
				proclamation.setFileName(rs.getString("fileName"));
				proclamation.setReadUserId(rs.getString("readUserId"));
				proclamation.setProperty(rs.getString("property"));
				proclamation.setCtype(rs.getString("ctype"));
				
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return proclamation;
	}
	
	
	/**
	 * 根据流程key得到流程定义ID
	 * @param key
	 * @return
	 */
	public String getPdidByKey(String key){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String processDefinitionId = "";
		
		String sql = " select processDefinitionId from j_processdeploy where processKey=? ";
		sql="select pdid from mt_jbpm_processdeploy where pkey=? ";
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, key);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				processDefinitionId += rs.getString(1);
				
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return processDefinitionId;
	}
	
	/**
	 * 根据sql 得到值
	 * @param sql
	 * @return
	 */
	public String getValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				value += rs.getString(1) +"@`@";
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	
	/**
	 * 公告流程记录表
	 * @param proclamationFlow
	 * @return
	 */
	public boolean addProclamationProcss(ProclamationFlow proclamationFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_proclamationprocss (ProcessInstanceId,proclamationId,Applyuser,ApplyDate,State,Property) "
				+ "value (?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, proclamationFlow.getProcessInstanceId());
			ps.setString(i++, proclamationFlow.getProclamationId());
			ps.setString(i++, proclamationFlow.getApplyuser());
			ps.setString(i++, proclamationFlow.getApplyDate());
			ps.setString(i++, proclamationFlow.getState());
			ps.setString(i++, proclamationFlow.getProperty());

			result = ps.execute();

			result = true;
		} catch (SQLException e) {

			System.out.println("添加 合同流程失败service:" + e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/**
	 * 修改莫列的值
	 * @param sql
	 * @return
	 */
	public boolean UpdateValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.executeUpdate();
			
			result = true;
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return result;
	}
	
}
