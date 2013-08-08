package com.matech.audit.work.cadet;

import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.helpers.DateTimeDateFormat;
import org.jbpm.api.ProcessInstance;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;



import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.cadet.model.CadetCheckVO;
import com.matech.audit.service.cadet.model.CadetVO;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.form.FormDefineService;

import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.audit.work.login.LoginAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.login.LoginService;

public class CadetAction extends MultiActionController{
	
	protected enum Jsp{
		
		  sz_table,yj_table,modify_projectcase;
		  
		  public String getPath(){
			  return MessageFormat.format("/cadet/{0}.jsp", this.name());
		  }
	}
	
	public final static String CADET_DEPARTMENID="127610";
	public final static String CADET_CHECK_FORMID="454aa9ae-8b44-4979-a90f-a51df8a53241";
	public final static String CADET_CHECK_PKEY="31606af1-6528-4525-a846-93fdc0b57bfe";
	
	private String CADET = "/cadet/cadet.jsp";
	private String VIEW = "/cadet/view.jsp";
	private String LOGIN = "/login.do";
	private String List = "/cadet/list.jsp";
	private String Employee = "/employee/list.jsp";
	private String MODIFY = "/cadet/modify.jsp";
	private String BEFORECADETSKIP = "/cadet/beforeCadetSkip.jsp";
	private String EMPLOYEEJSP = "/employee/employee.jsp";


