package com.matech.audit.service.attach;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;

public class AttachService {

	private Connection conn;

	public AttachService(Connection conn){
		this.conn = conn;
	}

	public String getSubTree(String pid,String opt,String cid) throws Exception {
		DbUtil.checkConn(conn);
		if (pid == null) {
			return "";
		}
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	    	StringBuffer sb = new StringBuffer("");
	    	String sql = "select IsLeaf,id,TypeName from asdb.k_attachtype where ParentID = '" +pid + "' ";
	    	ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	        	sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	        	if (rs.getInt(1) == 0) {
	        		sb.append("<tr onclick=\"getSubTree(" + rs.getInt(2) + ","+opt+","+cid+");\" style=\"cursor: hand;\">");
	        		sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
	        		sb.append("<img id=\"ActImg" + cid + rs.getInt(2) +"\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
	        		sb.append("</td>");
	        		sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt="+opt+"&cid="+cid+"&pid=" +rs.getInt(2) +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" + rs.getString(3) + "</a></td>");
          			sb.append("</tr>");
	        	}else {
	                sb.append("<tr style=\"cursor: hand;\">");
	                sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
	                sb.append("<img id=\"ActImg" + cid + rs.getInt(2) + "\" src=\"images/sjx1.gif\" width=\"11\" height=\"11\" />");
	                sb.append("</td>");
	                sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt="+opt+"&cid="+cid+"&pid=" +rs.getInt(2) +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" +rs.getString(3) + "</a></td>");
	                sb.append("</tr>");
	        	}
	        	sb.append("<tr>");
	            sb.append("<td id='subImg" + cid + rs.getInt(2) + "' style='display:none'></td>");
	            sb.append("<td id='subTree" + cid + rs.getInt(2) + "' style='display:none'></td>");
	            sb.append("</tr>");
	            sb.append("</table>");
	        }

	    	return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getOfficeTable(String OfficeID,String OfficeName) throws Exception{
		StringBuffer sb = new StringBuffer("");
		sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		sb.append("<tr onclick=\"getCustTree(" + OfficeID + ",1,"+OfficeID+");\" style=\"cursor: hand;\">");
		sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
		sb.append("<img id=\"ActImg" + OfficeID +"\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
		sb.append("</td>");
		sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt=1&pid=0&cid=" +OfficeID +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" + OfficeName + "</a></td>");
		sb.append("</tr>");

		sb.append("<tr>");
        sb.append("<td id='subImg" + OfficeID + "' style='display:none'></td>");
        sb.append("<td id='subTree" + OfficeID + "' style='display:none'></td>");
        sb.append("</tr>");
        sb.append("</table>");

		return sb.toString();
	}

	public String getCustomerTable(String CustomerID) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	    	//本单位及下级单位
