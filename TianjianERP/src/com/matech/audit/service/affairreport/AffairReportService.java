package com.matech.audit.service.affairreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.affairreport.model.AffairReportTable;
import com.matech.framework.pub.db.DbUtil;





/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 2.2
 */

public class AffairReportService {
	
	private Connection conn;

	public AffairReportService(Connection conn) {
		this.conn = conn;
	}
	
	public String getSqlProperty(String curDepartId,String ID)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String  sql = "select * from z_affairreport where id='"+ID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			//System.out.println("yzm:id="+ID);
			return rs.getString("ProjectID");
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		finally {
			if (ps != null)ps.close();
			
		}
	}
	public void isSaveProperty(String curDepartId,String ID,String Property)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			
			String sql = "UPDATE z_affairreport set porperty='"+Property+"' where id='"+ID+"'";
			
			ps = conn.prepareStatement(sql);
			ps.execute();
//			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
//			return false;
		}
		finally {
			if (ps != null)ps.close();
			
		}
	}
	
	public void setPlacard(AffairReportTable art,String curDepartId)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		
			
			String  sql = "select * from z_affairreport where pid='0' and ProjectID = ? and Matter = ? and Executer = ? and Author = ?";
		//	System.out.println("yzm:sql="+sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, art.getProjectID());
			ps.setString(2, art.getMatter());
			ps.setString(3, art.getExecuter());
			ps.setString(4, art.getAuthor());
			rs = ps.executeQuery();
			rs.next();
			String sid = rs.getString("ID"); 
			
			if(ps != null){
				ps.close();
			}
			
			String str = "";
			ps = conn.prepareStatement("select * from asdb.k_user where id='"+art.getAuthor()+"'");
			rs = ps.executeQuery();
			rs.next();
			str = rs.getString("Name");
			
			ps = conn.prepareStatement("INSERT INTO asdb.k_placard(Addresser,AddresserTime,Caption,Matter,Addressee,IsRead,IsReversion,isNotReversion,Property) VALUES(?,?,?,?,?,?,?,?,?)");
			
