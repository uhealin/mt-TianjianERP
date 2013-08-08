package com.matech.audit.work.bidProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.bidCompetitor.BidCompetitorService;
import com.matech.audit.service.bidCompetitor.model.BidCompetitor;
import com.matech.audit.service.bidproject.BidProjectService;
import com.matech.audit.service.bidproject.model.BidProject;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;


public class BidProjectAction extends MultiActionController{
	
	private static final String list = "/BidProject/list.jsp";
	private static final String addAndEdit = "BidProject/addAndEdit.jsp";
	private static final String addCompetitor = "BidProject/addCompetitor.jsp";
	private static final String addAttach = "BidProject/addAttach.jsp";
	private static final String addAudit = "BidProject/addAudit.jsp";
	private static final String look = "BidProject/look.jsp";
	
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(list) ;
		
		DataGridProperty pp = new DataGridProperty();//未分类
		DataGridProperty already = new DataGridProperty();//已审核
		ASFuntion af = new ASFuntion();
		String  gsName = af.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		Connection conn = null;
		
		String opt = af.showNull(request.getParameter("opt"));
		try {
			conn=new DBConnect().getConnect("");
		//	if("".equals(opt)){
		//		opt = "finish"; //屈总说不显示新增和修改
		//	}
			
			String sql = " select uuid,auditUnit,endDate,bidMember,bidMemberName,bidAttachId,bidCompetitor, "
					   + " bidAttachFileId,auditorName,auditDate,createDate,getBidPerson,getBidPrice," 
					   + " bidStatus,auditStatus,c.departname,isGetBidProject "
					   + " from k_bidProject bp left join k_customer c on bp.auditUnit=c.departid " 
					   + " left join k_user u on u.id=bp.createId " 
				       + " where 1=1 ${departname} ${bidMember} ${endDate} ${createDate} ";

			
			String menuid = af.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000649";
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid); //部门授权
			
			pp.addColumn("客户", "departname");
			pp.addColumn("参加人", "bidMemberName");
			
			
			if("add".equalsIgnoreCase(opt) || "update".equalsIgnoreCase(opt)){// 创建或者修改 招投标 信息
				pp.setColumnWidth("20,20,10,10,10");
				
				// 只能看到自己创建的招投标
				sql = sql + " and (createId = '"+userid+"' or concat(',',bidmember) like '%,"+userSession.getUserId()+",%') ";
			//sql = sql + "  and (u.departmentId IN ("+departments+")) ";
			}else if("uploadFile".equalsIgnoreCase(opt)){// 参与人上传标书
				pp.setColumnWidth("20,20,10,10,10");
				// 只能看到自己参与的招投标
				sql = sql + " and (createId = '"+userid+"' or concat(',',bidmember) like '%,"+userid+",%')  ";
				//sql = sql + " and (u.departmentId IN ("+departments+"))";
			}else if("audit".equalsIgnoreCase(opt)){// 审核标书
				pp.addColumn("审核人", "auditorName");
				pp.addColumn("审核日期", "auditDate");
				pp.setColumnWidth("20,20,10,10,10,10,10");
				String shichang = "";
				if(userSession.getUserAuditDepartmentName().indexOf("市场")>-1 || "19".equals(userSession.getUserId())){
					shichang = " or 1=1 ";
				}
				String auditSql = "";
				if(gsName.indexOf("安联")>-1){
					auditSql = " and bp.auditStatus <> '通过'";
				}
				sql = sql + "and ( concat(',',bidmember) like '%,"+userSession.getUserId()+",%' "+shichang+" )  "+auditSql+" and bp.auditStatus='未审核'";
				//sql = sql + "  and (u.departmentId IN ("+departments+")) ";
			}else if("finish".equalsIgnoreCase(opt)){// 完善投标信息 加 竞争对手  中标人 中标价格等
				pp.addColumn("中标人", "getBidPerson");
				pp.addColumn("中标价", "getBidPrice");
				pp.addColumn("审核人", "auditorName");
				pp.addColumn("审核日期", "auditDate");
				pp.setColumnWidth("20,20,10,10,10,10,10,10,10");
				
				String shichang = "";
				if(userSession.getUserAuditDepartmentName().indexOf("市场")>-1){
					shichang = " or 1=1 ";
				}
				sql = sql + "and ( concat(',',bidmember) like '%,"+userSession.getUserId()+",%' "+shichang+" )";
				// 只能看到自己创建的招投标
				//sql = sql + " and createId = '"+userid+"' and (u.departmentId IN ("+departments+"))  ";
			}else{
				// 只能看到自己创建的招投标
				sql = sql + " and createId = '"+userid+"' and (u.departmentId IN ("+departments+"))  ";
				pp.setColumnWidth("20,20,10,10,10");
			}
			
			
			
