package com.matech.audit.service.employment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.employment.model.Education;
import com.matech.audit.service.employment.model.FamilyInfo;
import com.matech.audit.service.employment.model.Info;
import com.matech.audit.service.employment.model.PersonalInfo;
import com.matech.audit.service.employment.model.Questions;
import com.matech.audit.service.employment.model.Reference;
import com.matech.audit.service.employment.model.Skills;
import com.matech.audit.service.employment.model.Train;
import com.matech.audit.service.employment.model.WorkHistory;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

public class EmploymentService {
	private Connection conn=null;
	protected DbUtil dbUtil;
	public EmploymentService(Connection conn){
		this.conn=conn;
		try {
			dbUtil=new DbUtil(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final String UUID_FORM_TYPE_SUBSET="7940c60e-cac4-41f5-8c40-eff9f4b27f29";
	
	//插入应聘信息
	public boolean addInfo(Info info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_info (position,secondChoice,preferred,secondWork,candidateSource,monthSalary,"
						+"others,annualPackage,availableWork,photo,linkUserId,recommend,photoTemp) values(?,?,?,?,?,?, ?,?,?,?,?,?, ?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getPosition());
			ps.setString(i++, info.getSecondChoice());
			ps.setString(i++, info.getPreferred());
			ps.setString(i++, info.getSecondWork());
			ps.setString(i++, info.getCandidateSource());
			ps.setString(i++, info.getMonthSalary());
			
			ps.setString(i++, info.getOthers());
			ps.setString(i++, info.getAnnualPackage());
			ps.setString(i++, info.getAvailableWork());
			ps.setString(i++, info.getPhoto());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getRecommend());
			
			ps.setString(i++, info.getPhotoTemp());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新应聘信息
	public void updateInfo(Info info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_info set position=?,secondChoice=?,preferred=?,secondWork=?,candidateSource=?," 
						+"monthSalary=?,others=?,annualPackage=?,availableWork=?,photo=?,linkUserId=?,recommend=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getPosition());
			ps.setString(i++, info.getSecondChoice());
			ps.setString(i++, info.getPreferred());
			ps.setString(i++, info.getSecondWork());
			ps.setString(i++, info.getCandidateSource());
			
			ps.setString(i++, info.getMonthSalary());
			ps.setString(i++, info.getOthers());
			ps.setString(i++, info.getAnnualPackage());
			ps.setString(i++, info.getAvailableWork());
			ps.setString(i++, info.getPhoto());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getRecommend());
			
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除应聘信息
	public void delInfo(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_info where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkUserId查找应聘信息
	public Info findInfo(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		Info info=null;
		try {
			String sql="select * from k_employment_info where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				info=new Info();
				
				info.setId(rs.getString("id"));
				info.setPosition(rs.getString("position"));
				info.setSecondChoice(rs.getString("secondChoice"));
				info.setPreferred(rs.getString("preferred"));
				info.setSecondWork(rs.getString("secondWork"));
				
				info.setCandidateSource(rs.getString("candidateSource"));
				info.setMonthSalary(rs.getString("monthSalary"));
				info.setOthers(rs.getString("others"));
				info.setAnnualPackage(rs.getString("annualPackage"));
				info.setAvailableWork(rs.getString("availableWork"));
				
				info.setPhoto(rs.getString("photo"));
				info.setPhotoTemp(rs.getString("phototemp"));
				info.setLinkUserId(linkUserId);
				info.setRecommend(rs.getString("recommend"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return info;
	}
	
	//插入个人信息
	public void addPersonalInfo(PersonalInfo info){
		PreparedStatement ps=null;
		try {
			String sql="insert into k_employment_personalinfo (chineseName,englishName,gender,born,place,nowWorkPlace,"
						+"nation,degree,residence,marital,mobile,passport," 
						+"idNo,email,qualifications,nationality,permanentResidence,huKou," 
						+"relationships,pregnant,state) " 
						+" values(?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getChineseName());
			ps.setString(i++, info.getEnglishName());
			ps.setString(i++, info.getGender());
			ps.setString(i++, info.getBorn());
			ps.setString(i++, info.getPlace());
			ps.setString(i++, info.getNowWorkPlace());
			
			ps.setString(i++, info.getNation());
			ps.setString(i++, info.getDegree());
			ps.setString(i++, info.getResidence());
			ps.setString(i++, info.getMarital());
			ps.setString(i++, info.getMobile());
			ps.setString(i++, info.getPassport());
			
			ps.setString(i++, info.getIdNo());
			ps.setString(i++, info.getEmail());
			ps.setString(i++, info.getQualifications());
			ps.setString(i++, info.getNationality());
			ps.setString(i++, info.getPermanentResidence());
			ps.setString(i++, info.getHuKou());
			
			ps.setString(i++, info.getRelationships());
			ps.setString(i++, info.getPregnant());
			ps.setString(i++, "候选");
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//更新个人信息
	public void updatePersonalInfo(PersonalInfo info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_personalinfo set chineseName=?,englishName=?,gender=?,born=?,place=?," 
						+"nowWorkPlace=?,nation=?,degree=?,residence=?,marital=?,mobile=?,passport=?,idNo=?,email=?,"
						+"qualifications=?,nationality=?,permanentResidence=?,huKou=?,relationships=?,pregnant=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getChineseName());
			ps.setString(i++, info.getEnglishName());
			ps.setString(i++, info.getGender());
			ps.setString(i++, info.getBorn());
			ps.setString(i++, info.getPlace());
			
			ps.setString(i++, info.getNowWorkPlace());
			ps.setString(i++, info.getNation());
			ps.setString(i++, info.getDegree());
			ps.setString(i++, info.getResidence());
			ps.setString(i++, info.getMarital());
			ps.setString(i++, info.getMobile());
			ps.setString(i++, info.getPassport());
			ps.setString(i++, info.getIdNo());
			ps.setString(i++, info.getEmail());
			
			ps.setString(i++, info.getQualifications());
			ps.setString(i++, info.getNationality());
			ps.setString(i++, info.getPermanentResidence());
			ps.setString(i++, info.getHuKou());
			ps.setString(i++, info.getRelationships());
			ps.setString(i++, info.getPregnant());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除个人信息
	public void delPersonalInfo(String id){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_personalinfo where id="+id;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据id查找个人信息
	public PersonalInfo findPersonalInfo(String id){
		PreparedStatement ps=null;
		ResultSet rs=null;
		PersonalInfo info=null;
		try {
			String sql="select * from k_employment_personalinfo where id="+id;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){	
				info=new PersonalInfo();
				
				info.setId(rs.getString("id"));
				info.setChineseName(rs.getString("chineseName"));
				info.setEnglishName(rs.getString("englishName"));
				info.setGender(rs.getString("gender"));
				info.setBorn(rs.getString("born"));
				
				info.setPlace(rs.getString("place"));
				info.setNowWorkPlace(rs.getString("nowWorkPlace"));
				info.setNation(rs.getString("nation"));
				info.setDegree(rs.getString("degree"));
				info.setResidence(rs.getString("residence"));
				
				info.setMarital(rs.getString("marital"));
				info.setMobile(rs.getString("mobile"));
				info.setPassport(rs.getString("passport"));
				info.setIdNo(rs.getString("idNo"));
				info.setEmail(rs.getString("email"));
				
				info.setQualifications(rs.getString("qualifications"));
				info.setNationality(rs.getString("nationality"));
				info.setPermanentResidence(rs.getString("permanentResidence"));
				info.setHuKou(rs.getString("huKou"));
				info.setRelationships(rs.getString("relationships"));
				info.setPregnant(rs.getString("pregnant"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return info;
	}
	
	//插入教育信息
	public boolean addEducation(Education info){
		PreparedStatement ps=null;
		boolean result = false;
		try {
			String sql="insert into k_employment_education (schoolType,schoolName,startTime,endTime,major,degreeAndDiploma,"
						+"linkUserId) " 
						+" values(?,?,?,?,?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getSchoolType());
			ps.setString(i++, info.getSchoolName());
			ps.setString(i++, info.getStartTime());
			ps.setString(i++, info.getEndTime());
			ps.setString(i++, info.getMajor());
			ps.setString(i++, info.getDegreeAndDiploma());
			ps.setString(i++, info.getLinkUserId());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新教育信息
	public void updateEducation(Education info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_education set schoolType=?,schoolName=?,startTime=?,endTime=?,major=?," 
						+"degreeAndDiploma=?,linkUserId=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getSchoolType());
			ps.setString(i++, info.getSchoolName());
			ps.setString(i++, info.getStartTime());
			ps.setString(i++, info.getEndTime());
			ps.setString(i++, info.getMajor());
			
			ps.setString(i++, info.getDegreeAndDiploma());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除教育信息
	public void delEducation(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_education where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkuserid查找教育信息
	public List<Education> findEducation(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<Education> list=null;
		try {
			String sql="select * from k_employment_education where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<Education>();
				}
				Education info=new Education();
				
				info.setId(rs.getString("id"));
				info.setSchoolType(rs.getString("schoolType"));
				info.setSchoolName(rs.getString("schoolName"));
				info.setStartTime(rs.getString("startTime"));
				info.setEndTime(rs.getString("endTime"));
				
				info.setMajor(rs.getString("major"));
				info.setDegreeAndDiploma(rs.getString("degreeAndDiploma"));
				info.setLinkUserId(linkUserId);
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	//插入工作经验信息
	public boolean addWorkHistory(WorkHistory info){
		PreparedStatement ps=null;
		boolean result = false;
		try {
			String sql="insert into k_employment_workhistory (startTime,endTime,company,position,major,supervisor,"
						+"salary,leaveReasons,linkUserId) values(?,?,?,?,?,?, ?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getStartTime());
			ps.setString(i++, info.getEndTime());
			ps.setString(i++, info.getCompany());
			ps.setString(i++, info.getPosition());
			ps.setString(i++, info.getMajor());
			ps.setString(i++, info.getSupervisor());
			
			ps.setString(i++, info.getSalary());
			ps.setString(i++, info.getLeaveReasons());
			ps.setString(i++, info.getLinkUserId());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新工作经验信息
	public void updateWorkHistory(WorkHistory info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_workhistory set startTime=?,endTime=?,company=?,position=?,major=?," 
						+"supervisor=?,salary=?,leaveReasons=?,linkUserId=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getStartTime());
			ps.setString(i++, info.getEndTime());
			ps.setString(i++, info.getCompany());
			ps.setString(i++, info.getPosition());
			ps.setString(i++, info.getMajor());
			ps.setString(i++, info.getSupervisor());
			
			ps.setString(i++, info.getSalary());
			ps.setString(i++, info.getLeaveReasons());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除工作经验信息
	public void delWorkHistory(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_workhistory where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkuserid查找工作经验信息
	public List<WorkHistory> findWorkHistory(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<WorkHistory> list=null;
		try {
			String sql="select * from k_employment_workhistory where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<WorkHistory>();
				}
				WorkHistory info=new WorkHistory();
				
				info.setId(rs.getString("id"));
				info.setStartTime(rs.getString("startTime"));
				info.setEndTime(rs.getString("endTime"));
				info.setCompany(rs.getString("company"));
				info.setPosition(rs.getString("position"));
				
				info.setMajor(rs.getString("major"));
				info.setSupervisor(rs.getString("supervisor"));
				info.setSalary(rs.getString("salary"));
				info.setLeaveReasons(rs.getString("leaveReasons"));
				info.setLinkUserId(linkUserId);
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	//插入证明人信息
	public boolean addReference(Reference info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_reference (name,company,relationship,occupation,tel,linkUserId"
						+") values(?,?,?,?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getName());
			ps.setString(i++, info.getCompany());
			ps.setString(i++, info.getRelationship());
			ps.setString(i++, info.getOccupation());
			ps.setString(i++, info.getTel());
			ps.setString(i++, info.getLinkUserId());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新证明人信息
	public void updateReference(Reference info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_reference set name=?,company=?,relationship=?,occupation=?,tel=?," 
						+"linkUserId=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getName());
			ps.setString(i++, info.getCompany());
			ps.setString(i++, info.getRelationship());
			ps.setString(i++, info.getOccupation());
			ps.setString(i++, info.getTel());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除证明人信息
	public void delReference(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_reference where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkuserid查找证明人信息
	public List<Reference> findReference(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<Reference> list=null;
		try {
			String sql="select * from k_employment_reference where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<Reference>();
				}
				Reference info=new Reference();
				
				info.setId(rs.getString("id"));
				info.setName(rs.getString("name"));
				info.setCompany(rs.getString("company"));
				info.setRelationship(rs.getString("relationship"));
				info.setOccupation(rs.getString("occupation"));
				
				info.setTel(rs.getString("tel"));
				info.setLinkUserId(linkUserId);
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	//插入技能信息
	public boolean addSkills(Skills info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_skills (motherTongue,foreignLanguage,computerSkills,special,linkUserId"
						+") values(?,?,?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getMotherTongue());
			ps.setString(i++, info.getForeignLanguage());
			ps.setString(i++, info.getComputerSkills());
			ps.setString(i++, info.getSpecial());
			ps.setString(i++, info.getLinkUserId());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新技能信息
	public void updateSkills(Skills info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_skills set motherTongue=?,foreignLanguage=?,computerSkills=?,special=?,linkUserId=?" 
						+" where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getMotherTongue());
			ps.setString(i++, info.getForeignLanguage());
			ps.setString(i++, info.getComputerSkills());
			ps.setString(i++, info.getSpecial());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除技能信息
	public void delSkills(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_skills where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据id查找技能信息
	public Skills findSkills(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		Skills info=null;
		try {
			String sql="select * from k_employment_skills where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				info=new Skills();
				
				info.setId(rs.getString("id"));
				info.setMotherTongue(rs.getString("motherTongue"));
				info.setForeignLanguage(rs.getString("foreignLanguage"));
				info.setComputerSkills(rs.getString("computerSkills"));
				info.setSpecial(rs.getString("special"));
				info.setLinkUserId(linkUserId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return info;
	}
	
	//插入家庭成员信息
	public boolean addFamilyInfo(FamilyInfo info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_familyinfo (linkUserId,name,age,relationship,occupation,company,"
						+"address,tel) values(?,?,?,?,?,?, ?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getName());
			ps.setString(i++, info.getAge());
			ps.setString(i++, info.getRelationship());
			ps.setString(i++, info.getOccupation());
			ps.setString(i++, info.getCompany());
			
			ps.setString(i++, info.getAddress());
			ps.setString(i++, info.getTel());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新家庭成员信息
	public void updateFamilyInfo(FamilyInfo info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_familyinfo set linkUserId=?,name=?,age=?,relationship=?,occupation=?," 
						+"company=?,address=?,tel=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getName());
			ps.setString(i++, info.getAge());
			ps.setString(i++, info.getRelationship());
			ps.setString(i++, info.getOccupation());
			ps.setString(i++, info.getCompany());
			
			ps.setString(i++, info.getAddress());
			ps.setString(i++, info.getTel());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除家庭成员信息
	public void delFamilyInfo(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_familyinfo where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkUserId查找家庭成员信息
	public List<FamilyInfo> findFamilyInfo(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<FamilyInfo> list=null;
		try {
			String sql="select * from k_employment_familyinfo where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<FamilyInfo>();
				}
				FamilyInfo info=new FamilyInfo();
				
				info.setId(rs.getString("id"));
				info.setLinkUserId(linkUserId);
				info.setName(rs.getString("name"));
				info.setAge(rs.getString("age"));
				info.setRelationship(rs.getString("relationship"));
				info.setOccupation(rs.getString("occupation"));
				
				info.setCompany(rs.getString("company"));
				info.setAddress(rs.getString("address"));
				info.setTel(rs.getString("tel"));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	//插入问题信息
	public boolean addQuestions(Questions info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_questions (linkUserId,honorsObtained,personality"
						+") values(?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getHonorsObtained());
			ps.setString(i++, info.getPersonality());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新问题信息
	public void updateQuestions(Questions info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_questions set linkUserId=?,honorsObtained=?,personality=?" 
						+" where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getHonorsObtained());
			ps.setString(i++, info.getPersonality());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除问题信息
	public void delQuestions(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_questions where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkUserId查找问题信息
	public Questions findQuestions(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		Questions info=null;
		try {
			String sql="select * from k_employment_questions where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				info=new Questions();
				
				info.setId(rs.getString("id"));
				info.setLinkUserId(linkUserId);
				info.setHonorsObtained(rs.getString("honorsObtained"));
				info.setPersonality(rs.getString("personality"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return info;
	}
	
	//根据身份证和名称查找个人信息id
	public String getIdByIdNo(String chineseName,String idNo){
		String id=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select id from k_employment_personalinfo where chineseName=? and idno=?";
			ps=this.conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, chineseName);
			ps.setString(i++, idNo);
			rs=ps.executeQuery();
			if(rs.next()){
				id=rs.getString("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return id;
	}
	//插入培训信息
	public boolean addTrain(Train info){
		PreparedStatement ps=null;
		boolean result=false;
		try {
			String sql="insert into k_employment_train (training,trainingName,trainStartTime,trainEndTime,certificate,remarks,"
						+"linkUserId) " 
						+" values(?,?,?,?,?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getTraining());
			ps.setString(i++, info.getTrainingName());
			ps.setString(i++, info.getTrainStartTime());
			ps.setString(i++, info.getTrainEndTime());
			ps.setString(i++, info.getCertificate());
			ps.setString(i++, info.getRemarks());
			ps.setString(i++, info.getLinkUserId());
			ps.execute();
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	//更新教育信息
	public void updateTrain(Train info){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_train set training=?,trainingName=?,trainStartTime=?,trainEndTime=?,certificate=?," 
						+"remarks=?,linkUserId=? where id=?";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, info.getTraining());
			ps.setString(i++, info.getTrainingName());
			ps.setString(i++, info.getTrainStartTime());
			ps.setString(i++, info.getTrainEndTime());
			ps.setString(i++, info.getCertificate());
			
			ps.setString(i++, info.getRemarks());
			ps.setString(i++, info.getLinkUserId());
			ps.setString(i++, info.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//删除教育信息
	public void delTrain(String linkUserId){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_employment_education where linkUserId="+linkUserId;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//根据linkuserid查找教育信息
	public List<Train> findTrain(String linkUserId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<Train> list=null;
		try {
			String sql="select * from k_employment_education where linkUserId="+linkUserId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<Train>();
				}
				Train info=new Train();
				
				info.setId(rs.getString("id"));
				info.setTraining(rs.getString("training"));
				info.setTrainingName(rs.getString("trainingName"));
				info.setTrainStartTime(rs.getString("trainStartTime"));
				info.setTrainEndTime(rs.getString("trainEndTime"));
				
				info.setCertificate(rs.getString("certificate"));
				info.setRemarks(rs.getString("remarks"));
				info.setLinkUserId(linkUserId);
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	//批量修改状态
	public void updatePersonState(String state,String id){
		PreparedStatement ps=null;
		try {
			String sql="update k_employment_personalinfo set state = ? where id=?";
			int i = 1;
			ps=conn.prepareStatement(sql);
			ps.setString(i++, state);
			ps.setString(i++, id);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//判定是不是第一次申请
	public boolean isFirst(String id){
		boolean isFirst=false;
		PreparedStatement ps=null;
		ResultSet rs=null;
		String result=null;
		try {
			String sql="select submitdate from k_employment_personalinfo where id="+id;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				result = rs.getString("submitdate");
				if(result==null || "".equals(result)){
					isFirst = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return isFirst;
	}
	
	//更新个人信息表的标示是否第一次填写入职申请的字段--submitdate
	public void updateSubmitDate(String id){
		PreparedStatement ps=null;
		try {
			String sql = "update k_employment_personalinfo set submitdate = now() where id="+id;
			ps=conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//插入分发简历信息
	public void insertDistribution(String id,String userId){
		PreparedStatement ps=null;
		try {
//			String sql = "insert into k_employment_distribution (employmentid,userid,createTime,type) values(?,?,now(),convert('授权' using gbk))";
			String sql = "insert into k_employment_distribution (employmentid,userid,createTime,type) values(?,?,now(),?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, id);
			ps.setString(i++, userId);
			ps.setString(i++, "授权");
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//插入简历日志信息
	public void insertResumeState(String id,String userId){
		PreparedStatement ps=null;
		try {
			String sql = "insert into k_employment_distribution (employmentid,userid,createTime,state) values(?,?,now(),?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, id);
			ps.setString(i++, userId);
			ps.setString(i++, "日志");
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	//区域树
		public List getArea(UserSession userSession,String organid,String checked)throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			List list = null;
			
			String sql = "";
			
			try {
				KDepartmentVO departmentVO=dbUtil.load(KDepartmentVO.class,Integer.parseInt(userSession.getUserAuditDepartmentId()));
				int ii = 0; //区域树无值，返回"";
				list = new ArrayList();
				
				sql = "select * from k_department where parentid = ? and areaid=? and ifnull(areaid,'') = '' limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, organid);
				ps.setString(2, departmentVO.getAreaid());
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
				sql = "select * from k_area where organid = ? and  autoid=? order by orderid,autoid";
				ps = conn.prepareStatement(sql);
				ps.setString(1, organid);
				ps.setInt(2,Integer.parseInt(userSession.getAreaid()) );
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
	
	
}
