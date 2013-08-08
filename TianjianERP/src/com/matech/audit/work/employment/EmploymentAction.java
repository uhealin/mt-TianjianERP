package com.matech.audit.work.employment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsonVerifier;
import net.sf.json.test.JSONAssert;
import net.sf.json.util.JSONStringer;
import net.sf.json.util.JSONUtils;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.employe.employeeService;
import com.matech.audit.service.employment.EmploymentService;
import com.matech.audit.service.employment.model.Education;
import com.matech.audit.service.employment.model.FamilyInfo;
import com.matech.audit.service.employment.model.Info;
import com.matech.audit.service.employment.model.PersonalInfo;
import com.matech.audit.service.employment.model.Questions;
import com.matech.audit.service.employment.model.Reference;
import com.matech.audit.service.employment.model.Skills;
import com.matech.audit.service.employment.model.Train;
import com.matech.audit.service.employment.model.UserQueryItemVO;
import com.matech.audit.service.employment.model.UserQueryVO;
import com.matech.audit.service.employment.model.WorkHistory;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.kdic.model.Dic;
import com.matech.audit.service.kdic.model.KDicVO;
import com.matech.audit.service.log.LogService; 
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.oa.foder.Foder;

import com.matech.audit.service.user.JobService;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.UserDetailsTree;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.work.employee.EmployeeAction;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.WebUtil;

public class EmploymentAction extends MultiActionController {
	
	
	
	private final String _information="employment/information.jsp";
	private final String _personal="employment/personal.jsp";
	private final String _list="employment/list.jsp";
	private final String _register="AS_SYSTEM/userRegister.jsp";
	private final String _view="employment/view.jsp";
	private final String _viewInfo="employment/viewInfo.jsp";
	private final String _distributionList="employment/distributionList.jsp";
	private final String _distributionHistory="employment/distributionHistory.jsp";
	
	public enum Jsp{
		nlist,ListEm; 
		
		public String getPath(){
			return MessageFormat.format("employment/{0}.jsp", this.name());
		}
	}
	
	/*
	 * 跳转到填写简历个人信息页面
	 */
	public ModelAndView addPersonal(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(_personal);
		return modelAndView;
	}
	