	//判断证件号
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ModelAndView modelandview = new ModelAndView(List);
		return modelandview;
	}
	
	public ModelAndView getOpt(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String opt = request.getParameter("opt");
		String cardNum = request.getParameter("cardNum");
		request.setAttribute("opt", opt);
		request.setAttribute("cardNum", cardNum);
		ModelAndView modelandview = new ModelAndView(Employee);
		return modelandview;
	}
	
	public ModelAndView cadet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String opt = request.getParameter("opt");
		String idcard=request.getParameter("idcard");
		String name_cn=request.getParameter("name_cn");
		String recruit=request.getParameter("recruit");
		ModelAndView modelandview=null;
		Connection conn=new DBConnect().getConnect();
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			dbUtil=new DbUtil(conn);
			int re=dbUtil.queryForInt("select count(*) from cadet_audit where name=? ",new Object[]{ name_cn});
			if(re==0){
				webUtil.alert("实习生名单中不存在此人，请与相关人员联系");
			    response.getWriter().write("<script>window.close();</script>");
				return null;
			}
		
			List<CadetVO> cadetVOs=dbUtil.select(CadetVO.class, "select * from {0} where idcard=? and name_cn=?",idcard,name_cn);
		    CadetVO cadetVO=cadetVOs.size()>0?cadetVOs.get(0):new CadetVO();
		    if(cadetVO.getUuid()==null){
		    cadetVO.setIdcard(idcard);
		    cadetVO.setName_cn(name_cn);
		    cadetVO.setDepartmentid(CADET_DEPARTMENID);
		    }
		    if("社招".equals(recruit)){
		    	if(cadetVO.getUuid() != null) {
		    		if(!"s".equals(cadetVO.getType())) {
						webUtil.alert("对不起!社会招聘简历表中没有'"+cadetVO.getName_cn()+"'该人员,请与相关人员联系");
					    response.getWriter().write("<script>window.close();</script>");
						return null;
		    		}
		    	}		    	
		       modelandview=new ModelAndView(Jsp.sz_table.getPath());
		    }else if("应届".equals(recruit)){
		    	if(cadetVO.getUuid() != null) {
		    		if(!"y".equals(cadetVO.getType())) {
						webUtil.alert("对不起!应届招聘简历表中没有'"+cadetVO.getName_cn()+"'该人员,请与相关人员联系");
					    response.getWriter().write("<script>window.close();</script>");
						return null;
		    		}
		    	}
		    	modelandview=new ModelAndView(Jsp.yj_table.getPath());
		    }else{
		    	modelandview = new ModelAndView(CADET);
		    }
		    modelandview.addObject("vo", cadetVO);
			modelandview.addObject("hasAssigned",!CADET_DEPARTMENID.equals(cadetVO.getDepartmentid()));
		    
			
		
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelandview;
	}
	

	
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
  
		response.setContentType("text/html;charset=utf-8");
		
		ModelAndView modelandview = new ModelAndView(LOGIN);
		WebUtil webUtil = null;
		CadetVO cadetvo = null;
		Connection conn = null;
		DbUtil dbutil = null;
		int i = 0;
		try{
		
			webUtil =new WebUtil(request, response);
			cadetvo = webUtil.evalObject(CadetVO.class);
		     conn = new DBConnect().getConnect();
		     dbutil = new DbUtil(conn);
		     if(cadetvo.getUuid().isEmpty()){
		       cadetvo.setUuid(UUID.randomUUID().toString());
		       //cadetvo.setDepartmentid("127609");  //默认为实习生部门
		       i = dbutil.insert(cadetvo);
		     }else{
		    	i=dbutil.update(cadetvo);
		     }
		     if(i == 1){
		       webUtil.alert("保存成功");
		     }else{
		    	webUtil.alert("保存失败，请重新登记");
		     }
		    	 
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		//response.sendRedirect("/login.do");
		response.getWriter().write("<script>window.close()</script>");
		return modelandview;
	}
	
	
	//查看
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelandview = new ModelAndView(CADET);
		
		String uuid = request.getParameter("uuid");
		WebUtil webUtil = null;
		CadetVO cadetVO  = null;
		Connection conn = null;
		DbUtil dbutil = null;
		try {
			webUtil = new WebUtil(request, response);
			//cadetVO = webUtil.evalObject(CadetVO.class);
			
			conn = new DBConnect().getConnect();
			dbutil = new DbUtil(conn);
			cadetVO = dbutil.load(CadetVO.class, uuid);
			request.setAttribute("vo", cadetVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbutil.close(conn);
		}
		
		return modelandview;
	}
	
	public ModelAndView checkIdcard(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		String idcard=request.getParameter("idcard");
		int eff=0;
		String re="";
		JSONObject json=new JSONObject();
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			List<ResumeVO> list=dbUtil.select(ResumeVO.class,"select * from {0} where idcard=?", idcard);
            if(list.size()>0){
                json.put("re", 1);
                json.put("uuid", list.get(0).getUuid());
            }else{
            	json.put("re", 1);
            }
            re=json.toString();
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doAssignDep(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
		Connection conn=null;
		DbUtil dbUtil=null;
		String departmentid=request.getParameter("departmentid");
		departmentid=departmentid.split("-")[1];
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String prac_real_start_date=request.getParameter("prac_real_start_date"),
		prac_real_end_date=request.getParameter("prac_real_end_date");
		String sql="update hr_prac_info set departmentid=?,prac_real_start_date=?,prac_real_end_date=?,hr_state=? where uuid  in ("+uuids+")";
		String formid=request.getParameter("formid");
		String menuid=request.getParameter("menuid");
		int eff=0;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    eff+=dbUtil.executeUpdate(sql, new Object[]{departmentid,prac_real_start_date,prac_real_end_date,"已分配"});
		    
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		return null;
	}
	
	public ModelAndView doDepCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids,",");
		String hr_state=request.getParameter("hr_state");
		String remark_check=request.getParameter("remark_check");
		String sql="update hr_prac_info set hr_state=?,remark_check=? where uuid in ("+uuids+")";
		String sql2="update hr_prac_info set hr_state=?,remark_check=?, departmentid=? where uuid in ("+uuids+")";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			if("审核通过".equals(hr_state)){
			   eff+= dbUtil.executeUpdate(sql,new Object[]{hr_state,remark_check});
			}else{
			   eff+=dbUtil.executeUpdate(sql2,new Object[]{hr_state,remark_check,CADET_DEPARTMENID});	
			}
		    if(eff>0){
		    	re="审核完成";
		    }else{
		    	re="审核失败";
		    }
		    	
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doHrCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		String hr_state=request.getParameter("hr_state");
		String remark_check=request.getParameter("remark_check");
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update hr_prac_info set hr_state=?,remark_check=? where uuid in ("+uuids+")";
		String sql2="update hr_prac_info set hr_state=?,remark_check=?, department=? where uuid in ("+uuids+")";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			eff+=dbUtil.executeUpdate(sql, new Object[]{hr_state,remark_check});
			for(String uuid:uuids.split(",")){
				CadetVO cadetVO=dbUtil.load(CadetVO.class,StringUtil.trim(uuid, "'"));
				UserVO userVO=new UserVO();
				userVO.setName(cadetVO.getName_cn());
				userVO.setSex(cadetVO.getSex());
				userVO.setBornDate(cadetVO.getBirthday());
				userVO.setDepartmentid(cadetVO.getDepartmentid());
				userVO.setEmtype("022");
				userVO.setMobilePhone(cadetVO.getMobile());
				userVO.setPhone(cadetVO.getPhoto_attach_id());
				userVO.setState(0);
				userVO.setIdentityCard(cadetVO.getIdcard());
				userVO.setPhone(cadetVO.getPhone());
				userVO.setPassword("");
				userVO.setParentGroup("自定义");
				userVO.setIsTips(1);
				dbUtil.insert(userVO);
			}
			if(eff>0){
		    	re="审核完成";
		    }else{
		    	re="审核失败";
		    }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView doSetStartDate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
		Connection conn=null;
		DbUtil dbUtil=null;
		
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String prac_real_start_date=request.getParameter("prac_real_start_date");
		//prac_real_end_date=request.getParameter("prac_real_end_date");
		String sql="update hr_prac_info set hr_prac_real_start_date=? where uuid  in ("+uuids+")";
		String formid=request.getParameter("formid");
		String menuid=request.getParameter("menuid");
		int eff=0;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String re="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    eff+=dbUtil.executeUpdate(sql, new Object[]{prac_real_start_date});
		    if(eff>0){
		    	re="修改成功";
		    }else{
		    	re="修改失败";
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		//response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView doUpdateCadetState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
		Connection conn=null;
		DbUtil dbUtil=null;
		
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String cadet_state=request.getParameter("cadet_state");
		//prac_real_end_date=request.getParameter("prac_real_end_date");
		String sql="update hr_prac_info set cadet_state=? where uuid  in ("+uuids+")";
		String formid=request.getParameter("formid");
		String menuid=request.getParameter("menuid");
		int eff=0;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String re="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    eff+=dbUtil.executeUpdate(sql, new Object[]{cadet_state});
		    if(eff>0){
		    	re="修改成功";
		    }else{
		    	re="修改失败";
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		//response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doApplyCheck(HttpServletRequest request,HttpServletResponse response) {
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String idcard=request.getParameter("idcard");
		String name_cn=request.getParameter("name_cn");
		CadetVO cadetVO=null;
		CadetCheckVO cadetCheckVO=new CadetCheckVO();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String report=request.getParameter("report");
		String checkerid=request.getParameter("checkerid");
		String project_case=request.getParameter("project_case");
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			List<CadetVO> cadetVOs=dbUtil.select(CadetVO.class,"select * from {0} where idcard=? and name_cn=?",idcard,name_cn );
			if(cadetVOs.size()<1){
				re="你的实习信息不存在";
				response.getWriter().write(re);
				return null;
			}
			cadetVO=cadetVOs.get(0);
			
			boolean isAllow=false; //dbUtil.select(CadetCheckVO.class, "select uuid from {0} where practiceId=?", cadetVO.getUuid()).size()<1;
			//if(!isAllow)throw new Exception("你已申请过考核，不能再申请");
			try{
				Date prac_start_date=sdf.parse(cadetVO.getHr_prac_real_start_date());
				Calendar calStart=Calendar.getInstance(),calNow=Calendar.getInstance();
				calStart.setTime(prac_start_date);
				long lday=24*3600*1000;
				isAllow=calNow.getTime().getTime()-calStart.getTime().getTime()>30*lday;
					//throw new Exception("未实习未满一个月不能申请考核");
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("实习日期为空或格式错误，请联系相关负责人");
			}
			if(!isAllow){
				throw new Exception("实习未满一个月不能申请考核");
			}
			cadetCheckVO.setUuid(UUID.randomUUID().toString());
			//cadetCheckVO.setP_real_start_time(cadetVO.getPrac_real_start_date());
			cadetCheckVO.setP_real_start_time(cadetVO.getHr_prac_real_start_date());
			cadetCheckVO.setName(cadetVO.getName_cn());
			cadetCheckVO.setSchool(cadetVO.getGrud_uni());
			cadetCheckVO.setPracticeId(cadetVO.getUuid());
			cadetCheckVO.setDepartmentId(cadetVO.getDepartmentid());
			//cadetCheckVO.setProject_case(cadetVO.getGrud_pro_master());
			cadetCheckVO.setSex(cadetVO.getSex());
			//cadetCheckVO.setEducation(cadetVO.getGrud_level());
			cadetCheckVO.setProfession(cadetVO.getGrud_pro_master());
			cadetCheckVO.setReport(report);
			cadetCheckVO.setEducation(cadetVO.getGrud_level());
			cadetCheckVO.setP_startTime(cadetVO.getPrac_start_date());
			cadetCheckVO.setP_endTime(cadetVO.getPrac_end_date());
			cadetCheckVO.setProject_case(project_case);
			cadetCheckVO.setSituation(cadetVO.getCadet_state());
			cadetCheckVO.setUserid(checkerid);
			//cadetCheckVO.setRegister(register);
			eff=dbUtil.insert(cadetCheckVO);
			if(eff<1){
				throw new Exception("你已申请过考核，不能再申请");
			}
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			
				//新发起流程
				ProcessService prs = new ProcessService(conn) ;
				
				String pId = StringUtil.showNull(prs.getPIdByPKeyAndForeignId(CADET_CHECK_PKEY, cadetCheckVO.getUuid())) ;
				
				//保存表单
				//uuid = fdfs.saveFormData(request,response,formId,formEntityId);
				
				if(!"".equals(pId)) {
					//已经发起申请了，直接跳转
					//taskId = jbpmTemplate.getActivityTask(pId).getId() ;
					//response.sendRedirect(request.getContextPath()+"/process.do?method=processTransfer&pKey="
					//					+pKey+"&uuid="+formEntityId+"&taskId="+taskId+"&apply="+apply) ;
					//return null ;
				}
				
				//首次发起申请 
				ProcessDeploy pd = prs.getProcessDeploy(CADET_CHECK_PKEY) ;
				
				Map<String,String> startMap = new HashMap<String, String>() ;
				startMap.put("applyUser", checkerid) ;
				startMap.put("uuid", cadetCheckVO.getUuid() ) ;
				startMap.put("mt_formid", CADET_CHECK_FORMID ) ;
				startMap.put("processName", StringUtil.showNull(pd.getPname()) ) ;
				startMap.put("pKey", CADET_CHECK_PKEY ) ;
				
				ProcessInstance pi = jbpmTemplate.startProcessById(pd.getPdId(),startMap);
				
				//完成第一个结点的任务  lmb修改成开始后不完成第一个节点
				/*
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();  				
				Task myTask = taskList2.get(0) ;
				jbpmTemplate.completeTask(myTask.getId(),nextNodeName) ;
				*/
				//保存流程轨迹
				ProcessForm pf = new ProcessForm() ;
				pf.setpId(pi.getId()) ;
				pf.setKey("意见") ;
				pf.setValue("新发起申请") ;
				pf.setDealTime(StringUtil.getCurDateTime()) ;
				pf.setDealUserId(checkerid) ;
				pf.setNodeName("保存") ;
				pf.setFormId(CADET_CHECK_FORMID) ;
				pf.setFormEntityId(cadetCheckVO.getUuid()) ;
				prs.addProcessForm(pf) ;
				
				//保存申请记录
				ProcessApply pa = new ProcessApply() ;
				pa.setId(StringUtil.getUUID()) ;
				pa.setPkey(CADET_CHECK_PKEY) ;
				pa.setPid(pi.getId()) ;
				pa.setForeignId(cadetCheckVO.getUuid()) ;
				pa.setPname(pd.getPname()) ;
				pa.setApplyTime(StringUtil.getCurDateTime()) ;
				pa.setApplyUserId(checkerid) ;
				prs.addProcessApply(pa) ;
			    re="已成功发起考核申请！";
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		try {
			response.getWriter().write(re);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public ModelAndView modify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String opt = request.getParameter("opt");
		String idcard=request.getParameter("idcard");
		String name_cn=request.getParameter("name_cn");
		ModelAndView modelandview=null;
		Connection conn=new DBConnect().getConnect();
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		CadetVO cadetVO=null;
		CadetCheckVO cadetCheckVO=null;
		try{
			dbUtil=new DbUtil(conn);
			List<CadetVO> cadetVOs=dbUtil.select(CadetVO.class, "select * from {0} where idcard=? and name_cn=? ",idcard,name_cn);
            if(cadetVOs.size()<1){
            	webUtil.alert("实习信息不存在");
            	response.getWriter().write("<script>window.parent.close();</script>");
            	return null;
            }
			cadetVO=cadetVOs.get(0);
			if("1".equals(opt)){
				
				
				modelandview = new ModelAndView(Employee);
				
			}else if("2".equals(opt)){
				
				String re="";
				//cadetCheckVO.getPracticeId()
				cadetCheckVO=dbUtil.load(CadetCheckVO.class, "practiceId",cadetVO.getUuid());
				   if(cadetCheckVO.getUuid()==null){
				   	 re="尚未发起实习考核申请";
				   }else if(!StringUtil.isBlank(cadetCheckVO.getSuggestion())){
				  	 re="指导老师已经给出评价,不能再次修改";
				   }
				if(re.length()>0){
	            	webUtil.alert(re);
	            	response.getWriter().write("<script>window.parent.close();</script>");
	            	return null;
				}
				modelandview=new ModelAndView(Jsp.modify_projectcase.getPath());
				modelandview.addObject("vo", cadetVO);
				modelandview.addObject("checkVO", cadetCheckVO);
			}else{
			  //  CadetVO cadetVO=cadetVOs.size()>0?cadetVOs.get(0):new CadetVO();
			  //  if(cadetVO.getUuid()==null){
			//	    cadetVO.setIdcard(idcard);
			//	    cadetVO.setName_cn(name_cn);
			//	    cadetVO.setDepartmentid(CADET_DEPARTMENID);
			 //   }
			    modelandview = new ModelAndView(MODIFY);
			    modelandview.addObject("vo", cadetVO);
				modelandview.addObject("hasAssigned",!CADET_DEPARTMENID.equals(cadetVO.getDepartmentid()));
	
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelandview;
	}	
	
	public ModelAndView correct(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
  
		response.setContentType("text/html;charset=utf-8");
		ModelAndView modelandview = new ModelAndView(LOGIN);
		
		String uuid = request.getParameter("uuid");
		
		String address = request.getParameter("address");
		String email = request.getParameter("email");
		String mobile = request.getParameter("mobile");
		String urgent_conn_name = request.getParameter("urgent_conn_name");
		String urgent_conn_relation = request.getParameter("urgent_conn_relation");
		String urgent_conn_phone = request.getParameter("urgent_conn_phone");
		
		WebUtil webUtil = null;
		CadetVO cadetvo = null;
		Connection conn = null;
		DbUtil dbutil = null;
		int i = 0;
		try{
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false);
			dbutil = new DbUtil(conn);
			webUtil =new WebUtil(request, response);
//			cadetvo = dbutil.load(CadetVO.class, uuid);
//		    
//			cadetvo.setAddress(address);
//			cadetvo.setEmail(email);
//			cadetvo.setMobile(mobile);
//			cadetvo.setUrgent_conn_name(urgent_conn_name);
//			cadetvo.setUrgent_conn_relation(urgent_conn_relation);
//			cadetvo.setUrgent_conn_phone(urgent_conn_phone);
			String sql="update hr_prac_info set address = '"+address+"',email='"+email+"' ,mobile='"+mobile+"', " +
					"urgent_conn_name='"+urgent_conn_name+"',urgent_conn_relation='"+urgent_conn_relation+
					"',urgent_conn_phone='"+urgent_conn_phone+"' where uuid='"+uuid+"' ";
//			System.out.println(sql);
			i = dbutil.executeUpdate(sql);
			conn.commit();
		     webUtil.alert("修改成功");

		}catch (Exception e) {
			e.printStackTrace();
	    	webUtil.alert("修改失败，请重新操作");
		} finally {
			DbUtil.close(conn);
		}
		//response.sendRedirect("/login.do");
		response.getWriter().write("<script>parent.window.close()</script>");
		return modelandview;
	}	
	
	

	//批量修改是否需要考核
	public ModelAndView doUpdateCadetVerify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
		Connection conn=null;
		DbUtil dbUtil=null;
		
		String uuids=request.getParameter("uuids");
		String pro=request.getParameter("pro");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update hr_prac_info set verify='"+pro+"'  where uuid  in ("+uuids+")";
		int eff=0;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String re="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    eff+=dbUtil.executeUpdate(sql);
		    if(eff>0){
		    	re="修改成功";
		    }else{
		    	re="修改失败";
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		//response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		response.getWriter().write(re);
		return null;
	}
	


	//判断是否可以填写入职申请
	public ModelAndView beforeCadet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
		Connection conn=null;
		DbUtil dbUtil=null;
		request.setCharacterEncoding("utf-8");
		
		String cardNum=request.getParameter("cardNum");
		String cardName=request.getParameter("cardName");
		String vtype=request.getParameter("vtype");
		//是有这个人
		//String sql1="select * from hr_prac_info where name_cn ='"+cardName+"' and idcard='"+cardNum+"'";
		//是否能够填写入职申请
		//String sql="select * from hr_prac_info where name_cn ='"+cardName+"' and idcard='"+cardNum+"' and (verify='否' or cadet_state ='协议已签' )";
		CadetCheckVO cadetCheckVO=null;
		CadetVO cadetVO=null;
		int eff=0;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String re="不可以发起申请";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			//String b1=dbUtil.queryForString(sql1);
			List<CadetVO> cadetVOs=dbUtil.select(CadetVO.class,"select * from {0} where name_cn=? and idcard=?" , cardName,cardNum);
			if(cadetVOs.size()<1)
			{
				re="请填写正确的身份信息";
				response.getWriter().write(re);
				return null;
			}
			
			cadetVO=cadetVOs.get(0);
			//if("a".equals(vtype)){
			    if("协议已签".equals(cadetVO.getCadet_state())||"否".equals(cadetVO.getVerify())){
			    	re="true";
			    }else{
			    	re="未签订协议或者还需考核";
			    }
				
			//}else if("b".equals(vtype)){
			 //    cadetCheckVO=dbUtil.load(CadetCheckVO.class, cadetVO.getUuid());
			  //   if(cadetCheckVO.getUuid()==null){
			   // 	 re="尚未发起实习考核申请";
			   //  }else if(!StringUtil.isBlank(cadetCheckVO.getSuggestion())){
			   // 	 re="指导老师已经给出评价,不能再次修改";
			   //  }else{
			   // 	 re="true";
			   //  }
			//}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		//response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		response.getWriter().write(re);
		return null;
	}

	
	//跳转到employeeJsp页面
	public ModelAndView employeeJspSkip(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String opt = request.getParameter("opt");
		String cardNum = request.getParameter("cardNum");
		String cardName = request.getParameter("cardName");
		String hiddentype = request.getParameter("hiddentype");
		if(hiddentype.equals("hiddentype")){
			request.setAttribute("wherefrom", "ok");
		}else{
			request.setAttribute("wherefrom", "no");
		}
		request.setAttribute("cardNum", cardNum);
		request.setAttribute("cardName", cardName);
		request.setAttribute("opt", opt);
		Connection conn=new DBConnect().getConnect();
		DbUtil dbUtil=new DbUtil(conn);
		CadetVO cadet=dbUtil.load(CadetVO.class, "idcard", cardNum);

		request.setAttribute("cadet", cadet);
		ModelAndView modelandview = new ModelAndView(EMPLOYEEJSP);
		return modelandview;
	}
	
	
	public ModelAndView beforeCadetSkip(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(BEFORECADETSKIP);
		return modelandview;
	}
	
	
	/**
	 * 批量设置离所实习生
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doLeave(HttpServletRequest request,HttpServletResponse response)throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		String leaveSu="是";
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update hr_prac_info set leavesu=? where uuid in ("+uuids+")";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{leaveSu});
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
	
	
	public ModelAndView updateCadetCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		CadetCheckVO cadetCheckVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			cadetCheckVO=dbUtil.load(CadetCheckVO.class, uuid);
			cadetCheckVO=webUtil.evalObject(cadetCheckVO);
			eff+=dbUtil.update(cadetCheckVO);
	        if(eff>0){
	        	re="修改成功";
	        }else{
	        	re="修改失败";
	        }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		webUtil.alert(re);
		response.getWriter().write("<script>window.parent.close()</script>");
		return null;
	}
}