			pp.addColumn("招投标状态", "bidStatus");
			pp.addColumn("创建日期", "createDate");
			pp.addColumn("投标截止日期", "endDate");
			pp.addColumn("标书状态", "auditStatus");
			pp.addColumn("是否中标", "isGetBidProject");
			
			pp.addSqlWhere("departname", " and departname like '%${departname}%' ");
			pp.addSqlWhere("bidMember", " and bidMember like '%${bidMember}%' ");
			pp.addSqlWhere("endDate", " and endDate like '%${endDate}%' ");
			pp.addSqlWhere("createDate", " and createDate like '%${createDate}%' ");

			pp.setTableID("bidProjectList");
			
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("招投标列表");
		    pp.setPrintColumnWidth("15,20,62,30,30");
		    pp.setPrintSqlColumn("departname,bidMemberName,bidStatus,createDate,endDate,auditStatus,isGetBidProject");
			pp.setPrintColumn("客户名称`参加人`招投标状态`创建日期`投标截止日期`标书状态`是否中标");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("endDate");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println("111111111opt="+opt+" 招 投 标 list  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

			model.addObject("opt", opt);
			model.addObject("nowDate", af.getCurrentDate());
			model.addObject("gsName", gsName);
			
			
			//已审核 
			String alSql = " select uuid,auditUnit,endDate,bidMember,bidMemberName,bidAttachId,bidCompetitor, "
				   + " bidAttachFileId,auditorName,auditDate,createDate,getBidPerson,getBidPrice," 
				   + " bidStatus,auditStatus,c.departname,isGetBidProject "
				   + " from k_bidProject bp left join k_customer c on bp.auditUnit=c.departid " 
				   + " left join k_user u on u.id=bp.createId " 
			       + " where 1=1 ${departname} ${bidMember} ${endDate} ${createDate} and bp.auditStatus='通过'";
			
			already.setTableID("bidProjectAlreadyList");
			already.setCustomerId("");
			already.setWhichFieldIsValue(1);
			already.setInputType("radio");
			already.setOrderBy_CH("endDate");
			already.setDirection("desc");
			already.setPrintEnable(true);	//关闭dg打印

			already.setPrintTitle("已审核文件");
			
			already.setColumnWidth("15,15,15,10,10,10,10,10,5");
			already.setSQL(alSql);
			
			already.addColumn("客户", "departname");
			already.addColumn("参加人", "bidMember");
			already.addColumn("投标截止日期", "endDate");
			already.addColumn("创建日期", "createDate");
			
			
			already.addSqlWhere("departname", " and departname like '%${departname}%' ");
			already.addSqlWhere("bidMember", " and bidMember like '%${bidMember}%' ");
			already.addSqlWhere("endDate", " and endDate like '%${endDate}%' ");
			already.addSqlWhere("createDate", " and createDate like '%${createDate}%' ");
			
			request.getSession().setAttribute(DataGrid.sessionPre + already.getTableID(), already);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}				
		return model;
		
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
			
			BidCompetitorService bc = new BidCompetitorService(conn);
			// 删除该投标竞争人员
			bc.deleteByBidProjectId(id);
			
			BidProjectService bs = new BidProjectService(conn);
			// 删除招投标
			bs.deleteBidProject(id);
		
