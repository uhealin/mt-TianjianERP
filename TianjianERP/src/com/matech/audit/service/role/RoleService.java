package com.matech.audit.service.role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.audit.service.role.model.RoleTable;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class RoleService {


	 private Connection conn=null;

	    public RoleService(Connection conn) {
	        this.conn=conn;
	    }
	
	public void add(RoleTable rt,String act)throws Exception{
		 DbUtil.checkConn(conn);
         PreparedStatement ps = null;
         try {
        	
        	 String sql = "";
        	 int i =1;
        	 if("ad".equals(act)){
        		 sql = "insert into k_role (rolename, rolevalue, property,ltype,innername)values (?,?,?,?,?)";
        		 ps = conn.prepareStatement(sql);
        		 ps.setString(i++, rt.getRolename());
        		 ps.setString(i++, rt.getRolevalue());
        		 ps.setString(i++, rt.getProperty());
        		 ps.setString(i++, rt.getLtype());    
        		 ps.setString(i++, rt.getInnername());    
        	 }else{
        		 sql = "update k_role set rolename = ?, rolevalue =?, property =?,ltype=?,innername=? where id=?";
        		 ps = conn.prepareStatement(sql);
        		 ps.setString(i++, rt.getRolename());
        		 ps.setString(i++, rt.getRolevalue());
        		 ps.setString(i++, rt.getProperty()); 
        		 ps.setString(i++, rt.getLtype());    
        		 ps.setString(i++, rt.getInnername());
        		 ps.setString(i++, rt.getId()); 
        	 }
        	 ps.execute();
         }catch (Exception e) {
             Debug.print(Debug.iError,"����ʧ��",e);
 			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
         } finally {
            // DbUtil.close(rs);
             DbUtil.close(ps);
         }
	}
	
	public RoleTable get(String id)throws Exception{
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        RoleTable rt = new RoleTable();
        try {
        	
        	String sql = "select * from k_role where id='"+id+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	if(rs.next()){
        		rt.setId(rs.getString("id"));
        		rt.setRolename(rs.getString("rolename"));
        		rt.setRolevalue(rs.getString("rolevalue"));
        		rt.setPopedom(rs.getString("popedom"));
        		rt.setProperty(rs.getString("property"));
        		rt.setLtype(rs.getString("ltype"));
        		rt.setInnername(rs.getString("innername"));
        	}
        	
        	return rt;
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
		
	}
	
	public boolean del(String id)throws Exception {
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        
        	String sql = "select * from k_userrole where rid = '"+id+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	String userId = "";
        	while(rs.next()){
        		userId+=rs.getString("userId")+",";
        	}
        	
        	if(!"".equals(userId)){
        		userId = userId.substring(0, userId.length()-1);

        		ps = conn.prepareStatement("select * from k_user where id in ("+userId+")");
        		rs = ps.executeQuery();
        		if(rs.next()){
        			 return false;
        		}
        	}
        	
        	sql = "delete from k_role where id='"+id+"'";
        	ps = conn.prepareStatement(sql);
        	ps.execute();
        	return true;
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public String getRole(String type)throws Exception {
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer("");
        try {
        	
        	String sql = "select * from k_role order by id";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	sb.append("<table  border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" >");
        	while(rs.next()){
        		sb.append("<tr height=\"20\" style=\"cursor: hand;\">");
        		sb.append("<td width=\"21\"><input type=\""+type+"\" name=\"role\" value=\""+rs.getString("id")+"\" rname=\""+rs.getString("rolename")+"\" onclick=\"onRole(this);\">");
        		sb.append("</td><td align=\"left\">");
        		sb.append(rs.getString("rolename"));
//        		if(rs.getString("popedom") !=null && !"".equals(rs.getString("popedom"))){
//        			sb.append("<font color='blue'>(已授权)</font>");
//        		}
        		sb.append("</td></tr>");
        	}
        	sb.append("</table>");
        	return sb.toString();
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public void updateRolePopedom(String stAll,String stRole)throws Exception {
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        try {
        	
        	String [] role = stRole.split("\\.");
        	String sql = "update k_role set popedom = ? where id =?";
        	ps = conn.prepareStatement(sql);
        	ps.setString(1, stAll);
        	for (int i = 0; i < role.length; i++) {
				if(role[i]!=null && !"".equals(role[i])){
					ps.setString(2, role[i]);
					ps.addBatch();
				}
			}
        	ps.executeBatch();
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
           // DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public boolean isUserRole(String rid)throws Exception{
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	
        	String sql = "select * from k_userrole where rid = '"+rid+"'";
        	ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
        	return false;
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public boolean hasRole(String userId, String roleName)throws Exception{
		 DbUtil.checkConn(conn);
       PreparedStatement ps = null;
       ResultSet rs = null;
       
       try {
       	
       	String sql = " select 1 from k_userrole a,k_role b "
       				+ " where a.rid=b.id "
       				+ " and a.userid=? "
       				+ " and b.rolename=? ";
       		ps = conn.prepareStatement(sql);
       		ps.setString(1, userId);
       		ps.setString(2, roleName);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
       	return false;
       }catch (Exception e) {
           Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
       } finally {
           DbUtil.close(rs);
           DbUtil.close(ps);
       }
	}
	
	
	public String getUserTable(String rid)throws Exception{
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer("");
        try {
        	
        	String sql = "select userid,name from k_userrole a, k_user b where b.id=userid and rid='"+rid+"' and b.state=0 order by id";
        	ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb.append(" <fieldset  style=\"width:98%\">");
			sb.append("<legend>已关联人员</legend>");
			sb.append("<table  border=\"0\" cellpadding=\"2\" cellspacing=\"1\" width=\"100%\" >");
			int i=0;
			sb.append("<tr height=\"20\" >");
			while(rs.next()){
				if(i%4==0){
					sb.append("</tr><tr height=\"20\" >");
				}
				sb.append("<td><input checked type=\"checkbox\" name=\"Saveid\" value=\""+rs.getString("userid")+"\" >"+rs.getString("name")+"</td>");
				
				i++;	
			}
			if(i==0) sb.append("<td></td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</fieldset>");
        	return sb.toString();
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public String getUserTable(String rid,String usr)throws Exception{
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer("");
        try {
        	String sql = "";
        	sb.append(getUserTable(rid));
        	
        	
        	
        	sb.append(" <fieldset  style=\"width:98%\">");
			sb.append("<legend>新增人员</legend>");
			sb.append("<table  border=\"0\" cellpadding=\"2\" cellspacing=\"1\" width=\"100%\" >");
			int i=0;
			sb.append("<tr height=\"20\" >");
			
        	String oSR = "";
        	if(!"`".equals(usr) && !"".equals(usr)){
        		oSR = "'"+usr.substring(1, usr.length()-1).replaceAll("`", "','")+"'";
        		sql = "select * from k_user a where  id in ("+oSR+") and id not in (select userid from k_userrole where rid='"+rid+"') order by id";
        		ps = conn.prepareStatement(sql);
    			rs = ps.executeQuery();
    			while(rs.next()){
    				if(i%4==0){
    					sb.append("</tr><tr height=\"20\" >");
    				}
    				sb.append("<td><input checked type=\"checkbox\" name=\"Saveid\" value=\""+rs.getString("id")+"\" >"+rs.getString("name")+"</td>");
    				
    				i++;	
    			}
        	}

			if(i==0) sb.append("<td></td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</fieldset>");
        	return sb.toString();
        }catch (Exception e) {
            Debug.print(Debug.iError,"����ʧ��",e);
			throw new MatechException("����ʧ�ܣ�"+e.getMessage(),e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
	public String saveUserRole(String rid,String sUsr) throws Exception {
		 DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        try {
        	
        	String result = "",strUser = "";
        	String [] users = sUsr.split("`");
        	String sql = "";
        	RoleTable rt = get(rid) ;
        	
        	result += "["+rt.getRolename()+"]角色变动人员[";
        	
        	UserService us = new UserService(conn);
        	User uservo = null;
        	
        	sql = "delete from k_userrole where rid='"+rid+"'";
        	ps = conn.prepareStatement(sql);
        	ps.execute();
        	
        	sql = "insert into k_userrole (userid,rid) values(?,?) ";
        	ps = conn.prepareStatement(sql);
        	ps.setString(2,rid);
        	for (int i = 0; i < users.length; i++) {
				if( users[i]!=null && !"".equals(users[i])){
					ps.setString(1, users[i]);
					ps.addBatch();
					
					uservo = us.getUser(users[i],"loginid");
					strUser += "," + uservo.getLoginid()+":"+uservo.getName() ;
				}				
			}
        	ps.executeBatch();
        	
        	if("".equals(strUser)){
        		result = "清除角色的人员关系";
        	}else{
        		strUser = strUser.substring(1);
        	}
        	
        	result += strUser + "]";
        	
        	return result;
        }catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        } finally {
           // DbUtil.close(rs);
            DbUtil.close(ps);
        }
	}
	
}



;