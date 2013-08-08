package com.matech.audit.service.userpopedom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.user.UserService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

public class UserPopedomService {
	
	ASFuntion CHF = new ASFuntion();
	
	private String property = "user"; 
	
	
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	Connection conn = null ;
	

	public UserPopedomService(Connection conn) {
		this.conn = conn ;
	}
	
	
	/**
	 * 通过用户的loginid，得到部门授权
	 */
	public String getLoginIdDepartment(String loginid,String menuid,String property) throws Exception{
		try {
			//通过loginid得到人员ID
			String userid = new UserService(conn).getUser(loginid,"loginid").getId();
			return getDepartment(userid, menuid, property);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
		}
	}
	/**
	 * 值查询：部门授权
	 */
	public String getDepartment(String oid,String menuid,String property) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String departmentid = "";
			String sql = "select * from k_userpopedom where userid = ? and menuid = ? and property = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, oid);
			ps.setString(2, menuid);
			ps.setString(3, property);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid = CHF.showNull(rs.getString("departmentid"));
			}
			return departmentid;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 批量插入
	 * @param userid : 人员loginid
	 * @param menus ： 菜单ID数组
	 * @param departments ： 部门ID数组
	 * @throws Exception
	 */
	public void saveLoginIdPopedom(String loginid,String [] menus,String []departments) throws Exception {
		try {
			//通过loginid得到人员ID
			String userid = new UserService(conn).getUser(loginid,"loginid").getId();
			
			saveUserIdPopedom( userid, menus,departments);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
		}
	}
	
	/**
	 * 批量插入
	 * @param userid : 人员ID
	 * @param menus ： 菜单ID数组
	 * @param departments ： 部门ID数组
	 * @throws Exception
	 */
	public void saveUserIdPopedom(String userid,String [] menus,String []departments) throws Exception {
		try {
			for (int i = 0; i < menus.length; i++) {
				String menuid = CHF.showNull(menus[i]);
				String departmentid = CHF.showNull(departments[i]);
				System.out.println(menus.length+"|"+menuid+"="+departmentid);
				if(!"".equals(departmentid)){
					saveUserIdPopedom( userid, menuid, departmentid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 保存人员与部门的权限 
	 * @param userid : 人员ID
	 * @param menuid ： 菜单ID
	 * @param departmentid : 部门ID,多部门时以","分隔
	 * @throws Exception
	 */
	public void saveUserIdPopedom(String userid,String menuid,String departmentid) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "";
			//修改property为空的，默认为user
			sql = "update k_userpopedom set property = 'user' where ifnull(property,'') = ''";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//1、删除原来 (人员=userid and 菜单ID=menuid) 的记录
			sql = "delete from k_userpopedom where userid = ? and menuid = ? and property = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			ps.setString(2, menuid);
			ps.setString(3, this.property);
			ps.execute();
			DbUtil.close(ps);
			
			//新增
			if(!"-1".equals(departmentid)){
				sql = "insert into k_userpopedom (userid,property, menuid, departmentid) values(?,?,?,?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, userid);
				ps.setString(2, this.property);
				ps.setString(3, menuid);
				ps.setString(4, departmentid);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	

	/*
	 * 用userid,menuid查找是否有配过菜单
	 */
	public int countPopedom(String userid,String menuid){
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i=0;
		try {
			String sql="select count(autoid) from k_userpopedom where userid='"+userid+"' and menuid like concat('%',"+menuid+",'%')";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				i=rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return i;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------
	// TODO:以上是部门授权的修改，保存，以下是字段授权的修改、保存
	//-------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * 保存字段授权的【可读、编辑】
	 * @param roleid 角色ID
	 * @param menus 菜单ID 
	 * @param reading 可读：【|字段1|字段2】
	 * @param editing 可编辑：【|字段1|字段2】
	 * @throws Exception
	 */
	public void saveFieldPopedom(String roleid,String [] menus,String []reading,String []editing) throws Exception{
		try {
			for (int i = 0; i < menus.length; i++) {
				String menuid = CHF.showNull(menus[i]);
				if(!"".equals(menuid)){
					saveFieldPopedom(roleid, menuid, reading[i], editing[i]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void saveFieldPopedom(String roleid,String menuid,String reading,String editing) throws Exception{
		PreparedStatement ps = null;
		try {
			reading = "-1" + CHF.replaceStr(reading, "|", ",");
			editing = "-1" + CHF.replaceStr(editing, "|", ",");
			
			if("-1".equals(editing) && "-1".equals(reading)) return;
			
			String sql = "";
			//删除已有菜单的字段授权
			sql = "delete from k_Fieldpopedom where roleid = ? and menuid = ? and property = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, roleid);
			ps.setString(2, menuid);
			ps.setString(3, this.property);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "insert into k_Fieldpopedom(menuid,roleid,Fieldid,property,reading,editing) " +
			"	select menuid,? as roleid,Fieldid,? as  property,group_concat(reading) as reading,group_concat(editing) as editing " +
			"	from (" +
			"		select menuid,autoid as Fieldid,'是' as reading,null as editing from k_Field where menuid = ? and autoid in ("+reading+") " +
			"		union " +
			"		select menuid,autoid as Fieldid,null as reading,'是' as editing  from k_Field where menuid = ? and autoid in ("+editing+") " +
			"	) a group by Fieldid ";
			int ii = 1; 
			ps = conn.prepareStatement(sql);
			ps.setString(ii++, roleid);
			ps.setString(ii++, this.property);
			ps.setString(ii++, menuid);
			ps.setString(ii++, menuid);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			DbUtil.close(ps);
		}
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------
	// TODO:以上是字段授权的修改、保存；   以下是部门授权的应用
	//-------------------------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * 通过人员loginid和菜单ID，得到部门的权限
	 * @param loginid 人员loginid
	 * @param menuid 菜单ID
	 * @return 部门的权限
	 * @throws Exception
	 */
	public String getLoginIdPopedom(String loginid,String menuid) throws Exception {
		try {
			
			//通过loginid得到人员ID
			String userid = new UserService(conn).getUser(loginid,"loginid").getId();
			
			return getUserIdPopedom( userid, menuid);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
		}
	}
	
	
	
	/**
	 * 通过人员ID和菜单ID，得到部门的权限,(包含了空部门)--去掉了缺省返回自己所在部门的函数
	 * @param userid 人员ID；通过人员ID得到人员 【所属部门、所属角色、所属机构：一级的部门】 
	 * @param menuid 菜单ID
	 * @return 部门的权限
	 * @throws Exception
	 */
	public String getUserIdPopedom(String userid,String menuid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try { 
			String departmentid = "",departmentid1 = "", departmentid2 = "" ,departmentid3 = "",roles = "";
			int ii = 0;
			String sql = "";
			
			if("19".equals(userid)){
				//admin 看到所有部门
				sql = "select group_concat(distinct autoid) as departmentid from k_department";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					departmentid = CHF.showNull(rs.getString("departmentid"));
				}
				return departmentid;
			}
			
			//全所
			sql = "select group_concat( autoid) as departmentid from k_department ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid3 = CHF.showNull(rs.getString("departmentid"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属角色
			sql = "select group_concat(distinct rid) as roles from k_userrole where userid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			rs = ps.executeQuery();
			if(rs.next()){
				roles = CHF.showNull(rs.getString("roles"));
			}
			if("".equals(roles)) roles = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属部门
			sql = "select * from k_user where id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid1 = CHF.showNull(rs.getString("departmentid"));
			}
			if("".equals(departmentid1)) departmentid1 = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属机构对应的所有下级部门
			sql = "select group_concat(distinct b.autoid) as departmentid " +
			"	from k_department a,k_department b  " +
			"	where a.autoid = ? " +
			"	and a.areaid = b.areaid " ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, departmentid1);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid2 = CHF.showNull(rs.getString("departmentid"));
			}
			if("".equals(departmentid2)) departmentid2 = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//人员的部门授权
			sql = "select * from k_userpopedom where userid = ? and menuid = ? and property = 'user' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			ps.setString(2, menuid);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid = CHF.showNull(rs.getString("departmentid"));
				ii ++;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//角色的部门授权
			sql = "select * from k_userpopedom where userid in ("+roles+") and menuid = ? and property = 'role' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, menuid);
			rs = ps.executeQuery();
			while(rs.next()){
				departmentid += rs.getString("departmentid");
				ii ++;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			departmentid = CHF.replaceStr(departmentid, "${alldeparts}", departmentid3); 
			departmentid = CHF.replaceStr(departmentid, "${department}", departmentid1); //修改所属部门
			departmentid = CHF.replaceStr(departmentid, "${office}", departmentid2);//修改所属机构
			departmentid += "'',-1";
			
			//增加人员对应的本部门、类同部门、授权部门
			sql = "select group_concat(distinct autoid) as autoid from ( " +
			"		SELECT a.autoid " +
			"		FROM k_department a,k_user b " +  
			"		WHERE b.id=? AND (b.ProjectPopedom LIKE CONCAT('%.',a.autoid,'.%')) " + 
			"		UNION " +
			"		SELECT c.autoid " +
			"		FROM k_department a,k_user b ,k_department c " +
			"		WHERE b.id=? AND a.autoid=b.departmentid " +
			"		AND concat(',',a.ProjectPopedom,',') LIKE CONCAT('%,',c.autoid,',%')  " +
			"	) a";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			ps.setString(2, userid);
			rs = ps.executeQuery();
			if(rs.next()){
				String autoid =  CHF.showNull(rs.getString("autoid"));
				if(!"".equals(autoid)){
					departmentid += "," + autoid;
				}
			}
			
			return departmentid;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 通过人员ID和菜单ID，得到部门的权限
	 * 仅获得菜单勾选部门的权限
	 * @param userid 人员ID
	 * @param menuid 菜单ID
	 * @return 部门的权限
	 * @throws Exception
	 */
	public String getUserPopedom(String userid,String menuid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String departmentid = "",departmentid1 = "", departmentid2 = "",departmentid3="" ,roles = "";
			String sql = "";
			
			if("19".equals(userid)){
				//admin 看到所有部门
				sql = "select group_concat(distinct autoid) as departmentid from k_department";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					departmentid = CHF.showNull(rs.getString("departmentid"));
				}
				return departmentid;
			}
			
			//全所
			sql = "select group_concat( autoid) as departmentid from k_department ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid3 = CHF.showNull(rs.getString("departmentid"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属角色
			sql = "select group_concat(distinct rid) as roles from k_userrole where userid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			rs = ps.executeQuery();
			if(rs.next()){
				roles = CHF.showNull(rs.getString("roles"));
			}
			if("".equals(roles)) roles = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属部门
			sql = "select * from k_user where id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid1 = CHF.showNull(rs.getString("departmentid"));
			}
			if("".equals(departmentid1)) departmentid1 = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//所属机构对应的所有下级部门
			sql = "select group_concat(distinct b.autoid) as departmentid " +
			"	from k_department a,k_department b  " +
			"	where a.autoid = ? " +
			"	and a.areaid = b.areaid " ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, departmentid1);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid2 = CHF.showNull(rs.getString("departmentid"));
			}
			if("".equals(departmentid2)) departmentid2 = "-1";
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//人员的部门授权
			sql = "select * from k_userpopedom where userid = ? and menuid = ? and property = 'user' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			ps.setString(2, menuid);
			rs = ps.executeQuery();
			if(rs.next()){
				departmentid = CHF.showNull(rs.getString("departmentid"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//角色的部门授权
			sql = "select * from k_userpopedom where userid in ("+roles+") and menuid = ? and property = 'role' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, menuid);
			rs = ps.executeQuery();
			while(rs.next()){
				departmentid += rs.getString("departmentid");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			departmentid = CHF.replaceStr(departmentid, "${alldeparts}", departmentid3); 
			departmentid = CHF.replaceStr(departmentid, "${department}", departmentid1); //修改所属部门
			departmentid = CHF.replaceStr(departmentid, "${office}", departmentid2);//修改所属机构
			
			departmentid = UTILString.killEndToken(departmentid,",") ;
			if("".equals(departmentid)) {
				departmentid = "-1" ;
			}
			
			return departmentid;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
}
