package com.matech.audit.service.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;
import org.jbpm.api.task.AssignmentHandler;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public abstract class BaseAssignment implements AssignmentHandler {
	
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

	public abstract void assign(Assignable assignable, OpenExecution openExecution) ;
	
	public String[] getUser(String creator) {
		
		Connection conn = null ;
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		String creatorDepart = "" ;
		String creatorRole = "" ;
		ASFuntion CHF = new ASFuntion() ;
		try {
			conn = new DBConnect().getConnect("") ;
			String sql = "";
			if(department.indexOf("$1") > -1) {
				//找出发起人所在部门
				sql = " SELECT departmentid FROM k_user WHERE id='"+creator+"'" ;
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				if(rs.next()) {
					creatorDepart = rs.getString(1) ;
				}
				department = department.replaceAll("\\$1", creatorDepart);		
			}
			if(role.indexOf("$1") > -1) {
				//找出发起人所在角色
				sql = " SELECT GROUP_CONCAT(rid) FROM k_userrole WHERE userid = '"+creator+"'" ;
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				if(rs.next()) {
					creatorRole = rs.getString(1) ;
				}
				role = role.replaceAll("\\$1", creatorRole);		
			}
			if(user.indexOf("$1") > -1) {
				user = user.replaceAll("\\$1",creator);		
			}
			
			sql = "SELECT group_concat(id) FROM k_user " 
				+ "WHERE 1=1 " ;
			
			if(!"".equals(department)) {
				sql += " and departmentid in("+department+")" ;
			}
			if(!"".equals(role)) {
				sql += " and id in(SELECT userid FROM k_userrole WHERE rid IN ("+role+"))" ;
			}
			if(!"".equals(user)) {
				sql   += " and id in ("+user+") " ;
			}
			
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				String users = CHF.showNull(rs.getString(1)) ;
				if(!"".equals(users)) {
					return users.split(",") ;
				}else {
					return new String[]{"19"} ;
				}
			}
		}catch (Exception e) {
            Debug.print(Debug.iError, "访问失败", e);
            e.printStackTrace();
        } finally {
            DbUtil.close(conn);
        }
        return null ;
	}

}
