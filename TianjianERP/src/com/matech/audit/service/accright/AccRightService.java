package com.matech.audit.service.accright;
import java.sql.*;
import java.util.*;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
/*******************************************************************************
 * Copyright (c) 2006, 2008 MaTech Corporation.
 * All rights reserved.
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：
 * http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *******************************************************************************/

/**
 * @author k
 *
 */
public class AccRightService {

	private PreparedStatement ps = null;

	public AccRightService() {

	}

	/**
	 * 得到已授权的部门列表
	 * @param acc
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public ArrayList getDepartList(String acc, Connection conn)throws Exception {
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		ASFuntion CHF=new ASFuntion();
		try {
			 String sql = "select userid,Property from k_accright a where  DepartID='"+acc+"'";
			 ps = conn.prepareStatement(sql);
			 rs = ps.executeQuery();
			 while(rs.next()){
				 ArrayList all = new ArrayList();
				 all.add(CHF.showNull(rs.getString(1)));
				 all.add(CHF.showNull(rs.getString(2)));
				 al.add(all);
			 }
			 return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * 判断当前帐套的导入者是否为当前用户
	 *
	 * @param acc
	 *            当前帐套
	 * @param usrid
	 *            当前用户
	 * @param conn
	 *            数据库连接
	 * @return
	 * @throws Exception
	 */
	public String isAccName(String pid, String usrid, Connection conn)
			throws Exception {
		ResultSet rs = null;
		String result = "ok";
		try {
			String sql = "show Tables LIKE 'k_accright'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select customerid from z_project where projectid='"+pid+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					String departid = rs.getString(1);
					sql = "select * from k_accright WHERE departid='"+departid+"'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						sql = "select count(*) from k_accright WHERE departid='"+departid+"' and (userid='"+usrid+"' or userid in (select concat('[',departmentid,']') from k_user where id='"+usrid+"'))";
						org.util.Debug.prtOut(sql);
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if (rs.next()) {
							if (rs.getInt(1) <= 0) {
								result = "fail";
							} else {
								sql = "select count(*) from k_accright WHERE departid='"+departid+"' and (userid='"+usrid+"' or userid in (select concat('[',departmentid,']') from k_user where id='"+usrid+"')) and (property >= substring(now(),1,10) or property = '')";
								org.util.Debug.prtOut(sql);
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								if (rs.next()) {
									if (rs.getInt(1) <= 0) {
										result = "hasnoright";
									}
								}
								
							}
						}
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * 得到当前帐套的信息
	 *
	 * @param acc
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String getAccpInfo(String acc, Connection conn) throws Exception {
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select DepartName from k_customer where Property='1' and DepartID='"+acc+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("Departname"); // 客户名称1
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * 得到部门的树
	 * @param UsrID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String getATreeTable(String UsrID, Connection conn)
		throws Exception {
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer("");
		try {
			String sql = "select * from k_department where parentid='" + UsrID
					+ "'";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb.append("<table  border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" >");
			while (rs.next()) {
				sb.append("<tr height=\"20\" style=\"cursor: hand;\">");
				sb.append("<td width=\"20\" align=\"center\"  nowrap onclick=\"getSubTree(" + rs.getString("autoid") + ");\">");
				sb.append("<img id=\"ActImg" + rs.getString("autoid") +"\" src=\"/AuditSystem//images/nofollow.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td align=left valign=\"bottom\"  nowrap>");
				sb.append("<input type=\"checkbox\" name=\"departname\" value=\"["
					+ rs.getString("autoid")
					+ "]\" onclick=\"getBUser();\" >"
					+ "<span onclick=\"getSubTree(" + rs.getString("autoid") +
                    ");\"><font size=2>"+rs.getString("departname")+"</font></span>"
					);
				sb.append("</td></tr>");

				sb.append("<tr><td id='subImg" + rs.getString("autoid") + "' style='display:block'></td><td align=\"left\" valign=\"bottom\" style='display:block' id='subTree" + rs.getString("autoid") + "'>");
				sb.append(getATreeTable(rs.getString("autoid"),conn));
				sb.append("</td></tr>");
			}
			sb.append("</table>");

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
		}
	}




	public String getAUserTable(Connection conn) throws Exception {
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer("");
		ASFuntion CHF=new ASFuntion();
		try {
			String sql = "select name,loginid,departname,id  FROM k_user a  left join k_department b on a.departmentid=b.autoid where state=0 order by departname,name";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 0;
			int op =0;
			int jj = 0;
			String str = "`";
			while (rs.next()) {

				if(ii>0 && !str.equals(CHF.showNull(rs.getString("departname")))){
					sb.append("</tr>");
					sb.append("</table>");
					sb.append("</fieldset>");
					ii=0;
					op=0;
					jj=0;
				}
				if(jj==0 && !str.equals(CHF.showNull(rs.getString("departname")))){
					sb.append(" <fieldset  style=\"width:98%\">");
					sb.append("<legend>"+("".equals(CHF.showNull(rs.getString("departname")))?"无部门人员":CHF.showNull(rs.getString("departname")))+"</legend>");
					sb.append("<table   border=\"0\" cellpadding=\"2\" cellspacing=\"1\" width=\"100%\" >");
					jj=1;
					str = CHF.showNull(rs.getString("departname"));
				}
				if(ii%4==0){
					if(op==0){
						sb.append("<tr height=\"20\" >");
						op=1;
					}else{
						sb.append("</tr><tr height=\"20\" >");
					}
				}
				sb.append("<td><input type=\"checkbox\" name=\"Userid\" value=\""+rs.getString("id")+"\" onclick=\"getBUser();\" >"+rs.getString("name")+"</td>");

				ii++;
				if(jj!=0)str = CHF.showNull(rs.getString("departname"));

			}
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</fieldset>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
		}
	}



	public String getBUserTable(String optSS,String optSR,Connection conn,String acc)
	throws Exception {
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer("");
		try {
			String sql = "";
			String oSS ="";
			int ii = 0;
			int op =0;
			if(!optSS.equals("")){
				oSS = "'"+optSS.substring(0, optSS.length()-1).replaceAll("`", "','")+"'";
				sql = "select  autoid,departname,if(property,null,'') property from k_department where autoid in ("+oSS.replaceAll("\\[", "").replaceAll("\\]", "")+") and autoid not in (select  a.autoid from k_department a,k_accright b where b.userid in ("+oSS+") and concat('[',a.autoid,']')=b.userid and b.departid='"+acc+"') union  select  a.autoid,a.departname,b.property from k_department a,k_accright b where b.userid in ("+oSS+") and concat('[',a.autoid,']')=b.userid and b.departid='"+acc+"'";
				ps = conn.prepareStatement(sql);
				
				System.out.println("getBUserTable:"+sql);
				
				rs = ps.executeQuery();
				sb.append(" <fieldset  style=\"width:98%\">");
				sb.append("<legend>已授权部门</legend>");
				sb.append("<table   border=\"0\" cellpadding=\"2\" cellspacing=\"1\" width=\"100%\" >");
				while (rs.next()) {

					sb.append("<tr height=\"20\" >");
					sb.append("<td><input checked type=\"checkbox\" name=\"Saveid\" tvalue=\""+rs.getString("autoid")+"\" value=\"["+rs.getString("autoid")+"]\" >"+rs.getString("departname")+"</td>");
					sb.append("<td>有效期：<input name=\"bdate"+rs.getString("autoid")+"\" type=\"text\" id=\"bdate"+rs.getString("autoid")+"\" value=\""+rs.getString(3)+"\" maxlength=\"10\" class=\"validate-date-cn\"  title=\"请输入日期！\" showcalendar=\"true\">【选择】不填表示永远</td>");
					sb.append("</tr>");

				}
			//	sb.append("</tr>");
				sb.append("</table>");
				sb.append("</fieldset>");

			}
			String oSR = "";
			ii = 0;
			op =0;
			if(!optSR.equals("`")){
				oSR = "'"+optSR.substring(1, optSR.length()-1).replaceAll("`", "','")+"'";
				sql = "select name,loginid ,id,if(0,null,'') property from k_user a where id in ("+oSR+") and id not in (select a.id from k_user a,k_accright b where a.id in ("+oSR+") and a.id=b.userid and b.departid='"+acc+"' ) union select a.name,a.loginid ,a.id,b.property from k_user a,k_accright b where a.id in ("+oSR+") and a.id=b.userid and b.departid='"+acc+"'";
				ps = conn.prepareStatement(sql);
			
				rs = ps.executeQuery();

				sb.append(" <fieldset  style=\"width:98%\">");
				sb.append("<legend>已授权人员</legend>");
				sb.append("<table   border=\"0\" cellpadding=\"2\" cellspacing=\"1\" width=\"100%\" >");
				while (rs.next()) {
					sb.append("<tr height=\"20\" >");
					sb.append("<td><input checked type=\"checkbox\" name=\"Saveid\" tvalue=\""+rs.getString("id")+"\" value=\""+rs.getString("id")+"\" >"+rs.getString("name")+"</td>");
					sb.append("<td>有效期：<input name=\"bdate"+rs.getString("id")+"\" type=\"text\" id=\"bdate"+rs.getString("id")+"\" value=\""+rs.getString(4)+"\" maxlength=\"10\" class=\"validate-date-cn\"  title=\"请输入日期！\" showcalendar=\"true\">【选择】不填表示永远</td>");
					sb.append("</tr>");

				}
		//		sb.append("</tr>");
				sb.append("</table>");
				sb.append("</fieldset>");
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
		}
	}



	public void DelAccRight(String acc, Connection conn)throws Exception {
		try {
			String sql = "delete from k_accright where DepartID='"+acc+"'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("flush tables");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}
	/**
	 * 保存权限列表
	 * @param acc
	 * @param result
	 * @param conn
	 * @throws Exception
	 */
	public void SaveAccRight(String acc,String result, Connection conn)throws Exception {
		try {
			DelAccRight(acc,conn);

			String [] res = result.split("\\|");

			String sql = "insert into k_accright(DepartID,userid,Property) VALUES(?,?,?)";
			ps = conn.prepareStatement(sql);
			for(int i=0;i<res.length;i++){
				if(!res[i].equals("")){
					String [] ss = res[i].split("`");
					org.util.Debug.prtOut(ss[0]+":"+ss[1]);
					ps.setString(1,acc);
					ps.setString(2,"".equals(ss[0])?"":ss[0]);
					ps.setString(3,"".equals(ss[1])?"":ss[1]);

					ps.addBatch();
				}
			}
			ps.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}

	//保存 用户->客户权限 
	public void SaveUserRight(String userid,String customers, Connection conn)throws Exception {
		PreparedStatement ps1 = null;
		try {
			DelUserRight( userid,  conn);
			
			String [] res = customers.split(",");

			String sql = "insert into k_accright(DepartID,userid,Property) VALUES(?,?,?)";
			ps1 = conn.prepareStatement(sql);
			for(int i=0;i<res.length;i++){
				if(!res[i].equals("")){
					ps1.setString(1,res[i]);
					ps1.setString(2,userid);
					ps1.setString(3,"");

					ps1.addBatch();
				}
			}
			ps1.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(ps1);
		}
	}

	public void DelUserRight(String userid, Connection conn)throws Exception {
		PreparedStatement ps1 = null;
		try {
			String sql = "delete from k_accright where userid='"+userid+"'";
			ps1 = conn.prepareStatement(sql);
			ps1.execute();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(ps1);
		}
	}
}
