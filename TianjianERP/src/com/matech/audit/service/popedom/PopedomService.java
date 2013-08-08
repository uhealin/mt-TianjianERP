package com.matech.audit.service.popedom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.sysmenu.SysMenuCustomer;

/**
 * 
 * <p>
 * Title: TODO
 * </p>
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * <p>
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author LuckyStar 2007-6-28
 */
public class PopedomService {

	private Connection conn = null;

	private PreparedStatement ps = null;

	private String SysPpm = ""; // 系统拥有的权限

	private boolean userPopedom = false; //是否显示用户所有的部门权限
	private String loginid = ""; //用户loginid
	private String roleid = ""; //角色id
	private String property = "user"; //k_userpopedom 的property字段，用于区分是人员还是角色的部门授权,默认为user
	
	private int userRoleOptimization = 0;

	public int getUserRoleOptimization() {
		return userRoleOptimization;
	}

	public void setUserRoleOptimization(int userRoleOptimization) {
		this.userRoleOptimization = userRoleOptimization;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public boolean isUserPopedom() {
		return userPopedom;
	}

	public void setUserPopedom(boolean userPopedom) {
		this.userPopedom = userPopedom;
	}

	public PopedomService(Connection conn) {
		this.conn = conn;
	}

	public PopedomService(Connection conn, String SysPpm) {
		this.conn = conn;
		this.SysPpm = SysPpm;
	}

	
	/**
	 * 得到部门的树
	 * 
	 * @param parentid
	 *            部门的autoid,机构ID为555555
	 * @return
	 * @throws Exception
	 */
	public String getATreeTable(String parentid) throws MatechException {
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer("");
		try {
			String sql = "select * from k_department where parentid='"
					+ parentid + "' order by property";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb
					.append("<table  border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" >");
			while (rs.next()) {
				sb.append("<tr height=\"20\" style=\"cursor: hand;\">");
				sb
						.append("<td width=\"20\" align=\"center\"  nowrap onclick=\"getSubTree("
								+ rs.getString("autoid") + ");\">");
				sb
						.append("<img id=\"ActImg"
								+ rs.getString("autoid")
								+ "\" src=\"images/nofollow.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td align=left valign=\"bottom\"  nowrap>");
				sb
						.append("<input type=\"checkbox\" name=\"departname\" value=\""
								+ rs.getString("autoid")
								+ "\" onclick=\"getBUser();\" >"
								+ "<span onclick=\"getSubTree("
								+ rs.getString("autoid")
								+ ");\"><font size=2>"
								+ rs.getString("departname") + "</font></span>");
				sb.append("</td></tr>");

				sb
						.append("<tr><td id='subImg"
								+ rs.getString("autoid")
								+ "' style='display:block'></td><td align=\"left\" valign=\"bottom\" style='display:block' id='subTree"
								+ rs.getString("autoid") + "'>");
				sb.append(getATreeTable(rs.getString("autoid")));
				sb.append("</td></tr>");
			}
			sb.append("</table>");

			return sb.toString();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
	}

	/**
	 * 得到项目权限
	 * 
	 * @param loginid
	 *            用户登录名
	 * @return
	 */
	public String getProjectPopedom(String loginid) throws MatechException {
		String ppm = "";
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select ProjectPopedom from k_user where loginid ='"
							+ loginid + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				ppm = rs.getString(1);
			}
			rs.close();
			ps.close();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
		return ppm;
	}

	/**
	 * 得到项目权限
	 * 
	 * @param userid
	 *            用户登录名
	 * @return
	 */
	public String getProjectPopedomById(String id) throws MatechException {
		String ppm = "";
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select Popedom from k_user where id ='"
					+ id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				ppm = rs.getString(1) + "";
			}
			rs.close();
			ps.close();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
		return ppm;
	}

	/**
	 * 保存项目权限
	 * 
	 * @param ppm
	 * @param loginid
	 */
	public void SaveProjectPopedom(String ppm, String loginid)
			throws MatechException {
		try {
			ps = conn
					.prepareStatement("update k_user set ProjectPopedom = ? where loginid ='"
							+ loginid + "'");
			ps.setString(1, ppm);
			ps.executeUpdate();
			//ps.execute("Flush tables");
			ps.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 得到用户的所有权限
	 * 
	 * @param loginid
	 * @return
	 * @throws MatechException
	 */
	public String getUserPpmAll(String loginid) throws MatechException {
		String ppm = "";
		ResultSet rs = null;
		try {
			String sql = "select popedom from k_user where loginid ='"
					+ loginid
					+ "' union select b.popedom from k_userrole a, k_role b,k_user c where c.loginid='"
					+ loginid
					+ "' and a.rid = b.id and a.userid=c.id and c.state=0";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ppm += rs.getString(1);
			}
			return ppm;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}

	}

	/**
	 * 得到用户角色的所有权限
	 * 
	 * @param loginid
	 * @param conn
	 * @return
	 * @throws MatechException
	 */
	public String getUserRolePpm(String loginid) throws MatechException {
		String ppm = "";
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select b.popedom from k_userrole a, k_role b,k_user c  where c.loginid='"
							+ loginid
							+ "' and a.rid = b.id and a.userid=c.id and c.state=0");
			rs = ps.executeQuery();
			while (rs.next()) {
				ppm += rs.getString(1);
			}
			return ppm;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
	}

	/**
	 * 通过系统权限得到系统所有菜单
	 * 
	 * @param pid
	 * @param ppm
	 * @param onlyPpm
	 * @param conn
	 * @return
	 * @throws MatechException
	 */
	public String getSubTree(String pid, String ppm, String onlyPpm) throws MatechException {
		ResultSet rs = null;
		try {
			String sql = "select * from s_config where sname = '启用的中心' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			boolean bool = false;
			if(rs.next()){
				String svalue = rs.getString("svalue");
				if(!"".equals(svalue)){
					bool = true;
				}
			}
			StringBuffer sb = new StringBuffer("");
			if(bool){
				sql = "select DISTINCT menuversion,c.Autoid 	" +
				"	from k_menuversion a ,s_config b,k_dic c  	" +
				"	where b.sname = '启用的中心' 	" +
				"	AND c.ctype = 'dogversion' " +
				"	AND a.menuversion = c.Name " +
				"	and concat(',',b.svalue,',') like concat('%,',a.menuversion,',%') " +
				"	ORDER BY c.Autoid	";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				String menuversion = "";
				String menuverid = "";
				int opt = 0;
				while(rs.next()){
					menuversion = rs.getString("menuversion");
					menuverid = rs.getString("Autoid"); 
					
					sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\" >");
					sb.append("");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<font size=2>"+ menuversion+ "</font></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg' ></td>");
					sb.append("<td id='subTree' >"+ getSubTree1(pid, ppm, onlyPpm,menuversion,menuverid) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
					
					opt++;
				}
			}else{
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td width=\"20\" height=\"18\" align=\"right\" >");
				sb.append(""); 
				sb.append("</td>");
				sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<font size=2>审计作业中心</font></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg' ></td>");
				sb.append("<td id='subTree' >"+ getSubTree1(pid, ppm, onlyPpm) + "</td>");
				sb.append("</tr>");
				sb.append("</table>");
				
//				sb.append(getSubTree1(pid, ppm, onlyPpm));
			}
			return sb.toString();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
	}
	
	public String getSubTree1(String pid, String ppm, String onlyPpm,String menuversion,String menuverid) throws MatechException {
		ResultSet rs = null;
		try {
			
			DbUtil db = new DbUtil(conn);
			
			String strSql = "";
			if("role".equals(this.property)){
				strSql = "	and (a.property = '' or  a.property is null or a.property <= '"+this.userRoleOptimization+"') " ;
			}
//			String sql = "select depth,menu_id,name,ctype,parentid,id from s_sysmenu where parentid = '"
//				+ pid + "' and id in (" + SysPpm + ") order by id";
			String sql = "SELECT depth,menu_id,NAME,ctype,parentid,id  " +
			"	FROM s_sysmenu a,( " +
			"		SELECT a.* FROM k_menuversion a ,s_config b  " + 		
			"		WHERE b.sname = '启用的中心' 	 " +	
			"		AND CONCAT(',',b.svalue,',') LIKE CONCAT('%,',a.menuversion,',%') 	 " +
			"	) b " +  	
			"	WHERE a.id = b.menuid 	 " +
			"	and parentid <> '000' and ctype<>'000'" +
			"	AND b.menuversion = '"+menuversion+"' " +
			"	and a.parentid = '"+ pid + "' " +
			"	and a.id in (" + SysPpm + ") " +
			strSql + 
			"	order by seq_no,menu_id";
			//System.out.println(pid + "|"+StringUtil.getCurDateTime());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			int depth = 0;
			String menu_id = "";
			String name = "";
			String type = "&nbsp;<font size=2 color=blue>[菜单项]</font>";
			String state = "";
			String parentid = "";
			String powerid = "";
			String ctype = "";
			String only = "";

			// conn.close();
			while (rs.next()) {
				//depth,menu_id,NAME,ctype,parentid,id
				depth = rs.getInt("depth");
				menu_id = rs.getString("menu_id");
				name = rs.getString("NAME");
				ctype = rs.getString("ctype"); 
				parentid = rs.getString("parentid");
				powerid = rs.getString("id");
				state = "";
				only = "";
				
				if (ppm.indexOf("." + powerid + ".") >= 0) {
					state = "checked";
				}
				if ("02".equals(rs.getString(4))) {
					type = "&nbsp;<font size=2 color=red>[按钮]</font>";
				}else{
					type = "&nbsp;<font size=2 color=blue>[菜单项]</font>";
				}
				
				if(this.userPopedom){
					if(depth != 1 && !"02".equals(ctype)){
						//人员与部门权限：判断是否已授权 1为已授权
						ASFuntion CHF = new ASFuntion();
						String userpopedom = "";
						if("role".equals(this.property)){
							//角色的部门授权
							sql = "select 1 from k_userpopedom a where a.userid = ? and a.menuid =? and a.property =? ";
							userpopedom = CHF.showNull(db.queryForString(sql, new String[]{this.roleid,powerid,this.property}));
						}else{
							//用户的部门授权
							sql = "select 1 from k_userpopedom a,k_user b where a.userid = b.id and b.loginid = ? and a.menuid =? and a.property =? ";
							userpopedom = CHF.showNull(db.queryForString(sql, new String[]{this.loginid,powerid,this.property}));
						}
						if("1".equals(userpopedom)){
							type += "&nbsp;<input type='hidden' id='departmentid"+powerid+"' name='departmentid' value='' >" +
									"<input type='hidden' id='menuid"+powerid+"' name='menuid' value='"+powerid+"' >" +
									"<a href=\"javascript:void(0);\" onclick=\"getUserPopedom('"+powerid+"');\");\">[部门授权]<font color=red>(已授权)</font></a>";
						}else{
							type += "&nbsp;<input type='hidden' id='departmentid"+powerid+"' name='departmentid' value='' >" +
									"<input type='hidden' id='menuid"+powerid+"' name='menuid' value='"+powerid+"' >" +
									"<a href=\"javascript:void(0);\" onclick=\"getUserPopedom('"+powerid+"');\");\">[部门授权]</a>";	
						}
						if("role".equals(this.property)){
							//只有角色才有
							strSql = "select 1 from k_Field where menuid = '"+powerid+"' limit 1";
							String ifTrue = CHF.showNull(db.queryForString(strSql));
							if("1".equals(ifTrue)){
								type += "&nbsp;" +
										"<input type='hidden' id='omenuid"+powerid+"' name='omenuid' value='"+powerid+"' >" +
										"<input type='hidden' id='reading"+powerid+"' name='reading' value='' >" +
										"<input type='hidden' id='editing"+powerid+"' name='editing' value='' >" +
										"<a href=\"javascript:void(0);\" onclick=\"getFieldPopedom('"+powerid+"');\");\">[字段权限]</a>";
							}
						}
					}
				}

				if (onlyPpm.indexOf("." + powerid + ".") >= 0) {
					only = " disabled ";
				}

				i++;
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (depth > 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\" onclick=\"getSubTree("+ menuverid + menu_id + ");\">");
					sb.append("<img id=\"ActImg"+ menuverid + menu_id+ "\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type='checkbox' menuName='"+name+"' id='"+ parentid+ "'  name='MenuID' Menu='"+ menu_id+ "' value='"+ powerid+ "' MyID='"+ menu_id+ "' ParentID='"+ parentid+ "' onclick='setCountEnable(); setEnableTree(this);' "+ state+ only+ "><span onclick=\"getSubTree("+ menu_id+ ");\"><font size=2>"+ name+ "</font></span>" + type + "</td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" + menuverid + menu_id+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + menuverid + menu_id+ "' style='display:none'>"+ getSubTree1(menu_id, ppm, onlyPpm,menuversion,menuverid) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb.append("<img id=\"ActImg"+ menuverid + menu_id+ "\" src=\"images/sjx1.gif\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type='checkbox' menuName='"+name+"' id='"+ parentid+ "' name='MenuID' Menu='"+ menu_id+ "' value='"+ powerid+ "' depth=0 MyID='"+ menu_id+ "' ParentID='"+ parentid+ "' onclick='setCountEnable();setEnableTree(this);' "+ state+ only+ "><font size=2>"+ name+ "</font>&nbsp;" + type + "</td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" + menuverid + menu_id+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + menuverid + menu_id+ "' style='display:none'></td>");
					sb.append("</tr>");
					sb.append("</table>");
				}

			}
			rs.close();
			ps.close();
			return sb.toString();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}

	}

	public String getSubTree1(String pid, String ppm, String onlyPpm) throws MatechException {
		ResultSet rs = null;
		try {
			
			DbUtil db = new DbUtil(conn);
			
			String strSql = "";
			if("role".equals(this.property)){
				strSql = "	and (a.property = '' or  a.property is null or a.property <= '"+this.userRoleOptimization+"') " ;
			}
			
			String sql = "select depth,menu_id,name,ctype,parentid,id from s_sysmenu a where parentid = '"
				+ pid + "' and id in (" + SysPpm + ") and parentid <> '000'and ctype<>'000' " +
					
				strSql + 
				"order by seq_no,menu_id";
//			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			int depth = 0;
			String menu_id = "";
			String name = "";
			String type = "&nbsp;<font size=2 color=blue>[菜单项]</font>";
			String state = "";
			String parentid = "";
			String powerid = "";
			String ctype = "";
			String only = "";

			// conn.close();
			while (rs.next()) {
				depth = rs.getInt("depth");
				menu_id = rs.getString("menu_id");
				name = rs.getString("NAME");
				ctype = rs.getString("ctype"); 
				parentid = rs.getString("parentid");
				powerid = rs.getString("id");
				state = "";
				only = "";
				if (ppm.indexOf("." + powerid + ".") >= 0) {
					state = "checked";
				}
				if ("02".equals(rs.getString(4))) {
					type = "&nbsp;<font size=2 color=red>[按钮]</font>";
				}else{
					type = "&nbsp;<font size=2 color=blue>[菜单项]</font>";
				}

				if(this.userPopedom){
					if(depth != 1 && !"02".equals(ctype)){
						//人员与部门权限：判断是否已授权 1为已授权
						//1、property 2、loginid roleid
						ASFuntion CHF = new ASFuntion(); 
						String userpopedom = "";
						if("role".equals(this.property)){
							//角色的部门授权
							sql = "select 1 from k_userpopedom a where a.userid = ? and a.menuid =? and a.property =? ";
							userpopedom = CHF.showNull(db.queryForString(sql, new String[]{this.roleid,powerid,this.property}));
						}else{
							//用户的部门授权
							sql = "select 1 from k_userpopedom a,k_user b where a.userid = b.id and b.loginid = ? and a.menuid =? and a.property =? ";
							userpopedom = CHF.showNull(db.queryForString(sql, new String[]{this.loginid,powerid,this.property}));
						}
						if("1".equals(userpopedom)){
							type += "&nbsp;<input type='hidden' id='departmentid"+powerid+"' name='departmentid' value='' >" +
									"<input type='hidden' id='menuid"+powerid+"' name='menuid' value='"+powerid+"' >" +
									"<a href=\"javascript:void(0);\" onclick=\"getUserPopedom('"+powerid+"');\");\">[部门授权]<font color=red>(已授权)</font></a>";
						}else{
							type += "&nbsp;<input type='hidden' id='departmentid"+powerid+"' name='departmentid' value='' >" +
									"<input type='hidden' id='menuid"+powerid+"' name='menuid' value='"+powerid+"' >" +
									"<a href=\"javascript:void(0);\" onclick=\"getUserPopedom('"+powerid+"');\");\">[部门授权]</a>";	
						}
						
						if("role".equals(this.property)){
							//只有角色才有
							//通过SQL得到哪些菜单要字段权限
							strSql = "select 1 from k_Field where menuid = '"+powerid+"' limit 1";
							String ifTrue = CHF.showNull(db.queryForString(strSql));
							if("1".equals(ifTrue)){
								type += "&nbsp;" +
										"<input type='hidden' id='omenuid"+powerid+"' name='omenuid' value='"+powerid+"' >" +
										"<input type='hidden' id='reading"+powerid+"' name='reading' value='' >" +
										"<input type='hidden' id='editing"+powerid+"' name='editing' value='' >" +
										"<a href=\"javascript:void(0);\" onclick=\"getFieldPopedom('"+powerid+"');\");\">[字段权限]</a>";
							}
						}
					}
				}
				
				if (onlyPpm.indexOf("." + powerid + ".") >= 0) {
					only = " disabled ";
				}

				i++;
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (depth > 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\" onclick=\"getSubTree("+  menu_id + ");\">");
					sb.append("<img id=\"ActImg"+  menu_id+ "\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type='checkbox' menuName='"+name+"' id='"+ parentid+ "'  name='MenuID' Menu='"+ menu_id+ "' value='"+ powerid+ "' MyID='"+ menu_id+ "' ParentID='"+ parentid+ "' onclick='setCountEnable(); setEnableTree(this);' "+ state+ only+ "><span onclick=\"getSubTree("+ menu_id+ ");\"><font size=2>"+ name+ "</font></span>" + type + "</td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" +  menu_id+ "' style='display:none'></td>");
					sb.append("<td id='subTree" +  menu_id+ "' style='display:none'>"+ getSubTree1(menu_id, ppm, onlyPpm) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb.append("<img id=\"ActImg"+  menu_id+ "\" src=\"images/sjx1.gif\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type='checkbox' menuName='"+name+"' id='"+ parentid+ "' name='MenuID' Menu='"+ menu_id+ "' value='"+ powerid+ "' depth=0 MyID='"+ menu_id+ "' ParentID='"+ parentid+ "' onclick='setCountEnable();setEnableTree(this);' "+ state+ only+ "><font size=2>"+ name+ "</font>&nbsp;" + type + "</td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" +  menu_id+ "' style='display:none'></td>");
					sb.append("<td id='subTree" +  menu_id+ "' style='display:none'></td>");
					sb.append("</tr>");
					sb.append("</table>");
				}

			}
			rs.close();
			ps.close();
			return sb.toString();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}

	}
	
	/**
	 * 得到系统菜单树
	 * 
	 * @param pid
	 * @param loginid
	 * @return
	 * @throws MatechException
	 */
	public String getPopedomTree(String pid, String loginid)
			throws MatechException {
		try {
			if (pid == null || pid.equals("")) {
				return "";
			}
			if (loginid == null || loginid.equals("")) {
				return "";
			}
			this.loginid = loginid;
			return getSubTree(pid, getUserPpmAll(loginid),getUserRolePpm(loginid));
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 保存用户权限
	 * 
	 * @param ppm
	 * @param loginid
	 * @throws MatechException
	 */
	public void SavePopedom(String ppm, String loginid) throws MatechException {
		try {
			ps = conn
					.prepareStatement("update k_user set Popedom = ? where loginid ='"
							+ loginid + "'");
			ps.setString(1, ppm);
			ps.executeUpdate();
			//ps.execute("Flush tables");
			ps.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 得到角色的所有权限
	 * 
	 * @param loginid
	 * @param conn
	 * @return
	 * @throws MatechException
	 */
	public String getRolePpm(String loginid) throws MatechException {
		String ppm = "";
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select popedom from  k_role b where id='"
							+ loginid + "'");
			rs = ps.executeQuery();
			while (rs.next()) {
				ppm += rs.getString(1);
			}
			return ppm;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
		}
	}

	/**
	 * 检查用户（用户权限和部门权限）是否含有底稿另存为的权限
	 * 
	 * @param UsrID
	 * @param conn
	 * @return
	 */
	public boolean hasSaveRight(String UsrID) {
		boolean is = false; // 默认无权导出底稿
		String myPopedom = null;
		try {
			// 获得用户权限字段
			myPopedom = new SysMenuCustomer(conn).UserPpm(UsrID);
			// System.out.println(myPopedom);
			if (myPopedom.indexOf(".90.") > -1) { // 有权导出底稿
				is = true;
			}
		} catch (Exception e) {
			// "取用户底稿另存为权限失败!"
			e.printStackTrace();
		}
		return is;
	}
	
	/**
	 * 判断用户是否有菜单或按钮权限
	 * @param userId
	 * @param id
	 * @return
	 */
	public boolean hasRight(String userId, String id) {
		String myPopedom = null;
		try {
			// 获得用户权限字段
			myPopedom = new SysMenuCustomer(conn).UserPpm(userId);

			if (myPopedom.indexOf("." + id + ".") > -1) {
				return true;
			}
		} catch (Exception e) {
			// "取用户底稿另存为权限失败!"
			e.printStackTrace();
		}
		return false;
	}
}
