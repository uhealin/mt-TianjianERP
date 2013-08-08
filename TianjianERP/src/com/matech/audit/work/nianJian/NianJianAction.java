package com.matech.audit.work.nianJian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.nianJian.model.Accoutant;
import com.matech.audit.service.nianJian.model.ProjectReport;
import com.matech.audit.service.nianJian.model.SocietyCheck;
import com.matech.audit.service.nianJian.model.TaxCheck;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.print.PrintSetup;



/**
 * 年检申报
 * @author Administrator
 *
 */
public class NianJianAction extends MultiActionController{
	//日志
	Log log=LogFactory.getLog(NianJianAction.class);
	private final String LIST_VIEW="/nianJian/list.jsp";
	private final String EDIT_VIEW="/nianJian/edit.jsp";
	private final String TAX_VIEW="/nianJian/taxList.jsp";
	private final String TAX_EDIT="/nianJian/taxEdit.jsp";
	private final String RECORD_VIEW="/nianJian/recordList.jsp";
	/**
	 * 人员列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(LIST_VIEW);
		
		UserSession userSession=(UserSession) request.getSession().getAttribute("userSession");
		
		String userId=userSession.getUserId();
		
		String departmentId=userSession.getUserAuditDepartId();
		
		DataGridProperty pp =new DataGridProperty();
		
		pp.setTableID("cpaNianJianList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("注册会计师年检");
	    
	    String sql="";
	    
	    
	    if(departmentId.equals("1247")){
	    sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.ryzhushi,u.reportType,u.alreadyCreate "+
			" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid where 1=1 ${departmentId} ${name} ${sex} ${ryzhushi} ${reportType} ${alreadyCreate}";
	    }else{
	    	 sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.ryzhushi,u.reportType,u.alreadyCreate "+
				" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid where 1=1 and u.id='"+userId+"' ${departmentId} ${name} ${sex} ${ryzhushi} ${reportType} ${alreadyCreate} ";
	    }
	  
	   
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("departmentid") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    
	    
	    pp.addColumn("所属部门","departname");
	    pp.addColumn("姓名","name");
	    pp.addColumn("性别","usex");
	    pp.addColumn("荣誉注师","ryzhushi");
	    pp.addColumn("主要项目清单生成模式","reportType");
	    pp.addColumn("已生成过年检表","alreadyCreate");
	    
	    pp.setColumnWidth("12,12,10,8,15,12");
	    
	    pp.addSqlWhere("name", "and u.name like '%${name}%'");
	    pp.addSqlWhere("departmentId", "and departmentId='${departmentId}'");
	    pp.addSqlWhere("sex", "and u.sex like '%${sex}%'");
	    pp.addSqlWhere("ryzhushi", "and ryzhushi like '%${ryzhushi}%'");
	    pp.addSqlWhere("reportType", "and u.reportType like '%${reportType}%'");
	    pp.addSqlWhere("alreadyCreate", "and alreadyCreate like '%${alreadyCreate}%'");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
		
	}
	
	/**
	 * 修改项目清单生成模式
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView modifyProject(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int num=0;
		String result="";
		
		
		String uuid=request.getParameter("uuid");
		
		
		String state=StringUtil.showNull(request.getParameter("state"));
		
		
		String sql="update k_user set reportType=? where id='"+uuid+"'";

		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			num+=dbUtil.executeUpdate(sql, new Object[]{state});
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
	 * 生成注册年检表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(EDIT_VIEW);
		
		String userId=StringUtil.showNull(request.getParameter("chooseValue"));
		
		String opt=StringUtil.showNull(request.getParameter("opt"));
		
		String currentDate=StringUtil.getCurDate();
		
		Calendar cal = Calendar.getInstance();
		
		int year=cal.get(Calendar.YEAR)-1;
		
		int lastYear=year-1;
		
		Connection conn=null;
		
		
		DbUtil dbUtil=null;
		
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
			String sql="select * from z_annual_survey where userid=? and year=?";
			List list=dbUtil.select(Accoutant.class, sql,new Object[]{userId,year});
			System.out.println(list.size());
			if(list.size()>0){
				
				Accoutant accoutant=(Accoutant)list.get(0);
				
				String uuid=accoutant.getUuid();
				
				String sqlPrj="select * from z_project_report where mainformid=? order by rand() desc limit 10 ";
				
				List prjList=dbUtil.select(ProjectReport.class, sqlPrj,new Object[]{uuid});
				
				modelAndView.addObject("accoutant",accoutant);
				modelAndView.addObject("prjList",prjList);
				modelAndView.addObject("flag","edit");
				
				
			}else{
				UserVO user=dbUtil.load(UserVO.class,"id",userId);
				Accoutant accoutant=new Accoutant();
				accoutant.setUuid(StringUtil.getUUID());
				accoutant.setUserId(userId);
				accoutant.setName(user.getName());
				accoutant.setPolitics_Status(user.getPolitics());
				accoutant.setAllJob(user.getRank());
				accoutant.setFillDate(currentDate);
				modelAndView.addObject("accoutant",accoutant);
				modelAndView.addObject("flag","add");
				
			}
			modelAndView.addObject("year",year);
			modelAndView.addObject("lastYear",lastYear);
			modelAndView.addObject("opt",opt);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
		
	}
	/**
	 * 保存生成的注师年检申请表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		
		
		String accountantOfficeName=StringUtil.showNull(request.getParameter("accountantOfficeName"));
		
		String fillDate=StringUtil.showNull(request.getParameter("fillDate"));
		
		String cpaId=StringUtil.showNull(request.getParameter("cpaId"));
		
		String name=StringUtil.showNull(request.getParameter("name"));
		
		String isFull=StringUtil.showNull(request.getParameter("isFull"));
		
		String politics_Status=StringUtil.showNull(request.getParameter("politics_Status"));
		
		String rankId=StringUtil.showNull(request.getParameter("rankId"));
		
		String education=StringUtil.showNull(request.getParameter("rankId"));
		
		String approvalNum=StringUtil.showNull(request.getParameter("approvalNum"));
		
		String registerDate=StringUtil.showNull(request.getParameter("registerDate"));
		
		String npcOrCPPCC=StringUtil.showNull(request.getParameter("npcOrCPPCC"));
		
		String allJob=StringUtil.showNull(request.getParameter("allJob"));
		
		String reward=StringUtil.showNull(request.getParameter("reward"));
		
		String lastYear_criminal=StringUtil.showNull(request.getParameter("lastYear_criminal"));
		
		String responsibility=StringUtil.showNull(request.getParameter("responsibility"));
		
		String lastYear_industry=StringUtil.showNull(request.getParameter("lastYear_industry"));
		
		String lastYear_administrative=StringUtil.showNull(request.getParameter("lastYear_administrative"));
		
		String lastYear_eduHours=StringUtil.showNull(request.getParameter("lastYear_eduHours"));
		
		String remark=StringUtil.showNull(request.getParameter("remark"));
		
		String flag=StringUtil.showNull(request.getParameter("flag"));
		
		String uuid=StringUtil.showNull(request.getParameter("uuid"));
		
		String userId=StringUtil.showNull(request.getParameter("userId"));
		
		String gazg=StringUtil.showNull(request.getParameter("gazg"));
		
		String gxlJob=StringUtil.showNull(request.getParameter("gxlJob"));
		
		String glzg=StringUtil.showNull(request.getParameter("glzg"));
		
		String phone=StringUtil.showNull(request.getParameter("phone"));
		
		String year=StringUtil.showNull(request.getParameter("year"));
		
		String opt=StringUtil.showNull(request.getParameter("opt"));
		
		Accoutant accoutant=new Accoutant();
		accoutant.setAccountantOfficeName(accountantOfficeName);
		accoutant.setAllJob(allJob);
		accoutant.setApprovalNum(approvalNum);
		accoutant.setCpaId(cpaId);
		accoutant.setName(name);
		accoutant.setGlzg(glzg);
		accoutant.setGxlJob(gxlJob);
		accoutant.setEducation(education);
		accoutant.setFillDate(fillDate);
		accoutant.setGazg(gazg);
		accoutant.setLastYear_administrative(lastYear_administrative);
		accoutant.setLastYear_criminal(lastYear_criminal);
		accoutant.setLastYear_eduHours(lastYear_eduHours);
		accoutant.setLastYear_industry(lastYear_industry);
		accoutant.setRankId(rankId);
		accoutant.setUserId(userId);
		accoutant.setRegisterDate(registerDate);
		accoutant.setReward(reward);
		accoutant.setGazg(gazg);
		accoutant.setPhone(phone);
		accoutant.setResponsibility(responsibility);
		accoutant.setYear(year);
		accoutant.setRemark(remark);
		accoutant.setPolitics_Status(politics_Status);
		accoutant.setIsFull(isFull);
		accoutant.setNpcOrCPPCC(npcOrCPPCC);
		
		Connection conn=null;
		
		DbUtil dbUtil=null;
		String[] uuids=request.getParameterValues("subId");
		String[] prjIds=request.getParameterValues("prjId");
		String[] prjNames=request.getParameterValues("prjName");
		String[] reportIds=request.getParameterValues("reportId");
		String[] reportNames=request.getParameterValues("reportName");
		
		ProjectReport projectReport=null;
		
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			if(flag.equals("add")){
				uuid=StringUtil.getUUID();
				accoutant.setUuid(uuid);
				dbUtil.insert(accoutant);
				String sql="update k_user set alreadyCreate=? where id=?";
				dbUtil.executeUpdate( sql,new Object[]{"是",userId});
				for(int i=0;i<prjIds.length;i++){
					projectReport=new ProjectReport();
					projectReport.setMainformId(uuid);
					projectReport.setPrjId(prjIds[i]);
					projectReport.setPrjName(prjNames[i]);
					projectReport.setReportId(reportIds[i]);
					projectReport.setReportName(reportNames[i]);
					projectReport.setUuid(StringUtil.getUUID());
					dbUtil.insert(projectReport);
				}
				
			}else{
				accoutant.setUuid(uuid);
				
				dbUtil.update(accoutant);
				
				for(int i=0;i<prjIds.length;i++){
					projectReport=new ProjectReport();
					projectReport.setMainformId(uuid);
					projectReport.setPrjId(prjIds[i]);
					projectReport.setPrjName(prjNames[i]);
					projectReport.setReportId(reportIds[i]);
					projectReport.setReportName(reportNames[i]);
					projectReport.setUuid(uuids[i]);
					dbUtil.update(projectReport);
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		response.sendRedirect("nianJian.do?method=edit&chooseValue="+userId+"&opt="+opt);
		return null;
		
	}
	
	
	
	/**
	 * 人员列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listTax(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(TAX_VIEW);
		
		UserSession userSession=(UserSession) request.getSession().getAttribute("userSession");
		
		String userId=userSession.getUserId();
		
		String departmentId=userSession.getUserAuditDepartId();
		
		DataGridProperty pp =new DataGridProperty();
		
		pp.setTableID("taxNianJianList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("注册税务师年检");
	    
	    String sql="";
	    
	    
	    if(departmentId.equals("1247")){
	    sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.taxRegister,u.reportType,u.taxCreate "+
			" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid where 1=1 ${departmentId} ${name} ${sex} ${taxRegister} ${reportType} ${taxCreate}";
	    }else{
	    	 sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.taxRegister,u.reportType,u.taxCreate "+
				" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid where 1=1 and u.id='"+userId+"' ${departmentId} ${name} ${sex} ${taxRegister} " +
						" ${reportType} ${taxCreate}";
	    }
	  
	   
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("departmentid") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("radio");
	    pp.setWhichFieldIsValue(1);
	    
	    
	    pp.addColumn("所属部门","departname");
	    pp.addColumn("姓名","name");
	    pp.addColumn("性别","usex");
	    pp.addColumn("荣誉注税","taxRegister");
	    pp.addColumn("主要项目清单生成模式","reportType");
	    pp.addColumn("已生成过年检表","taxCreate");
	    
	    pp.setColumnWidth("12,12,10,8,15,12");
	    
	    pp.addSqlWhere("name", "and u.name like '%${name}%'");
	    pp.addSqlWhere("departmentId", "and departmentId='${departmentId}'");
	    pp.addSqlWhere("sex", "and u.sex like '%${sex}%'");
	    pp.addSqlWhere("taxRegister", "and u.taxRegister like '%${taxRegister}%'");
	    pp.addSqlWhere("reportType", "and u.reportType like '%${reportType}%'");
	    pp.addSqlWhere("taxCreate", "and u.taxCreate like '%${taxCreate}%'");
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
		
	}
	
	/**
	 * 生成注税年检表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editTax(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(TAX_EDIT);
		
		String userId=StringUtil.showNull(request.getParameter("chooseValue"));
		
		String opt=StringUtil.showNull(request.getParameter("opt"));
		
		
		Calendar cal = Calendar.getInstance();
		
		int year=cal.get(Calendar.YEAR)-1;
		
		int lastYear=year-1;
		
		Connection conn=null;
		
		
		DbUtil dbUtil=null;
		
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
			
			String sql="select * from z_tax_check where userid=? and year=?";
			
			List list=dbUtil.select(TaxCheck.class, sql,new Object[]{userId,year});
			
			String sqlS="select * from z_society_check where userid=? and year=?";
			
			List listS=dbUtil.select(SocietyCheck.class, sqlS,new Object[]{userId,year});
			
			if(list.size()>0){
				
				TaxCheck tax=(TaxCheck)list.get(0);
				
				SocietyCheck society=(SocietyCheck) listS.get(0);
				
				String uuid=tax.getUuid();
				
				String sqlPrj="select * from z_project_report where mainformid=? order by rand() desc limit 10";
				
				List prjList=dbUtil.select(ProjectReport.class, sqlPrj,new Object[]{uuid});
				
				modelAndView.addObject("tax",tax);
				modelAndView.addObject("society",society);
				modelAndView.addObject("prjList",prjList);
				modelAndView.addObject("flag","edit");
				
				
			}else{
				UserVO user=dbUtil.load(UserVO.class,"id",userId);
				TaxCheck tax=new TaxCheck();
				tax.setUuid(StringUtil.getUUID());
				tax.setUserId(userId);
				tax.setName(user.getName());
				tax.setCardNum(user.getIdentityCard());
				tax.setSex(user.getSex());
				tax.setBirthday(user.getBornDate());
				tax.setPhone(user.getPhone());
				tax.setEducation(user.getEducational());
	
				SocietyCheck society=new SocietyCheck();
				society.setUserId(userId);
				society.setSex(user.getSex());
				society.setName(user.getName());
				society.setNation(user.getNation());
				society.setAddress(user.getAddress());
				society.setCardNum(user.getIdentityCard());
				society.setCurrentAddress(user.getHouse());
				society.setEducation(user.getEducational());
				society.setPolitical(user.getPolitics());
				society.setPostcode(user.getPost());
				society.setBirthday(user.getBornDate());
				society.setPhone(user.getPhone());
				
				modelAndView.addObject("tax",tax);
				modelAndView.addObject("society",society);
				modelAndView.addObject("flag","add");
				
			}
			modelAndView.addObject("year",year);
			modelAndView.addObject("lastYear",lastYear);
			modelAndView.addObject("opt",opt);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
		
	}
	
	/**
	 * 保存注税年检表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveTax(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		//执业注册税务师年检登记表
		
		Connection conn=null;
		DbUtil dbUtil=null;
		
		String uuid=StringUtil.showNull(request.getParameter("uuid"));
		
		String name=StringUtil.showNull(request.getParameter("name"));
		
		String sex=StringUtil.showNull(request.getParameter("sex"));
		
		String birthday=StringUtil.showNull(request.getParameter("birthday"));
		
		String education=StringUtil.showNull(request.getParameter("education"));
		
		String cardNum=StringUtil.showNull(request.getParameter("cardNum"));
		
		String unit=StringUtil.showNull(request.getParameter("unit"));
		
		String phone=StringUtil.showNull(request.getParameter("phone"));
		
		String registId=StringUtil.showNull(request.getParameter("registId"));
		
		String rate=StringUtil.showNull(request.getParameter("rate"));
		
		String rank=StringUtil.showNull(request.getParameter("rank"));
		
		String referenceNo=StringUtil.showNull(request.getParameter("referenceNo"));
		
		String selfCondition=StringUtil.showNull(request.getParameter("selfCondition"));
		
		String userId=StringUtil.showNull(request.getParameter("userId"));
		
		String year=StringUtil.showNull(request.getParameter("year"));
		
		TaxCheck tax=new TaxCheck();
		tax.setName(name);
		tax.setSelfCondition(selfCondition);
		tax.setSex(sex);
		tax.setBirthday(birthday);
		tax.setEducation(education);
		tax.setCardNum(cardNum);
		tax.setUnit(unit);
		tax.setPhone(phone);
		tax.setRegistId(registId);
		tax.setRate(rate);
		tax.setRank(rank);
		tax.setReferenceNo(referenceNo);
		tax.setUserId(userId);
		tax.setYear(year);
		
		//中国注册税务师协会执业会员年度检查登记表
		
		String sUuid=StringUtil.showNull(request.getParameter("sUuid"));
		
		String sName=StringUtil.showNull(request.getParameter("sName"));
		
		String sSex=StringUtil.showNull(request.getParameter("sSex"));
		
		String sBirthday=StringUtil.showNull(request.getParameter("sBirthday"));
		
		String currentAddress=StringUtil.showNull(request.getParameter("currentAddress"));
		
		String nation=StringUtil.showNull(request.getParameter("nation"));
		
		String sEducation=StringUtil.showNull(request.getParameter("sEducation"));
		
		String sCardNum=StringUtil.showNull(request.getParameter("sCardNum"));
		
		String specialty=StringUtil.showNull(request.getParameter("specialty"));
		
		String political=StringUtil.showNull(request.getParameter("political"));
		
		String workYear=StringUtil.showNull(request.getParameter("workYear"));
		
		String zhiyezgNum=StringUtil.showNull(request.getParameter("zhiyezgNum"));
		
		String zhiyebaNum=StringUtil.showNull(request.getParameter("zhiyebaNum"));
		
		String zhiyehyNum=StringUtil.showNull(request.getParameter("zhiyehyNum"));
		
		String sPhone=StringUtil.showNull(request.getParameter("sPhone"));
		
		String address=StringUtil.showNull(request.getParameter("address"));
		
		String postcode=StringUtil.showNull(request.getParameter("postcode"));
		
		String zhiyeUnit=StringUtil.showNull(request.getParameter("zhiyeUnit"));
		
		String sRank=StringUtil.showNull(request.getParameter("sRank"));
		
		String lastResult=StringUtil.showNull(request.getParameter("lastResult"));
		
		String feePay=StringUtil.showNull(request.getParameter("feePay"));
		
		String continueEducate=StringUtil.showNull(request.getParameter("continueEducate"));
		
		String punishment=StringUtil.showNull(request.getParameter("punishment"));
		
		String badRecord=StringUtil.showNull(request.getParameter("badRecord"));
		
		SocietyCheck so=new SocietyCheck();
		so.setName(sName);
		so.setSex(sSex);
		so.setSpecialty(specialty);
		so.setBirthday(sBirthday);
		so.setBadRecord(badRecord);
		so.setEducation(sEducation);
		so.setAddress(address);
		so.setRank(sRank);
		so.setPhone(sPhone);
		so.setCardNum(sCardNum);
		so.setContinueEducate(continueEducate);
		so.setCurrentAddress(currentAddress);
		so.setFeePay(feePay);
		so.setPunishment(punishment);
		so.setPostcode(postcode);
		so.setPolitical(political);
		so.setZhiyebaNum(zhiyebaNum);
		so.setZhiyehyNum(zhiyehyNum);
		so.setZhiyeUnit(zhiyeUnit);
		so.setZhiyezgNum(zhiyezgNum);
		so.setLastResult(lastResult);
		so.setWorkYear(workYear);
		so.setNation(nation);
		so.setUserId(userId);
		so.setYear(year);
		
		//项目表
		String[] uuids=request.getParameterValues("subId");
		String[] prjIds=request.getParameterValues("prjId");
		String[] prjNames=request.getParameterValues("prjName");
		String[] reportIds=request.getParameterValues("reportId");
		String[] reportNames=request.getParameterValues("reportName");
		
		String flag=StringUtil.showNull(request.getParameter("flag"));
		String opt=StringUtil.showNull(request.getParameter("opt"));
		
		ProjectReport projectReport=null;
		
		try{
			conn=new DBConnect().getConnect();
			
			dbUtil=new DbUtil(conn);
			
			if(flag.equals("add")){
				projectReport=new ProjectReport();
				uuid=StringUtil.getUUID();
				sUuid=StringUtil.getUUID();
				tax.setUuid(uuid);
				so.setUuid(sUuid);
				dbUtil.insert(tax);
				dbUtil.insert(so);
				String sql="update k_user set taxCreate=? where id=?";
				dbUtil.executeUpdate(sql,new Object[]{"是",userId});
				for(int i=0;i<prjIds.length;i++){
					projectReport=new ProjectReport();
					projectReport.setMainformId(uuid);
					projectReport.setPrjId(prjIds[i]);
					projectReport.setPrjName(prjNames[i]);
					projectReport.setReportId(reportIds[i]);
					projectReport.setReportName(reportNames[i]);
					projectReport.setUuid(StringUtil.getUUID());
					dbUtil.insert(projectReport);
				}
				
			}else{
				tax.setUuid(uuid);
				so.setUuid(sUuid);
				dbUtil.update(tax);
				dbUtil.update(so);
				for(int i=0;i<prjIds.length;i++){
					projectReport=new ProjectReport();
					projectReport.setMainformId(uuid);
					projectReport.setPrjId(prjIds[i]);
					projectReport.setPrjName(prjNames[i]);
					projectReport.setReportId(reportIds[i]);
					projectReport.setReportName(reportNames[i]);
					projectReport.setUuid(uuids[i]);
					dbUtil.update(projectReport);
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		response.sendRedirect("nianJian.do?method=editTax&chooseValue="+userId+"&opt="+opt);
		return null;
		
	}
	
	
	
	/**
	 * 人员列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listRecord(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(RECORD_VIEW);
		
		UserSession userSession=(UserSession) request.getSession().getAttribute("userSession");
		
		String userId=userSession.getUserId();
		
		String departmentId=userSession.getUserAuditDepartId();
		
		DataGridProperty pp =new DataGridProperty();
		
		pp.setTableID("recordNianJianList");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("注册会计师及税务师备案");
	    
	    String sql="";
	    
	    
	    if(departmentId.equals("1247")){
	    sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.taxRegister," +
	    	 " u.ryzhushi,u.reportType,u.loginid,group_concat(r.rolename) as popedom "+
			" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid " +
			" left join k_userrole ur on ur.userid=u.id " +
			" left join k_role r on r.id=ur.rid  where 1=1 ${departmentId} ${name} ${sex} ${taxRegister} ${reportType} ${loginId} ${ryzhushi} " +
			" group by u.id";
	    }else{
	    	 sql="SELECT u.id,u.name,d.departname,u.departmentid,CASE WHEN u.sex='F' THEN '女' ELSE '男' END AS usex,u.sex,u.taxRegister," +
	    	 		"u.ryzhushi,u.reportType,u.loginid,group_concat(r.rolename) as popedom " +
				" FROM k_user u LEFT JOIN k_department d ON d.autoid=u.departmentid " +
				" left join k_userrole ur on ur.userid=u.id " +
				" left join k_role r on r.id=ur.rid  where 1=1 and u.id='"+userId+"' ${departmentId} ${name} ${sex} ${taxRegister} " +
						" ${reportType} ${loginId} ${ryzhushi} group by u.id";
	    }
	  
	   
	   
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("departmentid") ;
	    pp.setDirection_CH("desc");
	    pp.setInputType("checkbox");
	    pp.setWhichFieldIsValue(1);
	    
	  
	    
	    pp.addColumn("姓名","name");
	    pp.addColumn("登录名","loginId");
	    pp.addColumn("所属部门","departname");
	    pp.addColumn("性别","usex");
	    pp.addColumn("系统权限","popedom");
	    pp.addColumn("主要项目清单生成模式","reportType");
	    pp.addColumn("注册会计师","ryzhushi");
	    pp.addColumn("注册税务师","taxRegister");
	    
	    pp.setColumnWidth("10,10,15,6,15,12,10,10");
	    
	    pp.addSqlWhere("name", "and u.name like '%${name}%'");
	    pp.addSqlWhere("departmentId", "and departmentId='${departmentId}'");
	    pp.addSqlWhere("loginId", "and u.loginId like '%${loginId}%'");
	    pp.addSqlWhere("sex", "and u.sex like '%${sex}%'");
	    pp.addSqlWhere("taxRegister", "and u.taxRegister like '%${taxRegister}%'");
	    pp.addSqlWhere("reportType", "and u.reportType like '%${reportType}%'");
	    pp.addSqlWhere("ryzhushi", "and ryzhushi like '%${ryzhushi}%'"); 
	    
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
	    
	    modelAndView.addObject("departmentId",departmentId);
		return modelAndView;
		
	}
	
	/**
	 * 发站内短信
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public void sendMessage(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		
		String userIds=StringUtil.showNull(request.getParameter("userIds"));
		
		String conter=StringUtil.showNull(request.getParameter("conter"));
		
		userIds=StringUtil.trim(userIds,",");
		
		String[] ids=userIds.split(",");
		
		Connection conn=null;
		
		String result="";
		
		UserSession userSession=(UserSession) request.getSession().getAttribute("userSession");
		
		
		try{
			conn=new DBConnect().getConnect();
			PlacardService ps=new PlacardService(conn);
			ASFuntion CHF=new ASFuntion();
			for (int i = 0; i <ids.length; i++) {
			PlacardTable pt=new PlacardTable();
			pt.setCaption("注会注册及注税备案提醒");
			pt.setIsNotReversion(0);
			pt.setProperty("");
			pt.setIsReversion(0);
			pt.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			pt.setIsRead(0);
			pt.setCtype("注会及注税备案");
			pt.setImage("");
			pt.setModel("");
			pt.setAddresser(userSession.getUserId());
			pt.setMatter("注会及注税备案的短信内容《"+conter+"》");
			pt.setAddressee(ids[i]);
			ps.AddPlacard(pt);
			}
			result="发送成功!";
			response.getWriter().write(result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}		
	}
	
	
	private static final String OPENFILE = "Excel/tempdata/PrintandSave.jsp";
	/**
	 * 打印
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView proveModel(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView(OPENFILE);
		
		DbUtil dbUtil=null;
		
		modelAndView.addObject("templateid","proveModel");
		
		String uuid=StringUtil.showNull(request.getParameter("uuid"));
		
		Connection conn=null;
		
		
		String sql="";
		
		try{
			conn=new DBConnect().getConnect();
			
			dbUtil=new DbUtil(conn);
			
			PrintSetup printSetup = new PrintSetup(conn);
			
			sql="SELECT name,identityCard,CURRENT_DATE as currentDate FROM k_user WHERE id=?";
			
			Map varMap=dbUtil.query(sql,Arrays.asList(uuid),
					Arrays.asList("name","identityCard","currentDate")
			).get(0);
	
			varMap.put("name", varMap.get("name"));
			varMap.put("identityCard",varMap.get("identityCard"));
			varMap.put("currentDate", varMap.get("currentDate"));
			
			printSetup.setVarMap(varMap);
			
			//设置使用的模板
			printSetup.setStrExcelTemplateFileName("证明.xls");
			String filename = printSetup.getExcelFile();
			modelAndView.addObject("filename",filename) ;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
		
	}
}

