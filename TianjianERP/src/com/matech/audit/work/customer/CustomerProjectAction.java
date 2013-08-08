package com.matech.audit.work.customer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerProjectService;
import com.matech.audit.service.customer.model.CustomerProject;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.project.BusinessProjectService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.BusinessProject;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 客户立项管理
 * @author Ymm
 *
 */
public class CustomerProjectAction extends MultiActionController {
	 
	 private final String list ="customerProject/list.jsp";
	 private final String andAndEdit ="customerProject/AddandEdit.jsp";
	
	 public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		 ModelAndView modelAndView = new ModelAndView(list);
		 DataGridProperty pp = new DataGridProperty();
	   
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
	    /* Connection conn = new DBConnect().getConnect("");
		
		String bcRoleString =new ASFuntion().showNull(new DbUtil(conn).queryForString("SELECT a.rid FROM k_userrole a LEFT JOIN k_role b ON a.`rid` = b.`id` WHERE a.userid='"+userSession.getUserId()+"' AND b.`rolename` = '市场部负责人'"));
		if(!"".equals(bcRoleString)){
			bcRoleString = " OR 1=1 ";
		}
		DbUtil.close(conn);*/
		 
		 String sql= "SELECT a.autoId,a.`customerId`,a.`customerName`,a.`customerRank`,a.`customerSource`,a.`businessType`," +
			 		 "	a.`properties`,a.`projectType`,a.`contractMoney`,a.`workingHours`,a.`admissionTime`," +
			 		 "  b.name as distributeUser,c.name as followUser,c.name as auditUser,a.`state`,a.`createUser`," +
			 		 "	a.`createDate`,a.`createDepartment`  " +
			 		 "	FROM `asdb`.`k_customerproject` a "  +
			 		 " left join k_user b on a.distributeUser = b.id "+
			 		"  left join k_user c on a.followUser = c.id "+
			 		"  left join k_user d on a.auditUser = d.id "+
			 		 "  where 1=1 " +
			 		 " and (a.auditUser="+userSession.getUserId()+" or a.createUser = '"+userSession.getUserId() +"' or a.distributeUser="+userSession.getUserId()+" or a.followUser = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' ) " +
			 		 " ${customerName} ${customerRank} ${customerSource} ${businessType} ${properties} ${projectType} ${distributeUser} ${followUser}";
		 	
		    pp.setTableID("customerProjectList");
			pp.setPageSize_CH(20);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("autoId");
			pp.setDirection("asc");
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    
		    pp.setSQL(sql);
		    pp.addColumn("客户名称", "customerName");
		    pp.addColumn("客户级别", "customerRank");
		    pp.addColumn("客户来源", "customerSource");
		    pp.addColumn("行业类型", "businessType");
		    pp.addColumn("公司性质", "properties");
		    pp.addColumn("项目类型", "projectType");
		    pp.addColumn("合同金额", "contractMoney");
		    pp.addColumn("预算工时", "workingHours");
		    pp.addColumn("入场时间", "admissionTime");
		    pp.addColumn("分配人", "distributeUser");
		    pp.addColumn("跟进人", "followUser");
		    pp.addColumn("审核人", "auditUser");
		    pp.addColumn("状态", "state");
		    
