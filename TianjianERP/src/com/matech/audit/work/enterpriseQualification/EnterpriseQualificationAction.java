package com.matech.audit.work.enterpriseQualification;

import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.enterpriseQualification.EnterpriseQualificationPowerService;
import com.matech.audit.service.enterpriseQualification.EnterpriseQualificationService;
import com.matech.audit.service.enterpriseQualification.model.EnterpriseQualification;
import com.matech.audit.service.enterpriseQualification.model.EnterpriseQualificationPower;
import com.matech.audit.service.role.model.RoleTable;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.sms.SmsOpt;

public class EnterpriseQualificationAction extends MultiActionController{
	private static final String list = "/EnterpriseQualification/list.jsp";
	private static final String addAndEdit = "EnterpriseQualification/addAndEdit.jsp";
	private static final String view = "EnterpriseQualification/view.jsp";
	
	private static final String borrow = "EnterpriseQualification/borrow.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		ModelAndView model = new ModelAndView(list) ;
		
		String opt = request.getParameter("opt");
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String departments = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "enterpriseQualificationList"));
			DataGridProperty pp = new DataGridProperty();
			
			
			String sql = " select DISTINCT eq.uuid,title,uploadUserId,eq.uploadTime,attachFileId,eqp.modelname,u.name as name " 
					+ " from k_enterpriseQualification eq " 
					+ " left join k_enterpriseQualificationPower eqp on eq.uuid=eqp.enterpriseQualificationId " 
					+ " left join k_user u on eq.uploadUserId=u.id where u.departmentId in ("+departments+") OR  eq.uploadUserId = "+userid;
			
			if("view".equalsIgnoreCase(opt)){
//				conn = new DBConnect().getConnect("");
//				EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
//				List list = eqs.getListBySql(" select distinct rid from k_userrole where userid = '"+userid+"'");
//				String roleid = "";
//				
//				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//					Map map = (Map) iterator.next();
//					roleid = roleid + "  concat(',',powerId,',') like '%,"+map.get("rid")+",%' or "  ;
//					
//				}
//				
//				if(roleid.indexOf("or")>0){
//					roleid = " ( ( "+roleid.substring(0,roleid.lastIndexOf("or")) + " ) and powerType='角色' ) ";
//				} 
//				 
//				sql = " select eq.uuid,title,uploadUserId,eq.uploadTime,attachFileId,eqp.modelname,u.name " 
//					+ " from k_enterpriseQualification eq " 
//					+ " left join k_enterpriseQualificationPower eqp on eq.uuid=eqp.enterpriseQualificationId " 
//					+ " left join k_user u on eq.uploadUserId=u.id " 
//					+ " where ( concat(',',powerId,',') like '%,"+userid+",%' and powerType='人员') or "+roleid;
				
				response.sendRedirect(request.getContextPath() + "/enterpriseQualification.do?method=viewList");
				return null;
			}
			pp.setColumnWidth("15,10,10");
			pp.addColumn("标题", "title");
			//pp.addColumn("模块名称", "modelname");
			pp.addColumn("上传人", "name");
			
			pp.addColumn("上传时间", "uploadTime");
			
			
			pp.setTableID("enterpriseQualificationList");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("uploadTime");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			model.addObject("opt", opt);
			
			System.out.println("opt="+opt+"         1 list  sql="+sql);
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			// TODO: handle exception
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
	 * 获取角色列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getRoleList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		ASFuntion af = new ASFuntion();
		
		String roleId = af.showNull(request.getParameter("roleId"));
		roleId = ","+roleId;
		Connection conn = null;
		
//		StringBuffer sb = new StringBuffer();

