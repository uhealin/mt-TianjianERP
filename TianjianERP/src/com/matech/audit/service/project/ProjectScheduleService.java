package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import com.matech.audit.service.project.model.ProjectSchedule;
import com.matech.framework.pub.db.DbUtil;

public class ProjectScheduleService {
	
	private Connection conn = null;
	
	public ProjectScheduleService(Connection conn){
		this.conn = conn;
	}
	
	public boolean add(ProjectSchedule psh){
		boolean result = false;
		PreparedStatement ps = null;
		try{
			String sql="INSERT INTO `asdb`.`z_projectschedule`" +
						"(`projectId`,`projectName`,projectType,`responsibleUser`,`enterPdate`,`enterRdate`," +
						"`outworkerPdate`,`outworkerRdate`,`internalPdate`,`internalRdate`,`firstPdate`," +
						"`firstRdate`,`twoPdate`,`twoRdate`,`threePdate`,`threeRdate`," +
						"`reportPdate`,`reportRdate`,`archivesPdate`,`archivesRdate`,`createUser`," +
						"`createDate`,`createDepartment`) " +
						"values (?,?,?,?,?,?," +
								"?,?,?,?,?," +
								"?,?,?,?,?," +
								"?,?,?,?,?,now()," +
								"?)";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++,psh.getProjectId());
			ps.setString(i++,psh.getProjectName());
			ps.setString(i++,psh.getProjectType());
			ps.setString(i++,psh.getResponsibleUser());
			ps.setString(i++,psh.getEnterPdate());
			ps.setString(i++,psh.getEnterRdate());
			
			ps.setString(i++,psh.getOutworkerPdate());
			ps.setString(i++,psh.getOutworkerRdate());
			ps.setString(i++,psh.getInternalPdate());
			ps.setString(i++,psh.getInternalRdate());
			ps.setString(i++,psh.getFirstPdate());
			
			ps.setString(i++,psh.getFirstRdate());
			ps.setString(i++,psh.getTwoPdate());
			ps.setString(i++,psh.getTwoRdate());
			ps.setString(i++,psh.getThreePdate());
			ps.setString(i++,psh.getThreeRdate());
			
			ps.setString(i++,psh.getReportPdate());
			ps.setString(i++,psh.getReportRdate());
			ps.setString(i++,psh.getArchivesPdate());
			ps.setString(i++,psh.getArchivesRdate());
			ps.setString(i++,psh.getCreateUser());
			
			ps.setString(i++,psh.getCreateDepartment());
			ps.execute();
			
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;

	}
	
