package com.matech.audit.work.seal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;  
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.seal.SealService;
import com.matech.audit.service.seal.model.Seal;
import com.matech.audit.service.seal.model.SealFlow;
import com.matech.audit.service.task.TaskTemplateService;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.waresStock.WaresStockService;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockDetails;
import com.matech.audit.service.waresStock.model.WaresStramFlow;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SealAction extends MultiActionController{
	
	private static final String LIST="process/SealProcess/List.jsp";
	private static final String ADDSKIP="process/SealProcess/AddandEdit.jsp";
	private static final String UPDATESKIP="process/SealProcess/AddandEdit.jsp";
	private static final String AUDITLIST="process/SealProcess/auditList.jsp";
	private static final String AUDIT="process/SealProcess/audit.jsp";
	private static final String LOOKAUDIT="process/SealProcess/lookAudit.jsp";
	private static final String statisticsList="process/SealProcess/statisticsList.jsp";
	
	private JbpmTemplate jbpmTemplate;
	
	private static final String INDEXPAGE = "seal"; // 附件名
	
	
	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}

	/**
	 	* list
	 * @param request
	 * @param response
	 * @return
	 */
		public ModelAndView list(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(LIST);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			DataGridProperty pp = new DataGridProperty(); 
			try {	
				
				
				String ppSql="SELECT concat(c.uuid,',',ifnull(a.taskId,'-1')) as uuid,a.*,c.`ApplyDate`,c.`matter`,c.`ctype`,c.`status`,c.`remark`,d.`Name` FROM \n"
								+"k_seal c \n"
								+"LEFT JOIN k_user d ON c.`userId` = d.id  \n"
								+"LEFT JOIN `j_sealprocss` b ON c.uuid = b.`uuid`  \n"
								+"LEFT JOIN  \n"
								+"( \n"
								+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
								+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
								+"	FROM jbpm4_task a    \n"
								+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
								+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
								+"	GROUP BY a.EXECUTION_ID_   \n"
								+") a on  a.ID_  = b.`ProcessInstanceId`  \n"
								+"WHERE 1=1 AND STATUS !='已作废' and c.userId = '"+userSession.getUserId()+"'  ${title} ${content} ${publishDate}"; 
				
				pp.setTableID("sealList");
				pp.setCustomerId("");
				pp.setWhichFieldIsValue(1);
				pp.setInputType("radio");
				pp.setOrderBy_CH("applyDate");
				pp.setDirection("desc");
				
				pp.setColumnWidth("10,10,15,8,20");
				pp.setSQL(ppSql);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle("印章请用列表");
				
				pp.setTrActionProperty(true); // 设置 table可双击
				pp.setTrAction("style=\"cursor:hand;\" taskId=\"${taskId}\"  ");
				
				//pp.addColumn("发布人", "userId");
				//pp.addColumn("发布部门", "departmentId");
				pp.addColumn("申请事项", "matter");
				pp.addColumn("公章类型", "ctype");
				pp.addColumn("申请时间", "applyDate");
				pp.addColumn("状态", "status");
				//pp.addColumn("备注", "remark");
				
				
				pp.addSqlWhere("title"," and c.title like '%${title}%' ");
				pp.addSqlWhere("content"," and c.content like '%${content}%' ");
				pp.addSqlWhere("publishDate"," and c.publishDate like '%${publishDate}%' ");
				
	 			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{			
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			}
			
			return modelAndView;
		}
 
		/**
		 * 新增跳转
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView addSkip(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(ADDSKIP);
			
			return modelAndView;
		}
		
		/**
		 * 新增
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView add(HttpServletRequest request, HttpServletResponse response){
			
			ASFuntion asf = new ASFuntion();
			
			String matter = asf.showNull(request.getParameter("matter"));
			String ctype = asf.showNull(request.getParameter("ctype"));
			String fileName = asf.showNull(request.getParameter("fileName"));
			String remark = asf.showNull(request.getParameter("remark"));
			String isBeginFile = asf.showNull(request.getParameter("isBeginFile")); //保存并发起流程
			String attachname = asf.showNull(request.getParameter("attachname"));  //需要盖章的附件
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			String	applyDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date());
			String uuid = UUID.randomUUID().toString();
			Seal seal = new Seal();
			seal.setUuid(uuid);
			seal.setUserId(userSession.getUserId());
			seal.setApplyDate(applyDate);
			seal.setMatter(matter);
			seal.setCtype(ctype);
			seal.setStatus("未发起");
			seal.setFileName(fileName);
			seal.setRemark(remark);
			seal.setAttachname(attachname);
			Connection conn = null;
		
			try{
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				sealService.add(seal);
				
				if("发起".equals(isBeginFile)){
					
					this.startFlow(request, response,uuid); //启动流程
				
				} 
					response.sendRedirect(request.getContextPath()+"/seal.do?method=list");
			}catch (Exception e) {
				System.out.println("印章新增错误啦："+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
			return null;
		}
		
		/**
		 * 修改跳转
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView updateSkip(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
			
			String uuid = request.getParameter("uuid");
			Connection conn = null;
			try {
				
				conn=new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				Seal seal = sealService.getSeal(uuid);
			
				modelAndView.addObject("seal",seal);
				
			} catch (Exception e) {
				
				System.out.println("得到印章信息出错："+e.getMessage());
			
			}finally{
				DbUtil.close(conn);
			}
			
			return modelAndView;
		}

		/**
		 * 修改
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView update(HttpServletRequest request, HttpServletResponse response){
			
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
			String matter = asf.showNull(request.getParameter("matter"));
			String ctype = asf.showNull(request.getParameter("ctype"));
			String fileName = asf.showNull(request.getParameter("fileName"));
			String remark = asf.showNull(request.getParameter("remark"));
			 
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			
			Seal seal = new Seal();
			seal.setUuid(uuid);
			seal.setUserId(userSession.getUserId());
			seal.setMatter(matter);
			seal.setCtype(ctype);
			seal.setFileName(fileName);
			seal.setRemark(remark);
			
			Connection conn = null;
		
			try{
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				sealService.update(seal);
				
				response.sendRedirect(request.getContextPath()+"/seal.do?method=list");
			}catch (Exception e) {
				System.out.println("印章修改错误啦："+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
			return null;
		}
		
		/**
		 * 删除
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception{
			Connection conn = null;
			
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			try {
				conn = new DBConnect().getConnect();
				
				
				if(!"".equals(uuid)){
					SealService sealService = new SealService(conn);
					AttachService attachService = new AttachService(conn);
					Seal seal = sealService.getSeal(uuid);
					

					attachService.remove(INDEXPAGE, seal.getFileName()); // 删除文件
					
					boolean result = sealService.delete(uuid);
					System.out.println("删除印章否成功："+result);
					 
				} 
				
				response.sendRedirect(request.getContextPath()+"/seal.do?method=list");
				
			} catch (IOException e) {
				
				System.out.println("删除印章错误："+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
			
			return null;
		}
		
		/**
		 * 查询是否上传附件
		 * @param table
		 * @param uuid
		 * @return
		 */
		public ModelAndView getAccessory(HttpServletRequest request, HttpServletResponse response) throws Exception{
			Connection conn = null;
			
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			PrintWriter out =  response.getWriter();
			
			ASFuntion asf = new ASFuntion();
			
			String table = asf.showNull(request.getParameter("table"));
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			try {
				conn = new DBConnect().getConnect();
				
				String  result="";
				
				if(!"".equals(uuid) && !"".equals(table)){
					
					SealService sealService = new SealService(conn);
					
					result = sealService.getAccessory(table,uuid);
					 
				} 
				
				out.write(result);
				
			} catch (IOException e) {
				
				System.out.println("查询附件是否上传错误："+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
			
			return null;
		}
		
		
		/**
		 * 发起 公章申请流程
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView updateStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
			Connection conn = null;
			
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			PrintWriter out =  response.getWriter();
			
			ASFuntion asf = new ASFuntion();
			
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			try {
				conn = new DBConnect().getConnect();
				
				String  result="";
				
				if(!"".equals(uuid)){
					
					SealService sealService = new SealService(conn);
					
					Seal seal = sealService.getSeal(uuid);
					if(!"未申请".equals(seal.getStatus())){
						result="公章申请状态不是未申请状态";
					}else{
						
						int row = sealService.updateStatus(uuid,"审批中");
						
						if(row>0){
							result="发起成功，您的公章申请进入审批中！";
						}else{
							result="发起失败，请查询后台原因！";
						}
					}
					
					 
				} 
				
				out.write(result);
				
			} catch (IOException e) {
				
				System.out.println("发起公章申请错误："+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
			
			return null;
		}
		
		/**
		 * 启动流程
		 * @param request
		 * @param response
		 * @return
		 * @throws IOException 
		 */
		public ModelAndView startFlow(HttpServletRequest request, HttpServletResponse response,String uuid) throws IOException{
			// 获取登录的用户
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userId = userSession.getUserId();
			Connection conn = null;
			
			if("".equals(uuid)){
				
				uuid = request.getParameter("uuid");
			}
			
			ASFuntion asf = new ASFuntion();
			try {
					conn = new DBConnect().getConnect();

					SealService sealService = new SealService(conn);
					Seal seal = sealService.getSeal(uuid);
					Map<String, String> startMap = new HashMap<String, String>();
					startMap.put("applyUserId", userId);
			
					String sql = "select processDefinitionId from j_processdeploy where processKey='sealApplyFlow' " ;
					String pdIds = sealService.getValueBySql(sql);
					String[] paId = pdIds.split("@`@");
					
					String processDefinitionId = paId[0].toString() ;
					System.out.println("流程定义ID="+processDefinitionId);
					System.out.println("发起人="+startMap.get("applyUserId"));
					if("".equals(processDefinitionId)){
						processDefinitionId =  paId[0];
					}
					
					String judgeAudit = "";
					/*
					String appoint = "分所盖总所印章"; //测试的
					if("分所盖总所印章".equals(appoint)){
						judgeAudit = "分所盖总所印章";
					}else if("业务部门使用公章".equals(appoint)){
						judgeAudit = "业务部门使用公章";
					} else if("公共部门使用公章".equals(appoint)){
						judgeAudit = "公共部门使用公章";
					}	
					*/
					if(!"".equals(asf.showNull(seal.getApplyDepartment()))){
						judgeAudit = seal.getApplyDepartment();
					}else{
						judgeAudit ="分所盖总所印章";
					}
					System.out.println("分支走向="+judgeAudit);
					startMap.put("judgeAudit", judgeAudit);
					
					if(seal.getCtype().indexOf("电子")>-1){
						startMap.put("sealType", "电子章");
					}else{
						startMap.put("sealType", "其它");
					}
					
					String areaid = new DbUtil(conn).queryForString("SELECT b.areaid FROM k_user a LEFT JOIN k_department b ON a.departmentId = b.autoid WHERE a.id= '"+userSession.getUserId()+"'");
					
					String filialeInchargeUserid = "";
					if(!"".equals(areaid)){
						
						sql = "SELECT DISTINCT c.id FROM k_role a \n"
							+"LEFT JOIN k_userrole b ON a.`id` = b.`rid` \n"
							+"LEFT JOIN k_user c ON b.`userid` = c.`id` \n"
							+"LEFT JOIN k_department d ON c.`departmentid` = d.`autoid` \n"
							+"LEFT JOIN k_area e ON d.`areaid` = e.`autoid` \n"
							+"WHERE a.rolename = '分所所长' AND e.`autoid` = '"+areaid+"'";
						 filialeInchargeUserid = sealService.getValueBySql(sql);
						 if(!"".equals(filialeInchargeUserid)){
							 filialeInchargeUserid = filialeInchargeUserid.replaceAll("@`@", ",");
							 filialeInchargeUserid = filialeInchargeUserid+"19";
						 }else{
							 filialeInchargeUserid = "19";
						 }
					}else{
						 filialeInchargeUserid = "19";		
					}
					
					startMap.put("filialeInchargeUserid", filialeInchargeUserid);
					
					// 启动流程
					ProcessInstance pi =jbpmTemplate.startProcessById(processDefinitionId, startMap);
			
					// 获取节点任务
					TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
					List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
					Task myTask = taskList2.get(0);
			  
					SealFlow sealFlow = new SealFlow();
					sealFlow.setProcessInstanceId(pi.getId());
					sealFlow.setUuid(uuid);
					sealFlow.setApplyuser(userSession.getUserId());
					sealFlow.setApplyDate(asf.getCurrentDate()+" "+asf.getCurrentTime());
					sealFlow.setState(myTask.getName());
					sealFlow.setProperty("");
			
					// 根据节点 ID 把下个节点的执行人 传到下一个节点
					jbpmTemplate.setTaskVariables(myTask.getId(), startMap);
					
					ProcessFormService processFormService = new ProcessFormService(conn);
					
					String activeName = jbpmTemplate.getActivityName(myTask.getId()); // 获取当前节点
					ProcessForm processForm = new ProcessForm();
					processForm.setProcessInstanseId(paId[0]);
					processForm.setKey("发起状态");
					processForm.setValue("发起成功");
					processForm.setNodeName(activeName);
					processForm.setDealUserId(userId);
					processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
					processForm.setDealUserId(userId);
					processForm.setProperty("");
					processFormService.add(processForm);// 添加记录(流程表)
					
					// 完成节点
					jbpmTemplate.completeTask(myTask.getId());
			
					sealService.addSealProcss(sealFlow);// 添加流程 (自己的表)
					
					sql = "update k_seal set status='已发起' where uuid='"+uuid+"' " ;
					sealService.UpdateValueBySql(sql);
					
					
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return null;

		}

		/**
		 * 审批list
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView auditList(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(AUDITLIST);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			DataGridProperty pp = new DataGridProperty(); 
			DataGridProperty yetAuditPP = new DataGridProperty(); 
			try {	
				
				
				String ppSql="SELECT a.*,c.`ApplyDate`,c.`matter`,c.`ctype`,c.`status`,c.`remark`,e.attachname,c.sealCount,d.`Name` FROM ( \n"
							+"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
							+"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
							+"FROM jbpm4_task a   \n"
							+"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
							+"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
							+" where 1=1 " 
							+" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
							+"GROUP BY a.EXECUTION_ID_  \n"
							+") a  \n"
							+"INNER JOIN `j_sealprocss` b ON a.ID_  = b.`ProcessInstanceId` \n"
							+"LEFT JOIN k_seal c ON b.`uuid` = c.`uuid` \n"
							+"LEFT JOIN k_user d ON c.`userId` = d.id "
							+" left join k_attachext e on c.filename = e.indexid \n"
							+"where 1=1 and c.status <> '终止'  and c.attachname = e.attachid ${applyDate} ${remark} ${userId} ${matter} ${ctype}"; 
				
				System.out.println(ppSql);
				pp.setTableID("sealAuditList");
				pp.setCustomerId("");
				pp.setWhichFieldIsValue(1);
				pp.setInputType("radio");
				pp.setOrderBy_CH("applyDate");
				pp.setDirection("desc");
				
				
				pp.setColumnWidth("10,10,10,8,5,15,20,15");
				pp.setSQL(ppSql);
				
				pp.addColumn("申请事项", "matter");
				pp.addColumn("公章类型", "ctype");
				pp.addColumn("申请时间", "applyDate");
				pp.addColumn("申请人", "name");
				pp.addColumn("申请份数", "sealCount");
				pp.addColumn("附加名", "attachname");
				//pp.addColumn("备注", "remark");
				pp.addColumn("审批状态", "auditStatus");
				
				
				pp.setPrintEnable(true);
				pp.setPrintTitle("印章审批列表");
				
				pp.addSqlWhere("applyDate"," and c.applyDate like '%${applyDate}%' ");
				pp.addSqlWhere("remark"," and c.remark like '%${remark}%' ");
				pp.addSqlWhere("userId"," and d.name like '%${userId}%' ");
				pp.addSqlWhere("matter"," and c.matter like '%${matter}%' ");
				pp.addSqlWhere("ctype"," and c.ctype like '%${ctype}%' ");
				
				
				
				String yetPP="SELECT  DISTINCT c.uuid,b.`ApplyDate`,c.`matter`,c.`ctype`,b.`state`,b.ProcessInstanceId,c.`remark`,d.`Name` FROM k_seal c \n"
							+"INNER JOIN `j_sealprocss` b ON c.uuid  = b.uuid \n"
							+"INNER JOIN `j_processform` e ON b.ProcessInstanceId  = e.ProcessInstanseId \n"
							+"LEFT JOIN k_user d ON c.`userId` = d.id "
							+"where 1=1 and e.dealUserId ='"+userSession.getUserId()+"' and c.status !='已审批' ${applyDate2} ${remark2} ${userId2} ${matter2} ${ctype2}"; 
				yetAuditPP.setTableID("sealYetAuditList");
				yetAuditPP.setCustomerId("");
				yetAuditPP.setWhichFieldIsValue(1);
				yetAuditPP.setInputType("radio");
				yetAuditPP.setOrderBy_CH("applyDate");
				yetAuditPP.setDirection("desc");
				
				yetAuditPP.setColumnWidth("10,10,15,8,20,15");
				yetAuditPP.setSQL(yetPP);
				
				yetAuditPP.addColumn("申请事项", "matter");
				yetAuditPP.addColumn("公章类型", "ctype");
				yetAuditPP.addColumn("处理时间", "applyDate");
				yetAuditPP.addColumn("申请人", "name");
				//yetAuditPP.addColumn("备注", "remark");
				yetAuditPP.addColumn("审批状态", "state");
				yetAuditPP.addColumn("操作", "processinstanceid",null,null,"<a href=# onclick=goView('${processinstanceid}','印章流程');>【查看流程图】</a>");
				
				yetAuditPP.setPrintEnable(true);
				yetAuditPP.setPrintTitle("印章已审批列表");
				
				yetAuditPP.addSqlWhere("applyDate2"," and c.applyDate like '%${applyDate2}%' ");
				yetAuditPP.addSqlWhere("remark2"," and c.remark like '%${remark2}%' ");
				yetAuditPP.addSqlWhere("userId2"," and d.name like '%${userId2}%' ");
				yetAuditPP.addSqlWhere("matter2"," and c.matter like '%${matter2}%' ");
				yetAuditPP.addSqlWhere("ctype2"," and c.ctype like '%${ctype2}%' ");
				
	 			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{			
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
				request.getSession().setAttribute(DataGrid.sessionPre + yetAuditPP.getTableID(), yetAuditPP);
			}
			
			return modelAndView;
		}
         //查看附件
		public ModelAndView filePage(HttpServletRequest request,HttpServletResponse response){
			//ModelAndView modelAndView = new ModelAndView("process/SealProcess/file.jsp"); //版本一
			ModelAndView modelAndView = new ModelAndView("goldgrid/edit.jsp"); //版本二
			Connection conn = null;
			try {
				ASFuntion asf = new ASFuntion();
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
				String attachId = asf.showNull(request.getParameter("file")); //k_attachFile中的id
				String opt = asf.showNull(request.getParameter("opt")); // 从印章请用 的双击跳转的
				modelAndView.addObject("opt",opt);
				String fileName =""; //文件名
				String filePostfix ="" ; //后缀名 如：.doc
				String filePath = "";  //文件路径
				String loginName = "";  //登录名  如:喻明明

				conn = new DBConnect().getConnect();
				SealService ss = new SealService(conn);
				// -----------------版本一  用屈总的控件打开
				/*
			    attachFileName  = new DbUtil(conn).queryForString("SELECT attachname FROM `k_attachext` WHERE attachid = '"+param+"'");
			    //attachId = new DbUtil(conn).queryForString("SELECT attachid FROM `k_attachext` WHERE attachid = '"+param+"'");
			    String uuid = new DbUtil(conn).queryForString("SELECT `uuid` FROM `k_seal` WHERE attachname = '"+param+"'");
				request.setAttribute("attachFileName", attachFileName); //文件名
				request.setAttribute("attachId", param);  //文件ID
				request.setAttribute("uuid", uuid);*/
				
				//版本二   用金格的控件打开
				Map fileMap = new HashMap();
				
				String uuid = asf.showNull(request.getParameter("uuid")); //k_seal中的uuid
				if("".equals(attachId)){
					attachId = new DbUtil(conn).queryForString("SELECT attachname FROM `k_seal` WHERE uuid = '"+uuid+"'");
				}
				Seal seal  = ss.getSeal(uuid);
				fileName =  new DbUtil(conn).queryForString("SELECT attachname FROM `k_attachext` WHERE attachid = '"+attachId+"'");
				if(!"".equals(fileName)){
					
					filePostfix = fileName.substring(fileName.indexOf("."), fileName.length());

					fileName = fileName.substring(0, fileName.indexOf("."));
				}
				
				//文件路径
				//filePath = BackupUtil.getDATABASE_PATH()+"../../webRoot/attachFile/seal/";
				filePath = BackupUtil.getDATABASE_PATH();
				System.out.println(request.getContextPath());
				filePath = filePath.replaceAll("////", "\\\\");
				String recordId  = "";
				if(filePostfix.indexOf(".pdf")>-1){
					recordId = "2";
				}else{
					recordId = "1";
				}
				String printCount = new DbUtil(conn).queryForString("SELECT printCount FROM `k_seal` WHERE uuid = '"+uuid+"'");
				fileMap.put("recordId", recordId); //文档编号。用于区分用office打开还是pdf
				fileMap.put("attachId", attachId);
				fileMap.put("fileName",fileName);
				fileMap.put("filePostfix",filePostfix);
				fileMap.put("filePath",filePath.replaceAll("//", "\\"));
				fileMap.put("uuid",uuid);
				fileMap.put("loginName",userSession.getUserName());
				fileMap.put("sealCount",seal.getSealCount());
				fileMap.put("printCount",printCount);
				
				modelAndView.addObject("fileMap", fileMap);
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return modelAndView;
		}
		/**
		 * 审批跳转
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView auditSkip(HttpServletRequest request,HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(AUDIT);
			
			ASFuntion asf = new ASFuntion();
			
			String taskId = asf.showNull(request.getParameter("taskId"));
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			Connection conn = null;
			
			try {
				
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				ProcessFormService pfs = new ProcessFormService(conn);
				UserService userService = new UserService(conn);
					
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				List nodeList = pfs.getNodeList(pdid);
				if("".equals(uuid)){
					String sql = "select uuid from j_sealprocss where ProcessInstanceId='"+pdid+"'";
					uuid = new DbUtil(conn).queryForString(sql);
				}
				
				Seal seal = sealService.getSeal(uuid);
				User user =  userService.getUser(seal.getUserId(), "id");
				seal.setUserId(user.getName()) ;
				String accAry  = new DbUtil(conn).queryForString("SELECT attachid FROM `k_attachext` WHERE indexid = '"+seal.getFileName()+"'");
				String attachFileName  = new DbUtil(conn).queryForString("SELECT attachname FROM `k_attachext` WHERE indexid = '"+seal.getFileName()+"'");
				if(!"".equals(accAry)){
					
					modelAndView.addObject("attachid",accAry);
					modelAndView.addObject("attachFileName",attachFileName);
				}
				String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
				modelAndView.addObject("nodeList", nodeList);
				modelAndView.addObject("seal",seal);
				modelAndView.addObject("pdId",pdid);
				modelAndView.addObject("taskId",taskId);
				modelAndView.addObject("uuid",uuid);
				modelAndView.addObject("activeName",activeName);
				//modelAndView.addObject("file",UUID.randomUUID().toString());
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return modelAndView ;
		}
		/**
		 * 结束印章流程
		 * @param request
		 * @param response
		 * @return
		 * @throws IOException
		 */
		public ModelAndView end(HttpServletRequest request,HttpServletResponse response) throws IOException{
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
		    System.out.println(uuid);
			Connection conn = null;
			try{
				conn = new DBConnect().getConnect();
				SealService sealService = new SealService(conn);
				String sql = "update k_seal set status='终止' where uuid='"+uuid+"'";
				sealService.UpdateValueBySql(sql);
				
			}catch (Exception e) {
				e.printStackTrace();
			}	finally{
				DbUtil.close(conn);
				response.sendRedirect(request.getContextPath()+"/seal.do?method=auditList");
			}
			return null;
		}
		/**
		 * 审批
		 * @param request
		 * @param response
		 * @return
		 * @throws IOException
		 */
		public ModelAndView audit(HttpServletRequest request,HttpServletResponse response) throws IOException{
			
			ASFuntion asf = new ASFuntion();
			
			String taskId = asf.showNull(request.getParameter("taskId"));
			String remark = asf.showNull(request.getParameter("remark"));
			String uuid = asf.showNull(request.getParameter("uuid"));
			String instationMsg =  asf.showNull(request.getParameter("instationMsg")) ; //站内短信
			String auditUserId =  asf.showNull(request.getParameter("auditUserId")) ; //选择审核人
			String mobilePhoneMsg =  asf.showNull(request.getParameter("mobilePhoneMsg")) ; //手机短信
			String msgUserId =  asf.showNull(request.getParameter("msgUserId")) ; //人员
			String sealCount =  asf.showNull(request.getParameter("sealCount")) ; //打印份数
			Connection conn = null;
			
			try {
				
				conn = new DBConnect().getConnect();
				
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				SealService sealService = new SealService(conn);
				ProcessFormService processFormService = new ProcessFormService(conn);
				//InteriorEmailAction emailAction = new InteriorEmailAction();
				PlacardService placardService = new PlacardService(conn);
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				String activeName = jbpmTemplate.getActiveName(pdid);
				
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdid);
				processForm.setKey("审批意见");
				processForm.setValue(remark);
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userSession.getUserId());
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
			
				String sql = "update j_sealprocss set state='"+activeName+"' where ProcessInstanceId='"+pdid+"'";
				sealService.UpdateValueBySql(sql); //
				
				if(!"".equals(uuid)){
					new DbUtil(conn).execute("update k_seal set sealCount='"+sealCount+"' where uuid='"+uuid+"'");
				}
				
				if("行政使用".equals(activeName) || "电子章管理员".equals(activeName)){  // 最后一个节点的时候  修改状态

					if(!"".equals(uuid)){
						sql = " update k_seal set status='已审批'  where uuid='"+uuid+"'";
						sealService.UpdateValueBySql(sql); //修改状态
					}
				}

				if(!"".equals(auditUserId)){
					msgUserId = msgUserId+","+auditUserId;
					Map<String, String> startMap = new HashMap<String, String>();
					startMap.put("auditUserId",auditUserId);
					// 根据节点 ID 把下个节点的执行人 传到下一个节点
					jbpmTemplate.setTaskVariables(taskId, startMap);
				}
				
				
				String content = "您有一个电子印章的审批等待处理，请及时登录ERP系统进行审批!";
				System.out.println("发文发送内容 ："+content);
				if("是".equals(instationMsg)){
					PlacardTable placardTable=new PlacardTable(); 
					placardTable.setAddresser(userSession.getUserId());//发起
					placardTable.setAddresserTime(asf.getCurrentDate()+" "+asf.getCurrentTime());
					placardTable.setCaption("电子签章审批");
					placardTable.setMatter("'"+content+"'");
					placardTable.setIsRead(0);
					placardTable.setIsReversion(0);
					placardTable.setIsNotReversion(0);
					if(!"".equals(msgUserId)){
						msgUserId = msgUserId.substring(0, msgUserId.length()-1);
						String[] userIds = msgUserId.split(",");
						
						for (int i = 0; i < userIds.length; i++) {
							placardTable.setAddressee(userIds[i]); //接收的老大UserId
							
							if("是".equals(mobilePhoneMsg)){
								if(!"".equals(msgUserId)){
									msgUserId = msgUserId.substring(0, msgUserId.length()-1);
								}
								placardTable.setMpShortMessage("是");
								placardTable.setMpContent(content);
							}
							placardService.AddPlacard(placardTable); //记录人发消息
						}
					}
					
				}
				
				
				//手机短信
				/*if("是".equals(mobilePhoneMsg)){
					if(!"".equals(msgUserId)){
						msgUserId = msgUserId.substring(0, msgUserId.length()-1);
					}
					emailAction.mobilePhoneInfo(request,response,"", msgUserId, content);
				}*/
				jbpmTemplate.completeTask(taskId);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
				response.sendRedirect(request.getContextPath()+"/seal.do?method=auditList");
			}
			return null ;
		}

		/**
		 * 得到状态ajax
		 * @param request
		 * @param response
		 * @return
		 * @throws IOException
		 */
		public ModelAndView getStatus(HttpServletRequest request,HttpServletResponse response) throws IOException{
			
			String uuid = request.getParameter("uuid");
			
			Connection conn = null;
			
			try {
				
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				String sql = "select status from k_seal where uuid='"+uuid+"' ";
				String statusArray = sealService.getValueBySql(sql); //
				String[] status =statusArray.split("@`@");
				
				out.write(status[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return null ;
		}

		/**
		 * 查看审批流程信息
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView lookAudit(HttpServletRequest request,HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(LOOKAUDIT);
			String uuid = request.getParameter("uuid");
			
			Connection conn = null;
			
			try {
				
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				ProcessFormService pfs = new ProcessFormService(conn);
				UserService userService = new UserService(conn);
					
				String sql = "select ProcessInstanceId from j_sealprocss where uuid='"+uuid+"'";
				String ProcessInstanceIds = sealService.getValueBySql(sql); //报废ID
				
				String[] pdid = ProcessInstanceIds.split("@`@");  //报废ID
				List nodeList = pfs.getNodeList(pdid[0]);
				
				
				Seal seal = sealService.getSeal(uuid);
				User user =  userService.getUser(seal.getUserId(), "id");
				seal.setUserId(user.getName()) ;
				
				String attachid  = new DbUtil(conn).queryForString("SELECT attachid FROM `k_attachext` WHERE indexid = '"+seal.getFileName()+"'");
				modelAndView.addObject("attachid",attachid);
				modelAndView.addObject("nodeList", nodeList);
				modelAndView.addObject("seal",seal);
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return modelAndView ;
		}
		
		/**
		 * 根据UUID 查询流程ID
		 * @param request
		 * @param response
		 * @return
		 * @throws IOException
		 */
		public ModelAndView lookImages(HttpServletRequest request, HttpServletResponse response) throws IOException{
			// 获取登录的用户
			Connection conn = null;
			
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			try {
					conn = new DBConnect().getConnect();

				if(!"".equals(uuid)){
					
					SealService sealService = new SealService(conn);
					String sql = " SELECT a.* FROM ( \n"
						+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_  \n"
						+"	FROM jbpm4_task a    \n"
						+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
						+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'   \n"
						+" ) a \n"
						+"	INNER JOIN j_sealprocss b ON a.id_ = b.`ProcessInstanceId` \n"
						+"where uuid='"+uuid+"' " ;
					
					String pdIds = sealService.getValueBySql(sql);
					String[] pdId = pdIds.split("@`@");
					
					out.write(pdId[0]);
				}else{
					out.write("");
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			return null;

		}
		
		/**
		 * 公告统计
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView statisticsList(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelAndView = new ModelAndView(statisticsList);
			//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			DataGridProperty pp = new DataGridProperty(); 
			try {	
				
				
				String ppSql=" SELECT a.`uuid`,b.`Name` AS userId,a.`applyDate`,a.`matter`,a.`ctype`,a.`status`,a.`remark`,c.`departname` \n"
							  +"  FROM k_seal a \n"
							  +"  LEFT JOIN k_user b ON a.`userId` = b.`id` \n"
							  +"  LEFT JOIN k_department c ON b.`departmentid` = c.`autoid`  \n"
						      +" WHERE 1=1   and status <>'已作废'  ${userId} ${applyDate} ${departname} ${matter} ${ctype} ${status} ";
				
				pp.setTableID("sealStatisticsList");
				pp.setCustomerId("");
				pp.setWhichFieldIsValue(1);
				//pp.setInputType("radio");
				pp.setOrderBy_CH("applyDate");
				pp.setDirection("desc");
				
				pp.setColumnWidth("10,10,10,10,10,10,20");
				pp.setSQL(ppSql);
				
				pp.addColumn("申请人", "userId");
				pp.addColumn("印章事项", "matter");
				pp.addColumn("印章类型", "ctype");
				pp.addColumn("申请时间", "applyDate");
				pp.addColumn("状态", "status");
				pp.addColumn("申请人所属部门", "departname");
				pp.addColumn("备注", "remark");
				
				pp.addSqlWhere("userId"," and b.name like '%${userId}%' ");
				pp.addSqlWhere("applyDate"," and a.applyDate like '%${applyDate}%' ");
				pp.addSqlWhere("departname"," and c.departname like '%${departname}%' ");
				pp.addSqlWhere("matter"," and a.matter like '%${matter}%' ");
				pp.addSqlWhere("ctype"," and a.ctype like '%${ctype}%' ");
				pp.addSqlWhere("status"," and a.status like '%${status}%' ");
				
	 			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{			
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			}
			
			return modelAndView;
		}
	 
		/**
		 * 从0号模板打开一张空白的底稿
		 * 
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView downloadFile(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			response.setContentType("text/html;charset=utf-8");
			ASFuntion CHF = new ASFuntion();

			byte[] bytes = null;
			String fileName = null;

			Connection conn = null;

			try {
				conn = new DBConnect().getConnect();
				//SealService sealService = new SealService(conn);
				String textFileName = CHF.showNull(request.getParameter("textFileName")) ; //k_attachext 的  attachid
				String indexTable = CHF.showNull(request.getParameter("indexTable")) ;
				
				if(!"".equals(textFileName)) {
					//修改时打开正文
					fileName = textFileName ;
					//String sql = "SELECT attachid,attachname,attachfilepath FROM `k_attachext` WHERE indexTable = '"+indexTable+"' AND attachid='"+textFileName+"'";
					//String accAry = sealService.getValueBySql(sql);
					//accAry = accAry.replaceAll("@`@", ",");
					//accAry = accAry.substring(0, accAry.length()-1);
					//String[] attach = accAry.split(",");
					
					String path = BackupUtil.getDATABASE_PATH()+"../../webRoot/attachFile/"+indexTable+"/"+textFileName;
					System.out.println("服务器文件路径："+path);
					File file = new File(path+textFileName) ;
					if(!file.exists()){
						//path = BackupUtil.getDATABASE_PATH()+"../../attachFile/"+indexTable+"/"+textFileName;
						System.out.println("本机文件路径："+path);
						file = new File(path) ;
					}
					System.out.println("文件："+path);
					this.downLoad(path, response, false);
					
					
					/*FileInputStream stream = new FileInputStream(file);
					ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
					byte[] b = new byte[1024];
					int n;
					while ((n = stream.read(b)) != -1)
						out.write(b, 0, n);
					
					stream.close();
					out.close();
					
					bytes =  out.toByteArray();*/
					
				}else {
					//从0号模板打开空白word
					
					ManuFileService manuFileService = new ManuFileService(conn);
					TaskTemplateService tts = new TaskTemplateService(conn,"0") ;
					
					com.matech.audit.service.task.model.Task task = tts.getTaskTemplateByTaskCode("01-2") ;
					// 判断是否有该底稿存在
					if (task == null) {
						throw new Exception("指定的底稿不存在！taskcode=01-2");
					}
					String taskId = CHF.showNull(task.getTaskId());
					
					bytes = manuFileService.getUnZipFileByTypeIdAndTaskId("0",taskId);
					fileName = task.getTaskName() ;

					fileName = URLEncoder.encode(fileName,"UTF-8") ;
					response.setContentType("application/x-msdownload");
					response.setHeader("Content-disposition",
							"attachment; filename=" + fileName);
					response.setHeader("Content-Length", String.valueOf(bytes.length));
					if (bytes != null && bytes.length > 0) {
						OutputStream outs = response.getOutputStream();
						outs.write(bytes);
						outs.flush();
						outs.close();
					} else {
						PrintWriter out = response.getWriter();
						out.print("错误!!文件不存在！");
					}
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(conn);
			}
			return null;
		}
		
		//文件下载
		public void downLoad(String filePath,HttpServletResponse response,boolean isOnLine)
		throws Exception{
				File f = new File(filePath);
				if(!f.exists()){
				response.sendError(404,"File not found!");
				return;
				}
				//FileInputStream br = new FileInputStream(f);
				try{
						BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
						byte[] buf = new byte[1024];
						int len = 0;
		
						response.reset(); //非常重要
						if(isOnLine){ //在线打开方式
						URL u = new URL("file:///"+filePath);
						response.setContentType(u.openConnection().getContentType());
						response.setHeader("Content-Disposition", "inline; filename="+f.getName());
						//文件名应该编码成UTF-8
						}
						else{ //纯下载方式
						response.setContentType("application/x-msdownload"); 
						response.setHeader("Content-Disposition", "attachment; filename=" + f.getName()); 
						}
						OutputStream out = response.getOutputStream();
						//ServletOutputStream  out =response.getOutputStream();
						while((len = br.read(buf)) >0)
							out.write(buf,0,len);
						br.close();
						if(out !=null){
							out.close();
						}
				}catch (Exception e) {
					e.printStackTrace();
				}
		} 


		
		/**
		 * 
		 * 保存发文正文
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		public void uploadFile(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = null ;
			ASFuntion asf = new ASFuntion();
			String fileTempName = asf.showNull(request.getParameter("fileTempName")) ;
			String indexTable = asf.showNull(request.getParameter("indexTable")) ;
			String attachid  =  asf.showNull(request.getParameter("attachId"));
			//String uuid = asf.showNull(request.getParameter("uuid")) ;
			
			//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			
			try {
				out = response.getWriter();
				
				//文件上传路径
				String path = BackupUtil.getDATABASE_PATH()+"../../webRoot/attachFile/"+indexTable+"/"+fileTempName;
				File file = new File(path);
				// 验证文件是否存在
				if(!file.exists()) {
					
					path = BackupUtil.getDATABASE_PATH()+"../../attachFile/"+indexTable+"/"+fileTempName;
					file = new File(path) ;

					if(!file.exists()) {
						
						file.mkdirs() ;
					}
				}
				MyFileUpload myfileUpload = new MyFileUpload(request);
				String uploadTempPath = myfileUpload.UploadFile(fileTempName,null);
				Map parameters = myfileUpload.getMap();
				//String filename = (String)parameters.get("filename") ;
				//String tempFileTempName = (String)parameters.get("fileTempName") ;
			
				//File newFile = new File(path) ;
			 
				if(file.exists() && file.isFile())  {
					file.delete() ;
				}
				File oldFile = new File(uploadTempPath+fileTempName) ;
				ManuFileService mfs = new ManuFileService() ;
				mfs.copyFile(oldFile, file) ;
				
				if(oldFile.exists()) {
					oldFile.delete() ;
				}
				out.write("Success\n文件修改成功!");
				out.close() ;
			} catch (Exception e) {
				e.printStackTrace();
			} finally { 
				
			}
		}
}