//		sb.append("[");
//
//		try {
//			
//			conn = new DBConnect().getConnect("");
//		
//			List roleList = new EnterpriseQualificationService(conn).getRoleList();
//
//			boolean check = false;
//			
//			for(int i=0; i < roleList.size(); i++) {
//				RoleTable roletable = (RoleTable)roleList.get(i);
//				check = (","+roleId).indexOf("," + roletable.getId() + ",") > -1;
//				
//				sb.append(" {cls:'file',")
//					.append("leaf:true,")
//					.append("children:null,")
//					.append("type:'role',")
//					.append("roleId:'").append(roletable.getId()).append("',")
//					.append("checked:").append(check).append(", ")
//					.append("id:'roleId_" + roletable.getId()).append("',")
//					.append("text:'").append(roletable.getRolename()).append("' ");
//				
//				sb.append("}");
//				if(i != roleList.size()-1) {
//					sb.append(",");
//				}
//			}
//				
//			sb.append("]");
//			
//			System.out.println(" 角色 sb.toString()="+sb.toString());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			DbUtil.close(conn);
//		}
//		
//		response.getWriter().write(sb.toString());
//		return null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		//sb.append("[");
		List treeList = new ArrayList() ;
		try {
			
			conn = new DBConnect().getConnect("");
		
			List roleList = new EnterpriseQualificationService(conn).getRoleList();

			boolean check = false;
			
			for(int i=0; i < roleList.size(); i++) {
				RoleTable roletable = (RoleTable)roleList.get(i);
				//check = (","+roleId).indexOf("," + roletable.getId() + ",") > -1;
				//sb.append(" {cls:'file',")
				//.append("leaf:false,")
				//.append("children:null,")
				//.append("type:'role',")
				//.append("roleId:'").append(roletable.getId()).append("',")
				//.append("checked:").append(check).append(", ")
				//.append("id:'roleId_" + roletable.getId()).append("',")
				//.append("text:'").append(roletable.getRolename()).append("' ");
				//sb.append("}");
				/*if(i != roleList.size()-1) {
					sb.append(",");
				}*/
				Map map = new HashMap();
				map.put("text", roletable.getRolename());
				map.put("id", "roleId_"+roletable.getId());
				map.put("leaf", false);
				map.put("checked", false);
				
				String sql = "select b.id,b.name from k_userRole a \n" +
							" inner join k_user b on a.userid = b.id \n"+
							"where rid='"+roletable.getId()+"'";
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				List childList = new ArrayList() ;
			
				while(rs.next()) {
					boolean checked = false;
					if(roleId.indexOf(rs.getString("id")+",")>0){
						checked = true;
					} 
					Map childTreeNode = new HashMap();
					childTreeNode.put("id",rs.getString(1)) ;
					childTreeNode.put("text", rs.getString(2));
					childTreeNode.put("userName",rs.getString(2)) ;
					childTreeNode.put("leaf",true) ;
					childTreeNode.put("checked",checked) ;
					childList.add(childTreeNode) ;
				}
				map.put("children",childList) ;
				treeList.add(map) ;	
			}
			//sb.append("]");
			
			//System.out.println(" 角色 sb.toString()="+sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		//response.getWriter().write(sb.toString());
		String jsonStr = JSONArray.fromObject(treeList).toString() ;
		response.getWriter().write(jsonStr) ;
		return null;
	}
	
	
	
	/**
	 * 跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView go(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(addAndEdit) ;

		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		EnterpriseQualification  eqm = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			
			if("add".equalsIgnoreCase(opt)){
				eqm = new EnterpriseQualification();
				eqm.setUuid(UUID.randomUUID().toString());
				eqm.setAttachFileId(UUID.randomUUID().toString());
				model.addObject("eqm",eqm);
			}else{
				EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
				eqm = eqs.getEnterpriseQualification(id);

				EnterpriseQualificationPowerService eqps = new EnterpriseQualificationPowerService(conn);
				List liste = eqps.getEnterpriseQualificationPowerList(id);
				
				// 模块名称
				String modelName = "";
				String joinUser = "";
				String joinRole = "";
				//String powerId = "";
				//String powerType = "";
				for (Iterator iterator = liste.iterator(); iterator.hasNext();) {
					EnterpriseQualificationPower eqp = (EnterpriseQualificationPower) iterator.next();
					modelName = eqp.getModelName();
					
					if("人员".equals(eqp.getPowerType())){
						joinUser = eqp.getPowerId();
					}
					if("角色".equals(eqp.getPowerType())){
						joinRole = eqp.getPowerId();
					}
					//powerId = powerId + eqp.getPowerId();
					//powerType = eqp.getPowerType();
				}
				
				/*if(modelName.indexOf(",")>0){
					modelName = modelName.substring(0,modelName.length()-1);
				}*/
				
				// 根据 参与人 找出参与人所在 部门 去 勾 部门树
				//DbUtil du = new DbUtil(conn);
				
				//String sql = "";
				
				//String joinUserDepartmentId = "";
				//String departmengId = "";
				
				/*if(powerId.indexOf(",")>0){
					String[] powerIds = powerId.split(",");
					sql = " select departmentid from k_user where id=? ";
					for (int i = 0; i < powerIds.length; i++) {
						joinUser = joinUser + powerIds[i]+",";
						departmengId = du.queryForString(sql, new Object[]{powerIds[i]});
						if(joinUserDepartmentId.indexOf(","+departmengId+",")<0){
							joinUserDepartmentId = joinUserDepartmentId + departmengId + "," ;
						}
					}
				}*/
				model.addObject("eqm",eqm);
				model.addObject("modelName",modelName);
				//if("角色".equals(powerType)){
				//	model.addObject("powerId",powerId);
				//}else{
					model.addObject("joinUser",joinUser);
					model.addObject("joinRole",joinRole);
					//model.addObject("joinUserDepartmentId",joinUserDepartmentId);
				//	}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		model.addObject("opt",opt);
		return model;
		
	}
	
	
	/**
	 * 查看
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(view) ;

		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		EnterpriseQualification  eqm = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			eqm = eqs.getEnterpriseQualification(id);

			EnterpriseQualificationPowerService eqps = new EnterpriseQualificationPowerService(conn);
			List liste = eqps.getEnterpriseQualificationPowerList(id);
			
			// 模块名称
			String modelName = "";
			String powerId = "";
			String powerType = "";
			for (Iterator iterator = liste.iterator(); iterator.hasNext();) {
				EnterpriseQualificationPower eqp = (EnterpriseQualificationPower) iterator.next();
				modelName = modelName + eqp.getModelName() + ",";
				powerId = powerId + eqp.getPowerId();
				powerType = eqp.getPowerType();
			}
			
			if(modelName.indexOf(",")>0){
				modelName = modelName.substring(0,modelName.length()-1);
			}
			
			model.addObject("eqm",eqm);
			model.addObject("modelName",modelName);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		model.addObject("opt",opt);
		return model;
		
	}
	
	
	
	/**
	 * 保存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response)  throws Exception {

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		
		String uuid = af.showNull(request.getParameter("uuid"));
		String title = af.showNull(request.getParameter("title"));
		String modelName = af.showNull(request.getParameter("modelName"));
		String attachFileId = af.showNull(request.getParameter("attachFileId"));
		
		String joinUser = af.showNull(request.getParameter("joinUser"));
		String joinRole = af.showNull(request.getParameter("joinRole"));
		
		String powerId = af.showNull(request.getParameter("powerId"));
		String maintainUser = af.showNull(request.getParameter("maintainUser")); //可维护附件的人员
		String powerType = af.showNull(request.getParameter("powerType"));
		
		Connection conn = null;
		
		EnterpriseQualification  eqm = new EnterpriseQualification();
		eqm.setUuid(uuid);
		eqm.setTitle(title);
		eqm.setUploadTime(af.getCurrentDate());
		eqm.setUploadUserId(userid);
		eqm.setAttachFileId(attachFileId);
		eqm.setMaintainUser(maintainUser);
		
		EnterpriseQualificationPower eqp = new EnterpriseQualificationPower();
		eqp.setUuid(UUID.randomUUID().toString());
		eqp.setEnterpriseQualificationId(uuid);
		eqp.setModelName(modelName);
		eqp.setPowerId(powerId);
		eqp.setPowerType(powerType);
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			EnterpriseQualificationPowerService eqps = new EnterpriseQualificationPowerService(conn);
			
			if("add".equalsIgnoreCase(opt)){
				eqs.addEnterpriseQualification(eqm);
				
			}else{
				new DbUtil(conn).executeUpdate("DELETE FROM `k_enterprisequalificationpower` WHERE enterpriseQualificationId = '"+uuid+"'");
				eqs.updateEnterpriseQualification(eqm);
				//eqps.updateEnterpriseQualificationPower(eqp);
			}
			
			if(!"".equals(joinUser)){
				eqp.setUuid(UUID.randomUUID().toString());
				eqp.setPowerId(joinUser);
				eqp.setPowerType("人员");
				eqps.addEnterpriseQualificationPower(eqp);
			}
			if(!"".equals(joinRole)){
				eqp.setUuid(UUID.randomUUID().toString());
				eqp.setPowerId(joinRole);
				eqp.setPowerType("角色");
				eqps.addEnterpriseQualificationPower(eqp);
			}
			
			response.sendRedirect(request.getContextPath()+"/enterpriseQualification.do?method=list");
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	

	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			EnterpriseQualificationService eqs= new EnterpriseQualificationService(conn);
			
			// 删除
			eqs.delete(id);
			
			response.sendRedirect(request.getContextPath()+"/enterpriseQualification.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 查看企业资质列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private static final String viewList = "EnterpriseQualification/viewList.jsp";
	public ModelAndView viewList(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView model = new ModelAndView(viewList) ;
		Connection conn = null;
		ResultSet rs = null;
		try {
			ASFuntion af = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();

			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			List mapList = new ArrayList();
			
			String attachfileid = af.showNull(request.getParameter("attachfileid")); //附件的attachfileid
			String sql = "";
			
			if("".equals(attachfileid)){
				//目录节点
				/*List list = eqs.getListBySql(" select distinct rid from k_userrole where userid = '"+userid+"'");
				String roleid = "";
				
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					Map map = (Map) iterator.next();
					roleid = roleid + "  concat(',',powerId,',') like '%,"+map.get("rid")+",%' or "  ;
					
				}
				
				if(roleid.indexOf("or")>0){
					roleid = " ( ( "+roleid.substring(0,roleid.lastIndexOf("or")) + " ) and powerType='角色' ) ";
				} 
				 
				sql = " select eq.uuid,title,uploaduserid,eq.uploadtime,attachfileid,eqp.modelname,u.name " 
					+ " from k_enterpriseQualification eq " 
					+ " left join k_enterpriseQualificationPower eqp on eq.uuid=eqp.enterpriseQualificationId " 
					+ " left join k_user u on eq.uploadUserId=u.id " 
					+ " where ( concat(',',powerId,',') like '%,"+userid+",%' and powerType='人员') or "+roleid;
					*/
				sql = " select * from (select DISTINCT eq.uuid,title,uploaduserid,eq.uploadtime,attachfileid,eqp.modelname,u.name,GROUP_CONCAT(DISTINCT powerId) as  powerId" 
					+ " from k_enterpriseQualification eq " 
					+ " left join k_enterpriseQualificationPower eqp on eq.uuid=eqp.enterpriseQualificationId " 
					+ " left join k_user u on eq.uploadUserId=u.id " 
					+ " where 1=1 group by eqp.enterpriseQualificationId ) a where 1=1 AND (CONCAT(',',powerId,',') LIKE CONCAT('%,',"+userid+",',%') OR powerId ='allUser') ";