		    pp.addSqlWhere("customerName", " and a.customerName like '%${customerName}%'");
		    pp.addSqlWhere("customerRank", " and a.customerRank like '%${customerRank}%'");
		    pp.addSqlWhere("customerSource", " and a.customerSource = '${customerSource}'");
		    pp.addSqlWhere("businessType", " and a.businessType = '${businessType}'");
		    pp.addSqlWhere("properties"," and a.properties = '${properties}'");
		    pp.addSqlWhere("projectType", " and a.projectType = '${projectType}'");
		    pp.addSqlWhere("distributeUser", " and a.distributeUser = '${distributeUser}'");
		    pp.addSqlWhere("followUser", " and a.followUser = ${followUser}");
		    
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		    return modelAndView;
	 }
	 
	 public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 ModelAndView modelAndView = new ModelAndView(andAndEdit);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 String audit = asf.showNull(request.getParameter("audit"));
		 String opt = asf.showNull(request.getParameter("opt"));
		 
		 modelAndView.addObject("opt",opt);
		 modelAndView.addObject("audit", audit);
		 Connection conn = null;
		 CustomerProject cp = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerProjectService sps = new CustomerProjectService(conn);
			 if(!autoId.equals("")){
				 cp = sps.getCustomerProject(autoId);
			 }
			 modelAndView.addObject("cp", cp);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 
	 public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		 ASFuntion asf = new ASFuntion();
		 
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 String customerId = asf.showNull(request.getParameter("customerId"));
		 String customerName = asf.showNull(request.getParameter("customerName"));
		 String customerRank = asf.showNull(request.getParameter("customerRank"));
		 String customerSource = asf.showNull(request.getParameter("customerSource"));
		 String businessType = asf.showNull(request.getParameter("businessType"));
		 String properties = asf.showNull(request.getParameter("properties"));
		 String projectType = asf.showNull(request.getParameter("projectType"));
		 String contractMoney = asf.showNull(request.getParameter("contractMoney"));
		 String workingHours = asf.showNull(request.getParameter("workingHours"));
		 String admissionTime = asf.showNull(request.getParameter("admissionTime"));
		 String distributeUser = asf.showNull(request.getParameter("distributeUser"));
		 String followUser = asf.showNull(request.getParameter("followUser"));
		 String auditUser = asf.showNull(request.getParameter("auditUser"));
		 String bidProjectId = asf.showNull(request.getParameter("bidProjectId"));//招投标主键ID
		 
		 String createtTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
		 String createDepartment = userSession.getUserAuditDepartmentId();
		 String createUser = userSession.getUserId();
		 
		 CustomerProject cp = new CustomerProject();
		 
		 cp.setAutoId(autoId);
		 cp.setCustomerId(customerId);
		 cp.setCustomerName(customerName);
		 cp.setCustomerRank(customerRank);
		 cp.setCustomerSource(customerSource);
		 cp.setBusinessType(businessType);
		 cp.setProperties(properties);
		 cp.setProjectType(projectType);
		 cp.setContractMoney(contractMoney);
		 cp.setWorkingHours(workingHours);
		 cp.setAdmissionTime(admissionTime);
		 cp.setDistributeUser(distributeUser);
		 cp.setFollowUser(followUser);
		 cp.setAuditUser(auditUser);
		 cp.setCreateUser(createUser);
		
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerProjectService cps = new CustomerProjectService(conn);
			 if(!"".equals(autoId)){
				 cps.update(cp);
			 }else{
				 cp.setState("待审核");
				 cp.setCreateUser(createUser);
				 cp.setCreateDepartment(createDepartment);
				 cp.setCreateDate(createtTime);
				 cps.add(cp);
				 if(!"".equals(bidProjectId)){
					 new DbUtil(conn).executeUpdate("UPDATE k_bidProject SET isGetBidProject = '是' WHERE `uuid` = '"+bidProjectId+"'");
				 }
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/customerProject.do?method=list");
			 DbUtil.close(conn);
		 }
		 
		 return modelAndView;
	 }
	  
	 public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerProjectService cps = new CustomerProjectService(conn);
			 cps.delete(autoId);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/customerProject.do?method=list");
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 
	 public ModelAndView audit(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 
			 new DbUtil(conn).executeUpdate("UPDATE k_customerProject SET state='已审核' WHERE autoid='"+autoId+"'");
			 
			 new  DbUtil(conn).executeUpdate("UPDATE k_customer a,k_customerProject  b SET estate ='正式'  WHERE a.departid = b.customerid AND b.autoId ='"+autoId+"'");
			
			 //下面暂时没用到
			 CustomerProjectService sps = new CustomerProjectService(conn);
			 CustomerProject cp = null;
			 if(!autoId.equals("")){
				 cp = sps.getCustomerProject(autoId);
				 
				/* BusinessProjectService bps = new BusinessProjectService(conn) ;
				 ProjectService projectService  = new ProjectService(conn);
				 
				 Project project = new Project();
				 project.setProjectId(projectID);
				 project.setCustomerId(customerId);
				 project.setAccPackageId(accPackageId);
				 project.setAuditPara(auditpara);
				 project.setAuditType(typeId);
				 project.setProjectName(projectName);
				 project.setAuditTimeBegin(auditTimeBegin);
				 project.setAuditTimeEnd(auditTimeEnd);
				 project.setProjectCreated(CHF.getCurrentDate()) ;
				 project.setDepartmentId(departmentId) ;
				 String projectId = projectService.save(project);
				 
				 
				 BusinessProject bp = new BusinessProject() ;
				 bp.setCustomerId(cp.getCustomerId());
				 bp.setBusinessCost(cp.getContractMoney());
				 bp.setIsSpecialProject(cp.getProjectType());
				 bp.setCustomerType(cp.getProperties());
				 bp.setProjectID(projectId);
				 bps.save(bp) ;*/
				 
	
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/customerProject.do?method=list");
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 
	 public ModelAndView getProjectByCustomerId(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(andAndEdit);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerProjectService cps = new CustomerProjectService(conn);
			 CustomerProject cp = cps.getCustomerProjectByautoId(autoId);
			 modelAndView.addObject("cp", cp);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 /**
		 * 下载附件
		 * 
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView download(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Connection conn = null;
			try {
				
				response.setContentType("text/html;charset=utf-8");
				conn = new DBConnect().getConnect("");
				ASFuntion CHF = new ASFuntion();
				
				String fileTempName = CHF.showNull(request.getParameter("fileTempName")) ;
				String fileName = CHF.showNull(request.getParameter("fileName")) ;
				String projectId = CHF.showNull(request.getParameter("projectId")) ;
				String path = "";
				if(!"".equals(projectId)){
					conn = new DBConnect().getConnect("");
					String customerId = new DbUtil(conn).queryForString("SELECT customerId FROM z_project WHERE projectId ='"+projectId+"'");
					
					DbUtil.close(conn);
					if(!"".equals(customerId)){
						path = BackupUtil.getDATABASE_PATH()+"../manuscript/"+customerId+"/"+projectId+"/";
					}else{
						path = BackupUtil.getDATABASE_PATH()+"../manuscript/未指导客户/"+projectId+"/";
					}
				}else{
					
					path = BackupUtil.getDATABASE_PATH()+"../businessProjectBook/";
				}
			//	String path = "D:/project/AuditSystem4.0/database/businessProjectBook/";
				fileTempName = URLEncoder.encode(fileTempName, "UTF-8");
				
				if (!new File(path+fileTempName).exists()) {
					PrintWriter out = response.getWriter();
					out.println("<script language=javascript>");
					out.println("	window.parent.alert(\"下载文件失败，找不到对应文件，请联系管理员！\");");
					out.println("</script>");
					out.close();
					return null;
				} else {
				
					fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
					
					response.setContentType("application/x-msdownload");
					response.setHeader("Content-disposition",
							"attachment; filename=" + fileName);
					
					//开始下载
					OutputStream os = response.getOutputStream();
					BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream(new File(path+fileTempName)));

					byte b[] = new byte[512];
					int len;

					while ((len = bis.read(b)) != -1) {
						os.write(b, 0, len);
					}
					os.flush();
					bis.close();
					os.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(conn);
			}

			return null;
		}
		//上传文件
		public ModelAndView upload(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			Connection conn = null ;
			ASFuntion CHF = new ASFuntion() ;
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			PrintWriter out = response.getWriter() ;
			
			String projectId = CHF.showNull(request.getParameter("projectId"));
			String showDiv = CHF.showNull(request.getParameter("showDiv")); //要显示的DIV
			String s1 = CHF.showNull(request.getParameter("s1")); // 列名1
			String s2 = CHF.showNull(request.getParameter("s2")); //列名2 
			
			
			try {
				
				MyFileUpload myfileUpload = new MyFileUpload(request);
				
				String path = "";
				String tempPath = "";
				//文件上传路径
				if("".equals(projectId)){
					
					path = BackupUtil.getDATABASE_PATH()+"../businessProjectBook/";
					tempPath = path + "temp/"  ;
				}else{
					conn = new DBConnect().getConnect("");
					String customerId = new DbUtil(conn).queryForString("SELECT customerId FROM z_project WHERE projectId ='"+projectId+"'");
					
					
					if(!"".equals(customerId)){
						path = BackupUtil.getDATABASE_PATH()+"../manuscript/"+customerId+"/"+projectId+"/";
					}else{
						path = BackupUtil.getDATABASE_PATH()+"../manuscript/未指导客户/"+projectId+"/";
					}
					tempPath = path + "temp/"  ;
				}
				
				File file = new File(path);
				if(!file.exists()) {
					file.mkdirs() ; //创建文件夹
				}
				//文件上传的临时路径 
				File tempFilePath = new File(tempPath) ; 
				if(!tempFilePath.exists()) {
					tempFilePath.mkdirs() ;
				}
				
				String uploadTempPath = myfileUpload.UploadFile(null, tempPath);
				
				Map parameters = myfileUpload.getMap();
				
				String filename = CHF.showNull((String)parameters.get("filename")) ;
				String fileTempName = DELUnid.getNumUnid() ; //生成一个随机文件名
				
				if(!"".equals(projectId)){
					String textProjectId = CHF.showNull(new DbUtil(conn).queryForString("SELECT projectId FROM z_projectext WHERE projectId ='"+projectId+"'"));
					
					if("".equals(textProjectId)){
						new DbUtil(conn).execute(" INSERT INTO `asdb`.`z_projectext` (`projectid`,`"+s1+"`,"+s2+",s0 ) VALUES ('"+projectId+"','"+fileTempName+"','"+filename+"','二审时上传的附件' )");
					}else{
						if("report".equals(showDiv)){
							new DbUtil(conn).execute(" update `z_projectext` set "+s1+"='"+fileTempName+"',"+s2+"='"+filename+"' where projectId ='"+projectId+"'");
						}else if("nodulus".equals(showDiv)){
							new DbUtil(conn).execute(" update `z_projectext` set "+s1+"='"+fileTempName+"',"+s2+"='"+filename+"' where projectId ='"+projectId+"'");
						}else if("plan".equals(showDiv)){
							new DbUtil(conn).execute(" update `z_projectext` set "+s1+"='"+fileTempName+"',"+s2+"='"+filename+"' where projectId ='"+projectId+"'");
						}
					}
				}
				String type = CHF.showNull((String)parameters.get("type")) ;
				
				File newFile = new File(path+fileTempName) ;
				
				while(newFile.exists()) {
					fileTempName = DELUnid.getNumUnid() ; //重新生成文件名
					newFile = new File(path+fileTempName) ;
				}
				//把文件拷走
				File tempFile = new File(uploadTempPath+filename) ;
				ManuFileService mfs = new ManuFileService() ;
				mfs.copyFile(tempFile, newFile) ;
				
				if(tempFile.exists()) {
					tempFile.delete();
				}
				
				if(!"".equals(projectId)){
					out.println("<script>window.parent.updateBusiness('"+filename+"','"+fileTempName+"','"+showDiv+"','"+s1+"','"+s2+"')</script>");
				}else{				
					if("0".equals(type)) {
						//业务约定书
						out.println("<script>window.parent.updateBusiness('"+filename+"','"+fileTempName+"')</script>");
					}else {
						//保密文件
						out.println("<script>window.parent.updateSecrect('"+filename+"','"+fileTempName+"')</script>");
					}
				}
				DbUtil.close(conn);
				out.close() ;
			}catch(Exception e) {
				out.println("<script>window.parent.updateResult('fail')</script>");
				e.printStackTrace() ;
			}finally{
				DbUtil.close(conn) ;
			}
			return null; 
		}
		

}