			response.sendRedirect(request.getContextPath()+"/bidProject.do?method=list&opt=add");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}

	
	static final String newCustomer = "newCustomer/AddandEdit.jsp"; //客户承接登记
	/**
	 * 发起客户承接
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView startCustomerContinue(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(newCustomer);
		try {
			
			ASFuntion asf = new ASFuntion();
			
			String id = asf.showNull(request.getParameter("id"));
			
			if(!"".equals(id)){
				
				conn = new DBConnect().getConnect("");
				
				BidProjectService bs = new BidProjectService(conn);
				CustomerService ct = new CustomerService(conn);

				BidProject bp = bs.getBidProject(id);
				Customer customer = ct.getCustomer(bp.getTrustOrgan());
				
				Map map = new HashMap();
				map.put("customername", bp.getUnitName());  //客户名称
				map.put("ownername", customer.getDepartName());//委托机构
				modelAndView.addObject("map",map);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	
	/**
	 * 作废
	 * @throws IOException 
	 */
	public ModelAndView cancle(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String id = request.getParameter("id");
		
		Connection conn=null; 
		try {
			conn=new DBConnect().getConnect("");
			
			String strSql = " select bidstatus from k_bidProject where uuid = ? ";
			String rs = new DbUtil(conn).queryForString(strSql,new Object[]{id});
			
			if("废标".equals(rs)){
				out.print("0");
			}else{
				String opt = request.getParameter("opt");
				if("updateStatus".equalsIgnoreCase(opt)){
					strSql = " update k_bidProject set bidStatus = '废标' where uuid = ? ";
					new DbUtil(conn).executeUpdate(strSql,new Object[]{id});
					
					out.print("1");
				}else{
					out.print("-1");
				}					
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 招投标书 【 新增、修改 】
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addAndEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addAndEdit);
		
		ASFuntion af = new ASFuntion();
		
		String opt = request.getParameter("opt");
		String id = request.getParameter("id");
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			if("add".equalsIgnoreCase(opt)){
				bp = new BidProject();
				bp.setBidAttachFileId(UUID.randomUUID().toString());
				model.addObject("bp",bp);
			}else{
				// 招投标信息
				BidProjectService bs = new BidProjectService(conn);
				bp = bs.getBidProject(id);
				
				// 竞争对手信息
				BidCompetitorService bcs = new BidCompetitorService(conn);
				List bidCompetorList = bcs.getBidCompetitorList(bp.getUuid());
				
				// 根据 参与人 找出参与人所在 部门 去 勾 部门数
				DbUtil du = new DbUtil(conn);
				
				String bidMember = bp.getBidMember();
				String bidMemberDepartmentId = "";
				String departmengId = "";
				if(bidMember!=null && !"".equals(bidMember)){
					String sql = " select departmentid from k_user where id=? ";
					String[] bidMembers = bidMember.split(",");
					for (int i = 0; i < bidMembers.length; i++) {
						departmengId = du.queryForString(sql, new Object[]{bidMembers[i]});
						if(bidMemberDepartmentId.indexOf(","+departmengId+",")<0){
							bidMemberDepartmentId = bidMemberDepartmentId + departmengId + "," ;
						}
					}
				}
				

				// 转 字符串 到 list
				List listBidMemeber = null;
				if(af.showNull(bp.getBidMember()).indexOf(",")>-1 && af.showNull(bp.getBidMemberName()).indexOf(",")>-1 && af.showNull(bp.getDuty()).indexOf(",")>-1){
					listBidMemeber = bs.toList(bp.getBidMember().split(","),bp.getBidMemberName().split(","),bp.getDuty().split(","));
				}else{
					listBidMemeber = new ArrayList();
				}
				
				model.addObject("bp",bp);
				model.addObject("bidCompetorList",bidCompetorList);
				model.addObject("listBidMemeber",listBidMemeber);
				model.addObject("listSize",bidCompetorList.size());
				model.addObject("bidMemberDepartmentId",bidMemberDepartmentId);
				
				
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
	public ModelAndView look(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(look);
		
		ASFuntion af = new ASFuntion();
		
		String opt = request.getParameter("opt");
		String id = request.getParameter("id");
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 招投标信息
			BidProjectService bs = new BidProjectService(conn);
			bp = bs.getBidProject(id);
			
			// 竞争对手信息
			BidCompetitorService bcs = new BidCompetitorService(conn);
			List bidCompetorList = bcs.getBidCompetitorList(bp.getUuid());
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门数
			DbUtil du = new DbUtil(conn);
			
			String bidMember = bp.getBidMember();
			String bidMemberDepartmentId = "";
			String departmengId = "";
			if(bidMember!=null && !"".equals(bidMember)){
				String sql = " select departmentid from k_user where id=? ";
				String[] bidMembers = bidMember.split(",");
				for (int i = 0; i < bidMembers.length; i++) {
					departmengId = du.queryForString(sql, new Object[]{bidMembers[i]});
					if(bidMemberDepartmentId.indexOf(","+departmengId+",")<0){
						bidMemberDepartmentId = bidMemberDepartmentId + departmengId + "," ;
					}
				}
			}
			

			// 转 字符串 到 list
			List listBidMemeber = null;
			if(af.showNull(bp.getBidMember()).indexOf(",")>-1 && af.showNull(bp.getBidMemberName()).indexOf(",")>-1 && af.showNull(bp.getDuty()).indexOf(",")>-1){
				listBidMemeber = bs.toList(bp.getBidMember().split(","),bp.getBidMemberName().split(","),bp.getDuty().split(","));
			}else{
				listBidMemeber = new ArrayList();
			}
			
			model.addObject("bp",bp);
			model.addObject("bidCompetorList",bidCompetorList);
			model.addObject("listBidMemeber",listBidMemeber);
			model.addObject("listSize",bidCompetorList.size());
			model.addObject("bidMemberDepartmentId",bidMemberDepartmentId);
			
				
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		model.addObject("opt",opt);
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
		String bidMemeber = request.getParameter("bidMemeber");
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
				userIds += bidMemeber + ",";
				
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
				departmentIds = ","+request.getParameter("bidMemberDepartmentId");
				
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
			
			System.out.println("sb.toString()="+sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		response.getWriter().write(sb.toString());
		return null;
	}
	
	
	/**
	 * 保存
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response){
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		String userName = userSession.getUserName();
		
		
		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		String uuid = af.showNull(request.getParameter("uuid"));
		String state ="已发起招投标登记";
		String autoId = af.showNull(request.getParameter("obautoId"));
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			if(uuid==null || "".equals(uuid)){
				uuid = UUID.randomUUID().toString();
			}
			
			String auditUnit = request.getParameter("auditUnit");
			String trustOrgan = request.getParameter("trustOrgan");
			String serviceStartTime = request.getParameter("serviceStartTime");
			String serviceEndTime = request.getParameter("serviceEndTime");
			String serviceType = request.getParameter("serviceType");
			String projectName = request.getParameter("projectName");
			String projectSimpleName = request.getParameter("projectSimpleName");
			
			String unitName = request.getParameter("unitName");
			String vocationType = request.getParameter("vocationType");
			String unitEngName = request.getParameter("unitEngName");
			String hylx = request.getParameter("hylx");
			String register = request.getParameter("register");
			String curName = request.getParameter("curName");
			String unitSimpleName = request.getParameter("unitSimpleName");
			
			
			String endDate = request.getParameter("endDate");
			String bidAttachId = request.getParameter("bidAttachId");
			String bidAttachFileId = request.getParameter("bidAttachFileId");
			
			// 保存 参与人 编号
			String bidMember = request.getParameter("bidMember");
			String bidMemberName = request.getParameter("bidMemberName");
			String dutys = request.getParameter("dutys");
			
			String auditDate = request.getParameter("auditDate");
			
			BidProject bp = new BidProject();
			
			bp.setUuid(uuid);
			
			// 项目信息
			bp.setAuditUnit(auditUnit);
			bp.setTrustOrgan(trustOrgan);
			bp.setServiceStartTime(serviceStartTime);
			bp.setServiceEndTime(serviceEndTime);
			bp.setServiceType(serviceType);
			bp.setProjectName(projectName);
			bp.setProjectSimpleName(projectSimpleName);
			
			// 客户信息
			bp.setUnitName(unitName);
			bp.setVocationType(vocationType);
			bp.setUnitEngName(unitEngName);
			bp.setHylx(hylx);
			bp.setRegister(register);
			bp.setCurName(curName);
			bp.setUnitSimpleName(unitSimpleName);
			
			// 标书信息
			bp.setEndDate(endDate);
			
			// 标书附件
			bp.setBidAttachId(bidAttachId);
			
			// 投标文件附件
			bp.setBidAttachFileId(bidAttachFileId);
			
			bp.setBidMember(bidMember);
			bp.setBidMemberName(bidMemberName);
			bp.setDuty(dutys);
			bp.setCreateId(userid);
			bp.setCreateName(userName);
			bp.setCreateDate(af.getCurrentDate());
			
			bp.setAuditorId("");
			bp.setAuditorName("");
			bp.setAuditStatus("未审核");
			
			bp.setAuditDate(auditDate);
			bp.setBidStatus("立项");
			bp.setIsGetBidProject("否");// 未中标
			
			BidProjectService bs = new BidProjectService(conn);
			
			// 如果是修改 
			if("update".equalsIgnoreCase(opt)){
				// 招投标信息
				bs.deleteBidProject(uuid);
			}
			
			// 添加
			bs.addBidProject(bp);
			
			// 添加 操作 才 发通知 到 参与人
			if("add".equalsIgnoreCase(opt)){
				// 发通知 到 参与人
				String[] bidMembers = bidMember.split(","); 
				
				PlacardService ps = new PlacardService(conn);
				
				for (int i = 0; i < bidMembers.length; i++) {
					
					PlacardTable placard = new PlacardTable();
					
					placard.setCaption(projectName);
					
					placard.setMatter(userName+"于"+af.getCurrentDate().substring(0,4)
							+"年"+af.getCurrentDate().substring(5,7)+"月"+af.getCurrentDate().substring(8,10)+"日安排你参加"
							+projectName+"项目投标工作，投标截止日期为"+af.getCurrentDate().substring(0,4)
							+"年"+af.getCurrentDate().substring(5,7)+"月"+af.getCurrentDate().substring(8,10)+"日，请尽快处理。");
					
					placard.setIsReversion(0);
					placard.setAddresser(userid);
					placard.setAddressee(bidMembers[i]);
					placard.setAddresserTime(af.getCurrentDate() + " " + af.getCurrentTime());
					placard.setIsRead(0);
	
					ps.AddPlacard(placard);
				}
			}
			String sql = "UPDATE `oa_business` SET state='"+state+"' WHERE autoid='"+autoId+"'";
			
			new DbUtil(conn).executeUpdate(sql); //修改状态
			response.sendRedirect(request.getContextPath()+"/bidProject.do?method=list&opt="+opt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 转到添加竞争对手
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goAddCompetitor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addCompetitor);
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");

		String userAuditOfficeName = userSession.getUserAuditOfficeName();
		
		
		
		ASFuntion af = new ASFuntion();
		
		String id = request.getParameter("id");
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 招投标信息
			BidProjectService bs = new BidProjectService(conn);
			bp = bs.getBidProject(id);
			
			// 竞争对手信息
			BidCompetitorService bcs = new BidCompetitorService(conn);
			List bidCompetorList = bcs.getBidCompetitorList(bp.getUuid());
			
			// 找到  投标文件
			String sqlStr = " select attachid,attachname,filesize from k_attachExt where indexid = '"+bp.getBidAttachFileId()+"' and updateuser = '"+bp.getCreateId()+"'";
			List attachList = bs.getListBySql(sqlStr);
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门数
			DbUtil du = new DbUtil(conn);
			
			String bidMember = bp.getBidMember();
			String bidMemberDepartmentId = "";
			String departmengId = "";
			if(bidMember!=null && !"".equals(bidMember)){
				String sql = " select departmentid from k_user where id=? ";
				String[] bidMembers = bidMember.split(",");
				for (int i = 0; i < bidMembers.length; i++) {
					departmengId = du.queryForString(sql, new Object[]{bidMembers[i]});
					if(bidMemberDepartmentId.indexOf(","+departmengId+",")<0){
						bidMemberDepartmentId = bidMemberDepartmentId + departmengId + "," ;
					}
				}
			}
			

			// 转 字符串 到 list
			List listBidMemeber = null;
			if(af.showNull(bp.getBidMember()).indexOf(",")>-1 && af.showNull(bp.getBidMemberName()).indexOf(",")>-1 && af.showNull(bp.getDuty()).indexOf(",")>-1){
				listBidMemeber = bs.toList(bp.getBidMember().split(","),bp.getBidMemberName().split(","),bp.getDuty().split(","));
			}else{
				listBidMemeber = new ArrayList();
			}
			
			
			model.addObject("listBidMemeber",listBidMemeber);
			
			
			model.addObject("bp",bp);
			model.addObject("attachList",attachList);
			model.addObject("attachListSize",attachList.size());
			model.addObject("bidCompetorList",bidCompetorList);
			model.addObject("listSize",bidCompetorList.size());
			model.addObject("bidMemberDepartmentId",bidMemberDepartmentId);
			model.addObject("userAuditOfficeName",userAuditOfficeName);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
	}
	
	
	
	/**
	 * 保存竞争对手
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveCompetitor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		String bidProjectId = request.getParameter("uuid");
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 竞争对手
			String[] bidCompetitor = request.getParameterValues("bidCompetitor");
			String[] bidCompetitorPrice = request.getParameterValues("bidCompetitorPrice");
			String[] bidMemberSuperiority = request.getParameterValues("bidMemberSuperiority");
			String[] bidMemberDisadvantaged = request.getParameterValues("bidMemberDisadvantaged");
			
			String bidCompetitors = "";
			
			BidCompetitorService bcs = new BidCompetitorService(conn);
			
			// 先删除原来的 竞争对手
			bcs.deleteByBidProjectId(bidProjectId);
			
			for (int i = 0; i < bidCompetitor.length; i++) {
				
				BidCompetitor bc = new BidCompetitor();
				
				bc.setUuid(UUID.randomUUID().toString());
				bc.setBidProjectId(bidProjectId);
				bc.setBidCompetitor(bidCompetitor[i]);
				bc.setBidCompetitorPrice(bidCompetitorPrice[i]);
				bc.setBidMemberSuperiority(bidMemberSuperiority[i]);
				bc.setBidMemberDisadvantaged(bidMemberDisadvantaged[i]);
				bc.setProperty(i+"");
				
				bidCompetitors = bidCompetitors + bidCompetitor[i] + ",";
				
				// 添加 竞争对手
				bcs.addBidCompetitor(bc);
			}
			
			// 中标人
			String getBidPerson = request.getParameter("getBidPerson");
			// 中标价
			String getBidPrice = request.getParameter("getBidPrice");
			// 招投标状态
			String bidStatus = request.getParameter("bidStatus");
			// 是否中标
			String isGetBidProject = request.getParameter("isGetBidProject");
			
			
			BidProject bp = new BidProject();
			
			bp.setUuid(bidProjectId);
			
			bp.setGetBidPerson(getBidPerson);
			bp.setGetBidPrice(getBidPrice);
			bp.setBidCompetitor(bidCompetitors);
			bp.setBidStatus(bidStatus);
			bp.setIsGetBidProject(isGetBidProject);
			
			BidProjectService bs = new BidProjectService(conn);
			
			bs.updateBidProjectAfter(bp);
		
			response.sendRedirect(request.getContextPath()+"/bidProject.do?method=list&opt=finish");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
	/**
	 * 转到上传 填写好的 招标书
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goAddAttach(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addAttach);
		
		ASFuntion af = new ASFuntion();
		
		String id = request.getParameter("id");
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			
			// 招投标信息
			BidProjectService bs = new BidProjectService(conn);
			bp = bs.getBidProject(id);
			
			// 竞争对手信息
			BidCompetitorService bcs = new BidCompetitorService(conn);
			List bidCompetorList = bcs.getBidCompetitorList(bp.getUuid());
			
			// 所有写标书的人上传标书 后 都通过 主键 关联 上传的标书文件然后 加 附件 表里面的 updateuser 字段
			bp.setBidAttachId(id);
			
			// 找到  投标文件
			String sqlStr = " select attachid,attachname,filesize from k_attachExt where indexid = '"+bp.getBidAttachFileId()+"' and updateuser = '"+bp.getCreateId()+"'";
			List attachList = bs.getListBySql(sqlStr);
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门数
			DbUtil du = new DbUtil(conn);
			
			String bidMember = bp.getBidMember();
			String bidMemberDepartmentId = "";
			String departmengId = "";
			if(bidMember!=null && !"".equals(bidMember)){
				String sql = " select departmentid from k_user where id=? ";
				String[] bidMembers = bidMember.split(",");
				for (int i = 0; i < bidMembers.length; i++) {
					departmengId = du.queryForString(sql, new Object[]{bidMembers[i]});
					if(bidMemberDepartmentId.indexOf(","+departmengId+",")<0){
						bidMemberDepartmentId = bidMemberDepartmentId + departmengId + "," ;
					}
				}
			}
			

			// 转 字符串 到 list
			List listBidMemeber = null;
			if(af.showNull(bp.getBidMember()).indexOf(",")>-1 && af.showNull(bp.getBidMemberName()).indexOf(",")>-1 && af.showNull(bp.getDuty()).indexOf(",")>-1){
				listBidMemeber = bs.toList(bp.getBidMember().split(","),bp.getBidMemberName().split(","),bp.getDuty().split(","));
			}else{
				listBidMemeber = new ArrayList();
			}
			
			model.addObject("bidCompetorList",bidCompetorList);
			model.addObject("listBidMemeber",listBidMemeber);
			model.addObject("listSize",bidCompetorList.size());
			
			
			model.addObject("bp",bp);
			model.addObject("attachList",attachList);
			model.addObject("attachListSize",attachList.size());
			model.addObject("bidMemberDepartmentId",bidMemberDepartmentId);
				
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
	}
	
	

	/**
	 * 上传标书
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveAttach(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String bidProjectId = request.getParameter("uuid");
		String bidAttachId = request.getParameter("bidAttachId");
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");

			BidProject bp = new BidProject();
			
			bp.setUuid(bidProjectId);
			
			bp.setBidAttachId(bidAttachId);
			
			BidProjectService bs = new BidProjectService(conn);
			
			// 修改 标书 附件 编号
			bs.updateBidAttachId(bp);
		
			response.sendRedirect(request.getContextPath()+"/bidProject.do?method=list&opt=uploadFile");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
	/**
	 * 转到审核
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goAudit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addAudit);
		
		ASFuntion af = new ASFuntion();
		
		String id = request.getParameter("id");
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 招投标信息
			BidProjectService bs = new BidProjectService(conn);
			bp = bs.getBidProject(id);
			
			// 竞争对手信息
			BidCompetitorService bcs = new BidCompetitorService(conn);
			List bidCompetorList = bcs.getBidCompetitorList(bp.getUuid());
			
			// 找到  投标文件
			String sqlStr = " select attachid,attachname,filesize from k_attachExt where indexid = '"+bp.getBidAttachFileId()+"' and updateuser = '"+bp.getCreateId()+"'";
			List attachList = bs.getListBySql(sqlStr);
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门数
			DbUtil du = new DbUtil(conn);
			
			String bidMember = bp.getBidMember();
			String bidMemberDepartmentId = "";
			String departmengId = "";
			if(bidMember!=null && !"".equals(bidMember)){
				String sql = " select departmentid from k_user where id=? ";
				String[] bidMembers = bidMember.split(",");
				for (int i = 0; i < bidMembers.length; i++) {
					departmengId = du.queryForString(sql, new Object[]{bidMembers[i]});
					if(bidMemberDepartmentId.indexOf(","+departmengId+",")<0){
						bidMemberDepartmentId = bidMemberDepartmentId + departmengId + "," ;
					}
				}
			}
			
			
			// 转 字符串 到 list
			List listBidMemeber = null;
			if(af.showNull(bp.getBidMember()).indexOf(",")>-1 && af.showNull(bp.getBidMemberName()).indexOf(",")>-1 && af.showNull(bp.getDuty()).indexOf(",")>-1){
				listBidMemeber = bs.toList(bp.getBidMember().split(","),bp.getBidMemberName().split(","),bp.getDuty().split(","));
			}else{
				listBidMemeber = new ArrayList();
			}
			
			model.addObject("listBidMemeber",listBidMemeber);
			
			model.addObject("bp",bp);
			model.addObject("attachList",attachList);
			model.addObject("attachListSize",attachList.size());
			model.addObject("bidCompetorList",bidCompetorList);
			model.addObject("listSize",bidCompetorList.size());
			model.addObject("bidMemberDepartmentId",bidMemberDepartmentId);
				
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
	}
	
	
	/**
	 * 审核
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView audit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String bidProjectId = request.getParameter("uuid");
		String param_opt = request.getParameter("param_opt");
		String auditStatus = request.getParameter("auditStatus");
		String reason = request.getParameter("reason");

		ASFuntion af = new ASFuntion();
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");

			BidProject bp = new BidProject();
			
			bp.setUuid(bidProjectId);
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String userId = userSession.getUserId();
			String userName = userSession.getUserName();
			
			bp.setAuditorId(userId);
			bp.setAuditorName(userName);
			bp.setAuditDate(af.getCurrentDate());
			bp.setAuditStatus(auditStatus);
			if("no".equalsIgnoreCase(param_opt)){
				bp.setReason(reason);
			}
			
			BidProjectService bs = new BidProjectService(conn);
			
			// 修改 标书 审核人 信息
			bs.updateBidAuditor(bp);
		
			// 添加 操作 才 发通知 到 参与人
			if("no".equalsIgnoreCase(param_opt)){
				// 发通知 到 参与人
				BidProject bps = bs.getBidProject(bidProjectId);
				String[] bidMembers = bps.getBidMember().split(","); 
				
				PlacardService ps = new PlacardService(conn);
				
				for (int i = 0; i < bidMembers.length; i++) {
					
					PlacardTable placard = new PlacardTable();
					
					placard.setCaption(bps.getProjectName());
					
					placard.setMatter(userName+"于"+af.getCurrentDate().substring(0,4)
							+"年"+af.getCurrentDate().substring(5,7)+"月"+af.getCurrentDate().substring(8,10)+"日安排你参加"
							+bps.getProjectName()+"项目投标工作，投标截止日期为"+af.getCurrentDate().substring(0,4)
							+"年"+af.getCurrentDate().substring(5,7)+"月"+af.getCurrentDate().substring(8,10)+"日，出于"+reason+"的原因，审核未通过，请尽快处理。");
					
					placard.setIsReversion(0);
					placard.setAddresser(userId);
					placard.setAddressee(bidMembers[i]);
					placard.setAddresserTime(af.getCurrentDate() + " " + af.getCurrentTime());
					placard.setIsRead(0);
	
					ps.AddPlacard(placard);
				}
			}
			response.sendRedirect(request.getContextPath()+"/bidProject.do?method=list&opt=audit");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
	
	/**
	 * 得到审核状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAuditStatus(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		String id = request.getParameter("id");
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			// 发通知 到 参与人
			BidProjectService bs = new BidProjectService(conn);
			BidProject bps = bs.getBidProject(id);
			
			System.out.println(this.getClass()+"       id="+id+"    auditStatus="+bps.getAuditStatus());
			
			if("通过".equals(bps.getAuditStatus())){
				out.write("Y");
			}else{
				out.write("N");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	

	/**
	 * 得到招投标截止日期
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getEndDate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		String id = request.getParameter("id");
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			// 发通知 到 参与人
			BidProjectService bs = new BidProjectService(conn);
			BidProject bps = bs.getBidProject(id);
			
			out.write(bps.getEndDate());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 去立项
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private final String addPrject ="customerProject/AddandEdit.jsp";
	public ModelAndView goProjectApproval(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addPrject);
		
		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		BidProject bp = null;
		try {
			
			conn = new DBConnect().getConnect("");
 
			// 招投标信息
			BidProjectService bs = new BidProjectService(conn);
			bp = bs.getBidProject(id);
			
			model.addObject("isGetBidProject",bp.getIsGetBidProject());
			model.addObject("customerId",bp.getAuditUnit());
			model.addObject("bidProjectId",bp.getUuid());
		 
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
	}
}
