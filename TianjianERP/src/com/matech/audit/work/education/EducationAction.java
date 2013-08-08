package com.matech.audit.work.education;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.education.EducationService;
import com.matech.audit.service.education.model.Education;
import com.matech.audit.service.education.model.EducationPO;
import com.matech.audit.service.education.model.Exam;
import com.matech.audit.service.inbox.InboxService;
import com.matech.audit.service.teacher.TeacherService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class EducationAction extends MultiActionController {
	private final String _list="/education/List.jsp";
	private final String _addAndEdit="/education/addAndEdit.jsp";
	
	/** 增加现场培训班*/
	private final String _addLocaleEdu="/education/addLocaleEdu.jsp";
	/** 增加网络培训班*/
	private final String _addNetEdu="/education/addNetEdu.jsp";
	
	private final String _regDetail="/education/regDetail.jsp";
	private final String _regList="/education/regList.jsp";
	private final String _registration="/education/registration.jsp";
	private final String _returnToList="education.do?method=list";
	private final String _myRegList="/education/myRegList.jsp";
	private final String _examList="/education/examList.jsp";
	private final String _examAddAndEdit="/education/examAddAndEdit.jsp";
	private final String _examRegList="/education/examRegList.jsp";
	private final String _examReg="/education/examReg.jsp";
	private final String _educationTree="education/educationTree.jsp";
	private final String registerDetail="education/registerDetail.jsp";
	
	/**
	 * 班级管理列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		ModelAndView modelAndView = new ModelAndView(_list);
		DataGridProperty pp =new DataGridProperty();
		
		ASFuntion asf = new ASFuntion();
		
		String coursetype=asf.showNull(request.getParameter("coursetype"));
		String addSql="";
		
		if(!coursetype.equals("")){
			addSql=" and e.coursetype= '"+coursetype+"'";
		}
	
		
		pp.setTableID("education");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("培训班管理");
	    
	    String sql = "select e.*,group_concat(distinct concat('[',t.teacherNum,']',t.name)) teacher,c.type,s.stateType,g.score,g.personCount,f.applyCount,i.name as course " +
	    		    "from k_education e  " +
	    		    "left join k_teacher t on CONCAT(e.teacherId,',') LIKE CONCAT('%',t.id,',%') "
	    			+" left join k_classtype c on c.id=e.classtype "
	    			+" left join k_educationstate s on e.state=s.id "
	    			+" left join (" +
	    			"  select applyDate,educationId,count(*) as personCount,round(sum(votevalue)/count(*),2) as score " +
	    			"  from k_evaluate group by educationId" +
	    			") g on e.id=g.educationId "
	    			+" left join (select count(*) as applyCount,educationId from k_educationregdetail group by educationId ) f on e.id = f.educationId "
	    			+" left join k_dic i on i.autoid=e.courseType "
	    			+" where 1=1 ${name} ${teacherId} ${trainStartTime} ${trainEndTime} ${registrationStartTime} ${registrationEndTime} ${address}"
	    			+addSql
	    			+" group by e.id ";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
		pp.setTrAction(" educationId='${id}' style='cursor:hand;'");
		pp.addColumn("培训对象","trainObject");
	    pp.addColumn("培训班名称", "name");
	    pp.addColumn("课程类型", "course");
	    pp.addColumn("培训开始时间", "trainStartTime");
	    pp.addColumn("培训结束时间", "trainEndTime");
	    pp.addColumn("讲师教师", "teacher");
	    pp.addColumn("限定报名人数", "registrationNum");
	    pp.addColumn("报名人数", "applyCount");
	    //pp.addColumn("发布日期", "periodTime");
	    //pp.addColumn("费用", "cost");
	    pp.addColumn("报名日期", "registrationStartTime");
	    pp.addColumn("报名截止日期", "registrationEndTime");
	    pp.addColumn("状态", "stateType");
	    pp.addColumn("评价人数", "personCount");
	    pp.addColumn("平均分", "score");
	    pp.setColumnWidth("10,12,10,10,10,12,10,,10,10,8,8,,6");
	    	    
	    pp.addSqlWhere("name", "and e.name='${name}'");
	    pp.addSqlWhere("teacherId", "and teacherId='${teacherId}'");
	    pp.addSqlWhere("trainStartTime", "and trainStartTime='${trainStartTime}'");
	    pp.addSqlWhere("trainEndTime", "and trainEndTime='${trainEndTime}'");
	    pp.addSqlWhere("registrationStartTime", "and registrationStartTime='${registrationStartTime}'");
	    pp.addSqlWhere("registrationEndTime", "and registrationEndTime='${registrationEndTime}'");
	    pp.addSqlWhere("address", "and address='${address}'");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	/**
	 * 跳转到增加现场培训班
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addLocaleEdu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("---------------------");
		String uuid = UUID.randomUUID().toString();
		ModelAndView modelAndView = new ModelAndView(_addLocaleEdu);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
			modelAndView.addObject("act", act);
			if(act.equals("edit")||act.equals("reg")){
				int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
				Education education=new Education();
				EducationService es=new EducationService(conn);
				education=es.findById(id);
				String state=es.findState(education.getState());
				modelAndView.addObject("education", education);
				modelAndView.addObject("state", state);
				if(act.equals("reg")){
					double voteValue=es.getAvg(id);
					modelAndView.addObject("voteValue", voteValue);
					modelAndView.setViewName(_registration);
				}
			}else{
				modelAndView.addObject("UUID",uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return modelAndView;
	}
	
	/**
	 * 跳转到增加现网络培训班
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addNetEdu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uuid = UUID.randomUUID().toString();
		ModelAndView modelAndView = new ModelAndView(_addNetEdu);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
			modelAndView.addObject("act", act);
			if(act.equals("edit")||act.equals("reg")){
				int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
				Education education=new Education();
				EducationService es=new EducationService(conn);
				education=es.findById(id);
				String state=es.findState(education.getState());
				modelAndView.addObject("education", education);
				modelAndView.addObject("state", state);
				if(act.equals("reg")){
					double voteValue=es.getAvg(id);
					modelAndView.addObject("voteValue", voteValue);
					modelAndView.setViewName(_registration);
				}
			}else{
				modelAndView.addObject("UUID",uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return modelAndView;
	}
	
	/**
	 * 增加现场培训班
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String uuid = CHF.showNull(request.getParameter("uuid"));
			String act=CHF.showNull(request.getParameter("act"));
			String attachment=CHF.showNull(request.getParameter("attachment"));
			String name=CHF.showNull(request.getParameter("name"));
			String trainStartTime=CHF.showNull(request.getParameter("trainStartTime"));
			String trainEndTime=CHF.showNull(request.getParameter("trainEndTime"));
			String teacherId=CHF.showNull(request.getParameter("teacherId"));
			String registrationNum=CHF.showNull(request.getParameter("registrationNum"));
			String periodTime=CHF.showNull(request.getParameter("periodTime"));
			String cost=CHF.showNull(request.getParameter("cost"));
			String registrationStartTime=CHF.showNull(request.getParameter("registrationStartTime"));
			String registrationEndTime=CHF.showNull(request.getParameter("registrationEndTime"));
			String state=CHF.showNull(request.getParameter("state"));
			String classType=CHF.showNull(request.getParameter("classType"));
			String trainObject=CHF.showNull(request.getParameter("trainObject"));
			String address=CHF.showNull(request.getParameter("address"));
			String content=CHF.showNull(request.getParameter("content"));
			String arrangement=CHF.showNull(request.getParameter("arrangement"));
			String link=CHF.showNull(request.getParameter("link"));
			String courseType=CHF.showNull(request.getParameter("courseType"));
			Education education=new Education();
			if(request.getParameter("id")!="" && request.getParameter("id")!=null){
				int id=Integer.valueOf(request.getParameter("id"));
				education.setId(id);
			}
			education.setName(name);
			education.setAddress(address);
			education.setArrangement(arrangement);
			education.setAttachment(attachment);
			education.setClassType(classType);
			education.setContent(content);
			education.setCost(cost);
			education.setPeriodTime(periodTime);
			education.setRegistrationEndTime(registrationEndTime);
			education.setRegistrationNum(registrationNum);
			education.setRegistrationStartTime(registrationStartTime);
			education.setState(state);
			education.setTeacherId(teacherId);
			education.setTrainEndTime(trainEndTime);
			education.setTrainObject(trainObject);
			education.setTrainStartTime(trainStartTime);
			education.setLink(link);
			education.setUuid(uuid);
			education.setCourseType(courseType);
			EducationService es=new EducationService(conn);
			if(act.equals("add")){
				es.addLocaleEdu(education);
			}
			if(act.equals("edit")){
				es.update(education);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/"+_list);	
	}
	
	/**
	 * 保存或更新现场培训班
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveOrUpdateLocaleEdu(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			System.out.println("------------------保存或更新本地培训班------------------");
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String uuid = CHF.showNull(request.getParameter("uuid"));
			String act=CHF.showNull(request.getParameter("act"));
			String attachment=CHF.showNull(request.getParameter("attachment"));
			String name=CHF.showNull(request.getParameter("name"));
			String trainStartTime=CHF.showNull(request.getParameter("trainStartTime"));
			String trainEndTime=CHF.showNull(request.getParameter("trainEndTime"));
			String teacherId=CHF.showNull(request.getParameter("teacherId"));
			String registrationNum=CHF.showNull(request.getParameter("registrationNum"));
			String periodTime=CHF.showNull(request.getParameter("periodTime"));
			String cost=CHF.showNull(request.getParameter("cost"));
			String registrationStartTime=CHF.showNull(request.getParameter("registrationStartTime"));
			String registrationEndTime=CHF.showNull(request.getParameter("registrationEndTime"));
			String state=CHF.showNull(request.getParameter("state"));
			String classType=CHF.showNull(request.getParameter("classType"));
			String trainObject=CHF.showNull(request.getParameter("trainObject"));
			String address=CHF.showNull(request.getParameter("address"));
			String content=CHF.showNull(request.getParameter("content"));
			String arrangement=CHF.showNull(request.getParameter("arrangement"));
			String link=CHF.showNull(request.getParameter("link"));
			String courseType=CHF.showNull(request.getParameter("courseType"));
			Education education=new Education();	// Education实体
			if(request.getParameter("id")!="" && request.getParameter("id")!=null){
				int id=Integer.valueOf(request.getParameter("id"));
				education.setId(id);
			}
			education.setName(name);
			education.setAddress(address);
			education.setArrangement(arrangement);
			education.setAttachment(attachment);
			education.setClassType(classType);
			education.setContent(content);
			education.setCost(cost);
			education.setPeriodTime(periodTime);
			education.setRegistrationEndTime(registrationEndTime);
			education.setRegistrationNum(registrationNum);
			education.setRegistrationStartTime(registrationStartTime);
			education.setState(state);
			education.setTeacherId(teacherId);
			education.setTrainEndTime(trainEndTime);
			education.setTrainObject(trainObject);
			education.setTrainStartTime(trainStartTime);
			education.setLink(link);
			education.setUuid(uuid);
			education.setCourseType(courseType);
			EducationService es=new EducationService(conn);
			if(act.equals("add")){
			// 调用业务逻辑组件
				es.addLocaleEdu(education);
			}
			if(act.equals("edit")){
				es.update(education);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/"+_educationTree);	
		
		//response.sendRedirect(request.getContextPath() + "/"+ "education.do");	
	}
	
	/**
	 * 保存或更新网络培训班
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveOrUpdateNetEdu(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			System.out.println("------------------保存或更新网络培训班------------------");
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String uuid = CHF.showNull(request.getParameter("uuid"));
			String act=CHF.showNull(request.getParameter("act"));
			String attachment=CHF.showNull(request.getParameter("attachment"));
			String name=CHF.showNull(request.getParameter("name"));   
			String registrationNum=CHF.showNull(request.getParameter("registrationNum"));
			String periodTime=CHF.showNull(request.getParameter("periodTime"));
			String state=CHF.showNull(request.getParameter("state"));
			String classType=CHF.showNull(request.getParameter("classType"));
			String trainObject=CHF.showNull(request.getParameter("trainObject"));
			String address=CHF.showNull(request.getParameter("address"));
			String content=CHF.showNull(request.getParameter("content"));
			String arrangement=CHF.showNull(request.getParameter("arrangement"));
			String link=CHF.showNull(request.getParameter("link"));
			String courseType=CHF.showNull(request.getParameter("courseType"));
			Education education=new Education();	// Education实体
			if(request.getParameter("id")!="" && request.getParameter("id")!=null){
				int id=Integer.valueOf(request.getParameter("id"));
				education.setId(id);
			}
			education.setName(name);
			education.setAddress(address);
			education.setArrangement(arrangement);
			education.setAttachment(attachment);
			education.setClassType(classType);
			education.setContent(content);
			education.setPeriodTime(periodTime);
			education.setRegistrationNum(registrationNum);
			education.setState(state);
			education.setTrainObject(trainObject);
			education.setLink(link);
			education.setUuid(uuid);
			education.setCourseType(courseType);
			EducationService es=new EducationService(conn);
			if(act.equals("add")){
				// 调用业务逻辑组件
				es.addNetEdu(education);
			}
			if(act.equals("edit")){
				
				es.update(education);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/"+_educationTree);	
		//response.sendRedirect(request.getContextPath() + "/"+ "education.do");	
	}
	
	
	/*
	 * 跳转到修改培训班或报名页面
	 */
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uuid = UUID.randomUUID().toString();
		ModelAndView modelAndView = new ModelAndView(_addAndEdit);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
			modelAndView.addObject("act", act);
			if(act.equals("edit")||act.equals("reg")){
				int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
				Education education=new Education();
				EducationService es=new EducationService(conn);
				education=es.findById(id);
				String state=es.findState(education.getState());
				modelAndView.addObject("education", education);
				modelAndView.addObject("state", state);
				if(act.equals("reg")){
					double voteValue=es.getAvg(id);
					modelAndView.addObject("voteValue", voteValue);
					modelAndView.setViewName(_registration);
				}
			}else{
				modelAndView.addObject("UUID",uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return modelAndView;
	}
	
	
	
	/**
	 * 删除选中的培训班
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//ModelAndView modelAndView = new ModelAndView(_list);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
			EducationService es=new EducationService(conn);
			es.del(id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/"+_list);
		//return modelAndView;
	}
	
	/**
	 * 查看选中培训班的查看报名情况
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView regDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		System.out.println("----------------查看报名情况---------------");
		ModelAndView modelAndView = new ModelAndView(_regDetail);
		ASFuntion CHF=new ASFuntion();
		int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));

		DataGridProperty pp=new DataGridProperty();
		pp.setTableID("educationRegDetail");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("报名明细");
	    
	    String sql="select ed.userId,u.id,u.name,case when Sex='M' or Sex='男' then '男' else '女' end Sex," +
	    		    " d.departname,ed.time,ed.evaResult,ke.name as kname,i.name as iname from k_educationregdetail ed \n" +
	    		    "left join k_user u on u.id=ed.userid "
	    			+" left join k_department d on u.departmentid=d.autoid " +
	    		    " left join k_education ke on ke.id=ed.educationid " +
	    		    " left join k_dic i on i.autoid=ke.courseType "
	    			+" where ed.educationid="+id;
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("educationid") ;
	    pp.setInputType("checkbox");
	    pp.setWhichFieldIsValue(1);
	    pp.setColumnWidth("10,10,10,15,12,12,20");
	    
	    pp.addColumn(" ", "id","hide");
	    pp.addColumn("参与人", "name");
	    pp.addColumn("性别", "sex");
	    pp.addColumn("所属部门", "departname");
	    pp.addColumn("培训课程名称","kname");
	    pp.addColumn("课程类型","iname");
	    pp.addColumn("报名时间", "time");
	    pp.addColumn("教育培训结果","evaResult");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
	    
	    modelAndView.addObject("educationId",id);
		return modelAndView;
	}
	
	/*
	 * 查找还没到报名结束时间的班级
	 */
	public ModelAndView onReg(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_regList);
		DataGridProperty pp=new DataGridProperty();
		pp.setTableID("educationRegDetail");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("招人中的培训班");
	    
	    String sql = "select e.*,concat('[',t.teacherNum,']',t.name) teacher,c.type,s.stateType,i.name as course " +
	    		    "from k_education e   " +
	    		    "left join k_teacher t on e.teacherid=t.id"
					+" left join k_classtype c on c.id=e.classtype "
					+" left join k_educationstate s on s.id=e.state "
					+" left join k_dic i on i.autoid=e.courseType "
//					+" left join ( select  educationid,group_concat(userId) as userId from k_educationregdetail " 
//				    +" where userId <>  '"+userSession.getUserId()+"' group by educationid ) f on e.id=f.educationid "
//					+" where userId not like concat('%',"+userSession.getUserId()+",'%') group by educationid ) f on e.id=f.educationid "
					+" where  registrationendtime>=now() AND e.id NOT IN (SELECT educationid FROM k_educationregdetail WHERE userId  = '"+userSession.getUserId()+"') ${name} ${teacherId} ${trainStartTime} ${trainEndTime} ${registrationStartTime} ${registrationEndTime} ${address}";
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    
	    pp.addColumn("培训对象","trainObject");
	    pp.addColumn("培训班名称", "name");
	    pp.addColumn("课程类型", "course");
	    pp.addColumn("培训开始时间", "trainStartTime");
	    pp.addColumn("培训结束时间", "trainEndTime");
	    pp.addColumn("讲师教师", "teacher");
	    pp.addColumn("限定报名人数", "registrationNum");
	    pp.addColumn("发布时间", "periodTime");
	   // pp.addColumn("费用", "cost");
	    pp.addColumn("报名日期", "registrationStartTime");
	    pp.addColumn("报名截止日期", "registrationEndTime");
	    
	    pp.addColumn("状态", "statetype");
	    
	    pp.setColumnWidth("12,10,12,10,10,10,12,10,10,10,8");
	    pp.addSqlWhere("name", "and name='${name}'");
	    pp.addSqlWhere("teacherId", "and teacherId='${teacherId}'");
	    pp.addSqlWhere("trainStartTime", "and trainStartTime='${trainStartTime}'");
	    pp.addSqlWhere("trainEndTime", "and trainEndTime='${trainEndTime}'");
	    pp.addSqlWhere("registrationStartTime", "and registrationStartTime='${registrationStartTime}'");
	    pp.addSqlWhere("registrationEndTime", "and registrationEndTime='${registrationEndTime}'");
	    pp.addSqlWhere("address", "and address='${address}'");
	   // System.out.println(sql);
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	/*
	 * 报名，增加1条学员信息到培训班
	 */
	public ModelAndView educationReg(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_returnToList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		int userId=Integer.parseInt(userSession.getUserId());
		int educationId=Integer.parseInt(request.getParameter("educationId"));
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			EducationService es = new EducationService(conn);
			String exist=es.reg(educationId, userId);
			response.getWriter().write(exist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}		
		return null;
	}
	
	/*
	 * 查找自己报名过的培训班
	 */
	public ModelAndView myList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_myRegList);
		DataGridProperty pp=new DataGridProperty();
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		int myId=Integer.parseInt(userSession.getUserId());
			
		pp.setTableID("myRegList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("培训班管理");
	    
	   
	    
	    /*String sql = "select e.*,concat('<a href=http://',link,'> ',link,'</a>') netLink,concat('[',t.teacherNum,']',t.name) teacher,c.type,s.statetype from k_education e left join k_teacher t on e.teacherid=t.id"
	    			+" left join k_classtype c on c.id=e.classtype"
	    			+" left join k_educationregdetail er on e.id=er.educationid "
	    			+" left join k_educationstate s on s.id=e.state"
	    			+" where er.userid="+myId;*/
	    String sql = "select DISTINCT e.*,concat('<a href=http://',link,'> ',link,'</a>') netLink,(SELECT COUNT(*) FROM k_course WHERE educationUUID = e.uuid) AS linkcount," 
			+" concat('[',t.teacherNum,']',t.name) teacher,c.type,s.statetype,g.applyDate,g.score,er.evaResult  from k_education e left join k_teacher t on e.teacherid=t.id"
			+" left join k_classtype c on c.id=e.classtype"
			+" left join k_educationregdetail er on e.id=er.educationid and er.userid="+myId
			+" left join k_educationstate s on s.id=e.state " +
			 " left join k_evaluate v on v.educationId=e.id and v.userId="+myId
			+" left join ( select applyDate,educationId,round(sum(votevalue)/count(*),2) as score from k_evaluate group by educationId) g on e.id=g.educationId "
			+" where er.userid="+myId;
	    
	  
	   // pp.setTrActionProperty(true); // 设置 table可双击
		//pp.setTrAction("style=\"cursor:hand;\" educationId=\"${id}\"  ");
		pp.setInputType("radio");
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setWhichFieldIsValue(1);
	    pp.addColumn("培训对象","trainObject");
	    pp.addColumn("培训班名称", "name");
	    pp.addColumn("培训开始时间", "trainStartTime");
	    pp.addColumn("培训结束时间", "trainEndTime");
	    pp.addColumn("讲师教师", "teacher");
	    //pp.addColumn("限定报名人数", "registrationNum");
	    //pp.addColumn("期次", "periodTime");
	    //pp.addColumn("费用", "cost");
	    //pp.addColumn("报名日期", "registrationStartTime");
	    //pp.addColumn("报名截止日期", "registrationEndTime");
	    //pp.addColumn("状态", "statetype");
	    //pp.addColumn("网校链接", "netLink");
	    //pp.addColumn("在线课件", "linkcount","showCenter",null," <a href=\"javascript:void(0)\" onclick=showview(\'${uuid}\',\'${linkcount}\')><img src='img/play.png' alt='播放'></a>");
	    pp.addColumn("平均分", "score");
	    pp.addColumn("评价时间", "applyDate");
	    pp.addColumn("教育培训结果","evaResult");
	    pp.setColumnWidth("10,12,10,10,15,10,12,15");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	/*
	 * 判断重复报名
	 */
	public void isReg(HttpServletRequest request, HttpServletResponse response) throws Exception{
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		int userId=Integer.parseInt(userSession.getUserId());
		int educationId=Integer.parseInt(request.getParameter("educationId"));
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			EducationService es = new EducationService(conn);
			String exist=es.isReg(educationId, userId);
			response.getWriter().write(exist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 得到状态
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void getState(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		String id = request.getParameter("id");
		
		try {
			conn = new DBConnect().getConnect("");
			 
			String state = new DbUtil(conn).queryForString("SELECT b.statetype FROM `k_education` a LEFT JOIN `k_educationstate` b ON a.state = b.`id` WHERE a.`id` = '"+id+"'");
			
			response.getWriter().write(state);			
		} catch (IOException e) {
			
			System.out.println("删除收件错误："+e.getMessage());
		}finally{
			DbUtil.close(conn);
		}
		
	}
	/*
	 * 考试列表
	 */
	public ModelAndView examList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_examList);
		DataGridProperty pp =new DataGridProperty();
		
		pp.setTableID("examList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("考试列表");
	    
	    String sql="select id,b.name,examsubject,registrationStartTime,registrationEndTime,qualifications,examTime,"
	    			+"countReg from k_exam a inner join k_dic b on a.examtype=b.autoid"
	    			+" where 1=1 ${examType} ${examSubject} ${qualifications}";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("id") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    pp.setTrAction(" examListId='${id}' style='cursor:hand;'");
	    pp.addColumn("考试类型","name");
	    pp.addColumn("考试科目","examsubject");
	    pp.addColumn("报名开始时间","registrationStartTime");
	    pp.addColumn("报名结束时间","registrationEndTime");
	    pp.addColumn("资格要求","qualifications");
	    pp.addColumn("考试时间","examTime");
	    pp.addColumn("报名人数", "countReg");
	    
	    pp.addSqlWhere("examType", "and examType='${examType}'");
	    pp.addSqlWhere("examSubject", "and examSubject='${examSubject}'");
	    pp.addSqlWhere("qualifications", "and qualifications='${qualifications}'");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
	    return modelAndView;
	}
	/*
	 * 跳转到修改添加考试页面
	 */
	public ModelAndView addExam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_examAddAndEdit);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String id=CHF.showNull(request.getParameter("id"));
			String act=CHF.showNull(request.getParameter("act"));
			if(act.equals("edit") || act.equals("reg")){
				EducationService es=new EducationService(conn);
				Exam exam=new Exam();
				exam=es.getExamById(id);
				modelAndView.addObject("exam", exam);
				if(act.equals("reg")){
					modelAndView.setViewName(_examReg);
				}
			}
			modelAndView.addObject("act", act);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	/*
	 * 增加或更新考试记录
	 */
	public void saveOrUpdateExam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userId=userSession.getUserId();
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
			String id=CHF.showNull(request.getParameter("id"));
			String examType=CHF.showNull(request.getParameter("examType"));
			String examSubject=CHF.showNull(request.getParameter("examSubject"));
			String registrationStartTime=CHF.showNull(request.getParameter("registrationStartTime"));
			String registrationEndTime=CHF.showNull(request.getParameter("registrationEndTime"));
			String qualifications=CHF.showNull(request.getParameter("qualifications"));
			String examTime=CHF.showNull(request.getParameter("examTime"));
			String remark=CHF.showNull(request.getParameter("remark"));
			Exam exam=new Exam();
			exam.setId(id);
			exam.setExamType(examType);
			exam.setExamSubject(examSubject);
			exam.setRegistrationStartTime(registrationStartTime);
			exam.setRegistrationEndTime(registrationEndTime);
			exam.setQualifications(qualifications);
			exam.setExamTime(examTime);
			exam.setRemark(remark);
			EducationService es=new EducationService(conn);
			if(act.equals("add")){
				es.addExam(exam,userId);
			}else if(act.equals("edit")){
				es.updateExam(exam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/education.do?method=examList");
	}
	/*
	 * 删除考试记录
	 */
	public void delExam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String id=CHF.showNull(request.getParameter("id"));
			EducationService es=new EducationService(conn);
			es.delExam(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/education.do?method=examList");
	}
	/*
	 * 考试报名列表
	 */
	public ModelAndView examRegList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_examRegList);
		DataGridProperty pp =new DataGridProperty();
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId=userSession.getUserId();
		
		pp.setTableID("examRegList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("考试报名列表");
	    
	    String sql="select a.id,d.name,examsubject,registrationStartTime,registrationEndTime,qualifications,examTime,"
	    			+"countReg,achievement,ifnull(c.state,'未报名') stateReg from k_exam a left join k_examreg b on a.id=b.examid "
	    			+" left join (SELECT id,state FROM k_examreg WHERE userid="+userId+") c on c.id=b.id"
	    			+" inner join k_dic d on d.autoid=a.examtype"
	    			+" where 1=1 and registrationEndTime>=now() and registrationStartTime<=now() "
	    			+" ${examType} ${examSubject} ${qualifications}";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("registrationEndTime") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    pp.setTrActionProperty(true);
	    pp.addColumn("考试类型","name");
	    pp.addColumn("考试科目","examsubject");
	    pp.addColumn("报名开始时间","registrationStartTime");
	    pp.addColumn("报名结束时间","registrationEndTime");
	    pp.addColumn("资格要求","qualifications");
	    pp.addColumn("考试时间","examTime");
	    pp.addColumn("报名人数", "countReg");
	    pp.addColumn("我的成绩", "achievement");
	    pp.addColumn("状态", "stateReg");
	    
	    pp.addSqlWhere("examType", "and examType='${examType}'");
	    pp.addSqlWhere("examSubject", "and examSubject='${examSubject}'");
	    pp.addSqlWhere("qualifications", "and qualifications='${qualifications}'");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
	    return modelAndView;
	}
	/*
	 * 判定考试重复报名
	 */
	public void isRegExam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String examRegId=request.getParameter("examRegListId");
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId=userSession.getUserId();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			EducationService es=new EducationService(conn);
			String exist=es.isRegExam(examRegId, userId);
			response.getWriter().write(exist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
	}
	/*
	 * 考试报名
	 */
	public void examReg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String examId=request.getParameter("examRegListId");
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId=userSession.getUserId();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			EducationService es=new EducationService(conn);
			es.examReg(userId, examId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/education.do?method=examRegList");
	}
	/*
	 * 培训班树
	 */
	public void getEducationTree (HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion asf = new ASFuntion();
		try {
			conn=new DBConnect().getConnect("") ;
			response.setContentType("text/html;charset=utf-8") ;
			PrintWriter out = response.getWriter();
			//String diskPath = asf.showNull(request.getParameter("diskPath"));
			String id=asf.showNull(request.getParameter("id"));
			List treeList = new ArrayList() ;
			String addSql="";
			String sql = "";
//			if(!id.equals("0")){
//				addSql=" and a.coursetype ='"+id+"' " ;
//				sql="select distinct a.id,a.name as typeName from k_education a left join k_dic b on a.coursetype=b.autoid where 1=1 and a.name>''  "
//					+addSql;
//			}else{
//				addSql=" where ctype ='课程类型' " ;
//				sql="select distinct b.autoid as id,b.name as typeName from k_dic b "
//					+addSql;
//			}
			
			if("0".equals(id)){
				addSql=" where ctype ='课程类型' " ;
				sql="select distinct b.autoid as id,b.name as typeName from k_dic b "
					+addSql;
				
				ps=conn.prepareStatement(sql);
				rs=ps.executeQuery();
				while (rs.next()) {
					Map map = new HashMap();
					map.put("id", rs.getString("id"));
					
					map.put("leaf", true);
					
					map.put("text",rs.getString("typeName"));
					treeList.add(map) ;
				}
				String jsonStr = JSONArray.fromObject(treeList).toString() ;
				out.write(jsonStr) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
			
		}
	/**
	 * 设置评价结果
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doEvaluate(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		
		
		String uuids=request.getParameter("uuids");
		
		String educationId=StringUtil.showNull(request.getParameter("educationId"));
		
		String state=StringUtil.showNull(request.getParameter("state"));
		
		uuids=StringUtil.trim(uuids, ",");
		
		String sql="update k_educationregdetail set evaResult=? where userid in ("+uuids+") and educationId=?";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{state,educationId});
			if(num>0){
		    	result="设置成功";
		    }else{
		    	result="设置失败";
		    }
		}catch(Exception ex){
			result=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		
		response.getWriter().write(result);
		return null;
	}
	
	/**
	 * 报名汇总明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView viewDetail(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(registerDetail);
		
		String id=StringUtil.showNull(request.getParameter("id"));
		
		Connection conn=null;
		
		try{
			conn=new DBConnect().getConnect();
			
			EducationService educationServ=new EducationService(conn);
			
			EducationPO education=educationServ.queryHui(id);
			
			modelAndView.addObject("education",education);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		
		return modelAndView;
		
	}
	
}