//			String sql = "select departid,DepartName from k_customer where fullpathid like (select concat(fullpathid,'%') from k_customer where departid='"+CustomerID+"') order by departid";

	    	//除本单位外的其它单位
	    	String sql = "select departid,DepartName from asdb.k_customer where departid <> '"+CustomerID+"' order by departid";

			StringBuffer sb = new StringBuffer("");
			ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while(rs.next()){
	        	sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	        	sb.append("<tr onclick=\"getCustTree(" + rs.getInt(1) + ",2,"+rs.getInt(1)+");\" style=\"cursor: hand;\">");
        		sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
        		sb.append("<img id=\"ActImg" + rs.getInt(1) +"\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
        		sb.append("</td>");
        		sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt=2&pid=0&cid=" +rs.getInt(1) +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" + rs.getString(2) + "</a></td>");
      			sb.append("</tr>");
      			sb.append("<tr>");
	            sb.append("<td id='subImg" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("<td id='subTree" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("</tr>");
	            sb.append("</table>");
	        }
	        return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}


	public String getCustomerTable(String CustomerID,String userId,String departmentId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	    	//本单位及下级单位
//			String sql = "select departid,DepartName from k_customer where fullpathid like (select concat(fullpathid,'%') from k_customer where departid='"+CustomerID+"') order by departid";

	    	//除本单位外的其它单位
	    	String sql =
	    			"select distinct a.departid,a.departname from \n"+
	    			"k_accright b , k_customer a \n"+

	    			"where 1=1 and \n"+

	    			"( a.departid=b.departid and \n"+
	    			"	a.property='1' and a.departid <> '"+CustomerID+"' \n"+
	    			"and (b.departid is NULL or ((b.userid ='["+departmentId+"]' or b.userid='"+userId+"')  \n"+
	    			"	and (b.Property >= CURDATE() or b.Property ='' or b.property is null))) \n"+
	    			") \n"+
	    			"union \n"+
	    			"select distinct a.departid,a.departname from \n"+
	    			" k_customer a \n"+
	    			"where 1=1 and \n"+
	    			"(a.departid not in (select distinct departid from k_accright) and  a.property='1' and a.departid <> '555555')  ";

	    	//System.out.println("yzm:sql="+sql);
			StringBuffer sb = new StringBuffer("");
			ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while(rs.next()){
	        	sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	        	sb.append("<tr onclick=\"getCustTree(" + rs.getInt(1) + ",2,"+rs.getInt(1)+");\" style=\"cursor: hand;\">");
        		sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
        		sb.append("<img id=\"ActImg" + rs.getInt(1) +"\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
        		sb.append("</td>");
        		sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt=2&pid=0&cid=" +rs.getInt(1) +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" + rs.getString(2) + "</a></td>");
      			sb.append("</tr>");
      			sb.append("<tr>");
	            sb.append("<td id='subImg" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("<td id='subTree" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("</tr>");
	            sb.append("</table>");
	        }
	        return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	/**
	 * 通过客户编号构建审计对象树
	 * @param CustomerID
	 * @return
	 * @throws Exception
	 */

	public String getCustomerTableByCustomerId(String CustomerID) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	    	//本单位及下级单位
//			String sql = "select departid,DepartName from k_customer where fullpathid like (select concat(fullpathid,'%') from k_customer where departid='"+CustomerID+"') order by departid";

	    	//除本单位外的其它单位
	    	String sql = "select departid,DepartName from asdb.k_customer where departid = '"+CustomerID+"' order by departid";

			StringBuffer sb = new StringBuffer("");
			ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while(rs.next()){
	        	sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	        	sb.append("<tr onclick=\"getCustTree(" + rs.getInt(1) + ",2,"+rs.getInt(1)+");\" style=\"cursor: hand;\">");
        		sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
        		sb.append("<img id=\"ActImg" + rs.getInt(1) +"\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
        		sb.append("</td>");
        		sb.append("<td align=left valign=\"bottom\" nowrap><a href=\"attach.do?opt=2&pid=0&cid=" +rs.getInt(1) +"\" target='AttachMainFrame' onclick='doIt(this);'><font size=2>&nbsp;" + rs.getString(2) + "</a></td>");
      			sb.append("</tr>");
      			sb.append("<tr>");
	            sb.append("<td id='subImg" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("<td id='subTree" + rs.getInt(1) + "' style='display:none'></td>");
	            sb.append("</tr>");
	            sb.append("</table>");
	        }
	        return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void save(Attach attach ,String opt) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			if("ad".equals(opt)){
				sql = "insert into asdb.k_attach (UNID, typeid, Title, viewcount, orderId, lastPerson, udate, content, lastDate, filename, projectid, mime, departid, Property,edate,releasedate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				int i = 1;
				ps.setString(i++, attach.getUnid());
				ps.setString(i++, attach.getTypeId());
				ps.setString(i++, attach.getTitle());
				ps.setInt(i++, attach.getViewCount());
				ps.setString(i++, attach.getOrderId());

				ps.setString(i++, attach.getLastPerson());
				ps.setString(i++, attach.getUdate());
				ps.setString(i++, attach.getContent());
				ps.setString(i++, attach.getLastDate());
				ps.setString(i++, attach.getFilename());

				ps.setString(i++, attach.getProjectid());
				ps.setString(i++, attach.getMime());
				ps.setString(i++, attach.getDepartid());
				ps.setString(i++, attach.getProperty());
				ps.setString(i++, attach.getEdate());
				ps.setString(i++, attach.getReleasedate());

				ps.execute();
			}else{
				sql = "update asdb.k_attach set typeid = ?, Title = ?, viewcount = ?, orderId = ?, lastPerson = ?, edate = ?, content = ?, lastDate = ?,  projectid = ?, mime = ?, departid = ?, Property = ?,releasedate=? where UNID = ?";
				ps = conn.prepareStatement(sql);
				int i = 1;

				ps.setString(i++, attach.getTypeId());
				ps.setString(i++, attach.getTitle());
				ps.setInt(i++, attach.getViewCount());
				ps.setString(i++, attach.getOrderId());

				ps.setString(i++, attach.getLastPerson());
				ps.setString(i++, attach.getEdate());
				ps.setString(i++, attach.getContent());
				ps.setString(i++, attach.getLastDate());
//				ps.setString(i++, attach.getFilename());

				ps.setString(i++, attach.getProjectid());
				ps.setString(i++, attach.getMime());
				ps.setString(i++, attach.getDepartid());
				ps.setString(i++, attach.getProperty());
				ps.setString(i++, attach.getReleasedate());
				
				ps.setString(i++, attach.getUnid());
				
				ps.execute();
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public Attach getAttach(String unid) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Attach attach = new Attach();
			String sql = "select * from asdb.k_attach where unid='"+unid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){

				attach.setUnid(rs.getString("unid"));
				attach.setTypeId(rs.getString("typeId"));
				attach.setTitle(rs.getString("title"));
				attach.setContent(rs.getString("content"));
				attach.setUdate(rs.getString("udate"));

				attach.setLastDate(rs.getString("lastDate"));
				attach.setLastPerson(rs.getString("lastPerson"));
				attach.setOrderId(rs.getString("orderId"));
				attach.setViewCount(rs.getInt("viewCount"));
				attach.setFilename(rs.getString("filename"));

				attach.setEdate(rs.getString("edate"));
				attach.setMime(rs.getString("mime"));
				attach.setDepartid(rs.getString("departid"));
				attach.setProperty(rs.getString("property"));
				attach.setProjectid(rs.getString("projectid"));
				attach.setReleasedate(rs.getString("releasedate"));

			}
			return attach;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}


	public String getParentConn(String pid) throws Exception {
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    String result = "";
	    String sql = "";
	    ArrayList al = new ArrayList();
	    try {
	    	al.add(pid);
	    	for(int i=0; i<al.size();i++){
	    		sql = "select id from asdb.k_attachtype where parentid='"+(String)al.get(i)+"'";
	    		ps = conn.prepareStatement(sql);
	    		rs = ps.executeQuery();
	    		while(rs.next()){
	    			al.add(rs.getString(1));
	    		}
	    	}
	    	result = "('"+(String)al.get(0);
	    	for(int i=1; i<al.size();i++){
	    		result +="','"+(String)al.get(i);
	    	}
	    	result += "')";
	    	return result;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw e;
	    } finally {
	    	DbUtil.close(rs);
			DbUtil.close(ps);
	    }
	}

	public void delAttach(String unid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from asdb.k_attach where unid='" + unid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String filename = "";
			String departid = "";
			if(rs.next()){
				filename = rs.getString("filename");
				departid = rs.getString("departid");
				unid = rs.getString("unid");
			}
			if(!"".equals(filename)){
				String file = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/" + departid + "/attach/" + unid;
				ManuFileService.deleteFile(new File(file));
			}
			sql = "delete from asdb.k_attach where  unid='" + unid + "'";
			ps = conn.prepareStatement(sql);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getTypeName(String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select TypeName from asdb.k_attachtype where id='" + typeId + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 检查审计对象类型表里有没名称为typename的类型，有就返回编号，没有就加一个再返回编号
	 * @param typename 审计对象类型名称
	 * @param parentNameOrId 上级审计对象类型名称 或 编号        
	 * @return ：审计对象类型编号
	 * @throws Exception
	 */
	public String checkAndAddAtach(Attach attach) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String unid = "";

		try {
			String getParentSql = "select * from k_attach where typeid = '"+attach.getTypeId()+"' and  filename = '"+attach.getFilename()+"'";
			ps = conn.prepareStatement(getParentSql);
			rs = ps.executeQuery();
			
			if(rs.next()){
				
				unid = rs.getString("unid");	
				attach.setUnid(unid);
				attach.setContent(rs.getString("content"));
				attach.setEdate(rs.getString("edate"));
				attach.setOrderId(rs.getString("OrderId"));
				attach.setProjectid(rs.getString("Projectid"));
				attach.setProperty(rs.getString("Property"));
				attach.setUdate(rs.getString("Udate"));
				attach.setTitle(rs.getString("Title"));
				save(attach,"ed");
				return unid;
			}
			
			rs.close();
			ps.close();
			String NumUnid = DELUnid.getNumUnid();
			
			String tempsql = "select * from k_attach where unid = '"+NumUnid+"'";
			
			ps = conn.prepareStatement(tempsql);
			rs = ps.executeQuery();
			
			if(rs.next()){
				NumUnid= (rs.getLong("unid")+1)+"";
					
			}
			
			rs.close();
			ps.close();
			
			attach.setUnid(NumUnid);
			save(attach,"ad");
			
			
			return  NumUnid;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
}