//				System.out.println(sql);
				rs = db.getResultSet(sql);
				while(rs.next()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("uuid", rs.getString("uuid"));
					map.put("title", rs.getString("title"));
					map.put("uploaduserid", rs.getString("uploaduserid"));
					map.put("uploadtime", rs.getString("uploadtime"));
					map.put("attachfileid", rs.getString("attachfileid"));
					map.put("modelname", rs.getString("modelname"));
					map.put("name", rs.getString("name"));
					
					mapList.add(map);
				}
				model.addObject("flag", "folder");
			}else{
				//附件节点
				
				sql = "select * from MT_COM_ATTACH where indexid = '"+attachfileid+"' order by updatetime";
				rs = db.getResultSet(sql);
				while(rs.next()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("attachid", rs.getString("attachid"));
					map.put("attachname", rs.getString("attachname"));
					map.put("attachfile", rs.getString("attachfile"));
					map.put("indexid", rs.getString("indexid"));
					map.put("filesize", rs.getString("filesize"));
					map.put("updatetime", rs.getString("updatetime"));
					map.put("tilte", "文件名："+rs.getString("attachname")+"\n大小："+rs.getString("filesize")+"\n修改日期："+rs.getString("updatetime"));
					mapList.add(map);
				}
				
				model.addObject("flag", "file");
			}
			
			
			
			System.out.println(mapList);
			model.addObject("mapList", mapList);
			model.addObject("lineLength", 6 - mapList.size() % 6);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(conn);
		}
		
		
		return model;
		
	}
	
	//单位/部门树
	public void getTree(HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
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
			String joinUser = CHF.showNull(request.getParameter("joinUser")); //菜单ID	
			
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树
			
			System.out.println(addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect("");
			DepartmentService ds = new DepartmentService(conn);
			UserPopedomService up = new UserPopedomService(conn);
			String departments = "";
			if(!"".equals(menuid)){
				
				departments = up.getLoginIdPopedom(loginid, menuid);
			}
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			
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
				}else{
					//都是展开部门
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked);
					if("true".equals(addUser)){
						List list1 = eqs.getUser(departid, checked,","+joinUser);
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
	}
	
	private static final String addAccessory = "EnterpriseQualification/addAccessory.jsp";
	//添加附件
	public ModelAndView addAccessory(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(addAccessory) ;

		ASFuntion af = new ASFuntion();
		
		String uuid = af.showNull(request.getParameter("uuid"));
		String opt = af.showNull(request.getParameter("opt"));
		
		Connection conn = null;
		
		EnterpriseQualification  eqm = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			 
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			eqm = eqs.getEnterpriseQualification(uuid);

			EnterpriseQualificationPowerService eqps = new EnterpriseQualificationPowerService(conn);
			List liste = eqps.getEnterpriseQualificationPowerList(uuid);
			if(liste != null){
				
				//EnterpriseQualificationPower eqf = (EnterpriseQualificationPower)liste.get(0);
				String modelName = new DbUtil(conn).queryForString("select modelName  from k_enterpriseQualificationPower where enterpriseQualificationId ='"+uuid+"' LIMIT 1");
				model.addObject("modelName", modelName);
			}
			model.addObject("eqm", eqm);
			model.addObject("opt", opt);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
		
	}
	
	
	//借阅超期查询
	public ModelAndView borrow(HttpServletRequest request, HttpServletResponse response)  throws Exception {
//		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		ModelAndView model = new ModelAndView(borrow) ;
		try {
			
			DataGridProperty pp = new DataGridProperty();
			
			String sql = "select " +
			//"	concat(a.uuid,'|',if(ifnull(returntime,'')='',0,1)) as auuid," +
			"	a.*,b.name as username,c.departname,d.name as areaname,e.attachname, " +
			"	if(ifnull(returntime,'')>'',0,if(Datediff(borrowtime,now())<=0,1,0)) as diff, " +
			"	case when ifnull(returntime,'')>'' then '已归还' else " +
			"	case when Datediff(borrowtime,now()) = 0 then '今天到期'  " +
			"	when Datediff(borrowtime,now()) > 0 then concat('还有[',Datediff(borrowtime,now()),']天到期') " +
			"	when Datediff(borrowtime,now()) < 0 then concat('已超过[',abs(Datediff(borrowtime,now())),']天') end end as diffValue " +
			"	from K_ENTERPRISEBORROW a,k_user b,k_department c,k_area d,mt_com_attach e " +
			"	where a.BorrowUserid = b.id " +
			"	and b.Departmentid = c.autoid " +
			"	and c.areaid = d.autoid " +
			"	and a.BorrowAttachId = e.AttachId" ;
			
			//pp.setColumnWidth("15,10,10");
			pp.setTrBgColor("diff", "1", "#FF9966");
			
			pp.addColumn("超期检查", "diffValue");
			pp.addColumn("借用人", "username");
			pp.addColumn("借用证书", "attachname");
			pp.addColumn("所属分所", "areaname");
			pp.addColumn("所属部门", "departname");
			pp.addColumn("借阅时间", "ApplyTime");
			pp.addColumn("用途", "BorrowEffect");
			pp.addColumn("归还日期", "BorrowTime");
			pp.addColumn("手机号码", "phone");
			pp.addColumn("公司邮箱", "email");
			
			pp.addColumn("实际归还日期", "returntime");
			pp.addColumn("归还时证书状态", "returnstate");
			
			pp.addColumn("标志", "diff" ,"hide");//标志是否到期或过期
			
			pp.setTableID("tt" + DELUnid.getNumUnid());
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("BorrowTime");
			pp.setDirection_CH("desc");
			
			pp.setInputType("checkbox");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			
			pp.setSQL(sql);
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			model.addObject("returntime", StringUtil.getCurDate());
			model.addObject("smsvalue", "您借阅的证件已经超期，请尽快归还。");
			
			model.addObject("DataGrid", pp.getTableID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return model;

	}
	
	//发短信
	public void sendSms(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
//			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
//			String userid = userSession.getUserId();
			
			String uuids = StringUtil.showNull(request.getParameter("uuids"));
			String smsvalue = StringUtil.showNull(request.getParameter("smsvalue"));
			
			DbUtil db = new DbUtil(conn);
			
			String uuid = StringUtil.tranStrWithSign(uuids); //'1','2','3'
			
			String sql = "";
			
			//1、检查选择的uuid是不是已经全部归还了。全部归还就返回【证书已经归还，不用再归还】，否则就更新还没有归还的证书
			sql = "select 1 from k_enterpriseborrow where uuid in ("+uuid+") and ifnull(returntime,'') = '' "; //还有未归还的证书，就返回1
			String flag = StringUtil.showNull(db.queryForString(sql));
			
			String result = "";
			if("1".equals(flag)){
				//修改证书状态
				sql = "select a.*,b.name,b.mobilephone,c.attachname " +
				"	from k_enterpriseborrow a,k_user b,mt_com_attach c " +
				"	where a.borrowuserid = b.id " +
				"	and a.borrowattachid = c.attachid " +
				"	and ifnull(returntime,'') = '' " +
				"	and a.uuid in ("+uuid+") " ;
				List list = db.getList(sql);
				for (int i = 0; i < list.size(); i++) {
					Map m = (Map)list.get(i);
					System.out.println(m.get("name") + "|" + m.get("mobilephone") + "|" + m.get("attachname"));
					String mobile = StringUtil.showNull(m.get("mobilephone"));
					if("".equals(mobile)) {
						mobile = StringUtil.showNull(m.get("phone"));
					}
					if(!"".equals(mobile)){
						String context = "";
						context = m.get("name") + "先生：" + smsvalue + "\n所借证书："+m.get("attachname")+"\n借出时间：" + m.get("lendertime")+"\n归还时间：" + m.get("borrowtime");
						System.out.println(mobile + "|短信：" + context);
						
						//发送短信
						SmsOpt.sendSm(mobile,context);	
					}
				}
				
				result = "短信发送成功！";
			}else{
				result = "证书已经归还，不用再发短信！";
			}
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
			out.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}	
	}
	
	//归还登记
	public void borrowSave(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
		
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String userid = userSession.getUserId();
			String uuids = StringUtil.showNull(request.getParameter("uuids"));
			String returntime = StringUtil.showNull(request.getParameter("returntime"));
			String returnstate = StringUtil.showNull(request.getParameter("returnstate"));
			
			System.out.println(uuids +"|" + returntime + "|" + returnstate);
			DbUtil db = new DbUtil(conn);
			
			String uuid = StringUtil.tranStrWithSign(uuids); //'1','2','3'
			String sql = "";
			
			//1、检查选择的uuid是不是已经全部归还了。全部归还就返回【证书已经归还，不用再归还】，否则就更新还没有归还的证书
			sql = "select 1 from k_enterpriseborrow where uuid in ("+uuid+") and ifnull(returntime,'') = '' "; //还有未归还的证书，就返回1
			String flag = StringUtil.showNull(db.queryForString(sql));
			
			String result = "";
			if("1".equals(flag)){
				//修改证书状态
				sql = "update k_enterpriseborrow set property='在库' ,returnoperation = ?,returntime = ?,returnstate = ? where uuid in ("+uuid+") and ifnull(returntime,'') = '' ";
				db.execute(sql,new String[]{userid,returntime,returnstate});
				
				
				
				result = "证书归还登记成功！";
			}else{
				result = "证书已经归还，不用再归还！";
			}
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
}