	public boolean update(ProjectSchedule psh){
		boolean result = false;
		PreparedStatement ps = null;
		  try{
			  String sql ="UPDATE `asdb`.`z_projectschedule`" +
			  		      "SET " +
			  		      "`projectId` = ?," +
			  		      "`projectName` = ?," +
			  		      " projectType=?,"+
			  		      "`responsibleUser` = ?," +
			  		      "`enterPdate` = ?," +
			  		      "`enterRdate` = ?," +
			  		      "`outworkerPdate` = ?," +
			  		      "`outworkerRdate` = ?," +
			  		      "`internalPdate` = ?," +
			  		      "`internalRdate` = ?," +
			  		      "`firstPdate` = ?," +
			  		      "`firstRdate` = ?," +
			  		      "`twoPdate` = ?," +
			  		      "`twoRdate` = ?," +
			  		      "`threePdate` = ?," +
			  		      "`threeRdate` = ?," +
			  		      "`reportPdate` = ?," +
			  		      "`reportRdate` = ?," +
			  		      "`archivesPdate` = ?," +
			  		      "`archivesRdate` = ?" +
			  		      "WHERE `autoId` = ?;";
			  
		    ps = conn.prepareStatement(sql);
		    int i = 1;
			ps.setString(i++,psh.getProjectId());
			ps.setString(i++,psh.getProjectName());
			ps.setString(i++,psh.getProjectType());
			ps.setString(i++,psh.getResponsibleUser());
			ps.setString(i++,psh.getEnterPdate());
			ps.setString(i++,psh.getEnterRdate());
			
			ps.setString(i++,psh.getOutworkerPdate());
			ps.setString(i++,psh.getOutworkerRdate());
			ps.setString(i++,psh.getInternalPdate());
			ps.setString(i++,psh.getInternalRdate());
			ps.setString(i++,psh.getFirstPdate());
			
			ps.setString(i++,psh.getFirstRdate());
			ps.setString(i++,psh.getTwoPdate());
			ps.setString(i++,psh.getThreeRdate());
			ps.setString(i++,psh.getThreePdate());
			ps.setString(i++,psh.getThreeRdate());
			
			ps.setString(i++,psh.getReportPdate());
			ps.setString(i++,psh.getReportRdate());
			ps.setString(i++,psh.getArchivesPdate());
			ps.setString(i++,psh.getArchivesRdate());
		    ps.setString(i++,psh.getAutoId());
		    
		    ps.execute();
		    result = true;
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
		  return result;
		  
	} 
	
	public ProjectSchedule getProjectSchedule(String autoId){
		  
		  ProjectSchedule psh = new ProjectSchedule();
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  try{
			  String sql ="SELECT `autoId`,`projectId`,`projectName`,projectType,`responsibleUser`,`enterPdate`," +
			  			  "`enterRdate`,`outworkerPdate`,`outworkerRdate`,`internalPdate`,`internalRdate`," +
			  			  "`firstPdate`,`firstRdate`,`twoPdate`,`twoRdate`,`threePdate`," +
			  			  "`threeRdate`,`reportPdate`,`reportRdate`,`archivesPdate`,`archivesRdate`," +
			  			  "`createUser`,`createDate`,`createDepartment` " +
			  			  " FROM `asdb`.`z_projectschedule` where autoId="+autoId;
			  ps = conn.prepareStatement(sql);
			  rs = ps.executeQuery();
			  if(rs.next()){
				  
				  psh.setAutoId(autoId);
				  psh.setProjectId(rs.getString("projectId"));
				  psh.setProjectName(rs.getString("projectName"));
				  psh.setProjectType(rs.getString("projectType"));
				  psh.setResponsibleUser(rs.getString("responsibleUser"));
				  psh.setEnterPdate(rs.getString("enterPdate"));
				 
				  psh.setEnterRdate(rs.getString("enterRdate"));
				  psh.setOutworkerPdate(rs.getString("outworkerPdate"));
				  psh.setOutworkerRdate(rs.getString("outworkerRdate"));
				  psh.setInternalPdate(rs.getString("internalPdate"));
				  psh.setInternalRdate(rs.getString("internalRdate"));

				  psh.setFirstPdate(rs.getString("firstPdate"));
				  psh.setFirstRdate(rs.getString("firstRdate"));
				  psh.setTwoPdate(rs.getString("twoPdate"));
				  psh.setTwoRdate(rs.getString("twoRdate"));
				  psh.setThreePdate(rs.getString("threePdate"));

				  psh.setThreeRdate(rs.getString("threeRdate"));
				  psh.setReportPdate(rs.getString("reportPdate"));
				  psh.setReportRdate(rs.getString("reportRdate"));
				  psh.setArchivesPdate(rs.getString("archivesPdate"));
				  psh.setArchivesRdate(rs.getString("archivesRdate"));
				  
				  
				  psh.setCreateUser(rs.getString("createUser"));
				  psh.setCreateDate(rs.getString("createDate"));
				  psh.setCreateDepartment(rs.getString("createDepartment"));
				  
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
			  DbUtil.close(rs);
		  }
		  return psh;
	  }

	public boolean delete(String autoId){
		PreparedStatement ps = null;
		boolean result = false;
		try{
			String sql = "delete from z_projectschedule where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	public String getProjectJson(String projectId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String isSpecialProject ="";   //项目类型
		String managerUserId  = ""; //项目负责人
		try{
			String sql ="SELECT * FROM `z_projectbusiness` WHERE projectId='"+projectId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				isSpecialProject = rs.getString("isSpecialProject");
				managerUserId = rs.getString("managerUserId");
			}
			Map<String, String> projectMap = new HashMap<String, String>();
			projectMap.put("isSpecialProject", isSpecialProject);
			projectMap.put("managerUserId", managerUserId);
			
			String json = JSONArray.fromObject(projectMap).toString();
			return json;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return "";
	}
}
