/**
 * 
 */
package com.matech.audit.work.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLDocument.HTMLReader.FormAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.jpdl.DecisionHandler;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.model.Transition;
import org.jbpm.api.task.Participation;
import org.jbpm.api.task.Task;
import org.jbpm.jpdl.internal.activity.DecisionHandlerActivity;
import org.jbpm.jpdl.internal.activity.TaskActivity;
import org.jbpm.pvm.internal.el.Expression;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.jbpm.pvm.internal.wire.descriptor.ObjectDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.StringDescriptor;
import org.jbpm.pvm.internal.wire.operation.FieldOperation;
import org.jbpm.pvm.internal.wire.usercode.UserCodeReference;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.FileUpload;
import com.matech.audit.service.form.FormDefineService;
import com.matech.audit.service.form.FormQueryConfigService;
import com.matech.audit.service.form.model.FormQuery;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.process.inter.NodeInterface;
import com.matech.audit.service.process.jpdl.JpdlDrawer;
import com.matech.audit.service.process.jpdl.JpdlProperty;
import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessDeployVO;
import com.matech.audit.service.process.model.ProcessField;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.datagrid.DataGridFieldProcess;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.path.Path;
import com.matech.framework.pub.util.ClassUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.pub.util.ZipUtil;

public class ProcessAction extends MultiActionController {
	
	private static Log log = new Log(ProcessAction.class) ;
	private final static String JPDL_PATH = Path.getWarPath()
			+ "../process/";
	private final static String LIST_VIEW = "process/list.jsp";
	private final static String ADD_VIEW = "process/edit.jsp";
	private final static String EDIT_VIEW = "process/edit.jsp";
	private final static String DESIGN_VIEW = "process/designer.jsp";
	private final static String AUDITLIST = "process/auditList.jsp" ;
	private final static String VIEWIMAGE = "process/viewImage.jsp" ;
	private final static String AUDIT_VIEW = "process/audit.jsp" ;
	private final static String TASKMANAGERLIST = "process/taskManager.jsp" ;
	
   public enum ProcessUnCheckColumns{
	  
	   auditStatus("当前节点"),create_("送达时间"),applyUserName("申请人"),applytime("申请时间")
	   ;
	   
	   private String label;
	   private ProcessUnCheckColumns(String label) {
		// TODO Auto-generated constructor stub
		   this.label=label;
	   }
	public String getLabel() {
		return label;
	}
	   
	   
	   /*
	    pp.addColumn("当前节点", "auditStatus");
			pp.addColumn("送达时间", "create_");
			pp.addColumn("申请人", "applyUserName");
			pp.addColumn("申请时间", "applytime");
			
			
	    */
   }	
   
   public enum ProcessCheckedColumns{
		  
	   userName("申请人"),
	   activeName("当前节点"),dealUserName("当前处理人"),arrivetime("送达时间"),dealtime("办理时间")
	   ;
	   
	   private String label;
	   private ProcessCheckedColumns(String label) {
		// TODO Auto-generated constructor stub
		   this.label=label;
	   }
	public String getLabel() {
		return label;
	}
	   
	   
	   /*
	
			pp2.addColumn("申请人", "userName");
			pp2.addColumn("申请时间", "applytime");
			pp2.addColumn("当前节点", "activeName");
			pp2.addColumn("当前处理人", "dealUserName");
			pp2.addColumn("送达时间", "arrivetime");
			pp2.addColumn("办理时间", "dealtime");
	    */
   }	
	