	/*
	 * 跳转到填写简历信息页面
	 */
	public ModelAndView addResume(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(_information);
		ASFuntion CHF=new ASFuntion();
		String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			EmploymentService es= new EmploymentService(conn);
			Info info =new Info();
			List<Education> educationList = new ArrayList<Education>();
			List<FamilyInfo> familyInfo = new ArrayList<FamilyInfo>();
			Questions questions = new Questions();
			List<Reference> referenceList = new ArrayList<Reference>();
			Skills skills = new Skills();
			List<Train> trainList = new ArrayList<Train>();
			List<WorkHistory> workHistoryList = new ArrayList<WorkHistory>();
			info = es.findInfo(linkUserId);
			educationList = es.findEducation(linkUserId);
			familyInfo = es.findFamilyInfo(linkUserId);
			questions = es.findQuestions(linkUserId);
			referenceList = es.findReference(linkUserId);
			skills = es.findSkills(linkUserId);
			trainList = es.findTrain(linkUserId);
			workHistoryList = es.findWorkHistory(linkUserId);
			modelAndView.addObject("linkUserId", linkUserId);
			modelAndView.addObject("info", info);
			modelAndView.addObject("educationList", educationList);
			modelAndView.addObject("familyInfo", familyInfo);
			modelAndView.addObject("questions", questions);
			modelAndView.addObject("referenceList", referenceList);
			modelAndView.addObject("skills", skills);
			modelAndView.addObject("trainList", trainList);
			modelAndView.addObject("workHistoryList", workHistoryList);
			String userPhotoSrc = "" ;
			String fileTempName = DELUnid.getNumUnid() ;
			userPhotoSrc = info.getPhotoTemp();
			if(userPhotoSrc == null || "".equals(userPhotoSrc)) {
				userPhotoSrc = "/images/noPhoto.gif" ;
			}else {
				if(userPhotoSrc.indexOf(".")>-1){
					fileTempName = userPhotoSrc.substring(0,userPhotoSrc.indexOf("."));
				}
				userPhotoSrc = "/userPhoto/"+userPhotoSrc ;
			}
			modelAndView.addObject("fileTempName", fileTempName);
			modelAndView.addObject("userPhotoSrc", userPhotoSrc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	/*
	 * 跳转到查看简历信息页面
	 */
	public ModelAndView lookResume(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(_viewInfo);
		ASFuntion CHF=new ASFuntion();
		String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			EmploymentService es= new EmploymentService(conn);
			Info info =new Info();
			List<Education> educationList = new ArrayList<Education>();
			List<FamilyInfo> familyInfo = new ArrayList<FamilyInfo>();
			Questions questions = new Questions();
			List<Reference> referenceList = new ArrayList<Reference>();
			Skills skills = new Skills();
			List<Train> trainList = new ArrayList<Train>();
			List<WorkHistory> workHistoryList = new ArrayList<WorkHistory>();
			info = es.findInfo(linkUserId);
			educationList = es.findEducation(linkUserId);
			familyInfo = es.findFamilyInfo(linkUserId);
			questions = es.findQuestions(linkUserId);
			referenceList = es.findReference(linkUserId);
			skills = es.findSkills(linkUserId);
			trainList = es.findTrain(linkUserId);
			workHistoryList = es.findWorkHistory(linkUserId);
			modelAndView.addObject("linkUserId", linkUserId);
			modelAndView.addObject("info", info);
			modelAndView.addObject("educationList", educationList);
			modelAndView.addObject("familyInfo", familyInfo);
			modelAndView.addObject("questions", questions);
			modelAndView.addObject("referenceList", referenceList);
			modelAndView.addObject("skills", skills);
			modelAndView.addObject("trainList", trainList);
			modelAndView.addObject("workHistoryList", workHistoryList);
			String userPhotoSrc = "" ;
			String fileTempName = DELUnid.getNumUnid() ;
			userPhotoSrc = info.getPhotoTemp();
			if(userPhotoSrc == null || "".equals(userPhotoSrc)) {
				userPhotoSrc = "/images/noPhoto.gif" ;
			}else {
				if(userPhotoSrc.indexOf(".")>-1){
					fileTempName = userPhotoSrc.substring(0,userPhotoSrc.indexOf("."));
				}
				userPhotoSrc = "/userPhoto/"+userPhotoSrc ;
			}
			modelAndView.addObject("fileTempName", fileTempName);
			modelAndView.addObject("userPhotoSrc", userPhotoSrc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//增加个人信息
	public ModelAndView savePersonal(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(_personal);
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			conn = new DBConnect().getConnect("");
			String chineseName=CHF.showNull(request.getParameter("chineseName"));
			String englishName=CHF.showNull(request.getParameter("englishName"));
			String gender=CHF.showNull(request.getParameter("gender"));
			String born=CHF.showNull(request.getParameter("born"));
			String place=CHF.showNull(request.getParameter("place"));
			String nowWorkPlace=CHF.showNull(request.getParameter("nowWorkPlace"));
			String nation=CHF.showNull(request.getParameter("nation"));
			String degree=CHF.showNull(request.getParameter("degree"));
			String residence=CHF.showNull(request.getParameter("residence"));
			String marital=CHF.showNull(request.getParameter("marital"));
			String mobile=CHF.showNull(request.getParameter("mobile"));
			String passport=CHF.showNull(request.getParameter("passport"));
			String idNo=CHF.showNull(request.getParameter("idNo"));
			String email=CHF.showNull(request.getParameter("email"));
			String qualifications=CHF.showNull(request.getParameter("qualifications"));
			String nationality=CHF.showNull(request.getParameter("nationality"));
			String permanentResidence=CHF.showNull(request.getParameter("permanentResidence"));
			String huKou=CHF.showNull(request.getParameter("huKou"));
			String relationships=CHF.showNull(request.getParameter("relationships"));
			String pregnant=CHF.showNull(request.getParameter("pregnant"));
			String id=CHF.showNull(request.getParameter("id"));
			
			PersonalInfo personal=new PersonalInfo();
			
			personal.setBorn(born);
			personal.setChineseName(chineseName);
			personal.setDegree(degree);
			personal.setEmail(email);
			personal.setEnglishName(englishName);
			personal.setGender(gender);
			personal.setHuKou(huKou);
			personal.setIdNo(idNo);
			personal.setMarital(marital);
			personal.setMobile(mobile);
			personal.setNation(nation);
			personal.setNationality(nationality);
			personal.setNowWorkPlace(nowWorkPlace);
			personal.setPassport(passport);
			personal.setPermanentResidence(permanentResidence);
			personal.setPlace(place);
			personal.setPregnant(pregnant);
			personal.setQualifications(qualifications);
			personal.setRelationships(relationships);
			personal.setResidence(residence);
			EmploymentService es=new EmploymentService(conn);
			if("".equals(id)){
				es.addPersonalInfo(personal);
				String autoId=es.getIdByIdNo(chineseName, idNo);
				personal.setId(autoId);
				modelAndView.addObject("saveTrue", "true");
			}else{
				personal.setId(id);
				es.updatePersonalInfo(personal);
				modelAndView.addObject("updateTrue", "true");
			}
			modelAndView.addObject("personal", personal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	//保存学校信息
	public ModelAndView saveEducation(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String[] schoolType=request.getParameterValues("schoolType");
			String[] schoolName=request.getParameterValues("schoolName");
			String[] startTimeEducation=request.getParameterValues("startTimeEducation");
			String[] endTimeEducation=request.getParameterValues("endTimeEducation");
			String[] majorList=request.getParameterValues("major");
			String[] degreeAndDiploma=request.getParameterValues("degreeAndDiploma");
			boolean[] list=new boolean[schoolType.length];
			EmploymentService es=new EmploymentService(conn);
			es.delEducation(linkUserId);
			for(int i=0;i<schoolType.length;i++){
				Education education=new Education();
				if(schoolType[i]!=null){
					String type=schoolType[i];
					education.setSchoolType(type);
				}
				if(schoolName[i]!=null){
					String name=schoolName[i];
					education.setSchoolName(name);
				}
				if(startTimeEducation[i]!=null){
					String startTime=startTimeEducation[i];
					education.setStartTime(startTime);
				}
				if(endTimeEducation[i]!=null){
					String endTime=endTimeEducation[i];
					education.setEndTime(endTime);
				}
				if(majorList[i]!=null){
					String major=majorList[i];
					education.setMajor(major);
				}
				if(degreeAndDiploma[i]!=null){
					String degree=degreeAndDiploma[i];
					education.setDegreeAndDiploma(degree);
				}
				education.setLinkUserId(linkUserId);
				boolean outcome=es.addEducation(education);
				list[i]=outcome;
			}
			boolean result=true;
			for(int i=0;i<list.length;i++){
				if(list[i]==false){
					result=false;
				}
			}
			if(result){
				script="<script>"+"alert('保存成功');"+"</script>";
			}else{
				script="<script>"+"alert('保存失败');"+"</script>";
			}
			response.getWriter().println(script);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//保存培训信息
	public ModelAndView saveTrain(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String[] training=request.getParameterValues("training");
			String[] trainingName=request.getParameterValues("trainingName");
			String[] trainStartTime=request.getParameterValues("trainStartTime");
			String[] trainEndTime=request.getParameterValues("trainEndTime");
			String[] certificateList=request.getParameterValues("certificate");
			String[] remarksList=request.getParameterValues("remarks");
			EmploymentService es=new EmploymentService(conn);
			es.delEducation(linkUserId);
			for(int i=0;i<training.length;i++){
				Train train=new Train();
				if(training[i]!=null){
					String trainType=training[i];
					train.setTraining(trainType);
				}
				if(trainingName[i]!=null){
					String name=trainingName[i];
					train.setTrainingName(name);
				}
				if(trainStartTime[i]!=null){
					String startTime=trainStartTime[i];
					train.setTrainStartTime(startTime);
				}
				if(trainEndTime[i]!=null){
					String endTime=trainEndTime[i];
					train.setTrainEndTime(endTime);
				}
				if(certificateList[i]!=null){
					String certificate=certificateList[i];
					train.setCertificate(certificate);
				}
				if(remarksList[i]!=null){
					String remarks=remarksList[i];
					train.setRemarks(remarks);
				}
				train.setLinkUserId(linkUserId);
				boolean result=es.addTrain(train);
				if(result){
					script="<script>"+"alert('保存成功');"+"</script>";
				}else{
					script="<script>"+"alert('保存失败');"+"</script>";
				}
				response.getWriter().println(script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//保存工作经验信息
	public ModelAndView saveWorkHistory(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String[] startTimeList=request.getParameterValues("startTimeWork");
			String[] endTimeList=request.getParameterValues("endTimeWork");
			String[] companyList=request.getParameterValues("companyWork");
			String[] positionList=request.getParameterValues("positionWork");
			String[] majorList=request.getParameterValues("majorWork");
			String[] supervisorList=request.getParameterValues("supervisor");
			String[] salaryList=request.getParameterValues("salaryWork");
			String[] leaveReasonsList=request.getParameterValues("leaveReasons");
			EmploymentService es=new EmploymentService(conn);
			es.delWorkHistory(linkUserId);
			for(int i=0;i<companyList.length;i++){
				WorkHistory workHistory=new WorkHistory();
				if(startTimeList[i]!=null){
					String startTime=startTimeList[i];
					workHistory.setStartTime(startTime);
				}
				if(endTimeList[i]!=null){
					String endTime=endTimeList[i];
					workHistory.setEndTime(endTime);
				}
				if(companyList[i]!=null){
					String company=companyList[i];
					workHistory.setCompany(company);
				}
				if(positionList[i]!=null){
					String position=positionList[i];
					workHistory.setPosition(position);
				}
				if(majorList[i]!=null){
					String major=majorList[i];
					workHistory.setMajor(major);
				}
				if(supervisorList[i]!=null){
					String supervisor=supervisorList[i];
					workHistory.setSupervisor(supervisor);
				}
				if(salaryList[i]!=null){
					String salary=salaryList[i];
					workHistory.setSalary(salary);
				}
				if(leaveReasonsList[i]!=null){
					String leaveReasons=leaveReasonsList[i];
					workHistory.setLeaveReasons(leaveReasons);
				}
				workHistory.setLinkUserId(linkUserId);
				boolean result=es.addWorkHistory(workHistory);
				if(result){
					script="<script>"+"alert('保存成功');"+"</script>";
				}else{
					script="<script>"+"alert('保存失败');"+"</script>";
				}
				response.getWriter().println(script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//添加证明人信息
	public ModelAndView saveReference(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String[] nameList=request.getParameterValues("nameReference");
			String[] companyList=request.getParameterValues("companyReference");
			String[] relationshipList=request.getParameterValues("relationshipReference");
			String[] occupationList=request.getParameterValues("occupationReference");
			String[] telList=request.getParameterValues("telReference");
			EmploymentService es=new EmploymentService(conn);
			es.delReference(linkUserId);
			for(int i=0;i<nameList.length;i++){
				Reference reference=new Reference();
				if(nameList[i]!=null){
					String name=nameList[i];
					reference.setName(name);
				}
				if(relationshipList[i]!=null){
					String relationship=relationshipList[i];
					reference.setRelationship(relationship);
				}
				if(companyList[i]!=null){
					String company=companyList[i];
					reference.setCompany(company);
				}
				if(occupationList[i]!=null){
					String occupation=occupationList[i];
					reference.setOccupation(occupation);
				}
				if(telList[i]!=null){
					String tel=telList[i];
					reference.setTel(tel);
				}
				reference.setLinkUserId(linkUserId);
				boolean result=es.addReference(reference);
				if(result){
					script="<script>"+"alert('保存成功');"+"</script>";
				}else{
					script="<script>"+"alert('保存失败');"+"</script>";
				}
				response.getWriter().println(script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//添加技能信息
	public ModelAndView saveSkills(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String motherTongue=CHF.showNull(request.getParameter("motherTongue"));
			String foreignLanguage=CHF.showNull(request.getParameter("foreignLanguage"));
			String computerSkills=CHF.showNull(request.getParameter("computerSkills"));
			String special=CHF.showNull(request.getParameter("special"));
			EmploymentService es=new EmploymentService(conn);
			es.delSkills(linkUserId);
			Skills skills=new Skills();
			skills.setMotherTongue(motherTongue);
			skills.setForeignLanguage(foreignLanguage);
			skills.setComputerSkills(computerSkills);
			skills.setSpecial(special);
			skills.setLinkUserId(linkUserId);
			boolean result=es.addSkills(skills);
			if(result){
				script="<script>"+"alert('保存成功');"+"</script>";
			}else{
				script="<script>"+"alert('保存失败');"+"</script>";
			}
			response.getWriter().println(script);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//添加家庭信息
	public ModelAndView saveFamilyInfo(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String[] nameList=request.getParameterValues("nameFamily");
			String[] ageList=request.getParameterValues("ageFamily");
			String[] relationshipList=request.getParameterValues("relationshipFamily");
			String[] occupationList=request.getParameterValues("occupationFamily");
			String[] companyList=request.getParameterValues("companyFamily");
			String[] addressList=request.getParameterValues("addressFamily");
			String[] telList=request.getParameterValues("telFamily");
			EmploymentService es=new EmploymentService(conn);
			es.delReference(linkUserId);
			for(int i=0;i<nameList.length;i++){
				FamilyInfo family=new FamilyInfo();
				if(nameList[i]!=null){
					String name=nameList[i];
					family.setName(name);
				}
				if(ageList[i]!=null){
					String age=ageList[i];
					family.setAge(age);
				}
				if(relationshipList[i]!=null){
					String relationship=relationshipList[i];
					family.setRelationship(relationship);
				}
				if(occupationList[i]!=null){
					String occupation=occupationList[i];
					family.setOccupation(occupation);
				}
				if(companyList[i]!=null){
					String company=companyList[i];
					family.setCompany(company);
				}
				if(addressList[i]!=null){
					String address=addressList[i];
					family.setAddress(address);
				}
				if(telList[i]!=null){
					String tel=telList[i];
					family.setTel(tel);
				}
				family.setLinkUserId(linkUserId);
				boolean result=es.addFamilyInfo(family);
				if(result){
					script="<script>"+"alert('保存成功');"+"</script>";
				}else{
					script="<script>"+"alert('保存失败');"+"</script>";
				}
				response.getWriter().println(script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//添加问题信息
	public ModelAndView saveQuestions(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String honorsObtained=CHF.showNull(request.getParameter("honorsObtained"));
			String personality=CHF.showNull(request.getParameter("personality"));
			EmploymentService es=new EmploymentService(conn);
			es.delQuestions(linkUserId);
			Questions questions=new Questions();
			questions.setHonorsObtained(honorsObtained);
			questions.setPersonality(personality);
			questions.setLinkUserId(linkUserId);
			boolean result=es.addQuestions(questions);
			if(result){
				script="<script>"+"alert('保存成功');"+"</script>";
			}else{
				script="<script>"+"alert('保存失败');"+"</script>";
			}
			response.getWriter().println(script);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//添加应聘信息
	public ModelAndView saveInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8") ;
			String script="";
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			String position=CHF.showNull(request.getParameter("position"));
			String secondChoice=CHF.showNull(request.getParameter("secondChoice"));
			String preferred=CHF.showNull(request.getParameter("preferred"));
			String secondWork=CHF.showNull(request.getParameter("secondWork"));
			String candidateSource=CHF.showNull(request.getParameter("candidateSource"));
			String recommend=CHF.showNull(request.getParameter("recommend"));
			String monthSalary=CHF.showNull(request.getParameter("monthSalary"));
			String others=CHF.showNull(request.getParameter("others"));
			String annualPackage=CHF.showNull(request.getParameter("annualPackage"));
			String availableWork=CHF.showNull(request.getParameter("availableWork"));
			String photo=CHF.showNull(request.getParameter("uploadFileName"));
			String photoTemp=CHF.showNull(request.getParameter("fileRondomName"));
			//String photo=CHF.showNull(request.getParameter("photo"));
			EmploymentService es=new EmploymentService(conn);
			es.delInfo(linkUserId);
			Info info=new Info();
			info.setPosition(position);
			info.setSecondChoice(secondChoice);
			info.setPreferred(preferred);
			info.setSecondWork(secondWork);
			info.setCandidateSource(candidateSource);
			info.setRecommend(recommend);
			info.setMonthSalary(monthSalary);
			info.setOthers(others);
			info.setAnnualPackage(annualPackage);
			info.setAvailableWork(availableWork);
			info.setPhoto(photo);
			info.setLinkUserId(linkUserId);
			info.setPhotoTemp(photoTemp);
			boolean result=es.addInfo(info);
			if(result){
				script="<script>"+"alert('保存成功');"+"</script>";
			}else{
				script="<script>"+"alert('保存失败');"+"</script>";
			}
			response.getWriter().println(script);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}
	//跳转到修改个人信息页面
	public ModelAndView editPersonal(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(_personal);
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String id=CHF.showNull(request.getParameter("id"));
			EmploymentService es=new EmploymentService(conn);
			PersonalInfo personal=new PersonalInfo();
			personal=es.findPersonalInfo(id);
			modelAndView.addObject("personal", personal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	//跳转到查看个人信息页面
	public ModelAndView lookPersonal(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(_view);
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String id=CHF.showNull(request.getParameter("id"));
			EmploymentService es=new EmploymentService(conn);
			PersonalInfo personal=new PersonalInfo();
			personal=es.findPersonalInfo(id);
			modelAndView.addObject("personal", personal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	//跳转到修改应聘信息页面
	public ModelAndView editInformation(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_information);
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String linkUserId=CHF.showNull(request.getParameter("linkUserId"));
			EmploymentService es=new EmploymentService(conn);
			List<Education> education =new ArrayList<Education>();
			List<FamilyInfo> familyInfo=new ArrayList<FamilyInfo>();
			Info info =new Info();
			Questions questions=new Questions();
			List<Reference> reference=new ArrayList<Reference>();
			Skills skills=new Skills();
			List<Train> train=new ArrayList<Train>();
			List<WorkHistory> workHistory=new ArrayList<WorkHistory>();
			education=es.findEducation(linkUserId);
			familyInfo=es.findFamilyInfo(linkUserId);
			info=es.findInfo(linkUserId);
			questions=es.findQuestions(linkUserId);
			reference=es.findReference(linkUserId);
			skills=es.findSkills(linkUserId);
			train=es.findTrain(linkUserId);
			workHistory=es.findWorkHistory(linkUserId);
			modelAndView.addObject("educationList", education);
			modelAndView.addObject("familyInfoList", familyInfo);
			modelAndView.addObject("info", info);
			modelAndView.addObject("fn", info.getPhoto());
			modelAndView.addObject("questions", questions);
			modelAndView.addObject("referenceList", reference);
			modelAndView.addObject("skills", skills);
			modelAndView.addObject("trainList", train);
			modelAndView.addObject("workHistoryList", workHistory);
			String userPhotoSrc = "" ;
			String fileTempName = DELUnid.getNumUnid() ;
			userPhotoSrc = info.getPhotoTemp();
			if(userPhotoSrc == null || "".equals(userPhotoSrc)) {
				userPhotoSrc = "/images/noPhoto.gif" ;
			}else {
				if(userPhotoSrc.indexOf(".")>-1){
					fileTempName = userPhotoSrc.substring(0,userPhotoSrc.indexOf("."));
				}
				userPhotoSrc = "/userPhoto/"+userPhotoSrc ;
			}
			modelAndView.addObject("fileTempName", fileTempName);
			modelAndView.addObject("userPhotoSrc", userPhotoSrc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	//简历列表
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView= new ModelAndView(_list);
		DataGridProperty pp =new DataGridProperty();
		pp.setTableID("employment");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("简历管理");
	    
	    String sql="select a.id,chinesename,gender,born,a.nation,degree,a.residence,marital,mobile,a.email,nationality,a.state,c.name as nowResumeUser from k_employment_personalinfo a"
	    			+" left join(select distinct employmentid,userid from k_employment_distribution order by createtime desc limit 1) b ON b.employmentid = a.id "
	    			+" left join k_user	c on b.userid = c.id "
	    			+" where 1=1 ${chineseName} ${degree} ${gender}";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("checkbox");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    pp.addColumn("中文名", "chinesename");
	    pp.addColumn("性别", "gender");
	    pp.addColumn("出生时间", "born");
	    pp.addColumn("民族", "nation");
	    pp.addColumn("学位", "degree");
	    pp.addColumn("户口所在地", "residence");
	    pp.addColumn("婚姻状况", "marital");
	    pp.addColumn("手机号码", "mobile");
	    pp.addColumn("邮箱", "email");
	    pp.addColumn("国籍", "nationality");
	    pp.addColumn("状态", "state");
	    pp.addColumn("当前简历分发人", "nowResumeUser");
	    
	    pp.addSqlWhere("chineseName", " and chinesename like '%${chineseName}%'");
	    pp.addSqlWhere("degree", " and degree = '${degree}'");
	    pp.addSqlWhere("gender", " and gender = '${gender}'");
	    
	    modelAndView.addObject("tableid", pp.getTableID());
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	
	public ModelAndView ListEm(HttpServletRequest request, HttpServletResponse response)  throws Exception{  

		ModelAndView mw=new ModelAndView(Jsp.ListEm.getPath());
		ASFuntion CHF = new ASFuntion();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		DbUtil dbUtil=null;
		try {
			String temp = com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("clientDog");
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			
			String tabOpt = "0";
								
			DataGridProperty pp = new DataGridProperty() {
				
				public void onSearch(javax.servlet.http.HttpSession session,
						javax.servlet.http.HttpServletRequest request,
						javax.servlet.http.HttpServletResponse response)
						throws Exception {

					String departmentid = this.getRequestValue("departmentid");
					if (departmentid!=null || !"".equals(departmentid)){
						departmentid=" and b.autoID like '%"+departmentid+"%' ";
					}
					
					String emtype = this.getRequestValue("emtype");
					if (emtype!=null || !"".equals(emtype)){
						emtype=" and a.emtype like '%"+emtype+"%' ";
					}
					
					this.setOrAddRequestValue("departmentid", departmentid);
					this.setOrAddRequestValue("emtype", emtype);
					
					
				}
			};

			pp.setTableID("user");
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			
			pp.setPrintEnable(true);	//关闭dg打印
			
			pp.setInputType("radio");

//			页面上的tr属性			 
//			pp.setTrActionProperty(true);
//			pp.setTrAction("style='cursor:hand;' onDBLclick='goSort(this);' myUserid='${ID}' ");
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String departments = new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "user");
			
			String strSql = "";
			String userid = userSession.getUserId();
			UserService us=new UserService(conn);
			
			strSql = "select * from( select distinct(a.ID),a.Name,a.loginid,a.pccpa_seqno,"
				+" case when Sex='M' or Sex='男' then '男' else '女' end Sex,"
				+" Educational,DepartName,Post ,istips,a.rank,a.departmentid, \n"
				+" a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard,\n "
				+" a.station,a.nation,a.diplomatime,a.entrytime,a.phone,b.property \n" 
				+" from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n "
				+" ${qryJoin_em} where 1=1 ${departmentid} ${emtype} ${qryWhere_em} ) a  ";	
			pp.setSQL(strSql);
			
			pp.setOrderBy_CH("property,pccpa_seqno,loginid");
			pp.setDirection("asc,asc,asc");
			pp.setColumnWidth("10,10,5,10,10,10,13") ;
			pp.setUseBufferGrid(false) ;//全选x`
			pp.setPrintColumnWidth("30,20,10,20,35,25,35,40,35,20,30,30,30,30,30,30,30,30,30,30");
			pp.setPrintSqlColumn("Name,loginid,Sex,Educational,DepartName,Post,rank,roles,printLoginInfo,BornDate,Diploma,Specialty,mobilePhone,email,identityCard,station,nation,diplomatime,entrytime,phone");
			pp.setPrintColumn("姓名`登录名`性别`学历`所属部门`岗位`职级`操作权限`闲置时间`出生年月`毕业院校`特长`手机号码`电子邮件`身份证号`工号`民族`毕业时间`入职时间`办公电话");
		 	pp.setPrintCharColumn("10`13`15`16`18`19`20");
		 	
		 	pp.addSqlWhere("qryWhere_em", "${qryWhere_em}");
		 	pp.addSqlWhere("qryJoin_em", "${qryJoin_em}");
		 	pp.addSqlWhere("departmentid", "${departmentid}");
		 	pp.addSqlWhere("emtype", "${emtype}");
			
			pp.addColumn("姓名", "Name");
			pp.addColumn("登录名", "loginid","hide");
			pp.addColumn("性别", "Sex","showCenter");
			pp.addColumn("学历", "Educational");
			pp.addColumn("所属部门", "DepartName");
			pp.addColumn("职级", "rank");
			pp.addColumn("生日","BornDate","hide");
			pp.addColumn("毕业院校","Diploma","hide");
			pp.addColumn("专长", "Specialty","hide");
			pp.addColumn("手机号码","mobilePhone");
			pp.addColumn("电子邮件", "email");
			pp.addColumn("证件号码", "identityCard");
			pp.addColumn("工号","station");
			pp.addColumn("民族","nation","hide");
			pp.addColumn("毕业时间", "diplomatime","hide");
			pp.addColumn("入职时间", "entrytime","hide");
			pp.addColumn("办公电话", "phone");
			
			String departmentid=request.getParameter("departmentid");
			String emtype=CHF.showNull(request.getParameter("emtype"));
			
			String queryId=request.getParameter("queryId");
			String jarrStr="[]";
			if(queryId!=null&&!queryId.isEmpty())
			{
				UserQueryVO userQueryVO=dbUtil.load(UserQueryVO.class, queryId);
				List<UserQueryItemVO> userQueryItemVOs=dbUtil.select(UserQueryItemVO.class, "select * from {0} where mainformid=? ", queryId);
				//mw.addObject("userQuery",userQueryVO);
				 jarrStr=JSONArray.fromObject(userQueryItemVOs).toString();
				departmentid=userQueryVO.getDepartmentid();
				emtype=userQueryVO.getEmtype();
			}
			mw.addObject("jarrQueryitems",jarrStr);
			mw.addObject("departmentid", departmentid);
			mw.addObject("emtype", emtype);
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "查询用户列表失败！", e);
			e.printStackTrace();
			throw e;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		} 
		
		
		return mw;
		
	}
	
	public ModelAndView delPersonal(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		ASFuntion CHF=new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String id=CHF.showNull(request.getParameter("id"));
			EmploymentService es= new EmploymentService(conn);
			es.delPersonalInfo(id);
			es.delInfo(id);
			es.delEducation(id);
			es.delFamilyInfo(id);
			es.delReference(id);
			es.delQuestions(id);
			es.delSkills(id);
			es.delTrain(id);
			es.delWorkHistory(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/employment.do?method=list");
		return modelAndView;
	}
	
	//批量修改状态
	public void updateJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF = new ASFuntion();
			String table = CHF.showNull(request.getParameter("table"));
			String unid = CHF.showNull(request.getParameter("unid"));
			String state = CHF.showNull(request.getParameter("state"));
			conn= new DBConnect().getConnect("");
			
			EmploymentService es = new EmploymentService(conn);
			if("k_employment_personalinfo".toLowerCase().equals(table.toLowerCase())){
				//简历
				//unid 是多条的
				String [] uuid = unid.split(",");
				for (int i = 0; i < uuid.length; i++) {
					es.updatePersonState(state, uuid[i]);
					es.insertResumeState(uuid[i], userSession.getUserId());
				}
			}
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write("OK");
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	/**
	 * ajax入职人是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAjaxIfJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			response.setContentType("text/html;charset=utf-8") ;
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			
			String name = CHF.showNull(request.getParameter("name")); //区分查看的权限
			String papersnumber = CHF.showNull(request.getParameter("papersnumber")); //表名
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			EmploymentService es = new EmploymentService(conn);
			String unid = "";
			String jobUnidAndDate = "";
			if(!"".equals(name) && !"".equals(papersnumber)){
				
				String sql = "SELECT id FROM k_employment_personalinfo WHERE `chinesename` LIKE '%"+name+"%' AND idno='"+papersnumber+"'";
				unid = js.getValueBySql(sql);
				
				//查询状态
				if(!"".equals(unid)){
					if(es.isFirst(unid)){
						sql = "SELECT state FROM k_employment_personalinfo WHERE id='"+unid+"' ";
						jobUnidAndDate = unid +"`@`"+js.getValueBySql(sql);
						sql = "SELECT submitDate FROM k_employment_personalinfo WHERE id='"+unid+"' ";
						String submitDate = CHF.showNull(js.getValueBySql(sql));
						if("".equals(submitDate)){
							submitDate = "job";
						} 
						jobUnidAndDate = jobUnidAndDate+"`@`"+submitDate;
					}else{
						jobUnidAndDate = "second";
					}
				}
			}
			 
			out.write(jobUnidAndDate) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 用ajax得到简历信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAjaxJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			response.setContentType("text/html;charset=utf-8") ;
			PrintWriter out = response.getWriter();
			
			String unid = CHF.showNull(request.getParameter("unid")); //表名
			
			conn= new DBConnect().getConnect("");
			
			EmploymentService es = new EmploymentService(conn);
			Map edit = new HashMap();
			
			Info info = new Info();
			PersonalInfo personal = new PersonalInfo();
			List<FamilyInfo> family = new ArrayList<FamilyInfo>();
			List<Education> education = new ArrayList<Education>();
			List<WorkHistory> workHistory = new ArrayList<WorkHistory>();
			info = es.findInfo(unid);
			personal = es.findPersonalInfo(unid);
			family = es.findFamilyInfo(unid);
			education = es.findEducation(unid);
			workHistory = es.findWorkHistory(unid);
			edit.put("info", info);
			edit.put("personal", personal);
			edit.put("family", family);
			edit.put("education", education);
			edit.put("workHistory", workHistory);
//			if(!"".equals(unid)){
//				
//				edit = js.get("k_resume", "unid", unid);
//			}
			
			String jsonStr = JSONArray.fromObject(edit).toString() ;
			out.write(jsonStr) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	//跳转到员工报到页面
	public ModelAndView goRegister(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_register);
		ASFuntion CHF = new ASFuntion();
		Connection conn=null;
		String id = CHF.showNull(request.getParameter("id"));
		try {
			conn= new DBConnect().getConnect("");
			EmploymentService es = new EmploymentService(conn);
			Info info = new Info();
			PersonalInfo personal = new PersonalInfo();
			List<FamilyInfo> family = new ArrayList<FamilyInfo>();
			List<Education> education = new ArrayList<Education>();
			List<WorkHistory> workHistory = new ArrayList<WorkHistory>();
			info = es.findInfo(id);
			personal = es.findPersonalInfo(id);
			family = es.findFamilyInfo(id);
			education = es.findEducation(id);
			workHistory = es.findWorkHistory(id);
			modelAndView.addObject("info", info);
			modelAndView.addObject("personal", personal);
			modelAndView.addObject("family", family);
			modelAndView.addObject("education", education);
			modelAndView.addObject("workHistory", workHistory);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public void uploadPhoto(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter out = null ;
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			response.setContentType("text/html;charset=UTF-8") ;
			out = response.getWriter() ;
			MyFileUpload myfileUpload = new MyFileUpload(request);
		
			Foder foder  = new Foder("",request);
			String path = foder.createFoder("userPhoto") ;
			String newPath = foder.createFoder("userPhoto") ;
				
			String uploadtemppath = myfileUpload.UploadFile(DELUnid.getNumUnid(),path) ;

			Map parameters = myfileUpload.getMap();
			String fileName = (String)parameters.get("filename") ;
			String fileTempName = (String)parameters.get("clientFilePath");
			//fileTempName = fileTempName + fileName.substring(fileName.indexOf("."),fileName.length()) ;
			
			if (fileName != null && !"".equals(fileName)) {

				String oldfile = uploadtemppath + fileName;
				String newfile = "";
				newfile = newPath + fileTempName;
				ManuFileService mfs = new ManuFileService(conn);
				File file = new File(newfile);
				
				if(file.exists()) {
					file.delete() ;
				}

				System.out.println("oldfile="+oldfile);
				System.out.println("newPath="+newPath);
				mfs.copyFile(new File(oldfile), file);
			}
			
			out.println("<script>window.parent.changePhoto('"+fileTempName+"','"+fileName+"');alert(\"上传相片成功!\");</script>");
		
		}catch (Exception e) {
			out.println("<script>alert(\"上传照片失败!\")</script>");
			e.printStackTrace();
		} finally {
			out.close() ;
			DbUtil.close(conn);
		}
	}
	
	public void deleteUpdatePhoto(HttpServletRequest request,
			HttpServletResponse response) {
		
		PrintWriter out = null ;
		Connection conn = null;
		PreparedStatement ps = null ;
		try {
			conn = new DBConnect().getConnect("");
			out = response.getWriter() ;
			ASFuntion asf = new ASFuntion() ;
			
			String id = asf.showNull(request.getParameter("id")) ;
			
			String sql = "update k_employmet_info set photo=?,photoTemp=? where uuid=?";
			
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, "") ;
			ps.setString(2,"") ;
			ps.setString(3,id) ;
			
			ps.execute() ;
			ps.execute("Flush tables");
			
			Foder foder  = new Foder("",request);
		
			String fileName = asf.showNull(request.getParameter("deleteName")) ;
			
			if (fileName != null && !"".equals(fileName)) {

				String newPath = foder.createFoder("userPhoto") ;
				String newfile = newPath + fileName;
				
				File file = new File(newfile);
				
				if(file.exists()) {
					
					if(file.delete()) {
						out.write("suc") ;
					}else {
						out.write("fail") ;
					}
				} else {
					 out.write("notExist") ;
				}
				
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close() ;
		}
	}
	
	public void deletePhoto(HttpServletRequest request,
			HttpServletResponse response) {
		
		PrintWriter out = null ;
		try {
			out = response.getWriter() ;

			ASFuntion asf = new ASFuntion() ;
			Foder foder  = new Foder("",request);
			
		
			String fileName = asf.showNull(request.getParameter("deleteName")) ;
			
			if (fileName != null && !"".equals(fileName)) {

				String newPath = foder.createFoder("userPhoto") ;
				String newfile = newPath + fileName;
				
				File file = new File(newfile);
				
				if(file.exists()) {
					
					if(file.delete()) {
						out.write("suc") ;
					}else {
						out.write("fail") ;
					}
				} else {
					 out.write("notExist") ;
				}
				
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close() ;
		}
	}
	
	/*
	 * 分发简历
	 */
	public ModelAndView addDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_list);
		ASFuntion CHF = new ASFuntion();
		Connection conn=null;
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			String act = CHF.showNull(request.getParameter("act"));
			conn = new DBConnect().getConnect("");
			String userId = CHF.showNull(request.getParameter("userId"));
			String ids = CHF.showNull(request.getParameter("id"));	//简历id
			String [] id =ids.split(",");
			EmploymentService es = new EmploymentService(conn);
			for(int i = 0;i<id.length;i++){
				es.insertDistribution(id[i], userId);
			}
			if(act.equals("stateDatagird")){
				modelAndView = new ModelAndView(_distributionList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/*
	 * 分发简历后能修改状态的那个人的datagird
	 */
	public ModelAndView stateDatagird(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_distributionList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp =new DataGridProperty();
		pp.setTableID("stateDatagird");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("简历审批管理");
	    
	    String sql="select distinct a.id,chinesename,gender,born,a.nation,degree,a.residence,marital,mobile,a.email,nationality,a.state,f.name as nowResumeUser from k_employment_personalinfo a "
	    			+" left join k_employment_distribution b on a.id=b.employmentid "
	    			+" left join k_user c on b.userid = c.id "
	    			+" left join (select employmentid,userid from k_employment_distribution order by createtime desc limit 1) d on d.employmentid= a.id "
	    			+" LEFT JOIN k_user f ON d.userid = f.id "
	    			+" where c.id = '"+userSession.getUserId()+"' and b.type='授权' ${chineseName} ${degree} ${gender}";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("checkbox");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    
	    pp.setOrderBy_CH("id") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("checkbox");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    pp.addColumn("中文名", "chinesename");
	    pp.addColumn("性别", "gender");
	    pp.addColumn("出生时间", "born");
	    pp.addColumn("民族", "nation");
	    pp.addColumn("学位", "degree");
	    pp.addColumn("户口所在地", "residence");
	    pp.addColumn("婚姻状况", "marital");
	    pp.addColumn("手机号码", "mobile");
	    pp.addColumn("邮箱", "email");
	    pp.addColumn("国籍", "nationality");
	    pp.addColumn("状态", "state");
	    pp.addColumn("当前简历分发人", "nowResumeUser");
	    
	    pp.addSqlWhere("chineseName", " and chinesename like '%${chineseName}%'");
	    pp.addSqlWhere("degree", " and degree = '${degree}'");
	    pp.addSqlWhere("gender", " and gender = '${gender}'");
	    
	    modelAndView.addObject("tableid", pp.getTableID());
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	public ModelAndView distributionHistory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_distributionHistory);
		ASFuntion CHF = new ASFuntion();
		String employmentId = CHF.showNull(request.getParameter("employmentId"));
		DataGridProperty pp =new DataGridProperty();
		pp.setTableID("distributionHistory");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("简历分发历史");
	    
	    String sql="select a.id,b.name,createTime FROM k_employment_distribution a "
	    			+" LEFT JOIN k_user b ON a.userid = b.id "
	    			+" WHERE employmentid ='"+employmentId+"' AND `type`='授权' ";
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("createtime") ;
	    pp.setDirection_CH("asc");
	    
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    
	    pp.addColumn("简历分发授权人", "name");
	    pp.addColumn("时间", "createTime");
	    pp.setColumnWidth("30,30");
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	public ModelAndView nlist(HttpServletRequest request, HttpServletResponse response)throws Exception{
		return new ModelAndView(Jsp.nlist.getPath());
	}
	
	public ModelAndView departTree(HttpServletRequest request, HttpServletResponse response)throws Exception{
	
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		DbUtil dbUtil=null;
		List<Dic> dicEmtype=new ArrayList<Dic>();
		try {
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String departname = CHF.showNull(request.getParameter("departname"));	
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			
			String userpopedom = CHF.showNull(request.getParameter("userpopedom"));	 //用于判断部门是否要加上选择框
			
			String loginid = CHF.showNull(request.getParameter("loginid")); //人员loginid
			String menuid = CHF.showNull(request.getParameter("omenuid")); //菜单ID	
			
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树
			
			System.out.println(addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			dicEmtype=dbUtil.select(Dic.class, "select * from k_dic where ctype=?", "人员库");
			DepartmentService ds = new DepartmentService(conn);
			UserPopedomService up = new UserPopedomService(conn);
			String departments = up.getLoginIdPopedom(loginid, menuid);
			
			ds.setAddUser(addUser); //追加人员树 addUser = "addUser"; 
			
			List list = null;
			if("".equals(isSubject) || "undefined".equals(isSubject)) {
				list = ds.getOrgan(checked);	
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					departid = "555555";
					list = ds.getDepartment(departid, areaid, checked);
					
				}
			}else{
				if("1".equals(isSubject)){ 
					//如果是1，就表示当前节目是单位，要展开区域
					//1、区域表有值，要展开
					//2、区域表无值，直接展开部门表
					list = ds.getArea(departid,checked);
					if(list == null){
						//区域表无值，直接展开部门表
						if("userpopedom".equals(userpopedom)){
							checked = "false";
							ds.setUserpopedom(departments);
						}
						list = ds.getDepartment(departid, areaid, checked);
						if("true".equals(addUser)){
							List list1 = ds.getUser(departid, checked);
							if(list1 != null){
								if(list == null) list = new ArrayList();
								for(int i = 0;i<list1.size(); i++){
									list.add(list1.get(i));
								}
							}
						}
					}
				}else if("3".equals(isSubject)){
					List<Map> emtypes=new ArrayList<Map>();
					for(Dic dic:dicEmtype){
						Map<String, Object> map=new HashMap<String, Object>();
						map.put("leaf", true);
						map.put("isSubject","4");
						map.put("id", departid+"_"+dic.getValue());
						map.put("text",dic.getName());
						emtypes.add(map);
					}
					list=emtypes;
					
				}else{
					//都是展开部门
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked,false);
					//list.add(dicEmtype);
					if("true".equals(addUser)){
						List list1 = ds.getUser(departid, checked);
						if(list1 != null){
							if(list == null) list = new ArrayList();
							for(int i = 0;i<list1.size(); i++){
								System.out.println(list1.get(i));
								list.add(list1.get(i));
							}
						}
						
					}
				}
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
				
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	
	}
	
	
	public ModelAndView emTree(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		DbUtil dbUtil=null;
		List<KDicVO> dicEmtype=new ArrayList<KDicVO>();
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		try {
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String departname = CHF.showNull(request.getParameter("departname"));	
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			
			String userpopedom = CHF.showNull(request.getParameter("userpopedom"));	 //用于判断部门是否要加上选择框
			
			String loginid = CHF.showNull(request.getParameter("loginid")); //人员loginid
			String menuid = CHF.showNull(request.getParameter("omenuid")); //菜单ID	
			
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树
			
			String emtype = CHF.showNull(request.getParameter("emtype"));
			
			System.out.println(addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			
			DepartmentService ds = new DepartmentService(conn);
			EmploymentService employmentService=new EmploymentService(conn);
			UserPopedomService up = new UserPopedomService(conn);
			String departments = up.getLoginIdPopedom(loginid, menuid);
			
			ds.setAddUser(addUser); //追加人员树 addUser = "addUser"; 
			
			List list = new ArrayList();
			if("".equals(isSubject) || "undefined".equals(isSubject)) {
				//dicEmtype=dbUtil.select(Dic.class, "select * from {0} where ctype=?", "人员库");
				//Map<String, Object> emtype_root=new HashMap<String, Object>();
				//emtype_root.put("text","人员库");
				//emtype_root.put("isSubject", "emtype_root");
				//emtype_root.put("leaf", false);
				//list.add(emtype_root);
                dicEmtype=dbUtil.select(KDicVO.class, "select * from {0} where ctype=?", "人员库");
				
				for(KDicVO dic :dicEmtype){
					Map<String,Object> m=new HashMap<String, Object>();
					m.put("id", dic.getValue());
					m.put("text", dic.getName());
					m.put("isSubject", "emtype");
					m.put("leaf", false);
					m.put("emtype",dic.getValue());
					list.add(m);
				}
				Map<String, Object> subset_root=new HashMap<String, Object>();
				subset_root.put("text","子集");
				subset_root.put("isSubject", "subset_root");
				subset_root.put("leaf", false);
				list.add(subset_root);
				Map<String, Object> favsearch_root=new HashMap<String, Object>();
				favsearch_root.put("text","我的查询");
				favsearch_root.put("isSubject", "usersearch_root");
				favsearch_root.put("leaf", false);
				list.add(favsearch_root);
				
			}else if("usersearch_root".equals(isSubject)){
				List<UserQueryVO> queryVOs=dbUtil.select(UserQueryVO.class, "select * from {0} where userid=? ",userSession.getUserId()) ;
			    for(UserQueryVO queryVO :queryVOs){
					Map<String,Object> m=new HashMap<String, Object>();
					m.put("id", queryVO.getUuid());
					m.put("text", queryVO.getName());
					m.put("isSubject", "usersearch");
					m.put("leaf", true);
					
					list.add(m);
			    }
			}
			else if("emtype_root".equals(isSubject)){
				dicEmtype=dbUtil.select(KDicVO.class, "select * from {0} where ctype=?", "人员库");
				
				for(KDicVO dic :dicEmtype){
					Map<String,Object> m=new HashMap<String, Object>();
					m.put("id", dic.getValue());
					m.put("text", dic.getName());
					m.put("isSubject", "emtype");
					m.put("leaf", false);
					m.put("emtype",dic.getValue());
					list.add(m);
				}
			}else if("subset_root".equals(isSubject)){
				/*
				List<FormVO> formVOs=dbUtil.select(FormVO.class, "select * from {0} where form_type=?",
				"c4cf9647-e925-4f02-9656-f4d3ab3c75b7");
				
				for(FormVO formVO :formVOs){
					Map<String, Object> item=new HashMap<String, Object>();
					item.put("id", formVO.getTABLENAME());
					item.put("text", formVO.getNAME());
					item.put("isSubject", "item");
					item.put("leaf", false);
					
					list.add(item);
				}
				//List<UserDetailsTree> userDetailsTrees=dbUtil.select(UserDetailsTree.class, "select * from {0} where isshow=? order by orderby asc", 1);
				for(UserDetailsTree userDetailsTree :userDetailsTrees){
			    	
			    	Map<String, Object> item=new HashMap<String, Object>();
					item.put("id", userDetailsTree.getId());
					item.put("text", userDetailsTree.getText());
					item.put("isSubject", "item");
					item.put("leaf", false);
					item.put("emtype", emtype);
					item.put("url", userDetailsTree.getUrl());
					list.add(item);
			    }
				*/
			    List<FormVO> formVOs=dbUtil.select(FormVO.class, "select * from {0} where form_type=?",EmploymentService.UUID_FORM_TYPE_SUBSET);
				for(FormVO formVO :formVOs){
			    	
			    	Map<String, Object> item=new HashMap<String, Object>();
					item.put("id", formVO.getUUID());
					item.put("text", formVO.getNAME());
					item.put("isSubject", "item");
					item.put("leaf", true);
					item.put("emtype", emtype);
					item.put("subsetid",formVO.getUUID());
					item.put("url", MessageFormat.format("formDefine.do?method=formListView&uuid={0}", formVO.getUUID()));
					list.add(item);
			    }
			}
			else if("emtype".equals(isSubject)||"item".equals(isSubject)){ 
				list = ds.getOrgan(checked);	
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					departid = "555555";
					list = ds.getDepartment(departid, areaid, checked);
					for(Object obj:list){
						((Map)obj).put("emtype", emtype);
					}
				}
			}else if("1".equals(isSubject)){ 
				//如果是1，就表示当前节目是单位，要展开区域
				//1、区域表有值，要展开
				//2、区域表无值，直接展开部门表
				//list = ds.getArea(departid,checked);
				list=employmentService.getArea(userSession,departid, checked);
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked);
					if("true".equals(addUser)){
						List list1 = ds.getUser(departid, checked);
						if(list1 != null){
							if(list == null) list = new ArrayList();
							for(int i = 0;i<list1.size(); i++){
								list.add(list1.get(i));
							}
						}
					}
					for(Object obj:list){
						((Map)obj).put("emtype", emtype);
					}
				}
			}
			else{
					//都是展开部门
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked,true);
					//list.add(dicEmtype);
					if("true".equals(addUser)){
						List list1 = ds.getUser(departid, checked);
						if(list1 != null){
							if(list == null) list = new ArrayList();
							for(int i = 0;i<list1.size(); i++){
								System.out.println(list1.get(i));
								list.add(list1.get(i));
							}
						}
						
					}
				}
			
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
				
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	
	}
	
	public ModelAndView doBatchUpdate(HttpServletRequest request,HttpServletResponse response) throws Exception{
       String 
       qryWhere=request.getParameter("qryWhere")
       ,qryJoin=request.getParameter("qryJoin")
       ,departmentid=request.getParameter("departmentid")
       ,emtype=request.getParameter("emtype")
       ,item=request.getParameter("item")
       ,pro=request.getParameter("pro")
       ,val=request.getParameter("val")
       ;
       Connection conn=null;
       PreparedStatement ps=null;
       DbUtil dbUtil=null;
       String sqlPattern="update {0} set {1}=? where userid in (select distinct(t.id) from (select id from k_user a {2} where 1=1  {3} {4} {5} ) t )",sql="",result="";
       int affec=0;
	   try{
    	   conn=new DBConnect().getConnect();
    	   dbUtil=new DbUtil(conn);
    	   sql=MessageFormat.format(sqlPattern, 
    		item //0
    		,pro //1
    		,qryJoin  //2
    		,qryWhere  //3
    		,departmentid==null||departmentid.isEmpty()?"":" and a.departmentid= '"+departmentid+"'"
    	    ,emtype==null||emtype.isEmpty()?"":" and a.emtype= '"+emtype+"'"
    	   );
    	   ps=conn.prepareStatement(sql);
    	   ps.setString(1, val);
    	   affec+=ps.executeUpdate();
    	   if(affec>0){
    		   result=MessageFormat.format("成功更新 {0} 条数据", affec);
    	   }else{
    		   result=MessageFormat.format("没有更新 数据","");
    	   }
       }catch(Exception ex){
    	   ex.printStackTrace();
    	   result=ex.getLocalizedMessage();
       }finally{
    	   response.setContentType("text/html;charset=utf-8");
    	   response.getWriter().write(result);
       }
	   return null;		
	}
	
	public ModelAndView doSaveUserQuery(HttpServletRequest request,HttpServletResponse response) throws Exception{
	      Connection conn=null;
	       PreparedStatement ps=null;
	       DbUtil dbUtil=null;
	       //String sqlPattern="update {0} set {1}=? where userid in (select id from k_user a {2} where 1=1  {3} {4} {5} )",sql="",result="";
	       int affec=0,affec2=0;
	       WebUtil webUtil=new WebUtil(request, response);
	       UserQueryVO userQueryVO=webUtil.evalObject(UserQueryVO.class);
	       UserSession userSession=webUtil.getUserSession();
	       String result="";
	       String querys=request.getParameter("querys");
	       JSONArray jarr=JSONArray.fromObject(querys);
	       
	       List<UserQueryItemVO> queryItemVOs=JSONArray.toList(jarr,UserQueryItemVO.class);
		   try{
			   conn=new DBConnect().getConnect();
			   dbUtil=new DbUtil(conn);
			   userQueryVO.setUserid(userSession.getUserId());
			   userQueryVO.setUuid(UUID.randomUUID().toString());
			   List<UserQueryVO> userQueryVOs=dbUtil.select(UserQueryVO.class, "select * from {0} where name=? and userid=?", userQueryVO.getName(),userQueryVO.getUserid());
			   if(userQueryVOs.size()==0)
			     affec+=dbUtil.insert(userQueryVO);
			   else{
				  userQueryVO.setUuid(userQueryVOs.get(0).getUuid());
				  affec+=dbUtil.update(userQueryVO);
			   }
			   if(affec==1){
				   dbUtil.del("oa_user_query_item", "mainformid", userQueryVO.getUuid());
				   for(UserQueryItemVO userQueryItemVO :queryItemVOs){
					   userQueryItemVO.setUuid(UUID.randomUUID().toString());
					   userQueryItemVO.setMainformid(userQueryVO.getUuid());
					   affec2+=dbUtil.insert(userQueryItemVO);
				   }
				   result=MessageFormat.format("用户 {0} 的自定义查询 {1} {2}个条件  保存成功", userSession.getUserName(),userQueryVO.getName(),affec2);
			   }
		   }catch(Exception ex){
	    	   ex.printStackTrace();
	    	   result=ex.getLocalizedMessage();
	       }finally{
	    	   response.setContentType("text/html;charset=utf-8");
	    	   response.getWriter().write(result);
	       }
		   return null;	
	}
	
	public ModelAndView subsetTree (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		DbUtil dbUtil=null;
		String myUserid=request.getParameter("myUserid");
		try {
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			//UserService us=new UserService(conn);
			response.setContentType("html/text");//此5行必备，用于输出中文，否则乱码 
			response.setCharacterEncoding("UTF-8"); 
			PrintWriter out =  response.getWriter();
			//List<Map<String, String>> listMap = us.getListMapUserDetailsTree();
		    //List<UserDetailsTree> listMap= us.getListUserDetailsTree();
			List<FormVO> formVOs=dbUtil.select(FormVO.class, "select * from {0} where form_type=?", EmploymentService.UUID_FORM_TYPE_SUBSET);
			//String json = JSONArray.fromObject(listMap).toString();
			//System.out.println("join="+json);
			JSONArray jarr=new JSONArray();
			for(FormVO formVO:formVOs){
				JSONObject json=JSONObject.fromObject(formVO);
				json.put("url",MessageFormat.format("formDefine.do?method=formListView&uuid={0}&userId={1}",
						formVO.getUUID()
						,myUserid
						,"add,remove,viewDetail,edit"
						));
			    json.put("text", formVO.getNAME());
			    json.put("id", formVO.getUUID());
			    jarr.add(json);
			}
			out.write(jarr.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return null;
	}

	
	public ModelAndView doUpdateSubsetID(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String formid=request.getParameter("formid");
		String uuid=request.getParameter("uuid");
		String newID=request.getParameter("newID");
		try{
			
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			FormVO formVO=dbUtil.load(FormVO.class, formid);
			String sql=MessageFormat.format("update {0} set id={1} where uuid=''{2}''",
			  formVO.getTABLENAME()
			  ,newID
			  ,uuid
			);
			eff+=dbUtil.executeUpdate(sql);
			if(eff==1){
				re="子集编号更新成功";
			}else{
				re="子集编号更新失败";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
}