//				if(!"".equals(sart[i]) && !art.getAuthor().equals(sart[i])){
				if(art.getExecuter() != null && !"".equals(art.getExecuter()) ){	

					String str1 = ("").equals(art.getPorperty())?"无":art.getPorperty();
					ps.setString(1, art.getAuthor());
					ps.setString(2, art.getCreateTime());
					ps.setString(3, "请查看您的重大事件汇报！");
					ps.setString(4, "重大事件汇报：<br>项目名称：［"+art.getName()+"］<br>发报人：［"+str+"］<br>级别：［"+str1+"］<br>主题：［"+art.getCaption()+"］<br><br><a href='../affairReport/ViewDetail.jsp?chooseValue="+sid+"&DepartId="+curDepartId+"&opt=2' target='_self'>点击查看重大事件</a>");
					ps.setString(5, art.getExecuter());
					ps.setString(6,"0");
					ps.setString(7,"0");
					ps.setString(8,"1");
					ps.setString(9,"");
					ps.addBatch();
				}
			
			ps.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(rs != null) rs.close();
			if (ps != null)ps.close();
			
		}
		 
	}

	  public void AddAffairReport(AffairReportTable art,String curDepartId) throws Exception {
		new DBConnect().changeDataBase("", conn);
		DbUtil.checkConn(conn);
		
		PreparedStatement ps = null;
	    try {
	      int i = 1;
	      String[] userID=art.getExecuter().split(",");
	      
	      System.out.println("userID[0]="+userID[0]);
	      String sql ="";
	      if(art.getSubjectFullName1()==null||art.getSubjectFullName1()==""){
	    	  sql = "INSERT INTO z_affairreport(PID,ProjectID,Caption,Matter,Author,"
	    	  		+ "Executer,CreateTime,Porperty,"
	    	  		+ "status, principal, taskCodeList, lastUpdateTime, timeLimit) "
	    	  		+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)"; 
	      }else{
	    	  sql = "INSERT INTO z_affairreport(PID,ProjectID,Caption,Matter,Author,"
	    	  		+ "Executer,CreateTime,Porperty,"
	    	  		+ "status, principal, taskCodeList, lastUpdateTime, timeLimit, SubjectFullName1) "
	    	  		+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	      }
	      ps = conn.prepareStatement(sql);
	      
	      for(int j=0;j<userID.length;j++){
	    	  
	    	  if(userID[j] != null){
	    		  i=1;
	    		  System.out.println("hhhh:art="+art);
	    		  System.out.println("hhhh:ps="+ps);
		    	  ps.setInt(i++,art.getPID());
		    	  ps.setInt(i++,art.getProjectID());
		    	  ps.setString(i++,art.getCaption());
		    	  ps.setString(i++,art.getMatter());
		    	  ps.setString(i++,art.getAuthor());
		    	  ps.setString(i++,userID[j]);
		    	  System.out.println("hhhhh:userID[j]="+userID[j]);
		    	  ps.setString(i++,art.getCreateTime());
		    	  ps.setString(i++,art.getPorperty());
		    	  ps.setString(i++,art.getStatus());
		    	  ps.setString(i++,art.getPrincipal());
		    	  ps.setString(i++,art.getTaskCodeList());
		    	  ps.setString(i++,art.getLastUpdateTime());
		    	  ps.setString(i++,art.getTimeLimit());
		    	  if(art.getSubjectFullName1()!=null&&art.getSubjectFullName1()!=""){
		    		  ps.setString(i++,art.getSubjectFullName1());
		    	  }
		    	  ps.execute();
		    	  
		    	  if(art.getPID()==0){
		    		  art.setExecuter(userID[j]);
				      setPlacard(art,curDepartId);
			      }
		      }
	      }
	    }catch (Exception e) {
	      e.printStackTrace();
	    }finally{
	    	DbUtil.close(ps);
	    }
	  }

	public void delAffairReport(String sql,String curDepartId) throws Exception {
		DbUtil.checkConn(conn);
	    PreparedStatement ps = null;
	    try {
	      if (sql != null) {
	       
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally {
	      if (ps != null)
	        ps.close();
	      
	    }
	  }

	
	/**
	 * 
	  * <p>Description: ��ѯ��¼</p>
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	
	  public ArrayList getAffairReport(String sql,String curDepartId) throws Exception {
		  DbUtil.checkConn(conn);
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      ArrayList al = new ArrayList();

	      try {
	        if (sql != null) {
	        
	          ps = conn.prepareStatement(sql);
	          rs = ps.executeQuery();
	        
	         // System.out.println("yzm:sql="+sql);
	          while(rs.next()){
	        	 
	        	AffairReportTable art = new AffairReportTable();
	        	       	 
	            art.setID(rs.getInt("ID"));
	            art.setPID(rs.getInt("PID"));
	            art.setProjectID(rs.getInt("ProjectID"));
	            art.setCaption(rs.getString("Caption"));
	            art.setMatter(rs.getString("Matter"));
	            art.setAuthor(rs.getString("Author"));
	            art.setPrincipal(rs.getString("principal"));
	            art.setExecuter(rs.getString("Executer"));
	            art.setCreateTime(rs.getString("CreateTime"));  
	            art.setPorperty(rs.getString("porperty"));
		        art.setName(rs.getString("name"));
		        art.setIsRead(rs.getString("IsRead"));
		        art.setStatus(rs.getString("status"));
		        art.setTaskCodeList(rs.getString("taskCodeList"));
		        art.setSubjectFullName1(rs.getString("subjectFullName1"));
		        al.add(art);
	          
	          }
	        }
	        return al;
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	        return null;
	      }
	      finally {
	        if (rs != null)
	          rs.close();
	        if (ps != null)
	          ps.close();
	       
	      }
	  }
	  
	  
	  public ArrayList getNoRead(String id) throws Exception {
		  DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			ArrayList al = new ArrayList();
			try {

				String sql = "select concat('<a href=../affairReport/View.jsp target=\"_blank\">',CONCAT(SUBSTRING(a.Caption,1,8),'...'),'</a> <span class=\"disable_font\">[',b.name,']</span>') news from k_placard a,k_user b where Addressee='"
						+ id
						+ "' and isread='0' and a.Addressee = b.id order by a.addresserTime desc";
				//  org.util.Debug.prtOut("sql="+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					al.add(rs.getString("news"));
				}
				return al;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				

			}
		}
		
		/**
		 * 取得用户未读消息列表
		 * @param id
		 * @return
		 * @throws Exception
		 */
		public ArrayList getNoReadListByUserId(String userId,String curDepartId) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			ArrayList al = new ArrayList();
			try {
				
				String sql = "select concat('<a href=../affairReport.do target=\"_blank\">',CONCAT(SUBSTRING(a.Caption,1,8),'...'),'</a> <span class=\"disable_font\">[',b.name,']</span>') news "
							+ " from z_affairreport a ,k_user b "
							+ " where a.Executer= ? "
							+ " and a.IsRead = 0 "
							+ " and a.author = b.id "
							+ " order by a.createTime desc";
				  org.util.Debug.prtOut("sql="+sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				
				rs = ps.executeQuery();
				while (rs.next()) {
					al.add(rs.getString("news"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
				
			}
			
			return al;
		}
		
		
		public void updateIsRead(String id) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			try {

				ps = conn
						.prepareStatement("update z_affairreport set IsRead=1 where executer='"
								+ id + "'");
				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ps != null)
					ps.close();

			}
		}
		
		/**
		 * 取得公用主帖子
		 * @param id
		 * @return
		 * @throws Exception
		 */
		public String getMainAffairReport(String userId) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			String MainAffairReport="";
			try {
				
				String sql = "select id from z_affairreport where pid='0' and createtime=(select createtime from z_affairreport where id=?)";

				  org.util.Debug.prtOut("sql="+sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				
				rs = ps.executeQuery();
				if (rs.next()) {
					MainAffairReport=rs.getString("id");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
				
			}
			
			return MainAffairReport;
		}
		
		public String getStatus(String ID) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			String status = null;
			
			try{
				String sql = "select status from z_affairreport where id = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, ID);
				rs = ps.executeQuery();
				if(rs.next()){
					status = rs.getString("status");
				}
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return status;
		}
		
		public void update(String status, AffairReportTable art) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			
			String sql = "";
			if(art.getPrincipal() != null && !art.getPrincipal().equals("")){
				sql = "update z_affairreport set status = ?, lastUpdateTime = ?, principal = '"+art.getPrincipal()+"' where ID = ?";
			}
			else{
				sql = "update z_affairreport set status = ?, lastUpdateTime = ? where ID = ?";
			}
			
			try{
				ps = conn.prepareStatement(sql);
				ps.setString(1, status);
				ps.setString(2, art.getCreateTime());
				ps.setString(3, new Integer(art.getPID()).toString());
				
				ps.execute();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				DbUtil.close(ps);
			}
		}
		
		public void ReplyAffairReport(AffairReportTable art) throws Exception {
			 DbUtil.checkConn(conn);
			 
			 String sql = "INSERT INTO z_affairreport(PID,ProjectID,Caption,Matter,Author,"
	    	  		+ "CreateTime,Porperty,"
	    	  		+ "status, taskCodeList) "
	    	  		+ "VALUES(?,?,?,?,?,?,?,?,?)";
			 
			 PreparedStatement ps = null;
			 try{
				 ps = conn.prepareStatement(sql);
				 
				 int i = 1;
		    	 ps.setInt(i++,art.getPID());
		    	 ps.setInt(i++,art.getProjectID());
		    	 ps.setString(i++,art.getCaption());
		    	 ps.setString(i++,art.getMatter());
		    	 ps.setString(i++,art.getAuthor());
		    	 ps.setString(i++,art.getCreateTime());
		    	 ps.setString(i++,art.getPorperty());
		    	 ps.setString(i++,art.getStatus());
		    	 ps.setString(i++,art.getTaskCodeList());
				 
		    	 ps.execute();
			 }catch(Exception e){
				 e.printStackTrace();
			 }finally{
				 DbUtil.close(ps);
			 }
			 
			 String ID = new Integer(art.getPID()).toString();
			 String Executer = "";
			 String curDepartID = "";
			 try{
				 Executer = getExecuter(ID);
				 curDepartID = getDepartID(art);
			 }catch(Exception e){
				 e.printStackTrace();
			 }
			 
			 String Executers [] = new String[]{};
			 if(!"".equals(Executer)){
				 Executers = Executer.split(",");
			 }
			 
			System.out.println("HZH:Executer="+Executer);
			System.out.println("HZH");
			 
			 try{
				 for(int i=0; i<Executers.length; i++){
					 if(!"".equals(Executers[i]) && !art.getAuthor().equals(Executers[i])){
						 art.setExecuter(Executers[i]);
						 AddPlacard(art, curDepartID);
					 }
				 }
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		}
		
		public void AddPlacard(AffairReportTable art, String curDepartId) throws Exception {
			DbUtil.checkConn(conn);
			
			if("".equals(art.getExecuter())){
				return;
			}

			String sid = new Integer(art.getPID()).toString();
			if(sid == null || sid.equals("")){
				throw new Exception("id读取出错！");
			}
			
			String projectName = "";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				String sql = "select b.projectname as projectname from z_affairreport a left join z_project b \n"
							+ "on a.projectid = b.projectid \n"
							+ "where a.ProjectID = ? and a.Author = ? and a.CreateTime = ? ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, art.getProjectID());
				ps.setString(2, art.getAuthor());
				ps.setString(3, art.getCreateTime());
				
				rs = ps.executeQuery();
				if(rs.next()){
					projectName = rs.getString("projectname");
				}
				else{
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			String name = "";
			try{
				String sql = "select Name from asdb.k_user where id='"+art.getAuthor()+"'";
				ps = conn.prepareStatement(sql);
				
				rs = ps.executeQuery();
				if(rs.next()){
					name = rs.getString("Name");
				}
				else{
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}

			String level = "";
			if(art.getPorperty() == null || art.getPorperty().equals("")){
				level = "无";
			}
			else{
				level = art.getPorperty();
			}
			
			String PlacardBody = "重大事件汇报：<br>项目名称：［"+projectName+"］<br>发报人：［"+name+"］"
							+ "<br>级别：［"+level+"］<br>主题：［"+art.getCaption()+"］"
							+ "<br><a href='../affairReport/ViewDetail.jsp?chooseValue="+sid+"&DepartId="+curDepartId+"&opt=2' target='_self'>点击查看重大事件</a><br>"
							+ art.getMatter();
			
			try{
				String sql = "INSERT INTO asdb.k_placard("
					+ "Addresser,AddresserTime,Caption,Matter,"
					+ "Addressee,IsRead,IsReversion,"
					+ "isNotReversion,Property) "
					+ "VALUES(?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				
				ps.setString(1, art.getAuthor());
				ps.setString(2, art.getCreateTime());
				ps.setString(3, "您的重大事件汇报有更新");
				ps.setString(4, PlacardBody);
				ps.setString(5, art.getExecuter());
				ps.setString(6, "0");
				ps.setString(7, "0");
				ps.setString(8, "1");
				ps.setString(9, "");
				System.out.println("HZH:flag");
				ps.execute();
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
		}
		
		public String getExecuter(String id) throws Exception {
			
			String Executer = "";
			
			String sql = "select executer,author from z_affairreport where createtime = ( \n"
						+ "select createtime from z_affairreport where id = ?) \n"
						+ "and author = ( \n"
						+ "select author from z_affairreport where id = ?)";
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				ps = conn.prepareStatement(sql);
				ps.setString(1, id);
				ps.setString(2, id);
				
				rs = ps.executeQuery();
				
				String executer = "";
				String author = "";
				while(rs.next()){
					executer = rs.getString("executer");
					if(!"".equals(executer) && executer != null && !"null".equals(executer)){
						Executer += executer + ",";
					}
					author = rs.getString("author");
				}
				Executer = Executer + author + ",";
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return Executer;
		}
		
		public String getDepartID(AffairReportTable art) throws Exception {
			
			String departID = "";
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				String sql = "select departID from k_user where id = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(art.getAuthor()));
				
				rs = ps.executeQuery();
				if(rs.next()){
					departID = rs.getString("departID");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return departID;
		}
		
		public String getTaskName(String taskCode, String projectID) throws Exception {
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				String sql = "select TaskName from z_task where TaskCode = ? and ProjectID = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskCode);
				ps.setString(2, projectID);
				
				rs = ps.executeQuery();
				if(rs.next()){
					return rs.getString("TaskName");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return "";
		}
		
		public String getFullName(String subjectId,String CustomerID) throws Exception {
			new DBConnect().changeDataBase(CustomerID, conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				String sql = "select SubjectFullName from c_accpkgsubject where subjectId = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, subjectId);
				rs = ps.executeQuery();
				if(rs.next()){
					return rs.getString("SubjectFullName");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return "";
		}
		
		/**
		 * 判断项目中是否还存在未解决的重大事项<br>
		 * @param projectId 项目编号
		 * @return boolean true 存在
		 * @exception 抛出的异常
		 */
	public boolean isExistAffairReprot(String projectId) throws Exception {
			
			String sql = "select count(*) from z_affairreport where status='未解决' and projectId=?";
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectId);
				
				rs = ps.executeQuery();
				
				if(rs.next()) {
					
					int i = rs.getInt(1) ;
					if(i > 0) {
						return false ;
					}
					return true ;
				}
			
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return true ;
		}
}

