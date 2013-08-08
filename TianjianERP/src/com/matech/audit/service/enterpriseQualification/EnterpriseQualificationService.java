package com.matech.audit.service.enterpriseQualification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.enterpriseQualification.model.EnterpriseQualification;
import com.matech.audit.service.role.model.RoleTable;
import com.matech.framework.pub.db.DbUtil;

public class EnterpriseQualificationService {
	private Connection conn = null;

	public EnterpriseQualificationService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public EnterpriseQualification getEnterpriseQualification(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,title,uploadUserId,maintainUser,uploadTime,attachFileId,property " 
					   + " from k_enterpriseQualification where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			EnterpriseQualification  eq = new EnterpriseQualification();;
			
			if(rs.next()){
				eq.setUuid(id);
				eq.setTitle(rs.getString("title"));
				eq.setUploadUserId(rs.getString("uploadUserId"));
				eq.setUploadTime(rs.getString("uploadTime"));
				eq.setMaintainUser(rs.getString("maintainUser"));
				eq.setAttachFileId(rs.getString("attachFileId"));
				eq.setProperty(rs.getString("property"));
			}
			return eq;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	
	

	/**
	 * 新增 
	 * @param eq
	 * @return
	 * @throws Exception
	 */
	public void addEnterpriseQualification(EnterpriseQualification eq) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_enterpriseQualification (uuid,title,uploadUserId,maintainUser,uploadTime,attachFileId,property ) "
				       + " values(?,?,?,?,?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, eq.getUuid());
			ps.setString(i++, eq.getTitle());
			ps.setString(i++, eq.getUploadUserId());
			ps.setString(i++, eq.getMaintainUser());
			ps.setString(i++, eq.getUploadTime());
			ps.setString(i++, eq.getAttachFileId());
			
			ps.setString(i++, eq.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	

	/**
	 * 修改 
	 * @param eq
	 * @return
	 * @throws Exception
	 */
	public void updateEnterpriseQualification(EnterpriseQualification eq) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_enterpriseQualification set title=?,uploadUserId=?,maintainUser=?,uploadTime=?,attachFileId=?,property=? "
				       + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, eq.getTitle());
			ps.setString(i++, eq.getUploadUserId());
			ps.setString(i++, eq.getMaintainUser());
			ps.setString(i++, eq.getUploadTime());
			ps.setString(i++, eq.getAttachFileId());
			ps.setString(i++, eq.getProperty());
			
			ps.setString(i++, eq.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 删除
	 * @param id
	 * @throws Exception
	 */
	public void delete(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_enterpriseQualification where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, id);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	
	public List getRoleList() throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = " select id,roleName,roleValue from k_role where rolename !='' order by id ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			RoleTable role = null;
			while(rs.next()) {
				role = new RoleTable();
				
				role.setId(rs.getString(1));
				role.setRolename(rs.getString(2));
				role.setRolevalue(rs.getString(3));
				
				list.add(role);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	
	public List getListBySql(String sql) {

		PreparedStatement ps = null ;
		ResultSet rs = null ;
		List list = new ArrayList();
		try {
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i<=RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnName(i).toLowerCase(),rs.getString(RSMD.getColumnName(i).toLowerCase()));
				}
				list.add(map);
			}
			return list ;
		}catch(Exception e) {
			e.printStackTrace() ;
		}finally{
			DbUtil.close(rs) ;
			DbUtil.close(ps) ;
			
		}
		return null ;
	}
	
	//人员树
	public List getUser(String parentid,String checked,String joinUser)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = null;
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			//人员树
			list = new ArrayList();
			sql = "select a.* from k_user a  where a.departmentid = ? and state=0  AND a.name NOT LIKE '%(离)%' AND a.pccpa_seqno IS NOT NULL  ORDER BY pccpa_seqno  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, parentid);
			rs = ps.executeQuery();
			String userid,loginid,username;
			joinUser = joinUser+",";
			while(rs.next()){
				userid = rs.getString("id");
				loginid = rs.getString("loginid");
				username = rs.getString("name");
				
				Map map = new HashMap();
				map.put("isSubject","4");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",true);	
				map.put("id",userid);
				map.put("userid",userid);
				map.put("loginid",loginid);
				map.put("username",username);
				
				if(joinUser.indexOf(","+userid+",")>-1){
					checked = "true";
				}
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
					
				}
				if("true".equals(checked)){
					checked = "false";
				}
				
				map.put("text",username);
				
				list.add(map);
				ii++;
			}
			
			
			if(ii == 0) return null;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
}
