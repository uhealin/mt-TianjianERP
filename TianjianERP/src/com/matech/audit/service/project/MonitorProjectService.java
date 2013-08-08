package com.matech.audit.service.project;

import java.sql.*;

import com.matech.framework.pub.db.DbUtil;

public class MonitorProjectService {

	private DbUtil db = null;
	
	public MonitorProjectService(Connection conn) throws Exception {
		db = new DbUtil(conn);
	}
	
	//得到合伙人项目列表
	public String getMonitor(String projectyear,String choose)throws Exception {
		ResultSet rs = null;
		try {
			
			StringBuffer sb = new StringBuffer();
			String sql = "";
			
			sb.append("<table id=\"groupProjectTable\" align=\"top\" width=\"98%\" bgcolor=\"#99BBE8\" cellspacing=\"1\" cellpadding=\"3\" >");
			
			sql = "select * from k_user where id in ("+choose+") order by id";
			rs = db.getResultSet(sql);
			while(rs.next()){
				String userid = rs.getString("id");
				String name = rs.getString("name");
				sb.append("\n<tr id=\"head\">")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" align=\"left\" style=\"font-weight: bold;color: blue;\" >")
					.append("		<input type=\"checkbox\" value=\""+userid+"\" checked=true onclick=\"setJoinAll(this);\"/>" + name + "</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >项目名称</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >项目所属集团</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >项目类型</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >项目年度</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >原项目负责人</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >本次检查项目负责人</td>")
					.append("\n	<td bgcolor=\"#DDE9F9\" nowrap=\"nowrap\" >本次检查项目组员</td>")
					.append("\n</tr>");
				sb.append(getProject( projectyear, userid));
				
			}
			sb.append("\n</table>");
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
		}
		
	}
	
	//得到一个合伙人项目
	public String getProject(String projectyear,String userid)throws Exception {
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			String sql = "select distinct a.*,ifnull(d.name,'') as dname,ifnull(e.groupname,'') as egroupname " +
			"	from z_project a  " +
			"	inner join z_auditpeople b on b.role = '签字合伙人' and a.projectid = b.projectid  " +
			"	left join z_auditpeople c on c.role = '项目负责人' and a.projectid = c.projectid  " +
			"	left join k_user d on c.userid = d.id " +
			"	left join k_group e on a.groupname = e.groupid " +
			"	where 1=1 " +
			"	and a.projectyear >= ("+projectyear+"-2) and a.projectyear <= "+projectyear+"  " +
			"	and b.userid = "+userid+"	 " +
			"	and a.auditpara <> '后续质量监管'" +
			"	order by a.projectyear desc,a.groupname,a.projectid";
			rs = db.getResultSet(sql);
			while(rs.next()){
				String projectid = rs.getString("projectid");
				String projectname = rs.getString("projectname");	//项目名称	
				String auditpara = rs.getString("auditpara");	//项目类型
				String projectyear1 = rs.getString("projectyear");	//项目年度
				String dname = rs.getString("dname");	//原项目负责人
				String egroupname = rs.getString("egroupname");	//项目所属集团
				
				sb.append("\n<tr class=\"workClass\" >")
					.append("\n	<td>")
					.append("<input type=\"hidden\" name=\"projectid\" id=\"projectid_").append(projectid).append("\" value=\"").append(projectid).append("\" > ")
					.append("<input type=\"hidden\" name=\"userid\" id=\"userid_").append(projectid).append("\" value=\"").append(userid).append("\" > ")
					.append("</td>")
					.append("\n	<td align=\"left\">").append("<input userid =\""+userid+"\" type=\"checkbox\" value=\"").append(projectid).append("\" projectid=\"").append(projectid).append("\" onclick=\"setJoin(this);\"/ checked=true  name=\"isjoin\" id=\"isjoin_").append(projectid).append("\" >").append(projectname).append("</td>")
					.append("\n	<td align=\"left\">").append(egroupname).append("</td>")
					.append("\n	<td align=\"left\">").append(auditpara).append("</td>")
					.append("\n	<td align=\"left\">").append(projectyear1).append("</td>")
					.append("\n	<td align=\"left\">").append(dname).append("</td>")
					.append("\n	<td> ").append("<input type=\"text\" name=\"auditmanager\" id=\"auditmanager_").append(projectid).append("\" ")
					.append("			maxlength=\"20\" class=\"required\" ")
					.append("			title=\"项目经理必填\" onkeydown=\"onKeyDownEvent();\" ")
					.append("			onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" ")
					.append("			autoid=622 />").append("</td> ")
					.append("\n	<td>").append("<input type=\"text\" name=\"auditpeople\" id=\"auditpeople_").append(projectid).append("\" ")
					.append("			maxlength=\"20\" class=\"required\" ")
					.append("			title=\"项目组员必填\" onkeydown=\"onKeyDownEvent();\" ")
					.append("			onkeyup=\"onKeyUpEvent();\" multiselect=true onclick=\"onPopDivClick(this);\" ")
					.append("			autoid=622 />").append("</td>")
					.append("\n</tr>");
					
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
		}
	}
	
	

}