	/**
	 * 新增修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addAndEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(ADD_VIEW);

		String id = StringUtil.showNull(request.getParameter("id"));

		Connection conn = null;

		try {
			
			conn = new DBConnect().getConnect();
			
			if(!"".equals(id)) {
				//修改
				ProcessService processService = new ProcessService(conn);
				ProcessDeploy processDeploy = processService
						.getProcessDeployById(id);
				modelAndView.addObject("isAdd", "no");
				modelAndView.addObject("processDeploy", processDeploy);
			}else {
				modelAndView.addObject("isAdd", "yes");
			}
			
			List fieldClassList = ClassUtil.getClassListByInterface(DataGridFieldProcess.class,"com.matech.audit.service.process.impl") ;
			modelAndView.addObject("fieldClassList",fieldClassList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}
	
	/**
	 * 修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView designer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(DESIGN_VIEW);

		String id = request.getParameter("id");

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect();
 
			ProcessService processService = new ProcessService(conn);
			ProcessDeploy processDeploy = processService.getProcessDeployById(id);
			
			List classList = ClassUtil.getClassListByInterface(NodeInterface.class,"com.matech.audit.service.process.impl") ;
			modelAndView.addObject("classList",classList);
			
			List DecisionClassList = ClassUtil.getClassListByInterface(DecisionHandler.class,"com.matech.audit.service.process.decisionImpl") ;
			modelAndView.addObject("DecisionClassList",DecisionClassList);
			
			modelAndView.addObject("processDeploy", processDeploy);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}
	
	

	/**
	 * 下载文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void downloadFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {

			String fileName = request.getParameter("fileName");

			File file = new File(JPDL_PATH + fileName);
			if (file.exists()) {
				fileName = URLEncoder.encode(fileName, "UTF-8");

				response.setContentType("text/html;charset=UTF-8");
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition",
						"attachment; filename=" + fileName);

				bis = new BufferedInputStream(new FileInputStream(file));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;

		String fileName = request.getParameter("fileName");

		try {
			out = response.getWriter();

			FileUpload fileUpload = new FileUpload(request);

			fileUpload.UploadFile(fileName, JPDL_PATH);
			System.out.println(JPDL_PATH);

			out.write("ok");
		} catch (Exception e) {
			out.write("出错了:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存流程
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		
		ProcessDeploy processDeploy = new ProcessDeploy();
		
		try {
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");

			conn = new DBConnect().getConnect();

			String id = StringUtil.showNull(request.getParameter("id"));
			String pname = StringUtil.showNull(request.getParameter("pname"));
			String desccontent = StringUtil.showNull(request.getParameter("desccontent"));
			String relateForm = StringUtil.showNull(request.getParameter("relateForm"));
			String orderByRelateForm = StringUtil.showNull(request.getParameter("orderByRelateForm"));
			String processDes = StringUtil.showNull(request.getParameter("processDes"));
			ProcessService processService = new ProcessService(conn);
			
			processDeploy = new ProcessDeploy();
			processDeploy.setId(id);
			processDeploy.setPname(pname);
			processDeploy.setDesccontent(desccontent);
			processDeploy.setRelateForm(relateForm);
			processDeploy.setOrderByRelateForm(orderByRelateForm) ;
			processDeploy.setProcessDes(processDes) ;
			processDeploy.setUpdateTime(StringUtil.getCurDateTime());
			processDeploy.setUpdateUser(userSession.getUserId());
			processDeploy.setJoin_sql(request.getParameter("join_sql"));
			processDeploy.setJoin_head_jarr(request.getParameter("join_head_jarr"));
			processDeploy.setHidden_cols(request.getParameter("hidden_cols"));
			if("".equals(id)) {
				//新增,生成uuid 
				id = StringUtil.getUUID() ;
				processDeploy.setId(id) ;
				processDeploy.setPkey(id) ;
				processService.addProcessDeploy(processDeploy) ;
			}else {
				processDeploy.setId(id) ;
				processDeploy.setPkey(id) ;
				processService.updateProcessDeploy(processDeploy) ;
			}
		
			log.log("流程【"+pname+"】保存成功!") ;
			
			response.sendRedirect("process.do?method=addAndEdit&id=" + processDeploy.getId());

		} catch (Exception e) {
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 保存并发布流程
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveAndDeploy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		
		ProcessDeploy processDeploy = new ProcessDeploy();
		
		try {
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");

			conn = new DBConnect().getConnect();

			String id = request.getParameter("id");
			String processKey = id;
			String jbpmXml = request.getParameter("jbpmXml");
			String isAdd = request.getParameter("isAdd");
			String isDeploy = StringUtil.showNull(request.getParameter("isDeploy")) ;
			String isNotSelectUserNodes = StringUtil.showNull(request.getParameter("isNotSelectUserNodes")) ;
			
			ProcessService processService = new ProcessService(conn);
			String pname = processService.getProcessDeployById(id).getPname() ;
			String processDefinitionId = "" ;
			if(!"".equals(isDeploy)) {
				
				try {
					String tempPath = JPDL_PATH + StringUtil.getUUID() + "/";
					String zipPath = JPDL_PATH + processKey + ".zip";
		
					File tempFilePath = new File(tempPath);
		
					if (!tempFilePath.exists()) {
						tempFilePath.mkdirs();
					}
					// 生成xml文件		
					File file = new File(tempPath + processKey + ".jpdl.xml");
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
					osw.write(jbpmXml);
					osw.flush();
					osw.close();
		
					FileInputStream fis = new FileInputStream(file);
		
					// 生成图片文件
					JpdlProperty jbdlProperty = new JpdlProperty(fis);
					ImageIO.write(new JpdlDrawer().draw(jbdlProperty), "png", new File(
							tempPath + processKey + ".png"));
		
					// 压缩文件
					ZipUtil zipUtil = new ZipUtil();
					File newFile = new File(JPDL_PATH + processKey + ".zip");
		
					if (newFile.exists()) {
						newFile.delete();
					}
		
					zipUtil.zip(tempPath, zipPath);
		
					// 删除临时文件夹的文件
					if (tempFilePath.exists()) {
						ManuFileService.deleteFile(tempFilePath) ;
					}
					
					JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
		
					String deploymentId = jbpmTemplate.deployByZip(zipPath);
					
					processDefinitionId = jbpmTemplate.getProcessDefinition(deploymentId).getId();
				}catch(Exception e) {
					log.exception("流程【"+pname+"】发布失败,原因:" + e.getMessage(), e) ;
					e.printStackTrace() ;
				}
			}
		
			processDeploy = new ProcessDeploy();
			processDeploy.setId(id);
			processDeploy.setPdId(processDefinitionId);
			processDeploy.setJbpmXml(jbpmXml);
			processDeploy.setUpdateTime(StringUtil.getCurDateTime());
			processDeploy.setUpdateUser(userSession.getUserId());
			processDeploy.setNotSelectUserNodes(isNotSelectUserNodes) ;
			processService.updateDesign(processDeploy);
			
			
			log.log("流程【"+pname+"】设计保存成功!") ;
			
			response.sendRedirect("process.do?method=designer&id=" + processDeploy.getId());

		} catch (Exception e) {
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 删除流程
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void remove(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		try {

			conn = new DBConnect().getConnect();

			String id = request.getParameter("id");

			ProcessService processService = new ProcessService(conn);
			ProcessDeploy processDeploy = processService
					.getProcessDeployById(id);

			try {
				// 删除流程部署
				String processDefineId = processDeploy.getPdId();
				ProcessDefinition pd = JbpmServicce.getJbpmTemplate().getProcessDefinitionByPdid(processDefineId) ;
				String deploymentid = pd.getDeploymentId() ;
				JbpmServicce.getJbpmTemplate().deleteDeploymentCascade(deploymentid);
			} catch (Exception e) {
				log.exception("删除流程部署失败", e) ;
			}

			processService.deleteProcessDeploy(id);

			File zipFile = new File(JPDL_PATH + id + ".zip");

			if (zipFile.exists()) {
				zipFile.delete();
			}
			
			response.sendRedirect("process.do?method=processDefineList&backQuery=1");

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 启动流程
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void start(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		PrintWriter out = null;
		try {

			conn = new DBConnect().getConnect();
			String key = StringUtil.showNull(request.getParameter("key"));

			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			String userId = userSession.getUserId();

			if (!"".equals(key)) {
				ProcessInstance pi = JbpmServicce.getJbpmTemplate()
						.startProcessByKey(key, userId);
				String pid = pi.getId();

				ProcessApply pa = new ProcessApply();
				pa.setId(UUID.randomUUID().toString());
				pa.setPkey(key);
				pa.setPid(pid);
				pa.setForeignId("xxx");
				pa.setPname("xxx");
				pa.setApplyUserId(userSession.getUserId());
				pa.setApplyTime(time);

				ProcessService ps = new ProcessService(conn);
				ps.addProcessApply(pa);
			}

			out = response.getWriter();

			out.write("启动成功");
		} catch (Exception e) {
			out.write("error=" + e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 跳转到审批页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView goAudit(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("/process/audit.jsp");

		try {
			String foreignId = request.getParameter("foreignId");
			String taskId = request.getParameter("taskId");
			String pId = StringUtil.showNull(request.getParameter("pId"));

			if (!"".equals(pId)) {
				// 查看流程信息
				modelAndView.addObject("view", "view");
			} else {
				// 审批
				pId = JbpmServicce.getJbpmTemplate().getProcessInstanceId(
						taskId);
				String taskForm = JbpmServicce.getJbpmTemplate()
						.getFormResourceName(taskId);
				modelAndView.addObject("taskForm", taskForm);
			}

			modelAndView.addObject("taskId", taskId);
			modelAndView.addObject("pId", pId);
			modelAndView.addObject("foreignId", foreignId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	/**
	 * 审批操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView audit(HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			String userId = userSession.getUserId();

			String pId = request.getParameter("pId");
			String taskId = request.getParameter("taskId");
			String foreignId = request.getParameter("foreignId");
			String activityName = JbpmServicce.getJbpmTemplate()
					.getActivityName(taskId);

			String advice = StringUtil.showNull(request.getParameter("advice"));
			String label = StringUtil.showNull(request.getParameter("label"));

			ProcessService ps = new ProcessService(conn);
			ProcessForm pf = new ProcessForm();
			pf.setId(UUID.randomUUID().toString());
			pf.setpId(pId);
			pf.setKey(label);
			pf.setValue(advice);
			pf.setDealTime(time);
			pf.setDealUserId(userId);
			pf.setNodeName(activityName);
			ps.addProcessForm(pf);

			// 完成任务
			JbpmServicce.getJbpmTemplate().completeTask(taskId);

			// 跳到查看流程信息页面
			response.sendRedirect(request.getContextPath()
					+ "/process.do?method=goAudit&pId=" + pId + "&foreignId="
					+ foreignId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	public ModelAndView back(HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			String userId = userSession.getUserId();
			String pId = request.getParameter("pId");
			String taskId = request.getParameter("taskId");
			String activityName = JbpmServicce.getJbpmTemplate()
					.getActivityName(taskId);

			String advice = request.getParameter("advice");
			String label = StringUtil.showNull(request.getParameter("label"));

			ProcessService ps = new ProcessService(conn);
			ProcessForm pf = new ProcessForm();
			pf.setpId(pId);
			pf.setKey(label);
			pf.setValue(advice);
			pf.setDealTime(time);
			pf.setDealUserId(userId);
			pf.setNodeName(activityName + "退回");
			ps.addProcessForm(pf);

			// 回退
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("isBack", "true");
			JbpmServicce.getJbpmTemplate().setTaskVariables(taskId, variables);
			JbpmServicce.getJbpmTemplate().completeTask(taskId, "驳回");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 判断流程是否发布
	 * @param request
	 * @param response
	 * @return
	 */
	public void processExist(HttpServletRequest request,
			HttpServletResponse response){
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			conn = new DBConnect().getConnect() ;
			out = response.getWriter() ;
			String key = request.getParameter("key") ;
			ProcessService cps = new ProcessService(conn) ;
			ProcessDeploy pd = cps.getProcessDeploy(key) ;
			String pdId = "" ;
			if(pd != null) {
				pdId = pd.getPdId() ;
			}
			
			out.write(pdId) ;
			out.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn) ;
		}
	}

	/**
	 * 流程管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView processDefineList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(LIST_VIEW);
		
		 
	 	 String sql = " select a.id,pdid, pkey, pname, desccontent, property, updateTime, b.name "
					+ " from mt_jbpm_processdeploy A "
					+ " left join k_user B on a.updateuser=b.id "
	                + " where 1=1 ${pName}";
		
		DataGridProperty pp = new DataGridProperty();

		pp.setTableID("processDefineList");

		pp.setSQL(sql);

		pp.addColumn("流程名称", "pname");
		pp.addColumn("流程说明", "desccontent");
		pp.addColumn("更新人", "user_name");
		pp.addColumn("更新时间", "updateTime");

		pp.setWhichFieldIsValue(1);
		
		pp.setTrActionProperty(true); 
		pp.setTrAction(" pkey=\"${pkey}\" pdid=\"${pdid}\" ");
		
		pp.setInputType("radio");
		pp.setOrderBy_CH("pname,updateTime");
		pp.setDirection_CH("desc,desc");

		pp.addSqlWhere("pName", " and pname like'%${processName}%'");
		
		//返回到前一个查询sql
		String backQuery=StringUtil.showNull(request.getParameter("backQuery"));
		if(backQuery.equals("1")){
			if(request.getSession().getAttribute(ExtGrid.sessionPre + pp.getTableID())!=null){
				DataGridProperty oldPP=(DataGridProperty) request.getSession().getAttribute(ExtGrid.sessionPre + pp.getTableID());
				pp.setSQL(oldPP.getSQL());
				pp.setPage_CH(oldPP.getPage_CH());
			}			
		}		
		
		request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(),
				pp);

		return modelAndView;
	}
	
	
	/**
	 * 查看流程图
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView viewImageByTaskId(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(VIEWIMAGE) ;
		try {
			String taskId = request.getParameter("id") ;
			String processInstanceId = JbpmServicce.getJbpmTemplate().getProcessInstanceId(taskId) ;
			ActivityCoordinates ac = JbpmServicce.getJbpmTemplate().getActivityCoordinates(processInstanceId) ;
			
			modelAndView.addObject("ac", ac) ;
			modelAndView.addObject("id", processInstanceId) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	/**
	 * 查看流程图
	 * @param request
	 * @param response
	 * @return
	 */
	public void viewImageByDefinitionId(HttpServletRequest request,HttpServletResponse response){
		try {
			String pdId = request.getParameter("pdId") ;
			InputStream is = JbpmServicce.getJbpmTemplate().getProcessImageByDefinitionId(pdId);
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 查看流程图
	 * @param request
	 * @param response
	 * @return
	 */
	public void viewImageByInstanceId(HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
			String processInstanceId = request.getParameter("id") ;
			InputStream is = JbpmServicce.getJbpmTemplate().getProcessImageByInstanceId(processInstanceId) ;
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		 } catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * 查看流程图
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView viewImageByPIdOrKey(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(VIEWIMAGE) ;
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect() ;
			String processInstanceId = StringUtil.showNull(request.getParameter("id")) ;
			String processDefinitionKey = StringUtil.showNull(request.getParameter("key")) ;
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
			ProcessInstance pi = jbpmTemplate.getExecutionService().findProcessInstanceById(processInstanceId) ;
			if(pi == null) {
				ProcessService ps = new ProcessService(conn) ;
				ProcessDeploy pd = ps.getProcessDeploy(processDefinitionKey) ;
				modelAndView.addObject("pdId",pd.getPdId()) ;
			}else {
				ActivityCoordinates ac = jbpmTemplate.getActivityCoordinates(processInstanceId) ;
				modelAndView.addObject("ac", ac) ;
				modelAndView.addObject("id", processInstanceId) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		 return modelAndView ;
	}
	
	/**
	 * 用zip包发布流程方法
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void deploy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect() ;
			FileUpload fileUpload = new FileUpload(request);
			response.setContentType("text/html;charset=UTF-8") ;
			
			//文件上传路径
			File file = new File(JPDL_PATH);
			if(!file.exists()) {
				file.mkdirs() ;
			}
			
			Map parameters = fileUpload.UploadFile(null,null);
			
			String filename = (String)parameters.get("fileName") ;
			String processKey = (String)parameters.get("processKey") ;
			String filePath = (String)parameters.get("filePath") ;
			
			if(filename.indexOf(".zip") == -1) {
				throw new Exception("error=上传文件格式不是有效的流程定义文件!!路径："+filePath+filename);
			}
			
			File newFile = new File(JPDL_PATH+filename) ;
			if(newFile.exists()) {
				newFile.delete() ;
			}
			File oldFile = new File(filePath+filename) ;
			ManuFileService mfs = new ManuFileService() ;
			mfs.copyFile(oldFile, newFile) ;
			
			String deploymentId = JbpmServicce.getJbpmTemplate().deployByZip("D:\\project\\sdzj\\process\\"+filename) ;
			String processDefinitionId = JbpmServicce.getJbpmTemplate().getProcessDefinitionByDeployId(deploymentId).getId() ;
			ProcessService ps = new ProcessService(conn) ;
			ProcessDeploy pd =  ps.getProcessDeploy(processKey) ;
			pd.setPdId(processDefinitionId) ; 
			ps.updateProcessDeploy(pd) ;
			response.sendRedirect(request.getContextPath()+"/process.do?method=processDefineList") ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn) ;
		}
	}
	
	
	public ModelAndView checkAuditList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(AUDITLIST) ;
		Connection conn = null ;
		DbUtil dbUtil=null;
		JSONArray jarrJsonHead=new JSONArray();
		String joinCols="";
		JSONObject jsonRe=new JSONObject();
		String testSql = "" ,re="";
		String join_head_jarr=request.getParameter("join_head_jarr");
		try {
			
			conn = new DBConnect().getConnect() ;
			dbUtil=new DbUtil(conn);
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			
			ProcessService ps = new ProcessService(conn) ;
			FormDefineService formDefineService = new FormDefineService(conn);
			FormQueryConfigService formQueryService = new FormQueryConfigService(conn);
			
			String pkey = StringUtil.showNull(request.getParameter("pkey")) ;
			String menuId = StringUtil.showNull(request.getParameter("menuId")) ;
			String[] keyArr = pkey.split(",") ;
			
			String tempKey = "" ;
			if(keyArr.length > 1) {
				tempKey = keyArr[0] ;
			}else {
				tempKey = pkey ;
			}
			//ProcessDeploy pd = ps.getProcessDeploy(tempKey) ;
			ProcessDeployVO pd=dbUtil.load(ProcessDeployVO.class, "PKEY",tempKey);
		
			String formId = "" ;
			String orderByRelateForm = "" ;
			if(pd != null) {
				formId = StringUtil.showNull(pd.getRelateForm()) ;
				tempKey = tempKey.replaceAll("-", "_");
				
				orderByRelateForm = StringUtil.showNull(pd.getOrderByRelateForm()) ;
			}
			// 待审核任务列表
			String processSql = ProcessService.getDealingSql(pkey, userSession.getUserId()) ;
			
			//获取表单sql
			//String formSql = StringUtil.showNull(pd.getJoin_sql());//获取自定义的sql
			String formSql =request.getParameter("join_sql");
			if ("".equals(formSql)) { //如果自定义的sql为空，则构造默认sql
				formSql = StringUtil.showNull(formDefineService.getShowSql(formId));
			} 
			if(!StringUtil.isBlank(join_head_jarr)){
				jarrJsonHead=JSONArray.fromObject(join_head_jarr);
				for(int i=0;i<jarrJsonHead.size();i++){
					JSONObject jsonHead=jarrJsonHead.getJSONObject(i);
					String colName=jsonHead.getString("colName");
					String colAsName=jsonHead.getString("colAsName");
					
					joinCols+=","+colName+" as "+colAsName;
				}
			}
			formSql = StringUtil.transRequestValue(request, formSql);
			formSql = StringUtil.transSessionValue(request.getSession(), formSql);
			formSql = StringUtil.transUserPopedomValue(request, formSql);
			
			
			String orderBy = "" ;
			String direction = "" ;
			if(StringUtil.isBlank(formSql)) {
				testSql = processSql ;
			}else {
				testSql = " select a.* "+joinCols+" from ( "
					+ " " + processSql
					+ " ) a "
					+ formSql
					+ " where 1=1 " ;
			
			String testSql2=testSql.replace("${userPopedom}", "''");
			dbUtil.execute(testSql2);
		    jsonRe.put("re", 0);
			}
			}catch(Exception ex){
			jsonRe.put("re", -1);
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		jsonRe.put("sql", testSql);
		response.getWriter().write(jsonRe.toString());
		return null;
	}

	/**
	 * 测试流程通用审批List方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView(AUDITLIST) ;
		Connection conn = null ;
		DbUtil dbUtil=null;
		JSONArray jarrJsonHead=new JSONArray();
		String joinCols="",hidden_cols="";
		
		try {
			
			conn = new DBConnect().getConnect() ;
			dbUtil=new DbUtil(conn);
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			
			ProcessService ps = new ProcessService(conn) ;
			FormDefineService formDefineService = new FormDefineService(conn);
			FormQueryConfigService formQueryService = new FormQueryConfigService(conn);
			
			String pkey = StringUtil.showNull(request.getParameter("pkey")) ;
			String menuId = StringUtil.showNull(request.getParameter("menuId")) ;
			String[] keyArr = pkey.split(",") ;
			
			String tempKey = "" ;
			if(keyArr.length > 1) {
				tempKey = keyArr[0] ;
			}else {
				tempKey = pkey ;
			}
			//ProcessDeploy pd = ps.getProcessDeploy(tempKey) ;
			ProcessDeployVO pd=dbUtil.load(ProcessDeployVO.class, "PKEY",tempKey);
		    hidden_cols=StringUtil.showNull(pd.getHidden_cols());
			String formId = "" ;
			String orderByRelateForm = "" ;
			if(pd != null) {
				formId = StringUtil.showNull(pd.getRelateForm()) ;
				tempKey = tempKey.replaceAll("-", "_");
				
				orderByRelateForm = StringUtil.showNull(pd.getOrderByRelateForm()) ;
			}
			// 待审核任务列表
			String processSql = ProcessService.getDealingSql(pkey, userSession.getUserId()) ;
			
			//获取表单sql
			String formSql = StringUtil.showNull(pd.getJoin_sql());//获取自定义的sql
			if ("".equals(formSql)) { //如果自定义的sql为空，则构造默认sql
				formSql = StringUtil.showNull(formDefineService.getShowSql(formId));
			} 
			if(!StringUtil.isBlank(pd.getJoin_head_jarr())){
				jarrJsonHead=JSONArray.fromObject(pd.getJoin_head_jarr());
				for(int i=0;i<jarrJsonHead.size();i++){
					JSONObject jsonHead=jarrJsonHead.getJSONObject(i);
					String colName=jsonHead.getString("colName");
					String colAsName=jsonHead.getString("colAsName");
					
					joinCols+=","+colName+" as "+colAsName;
				}
			}
			formSql = StringUtil.transRequestValue(request, formSql);
			formSql = StringUtil.transSessionValue(request.getSession(), formSql);
			formSql = StringUtil.transUserPopedomValue(request, formSql);
			String sql = "" ;
			
			String orderBy = "" ;
			String direction = "" ;
			if(StringUtil.isBlank(formSql)) {
				sql = processSql ;
			}else {
				sql = " select a.* "+joinCols+" from ( "
					+ " " + processSql
					+ " ) a "
					+ formSql
					+ " where 1=1 " ;
				/*
				if("1".equals(orderByRelateForm)){
					List<FormQuery> list = formQueryService.getformQuery(formId);
					for (FormQuery query : list) {
						if (query.getBorder() != 0) {
							orderBy += "b."+query.getEnname() + ",";
							direction += ( query.getBorder() > 0 ? "asc," : "desc,");
						}
					}
					if(!"".equals(orderBy)){
						orderBy = orderBy.substring(0, orderBy.lastIndexOf(","));
						direction = direction.substring(0, direction.lastIndexOf(","));
					}
				}
				*/
			}
			
			
			sql += " ${pName} ${nodeName} ${applyUserName} " ;
			
			//取出session里的
			DataGridProperty prePp = (DataGridProperty)request.getSession().getAttribute(ExtGrid.sessionPre + "auditList_"+tempKey);
			
			
			DataGridProperty pp = new DataGridProperty();
			
			pp.setCustomerId("");
			pp.setTableID("auditList_"+tempKey);
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintColumnWidth("20,20,20,20,20");
			pp.setPrintTitle("流程审核列表");
			pp.setColumnWidth("20%,15%,15%,10%") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" pkey=\"${pkey}\" pid=\"${pid}\" taskId=\"${taskId}\" ");
			
			//pp.addColumn("流程名称", "pName");
			

			pp.addSqlWhere("nodeName", " and a.auditStatus like'%${nodeName}%'");
			if(StringUtil.isBlank(formSql)){
			pp.addSqlWhere("applyUserName", " and f.name like'%${applyUserName}%'");
			pp.addSqlWhere("pName", " and d.pName like'%${pName}%'");

			}else{
				pp.addSqlWhere("pName", " and a.pName like'%${pName}%'");
				pp.addSqlWhere("applyUserName", " and applyUserName like'%${applyUserName}%'");

			}
			pp.setSQL(sql);  
			
			if(!"".equals(orderBy)) {
				pp.setOrderBy_CH(orderBy);
				pp.setDirection(direction);
			}else {
				pp.setOrderBy_CH("create_");
				pp.setDirection("desc");
			}
			
			
			
			// 已审核任务列表
			String processSql2 = ProcessService.getDealtSql(pkey, userSession.getUserId()) ;
			
			String sql2 = "" ;
			if("".equals(formSql)) {
				sql2 = processSql2 ;
			}else {
				sql2 = " select a.* "+joinCols+" from ( "
					+ " " + processSql2
					+ " ) a "
					//+ " inner join ( "
					+   formSql
					//+ " ) b "
					//+ " on a.foreignUuid = b.uuid " 
					+ " where 1=1 " ;
			}
			
			sql2 += " ${pName} ${nodeName} ${applyUserName} " ;
			
			//取出session里的
			DataGridProperty prePp2 = (DataGridProperty)request.getSession().getAttribute(ExtGrid.sessionPre + "auditedList_"+tempKey);
			
			
			DataGridProperty pp2 = new DataGridProperty();
			
			pp2.setCustomerId("");
			pp2.setTableID("auditedList_"+tempKey);
			pp2.setInputType("radio");
			pp2.setWhichFieldIsValue(1);
			pp2.setPrintEnable(true);
			pp2.setPrintVerTical(false);
			pp2.setPrintColumnWidth("20,20,20,20,20");
			pp2.setPrintTitle("流程已处理列表");
			pp2.setColumnWidth("20,10,10,20") ;
			
			pp2.setTrActionProperty(true) ;
			pp2.setTrAction(" pkey=\"${pkey}\" pId=\"${pId}\" ");
			
			
			//pp2.addColumn("流程名称", "pName");
			//pp2.addColumn("申请人", "userName");
			//pp2.addColumn("申请时间", "applytime");
			//pp2.addColumn("当前节点", "activeName");
			//pp2.addColumn("当前处理人", "dealUserName");
			//pp2.addColumn("送达时间", "arrivetime");
			//pp2.addColumn("办理时间", "dealtime");
			
			
			if(StringUtil.isBlank(formSql)){
				pp2.addSqlWhere("nodeName", " and e.ACTIVITYNAME_ like'%${nodeName}%'");
				pp2.addSqlWhere("pName", " and b.pname like'%${pName}%'");
				pp2.addSqlWhere("applyUserName", " and d.name like'%${applyUserName}%'");
			}else{//a.activeName
				pp2.addSqlWhere("nodeName", " and a.activeName like'%${nodeName}%'");
				pp2.addSqlWhere("pName", " and a.pname like'%${pName}%'");
				pp2.addSqlWhere("applyUserName", " and a.userName like'%${applyUserName}%'");	
			}
			pp2.setSQL(sql2);
			pp2.setOrderBy_CH("arrivetime");
			pp2.setDirection("desc");
			
			
			if(!"".equals(formSql)) {
				//List<FormQuery> list = formQueryService.getformQuery(formId);
				
				String width = "" ;
				
				for (int i=0;i<jarrJsonHead.size();i++) {
					// 在list里面显示的列，0不显示1显示在列表
					 JSONObject jsonHead=jarrJsonHead.getJSONObject(i);  
					String colAsName=jsonHead.getString("colAsName");
					//colName=colName.contains(".")?colName.substring(colName.indexOf(".")+1,colName.length()):colName;
					String colLabel=jsonHead.getString("colLabel");
					String colWidth =jsonHead.getString("colWidth");
					   pp.addColumn(colLabel, colAsName,"1");
					   pp2.addColumn(colLabel, colAsName,"1");
						//列宽
						
						if(!"".equals(colWidth)) {
							width += colWidth + "," ;
						}else {
							width += "10," ; 
						}
					}
					/*
					//合计等汇总信息
					String summaryType = StringUtil.showNull(bean.getSummaryType()) ;
					if(!"".equals(summaryType)) {
						pp.addColSummary(bean.getEnname(), summaryType) ;
						pp2.addColSummary(bean.getEnname(), summaryType) ;
					}
					*/
					
				
				
				// 设置列宽
				if (width.indexOf(",")>0) {
					width = width.substring(0, width.lastIndexOf(","));
				} 
				pp.setColumnWidth(width);// 设置列宽
				pp2.setColumnWidth(width);// 设置列宽
			}
			
			for(ProcessUnCheckColumns processUnCheckColumns:ProcessUnCheckColumns.values()){
				if(hidden_cols.contains(processUnCheckColumns.name()))continue;
				pp.addColumn(processUnCheckColumns.getLabel(), processUnCheckColumns.name());
			}
			for(ProcessCheckedColumns processCheckedColumns:ProcessCheckedColumns.values()){
				if(hidden_cols.contains(processCheckedColumns.name()))continue;
				pp2.addColumn(processCheckedColumns.getLabel(), processCheckedColumns.name());
			}
			
			
			/*
			pp.addColumn("当前节点", "auditStatus");
			pp.addColumn("送达时间", "create_");
			pp.addColumn("申请人", "applyUserName");
			pp.addColumn("申请时间", "applytime");
			
			pp2.addColumn("申请人", "userName");
			pp2.addColumn("申请时间", "applytime");
			pp2.addColumn("当前节点", "activeName");
			pp2.addColumn("送达时间", "arrivetime");
			pp2.addColumn("办理时间", "dealtime");
			*/
			request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(ExtGrid.sessionPre + pp2.getTableID(),pp2);
			
			modelAndView.addObject("pKey",tempKey) ;
			
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		} 
		 return modelAndView;
	}
	
	
	/**
	 * 测试流程通用审批List方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditList_old(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView(AUDITLIST) ;
		Connection conn = null ;
		try {
			
			conn = new DBConnect().getConnect() ;
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			
			ProcessService ps = new ProcessService(conn) ;
			FormDefineService formDefineService = new FormDefineService(conn);
			FormQueryConfigService formQueryService = new FormQueryConfigService(conn);
			
			String pkey = StringUtil.showNull(request.getParameter("pkey")) ;
			String menuId = StringUtil.showNull(request.getParameter("menuId")) ;
			String[] keyArr = pkey.split(",") ;
			
			String tempKey = "" ;
			if(keyArr.length > 1) {
				tempKey = keyArr[0] ;
			}else {
				tempKey = pkey ;
			}
			ProcessDeploy pd = ps.getProcessDeploy(tempKey) ;
			
			String formId = "" ;
			String orderByRelateForm = "" ;
			if(pd != null) {
				formId = StringUtil.showNull(pd.getRelateForm()) ;
				tempKey = tempKey.replaceAll("-", "_");
				
				orderByRelateForm = StringUtil.showNull(pd.getOrderByRelateForm()) ;
			}
			// 待审核任务列表
			String processSql = ProcessService.getDealingSql(pkey, userSession.getUserId()) ;
			
			//获取表单sql
			String formSql = StringUtil.showNull(formDefineService.getListSql(formId));//获取自定义的sql
			if ("".equals(formSql)) { //如果自定义的sql为空，则构造默认sql
				formSql = StringUtil.showNull(formDefineService.getShowSql(formId));
			} 
			formSql = StringUtil.transRequestValue(request, formSql);
			formSql = StringUtil.transSessionValue(request.getSession(), formSql);
			formSql = StringUtil.transUserPopedomValue(request, formSql);
			String sql = "" ;
			
			String orderBy = "" ;
			String direction = "" ;
			if("".equals(formSql)) {
				sql = processSql ;
			}else {
				sql = " select * from ( "
					+ " " + processSql
					+ " ) a "
					+ " inner join ( "
					+   formSql
					+ " ) b "
					+ " on a.foreignUuid = b.uuid " 
					+ " where 1=1 " ;
				
				if("1".equals(orderByRelateForm)){
					List<FormQuery> list = formQueryService.getformQuery(formId);
					for (FormQuery query : list) {
						if (query.getBorder() != 0) {
							orderBy += "b."+query.getEnname() + ",";
							direction += ( query.getBorder() > 0 ? "asc," : "desc,");
						}
					}
					if(!"".equals(orderBy)){
						orderBy = orderBy.substring(0, orderBy.lastIndexOf(","));
						direction = direction.substring(0, direction.lastIndexOf(","));
					}
				}
				
			}
			
			
			sql += " ${pName} ${nodeName} ${applyUserName} " ;
			
			//取出session里的
			DataGridProperty prePp = (DataGridProperty)request.getSession().getAttribute(ExtGrid.sessionPre + "auditList_"+tempKey);
			
			
			DataGridProperty pp = new DataGridProperty();
			
			pp.setCustomerId("");
			pp.setTableID("auditList_"+tempKey);
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintColumnWidth("20,20,20,20,20");
			pp.setPrintTitle("流程审核列表");
			pp.setColumnWidth("20%,15%,15%,10%") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" pkey=\"${pkey}\" pid=\"${pid}\" taskId=\"${taskId}\" ");
			
			//pp.addColumn("流程名称", "pName");
			

			pp.addSqlWhere("pName", " and d.pName like'%${pName}%'");
			pp.addSqlWhere("nodeName", " and a.auditStatus like'%${nodeName}%'");
			pp.addSqlWhere("applyUserName", " and f.user_name like'%${applyUserName}%'");
			
			pp.setSQL(sql);  
			
			if(!"".equals(orderBy)) {
				pp.setOrderBy_CH(orderBy);
				pp.setDirection(direction);
			}else {
				pp.setOrderBy_CH("create_");
				pp.setDirection("desc");
			}
			
			
			
			// 已审核任务列表
			String processSql2 = ProcessService.getDealtSql(pkey, userSession.getUserId()) ;
			
			String sql2 = "" ;
			if("".equals(formSql)) {
				sql2 = processSql2 ;
			}else {
				sql2 = " select * from ( "
					+ " " + processSql2
					+ " ) a "
					+ " inner join ( "
					+   formSql
					+ " ) b "
					+ " on a.foreignUuid = b.uuid " 
					+ " where 1=1 " ;
			}
			
			sql2 += " ${pName} ${nodeName} ${applyUserName} " ;
			
			//取出session里的
			DataGridProperty prePp2 = (DataGridProperty)request.getSession().getAttribute(ExtGrid.sessionPre + "auditedList_"+tempKey);
			
			
			DataGridProperty pp2 = new DataGridProperty();
			
			pp2.setCustomerId("");
			pp2.setTableID("auditedList_"+tempKey);
			pp2.setInputType("radio");
			pp2.setWhichFieldIsValue(1);
			pp2.setPrintEnable(true);
			pp2.setPrintVerTical(false);
			pp2.setPrintColumnWidth("20,20,20,20,20");
			pp2.setPrintTitle("流程已处理列表");
			pp2.setColumnWidth("20,10,10,20") ;
			
			pp2.setTrActionProperty(true) ;
			pp2.setTrAction(" pkey=\"${pkey}\" pId=\"${pId}\" ");
			
			
			//pp2.addColumn("流程名称", "pName");
			//pp2.addColumn("申请人", "userName");
			//pp2.addColumn("申请时间", "applytime");
			//pp2.addColumn("当前节点", "activeName");
			pp2.addColumn("当前处理人", "dealUserName");
			//pp2.addColumn("送达时间", "arrivetime");
			//pp2.addColumn("办理时间", "dealtime");
			
			pp2.addSqlWhere("pName", " and b.pname like'%${pName}%'");
			pp2.addSqlWhere("nodeName", " and e.ACTIVITYNAME_ like'%${nodeName}%'");
			pp2.addSqlWhere("applyUserName", " and d.name like'%${applyUserName}%'");
			
			pp2.setSQL(sql2);
			pp2.setOrderBy_CH("arrivetime");
			pp2.setDirection("desc");
			
			
			if(!"".equals(formSql)) {
				List<FormQuery> list = formQueryService.getformQuery(formId);
				String width = "" ;
				
				for (FormQuery bean : list) {
					// 在list里面显示的列，0不显示1显示在列表
					if (bean.getBshow() == 1) {
						if (bean.getBtype() != null) {
							
							if("showAttach".equals(bean.getBtype())) {
								
								pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
								pp2.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
							}else if("showProcess".equals(bean.getBtype())){
								pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.ProcessColumnProcess", null, formId);
								pp2.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.ProcessColumnProcess", null, formId);
								//pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
							} else {
								pp.addColumn(bean.getName(), bean.getEnname(),bean.getBtype());
								pp2.addColumn(bean.getName(), bean.getEnname(),bean.getBtype());
							}
							
						} else {
							pp.addColumn(bean.getName(), bean.getEnname());
							pp2.addColumn(bean.getName(), bean.getEnname());
						}
						
						//列宽
						String colWidth = StringUtil.showNull(bean.getWidth()) ;
						if(!"".equals(colWidth)) {
							width += colWidth + "," ;
						}else {
							width += "10," ; 
						}
					}
					
					//合计等汇总信息
					String summaryType = StringUtil.showNull(bean.getSummaryType()) ;
					if(!"".equals(summaryType)) {
						pp.addColSummary(bean.getEnname(), summaryType) ;
						pp2.addColSummary(bean.getEnname(), summaryType) ;
					}
					
				}
				
				// 设置列宽
				if (width.indexOf(",")>0) {
					width = width.substring(0, width.lastIndexOf(","));
				} 
				pp.setColumnWidth("15,"+width);// 设置列宽
				pp2.setColumnWidth("15,7,"+width);// 设置列宽
			}
			
			pp.addColumn("当前节点", "auditStatus");
			pp.addColumn("送达时间", "create_");
			pp.addColumn("申请人", "applyUserName");
			pp.addColumn("申请时间", "applytime");
			
			pp2.addColumn("申请人", "userName");
			pp2.addColumn("申请时间", "applytime");
			pp2.addColumn("当前节点", "activeName");
			pp2.addColumn("送达时间", "arrivetime");
			pp2.addColumn("办理时间", "dealtime");
			
			request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(ExtGrid.sessionPre + pp2.getTableID(),pp2);
			
			modelAndView.addObject("pKey",tempKey) ;
			
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		} 
		 return modelAndView;
	}
	
	
	/**
	 * 测试流程通用审批方法
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditProcess(HttpServletRequest request,
			HttpServletResponse response){
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect() ;
			String taskId = request.getParameter("taskId") ;
			String pid = JbpmServicce.getJbpmTemplate().getProcessInstanceId(taskId) ;
			ProcessInstance processInstance = JbpmServicce.getJbpmTemplate().getExecutionService().findProcessInstanceById(pid);
			
			JbpmServicce.getJbpmTemplate().completeTask(taskId) ;
			
			//完成任务
			if(processInstance.isActive("state")) {
				JbpmServicce.getJbpmTemplate().getExecutionService().signalExecutionById(pid) ;
			}
			
			
			response.sendRedirect(request.getContextPath()+"/process.do?method=auditList") ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 
	 * 测试流程通用启动方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView startProcess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect() ;
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
			
			Map<String,String> startMap = new HashMap<String, String>() ;
			startMap.put("applyUser", userId) ;
			
			//首次发起申请 
			String pdId = StringUtil.showNull(request.getParameter("pdId")) ;
			ProcessInstance pi = JbpmServicce.getJbpmTemplate().startProcessById(pdId,startMap); 
			
			/*
			//完成第一个结点的任务
			TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
			List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();  				
			Task myTask = taskList2.get(0) ;
			jbpmTemplate.setTaskVariables(myTask.getId(),startMap) ;
			jbpmTemplate.completeTask(myTask.getId()) ;
			*/
			
			response.sendRedirect(request.getContextPath()+"/process.do?method=processDefineList") ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null ;
	}
	
	
	/**
	 * 
	 * 获取流程自定义表单的列属性
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getFormColumns(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect() ; 
			out = response.getWriter() ;
			
			String formid = StringUtil.showNull(request.getParameter("formid")) ;
			String processKey = StringUtil.showNull(request.getParameter("processKey")) ;
			String nodeName = StringUtil.showNull(request.getParameter("nodeName")) ;
			
			ProcessService ps = new ProcessService(conn) ;
			List<ProcessField> field = ps.getProcessFieldList(formid, processKey, nodeName) ;
			
			String json = JSONArray.fromObject(field).toString() ;
			log.debug("表单Json:"+json) ;
			out.write(json) ;
			
		}catch(Exception e) {
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn) ;
		}	
		return null ;
	}
	
	
	/**
	 * 更新流程自定义表单列的属性
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateFormColumns(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect() ; 
			out = response.getWriter() ;
			
			//表单元素的可见、可写属性
			String[] uuid = request.getParameterValues("uuid") ;
			String[] isHide = request.getParameterValues("isHide") ;
			String[] isReadOnly = request.getParameterValues("isReadOnly") ;
			String[] isProcessVariable = request.getParameterValues("isProcessVariable") ;
			
			//节点名称
			String nodeName = StringUtil.showNull(request.getParameter("node")) ;
			String processKey = StringUtil.showNull(request.getParameter("processKey")) ;
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			
			if(uuid != null) {
				ProcessService ps = new ProcessService(conn) ;
				//先清除之前的属性再新增
				ps.delFieldConfig(processKey, nodeName) ;
				for(int i=0;i<uuid.length;i++) {
					ProcessField ff = new ProcessField() ;
					ff.setUuid(uuid[i]) ;
					ff.setIsHide(isHide[i]) ;
					ff.setIsReadOnly(isReadOnly[i]) ;
					ff.setIsProcessVariable(isProcessVariable[i]) ;
					ff.setNodeName(nodeName) ;
					ff.setProcessKey(processKey) ;
					ff.setFormid(formId) ;
					ps.addFieldConfig(ff) ;
				}
			}
			out.println("<script>window.parent.alert('表单属性保存成功!')</script>");
		}catch(Exception e) {
			out.println("<script>window.parent.alert('后台发生异常，表单属性保存失败!')</script>");
			log.exception("表单属性保存失败", e) ;
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn) ;
		}	
		return null ;
	}
	
	
	/**
	 * 
	 * 流程流转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView processTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		ModelAndView mav = new ModelAndView(AUDIT_VIEW) ;
		try {
			FormDefineAction.setCurr(request);
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect() ; 
			
			String pId = StringUtil.showNull(request.getParameter("pId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			String view = StringUtil.showNull(request.getParameter("view")) ;
			String formEntityId = StringUtil.showNull(request.getParameter("uuid")) ;
			String apply = StringUtil.showNull(request.getParameter("apply")) ;
			
			ProcessService ps = new ProcessService(conn) ;
			ProcessDeploy pd = null ;
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			RepositoryService repositoryService = jbpmTemplate.getRepositoryService() ;
			FormDefineService fds = new FormDefineService(conn);
			fds.setContextPath(request.getContextPath()) ;
			
			String uuid = "" ;  //对应表单实体表的id
			String formatHtml = "" ;  //流程表单对应的html
			String form = "" ;
			String pdId = "" ;
			String subTableName=""; //子表单名字
			List<Transition> nextTrans = null ; //下级节点
			
			
			if(!"".equals(view)) {
				
				PreparedStatement psmt = null ;
				ResultSet rs = null ;
				//查看时，如果没传pId就查出来
				String sql = " select distinct a.pid,b.formid,a.foreignid,a.pkey from mt_jbpm_apply a "
						   + " left join mt_jbpm_processform b on a.pid = b.pid " 
						   + " and a.foreignid = b.formentityid "
						   + " where 1=1 " ;
				
				if(!"".equals(pId)) {
					sql += " and a.pid = '" + pId + "'" ;
				}else {
					sql += " and a.foreignid = '" + formEntityId + "'" ;
				}
				
				psmt = conn.prepareStatement(sql) ;
				rs = psmt.executeQuery() ;
				
				if(rs.next()) {
					pId = StringUtil.showNull(rs.getString(1)) ;
					form = StringUtil.showNull(rs.getString(2)) ;
					uuid = StringUtil.showNull(rs.getString(3)) ;
					pKey = StringUtil.showNull(rs.getString(4)) ;
				}
				
				formatHtml = fds.getFormDataHTML(request,form,uuid) ;
				pd = ps.getProcessDeploy(pKey) ;
				pdId = pd.getPdId();
			}else {
				pd = ps.getProcessDeploy(pKey) ;
				
				if(!"".equals(formEntityId)){
					//修改时,找出当前taskId ;
					String processId = StringUtil.showNull(ps.getPIdByPKeyAndForeignId(pKey, formEntityId)) ;
					TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
					List<Task> taskList2 = tq.processInstanceId(processId).list();  
					if(taskList2 != null & taskList2.size() > 0) {
						Task myTask = taskList2.get(0) ;
						taskId = myTask.getId() ;
					}
					
				}
				
				String curNodeName = "" ;
				if("".equals(taskId) || !"".equals(apply)) {
					//发起申请，读取流程的表单配置
					
					if(!"".equals(taskId)){
						//已经发起，就取旧流程
						pId = jbpmTemplate.getProcessInstanceId(taskId) ;
						ProcessInstance processInstance = jbpmTemplate.getExecutionService().findProcessInstanceById(pId);
						pdId = processInstance.getProcessDefinitionId() ;
					}else {
						pdId = pd.getPdId() ;
					}
					ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
					List activities =  processDefinition.getActivities();  
					
					//找始节点
					ActivityImpl startActivity = null ;
					for(int i=0;i<activities.size();i++){  
			            ActivityImpl activityImpl = (ActivityImpl)activities.get(i);  
			            String type = activityImpl.getType();  
			            
			            if("start".equals(type)) {
			            	startActivity = activityImpl ;
			            }
				    }
					
				    List<Transition> trans=(List<Transition>)startActivity.getOutgoingTransitions(); 
				    
				    if(trans != null) {
				    	Transition startNode = trans.get(0); //start后的第一个节点是发起申请的节点
				    	String StartNodeName = startNode.getDestination().getName() ;
				    	ActivityImpl applyActivity = processDefinition.findActivity(StartNodeName);
				    	TaskActivity taskActivity = (TaskActivity)applyActivity.getActivityBehaviour(); 
				    	form = StringUtil.showNull(taskActivity.getTaskDefinition().getFormResourceName()) ;  //获得发起申请的表单id
				    	
				    	subTableName=fds.getSubTableName(form);
				    	if(!"".equals(form))
				    		formatHtml = fds.getFormDataHTML(request,form,formEntityId) ;
						
						//log.debug("assignee:"+taskActivity.getTaskDefinition().getAssigneeExpression()) ; 
						//log.debug("CandidateUsers:"+taskActivity.getTaskDefinition().getCandidateUsersExpression()) ; 
						//log.debug("AssignmentHandler:"+taskActivity.getTaskDefinition().getAssignmentHandlerReference()) ; 
						
						//获取下级节点
						nextTrans=(List<Transition>)applyActivity.getOutgoingTransitions(); 
						
						curNodeName = StartNodeName ;
				    }
				    
				    ProcessService prs = new ProcessService(conn) ;
					
					pId = StringUtil.showNull(prs.getPIdByPKeyAndForeignId(pKey, formEntityId)) ;
					
					if(!"".equals(pId)) {
						//已经发起申请了，查找taskId
						Task task = jbpmTemplate.getActivityTask(pId) ;
						if(task != null) {
							taskId = task.getId() ;
							curNodeName = task.getActivityName() ;
						}
					}
					uuid = formEntityId ;
					
					//取出只读，可写等信息
					List<ProcessField> fieldList = ps.getProcessFieldList(form, pKey,curNodeName) ;
					String fieldJson = JSONArray.fromObject(fieldList).toString() ;
					mav.addObject("fieldJson",fieldJson) ;
				    
				    mav.addObject("apply",true) ;
				    
				}else {
					
					form = StringUtil.showNull(jbpmTemplate.getFormResourceName(taskId));   
					if("".equals(pId)) {
						pId = jbpmTemplate.getProcessInstanceId(taskId) ;
					}
					ProcessInstance processInstance = jbpmTemplate.getExecutionService().findProcessInstanceById(pId);
					pdId = processInstance.getProcessDefinitionId() ;
					ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
					
					uuid = ps.getProcessFormEntityId(pId, form) ;
					
					subTableName=fds.getSubTableName(form);  	//获取子表table name
					if(!"".equals(form))
						formatHtml = fds.getFormDataHTML(request,form,uuid) ;
					
					TaskImpl currentTask = (TaskImpl)jbpmTemplate.getTaskService().getTask(taskId);  
					
			        //获取当前任务的活动节点  
			        ActivityImpl currentActivity = processDefinition.findActivity(currentTask.getActivityName()); 
			        
			        //获取下级节点
					nextTrans=(List<Transition>)currentActivity.getOutgoingTransitions(); 
					
					curNodeName = currentTask.getActivityName() ;
				}  
				
				String notSelectUserNodes = StringUtil.showNull(pd.getNotSelectUserNodes()) ;
				
				mav.addObject("notSelectUserNodes",notSelectUserNodes) ;
				List<Transition> nextTransDesc=new ArrayList<Transition>();
				for(int i=nextTrans.size()-1;i>=0;i--){
					nextTransDesc.add(nextTrans.get(i));
					
				}
				nextTrans=nextTransDesc;
				mav.addObject("nextTrans",nextTrans) ;
				if(nextTrans!= null) mav.addObject("transSize",nextTrans.size()) ;
				
				//取出只读，可写等信息
				List<ProcessField> fieldList = ps.getProcessFieldList(form, pKey,curNodeName) ;
				String fieldJson = JSONArray.fromObject(fieldList).toString() ;
				mav.addObject("fieldJson",fieldJson) ;
				mav.addObject("processFieldList",fieldList) ;
				mav.addObject("curNodeName",curNodeName) ;
				
			}
			
			
			
			//构造流程审核轨迹的datagrid
			String sql = " select nodename,b.name,dealtime,value from  "
					   + " mt_jbpm_processform a "
					   + " left join k_user b on a.dealuserid = b.id " 
					   + " where a.pid = '" + pId + "'" ; 
				
			DataGridProperty pp = new DataGridProperty();
			pp.setCustomerId("");
			pp.setTableID("processTransferList");
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("20%,10%,10%,60%") ;
			
			pp.addColumn("节点名称", "nodename");
			pp.addColumn("处理人", "name");
			pp.addColumn("处理时间", "dealtime");
			pp.addColumn("处理意见", "value");
			
			pp.setCancelPage(true) ;
			pp.setCancelBBar(true) ;
			
			pp.setSQL(sql);
			pp.setOrderBy_CH("dealtime");
			pp.setDirection("desc");
			request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(), pp);
			
			
			mav.addObject("formatHtml",formatHtml) ;
			mav.addObject("pId",pId) ;
			mav.addObject("pdId",pd.getPdId()) ;
			mav.addObject("pKey",pKey) ;
			mav.addObject("taskId",taskId) ;
			mav.addObject("formEntityId",uuid) ;
			mav.addObject("formId",form) ;
			mav.addObject("view",view) ;
			mav.addObject("subTableName",subTableName) ;
			
			
		}catch(Exception e) {
			e.printStackTrace() ;
			throw e ;
		}finally {
			DbUtil.close(conn) ;
		}	
		return mav ;
	}
	
	
	/**
	 * 流程通用办结方法
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView dealTask(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		Connection conn = null ;
		
		String processName = "" ;
		WebUtil webUtil=new WebUtil(request, response);
		try {
			
			conn = new DBConnect().getConnect() ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			String formEntityId = StringUtil.showNull(request.getParameter("formEntityId")) ;//直接申请或修改时会把这个参数传进来
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String nextNodeName = StringUtil.showNull(request.getParameter("nextNodeName")) ;
			String nextTrans = StringUtil.showNull(request.getParameter("nextTrans")) ;
			String mt_process_advice = StringUtil.showNull(request.getParameter("mt_process_advice")) ;
			String mt_process_nextUser = StringUtil.showNull(request.getParameter("mt_process_nextUser")) ;
			String apply = StringUtil.showNull(request.getParameter("apply")) ;
			String mode=StringUtil.showNull(request.getParameter("mode"));
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
			
			FormDefineService fdfs = new FormDefineService(conn) ;
			ProcessService ps = new ProcessService(conn) ;
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			
			String uuid = "" ;//表单对应增删改实体表的主健
			if(!"".equals(apply)) {
				//新发起流程
				ProcessService prs = new ProcessService(conn) ;
				
				String pId = StringUtil.showNull(prs.getPIdByPKeyAndForeignId(pKey, formEntityId)) ;
				
				//保存表单
				if(!mode.contains("no_save")){
				uuid = fdfs.saveFormData(request,response,formId,formEntityId);
				}
				if(!"".equals(pId)) {
					//已经发起申请了，直接跳转
					taskId = jbpmTemplate.getActivityTask(pId).getId() ;
					response.sendRedirect(request.getContextPath()+"/process.do?method=processTransfer&pKey="
										+pKey+"&uuid="+formEntityId+"&taskId="+taskId+"&apply="+apply) ;
					return null ;
				}
				
				//首次发起申请 
				ProcessDeploy pd = ps.getProcessDeploy(pKey) ;
				processName = pd.getPname() ;
				
				Map<String,String> startMap = new HashMap<String, String>() ;
				startMap.put("applyUser", userId) ;
				startMap.put("uuid", StringUtil.showNull(uuid) ) ;
				startMap.put("mt_formid", StringUtil.showNull(formId) ) ;
				startMap.put("processName", StringUtil.showNull(pd.getPname()) ) ;
				startMap.put("pKey", StringUtil.showNull(pKey) ) ;
				
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
				pf.setValue(mt_process_advice) ;
				pf.setDealTime(StringUtil.getCurDateTime()) ;
				pf.setDealUserId(userId) ;
				pf.setNodeName("保存") ;
				pf.setFormId(formId) ;
				pf.setFormEntityId(uuid) ;
				ps.addProcessForm(pf) ;
				
				String processDes = StringUtil.showNull(pd.getProcessDes()) ;
				String[] varibles = StringUtil.getVaribles(processDes) ;
				if (varibles!=null) {
					for(int t=0;t<varibles.length;t++){
						String varibleValue = StringUtil.showNull(request.getParameter(varibles[t])) ;
						processDes = processDes.replaceAll("\\$\\{" + varibles[t] + "\\}", varibleValue) ;
					}
				}
				
				//保存申请记录
				ProcessApply pa = new ProcessApply() ;
				pa.setId(StringUtil.getUUID()) ;
				pa.setPkey(pKey) ;
				pa.setPid(pi.getId()) ;
				pa.setForeignId(uuid) ;
				pa.setPname(pd.getPname()) ;
				pa.setApplyTime(StringUtil.getCurDateTime()) ;
				pa.setApplyUserId(userId) ;
				pa.setProcessDes(processDes) ;
				ps.addProcessApply(pa) ;
				
				
				//取下级taskid
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();  				
				Task myTask = taskList2.get(0) ;
				//jbpmTemplate.completeTask(myTask.getId(),nextNodeName) ;
				//jbpmTemplate.getTaskService().takeTask(myTask.getId(),mt_process_nextUser) ;
				//jbpmTemplate.getTaskService().addTaskParticipatingUser(taskId,mt_process_nextUser,Participation.CANDIDATE);
				if(mode.contains("no_save")){
					response.sendRedirect(request.getContextPath()+"/process.do?method=processTransfer&pKey="
							+pKey+"&uuid="+formEntityId+"&taskId="+myTask.getId()+"&apply="+apply) ;	
				}else{
				response.sendRedirect(request.getContextPath()+"/process.do?method=processTransfer&taskId="+myTask.getId()+"&pKey="+pKey+"&apply="+apply+"&uuid="+uuid) ;
				}
				
				
			}else {
				
				
				String pid = jbpmTemplate.getProcessInstanceId(taskId) ;
				ProcessInstance processInstance = JbpmServicce.getJbpmTemplate().getExecutionService().findProcessInstanceById(pid);
				String activityName = jbpmTemplate.getActivityName(taskId) ;
				
				//保存表单
				Map<String,String> variableMap = new HashMap<String, String>() ;
				if(!"".equals(formId)) {
					request.setAttribute("curNodeName", activityName);
					uuid = fdfs.saveFormData(request,response,formId, formEntityId);
					
					List<ProcessField> processFieldList = ps.getProcessFieldList(formId,pKey,activityName) ;
					
					for(ProcessField pf:processFieldList) {
						//检查是否需要放流程变量
						String isProcessVariable = pf.getIsProcessVariable() ;
						if("是".equals(isProcessVariable)) {
							String name = pf.getName() ;
							String variable = StringUtil.showNull(request.getParameter(name)) ;
							
							variableMap.put(name, variable) ;
						}
					}
					variableMap.put("uuid", StringUtil.showNull(request.getParameter("uuid")) ) ;
					variableMap.put("mt_formid", StringUtil.showNull(request.getParameter("mt_formid")) ) ;
					jbpmTemplate.setTaskVariables(taskId, variableMap) ;
				}
				
				//获取任取到达时间
				Task task = jbpmTemplate.getTaskService().getTask(taskId) ;
				String arriveTime = "" ;
				if(task != null) {
					arriveTime = new SimpleDateFormat("yyyy-MM-dd").format(task.getCreateTime()) ;
				}
				
				//完成流程
				Map<String,String> varibleMap = new HashMap<String,String>() ;
				varibleMap.put("curNodeName",activityName) ; //本级节点名称
				varibleMap.put("nextNodeName", nextNodeName.replaceAll("to ","")) ; //下级节点名称
				varibleMap.put("curDealUser", userId) ; //本节点处理人
				varibleMap.put("nextDealUser",mt_process_nextUser) ; //下级节点处理人
				jbpmTemplate.setTaskVariables(taskId, varibleMap) ;
				jbpmTemplate.completeTask(taskId,nextTrans) ;
				
				//完成任务
				if(processInstance.isActive("state")) {
					jbpmTemplate.getExecutionService().signalExecutionById(pid) ;
				}
				
				//保存流程轨迹
				ProcessForm pf = new ProcessForm() ;
				pf.setpId(pid) ;
				pf.setKey("意见") ;
				pf.setValue(mt_process_advice) ;
				pf.setDealTime(StringUtil.getCurDateTime()) ;
				pf.setArriveTime(arriveTime) ;
				pf.setDealUserId(userId) ;
				pf.setNodeName(activityName) ;
				pf.setFormId(formId) ;
				pf.setFormEntityId(uuid) ;
				ps.addProcessForm(pf) ;
				
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
				List<Task> taskList2 = tq.processInstanceId(pid).list();  
				if(taskList2.size() > 0) {
					Task myTask = taskList2.get(0) ;
					//jbpmTemplate.getTaskService().takeTask(myTask.getId(),mt_process_nextUser) ;
					jbpmTemplate.getTaskService().addTaskParticipatingUser(myTask.getId(),mt_process_nextUser,Participation.CANDIDATE);
					//List list = jbpmTemplate.getTaskService().findPersonalTasks(mt_process_nextUser) ;
				}
				
				String goApplyUrl = StringUtil.showNull(request.getParameter("goApplyUrl")) ;
				if("true".equals(goApplyUrl)){
					
					String url=webUtil.getPreUrl();
					if("#".equals(url)){
						response.sendRedirect(request.getContextPath()+"/formDefine.do?method=formListView&uuid="+formId) ;
					}else{
						response.sendRedirect(url);
					}
					
					//
				}else {
					response.sendRedirect(request.getContextPath()+"/process.do?method=auditList&pkey="+pKey) ;
				}
				
				
			}
			
		} catch (Exception e) {
			log.exception("流程【"+processName+"】的数据处理出现异常", e) ;
			e.printStackTrace();
			throw e ;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 取得下一节点处理人
	 * @param request
	 * @param response
	 * @return
	 */
	/*
	public ModelAndView getDealUser(HttpServletRequest request,
			HttpServletResponse response){
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter() ;
			conn = new DBConnect().getConnect() ;
			String pdId = StringUtil.showNull(request.getParameter("pdId")) ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			String nodeName = StringUtil.showNull(request.getParameter("nodeName")) ;
			String transName = StringUtil.showNull(request.getParameter("transName")) ;
			String curNodeName = StringUtil.showNull(request.getParameter("curNodeName")) ;
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			nodeName = nodeName.replaceAll("to ","") ;
			log.debug("nodeName:"+nodeName) ;
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			RepositoryService repositoryService = jbpmTemplate.getRepositoryService() ;
			
			String pId = jbpmTemplate.getProcessInstanceId(taskId) ;
			ProcessInstance processInstance = jbpmTemplate.getExecutionService().findProcessInstanceById(pId);
			pdId = processInstance.getProcessDefinitionId() ;
			
			ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
			
	    	ActivityImpl startActivity = processDefinition.findActivity(nodeName);
	    	
	    	log.debug("nodeType:"+startActivity.getType()) ;
	    	
	    	//把新的节点信息放到流程变量
	    	ProcessService ps = new ProcessService(conn) ;
	    	List<ProcessField> fieldList = ps.getProcessFieldList(formId, pKey, curNodeName) ;
	    	Map<String,String> varibleMap = new HashMap<String, String>() ;
	    	for(ProcessField processField:fieldList){
	    		if("是".equals(processField.getIsProcessVariable())){
	    			String fieldValue = StringUtil.showNull(request.getParameter(processField.getEnname())) ;
	    			varibleMap.put(processField.getEnname(),fieldValue) ;
	    		}
	    	}
	    	jbpmTemplate.setTaskVariables(taskId, varibleMap) ;
	    	
	    	List<Map<String,String>> userList = null ;
	    	if(!"task".equals(startActivity.getType())) {
	    		if("decision".equals(startActivity.getType())) {
	    			//分支节点
	    			
	    			DecisionHandlerActivity dha = (DecisionHandlerActivity)startActivity.getActivityBehaviour() ;
	    			Field field = dha.getClass().getDeclaredField("decisionHandlerReference") ;
	    			field.setAccessible(true); 
	    			UserCodeReference ucf = (UserCodeReference)field.get(dha) ;
	    			
	    			
	    			ObjectDescriptor descriptor = (ObjectDescriptor)ucf.getDescriptor() ;
		    		String className = descriptor.getClassName() ;
		    		Class classz = Class.forName(className) ;
		    		
		    		String applyUser = (String)jbpmTemplate.getVariable(taskId,"applyUser") ;
		    		
		    		Object obj = classz.newInstance() ;
		    		Method setApplyUserId = classz.getMethod("setApplyUserId", new Class[]{String.class}) ;  
		    		setApplyUserId.invoke(obj,new Object[] {applyUser}) ;  
		    		
		    		Method setCurActivity = classz.getMethod("setCurActivity", new Class[]{ActivityImpl.class}) ;  
		    		setCurActivity.invoke(obj,new Object[] {startActivity}) ;  
		    		
		    		String eId = jbpmTemplate.getTaskService().getTask(taskId).getExecutionId() ;
	    			ExecutionImpl execution = (ExecutionImpl)jbpmTemplate.getExecutionService().findExecutionById(eId) ;
		    		
		    		Method decide = classz.getMethod("decide", new Class[]{OpenExecution.class}) ;  
		    		String transitionName = (String)decide.invoke(obj,new Object[] {execution}) ;

		    	    Transition transition = startActivity.getOutgoingTransition(transitionName);
		    		
		    	    ActivityImpl activityImpl = (ActivityImpl)transition.getDestination() ;
		    	    
		    	    if("task".equals(activityImpl.getType())) {
		    	    	TaskActivity taskActivity = (TaskActivity)activityImpl.getActivityBehaviour(); 
		    	    	userList = getUserListByActivity(taskActivity,taskId,transName) ;
		    	    }
	    			
	    		    //Transition transition = startActivity.getOutgoingTransition(transitionName);
	    		    //Activity activity = transition.getDestination() ;
	    		}
	    	}else {
	    		TaskActivity taskActivity = (TaskActivity)startActivity.getActivityBehaviour(); 
		    	userList = getUserListByActivity(taskActivity,taskId,transName) ;
		    	
		    	if(userList.size() == 0) {
		    		out.write("fail") ;
		    		return null ;
		    	}
	    	}
	    	
			String json = JSONArray.fromObject(userList).toString() ;
			log.debug("办理人json：" + json) ;
			out.write(json) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	*/
	
	/*
	public ModelAndView getDealUser(HttpServletRequest request,
			HttpServletResponse response){
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter() ;
			conn = new DBConnect().getConnect() ;
			String pdId = StringUtil.showNull(request.getParameter("pdId")) ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			String nodeName = StringUtil.showNull(request.getParameter("nodeName")) ;
			String curNodeName = StringUtil.showNull(request.getParameter("curNodeName")) ;
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			nodeName = nodeName.replaceAll("to ","") ;
			log.debug("nodeName:"+nodeName) ;
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			RepositoryService repositoryService = jbpmTemplate.getRepositoryService() ;
			ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
			
	    	ActivityImpl startActivity = processDefinition.findActivity(nodeName);
	    	
	    	log.debug("nodeType:"+startActivity.getType()) ;
	    	
	    	//把新的节点信息放到流程变量
	    	ProcessService ps = new ProcessService(conn) ;
	    	List<ProcessField> fieldList = ps.getProcessFieldList(formId, pKey, curNodeName) ;
	    	Map<String,String> varibleMap = new HashMap<String, String>() ;
	    	for(ProcessField processField:fieldList){
	    		if("是".equals(processField.getIsProcessVariable())){
	    			String fieldValue = StringUtil.showNull(request.getParameter(processField.getEnname())) ;
	    			varibleMap.put(processField.getEnname(),fieldValue) ;
	    		}
	    	}
	    	jbpmTemplate.setTaskVariables(taskId, varibleMap) ;
	    	
	    	List<Map<String,String>> userList = null ;
	    	if(!"task".equals(startActivity.getType())) {
	    		if("decision".equals(startActivity.getType())) {
	    			//分支节点
	    			
	    			DecisionHandlerActivity dha = (DecisionHandlerActivity)startActivity.getActivityBehaviour() ;
	    			Field field = dha.getClass().getDeclaredField("decisionHandlerReference") ;
	    			field.setAccessible(true); 
	    			UserCodeReference ucf = (UserCodeReference)field.get(dha) ;
	    			
	    			
	    			ObjectDescriptor descriptor = (ObjectDescriptor)ucf.getDescriptor() ;
		    		String className = descriptor.getClassName() ;
		    		Class classz = Class.forName(className) ;
		    		
		    		String applyUser = (String)jbpmTemplate.getVariable(taskId,"applyUser") ;
		    		
		    		Object obj = classz.newInstance() ;
		    		Method setApplyUserId = classz.getMethod("setApplyUserId", new Class[]{String.class}) ;  
		    		setApplyUserId.invoke(obj,new Object[] {applyUser}) ;  
		    		
		    		Method setCurActivity = classz.getMethod("setCurActivity", new Class[]{ActivityImpl.class}) ;  
		    		setCurActivity.invoke(obj,new Object[] {startActivity}) ;  
		    		
		    		String eId = jbpmTemplate.getTaskService().getTask(taskId).getExecutionId() ;
	    			ExecutionImpl execution = (ExecutionImpl)jbpmTemplate.getExecutionService().findExecutionById(eId) ;
	    			
	    			Map<String, Object> variables = jbpmTemplate.getExecutionService().getVariables(   
	    					eId, jbpmTemplate.getExecutionService().getVariableNames(eId));
	    			
	    			Method setVariableMap = classz.getMethod("setVariableMap", new Class[]{Map.class}) ;  
	    			setVariableMap.invoke(obj,new Object[] {variables}) ; 
		    		
		    		Method decide = classz.getMethod("decide", new Class[]{OpenExecution.class}) ;  
		    		String transitionName = (String)decide.invoke(obj,new Object[] {execution}) ;

		    	    Transition transition = startActivity.getOutgoingTransition(transitionName);
		    		
		    	    ActivityImpl activityImpl = (ActivityImpl)transition.getDestination() ;
		    	    
		    	    if("task".equals(activityImpl.getType())) {
		    	    	TaskActivity taskActivity = (TaskActivity)activityImpl.getActivityBehaviour(); 
		    	    	userList = getUserListByActivity(taskActivity,taskId) ;
		    	    }
	    			
	    		    //Transition transition = startActivity.getOutgoingTransition(transitionName);
	    		    //Activity activity = transition.getDestination() ;
	    		}
	    	}else {
	    		TaskActivity taskActivity = (TaskActivity)startActivity.getActivityBehaviour(); 
		    	userList = getUserListByActivity(taskActivity,taskId) ;
		    	
		    	if(userList.size() == 0) {
		    		out.write("fail") ;
		    		return null ;
		    	}
	    	}
	    	
			String json = JSONArray.fromObject(userList).toString() ;
			log.debug("办理人json：" + json) ;
			out.write(json) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	*/
	
	/**
	 * 取得下一节点处理人
	 * @param request
	 * @param response
	 * @return
	 */
	
	public ModelAndView getDealUser(HttpServletRequest request,
			HttpServletResponse response){
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter() ;
			conn = new DBConnect().getConnect() ;
			String pdId = StringUtil.showNull(request.getParameter("pdId")) ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			String nodeName = StringUtil.showNull(request.getParameter("nodeName")) ;
			String transName = StringUtil.showNull(request.getParameter("transName")) ;
			String curNodeName = StringUtil.showNull(request.getParameter("curNodeName")) ;
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			nodeName = nodeName.replaceAll("to ","") ;
			log.debug("nodeName:"+nodeName) ;
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			RepositoryService repositoryService = jbpmTemplate.getRepositoryService() ;
			
			String pId = jbpmTemplate.getProcessInstanceId(taskId) ;
			ProcessInstance processInstance = jbpmTemplate.getExecutionService().findProcessInstanceById(pId);
			pdId = processInstance.getProcessDefinitionId() ;
			
			ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
			
	    	ActivityImpl startActivity = processDefinition.findActivity(nodeName);
	    	
	    	log.debug("nodeType:"+startActivity.getType()) ;
	    	
	    	//把新的节点信息放到流程变量
	    	ProcessService ps = new ProcessService(conn) ;
	    	List<ProcessField> fieldList = ps.getProcessFieldList(formId, pKey, curNodeName) ;
	    	Map<String,String> varibleMap = new HashMap<String, String>() ;
	    	for(ProcessField processField:fieldList){
	    		if("是".equals(processField.getIsProcessVariable())){
	    			String fieldValue = StringUtil.showNull(request.getParameter(processField.getEnname())) ;
	    			varibleMap.put(processField.getEnname(),fieldValue) ;
	    		}
	    	}
	    	jbpmTemplate.setTaskVariables(taskId, varibleMap) ;
	    	
	    	
	    	List<Map<String,String>> userList = null ;
	    	if(!"task".equals(startActivity.getType())) {
	    		if("decision".equals(startActivity.getType())) {
	    			//分支节点
	    			
	    			DecisionHandlerActivity dha = (DecisionHandlerActivity)startActivity.getActivityBehaviour() ;
	    			Field field = dha.getClass().getDeclaredField("decisionHandlerReference") ;
	    			field.setAccessible(true); 
	    			UserCodeReference ucf = (UserCodeReference)field.get(dha) ;
	    			
	    			
	    			ObjectDescriptor descriptor = (ObjectDescriptor)ucf.getDescriptor() ;
		    		String className = descriptor.getClassName() ;
		    		Class classz = Class.forName(className) ;
		    		
		    		String applyUser = (String)jbpmTemplate.getVariable(taskId,"applyUser") ;
		    		
		    		Object obj = classz.newInstance() ;
		    		Method setApplyUserId = classz.getMethod("setApplyUserId", new Class[]{String.class}) ;  
		    		setApplyUserId.invoke(obj,new Object[] {applyUser}) ;  
		    		
		    		Method setCurActivity = classz.getMethod("setCurActivity", new Class[]{ActivityImpl.class}) ;  
		    		setCurActivity.invoke(obj,new Object[] {startActivity}) ;  
		    		
		    		String eId = jbpmTemplate.getTaskService().getTask(taskId).getExecutionId() ;
		    		//jbpmTemplate.getExecutionService().
	    			ExecutionImpl execution = (ExecutionImpl)jbpmTemplate.getExecutionService().findExecutionById(eId) ;
	    			
	    			Map<String, Object> variables = jbpmTemplate.getExecutionService().getVariables(   
	    					eId, jbpmTemplate.getExecutionService().getVariableNames(eId));
	    			
	    			Method setVariableMap = classz.getMethod("setVariableMap", new Class[]{Map.class}) ;  
	    			setVariableMap.invoke(obj,new Object[] {variables}) ; 
		    		
		    		Method decide = classz.getMethod("decide", new Class[]{OpenExecution.class}) ;  
		    		String transitionName = (String)decide.invoke(obj,new Object[] {execution}) ;

		    	    Transition transition = startActivity.getOutgoingTransition(transitionName);
		    		
		    	    ActivityImpl activityImpl = (ActivityImpl)transition.getDestination() ;
		    	    
		    	    if("task".equals(activityImpl.getType())) {
		    	    	TaskActivity taskActivity = (TaskActivity)activityImpl.getActivityBehaviour(); 
		    	    	userList = getUserListByActivity(taskActivity,taskId,transName) ;
		    	    }
	    			
	    		    //Transition transition = startActivity.getOutgoingTransition(transitionName);
	    		    //Activity activity = transition.getDestination() ;
	    		}
	    	}else {
	    		TaskActivity taskActivity = (TaskActivity)startActivity.getActivityBehaviour(); 
		    	userList = getUserListByActivity(taskActivity,taskId) ;
		    	
		    	if(userList.size() == 0) {
		    		out.write("fail") ;
		    		return null ;
		    	}
	    	}
	    	
			String json = JSONArray.fromObject(userList).toString() ;
			log.debug("办理人json：" + json) ;
			out.write(json) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	private List<Map<String,String>> getUserListByActivity(TaskActivity taskActivity,String taskId){
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect() ;
			
			Expression assigneeExp = taskActivity.getTaskDefinition().getAssigneeExpression() ;
	    	Expression candidateUsersExp = taskActivity.getTaskDefinition().getCandidateUsersExpression() ;
	    	UserCodeReference userCodeReference  = taskActivity.getTaskDefinition().getAssignmentHandlerReference() ;
	    	
	    	UserService us = new UserService(conn) ;
	    	JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
	    	List<Map<String,String>> userList = new ArrayList<Map<String,String>> ();
	    	if(assigneeExp != null) {
	    		//设定了待办用户，直接取待办用户就可以了
	    		String userId = StringUtil.showNull(assigneeExp.getExpressionString()) ;
	    		
	    		if(!"".equals(userId)) {
	    			Map<String,String> userMap = new HashMap<String,String>() ;
		    		userMap.put("userId",userId) ;
		    		User user = us.getUser(userId,"id") ;
		    		if(user != null) {
		    			userMap.put("userName",user.getName()) ;
		    		}else {
		    			userMap.put("userName",userId) ;
		    		}
		    		
		    		userList.add(userMap) ;
	    		}
	    		
	    	}else if(candidateUsersExp != null) {
	    		//设定了后选人,
	    		if(!"".equals(taskId)) {
	    			//这个只有把taskId传进来时才能取到
	    			String exp = StringUtil.showNull(candidateUsersExp.getExpressionString()) ;
	    			exp = exp.trim() ;
	    			exp = exp.replaceAll("\\$","") ;
	    			exp = exp.replaceAll("\\{","") ;
	    			exp = exp.replaceAll("\\}","") ;
	    								
	    			String[] expArr = exp.split(",") ;
	    			Map<String,String> userMap = new HashMap<String,String>() ;
	    			for(int z=0;z<expArr.length;z++) {
	    				String userId = (String)jbpmTemplate.getVariable(taskId,expArr[z]) ;
	    				User user = us.getUser(userId,"id") ;
	    				userMap.put("userId", userId) ;
	    				if(user != null) {
	    					userMap.put("userName",user.getName()) ;
	    				}else {
	    					userMap.put("userName",userId) ;
	    				}
	    				userList.add(userMap) ;
	    			}
	    		}
	    	}else if(userCodeReference != null) {
	    		// 设定了自定义类，要执行接口方法
	    		ObjectDescriptor descriptor = (ObjectDescriptor)userCodeReference.getDescriptor() ;
	    		String className = descriptor.getClassName() ;
	    		
	    		//获得jbpm下级节点参assignmentHandler参数
	    		List Operations = descriptor.getOperations() ;
	    		String role = "",user = "",department = "" ;
	    		for(int k=0;k<Operations.size();k++) {
	    			FieldOperation field = (FieldOperation)Operations.get(k) ;
	    			String fieldName = field.getFieldName() ;
	    			StringDescriptor sd = (StringDescriptor)field.getDescriptor() ;
	    			
	    			if("role".equals(fieldName)) {
	    				role = (String)sd.construct(null) ;
	    			}else if("user".equals(fieldName)) {
	    				user = (String)sd.construct(null) ;
	    			}else if("department".equals(fieldName)) {
	    				department = (String)sd.construct(null) ;
	    			}
	    		}
	    		String applyUser = (String)jbpmTemplate.getVariable(taskId,"applyUser") ;  //获取发起人
	    		Class classz = Class.forName(className) ;
	    		
	    		//调用接口的方法，把三个属性放进去
	    		Object obj = classz.newInstance() ;
	    		Method setUser = classz.getMethod("setUser", new Class[]{String.class}) ;  
	    		setUser.invoke(obj,new Object[] {user}) ;
	    		
	    		Method setDepartment = classz.getMethod("setDepartment", new Class[]{String.class}) ;  
	    		setDepartment.invoke(obj,new Object[] {department}) ;
	    		
	    		Method setRole = classz.getMethod("setRole", new Class[]{String.class}) ;  
	    		setRole.invoke(obj,new Object[] {role}) ;
	    		
	    		//调用getUser方法返回权限值
	    		Method assign = classz.getMethod("getUser", new Class[]{String.class}) ;  
	    		userList = (List<Map<String,String>>)assign.invoke(obj,new Object[] {applyUser}) ;
	    		
	    	}else {
	    		//都没设定的情况下,流程就不能往下走了
	    		
	    	}
			return userList ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null ;
		
	}
	
	private List<Map<String,String>> getUserListByActivity(TaskActivity taskActivity,String taskId,String transName){
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect() ;
			
			Expression assigneeExp = taskActivity.getTaskDefinition().getAssigneeExpression() ;
	    	Expression candidateUsersExp = taskActivity.getTaskDefinition().getCandidateUsersExpression() ;
	    	UserCodeReference userCodeReference  = taskActivity.getTaskDefinition().getAssignmentHandlerReference() ;
	    	
	    	UserService us = new UserService(conn) ;
	    	JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
	    	List<Map<String,String>> userList = new ArrayList<Map<String,String>> ();
	    	
	    	if(transName.indexOf("退回") > -1) {
	    		String pId = jbpmTemplate.getProcessInstanceId(taskId) ;
		    	String nodeName = taskActivity.getTaskDefinition().getName() ;
		    	ProcessService ps = new ProcessService(conn) ;
		    	userList = ps.getNodeUserList(pId,nodeName);
		    	if(userList.size() > 0) {
		    		//该节点已办理,取以前节点的办理人
		    		return userList ;
		    	}
	    	}
	    	
	    	
	    	if(assigneeExp != null) {
	    		//设定了待办用户，直接取待办用户就可以了
	    		String userId = StringUtil.showNull(assigneeExp.getExpressionString()) ;
	    		
	    		if(!"".equals(userId)) {
	    			Map<String,String> userMap = new HashMap<String,String>() ;
		    		userMap.put("userId",userId) ;
		    		User user = us.getUser(userId,"id") ;
		    		if(user != null) {
		    			userMap.put("userName",user.getName()) ;
		    		}else {
		    			userMap.put("userName",userId) ;
		    		}
		    		
		    		userList.add(userMap) ;
	    		}
	    		
	    	}else if(candidateUsersExp != null) {
	    		//设定了后选人,
	    		if(!"".equals(taskId)) {
	    			//这个只有把taskId传进来时才能取到
	    			String exp = StringUtil.showNull(candidateUsersExp.getExpressionString()) ;
	    			exp = exp.trim() ;
	    			exp = exp.replaceAll("\\$","") ;
	    			exp = exp.replaceAll("\\{","") ;
	    			exp = exp.replaceAll("\\}","") ;
	    								
	    			String[] expArr = exp.split(",") ;
	    			Map<String,String> userMap = new HashMap<String,String>() ;
	    			for(int z=0;z<expArr.length;z++) {
	    				String userId = (String)jbpmTemplate.getVariable(taskId,expArr[z]) ;
	    				User user = us.getUser(userId,"id") ;
	    				userMap.put("userId", userId) ;
	    				if(user != null) {
	    					userMap.put("userName",user.getName()) ;
	    				}else {
	    					userMap.put("userName",userId) ;
	    				}
	    				userList.add(userMap) ;
	    			}
	    		}
	    	}else if(userCodeReference != null) {
	    		// 设定了自定义类，要执行接口方法
	    		ObjectDescriptor descriptor = (ObjectDescriptor)userCodeReference.getDescriptor() ;
	    		String className = descriptor.getClassName() ;
	    		
	    		//获得jbpm下级节点参assignmentHandler参数
	    		List Operations = descriptor.getOperations() ;
	    		String role = "",user = "",department = "" ;
	    		for(int k=0;k<Operations.size();k++) {
	    			FieldOperation field = (FieldOperation)Operations.get(k) ;
	    			String fieldName = field.getFieldName() ;
	    			StringDescriptor sd = (StringDescriptor)field.getDescriptor() ;
	    			
	    			if("role".equals(fieldName)) {
	    				role = (String)sd.construct(null) ;
	    			}else if("user".equals(fieldName)) {
	    				user = (String)sd.construct(null) ;
	    			}else if("department".equals(fieldName)) {
	    				department = (String)sd.construct(null) ;
	    			}
	    		}
	    		String applyUser = (String)jbpmTemplate.getVariable(taskId,"applyUser") ;  //获取发起人
	    		Class classz = Class.forName(className) ;
	    		
	    		//调用接口的方法，把三个属性放进去
	    		Object obj = classz.newInstance() ;
	    		Method setUser = classz.getMethod("setUser", new Class[]{String.class}) ;  
	    		setUser.invoke(obj,new Object[] {user}) ;
	    		
	    		Method setDepartment = classz.getMethod("setDepartment", new Class[]{String.class}) ;  
	    		setDepartment.invoke(obj,new Object[] {department}) ;
	    		
	    		Method setRole = classz.getMethod("setRole", new Class[]{String.class}) ;  
	    		setRole.invoke(obj,new Object[] {role}) ;
	    		
	    		//调用getUser方法返回权限值
	    		Method assign = classz.getMethod("getUser", new Class[]{String.class}) ;  
	    		userList = (List<Map<String,String>>)assign.invoke(obj,new Object[] {applyUser}) ;
	    		
	    	}else {
	    		//都没设定的情况下,流程就不能往下走了
	    	}
			return userList ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null ;
		
	}
	
	
	/**
	 * 流程信息后台处理方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView taskManagerList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView(TASKMANAGERLIST) ;
		
		try {
			// 待审核任务列表
			String sql = ProcessService.getDealtSql("","") ;
			
			DataGridProperty pp = new DataGridProperty();
			pp.setCustomerId("");
			pp.setTableID("taskManagerList");
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			pp.setPrintTitle("流程信息列表");
			pp.setColumnWidth("20%,15%,15%,12%,13%") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" pkey=\"${pkey}\" pid=\"${pid}\" taskId=\"${taskId}\" uuid=\"${uuid}\" pName=\"${pName}\"");
			
			pp.addColumn("流程实例Id", "pid");
			pp.addColumn("流程名称", "pName");
			pp.addColumn("申请人", "userName");
			pp.addColumn("申请时间", "applytime");
			pp.addColumn("当前节点", "activeName");
			pp.addColumn("当前处理人", "dealUserName");
			pp.addColumn("送达时间", "create_");
			
			
			pp.setSQL(sql);  
			pp.setOrderBy_CH("applytime,create_");
			pp.setDirection("desc,desc");
			request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(), pp);
			
		 } catch (Exception e) {
			e.printStackTrace();
		} 
		 return modelAndView;
	}
	
	
	/**
	 * 
	 * 更新当前流程处理人
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView assignUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect() ; 
			out = response.getWriter() ;
			
			String assignUser = StringUtil.showNull(request.getParameter("assignUser")) ;
			String taskId = StringUtil.showNull(request.getParameter("taskId")) ;
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
			
			String pId = jbpmTemplate.getProcessInstanceId(taskId) ;
			UserService us = new UserService(conn) ;
			if(!"".equals(assignUser)) {
				String assignUserId = assignUser ;
				List<Participation> participations = jbpmTemplate.getTaskService().getTaskParticipations(taskId) ;
				//选清除原来的,再把新的人员加进去
				for(Participation p:participations) {
					String userId = p.getUserId() ;
					jbpmTemplate.getTaskService().removeTaskParticipatingUser(taskId, userId,Participation.CANDIDATE) ;
				}
				jbpmTemplate.getTaskService().addTaskParticipatingUser(taskId,assignUserId,Participation.CANDIDATE);
				log.log("流程实例【"+pId+"】办理人更改为:【 " + assignUserId + "】!") ;
			}
			
			out.write("ok") ;
			
		}catch(Exception e) {
			out.write("fail") ;
			log.exception("后台更改流程当前办理人失败", e) ;
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn) ;
		}	
		return null ;
	}
	
	
	/**
	 * 
	 * 检查流程状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		PrintWriter out = null ;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect() ; 
			out = response.getWriter() ;
			
			String uuid = StringUtil.showNull(request.getParameter("uuid")) ;
			String pkey = StringUtil.showNull(request.getParameter("pkey")) ;
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
			ProcessService ps = new ProcessService(conn) ;
			String pId = StringUtil.showNull(ps.getPIdByPKeyAndForeignId(pkey, uuid)) ;
			
			if("".equals(pId)) {
				out.write("ok") ;
				return null ;
			}
			
			TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
			List<Task> taskList = tq.processInstanceId(pId).list();  				
			
			String taskId = "" ;
			if(taskList.size() == 0) {
				out.write("fail") ;
				return null ;
			}else {
				Task myTask = taskList.get(0) ;
				taskId = myTask.getId();
			}
			
			if("".equals(taskId)) {
				out.write("fail") ;
				return null ;
			}
			
			ProcessInstance processInstance = jbpmTemplate.getExecutionService().findProcessInstanceById(pId);
			String pdId = processInstance.getProcessDefinitionId() ;
			
			//发起申请，读取流程的表单配置
			ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)jbpmTemplate.getRepositoryService()
										.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
			List activities =  processDefinition.getActivities();  
			
			//找始节点
			ActivityImpl startActivity = null ;
			for(int i=0;i<activities.size();i++){  
	            ActivityImpl activityImpl = (ActivityImpl)activities.get(i);  
	            String type = activityImpl.getType();  
	            
	            if("start".equals(type)) {
	            	startActivity = activityImpl ;
	            }
		    }
			
			if(startActivity == null) {
				out.write("ok") ;
				return null ;
			}
			
			String startName = "" ;
			List<Transition> trans = (List<Transition>) startActivity
					.getOutgoingTransitions();

			if (trans != null) {
				Transition startNode = trans.get(0); // start后的第一个节点是发起申请的节点
				startName = startNode.getDestination().getName();
			}
			
			String activityName = StringUtil.showNull(jbpmTemplate.getActivityName(taskId)) ;
			
			if(activityName.equals(startName)) {
				out.write("ok") ;
			}else {
				out.write("fail") ;
			}
			
			return null ; 
			
		}catch(Exception e) {
			out.write("fail") ;
			log.exception("流程检查状态出错", e) ;
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn) ;
		}	
		return null ;
	}
	
	
	/**
	 * 批量发布流程
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void batchDeploy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		
		PreparedStatement psmt = null ;
		ResultSet rs = null ;
		PrintWriter out = null ;
		try {
			out = response.getWriter() ;

			conn = new DBConnect().getConnect();

		
			//查看时，如果没传pId就查出来
			String sql = " select jbpmxml,pkey,pname from mt_jbpm_processdeploy " ;
			
			psmt = conn.prepareStatement(sql) ;
			rs = psmt.executeQuery() ;
			String errorMsg = "" ;
			while(rs.next()) {
				String jbpmxml = StringUtil.showNull(rs.getString(1)) ;
				String pkey = StringUtil.showNull(rs.getString(2)) ;
				String pname = StringUtil.showNull(rs.getString(3)) ;
				
				try {
					if(!"".equals(jbpmxml)) {
						String tempPath = JPDL_PATH + StringUtil.getUUID() + "/";
						String zipPath = JPDL_PATH + pkey + ".zip";
			
						File tempFilePath = new File(tempPath);
			
						if (!tempFilePath.exists()) {
							tempFilePath.mkdirs();
						}
						// 生成xml文件		
						File file = new File(tempPath + pkey + ".jpdl.xml");
						OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
						osw.write(jbpmxml);
						osw.flush();
						osw.close();
			
						FileInputStream fis = new FileInputStream(file);
			
						// 生成图片文件
						JpdlProperty jbdlProperty = new JpdlProperty(fis);
						ImageIO.write(new JpdlDrawer().draw(jbdlProperty), "png", new File(
								tempPath + pkey + ".png"));
			
						// 压缩文件
						ZipUtil zipUtil = new ZipUtil();
						File newFile = new File(JPDL_PATH + pkey + ".zip");
			
						if (newFile.exists()) {
							newFile.delete();
						}
			
						zipUtil.zip(tempPath, zipPath);
			
						// 删除临时文件夹的文件
						if (tempFilePath.exists()) {
							
							ManuFileService.deleteFile(new File(tempPath)) ;
						}
						
						JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			
						String deploymentId = jbpmTemplate.deployByZip(zipPath);
						
						String pdId = jbpmTemplate.getProcessDefinition(deploymentId).getId();
						
						DbUtil dbUtil = new DbUtil(conn) ;
						dbUtil.update("mt_jbpm_processdeploy","pkey", pkey, "pdid", pdId) ;
						log.log("流程【"+pname+"】发布成功!") ;
					}
				}catch(Exception e) {
					errorMsg += "流程【"+pname+"】发布失败,原因:" + e.getMessage() + " \n" ;
					log.exception("流程【"+pname+"】发布失败,原因:" + e.getMessage(), e) ;
					e.printStackTrace() ;
				}
			}
			
			if(!"".equals(errorMsg)) {
				out.write(errorMsg) ;
			}else {
				out.write("ok") ;
			}
			out.close() ;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	
	/**
	 * 删除流程实例
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void delInstanse(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Connection conn = null;
		PrintWriter out = null; 
		response.setContentType("text/html;charset=utf-8");
		try {
			out = response.getWriter() ;
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false) ;

			String pkey = StringUtil.showNull(request.getParameter("pkey"));
			String pId = StringUtil.showNull(request.getParameter("pId"));
			String uuid = StringUtil.showNull(request.getParameter("uuid"));

			ProcessService processService = new ProcessService(conn);
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
			
			//删除申请表
			processService.deleteApply(pkey, uuid) ;
			
			//删除流程表单表
			processService.deleteProcessform(pId) ;
			
			//删除流程实例 
			jbpmTemplate.delProcessInstance(pId) ;
			
			conn.commit();
			out.write("ok") ;
			
		} catch (Exception e) {
			conn.rollback() ;
			out.write("fail") ;
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
	
	
	/**
	 * 根据uuid删除form表单的记录和流程记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delFormData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String uuid = request.getParameter("uuid");
		String formId = request.getParameter("formId");
		String pkey = request.getParameter("pkey");
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false); // 关闭自动提交
			
			FormDefineService service = new FormDefineService(conn);
			service.removeFormData(formId, uuid); //删除表单数据
			
			ProcessService processService = new ProcessService(conn) ;
			String pId = StringUtil.showNull(processService.getPIdByPKeyAndForeignId(pkey,uuid)) ;
			
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
			
			//删除申请表
			processService.deleteApply(pkey, uuid) ;
			
			//删除流程表单表
			processService.deleteProcessform(pId) ;
			
			conn.commit(); // 提交事务
			
			try {
				//删除流程实例 
				jbpmTemplate.delProcessInstance(pId) ;
			}catch(Exception e){
				log.debug("流程实例删除失败");
			}
		} catch (Exception e) {
			DbUtil.rollback(conn);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect(request.getContextPath() + "/formDefine.do?method=formListView&uuid="+formId);
		return null;
	}
	
	
	
	/**
	 * 结束流程
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView end(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		try{
			String pId = StringUtil.showNull(request.getParameter("pId"));
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			//取消流程
			jbpmTemplate.getExecutionService().endProcessInstance(pId,"cancel") ;
			
			String goApplyUrl = StringUtil.showNull(request.getParameter("goApplyUrl")) ;
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String pKey = StringUtil.showNull(request.getParameter("pKey")) ;
			if("true".equals(goApplyUrl)){
				WebUtil webUtil=new WebUtil(request, response);
				String url=webUtil.getPreUrl();
				if("#".equals(url)){
					response.sendRedirect(request.getContextPath()+"/formDefine.do?method=formListView&uuid="+formId) ;
				}else{
					response.sendRedirect(url);
				}
				
			}else {
				response.sendRedirect(request.getContextPath()+"/process.do?method=auditList&pkey="+pKey) ;
			}
		}catch(Exception ex){
			ex.printStackTrace() ;
			log.exception("流程拒绝失败",ex) ;
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView doCancel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			dbUtil.executeUpdate("call proc_cancel_process('"+uuid+"')");
		    re="流程取消成功";
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doDeleteFormData(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String formid=request.getParameter("formid");
		String uuid=request.getParameter("uuid");
		FormVO formVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			formVO=dbUtil.load(FormVO.class, formid);
			String sql=MessageFormat.format("delete from {0} where uuid=?",formVO.getTABLENAME());
			eff+=dbUtil.executeUpdate(sql, new Object[]{uuid});
			if(eff<1){
				re="删除失败";
			}else{
				re="删除成功";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}

	public static String PATH_AUDIT_LIST(String pKey) {
		// TODO Auto-generated method stub
		return MessageFormat.format("process.do?method=auditList&pkey={1}", pKey);
	}
}
