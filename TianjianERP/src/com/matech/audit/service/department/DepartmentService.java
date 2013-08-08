package com.matech.audit.service.department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.service.userdef.Userdef;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class DepartmentService {

	private Connection conn = null;

	private boolean userbool = false; //部门树是否显示【所在机构】和【所在部门】 
	public boolean isUserbool() {
		return userbool;
	}

	public void setUserbool(boolean userbool) {
		this.userbool = userbool;
	}

	private String userpopedom = ""; //userpopedom 菜单的部门权限
	private String addUser = "";	
	public String getAddUser() {
		return addUser;
	}

	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}

	public String getUserpopedom() {
		return userpopedom;
	}

	public void setUserpopedom(String userpopedom) {
		this.userpopedom = userpopedom;
	}

	public DepartmentService(Connection conn) {
		this.conn = conn;
	}

	public String getLtype(String autoId) throws MatechException {
		DbUtil.checkConn(conn);
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String strSql = "select ltype from k_department where autoid=?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace() ;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	/**
	 * 获取部门的类同部门
	 */
	public String getProjectPopedom(String autoId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String projectPopedom ="";
		try{
			String sql = "select ProjectPopedom from k_department where autoid = "+autoId;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				projectPopedom = rs.getString(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return projectPopedom;
	}
	
	/**
	 * 判断部门是否还有人员
	 * 
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	/*
	 * public String isNullDepartment(String autoId) throws Exception {
	 * PreparedStatement ps = null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * DbUtil dbUtil = new DbUtil(this.conn); String strSql = "select count(1)
	 * from k_user where departmentid =? ";
	 * 
	 * Object[] objects = new Object[]{autoId};
	 * 
	 * int userCount = 0; userCount = dbUtil.queryForInt(strSql, objects);
	 * if(userCount > 0){ return "该部门下还有" + userCount + "个人员,该部门不允许删除!!"; } }
	 * catch (Exception e) { e.printStackTrace(); } finally { DbUtil.close(rs);
	 * DbUtil.close(ps); }
	 * 
	 * return "ok"; }
	 */

	public String isUpDepartment(String autoId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			DbUtil dbUtil = new DbUtil(this.conn);
			String strSql = "select count(*) from k_department where parentid = ?";
			Object[] objects = new Object[] { autoId };

			int department = 0;
			department = dbUtil.queryForInt(strSql, objects);
			if (department > 0) {
				return "该部门还有" + department + "个下级部门,不允许删除!!";

			}
			
			strSql=" select count(*) from k_user where departmentid =? ";
			department = dbUtil.queryForInt(strSql, objects);
			if (department > 0) {
				return ("该部门下面还有人员，不允许删除该部门！！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return "ko";

	}

	public void remove(String id, int autoid) throws Exception {

		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String strSql = "delete from k_department WHERE autoid = ?";
			String strSql1 = "delete from k_userdef WHERE contrastID = ?";

			ps = conn
					.prepareStatement(" select * from k_user where departmentid =? ");
			ps.setString(1, String.valueOf(autoid));

			if (ps.executeQuery().next()) {
				throw new Exception("该部门下面还有人员，不允许删除该部门！！");
			}

			ps = conn.prepareStatement(strSql);
			ps.setInt(1, autoid);
			ps.execute();
			//ps.execute("Flush tables");
			ps = conn.prepareStatement(strSql1);
			ps.setInt(1, autoid);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "����ʧ��", e);
			throw new Exception(e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * ��ӻ����޸�
	 * 
	 * @param tt
	 *            RowSetDynaClass
	 * @throws MatechException
	 */
	public void AddOrUpdate(DepartmentVO departmentVO, Userdef[] userdefs,
			String act, String property) throws MatechException {
		DbUtil.checkConn(conn);

		String strSql;
		UserdefService userDefService = new UserdefService(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		try {
			if (act.equals("add")) {
				// ����
				strSql = "INSERT INTO k_department(departname, parentid, Typeid,url,postalcode,address) VALUES(?, ?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(strSql);

				ps.setString(1, departmentVO.getDepartmentName());
				ps.setInt(2, departmentVO.getParentId());
				ps.setString(3, departmentVO.getTypeid());
				ps.setString(4, departmentVO.getUrl());
				ps.setString(5, departmentVO.getPostalcode());
				ps.setString(6, departmentVO.getAddress());

				ps.execute();
				//ps.execute("Flush tables");

				String contrastID = this.getDepartId(departmentVO
						.getDepartmentName());
				userDefService.addOrupdateUserdef(userdefs, contrastID,
						property);

			} else {
				//
				System.out.println(departmentVO.getDepartmentName());
				System.out.println(departmentVO.getParentId());
				System.out.println(departmentVO.getProperty());
				System.out.println(departmentVO.getAutoId());
				System.out.println(userdefs);
				strSql = "UPDATE k_department SET departname=?,parentid=?, Typeid=?,url=?,postalcode=?,address=? WHERE autoid=?";
				ps = conn.prepareStatement(strSql);

				ps.setString(1, departmentVO.getDepartmentName());
				ps.setInt(2, departmentVO.getParentId());
				ps.setString(3, departmentVO.getTypeid());
				ps.setString(4, departmentVO.getUrl());
				ps.setString(5, departmentVO.getPostalcode());
				ps.setString(6, departmentVO.getAddress());
				ps.setInt(7, departmentVO.getAutoId());

				ps.execute();
				//ps.execute("Flush tables");
				System.out.println(departmentVO.getAutoId() + "bbbbbbbbb");
				userDefService.addOrupdateUserdef(userdefs, departmentVO
						.getAutoId()
						+ "", property);
			}
			
			updateDepartmentPath();
		} catch (Exception e) {
			Debug.print(Debug.iError, "����ʧ��", e);
			throw new MatechException("����ʧ�ܣ�" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	// ȡ���������ϸ��Ϣ
	public DepartmentVO getVo(int autoid) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		DepartmentVO departmentVO = new DepartmentVO();
		ASFuntion CHF = new ASFuntion();
		try {
			String strSql = "select autoid, departname, parentid, typeid,url,level0,fullpath,postalcode,address,ltype from k_department WHERE autoid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setInt(1, autoid);
			rs = ps.executeQuery();

			if (rs.next()) {

				departmentVO.setAutoId(rs.getInt(1));
				departmentVO.setDepartmentName(CHF.showNull(rs.getString(2)));
				departmentVO.setParentId(rs.getInt(3));
				departmentVO.setTypeid(CHF.showNull(rs.getString(4)));
				departmentVO.setUrl(CHF.showNull(rs.getString(5)));
				departmentVO.setLevel0(rs.getString(6)) ;
				departmentVO.setFullpath(rs.getString(7)) ;
				departmentVO.setPostalcode(CHF.showNull(rs.getString(8)));
				departmentVO.setAddress(CHF.showNull(rs.getString(9)));
				departmentVO.setLtype(rs.getString(10));				
			}

			return departmentVO;

		} catch (Exception e) {
			Debug.print(Debug.iError, "����ʧ��", e);
			throw new MatechException("����ʧ�ܣ�" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String getDepartId(String departmentName) throws MatechException {
		DbUtil.checkConn(conn);
		String strSql;
		PreparedStatement ps = null;
		ResultSet rs = null;
		strSql = "select autoId from k_department where departName=?";

		try {
			ps = conn.prepareStatement(strSql);
			ps.setString(1, departmentName);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) + "";
			}

		} catch (SQLException e) {
			// TODO �Զ���� catch ��
			Debug.print(Debug.iError, "����ʧ��", e);
			throw new MatechException("����ʧ�ܣ�" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 判断部门编号是否唯一
	 * 
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public boolean checkExists(String departName) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String strSql = "select 1 from k_department where departname = '"
					+ departName + "'";

			ps = conn.prepareStatement(strSql);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return false;

	}
	
	/**
	 * 获取部门人员列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List getDepartmentList(String userDepartMentId) throws Exception {
		List departmentList = new ArrayList();
		
		//添加各个部门
		getDepartmentList(userDepartMentId, departmentList);
		
		//无部门人员
		DepartmentVO department = new DepartmentVO();
		department.setAutoId(0);
		department.setDepartmentName("无部门人员");
		department.setUserList(getUserList(""));
		departmentList.add(department);
		
		return departmentList;
	}
	
	public void getDepartmentList(String userDepartMentId, List list) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select autoid, departname, parentid, property, Popedom, url,if(autoid=?,0,1) orderid "
						+ " from k_department " 
						+ " order by ABS(property),autoid";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, userDepartMentId);
			
			rs = ps.executeQuery();
			
			DepartmentVO department = null;
			while(rs.next()) {
				department = new DepartmentVO();
				department.setAutoId(rs.getInt(1));
				department.setDepartmentName(rs.getString(2));
				department.setParentId(rs.getInt(3));
				department.setProperty(rs.getString(4));
				department.setPopedom(rs.getString(5));
				department.setUrl(rs.getString(6));
				department.setUserList(getUserList(rs.getString(1)));
				
				list.add(department);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 获取部门列表
	 * @return
	 * @throws Exception
	 */
	public List<DepartmentVO> getDepartmentList() throws Exception {
		
		List<DepartmentVO> list = new ArrayList<DepartmentVO>();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select autoid, departname, property "
						+ " from asdb.k_department " 
						+ " order by abs(property),departname ";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			DepartmentVO department = null;
			while(rs.next()) {
				department = new DepartmentVO();
				department.setAutoId(rs.getInt(1));
				department.setDepartmentName(rs.getString(2));
				department.setProperty(rs.getString(3));
				
				list.add(department);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	/**
	 * 更新排序
	 * @param autoIds
	 * @return
	 * @throws Exception
	 */
	public int saveOrderBy(String autoIds) throws Exception {
		PreparedStatement ps = null;
		
		int result = 0;
		
		String[] autoId = autoIds.split(",");
		try {
			
			if(autoId != null) {
				String sql = " update asdb.k_department set property=? where autoid=? ";
				
				ps = conn.prepareStatement(sql);
				
				for (int i = 0; i < autoId.length; i++) {
					ps.setString(1, String.valueOf(i+100));
					ps.setString(2, autoId[i]);
					
					if(ps.executeUpdate() > 0 ) {
						result ++;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		
		return result;
	}

	public List getUserList(String deparmentId) throws Exception {
		List userList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select a.*,c.rolename as rolename,max(c.property) from k_user a "
						+ " left join k_userrole b on a.id=b.userid "
						+ " left join k_role c on b.rid=c.id "
						+ " where a.state=0 "
						+ " and a.departmentid=? "
						+ " group by a.name ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, deparmentId);
			
			rs = ps.executeQuery();
			
			User user = null;
			while(rs.next()) {
				user = new User();
				user.setId(rs.getString("id"));
        		user.setName(rs.getString("name"));
        		user.setLoginid(rs.getString("Loginid"));
        		user.setSex(rs.getString("sex"));
        		user.setPassword(rs.getString("password"));
        		user.setBorndate(rs.getString("borndate"));
        		user.setEducational(rs.getString("educational"));
        		user.setDiploma(rs.getString("diploma"));
        		user.setDepartmentid(rs.getString("departmentid"));
        		user.setDepartid(rs.getString("departid"));
        		user.setRank(rs.getString("rank"));
        		user.setPost(rs.getString("post"));
        		user.setSpecialty(rs.getString("specialty"));
        		user.setClientDogSysUi(rs.getString("clientDogSysUi"));
        		user.setUserPhoto(rs.getString("userPhoto")) ;
        		user.setUserPhotoTemp(rs.getString("userPhotoTemp")) ;
        		user.setRoles(rs.getString("rolename"));
        		
        		userList.add(user);
			}
			
			return userList;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return null;
	}
	
	
	public List getDepartmentLevel() throws Exception {
		List levelList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select distinct level0 from k_department a "
						+ " order by level0 ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				levelList.add(rs.getString(1)) ;
			}
			
			return levelList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	
	
	public void updateDepartmentPath() throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = ""; 
			//清理ProjectPopedom,去掉【上级部门的ProjectPopedom = 下级的部门之和】,只保留非下级的ProjectPopedom
			sql = "update k_department a,( " +
			"		select a.autoid,group_concat(if(c.fullpath like concat(a.fullpath ,'%') =0,c.autoid,null) order by c.autoid) as cautoid " +
			"		from k_department a,k_department c " +
			"		where 1=1 " +
			"		and concat(',',a.projectpopedom,',') like concat('%,',c.autoid,',%') " + 
			"		and a.isleaf = 0 " +
			"		group by a.autoid  " +
			"	) b  " +
			"	set a.projectpopedom = b.cautoid " +
			"	where a.autoid = b.autoid";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate() ;
			DbUtil.close(ps);
			
			//先修复1级
			sql = " update k_department a,k_organ b set a.fullpath=concat(a.autoid,'|'),a.level0=1 where a.parentid = b.departid" ;
			ps = conn.prepareStatement(sql);
			ps.executeUpdate() ;
			DbUtil.close(ps);
			
			//全路径
			sql = " UPDATE k_department a,k_department b " 
				+ " SET a.fullPath = CONCAT(b.fullPath,a.autoid,'|'),"
				+ " a.level0= b.level0 + 1 "
				+ " WHERE a.parentid = b.autoid";
			for(int i=0;i<100;i++) {
				ps = conn.prepareStatement(sql);
				int result = ps.executeUpdate() ;
				DbUtil.close(ps);
				if(result == 0) break ;
			}
			
			//是否叶子
			sql = "update k_department set isleaf = 1 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update k_department a ,k_department b set a.isleaf = 0 where a.autoid = b.parentid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//重算非叶子ProjectPopedom
			sql = "update k_department a,( " +
			//求出上级部门对应可以查看的部门，并压缩(注：上级部门=下级部门+自定义部门)
			"		select a.autoid,group_concat(c.autoid order by c.autoid) as cautoid " +  
			"		from k_department a,( " +
			//求出上级部门有多少下级
			"			select a.autoid,group_concat(b.autoid order by b.autoid) as bautoid " + 
			"			from k_department a,k_department b  " +
			"			where b.fullpath like concat(a.fullpath,'%') " +
			"			and a.autoid <> b.autoid " +
			"			group by a.autoid " +
			"		) b ,k_department c " +
			"		where a.autoid = b.autoid " +
			"		and concat(',',ifnull(a.projectpopedom,''),',',bautoid,',') like concat('%,',c.autoid,',%') " +
			"		group by a.autoid " +
			"	) b  " +
			"	set a.projectpopedom = b.cautoid " +
			"	where a.autoid = b.autoid"; 
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}

	}
	
	
	//单位树
	public List getOrgan(String checked)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		List list = null;
		try {
			//单位树
			int ii = 0; //单位树无值，返回"";
			list = new ArrayList();
			
			Map map = null;
			System.out.println("getOrgan="+userpopedom);
			if(userbool){
				map = new HashMap();
				map.put("isSubject", "1");//用于标志：当前节目的类型
				map.put("cls","folder");
				//单位树 都不是叶子
				map.put("leaf",true);	
				
				map.put("id","organ_alldeparts");
				map.put("departid","${alldeparts}");
				map.put("areaid","");
				map.put("departname","全所");
				if((","+this.userpopedom).indexOf((",${alldeparts},"))>-1){
					map.put("checked",true);
				}else{
					map.put("checked","true".equals(checked));
				}
				map.put("text","全所");
				list.add(map);
				
				map = new HashMap();
				map.put("isSubject", "1");//用于标志：当前节目的类型
				map.put("cls","folder");
				//单位树 都不是叶子
				map.put("leaf",true);	
				
				map.put("id","organ_office");
				map.put("departid","${office}");
				map.put("areaid","");
				map.put("departname","所在区域");
				if((","+this.userpopedom).indexOf((",${office},"))>-1){
					map.put("checked",true);
				}else{
					map.put("checked","true".equals(checked));
				}
				map.put("text","所在区域");
				list.add(map);
				
				map = new HashMap();
				map.put("isSubject", "1");//用于标志：当前节目的类型
				map.put("cls","folder");
				//单位树 都不是叶子
				map.put("leaf",true);	
				
				map.put("id","organ_department");
				map.put("departid","${department}");
				map.put("areaid","");
				map.put("departname","所在部门");
				if((","+this.userpopedom).indexOf((",${department},"))>-1){
					map.put("checked",true);
				}else{
					map.put("checked","true".equals(checked));
				}
				map.put("text","所在部门");
				list.add(map);
			}
			
			sql = "select * from k_organ where 1=1 order by departid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String departid,departname;
			while(rs.next()){
				departid = rs.getString("departid");
				departname = rs.getString("departname");
				
				map = new HashMap();
				map.put("isSubject", "1");//用于标志：当前节目的类型
				map.put("cls","folder");
				//单位树 都不是叶子
				map.put("leaf",false);	
				
				map.put("id","organ_"+departid+UUID.randomUUID().toString());
				map.put("departid",departid);
				map.put("areaid","");
				map.put("departname",departname);
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
				}
				map.put("text","单位：" + departname);
				
				list.add(map);
				ii ++;
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
	
	//区域树
	public List getArea(String organid,String checked)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = null;
		
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			list = new ArrayList();
			
			sql = "select * from k_department where parentid = ? and ifnull(areaid,'') = '' limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organid);
			rs = ps.executeQuery();
			if(rs.next()){
				//还有没有设置区域的部门
				Map map = new HashMap();
				map.put("isSubject","0");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",false);	
				map.put("id","area_"+organid+UUID.randomUUID().toString()) ;
				map.put("departid",organid);
				map.put("areaid","");
				map.put("departname","无设置区域");
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
				}
				map.put("text","无设置区域");
				list.add(map);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//区域树
			sql = "select * from k_area where organid = ? order by orderid,autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organid);
			rs = ps.executeQuery();
			String departid,departname;
			while(rs.next()){
				departid = rs.getString("autoid");
				departname = rs.getString("name");
				
				Map map = new HashMap();
				map.put("isSubject","2");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",false);	
				map.put("id","area_"+departid+UUID.randomUUID().toString()) ;
				map.put("departid",organid);
				map.put("areaid",departid);
				map.put("departname",departname);
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
				}
				map.put("text","区域：" + departname);
				
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
	
	public List getDepartment(String parentid,String areaid,String checked)throws Exception{
		return getDepartment(parentid, areaid, checked,true);
	}
	
	//部门树
//	alter table `asdb`.`k_department` add column `areaid` varchar (100)  NULL  COMMENT '区域ID' ;
//	alter table `asdb`.`k_department` add column isleaf varchar (10)  NULL ;
//	alter table `asdb`.`k_department` add column rand0 varchar (20)  NULL ;
	public List getDepartment(String parentid,String areaid,String checked,boolean isLeaf)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		String sql = ""; 
		try {
			int ii = 0; //区域树无值，返回"";
			//部门树
			list = new ArrayList();
			sql = "select a.*,b.parentid  as bparentid from k_department a,k_department b where a.parentid = ? and ifnull(a.areaid,'') = ? and b.level0=1 and a.fullpath like concat(b.fullpath,'%') order by a.property,a.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, parentid);
			ps.setString(2, areaid);
			rs = ps.executeQuery();
			String departid,departname,isleaf,bparentid;
			while(rs.next()){
				departid = rs.getString("autoid");
				departname = rs.getString("departname");
				isleaf = rs.getString("isleaf");
				bparentid = rs.getString("bparentid");

				Map map = new HashMap();
				
				map.put("isSubject","3");//用于标志：当前节目的类型
				map.put("cls","folder");
				if(isLeaf){
				if("true".equals(this.addUser)){
					map.put("leaf",false);
				}else{
					map.put("leaf","1".equals(isleaf));
				}
				}else{
					map.put("leaf", false);
				}
				map.put("id","depart_"+departid+UUID.randomUUID().toString()) ;
				map.put("departid",departid);
				map.put("areaid",areaid);
				map.put("departname",departname);
				map.put("bparentid",bparentid);
				if(checked != null && !"".equals(checked)) {
					if((","+this.userpopedom).indexOf((","+departid+","))>-1){
						map.put("checked",true);
					}else{
						map.put("checked","true".equals(checked));
					}
				}
				map.put("text",departname);
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
	
	//人员树
	public List getUser(String parentid,String checked)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = null;
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			//人员树
			list = new ArrayList();
			sql = "select a.* from k_user a  where a.departmentid = ?  order by a.loginid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, parentid);
			rs = ps.executeQuery();
			String userid,loginid,username;
			while(rs.next()){
				userid = rs.getString("id");
				loginid = rs.getString("loginid");
				username = rs.getString("name");
				
				Map map = new HashMap();
				map.put("isSubject","4");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",true);	
				map.put("id","user_"+userid);
				map.put("userid",userid);
				map.put("loginid",loginid);
				map.put("username",username);
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
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
	/**
	 * 查询记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param value : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public Map get(String table,String field,String value) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			Map map = new HashMap();
			String sql = "select * from "+table+" where "+field+"=? ";
			ps = conn.prepareStatement(sql);
	        ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();	
			if(rs.next()){
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase() , rs.getObject(RSMD.getColumnLabel(i)));
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public void save(String table ,int issubject,Map parameters) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			DbUtil db = new DbUtil(conn);
			
			String sql = "",sql1 = "",sql2 = "";
			
			//如果issubject = 0时，name1为0就是新增；如果不为0，就是修改，要求出区域ID对应的区域名称
			if(issubject == 0){
				String name1 = CHF.showNull((String)parameters.get("name1")); 
				if(!"0".equals(name1)){
					//修改，要name1对应的区域名称
					sql = "select name from "+table+" where autoid = ? ";
					parameters.put("autoid", name1);
					parameters.put("name", db.queryForString(sql, new String[]{name1}));
					
				}
			}
			
			
			String autoid = CHF.showNull((String)parameters.get("autoid")); 
			
			sql = "select * from "+table+" where 1=2 " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			
			//注：单位表是没有autoid字段，所以只能是新增
			if("".equals(autoid)){
				//新增
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase())){
						sql1 += ",`"+RSMD.getColumnLabel(i).toLowerCase()+"`";
						sql2 += ",?";
					}
				}
				sql = "insert into "+table+" ("+sql1.substring(1)+") values ("+sql2.substring(1)+") ";
			}else{
				//修改
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase()) ){
						sql1 += ",`"+RSMD.getColumnLabel(i).toLowerCase()+"` = ? ";
					}
				}
				sql = "update "+table+" set " + sql1.substring(1) + " where autoid = ? ";
			}
			
			//issubject -> 1:单位 2:区域 3:部门 0:区域(*)
			switch (issubject) {
			case 1: //单位
				String departid = CHF.showNull((String)parameters.get("departid")); 
				String strSql = "";
				if("".equals(departid)){ //不存在，新增
					strSql = "select max(departid) from " + table;
					int departMax = db.queryForInt(strSql);
					departid = String.valueOf(departMax + 100); //生成一个新的机构编号 
				}
				parameters.put("departid", departid); 
				
				//保存:先删除原来，再保存
				strSql = "delete from " + table + " where departid = ? ";
				db.execute(strSql, new String[]{departid});
				
				ps = conn.prepareStatement(sql);
				int ii = 1;
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
					ps.setString(ii, (string == null) ? null : string.trim() );
					ii++;
				}
				ps.execute();
				DbUtil.close(ps);
				
				break;
			case 0://区域
				//如果issubject = 0时，name1为0就是新增；如果不为0，就是修改，要求出区域ID对应的区域名称
			case 2://区域
				//保存
				ps = conn.prepareStatement(sql);
				ii = 1;
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase()) 
					){
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						ps.setString(ii, (string == null) ? null : string.trim() );
						ii++;
					}
				}
				if(!"".equals(autoid)){
					ps.setString(ii, (String)parameters.get("autoid") );
				}
				ps.execute();
				
				if("".equals(autoid)){
					//求出新增的autoid
					sql = "select autoid from " + table + " where property = ? ";
					autoid = db.queryForString(sql, new String[]{(String)parameters.get("property")});
					parameters.put("autoid", autoid);
				}
				
				//设置区域部门
				String departments = CHF.showNull((String)parameters.get("departments"));//还没有设置区域的部门
				if("".equals(departments)) departments = "''";
				sql = "update k_department set areaid = ? where autoid in ("+departments+")";
				db.execute(sql, new String[]{autoid});
				
				//修改所部门的1级的parentid，让它与区域的organid一致
				sql = "update k_department set parentid = ? where areaid = ? and level0=1 ";
				db.execute(sql, new String[]{(String)parameters.get("organid"),autoid});
				
				break;
			case 3: //部门
				//保存部门表，重算level0,fullpath,isleaf
				ps = conn.prepareStatement(sql);
				ii = 1;
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase()) 
					){
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						ps.setString(ii, (string == null) ? null : string.trim() );
						ii++;
					}
				}
				if(!"".equals(autoid)){
					ps.setString(ii, (String)parameters.get("autoid") );
				}
				ps.execute();
				
				//重算level0,fullpath,isleaf
				updateDepartmentPath();
				
				if("".equals(autoid)){
					//求出新增的autoid
					sql = "select autoid from k_department where rand0 = ? ";
					autoid = db.queryForString(sql, new String[]{(String)parameters.get("rand0")});
					parameters.put("autoid", autoid);
					
					//同步修改区域ID，与父节目的areaid一致
					sql = "update k_department a ,k_department b set a.areaid = b.areaid where a.rand0 = ? and a.parentid = b.autoid";
					db.execute(sql, new String[]{(String)parameters.get("rand0")});
				}else{
					
					//同步修改区域ID，与父节目的areaid一致
					sql = "update k_department a ,k_department b set a.areaid = b.areaid where a.autoid = ? and a.parentid = b.autoid";
					db.execute(sql, new String[]{autoid});
					
					//修改所有下级的区域ID
					sql = "update k_department a,k_department b set b.areaid = a.areaid where a.autoid = ? and b.fullpath like concat(a.fullpath ,'%') ";
					db.execute(sql, new String[]{autoid});
				}
				
				break;					
			default:
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//删除当前单位/区域/部门
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	/*
	 * 根据部门id查找所属地区
	 */
	public String getAreaId(String departmentId){
		String areaId=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select areaid from k_department where autoid="+departmentId;
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				areaId=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return areaId;
	}
	
	
	public List<Map> getListMapUser(String parentid,String loginUserId)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Map> list = null;
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			//人员树
			list = new ArrayList();
			
			if("commonlyUsed".equals(parentid)){
				
				String ids = new DbUtil(conn).queryForString("SELECT group_concat(DISTINCT userId) FROM oa_emailuser WHERE  userId>'' AND`uuid` IN (SELECT DISTINCT `uuid` FROM oa_email WHERE addresser = '"+loginUserId+"')");
				
				sql  = "select a.*,b.departname from (SELECT DISTINCT id,name,departmentId FROM k_user where id in ("+ids+")) a" +
						" left join k_department b on a.departmentId = b.autoId ";
				ps = conn.prepareStatement(sql);
			}

			rs = ps.executeQuery();
			String id ="";
			String name ="";
			String departmentId ="";
			String departName ="";
			while(rs.next()){
				id = rs.getString("id");
				name = rs.getString("name");
				departmentId=rs.getString("departmentid");
				departName=rs.getString("departname");
				
				Map map = new HashMap();
				map.put("id",id);
				map.put("name",name);
				map.put("departmentId", departmentId);
				map.put("departName", departName);
				if(list==null){
					list=new ArrayList<Map>();
				}
				list.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	
	/*
	 * 根据部门id查找人员
	 */
	public List<Map> getUserById(String autoid){
		List<Map> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select a.id,a.`name`,a.departmentid,b.departName from k_user a " +
						" left join k_department b on a.departmentid = b.autoId"+
						" where a.departmentid='"+autoid+"' and state=0 and a.name not like '%(离)%' and a.pccpa_seqno is not null  order by pccpa_seqno";
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			String id,name,departmentId,departName;
			while(rs.next()){
				Map map=new HashMap();
				id=rs.getString("id");
				name=rs.getString("name");
				departmentId=rs.getString("departmentid");
				departName=rs.getString("departname");
				map.put("id", id);
				map.put("name", name);
				map.put("departmentId", departmentId);
				map.put("departName", departName);
				if(list==null){
					list=new ArrayList<Map>();
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	
	public List<Map> getUserByKeywords(String keywords){
		List<Map> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select a.id,a.`name`,a.departmentid,b.departName from k_user a " +
						" left join k_department b on a.departmentid = b.autoId" +
						" where 1=1 and (a.name like '%"+keywords+"%' or b.departname like '%"+keywords+"%' or a.loginid like '%"+keywords+"%')  and state=0";
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			String id,name,departmentId,departName;
			while(rs.next()){
				Map map=new HashMap();
				id=rs.getString("id");
				name=rs.getString("name");
				departmentId=rs.getString("departmentid");
				departName=rs.getString("departname");
				map.put("id", id);
				map.put("name", name);
				map.put("departmentId", departmentId);
				map.put("departName", departName);
				if(list==null){
					list=new ArrayList<Map>();
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	/**
	 * 根据人员id进行查询
	 * @param ids
	 * @return
	 */
	public List<Map> getUserByIds(String ids){
		List<Map> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select a.id,a.`name`,a.departmentid,b.departName from k_user a " +
						" left join k_department b on a.departmentid = b.autoId" +
						" where 1=1 and a.id in ("+ids+")  and state=0";
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			String id,name,departmentId,departName;
			while(rs.next()){
				Map map=new HashMap();
				id=rs.getString("id");
				name=rs.getString("name");
				departmentId=rs.getString("departmentid");
				departName=rs.getString("departname");
				map.put("id", id);
				map.put("name", name);
				map.put("departmentId", departmentId);
				map.put("departName", departName);
				if(list==null){
					list=new ArrayList<Map>();
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	/**
	 * 根据角色ID，查询人员
	 * @param RoleId
	 * @return
	 */
	public List<Map> getUserByRoleId(String RoleId){
		List<Map> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="SELECT b.id,b.name,b.departmentid,c.departname FROM k_userrole a" +
						" INNER JOIN k_user b ON a.userid = b.id" +
						" LEFT JOIN k_department c ON b.departmentid = c.autoid" +
						" WHERE a.rid = '"+RoleId+"' And b.state = 0 Order by b.name ";
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			String id,name,departmentId,departName;
			while(rs.next()){
				Map map=new HashMap();
				id=rs.getString("id");
				name=rs.getString("name");
				departmentId=rs.getString("departmentid");
				departName=rs.getString("departname");
				map.put("id", id);
				map.put("name", name);
				map.put("departmentId", departmentId);
				map.put("departName", departName);
				if(list==null){
					list=new ArrayList<Map>();
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}

	public List getArea(String organid,String checked,String loginAreaId,String partArea)throws Exception{

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = null;
		
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			list = new ArrayList();
			
			sql = "select * from k_department where parentid = ? and ifnull(areaid,'') = '' limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organid);
			rs = ps.executeQuery();
			if(rs.next()){
				//还有没有设置区域的部门
				Map map = new HashMap();
				map.put("isSubject","0");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",false);	
				map.put("id","area_"+organid+UUID.randomUUID().toString()) ;
				map.put("departid",organid);
				map.put("areaid","");
				map.put("departname","无设置区域");
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
				}
				map.put("text","无设置区域");
				list.add(map);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			String sqlWhere = "";
			if("true".equals(partArea)){
				if(!"".equals(loginAreaId)){
					sqlWhere = " and autoid = '"+loginAreaId+"'";
				}
			}
			//区域树
			sql = "select * from k_area where organid = ? "+sqlWhere+" order by orderid,autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organid);
			rs = ps.executeQuery();
			String departid,departname;
			while(rs.next()){
				departid = rs.getString("autoid");
				departname = rs.getString("name");
				
				Map map = new HashMap();
				map.put("isSubject","2");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",false);	
				map.put("id","area_"+departid) ;
				map.put("departid",organid);
				map.put("areaid",departid);
				map.put("departname",departname);
				if(checked != null && !"".equals(checked)) {
					map.put("checked","true".equals(checked));
				}
				map.put("text","区域：" + departname);
				
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
	
	public boolean isTotalDep(UserSession userSession){
		DbUtil dbUtil=null;
		List<DepartmentVO> departmentVOs=new ArrayList<DepartmentVO>();
		try {
		 dbUtil=new DbUtil(conn);
		 departmentVOs=dbUtil.select(DepartmentVO.class, "select * from {0} where autoid=? and areaid=?",userSession.getUserAuditDepartmentId(), "1100");
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return !departmentVOs.isEmpty();
	}
}
