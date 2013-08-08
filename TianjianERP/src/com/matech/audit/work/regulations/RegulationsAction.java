package com.matech.audit.work.regulations;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attach.AttachService;
import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.regulations.RegulationsService;
import com.matech.audit.service.regulations.model.Regulations;
import com.matech.audit.service.user.model.User;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class RegulationsAction extends MultiActionController {
	private final String MAIN = "regulations/main.jsp";
	private final String LIST = "regulations/list.jsp";
	private final String ADDANDEDIT = "regulations/addAndEdit.jsp";
	private final String VIEW = "regulations/view.jsp";
	private final String MORE = "regulations/more.jsp";

	/**
	 * 列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse res)
			throws Exception {

		DataGridProperty pp = new DataGridProperty();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		// 必要设置
		pp.setTableID("regulationsList");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);
		pp.addSqlWhere("ctype"," and a.ctype like '%${ctype_id}%' ");
		pp.addSqlWhere("title"," and title like '%${title}%' ");
		pp.addSqlWhere("publishUserId","  and a.loginid = '${publishUserId}' ");
		pp.addSqlWhere("updateTime"," and a.updateTime BETWEEN '${updateTime}' AND '${endupdateTime}'  ");//
		// sql设置

		String sql = " select * from (select r.autoId,ifnull(c.value,r.ctype) as ctype,subString(r.title,1,30) as title,subString(r.contents,1,30) as contents,r.updateTime,r.publishUserId,r.attachmentId,r.memo,r.property,u.name "
				+ " from oa_regulations r " +
					" left join k_user u on r.publishUserId=u.id " +
					" left join k_dic c on r.ctype=c.autoId and c.ctype LIKE '%部门规章%' " +
					" ) a where 1 = 1 ${ctype}  ${title} ${publishUserId} ${updateTime} and a.publishUserId='"+userSession.getUserId()+"' ";//

		pp.setSQL(sql);
		pp.setOrderBy_CH("updateTime");
		pp.setDirection("desc");

		pp.setColumnWidth("30,15,10,15");

		pp.setInputType("checkbox");
		pp.addColumn("标题", "title");
		pp.addColumn("分类", "ctype");
		pp.addColumn("发布人", "name");
		pp.addColumn("时间", "updateTime");

		pp.setTrActionProperty(true);
		pp.setTrAction(" autoId='${autoId}' style='cursor:hand;'");
		
		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("规章制度列表");

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		
		DataGridProperty ppLook = new DataGridProperty();

		// 必要设置
		ppLook.setTableID("regulationsLookList");
		// 基本设置

		ppLook.setCustomerId("");

		ppLook.setPageSize_CH(50);

		// sql设置

		String sqlLook ="SELECT DISTINCT a.*,ifnull(c.value,a.ctype) as autoCtype,u.`Name`  \n"
						+"FROM oa_regulations a \n"
						//+"LEFT JOIN k_userrole b ON a.lookRole LIKE CONCAT('%,',b.rid,',%') \n"
						+"LEFT JOIN k_user u ON a.publishUserId=u.id  \n"
						+" left join k_dic c on a.ctype=c.autoId and c.ctype LIKE '%部门规章%' " 
						+"WHERE 1=1  AND (a.lookUser LIKE '%,"+userSession.getUserId()+",%' or a.lookUser='allUser' OR a.`lookRole` LIKE '%,"+userSession.getUserId()+",%')"; 
		 

		ppLook.setSQL(sqlLook);
		ppLook.setOrderBy_CH("updateTime");
		ppLook.setDirection("desc");

		ppLook.setColumnWidth("30,15,10,15");

		ppLook.addColumn("标题", "title");
		ppLook.addColumn("分类", "autoCtype");
		ppLook.addColumn("发布人", "name");
		ppLook.addColumn("时间", "updateTime");

		ppLook.setTrActionProperty(true);
		ppLook.setTrAction(" autoId='${autoId}' style='cursor:hand;'");
		
		ppLook.setWhichFieldIsValue(1);

		ppLook.setPrintEnable(true);
		ppLook.setPrintVerTical(false);
		ppLook.setPrintTitle("规章制度列表");

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		request.getSession().setAttribute(DataGrid.sessionPre + ppLook.getTableID(), ppLook);

		return new ModelAndView(LIST);
	}

	/**
	 * 得到 news list 集合对象
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getListRegulations(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(MAIN);
		List<Regulations> list = null;
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);
			list = rs.getListRegulations();
			model.addObject("regulationsList", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return model;
	}

	/**
	 * 添加跳转
	 * 
	 * @return
	 */
	public ModelAndView goAdd(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(ADDANDEDIT);
		return model;
	}

	/**
	 * 修改跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView goUpdate(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(ADDANDEDIT);
		String autoId = request.getParameter("autoId");
		Connection conn = null;
		Regulations r = null;

		try {
			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);
			r = rs.getRegulationsByAutoId(autoId);
			model.addObject("autoId", autoId);
			model.addObject("regulations", r);
			
			/*
			List<Attach> list = rs.getAttach(r.getAttachmentId());
			System.out.println(list.size());
			if(list.size()>0){
				List lists = new ArrayList();
				for (Attach attach : list) {
					Map map = new HashMap();
					// 文件路径
					String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/"
					+ "regulations/" + attach.getMime();
					map.put("fileName",attach.getMime());
					map.put("filePath",filePath);
					map.put("unid", attach.getUnid());
					lists.add(map);
				}
				
				model.addObject("list", lists);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return model;
	}

	/**
	 * 获取部门列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getDepartmentList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		String type = request.getParameter("type");
		String joinUser = request.getParameter("joinUser");
		String departmentId = request.getParameter("departmentId");
		
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		Connection conn = null;
		
		StringBuffer sb = new StringBuffer();

		sb.append("[");

		try {
			
			conn = new DBConnect().getConnect("");
			
			String departmentIds = ",-9999999,";
			String userIds = ",-9999999,";
			
			if("department".equals(type)) {
					
				// 不用 默认勾选中 当前登录 人员 
				userIds += joinUser + ",";
				
				boolean check = false;
				
				//无部门人员
				if("0".equals(departmentId)) {
					departmentId = "";
				}
				
				List userList = new DepartmentService(conn).getUserList(departmentId);
				
				if(userList != null) {
					
					for(int j=0; j < userList.size(); j++) {
						User user = (User)userList.get(j);
						
						check = userIds.indexOf("," + user.getId() + ",") > -1;
						
						sb.append(" {cls:'file',")
							.append("leaf:true,")
							.append("checked:").append(check).append(",")
							.append("children:null,")
							.append("type:'user',")
							.append("icon:'img/").append(("M".equals(user.getSex()) || "男".equals(user.getSex())) ? "male.gif" : "female.gif").append("', ")
							.append("id:'user_").append(user.getId()).append("',")
							.append("departmentId:'").append(departmentId).append("',")
							.append("roleName:'").append(user.getRoles()).append("',")
							.append("userName:'").append(user.getName()).append("',")
							.append("userId:'").append(user.getId()).append("',")
							.append("text:'").append(user.getName()).append("'} ");
						if(j != userList.size()-1) {
							sb.append(",");
						}
					}
				}
			} else {
				List departmentList = new DepartmentService(conn).getDepartmentList(userSession.getUserAuditDepartmentId());

				departmentIds += ("".equals(userSession.getUserAuditDepartmentId()) ? "0" : userSession.getUserAuditDepartmentId()) + ",";
				
				// 对照 参与人字段，参与人有哪些就勾选哪些人
				// departmentIds = "0,";
				departmentIds = ","+request.getParameter("joinUserDepartmentId");
				
				boolean check = false;
				
				for(int i=0; i < departmentList.size(); i++) {
					DepartmentVO departmentVO = (DepartmentVO)departmentList.get(i);
					check = departmentIds.indexOf("," + departmentVO.getAutoId() + ",") > -1;
					
					sb.append(" { ")
						.append("cls:'folder',")
						.append("leaf:false,")
						.append("type:'department',")
						.append("departmentId:'").append(departmentVO.getAutoId()).append("',")
						.append("checked:").append(check).append(", ")
						.append("id:'department_" + departmentVO.getAutoId()).append("',")
						.append("text:'").append(departmentVO.getDepartmentName()).append("' ");
					
					sb.append("}");
					if(i != departmentList.size()-1) {
						sb.append(",");
					}
				}
				
			}
			sb.append("]");
			
			System.out.println(" 部门 sb.toString()="+sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		response.getWriter().write(sb.toString());
		return null;
	}
	
	/**
	 * 新增保存
	 * 
	 * @param request
	 * @param response
	 * @param news
	 * @return
	 * @throws IOException
	 */
	public ModelAndView addSave(HttpServletRequest request,
			HttpServletResponse response, Regulations regulations)
			throws IOException {
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();

			//MyFileUpload myfileUpload = new MyFileUpload(request);
			//myfileUpload.setUploadProcess(true,"") ;
			
			//response.setContentType("text/html;charset=UTF-8") ;
			
			//文件上传的路径 
			/*String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/regulations/";
			File file = new File(filePath) ; 
			if(!file.exists()) {
				file.mkdir() ;
			}
			
			String uploadtemppath = myfileUpload.UploadFile(null, filePath);

			System.out.println(this.getClass()+"   |  uploadtemppath="+uploadtemppath+"  | filePath="+filePath);


			Map parameters = myfileUpload.getMap();*/

			String unid = DELUnid.getNumUnid(); // 唯一编号

			/*regulations.setAttachmentId(unid);
			regulations.setTitle((String) parameters.get("title"));
			regulations.setContents((String) parameters.get("contents"));
			regulations.setMemo((String) parameters.get("memo"));*/
			String joinUser = CHF.showNull(request.getParameter("joinUser"));
			if(!"allUser".equals(joinUser)){
				joinUser=","+joinUser;
			}
			String attachmentId = request.getParameter("attachmentId");
			String ctype = request.getParameter("ctype");
			regulations.setAttachmentId(unid);
			regulations.setAttachmentId(attachmentId);
			regulations.setTitle( request.getParameter("title"));
			regulations.setContents(request.getParameter("contents"));
			regulations.setMemo( request.getParameter("memo"));
			regulations.setLookUser(joinUser);
			regulations.setLookRole( ","+request.getParameter("joinRole"));
			regulations.setCtype(ctype);

			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);

			// 获取当前时间
			String updateTime = CHF.getCurrentDate();
			regulations.setUpdateTime(updateTime);

			// 当前登录用户
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			regulations.setPublishUserId(userSession.getUserId());

			// 保存
			rs.addRegulations(regulations);

			// 附件名称 input="file" 类型
			//String filename = (String)request.getParameter("attachmentId");

		/*	if (filename != null && !"".equals(filename)) {
				// 上传附件
				Attach attach = new Attach();

				attach.setUnid(unid);

				attach.setUdate(CHF.getCurrentDate());
				String departid = userSession.getUserAuditDepartmentId();
				departid = "111"; // 暂时无条件设置 int 类型插入值的时候不能为空
				attach.setDepartid(departid);

				attach.setTitle(userSession.getCurCustomerId());
				attach.setTypeId("8");
				attach.setProperty(unid);
				attach.setOrderId("");
				attach.setContent("规章制度附件");
				attach.setEdate(CHF.getCurrentDate());
				attach.setReleasedate(CHF.getCurrentDate());
				attach.setLastDate(CHF.getCurrentDate());
				attach.setLastPerson(userSession.getUserId());

				attach.setFilename(filename);
				attach.setMime(filename.substring(filename.lastIndexOf("\\")+1,filename.length()));


				AttachService attachService = new AttachService(conn);
				attachService.save(attach, "ad");

			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("regulations.do");
		return null;
	}

	/**
	 * 修改保存
	 * 
	 * @param request
	 * @param response
	 * @param news
	 * @return
	 * @throws IOException
	 */
	public ModelAndView updateSave(HttpServletRequest request,
			HttpServletResponse response, Regulations regulations)
			throws IOException {
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();

			response.setContentType("text/html;charset=UTF-8") ;
/*			
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			myfileUpload.setUploadProcess(true,"") ;
			
			//文件上传的路径 
			String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/regulations/";
			File file = new File(filePath) ; 
			if(!file.exists()) {
				file.mkdir() ;
			}
			
			String uploadtemppath = myfileUpload.UploadFile(null, filePath);

			System.out.println(this.getClass()+"   |  uploadtemppath="+uploadtemppath+"  | filePath="+filePath);

			
			Map parameters = myfileUpload.getMap();
*/
			regulations.setAutoId((String) request.getParameter("autoId"));
			
			String joinUser = CHF.showNull(request.getParameter("joinUser"));
			if(!"allUser".equals(joinUser)){
				joinUser=","+joinUser;
			}
			regulations.setTitle((String) request.getParameter("title"));
			regulations.setContents((String) request.getParameter("contents"));
			regulations.setMemo((String) request.getParameter("memo"));
			regulations.setAttachmentId((String) request.getParameter("attachmentId"));
			regulations.setCtype((String) request.getParameter("ctype"));
			regulations.setLookUser(joinUser);
			regulations.setLookRole( ","+request.getParameter("joinRole"));

			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);
			ASFuntion as = new ASFuntion();
			// 获取当前时间
			String updateTime = as.getCurrentDate();
			regulations.setUpdateTime(updateTime);

			// 当前登录用户
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			regulations.setPublishUserId(userSession.getUserId());

			// 修改
			rs.updateRegulationsByAutoId(regulations);

			// 附件名称 input="file" 类型
			String filename = (String) request.getParameter("fileName");
		/*	if (filename != null && !"".equals(filename)) {
				// 上传附件
				Attach attach = new Attach();
				String unid = DELUnid.getNumUnid(); // 唯一编号
				attach.setUnid(unid);
	
				attach.setUdate(CHF.getCurrentDate());
				String departid = userSession.getUserAuditDepartmentId();
				departid = "111"; // 暂时无条件设置 int 类型插入值的时候不能为空
				attach.setDepartid(departid);
	
				attach.setTitle(userSession.getCurCustomerId());
				attach.setTypeId("8");
				attach.setProperty(rs.getRegulationsByAutoId((String)parameters.get("autoId")).getAttachmentId());
				attach.setOrderId("");
				attach.setContent("规章制度文件");
				attach.setEdate(CHF.getCurrentDate());
				attach.setReleasedate(CHF.getCurrentDate());
				attach.setLastDate(CHF.getCurrentDate());
				attach.setLastPerson(userSession.getUserId());
	 

				attach.setFilename(filename);
				attach.setMime(filename.substring(filename.lastIndexOf("\\")+1,filename.length()));

  

				AttachService attachService = new AttachService(conn);
				attachService.save(attach, "ad");
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("regulations.do");
		return null;
	}

	/**
	 * 查看
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView viewRegulations(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(VIEW);
		String autoId = request.getParameter("autoId");
		String opt = request.getParameter("opt");
		model.addObject("opt", opt);
		Connection conn = null;
		Regulations r = null;

		try {
			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);
			r = rs.getRegulationsByAutoId(autoId);
			model.addObject("regulations", r);
			
			List<Map> listMapIdea  =rs.getListMapIdea(autoId);
			model.addObject("listMapIdea", listMapIdea);
			
			List<Attach> list = rs.getAttach(r.getAttachmentId());
			System.out.println(list.size());
			if(list.size()>0){
				List lists = new ArrayList();
				for (Attach attach : list) {
					Map map = new HashMap();
					// 文件路径
					String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/"
					+ "regulations/" + attach.getMime();
					map.put("fileName",attach.getMime());
					map.put("filePath",filePath);
					map.put("unid", attach.getUnid());
					lists.add(map);
				}
				
				model.addObject("list", lists);
			}
			
		} catch (Exception e) {
		} finally {
			DbUtil.close(conn);
		}
		return model;
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deleteRegulations(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String autoId = request.getParameter("autoId");
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			RegulationsService rs = new RegulationsService(conn);
			AttachService attachService = new AttachService(conn);
			String[] autoids = autoId.split(",");
			
			for (int i = 0; i < autoids.length; i++) {
				Regulations rls = rs.getRegulationsByAutoId(autoids[i]);
				
				rs.deleteRegulationsByAutoId(autoids[i]);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect("regulations.do");
		return null;
	}

	/**
	 * 更多
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView more(HttpServletRequest request, HttpServletResponse res)
			throws Exception {

		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("regulationsList");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String sql = " select r.autoId,r.title,r.contents,r.updateTime,r.publishUserId,r.attachmentId,r.memo,r.property,u.name "
				+ " from oa_regulations r left join k_user u on r.publishUserId=u.id where 1 = 1 ";

		
		ASFuntion af = new ASFuntion();
		String title = af.showNull(request.getParameter("title"));
		String publishUserId = af.showNull(request
				.getParameter("publishUserId"));
		String updateTime = af.showNull(request.getParameter("updateTime"));


		// 查询设置
		if (!title.equals(""))
			sql = sql + " and title like '%" + title + "%'";
		if (!publishUserId.equals(""))
			sql = sql + " and publishUserId = '" + publishUserId + "'";
		if (!updateTime.equals(""))
			sql = sql + " and updateTime = '" + updateTime + "'";
		
		
		pp.setSQL(sql);
		pp.setOrderBy_CH("updateTime");
		pp.setDirection("desc");

		pp.setColumnWidth("15,60");
		
		pp.addColumn("时间", "updateTime");
		pp.addColumn("标题", "title");
//		pp.addColumn("内容", "contents");
//		pp.addColumn("发布人", "name");

		pp.setTrActionProperty(true);
		pp.setTrAction(" autoId='${autoId}' style='cursor:hand;'");
		
		pp.setWhichFieldIsValue(1);
		
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("规章制度列表");

		request.getSession().setAttribute(
				DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(MORE);
	}


	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");

		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;

		String filePath = request.getParameter("filePath");
		String fileName = request.getParameter("fileName");
		
		if(fileName==null || "".equals(fileName )){
			MyFileUpload myfileUpload = new MyFileUpload(request);
			String uploadtemppath = myfileUpload.UploadFile(null, null);
			Map parameters = myfileUpload.getMap();
			filePath = (String) parameters.get("filePath");
			fileName = (String) parameters.get("fileName");
		}


		try {
			if (!new File(filePath).exists()) {
				PrintWriter out = response.getWriter();
				out.println("<script language=javascript>");
				out.println("	window.parent.alert(\"下载文件失败，找不到对应文件，请联系管理员！\");");
				out.println("</script>");
				out.close();
				System.out.println("error:下载文件出错了,找不到对应的文件!");
				return null;
			} else {
				long fileLength = new File(filePath).length();
	
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition","attachment; filename=" + fileName);
				response.setHeader("Content-Length", String.valueOf(fileLength));
	
				bis = new BufferedInputStream(new FileInputStream(filePath));
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
		return null;
	}
	
	/**
	 * 删除文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception{

		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String filePath = request.getParameter("filePath");
		String attachmentId = request.getParameter("attachmentId");
		Connection conn= null;
		try{
			
			// 删除附件表中对应 的记录
			String sql = "delete from asdb.k_attach where UNID = '"+attachmentId+"'";
			 conn = new DBConnect().getConnect("");
			new DbUtil(conn).execute(sql);
	
	
			File file = new File(filePath);
			
			if(file.exists()) {
				if(file.delete()){     // 删除文件
					out.print("Y");
				}else{
					out.print("N");
				}
			}else{
				out.print("noexit");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			out.close();
		}
		return null;
	}
	
	public ModelAndView uploadImg(HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			response.setContentType("text/html;charset=UTF-8") ;
			
			//文件上传路径
			String path = request.getSession().getServletContext().getRealPath("/")+"ckEditorImages/";
			
			File file = new File(path);
			if(!file.exists()) {
				file.mkdirs() ;
			}
			
			String newFileName = DELUnid.getNumUnid() ;
			String uploadTempPath = myfileUpload.UploadFile(newFileName, path);
			Map parameters = myfileUpload.getMap();
			String fileName = (String)parameters.get("filename") ;  
			String ctype = (String)parameters.get("ctype");  //用来判断 是哪里调用的
			if(!"".equals(ctype) && ctype !=null){
				//response.getWriter().println("<script>parent.parent.document.getElementById('txtUrl').value = "+fileName+";" +"parent.parent.ckUploadImgWin.hide();</script>");
			}else{
				response.getWriter().println("<script>window.parent.setValue('"+fileName+"')</script>");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	  
    }
	
	/**
	 * 添加评论
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void addRGIdea(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Connection conn = null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			String content = request.getParameter("centent");
			
			String regulationsId = request.getParameter("autoId");
			
			conn = new DBConnect().getConnect("");
			
			RegulationsService regulationsService = new RegulationsService(conn);
			
			boolean result = regulationsService.addRegulationsIdea(regulationsId, userSession.getUserId(), content);
			
			if(result){
				out.write("true");
			}else{
				out.write("false");
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	
}
