package com.matech.audit.work.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;
import org.jbpm.api.task.AssignmentHandler;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class Assignment implements AssignmentHandler {
	private final static Log log = LogFactory.getLog(Assignment.class);
	private static final long serialVersionUID = 362167972927720287L;
	private String department = "" ;
	private String role = "" ;
	private String user = "" ;

	public String getDepartment() { 
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void assign(Assignable assignable, OpenExecution openExecution) {
		
		String creator = (String)openExecution.getVariable("applyUser");
		//String[] assignUser = getUser(creator) ;
		//for(String user:assignUser) {
		//	assignable.addCandidateUser(user) ;
		//}
	}
	
	
	public List<Map<String,String>> getUser(String creator) {
		
		Connection conn = null ;
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		String creatorDepart = "" ;
		String creatorRole = "" ;
		List<Map<String,String>> userList = new ArrayList<Map<String,String>> ();
		try {
			conn = new DBConnect().getConnect() ;
			String sql = "";
			if(department.indexOf("$1") > -1) {
				//找出发起人所在部门
				sql = " SELECT departmentid FROM mt_com_user WHERE USER_ID='"+creator+"'" ;
				sql = " SELECT departmentid FROM k_user WHERE id="+creator ;
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				if(rs.next()) {
					creatorDepart = rs.getString(1) ;
				}
				department = department.replaceAll("\\$1", creatorDepart);		
			}
			if(role.indexOf("$1") > -1) {
				//找出发起人所在角色 k_userrole
				sql = " SELECT roleId FROM mt_com_roleuser WHERE userid = '"+creator+"'" ;
				sql = " SELECT rid FROM k_userrole WHERE userid = "+creator+"" ;
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				while(rs.next()) {
					creatorRole += rs.getString(1) + "," ;
				}
				if(!"".equals(creatorRole)) {
					creatorRole = creatorRole.substring(0,creatorRole.length() - 1) ;
				}
				role = role.replaceAll("\\$1", creatorRole); 		
			}
			if(user.indexOf("$1") > -1) {
				user = user.replaceAll("\\$1",creator);		
			}
			
			sql = "SELECT USER_ID,user_name FROM mt_com_user " 
				+ "WHERE 1=1 " ;
			sql="SELECT ID,name FROM k_user where 1=1 ";
			if(!"".equals(department)) {
				department = "'" + department.replaceAll(",", "','") + "'";
				sql += " and departmentid in("+department+")" ;
				//sql += " and departId in("+department+")" ;
			}
			if(!"".equals(role)) {
				role = "'" + role.replaceAll(",", "','") + "'";
				//sql += " and USER_ID in(SELECT userid FROM mt_com_roleuser WHERE roleid IN ("+role+"))" ;
				sql += " and id in(SELECT userid FROM k_userrole WHERE rid IN ("+role+"))" ;

			}
			if(!"".equals(user)) {
				user = "'" + user.replaceAll(",", "','") + "'";
				//sql   += " and USER_ID in ("+user+") " ;
				sql   += " and id in ("+user+") " ;
			}
			System.err.println("getUserSql:"+sql);
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			while(rs.next()) {
				Map<String,String> userMap = new HashMap<String,String>() ;
				
				String userId = StringUtil.showNull(rs.getString(1)) ;
				String userName = StringUtil.showNull(rs.getString(2)) ;
				userMap.put("userId",userId) ;
    			userMap.put("userName",userName) ;
				userList.add(userMap) ;
			//	users += tempUser + "," ;
			}
			
			
		}catch (Exception e) {
			log.error(Assignment.class,e);
            e.printStackTrace();
        } finally {
            DbUtil.close(conn);
        }
        return userList ;
	}

}
