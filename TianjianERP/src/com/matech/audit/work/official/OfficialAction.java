package com.matech.audit.work.official;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.ProcessInstance;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.form.FormDefineService;
import com.matech.audit.service.official.OfficialService;
import com.matech.audit.service.official.model.Official;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;


/**
 * 员工转正
 * @author Administrator
 *
 */
public class OfficialAction extends MultiActionController {
	
	
	private final static  String MAIN_VIEW="/official/main.jsp";		//主页面
	private final static String EDIT_VIEW="/official/edit.jsp";		//转正编辑页面
	public final static String FORMID="5074251f-ba69-47fc-8e25-0b8be8e3f8e6"; //员工转正表单
	public final static String PKEY="b9e982ac-9827-4362-9500-9bd5d6da4968"; //
	

	
	/**
	 * 进入转正登记页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView main(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView =new ModelAndView(MAIN_VIEW);
		
		return modelAndView;
	}
	
	/**
	 * 进入转正申请页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView =new ModelAndView(EDIT_VIEW);
				
		String cardNum=StringUtil.showNull(request.getParameter("cardNum"));
		
		Connection conn=null;
		
		try{
			conn=new DBConnect().getConnect();
			
			DbUtil dbUtil=new DbUtil(conn);
			
			EmployeeVO employee=dbUtil.load(EmployeeVO.class,"idcard",cardNum);
			
			modelAndView.addObject("employee",employee);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 提交申请
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView apply(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		request.setCharacterEncoding("utf-8");
		
		String checkUser=StringUtil.showNull(request.getParameter("checker_id"));
		String name=StringUtil.showNull(request.getParameter("name"));
		String sex=StringUtil.showNull(request.getParameter("sex"));
		String birthday=StringUtil.showNull(request.getParameter("birthday"));
		String departmentId=StringUtil.showNull(request.getParameter("departmentId"));
		String joinTime=StringUtil.showNull(request.getParameter("joinTime"));
		String entryTime=StringUtil.showNull(request.getParameter("entryTime"));
		String pactlimit=StringUtil.showNull(request.getParameter("pactlimit"));
		String pactlimitEnd=StringUtil.showNull(request.getParameter("pactlimitEnd"));
		String education=StringUtil.showNull(request.getParameter("education"));
		String school=StringUtil.showNull(request.getParameter("school"));
		String specialty=StringUtil.showNull(request.getParameter("specialty"));
		String cardNum=StringUtil.showNull(request.getParameter("cardNum"));
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		
		Date d=df.parse(entryTime);
		 
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH,2);
		Date date=cal.getTime();
		String zz_Date=df.format(date);
		
		String uuid=StringUtil.getUUID();
		
		Official official=new Official();
		official.setUuid(uuid);
		official.setName(name);
		official.setCardNum(cardNum);
		official.setSex(sex);
		official.setBirthday(birthday);
		official.setEducation(education);
		official.setSpecialty(specialty);
		official.setDepartmentId(departmentId);
		official.setEntryTime(entryTime);
		official.setJoinTime(joinTime);
		official.setPactlimitEnd(pactlimitEnd);
		official.setPactlimit(pactlimit);
		official.setSchool(school);
		official.setState("审核未完成");
		official.setZz_date(zz_Date);
		//official.setResult("4");
		
		
		
		//WebUtil webUtil=new WebUtil(request, response);
		
		Connection conn=null;
		
		DbUtil dbUtil=null;
		
		//Official official=webUtil.evalObject(Official.class);
		
		UserSession userSession=new UserSession();
		
		userSession.setUserId(""+checkUser);
		request.getSession().setAttribute("userSession", userSession);
		
		String result="";
		
		
		
		try{
			
			conn=new DBConnect().getConnect();
			
			dbUtil=new DbUtil(conn);
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			
			//发起流程
			//FormDefineService fds= new FormDefineService(conn);
			ProcessService prs = new ProcessService(conn);
			
			//保存表单
			//String formEntityId = StringUtil.showNull(request.getParameter("formEntityId")) ;//直接申请或修改时会把这个参数传进来
			//uuid = fds.saveFormData(request,response,FORMID,formEntityId);
			dbUtil.insert(official);

			//首次发起申请
			ProcessDeploy pd = prs.getProcessDeploy(PKEY);
			Map<String,String> startMap = new HashMap<String, String>();
			startMap.put("applyUser", checkUser) ;
			startMap.put("uuid",uuid) ;
			startMap.put("mt_formid", FORMID) ;
			startMap.put("processName", StringUtil.showNull(pd.getPname()) ) ;
			startMap.put("pKey",PKEY) ;
			
			ProcessInstance pi = jbpmTemplate.startProcessById(pd.getPdId(),startMap);
			
			//保存流程轨迹
			ProcessForm pf = new ProcessForm() ;
			pf.setpId(pi.getId()) ;
			pf.setKey("意见") ;
			pf.setValue("新发起申请") ;
			pf.setDealTime(StringUtil.getCurDateTime()) ;
			pf.setDealUserId(checkUser) ;
			pf.setNodeName("保存") ;
			pf.setFormId(FORMID) ;
			pf.setFormEntityId(uuid) ;
			prs.addProcessForm(pf) ;
			
			//保存申请记录
			ProcessApply pa = new ProcessApply() ;
			pa.setId(StringUtil.getUUID()) ;
			pa.setPkey(PKEY) ;
			pa.setPid(pi.getId()) ; 
			pa.setForeignId(uuid) ;
			pa.setPname(pd.getPname()) ;
			pa.setApplyTime(StringUtil.getCurDateTime()) ;
			UserVO userVO=dbUtil.load(UserVO.class, "identityCard", official.getCardNum());
			pa.setApplyUserId(String.valueOf(userVO.getId()));
			pa.setForeignId(uuid);
			prs.addProcessApply(pa) ;
		    //result="已成功发起转正申请！";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write("<script>alert(\"已成功发起转正申请！\");parent.window.close();</script>");
		return null;
		
		
	}
	
	/**
	 * 填写申请前的验证
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkApply(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		request.setCharacterEncoding("utf-8");
		
		Connection conn=null;
		
		DbUtil dbUtil=null;
		
		String result="不符合发起转正申请的条件";
		
		PrintWriter out=null;
		
		String name=StringUtil.showNull(request.getParameter("name"));
		String cardNum=StringUtil.showNull(request.getParameter("cardNum"));
		
		
		
		//检查入职表中是否存在这个人
		String sql="SELECT * FROM hr_employee_register WHERE NAME='"+name+"' and idcard='"+cardNum+"'";
		
		//检查否有发起申请的条件
		String strSql="SELECT * FROM hr_employee_register WHERE NAME='"+name+"' and idcard='"+cardNum+"' AND (remarks='已审核' or isneed='1')";
		
		//检查是否已经发起过申请
		String existSql="SELECT * FROM hr_employee_official WHERE NAME='"+name+"' and cardNum='"+cardNum+"'";
		
		
		try{
			conn=new DBConnect().getConnect();
			
			dbUtil=new DbUtil(conn);
			
			out=response.getWriter();
			
			String str=StringUtil.showNull(dbUtil.queryForString(sql));
			
			if(str.equals("")){
				result="还未发起入职申请!";
			}else{
			String str1=StringUtil.showNull(dbUtil.queryForString(strSql));
			
			if(str1.equals("")){
				result="未通过入职考核!";
			}else{
				OfficialService officialServ=new OfficialService(conn);
				String backResult=officialServ.checkDate(cardNum);
				
				if(backResult.equals("0")){
					String str2=StringUtil.showNull(dbUtil.queryForObject(existSql));
					
					if("".equals(str2)){
						result="yes";
					}else{
						result="您已经发起过申请,无需重复申请!";
					}
					
				}else{
					result="所内要求进所日期要满45天才能发起转正申请,请到期再发起申请!";
				}

			}
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		out.write(result);
		return null;
		
		
	}
	
	/**
	 * 修改考核状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateState(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		String resultState=StringUtil.showNull(request.getParameter("resultState"));
		String remark=StringUtil.showNull(request.getParameter("remark"));
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update hr_employee_official set result=?,remark=? where uuid in ("+uuids+")";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{resultState,remark});
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
	 * 修改转正时间
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateDate(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		String zDate=StringUtil.showNull(request.getParameter("zDate"));
		
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update hr_employee_official set zz_Date=? where uuid in ("+uuids+")";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{zDate});
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
	 * 设置考核结果
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doCheck(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		String flag="0";
		
		String uuids=request.getParameter("uuids");
		uuids=StringUtil.trim(uuids, ",");
		String sql="update oa_practice_check set flag=? where uuid in ("+uuids+")";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{flag});
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
 
	
}
