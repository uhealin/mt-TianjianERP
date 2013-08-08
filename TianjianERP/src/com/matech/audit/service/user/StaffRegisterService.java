package com.matech.audit.service.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

//import antlr.collections.List;

import com.matech.audit.service.user.model.StaffJobIntro;
import com.matech.audit.service.user.model.StaffLiaison;
import com.matech.audit.service.user.model.StaffLiaisonFamily;
import com.matech.audit.service.user.model.StaffPost;
import com.matech.audit.service.user.model.StaffPractice;
import com.matech.audit.service.user.model.StaffRegister;
import com.matech.audit.service.user.model.StaffSocialseCurity;
import com.matech.audit.service.user.model.StaffSocialseCurityHp;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class StaffRegisterService {
	
	Connection conn = null;
	
	public StaffRegisterService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 新增 员工报到信息 返回Id
	 * @param sr
	 * @return
	 */
	public String addStaffRegister (StaffRegister sr){
		String id = "";
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffregister` \n" +
					 "(`Name`,`loginid`,`Password`,`Sex`,`BornDate`,`Educational`,`Diploma`,\n" +
					 "`DepartID`,`Rank`,`Post`,`Specialty`,`ParentGroup`,`Popedom`,`IsTips`,`departmentid`,\n" +
					 "`ProjectPopedom`,`clientDogSysUi`,`state`,`userPhoto`,`userPhotoTemp`,`mobilePhone`, \n" +
					 "`phone`,`email`,`cpano`,`floor`,`house`,`station`,`identityCard`,`paperstype`, \n" +
					 "`papersnumber`,`nation`,`marriage`,`category`,`place`,`residence`,`politics`, \n" +
					 "`partytime`,`relationships`,`profession`,`compact`,`workstate`,`leavetype`, \n" +
					 "`english`,`diplomatime`,`entrytime`,`ip`,`forbiddenDate`,`resume`,`employWay`, \n" +
					 "`referrer`,`contactWay`,`cpa`,`examSubject`,`createDate`,`createUser`,`createDepartment`) \n" +
					 "VALUES (?,?,?,?,?,?,?,?,?,?, \n" +
							 "?,?,?,?,?,?,?,?,?,?, \n" +
							 "?,?,?,?,?,?,?,?,?,?, \n" +
							 "?,?,?,?,?,?,?,?,?,?, \n" +
							 "?,?,?,?,?,?,?,?,?,?,\n" +
							 "?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			ps.setString(i++, sr.getName());
			ps.setString(i++, sr.getLoginid());
			ps.setString(i++, sr.getPassword());
			ps.setString(i++, sr.getSex());
			ps.setString(i++, sr.getBornDate());
			ps.setString(i++, sr.getEducational());
			ps.setString(i++, sr.getDiploma());
			ps.setString(i++, sr.getDepartID());
			ps.setString(i++, sr.getRank());
			ps.setString(i++, sr.getPost());
			
			ps.setString(i++, sr.getSpecialty());
			ps.setString(i++, sr.getParentGroup());
			ps.setString(i++, sr.getPopedom());
			ps.setString(i++, sr.getIsTips());
			ps.setString(i++, sr.getDepartmentid());
			ps.setString(i++, sr.getProjectPopedom());
			ps.setString(i++, sr.getClientDogSysUi());
			ps.setString(i++, sr.getState());
			ps.setString(i++, sr.getUserPhoto());
			ps.setString(i++, sr.getUserPhotoTemp());
			
			ps.setString(i++, sr.getMobilePhone());
			ps.setString(i++, sr.getPhone());
			ps.setString(i++, sr.getEmail());
			ps.setString(i++, sr.getCpa());
			ps.setString(i++, sr.getFloor());
			ps.setString(i++, sr.getHouse());
			ps.setString(i++, sr.getStation());
			ps.setString(i++, sr.getIdentityCard());
			ps.setString(i++, sr.getPaperstype());
			ps.setString(i++, sr.getPapersnumber());
			
			ps.setString(i++, sr.getNation());
			ps.setString(i++, sr.getMarriage());
			ps.setString(i++, sr.getCategory());
			ps.setString(i++, sr.getPlace());
			ps.setString(i++, sr.getResidence());
			ps.setString(i++, sr.getPolitics());
			ps.setString(i++, sr.getPartytime());
			ps.setString(i++, sr.getRelationships());
			ps.setString(i++, sr.getProfession());
			ps.setString(i++, sr.getCompact());
			
			ps.setString(i++, sr.getWorkstate());
			ps.setString(i++, sr.getLeavetype());
			ps.setString(i++, sr.getEnglish());
			ps.setString(i++, sr.getDiplomatime());
			ps.setString(i++, sr.getEntrytime());
			ps.setString(i++, sr.getIp());
			ps.setString(i++, sr.getForbiddenDate());
			ps.setString(i++, sr.getResume());
			ps.setString(i++, sr.getEmployWay());
			ps.setString(i++, sr.getReferrer());
			
			ps.setString(i++, sr.getContactWay());
			ps.setString(i++, sr.getCpa());
			ps.setString(i++, sr.getExamSubject());
			ps.setString(i++, sr.getCreateDate());
			ps.setString(i++, sr.getCreateUser());
			ps.setString(i++, sr.getCreateDepartment());
			
			int result = ps.executeUpdate();
			
			if(result>0){
				
				DbUtil.close(ps);
				
				ps = conn.prepareStatement("SELECT  id FROM k_staffregister WHERE NAME=? AND identityCard=?");
				ps.setString(1, sr.getName());
				ps.setString(2, sr.getIdentityCard());
				
				ResultSet pSet = ps.executeQuery();
				
				if(pSet.next()){
					id =  pSet.getString("id");
				}
				
				DbUtil.close(pSet);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return id;
	}
	
	/**
	 * 新增 员工报到(社保信息表)
	 * @param ssc
	 * @return
	 */
	public int addStaffSocialseCurity (StaffSocialseCurity ssc){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffsocialsecurity` \n" +
					 "(`id`,`paySort`,`insuredSort`,`firstJobTime`,`baseNumber`, \n" +
					 "`residencePermit`,`property`,`createDate`,`createUser`,`createDepartment`) \n" +
					 "VALUES (?,?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, ssc.getId());
			ps.setString(i++, ssc.getPaySort());
			ps.setString(i++, ssc.getInsuredSort());
			ps.setString(i++, ssc.getFirstJobTime());
			ps.setString(i++, ssc.getBaseNumber());
			ps.setString(i++, ssc.getResidencePermit());
			ps.setString(i++, ssc.getProperty());
			ps.setString(i++, ssc.getCreateDate());
			ps.setString(i++, ssc.getCreateUser());
			ps.setString(i++, ssc.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到(社保医疗信息 stsoseId 外键)
	 * @param ssch
	 * @return
	 */
	public int addStaffSocialseCurityHp (StaffSocialseCurityHp ssch){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffsocialsecurityhp` \n" +
					"(`id`,`stsoseId`,`organizationNames`,`coding`,`hospitalName`,`property`,`createDate`,`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, ssch.getId());
			ps.setString(i++, ssch.getStsoseId());
			ps.setString(i++, ssch.getOrganizationNames());
			ps.setString(i++, ssch.getCoding());
			ps.setString(i++, ssch.getHospitalName());
			ps.setString(i++, ssch.getProperty());
			ps.setString(i++, ssch.getCreateDate());
			ps.setString(i++, ssch.getCreateUser());
			ps.setString(i++, ssch.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到( 执 业 信 息
	 * @param sp
	 * @return
	 */
	public int addStaffPractice (StaffPractice sp){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffpractice` \n" +
				  	 "(`id`,`spname`,`rank`,`cNumber`,`ratifyOrgan`,\n" +
				  	 "`yearMax`,`referenceNumber`,`qualifiedCertificate`,`property`,`createDate`,`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, sp.getId());
			ps.setString(i++, sp.getSpname());
			ps.setString(i++, sp.getRank());
			ps.setString(i++, sp.getcNumber());
			ps.setString(i++, sp.getRatifyOrgan());
			ps.setString(i++, sp.getYearMax());
			ps.setString(i++, sp.getReferenceNumber());
			ps.setString(i++, sp.getQualifiedCertificate());
			ps.setString(i++, sp.getProperty());
			ps.setString(i++, sp.getCreateDate());
			ps.setString(i++, sp.getCreateUser());
			ps.setString(i++, sp.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到(执业信息 职称表)
	 * @param sp
	 * @return
	 */
	public int addStaffPost (StaffPost sp){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffpost` \n" +
					 "(`id`,`series`,`rankName`,`rankGrade`,`getDate`,`property`,`createDate`,`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, sp.getId());
			ps.setString(i++, sp.getSeries());
			ps.setString(i++, sp.getRankName());
			ps.setString(i++, sp.getRankGrade());
			ps.setString(i++, sp.getGetDate());
			ps.setString(i++, sp.getProperty());
			ps.setString(i++, sp.getCreateDate());
			ps.setString(i++, sp.getCreateUser());
			ps.setString(i++, sp.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到(员工联络卡)
	 * @param sl
	 * @return
	 */
	public int addStaffLiaison (StaffLiaison sl){
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffliaison` \n" +
					 "(`id`,`residence`,`policeSubstation`,`homeAddress`,`homePostcode`, \n" +
					 "`mailAddress`,`mailPostcode`,`homeTel`,`urgencyTel`,`msn`, \n" +
					 "`qq`,`archivesPlace`,`archivesId`,`property`,`createDate`, \n" +
					 "`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, sl.getId());
			ps.setString(i++, sl.getResidence());
			ps.setString(i++, sl.getPoliceSubstation());
			ps.setString(i++, sl.getHomeAddress());
			ps.setString(i++, sl.getHomePostcode());
			ps.setString(i++, sl.getMailAddress());
			ps.setString(i++, sl.getMailPostcode());
			ps.setString(i++, sl.getHomeTel());
			ps.setString(i++, sl.getUrgencyTel());
			ps.setString(i++, sl.getMsn());
			ps.setString(i++, sl.getQq());
			ps.setString(i++, sl.getArchivesPlace());
			ps.setString(i++, sl.getArchivesId());
			ps.setString(i++, sl.getProperty());
			ps.setString(i++, sl.getCreateDate());
			ps.setString(i++, sl.getCreateUser());
			ps.setString(i++, sl.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到(员工家庭情况)
	 * @param slf
	 * @return
	 */
	public int addStaffLiaisonFamily (StaffLiaisonFamily slf){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffliaisonfamily`" +
					 "(`id`,`familyName`,`relation`,`jobPlace`,`tel`,`identityCard`,`property`,`createDate`,`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, slf.getId());
			ps.setString(i++, slf.getFamilyName());
			ps.setString(i++, slf.getRelation());
			ps.setString(i++, slf.getJobPlace());
			ps.setString(i++, slf.getTel());
			ps.setString(i++, slf.getIdentityCard());
			ps.setString(i++, slf.getProperty());
			ps.setString(i++, slf.getCreateDate());
			ps.setString(i++, slf.getCreateUser());
			ps.setString(i++, slf.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 新增 员工报到(学习、工作简历介绍)
	 * @param sji
	 * @return
	 */
	public int addStaffJobIntro (StaffJobIntro sji){
		
		int result = 0;
		PreparedStatement ps = null;
		String sql = "INSERT INTO `asdb`.`k_staffjobintro`" +
					 "(`id`,`ctype`,`startDate`,`endDate`,`content`,`property`,`createDate`,`createUser`,`createDepartment`)\n" +
					 "VALUES (?,?,?,?,?,?,?,?,?); \n";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			
			ps.setString(i++, sji.getId());
			ps.setString(i++, sji.getCtype());
			ps.setString(i++, sji.getStartDate());
			ps.setString(i++, sji.getEndDate());
			ps.setString(i++, sji.getContent());
			ps.setString(i++, sji.getProperty());
			ps.setString(i++, sji.getCreateDate());
			ps.setString(i++, sji.getCreateUser());
			ps.setString(i++, sji.getCreateDepartment());
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}
	
	/**
	 * 删除数据
	 * @param table 表名
	 * @param id  主键
	 * @return
	 */
	public int del (String table,String id){
		int result = 0;
		PreparedStatement ps = null;
		String sql = " delete from "+table+" where id='"+id+"' ";
		try {
			ps = conn.prepareStatement(sql);
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return result;
	}

	/**
	 * 根据ID和TABLE返回LIST map
	 * @param table 表名
	 * @param id  主键
	 * @return
	 */
	public List getTableMap(String table,String id){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = (List) new ArrayList();

		ASFuntion CHF = new ASFuntion();
		 
		String sql = " select * from "+table+" where id='"+id+"' ";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rmd = rs.getMetaData();
			
			while (rs.next()) {
				Map<String,String> map = new HashMap<String, String>();
				
				for (int j = 1; j <= rmd.getColumnCount(); j++) {
					map.put(rmd.getColumnName(j).toLowerCase(),CHF.showNull(rs.getString(rmd.getColumnName(j))));
				}
				
				list.add(map);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return list;
	}
	
	/**
	 * 返回员工信息对象
	 * @param id
	 * @return
	 */
	public StaffRegister getStaffRegister(String id){
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		StaffRegister sr = new StaffRegister();
		
		String sql = " select * from k_StaffRegister where id='"+id+"' ";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				sr.setName(rs.getString("name"));
				sr.setLoginid(rs.getString("loginid"));
				sr.setPassword(rs.getString("password"));
				sr.setSex(rs.getString("sex"));
				sr.setBornDate(rs.getString("borndate"));
				sr.setEducational(rs.getString("educational"));
				sr.setDiploma(rs.getString("diploma"));
				sr.setDepartID(rs.getString("departid"));
				sr.setRank(rs.getString("rank"));
				sr.setPost(rs.getString("post"));
				
				sr.setSpecialty(rs.getString("specialty"));
				sr.setParentGroup(rs.getString("parentgroup"));
				sr.setPopedom(rs.getString("popedom"));
				sr.setIsTips(rs.getString("istips"));
				sr.setDepartmentid(rs.getString("departmentid"));
				sr.setProjectPopedom(rs.getString("projectpopedom"));
				sr.setClientDogSysUi(rs.getString("clientdogsysui"));
				sr.setState(rs.getString("state"));
				sr.setUserPhoto(rs.getString("userphoto"));
				sr.setUserPhotoTemp(rs.getString("userphototemp"));
				
				sr.setMobilePhone(rs.getString("mobilephone"));
				sr.setPhone(rs.getString("phone"));
				sr.setEmail(rs.getString("email"));
				sr.setCpa(rs.getString("cpa"));
				sr.setFloor(rs.getString("floor"));
				sr.setHouse(rs.getString("house"));
				sr.setStation(rs.getString("station"));
				sr.setIdentityCard(rs.getString("identitycard"));
				sr.setPaperstype(rs.getString("paperstype"));
				sr.setPapersnumber(rs.getString("papersnumber"));
				
				sr.setNation(rs.getString("nation"));
				sr.setMarriage(rs.getString("marriage"));
				sr.setCategory(rs.getString("category"));
				sr.setPlace(rs.getString("place"));
				sr.setResidence(rs.getString("residence"));
				sr.setPolitics(rs.getString("politics"));
				sr.setPartytime(rs.getString("partytime"));
				sr.setRelationships(rs.getString("relationships"));
				sr.setProfession(rs.getString("profession"));
				sr.setCompact(rs.getString("compact"));
				
				sr.setWorkstate(rs.getString("workstate"));
				sr.setLeavetype(rs.getString("leavetype"));
				sr.setEnglish(rs.getString("english"));
				sr.setDiplomatime(rs.getString("diplomatime"));
				sr.setEntrytime(rs.getString("entrytime"));
				sr.setIp(rs.getString("ip"));
				sr.setForbiddenDate(rs.getString("forbiddendate"));
				sr.setResume(rs.getString("resume"));
				sr.setEmployWay(rs.getString("employway"));
				sr.setReferrer(rs.getString("referrer"));
				
				sr.setContactWay(rs.getString("contactway"));
				sr.setCpa(rs.getString("cpa"));
				sr.setExamSubject(rs.getString("examsubject"));
				sr.setCreateDate(rs.getString("createdate"));
				sr.setCreateUser(rs.getString("createuser"));
				sr.setCreateDepartment(rs.getString("createdepartment"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return sr;
	}

	/**
	 * 修改 员工信息对
	 * @param sr
	 * @return
	 */
	public int updteStaffRegister (StaffRegister sr){
		int id = 0;
		PreparedStatement ps = null;
		String sql = "UPDATE `asdb`.`k_staffregister` \n" +
					 "SET  \n" +
					 "`Name` = ?,`loginid` = ?,`Sex` = ?,`BornDate` = ?,`Educational` = ?,\n" +
					 "`Diploma` = ?,`DepartID` = ?,`Rank` = ?,`Post` = ?,`Specialty` = ?, \n" +
					 "`ParentGroup` = ?,`Popedom` = ?,`IsTips` = ?,`departmentid` = ?,`ProjectPopedom` = ?, \n" +
					 "`clientDogSysUi` = ?,`state` = ?,`userPhoto` = ?,`userPhotoTemp` = ?,`mobilePhone` = ?, \n" +
					 "`phone` = ?,`email` = ?,`cpano` = ?,`floor` = ?,`house` = ?, \n" +
					 "`station` = ?,`identityCard` = ?,`paperstype` = ?,`papersnumber` = ?,`nation` = ?, \n" +
					 "`marriage` = ?,`category` = ?,`place` = ?,`residence` = ?,`politics` = ?, \n" +
					 "`partytime` = ?,`relationships` = ?,`profession` = ?,`compact` = ?,`workstate` = ?, \n" +
					 "`leavetype` = ?,`english` = ?,`diplomatime` = ?,`entrytime` = ?,`ip` = ?, \n" +
					 "`forbiddenDate` = ?,`resume` = ?,`employWay` = ?,`referrer` = ?,`contactWay` = ?," +
					 "`cpa` = ?,`examSubject` = ?" +
					 "WHERE `id` = ?;";
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);

			ps.setString(i++, sr.getName());
			ps.setString(i++, sr.getLoginid());
			ps.setString(i++, sr.getSex());
			ps.setString(i++, sr.getBornDate());
			ps.setString(i++, sr.getEducational());
			ps.setString(i++, sr.getDiploma());
			ps.setString(i++, sr.getDepartID());
			ps.setString(i++, sr.getRank());
			ps.setString(i++, sr.getPost());
			
			ps.setString(i++, sr.getSpecialty());
			ps.setString(i++, sr.getParentGroup());
			ps.setString(i++, sr.getPopedom());
			ps.setString(i++, sr.getIsTips());
			ps.setString(i++, sr.getDepartmentid());
			ps.setString(i++, sr.getProjectPopedom());
			ps.setString(i++, sr.getClientDogSysUi());
			ps.setString(i++, sr.getState());
			ps.setString(i++, sr.getUserPhoto());
			ps.setString(i++, sr.getUserPhotoTemp());
			
			ps.setString(i++, sr.getMobilePhone());
			ps.setString(i++, sr.getPhone());
			ps.setString(i++, sr.getEmail());
			ps.setString(i++, sr.getCpa());
			ps.setString(i++, sr.getFloor());
			ps.setString(i++, sr.getHouse());
			ps.setString(i++, sr.getStation());
			ps.setString(i++, sr.getIdentityCard());
			ps.setString(i++, sr.getPaperstype());
			ps.setString(i++, sr.getPapersnumber());
			
			ps.setString(i++, sr.getNation());
			ps.setString(i++, sr.getMarriage());
			ps.setString(i++, sr.getCategory());
			ps.setString(i++, sr.getPlace());
			ps.setString(i++, sr.getResidence());
			ps.setString(i++, sr.getPolitics());
			ps.setString(i++, sr.getPartytime());
			ps.setString(i++, sr.getRelationships());
			ps.setString(i++, sr.getProfession());
			ps.setString(i++, sr.getCompact());
			
			ps.setString(i++, sr.getWorkstate());
			ps.setString(i++, sr.getLeavetype());
			ps.setString(i++, sr.getEnglish());
			ps.setString(i++, sr.getDiplomatime());
			ps.setString(i++, sr.getEntrytime());
			ps.setString(i++, sr.getIp());
			ps.setString(i++, sr.getForbiddenDate());
			ps.setString(i++, sr.getResume());
			ps.setString(i++, sr.getEmployWay());
			ps.setString(i++, sr.getReferrer());
			
			ps.setString(i++, sr.getContactWay());
			ps.setString(i++, sr.getCpa());
			ps.setString(i++, sr.getExamSubject());
			ps.setString(i++, sr.getId());
			
			id = ps.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			
		}
		return id;
	}
	
	//根据姓名身份证号查找主键id
	public String getId(String name,String identityCard){
		String id = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select id from k_StaffRegister where name='"+name+"' and identityCard = '"+identityCard+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				id = rs.getString("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return id;
	}
	
	public List<StaffJobIntro> getStaffJobIntroList(String id){
		List<StaffJobIntro> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM k_staffJobIntro WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<StaffJobIntro>();
				}
				StaffJobIntro s = new StaffJobIntro();
				s.setAutoId(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setCtype(rs.getString("ctype"));
				s.setStartDate(rs.getString("startdate"));
				s.setEndDate(rs.getString("enddate"));
				s.setContent(rs.getString("content"));
				s.setProperty(rs.getString("property"));
				s.setCreateDate(rs.getString("createdate"));
				list.add(s);
			}
		} catch (Exception e) {
			
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public StaffLiaison getStaffLiaison(String id){
		StaffLiaison staffLiaison = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql ="SELECT * FROM k_staffLiaison WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				staffLiaison = new StaffLiaison();
				staffLiaison.setAutoId(rs.getString("autoid"));
				staffLiaison.setId(rs.getString("id"));
				staffLiaison.setResidence(rs.getString("residence"));
				staffLiaison.setPoliceSubstation(rs.getString("policeSubstation"));
				staffLiaison.setHomeAddress(rs.getString("homeAddress"));
				staffLiaison.setHomePostcode(rs.getString("homePostcode"));
				staffLiaison.setMailAddress(rs.getString("mailAddress"));
				staffLiaison.setMailPostcode(rs.getString("mailPostcode"));
				staffLiaison.setHomeTel(rs.getString("homeTel"));
				staffLiaison.setUrgencyTel(rs.getString("urgencyTel"));
				staffLiaison.setMsn(rs.getString("msn"));
				staffLiaison.setQq(rs.getString("qq"));
				staffLiaison.setArchivesPlace(rs.getString("archivesPlace"));
				staffLiaison.setArchivesId(rs.getString("archivesId"));
				staffLiaison.setProperty(rs.getString("property"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return staffLiaison;
	}
	
	public List<StaffLiaisonFamily> getStaffLiaisonFamily(String id){
		List<StaffLiaisonFamily> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="SELECT * FROM k_staffLiaisonFamily WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<StaffLiaisonFamily>();
				}
				StaffLiaisonFamily s = new StaffLiaisonFamily();
				s.setAutoId(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setFamilyName(rs.getString("familyName"));
				s.setRelation(rs.getString("relation"));
				s.setJobPlace(rs.getString("jobPlace"));
				s.setTel(rs.getString("tel"));
				s.setIdentityCard(rs.getString("identityCard"));
				s.setProperty(rs.getString("property"));
				s.setCreateDate(rs.getString("createdate"));
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public List<StaffPost> getStaffPost(String id){
		List<StaffPost> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="SELECT * FROM k_staffPost WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<StaffPost>();
				}
				StaffPost s = new StaffPost();
				s.setAutoId(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setSeries(rs.getString("series"));
				s.setRankName(rs.getString("rankName"));
				s.setRankGrade(rs.getString("rankGrade"));
				s.setGetDate(rs.getString("getDate"));
				s.setProperty(rs.getString("property"));
				s.setCreateDate(rs.getString("createdate"));
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public List<StaffPractice> getStaffPractice(String id){
		List<StaffPractice> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="SELECT * FROM k_staffPractice WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<StaffPractice>();
				}
				StaffPractice s = new StaffPractice();
				s.setAutoId(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setSpname(rs.getString("spname"));
				s.setRank(rs.getString("rank"));
				s.setcNumber(rs.getString("cNumber"));
				s.setRatifyOrgan(rs.getString("ratifyOrgan"));
				s.setYearMax(rs.getString("yearMax"));
				s.setReferenceNumber(rs.getString("referenceNumber"));
				s.setQualifiedCertificate(rs.getString("qualifiedCertificate"));
				s.setProperty(rs.getString("property"));
				s.setCreateDate(rs.getString("createdate"));
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public StaffSocialseCurity getStaffSocialseCurity(String id){
		StaffSocialseCurity s = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql ="SELECT * FROM k_staffSocialseCurity WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				s = new StaffSocialseCurity();
				s.setAutoId(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setPaySort(rs.getString("paySort"));
				s.setInsuredSort(rs.getString("insuredSort"));
				s.setFirstJobTime(rs.getString("firstJobTime"));
				s.setBaseNumber(rs.getString("baseNumber"));
				s.setResidencePermit(rs.getString("residencePermit"));
				s.setProperty(rs.getString("property"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return s;
	}
	
	public List<StaffSocialseCurityHp> getStaffSocialseCurityHp(String id){
		List<StaffSocialseCurityHp> list=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="SELECT * FROM k_staffSocialseCurityHp WHERE id='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<StaffSocialseCurityHp>();
				}
				StaffSocialseCurityHp s = new StaffSocialseCurityHp();
				s.setAutoid(rs.getString("autoid"));
				s.setId(rs.getString("id"));
				s.setStsoseId(rs.getString("stsoseId"));
				s.setOrganizationNames(rs.getString("organizationNames"));
				s.setCoding(rs.getString("coding"));
				s.setHospitalName(rs.getString("hospitalName"));
				s.setProperty(rs.getString("property"));
				s.setCreateDate(rs.getString("createdate"));
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
}
