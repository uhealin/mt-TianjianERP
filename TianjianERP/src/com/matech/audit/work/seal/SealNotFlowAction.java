package com.matech.audit.work.seal;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.seal.SealService;
import com.matech.audit.service.seal.model.Seal;
import com.matech.audit.service.seal.model.SealFlow;
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

public class SealNotFlowAction extends MultiActionController{
	
	private static final String LIST="seal/List.jsp";
	private static final String ADDSKIP="seal/AddandEdit.jsp";
	private static final String UPDATESKIP="seal/AddandEdit.jsp";
	

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
				
				
				String ppSql="SELECT c.uuid,c.`ApplyDate`,c.applyDepartment,c.`matter`,c.`ctype`,c.`status`,c.`remark`,d.`Name`,c.sealCount FROM \n"
								+"k_seal c \n"
								+"LEFT JOIN k_user d ON c.`userId` = d.id  \n"
								+"WHERE 1=1 AND STATUS !='已作废' and c.userId = '"+userSession.getUserId()+"'  ${applyDate} ${remark} ${matter} ${ctype}"; 
				
				pp.setTableID("sealList");
				pp.setCustomerId("");
				pp.setWhichFieldIsValue(1);
				pp.setInputType("radio");
				pp.setOrderBy_CH("applyDate");
				pp.setDirection("desc");
				
				pp.setColumnWidth("8,8,10,10,6,20");
				pp.setSQL(ppSql);
				
				pp.setTrActionProperty(true); // 设置 table可双击
				pp.setTrAction("style=\"cursor:hand;\" uuid=\"${uuid}\"  ");
				
				//pp.addColumn("发布人", "userId");
				//pp.addColumn("发布部门", "departmentId");
				pp.addColumn("申请事项", "matter");
				pp.addColumn("公章类型", "ctype");
				pp.addColumn("请用部门", "applyDepartment");
				pp.addColumn("申请时间", "applyDate");
				pp.addColumn("份数", "sealCount");
				pp.addColumn("状态", "status");
				//pp.addColumn("备注", "remark");
				
				pp.setPrintEnable(true);
				pp.setPrintTitle("印章请用列表");
				
				pp.addSqlWhere("applyDate"," and c.applyDate like '%${applyDate}%' ");
				pp.addSqlWhere("remark"," and c.remark like '%${remark}%' ");
				pp.addSqlWhere("matter"," and c.matter like '%${matter}%' ");
				pp.addSqlWhere("ctype"," and c.ctype like '%${ctype}%' ");
				
	 			
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
			SealAction sealAction = new SealAction();
			String matter = asf.showNull(request.getParameter("matter"));
			String ctype = asf.showNull(request.getParameter("ctype"));
			String fileName = asf.showNull(request.getParameter("fileName"));
			String remark = asf.showNull(request.getParameter("remark"));
			String applyDepartId = asf.showNull(request.getParameter("applyDepartId"));
			String sealCount = asf.showNull(request.getParameter("sealCount"));
			String applyDepartment = asf.showNull(request.getParameter("applyDepartment"));
			String attachname = asf.showNull(request.getParameter("attachname"));
			 
			String isBeginFile = asf.showNull(request.getParameter("isBeginFile")); //保存并发起流程
			
			String instationMsg =  request.getParameter("instationMsg") ; //站内短信
			String mobilePhoneMsg =  request.getParameter("mobilePhoneMsg") ; //手机短信
			String msgUserId =  request.getParameter("msgUserId") ; //人员
			
			
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
			seal.setApplyDepartment(applyDepartment);
			seal.setApplyDepartId(applyDepartId);
			seal.setSealCount(sealCount);
			seal.setAttachname(attachname);
			seal.setPrintCount(sealCount);	
			Connection conn = null;
		
			try{
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				sealService.add(seal);
				
				if("发起".equals(isBeginFile)){
					sealAction.setJbpmTemplate(jbpmTemplate);
					sealAction.startFlow(request, response, uuid); //启动流程
					
					String content = "您有一个电子印章的审批等待处理，请及时登录ERP系统进行审批!";
					System.out.println("发文发送内容 ："+content);
					if("是".equals(instationMsg)){
						PlacardService placardService = new PlacardService(conn);
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
						InteriorEmailAction emailAction = new InteriorEmailAction();
						if(!"".equals(msgUserId)){
							msgUserId = msgUserId.substring(0, msgUserId.length()-1);
						}
						emailAction.mobilePhoneInfo(request,response,"", msgUserId, content);
					}*/
				}
				response.sendRedirect(request.getContextPath()+"/sealNotFlow.do?method=list");
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
			String sealCount = asf.showNull(request.getParameter("sealCount"));
			String applyDepartId = asf.showNull(request.getParameter("applyDepartId"));
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			String applyDepartment = asf.showNull(request.getParameter("applyDepartment"));
			Seal seal = new Seal();
			seal.setUuid(uuid);
			seal.setUserId(userSession.getUserId());
			seal.setMatter(matter);
			seal.setCtype(ctype);
			seal.setFileName(fileName);
			seal.setRemark(remark);
			seal.setSealCount(sealCount);
			seal.setApplyDepartment(applyDepartment);
			seal.setApplyDepartId(applyDepartId);
			seal.setPrintCount(sealCount);	
			Connection conn = null;
		
			try{
				conn = new DBConnect().getConnect();
				
				SealService sealService = new SealService(conn);
				
				sealService.update(seal);
				
				response.sendRedirect(request.getContextPath()+"/sealNotFlow.do?method=list");
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
				
				response.sendRedirect(request.getContextPath()+"/sealNotFlow.do?method=list");
				
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
		
}
