package com.matech.audit.service.group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.group.model.Group;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class GroupService {
	
	Connection conn=null;
	public GroupService(Connection conn){
		this.conn=conn;
	}	
	
	//修改k_group表的Level、Fullpath
	public void updateFullpath() throws Exception{
		PreparedStatement ps=null;
		String sql = "";
		try {
			//清理fullpath
			sql = "UPDATE  k_group SET fullpath = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//修改一级集团路径
			sql = "UPDATE  k_group SET fullpath = CONCAT(groupid,'|'),level=1 WHERE parentId = 0";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//修改集团层次、路径
			int ii = 0;
			while(true){
				sql = "UPDATE k_group a,k_group b SET a.level=b.level+1,a.fullpath = CONCAT(b.fullpath,a.groupid,'|') WHERE a.parentId=b.groupid AND a.parentId<>0";
				ps = conn.prepareStatement(sql);
				int iResult = ps.executeUpdate();
				DbUtil.close(ps);
				if(iResult == 0 || ii == 100){
					System.out.println(iResult + "|" + ii);
					break;
				}
				ii++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	//修改k_group表的Departments，用于保存集团下级的部门ID
	public void updateDepartments() throws Exception{
		PreparedStatement ps=null;
		String sql = "";
		try {
			sql = "UPDATE k_group a " +
			"	INNER JOIN ( " +
			"		SELECT b.groupid,GROUP_CONCAT(DISTINCT a.departmentid ORDER BY a.departmentid) AS departments " +
			"		FROM k_group a,k_group b " +
			"		WHERE  1=1 " +
			"		AND a.groupid <> b.groupid " +
			"		AND a.fullpath LIKE CONCAT(b.fullpath,'%') " +
			"		GROUP BY b.groupid	 " +
			"	) b ON a.groupId =b.groupId " +
			"	SET a.departments = b.departments";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	/**
	 * 添加集团的方法
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public boolean addGroup(Group group) throws Exception{
		System.out.println("srvice  中进入了添加的方法");
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			int i=1;
			String sql="insert into k_group (groupName,parentId,level,property,departmentid) values(?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getGroupName());
			ps.setString(i++, group.getParentId());
			ps.setString(i++, "1");
//			ps.setString(i++, group.getFullpath());
			ps.setString(i++, group.getProperty());
			ps.setString(i++, group.getDepartmentid()); 
			ps.execute();

			//修改k_group表的Level、Fullpath
			updateFullpath();
			
			//修改一级集团的部门
			i=1;
			sql = "UPDATE k_group a,k_group b SET b.departmentid = a.departmentid WHERE a.parentid = ? AND a.departmentid <> ''AND b.parentid = 0 AND a.fullpath LIKE CONCAT(b.fullpath,'%') ";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getParentId());
			ps.execute();
			DbUtil.close(ps);
			
			i=1;
			//修改下级集团节点的部门
			sql = "UPDATE k_group a,(	SELECT b.* FROM k_group a,k_group b 	WHERE a.groupId = ? 	AND b.parentid = 0 AND a.fullpath LIKE CONCAT(b.fullpath,'%') ) b SET a.departmentid = b.departmentid WHERE CONCAT('|',a.fullpath) LIKE CONCAT('%|',b.fullpath,'%') AND a.groupId <> b.groupId";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getParentId());
			ps.execute();
			
			//修改k_group表的Departments，用于保存集团下级的部门ID
//			updateDepartments();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
		return false;
	}
	
	/**
	 * 修改集团的方法
	 * @param group
	 * @return
	 * @throws Exception 
	 */
	public boolean updateGroup(Group group) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			int i=1;
			String sql="update k_group set groupName=?,parentId=?,property=?,departmentid=? where groupId=?";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getGroupName());
			ps.setString(i++, group.getParentId());
//			ps.setString(i++, group.getLevel());
//			ps.setString(i++, group.getFullpath());
			ps.setString(i++, group.getProperty());
			ps.setString(i++, group.getDepartmentid());
			ps.setString(i++, group.getGroupId());
			ps.execute();
			DbUtil.close(ps);
			
			i=1;
			//修改一级集团的部门
			sql = "UPDATE k_group a,k_group b SET b.departmentid = a.departmentid WHERE a.groupId = ? AND b.parentid = 0 AND a.fullpath LIKE CONCAT(b.fullpath,'%')";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getGroupId());
			ps.execute();
			DbUtil.close(ps);
			
			i=1;
			//修改下级集团节点的部门
			sql = "UPDATE k_group a,(	SELECT b.* FROM k_group a,k_group b 	WHERE a.groupId = ? 	AND b.parentid = 0 AND a.fullpath LIKE CONCAT(b.fullpath,'%') ) b SET a.departmentid = b.departmentid WHERE CONCAT('|',a.fullpath) LIKE CONCAT('%|',b.fullpath,'%') AND a.groupId <> b.groupId";
			ps=conn.prepareStatement(sql);
			ps.setString(i++, group.getGroupId());
			ps.execute();
			
//			if("0".equals(group.getParentId())){
//				//修改一级集团时，同时修改下级集团的所属部门
//				i=1;
//				sql = "UPDATE k_group a,k_group b SET a.departmentid = b.departmentid WHERE b.groupId = ? AND CONCAT('|',a.fullpath) LIKE CONCAT('%|',b.fullpath,'%') AND a.groupId <> b.groupId";
//				ps=conn.prepareStatement(sql);
//				ps.setString(i++, group.getGroupId());
//				ps.execute();
//			}
			
			//修改k_group表的Departments，用于保存集团下级的部门ID
//			updateDepartments();
			return true;
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	/**
	 * 删除集团的方法
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteGroup(String groupId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="";
			sql = "SELECT 1 FROM k_group a,k_group b WHERE b.groupid = ? AND a.groupId <> ? AND a.fullpath LIKE CONCAT(b.fullpath,'%') LIMIT 1";
			ps=conn.prepareStatement(sql);
			ps.setString(1, groupId);
			ps.setString(2, groupId);
			rs = ps.executeQuery();
			if(rs.next()){
				//有下级集团，不能删除本级集团
				return false;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "delete from k_group where groupId = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, groupId);
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return false;
	}
	
	/**
	 * 根据编号得到集团对象的方法
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public Group getGroupById(String groupId) throws Exception{
		DbUtil.checkConn(conn);
		Group group=null;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try {
			String sql="select * from k_group where groupId = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, groupId);
			rs=ps.executeQuery();
			if(rs.next()){
				group=new Group();
				group.setGroupId(rs.getString("groupId"));
				group.setGroupName(rs.getString("groupName"));
				group.setParentId(rs.getString("parentId"));
				group.setLevel(rs.getString("level"));
				group.setFullpath(rs.getString("fullpath"));
				group.setProperty(rs.getString("property"));
				group.setDepartmentid(rs.getString("departmentid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return group;
	}
	
	public String getJSONTree(String parentId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();

		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" select a.groupId,a.groupName,a.parentId,a.fullpath ")
				.append(" from k_group a ")
				.append(" where a.parentId=? ")
				.append(" order by a.groupName ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, parentId);
			
			rs = ps.executeQuery();
			String groupId;
			String groupName;

			while (rs.next()) {
		
				groupId = rs.getString("groupId");
				groupName = rs.getString("groupName");
				
				sb.append(" { ")
					.append("cls:'folder',")
					.append("leaf:false,")
					.append("allowDrag:false,")
					.append("id:'").append(groupId).append("',")
					.append("text:'").append(groupName).append("',")
					.append("children:[ ").append(getJSONTree(groupId)).append("]")
					.append("}");
				
				
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
			String customerString = getCustomerJSONTree(parentId,"");
			if(!"".equals(customerString)) {
				if(!"".equals(sb.toString())) {
					sb.append(",");
				}
				
				sb.append(customerString);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();

	}
	
	public String getJSONTree1(String parentId,String sqlDepartmentid) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		ASFuntion CHF = new ASFuntion();
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" select distinct a.groupId,a.groupName,a.parentId,a.fullpath,a.departmentid,IF(b.groupId IS NULL,1,0) AS isleaf1 ");
			sql.append(" from k_group a LEFT JOIN k_group b ON b.parentId = a.groupId ");
			sql.append(" where a.parentId=? ");
			sql.append(sqlDepartmentid);
			sql.append(" order by a.groupName ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, parentId);
			
			rs = ps.executeQuery();
			String groupId;
			String groupName;
			String isleaf1,departmentid;
			
			while (rs.next()) {
		
				groupId = rs.getString("groupId");
				groupName = rs.getString("groupName");
				isleaf1 = rs.getString("isleaf1"); 
				departmentid = CHF.showNull(rs.getString("departmentid"));
				sb.append(" { ");
				sb.append("cls:'folder',");
//				if("1".equals(isleaf1)){
//					sb.append("leaf:true,");
//				}else{
//					sb.append("leaf:false,");
//				}
				sb.append("allowDrag:true,");
				sb.append("id:'").append(groupId).append("',");
				sb.append("groupId:'").append(groupId).append("',");
				sb.append("text:'").append(groupName).append("',");
				sb.append("property:'集团',");
				sb.append("departmentid:'").append(departmentid).append("'");
				//.append("children:[ ").append(getJSONTree(groupId)).append("]");
				sb.append("}");
				
				
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();

	}

	public String getDepartmentJSONTree(String parentId,String sqlDepartmentid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		ASFuntion CHF = new ASFuntion();
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT DISTINCT '0' AS groupId,IF(IFNULL(a.departmentid,'')='' AND IFNULL(b.departname,'') = '','公开集团',IFNULL(b.departname,CONCAT('集团部门【',a.departmentid,'】已删除'))) AS groupName,IF(IFNULL(a.departmentid,'')='','',IFNULL(a.departmentid,'')) AS departmentid,IF(IFNULL(a.departmentid,'')='','',concat('_',IFNULL(a.departmentid,''))) AS departmentid1 ");
			sql.append(" FROM k_group  a  LEFT JOIN k_department b ON a.departmentid = b.autoid ");
			sql.append(" where 1=1 ");
			sql.append(sqlDepartmentid);
			sql.append(" ORDER BY ABS(a.departmentid) ");
			
			System.out.println(sql.toString());
			ps = conn.prepareStatement(sql.toString());
			
			rs = ps.executeQuery();
			String groupId;
			String groupName;
			String isleaf1,departmentid,departmentid1;
			
			while (rs.next()) {
		
				groupId = rs.getString("groupId");
				groupName = rs.getString("groupName");
				departmentid = CHF.showNull(rs.getString("departmentid"));
				departmentid1 = CHF.showNull(rs.getString("departmentid1"));
				sb.append(" { ");
				sb.append("cls:'folder',");
//				if("1".equals(isleaf1)){
//					sb.append("leaf:true,");
//				}else{
//					sb.append("leaf:false,");
//				}
				sb.append("allowDrag:false,");
				sb.append("id:'").append(departmentid1).append("',");
				sb.append("text:'").append(groupName).append("',");
				sb.append("groupId:'").append(groupId).append("',");
				sb.append("property:'部门',");
				sb.append("departmentid:'").append(departmentid).append("'");
				//.append("children:[ ").append(getJSONTree(groupId)).append("]");
				sb.append("}");
				
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	public String getCustomerJSONTree(String groupId, String departmentId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();

		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" select departId,departName from k_customer  ");
			
			//所有没有集团的客户
			if("".equals(groupId)) {		
				sql.append(" where (groupName=? or groupName is null) ");
				
				if(departmentId != null && !"".equals(departmentId)){
					sql.append(" and departmentid = '"+departmentId+"' ");
				}
	
			} else {
				//根据集团找出客户
				sql.append(" where groupName=? ");

			}
			
			sql.append(" and property=1 ")
				.append(" order by departName ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, groupId);
			
			rs = ps.executeQuery();
			String departId;
			String departName;

			while (rs.next()) {
		
				departId = rs.getString("departId");
				departName = rs.getString("departName");
				
				sb.append(" { ")
					.append("cls:'folder',")
					.append("leaf:true,")
					.append("id:'").append(departId).append("',")
					.append("text:'").append(departName).append("'")
					.append("}");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();

	} 
	
	public void updateCustomer(String groupId, String customerId) {
		PreparedStatement ps = null;
		
		try {
			String sql = " update k_customer set groupName=? "
						+ " where departId in(" + customerId + ")";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}
	
	public String getCustomer(String groupId, String auditType) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		ASFuntion asf = new ASFuntion();
		
		try {
			String sql = " select departId,departName "
					   + " from k_customer "
					   + " where groupName=? "
					   + " order by departId ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			
			rs = ps.executeQuery();
			String customerId = "";
			String customerName = "";

			while(rs.next()) {
				
				customerId = asf.showNull(rs.getString("departId"));
				customerName = asf.showNull(rs.getString("departName"));
				
				sb.append("<tr class=\"workClass\" >")
					.append("	<td>")
					.append("		<input type=\"hidden\" name=\"groupCustomerId\" id=\"groupCustomerId_").append(customerId).append("\" value=\"").append(customerId).append("\" > ")
					.append("		<input type=\"hidden\" name=\"groupProjectId\" id=\"groupProjectId_").append(customerId).append("\" value=\"").append("").append("\"> ")
					.append("		<input type=\"hidden\" name=\"projectGroupName\" id=\"projectGroupName_").append(customerId).append("\" value=\"").append(groupId).append("\" > ")
					.append("	</td>")
					.append("	<td align=\"left\">")
					.append("		<input type=\"checkbox\" value=\"").append(customerId).append("\" customerId=\"").append(customerId).append("\" checked=true onclick=\"setJoin(this);\" name=\"isJoin\" id=\"isJoin_").append(customerId).append("\" >")
					.append(customerName)
					.append("	</td>")
					.append("	<td>")
					.append("		<input type=\"text\" name=\"groupAuditType\" id=\"groupAuditType_").append(customerId).append("\" ")
					.append("			maxlength=\"50\" class=\"required validate-digits\" value=\"").append(auditType).append("\" ")
					.append("			noinput=true title=\"底稿模板编号必填\" onkeydown=\"onKeyDownEvent();\" ")
					.append("			onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" ")
					.append("			autoid=5 refer=auditPara />")
					.append("	<td> ")
					.append("		<input type=\"text\" name=\"groupAuditManager\" id=\"groupAuditManager_").append(customerId).append("\" ")
					.append("			maxlength=\"20\" class=\"required\" ")
					.append("			title=\"项目经理必填\" onkeydown=\"onKeyDownEvent();\" ")
					.append("			onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" ")
					.append("			autoid=622 />")
					.append("	</td> ")
					.append("	<td>")
					.append("		<input type=\"text\" name=\"groupAuditPeople\" id=\"groupAuditPeople_").append(customerId).append("\" ")
					.append("			maxlength=\"20\" class=\"required\" ")
					.append("			title=\"项目组员必填\" onkeydown=\"onKeyDownEvent();\" ")
					.append("			onkeyup=\"onKeyUpEvent();\" multiselect=true onclick=\"onPopDivClick(this);\" ")
					.append("			autoid=622 />")
					.append(" </td>")
					.append("</tr>");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	public String getGroupProject(String parentId, String auditType) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {

			sb.append(getCustomer(parentId, auditType));
			
			String sql = " select groupName,groupId,level from k_group "
					   + " where parentId=? "
					   + " order by groupName ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, parentId);
			
			rs = ps.executeQuery();
			
			String groupName = "";
			int level = 0;
			
			while(rs.next()) {
				
				level = rs.getInt("level");
				groupName = rs.getString(1);
			
				sb.append("<tr class=\"workClass\" >")
					.append("	<td style=\"font-weight: bold;color: blue;\">")
					.append("		<span style=\"width:").append(level * 15).append("px;\"></span>")
					.append("		<img src=\"/AuditSystem/images/nofollow.jpg\" />")
					.append(groupName)
					.append("	</td>")
					.append("	<td colspan=\"4\">&nbsp;</td>")
					.append("</tr>")
					.append(getGroupProject(rs.getString(2), auditType));
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	//保存移动的集团
	/**
	 * 1、清除移动集团以及此下级集团的level、fullpath(不用)
	 * 2、修改移动集团对应的parentid
	 * 3、重算移动集团以及此下级集团的level、fullpath
	 */
	public void saveMoveNode(String dropNodeId,String targetId) throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			int i = 1;
			sql = "update k_group set parentid = ? where groupid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, targetId);
			ps.setString(i++, dropNodeId);
			ps.execute();
			
			updateFullpath();
//			updateDepartments();
		} catch (Exception e) {
			System.out.println("saveMoveNode sql=|"+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
}
