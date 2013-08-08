package com.matech.audit.work.waresStock;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.proclamation.ProclamationService;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.service.waresStock.WaresStockService;
import com.matech.audit.service.waresStock.model.WaresPrucVO;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockDetails;
import com.matech.audit.service.waresStock.model.WaresStramFlow;
import com.matech.audit.service.waresStock.model.WaresStream;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

/**
 * @author ymm
 * 物品库存管理
 *
 */
public class WaresStockAction extends MultiActionController{

	
	private static final String LIST = "waresStock/List.jsp";
	private static final String OFFICESTOCKLIST = "waresStock/officeStockList.jsp"; //办公物品库存查询：给普通人用的；
	private static final String ADD = "waresStock/AddandEdit.jsp";   //新增
	private static final String UPDATE = "waresStock/AddandEdit.jsp"; //修改
	private static final String STOCKREGISTER = "waresStock/stockRegister.jsp"; //已有物品入库登记  (跳转)
	private static final String STOCKRETURN = "waresStock/stockRegister.jsp"; //已有物品归还登记 (跳转)
	private static final String STOCKSCRAP = "waresStock/stockRegister.jsp"; //已有物品报废登记  (跳转)
	private static final String ORDINARYPEOPLEAPPLYSKIP = "waresStock/apply.jsp"; //申领
	private static final String VIEWSKIP = "waresStock/view.jsp"; //查看详情
	private static final String MYSTOCKLIST = "waresStock/myStockList.jsp"; //我的物品
	private static final String DEPARTMENTAUDIT = "waresStock/departmentAuditList.jsp"; //部门list
	private static final String ORGANIZATIONAUDIT = "waresStock/organizationAuditList.jsp"; //机构list
	private static final String AUDIT = "waresStock/audit.jsp"; //审批
	private static final String CANCELAUDIT = "waresStock/cancelAudit.jsp"; //报废审批详情
	private static final String CANCELAUDITLISTt = "waresStock/cancelAuditList.jsp"; //报废list
	private static final String APPLYINENTORYLIST = "waresStock/applyInventoryList.jsp"; //物品领用清单查询
	private static final String WARESSTATISTICELIST = "waresStock/waresStatisticeList.jsp"; //物品统计查询
	private JbpmTemplate jbpmTemplate;
	
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
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response){
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(LIST);
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
			conn = new DBConnect().getConnect();
			
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000668";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty pp = new DataGridProperty();
			String sql = "SELECT `uuid`,a.`name`,`remark`,`type`,`coding`,`unitUnit`,`lowestWarnStock`,kd1.`name` as pro_name, \n"
						+"`highestWarnStock`,`usableStock`,`scrappedStock`,`putin_time`,b.departname AS `departmentId`,price1,case a.need_check_ind when 'true' then '是' when 'false' then '否' else '' end as a_need_check_ind \n"
						+"FROM `k_waresstock` a \n"
						+"LEFT JOIN k_department b ON a.departmentId=b.autoId \n"
						+"left join k_dic kd1 on a.pro_type=kd1.value and kd1.ctype='物品属性类型' \n"
						+"left join (select waresStockId,sum(quantity) as quantity1 ,sum(quantity * price) as allprice1,sum(quantity * price)/sum(quantity) as price1 "
						+" from k_waresstockdetails where ctype = '入库' group by waresStockId) a1 on a.uuid=a1.waresStockId \n"
						+"WHERE 1=1 and (a.departmentId='"+departmentid+"' or a.departmentid in ("+departments+")) ${name} ${remark} ${type} ${coding} ${lowestStock} ${lowestWarnStock} ${highestWarnStock}";
	
			pp.setTableID("waresList");
			pp.setPageSize_CH(50);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("name");
			pp.setDirection("asc");
			pp.setColumnWidth("10,10,10,10,10,10,10,10,10,10");
			pp.setPrintEnable(true);	//启用打印
			pp.setPrintTitle("库存管理列表");
			pp.setSQL(sql);
			
		    pp.addColumn("物品名称", "name"); 
		    pp.addColumn("入库日期", "putin_time");
		    pp.addColumn("描述", "remark");
		    //pp.addColumn("类型", "type");
		    pp.addColumn("所属库", "pro_name");
		    pp.addColumn("是否需要审核","a_need_check_ind");
		    pp.addColumn("编码", "coding");
		    pp.addColumn("计量单位", "unitUnit");
		    pp.addColumn("折算单价", "price1","showMoney");
		    //pp.addColumn("最低库存", "lowestStock");
		    pp.addColumn("最低警告库存", "lowestWarnStock");
		    pp.addColumn("最高警告库存", "highestWarnStock");
		    pp.addColumn("当前可用的库存", "usableStock");
		    pp.addColumn("当前报废的库存", "scrappedStock");
		    pp.addColumn("所属部门", "departmentId");
		    
		   
		    pp.addSqlWhere("name", " and a.name like '%${name}%'");
		    pp.addSqlWhere("remark", "and a.remark like '%${remark}%'");
		    pp.addSqlWhere("type", " and a.type like '%${type}%'");
		    pp.addSqlWhere("coding", " and a.coding like '%${coding}%'");
		    pp.addSqlWhere("lowestStock", " and a.lowestStock like '%${lowestStock}%'");
		    pp.addSqlWhere("lowestWarnStock", " and a.lowestWarnStock like '%${lowestWarnStock}%'");
		    pp.addSqlWhere("highestWarnStock", " and a.highestWarnStock like '%${highestWarnStock}%'");
		    
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			
		}
		
		return modelAndView;
	}
	
	/**
	 * 新增
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response) throws IOException{
		WebUtil webUtil=new WebUtil(request, response);
		/*
		String name = request.getParameter("name");
		String remark = request.getParameter("remark");
		String type = request.getParameter("type");
		String coding = request.getParameter("coding");
		String unitUnit = request.getParameter("unitUnit");
		String lowestStock = request.getParameter("lowestStock");
		String lowestWarnStock = request.getParameter("lowestWarnStock");
		String highestWarnStock = request.getParameter("highestWarnStock");
		String departmentId = request.getParameter("departmentId");
		
		WaresStock waresStock = new WaresStock();
		waresStock.setUuid(UUID.randomUUID().toString());
		waresStock.setName(name);
		waresStock.setRemark(remark);
		waresStock.setType(type);
		waresStock.setCoding(coding);
		waresStock.setUnitUnit(unitUnit);
		waresStock.setLowestStock(lowestStock);
		waresStock.setLowestWarnStock(lowestWarnStock);
		waresStock.setHighestWarnStock(highestWarnStock);
		waresStock.setDepartmentId(departmentId);
		*/
		Connection conn = null;
		WaresStock waresStock =null;
		DbUtil dbUtil=null;
		try {
			waresStock = webUtil.evalObject(WaresStock.class);
			conn = new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			WaresStockService waresStockService = new WaresStockService(conn);
			waresStock.setUuid(UUID.randomUUID().toString());
			dbUtil.insert(waresStock);
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			
			DbUtil.close(conn);
			
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=list");
		}
		
		return null;
	}

	/**
	 * 新增跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ADD);
		
		return modelAndView ;
	}

	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATE);
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		DbUtil dbUtil=null;
		try {
			
			conn = new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock =dbUtil.load(WaresStock.class, uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}

	/**
	 * 修改
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView update(HttpServletRequest request,HttpServletResponse response) throws IOException{
		/*
		String name = request.getParameter("name");
		String uuid = request.getParameter("uuid");
		String remark = request.getParameter("remark");
		String type = request.getParameter("type");
		String coding = request.getParameter("coding");
		String unitUnit = request.getParameter("unitUnit");
		String lowestStock = request.getParameter("lowestStock");
		String lowestWarnStock = request.getParameter("lowestWarnStock");
		String highestWarnStock = request.getParameter("highestWarnStock");
		String departmentId = request.getParameter("departmentId");
		*/
		WebUtil webUtil=new WebUtil(request, response);
		/*
		WaresStock waresStock = new WaresStock();
		waresStock.setUuid(uuid);
		waresStock.setName(name);
		waresStock.setRemark(remark);
		waresStock.setType(type);
		waresStock.setCoding(coding);
		waresStock.setUnitUnit(unitUnit);
		waresStock.setLowestStock(lowestStock);
		waresStock.setLowestWarnStock(lowestWarnStock);
		waresStock.setHighestWarnStock(highestWarnStock);
		waresStock.setDepartmentId(departmentId);
		*/
		WaresStock waresStock = new WaresStock();
	    waresStock=webUtil.evalObject(waresStock);
		
		Connection conn = null;
		DbUtil dbUtil=null;
		try {
		
			conn = new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			//WaresStockService waresStockService = new WaresStockService(conn);
			
			//waresStockService.update(waresStock);
			waresStock=dbUtil.load(WaresStock.class,waresStock.getUuid());
			waresStock=webUtil.evalObject(waresStock);
		    dbUtil.update(waresStock);
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			
			DbUtil.close(conn);
			
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=list");
		}
		
		return null;
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			boolean result = stockService.delete(uuid);

			System.out.println("是否删除成功："+result);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
			
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=list");
		}
		
		return null ;
	}
	
	/**
	 * 已有物品"入库"登记 (跳转)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView stockRegisterSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(STOCKREGISTER);
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock = stockService.getWaresStock(uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
			modelAndView.addObject("ctype","入库");
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}
	
	/**
	 * 已有物品"归还"登记(跳转)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView stockReturnSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(STOCKRETURN);
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock = stockService.getWaresStock(uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
			modelAndView.addObject("ctype","归还");
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}
	
	/**
	 * 已有物品"报废"登记(跳转)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView stockScrapSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(STOCKSCRAP);
		String uuid = request.getParameter("uuid");
		String paramSkip = request.getParameter("paramSkip");//有值就是从我的物品管理跳转过来
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock = stockService.getWaresStock(uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
			modelAndView.addObject("ctype","报废");
			modelAndView.addObject("paramSkip",paramSkip); //从我的物品管理跳转过来
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}
	
	/**
	 * 已有物品登记 (新增)
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView stockRegister(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String waresStockId = request.getParameter("waresStockId");
		String ctype = request.getParameter("ctype");
		String quantity = request.getParameter("quantity");
		String price = request.getParameter("price");
		String suppliers = request.getParameter("suppliers");
		String userId = request.getParameter("userId");
		String paramSkip = request.getParameter("paramSkip");//有值就是从我的物品管理跳转过来
	    String remark=request.getParameter("remark");
	    String scrap_time=request.getParameter("scrap_time");
	    String pruc_id=request.getParameter("pruc_id");
		Connection conn = null;
		WaresPrucVO waresPrucVO=null;
		DbUtil dbUtil=null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			conn = new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStockDetails waresStockDetails = new WaresStockDetails();  
			waresStockDetails.setCtype(ctype);
			
			if("归还".equals(ctype) || "报废".equals(ctype)){
				waresStockDetails.setUserId(userId);
			}else{				
				waresStockDetails.setUserId(userSession.getUserId());
			}
			
			String uuid = UUID.randomUUID().toString();
			waresStockDetails.setUuid(uuid);
			waresStockDetails.setQuantity(quantity);
			waresStockDetails.setPrice(price);
			waresStockDetails.setSuppliers(suppliers);
			waresStockDetails.setWaresStockId(waresStockId);
			waresStockDetails.setRemark(remark);
			waresStockDetails.setScrap_time(scrap_time);
			boolean result = stockService.addWaresStockDetails(waresStockDetails);
			
			if(result){
				
				if("入库".equals(ctype) || "归还".equals(ctype)){
					
					stockService.updateUsableStock(waresStockId, quantity,"增加");
				
				}else if("报废".equals(ctype)){
					
					//启动流程						
					this.startFlow(request, response,"waresCancelFlow", uuid);
					
					String sql = "UPDATE k_waresstockdetails SET `status`  = '已发起' WHERE `uuid` = '"+uuid+"'";
					stockService.UpdateValueBySql(sql); //修改状态
				}
				
				if("入库".equals(ctype)){
					waresPrucVO=dbUtil.load(WaresPrucVO.class, pruc_id);
					waresPrucVO.setState("已入库");
					waresPrucVO.setA_time(StringUtil.getCurDateTime());
					waresPrucVO.setReal_quantity(Integer.parseInt(quantity));

					dbUtil.update(waresPrucVO);
				}
			}
			
			//有值就是从我的物品管理跳转过来
			if(!"".equals(paramSkip)){
				response.sendRedirect( request.getContextPath()+"/waresStock.do?method=myWaresList");

			}else{
				
				response.sendRedirect( request.getContextPath()+"/waresStock.do?method=list");
			}
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return null ;
	}
	
	/**
	 * 同意 已有物品"报废"申请
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView stocScrapkAgree(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		
		//String pdId = request.getParameter("pdId");
		//String waresStockId = request.getParameter("waresStockId"); //物品ID 可以正常使用
		String taskId = request.getParameter("taskId");
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect();
			WaresStockService stockService = new WaresStockService(conn);
			ProcessFormService processFormService = new ProcessFormService(conn);
			
			WaresStockDetails waresStockDetails = stockService.getCNWaresStockDetails(uuid);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
			String pdId = jbpmTemplate.getProcessInstanceId(taskId);
			
			ProcessForm processForm = new ProcessForm();
			processForm.setKey("审批状态");
			processForm.setValue("已通过");
			processForm.setDealUserId(userSession.getUserId());
			processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
			processForm.setProperty("");
			processForm.setProcessInstanseId(pdId);
			processForm.setNodeName(activeName);
			processFormService.add(processForm);// 添加记录
			
			String sql = "UPDATE k_waresstockdetails SET `status`  = '已审批' WHERE `uuid` = '"+uuid+"'";
			stockService.UpdateValueBySql(sql); //修改状态
			
			sql = "UPDATE j_waresstreamprocss SET `state`  = '"+activeName+"' WHERE `uuid` = '"+uuid+"'";
			stockService.UpdateValueBySql(sql); //修改状态
			
			//物品库存 修改
			//stockService.updateUsableAndScrapped(waresStockDetails.getWaresStockId(), waresStockDetails.getQuantity());
			
			jbpmTemplate.completeTask(taskId);
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}finally{
			DbUtil.close(conn);
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=cancelAuditList");
		}
		
		return null ;
	}

	/**
	 * 申领跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView applySkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(STOCKSCRAP);
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock = stockService.getWaresStock(uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
			modelAndView.addObject("ctype","报废");
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}
	
	/**
	 * 申领 物品
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView applyWares(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		ModelAndView modelAndView = new ModelAndView(STOCKSCRAP);
		
		String[] waresStockId = request.getParameterValues("waresStockId");
		//String[] quantity = request.getParameterValues("quantity");
		String[] remark = request.getParameterValues("remark");
		String[] status = request.getParameterValues("status");
		String applyDate = request.getParameter("applyDate");
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStream waresStream = new WaresStream();
			
			waresStream.setUserId(userSession.getUserId());
			waresStream.setApplyDate(applyDate);
			waresStream.setStatus("未发起");
			
			for (int i = 0; i < waresStockId.length; i++) {
				
				if(!"".equals(waresStockId[i]) &&  !"".equals(status[i])){
					String uuid = UUID.randomUUID().toString();
					waresStream.setUuid(uuid);
					waresStream.setWaresStockId(waresStockId[i]);
					String  quantity = asf.showNull(request.getParameter("quantity"+status[i]));
					waresStream.setQuantity(quantity);
					waresStream.setApplyReason(remark[i]);
					
					if(!quantity.equals("")){						
						stockService.addWaresStream(waresStream);
						
						//启动流程						
						this.startFlow(request, response,"waresApplyFlow", uuid);
					}
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=officeStockList");
		}
		
		return modelAndView ;
	}
	
	/**
	 * 普通人申领跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ordinaryPeopleApplySkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ORDINARYPEOPLEAPPLYSKIP);
		String uuids = request.getParameter("uuids");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			//Date date = new Date();
			ASFuntion asf = new ASFuntion();
			String applyDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
			
			WaresStockService stockService = new WaresStockService(conn);
			List<WaresStock>  listWaresStock = new ArrayList();  
			if(!uuids.equals("")){
				String [] uuid = uuids.split(",");
				for (int i = 0; i < uuid.length; i++) {				
					
					WaresStock waresStock = stockService.getWaresStock(uuid[i]);
					String quantity = stockService.getAvailableQuantity(uuid[i]);
					waresStock.setCoding(quantity); //先临时放在这里
					listWaresStock.add(waresStock);
				}
			}
			modelAndView.addObject("listWaresStock",listWaresStock);
			modelAndView.addObject("userSession",userSession);
			modelAndView.addObject("applyDate",applyDate);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}
	
	/**
	 * 办公物品库存查询：给普通人用的；
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView officeStockList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(OFFICESTOCKLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
		DataGridProperty pp = new DataGridProperty();
		try {	
			String sql = "SELECT`uuid`,`name`,`remark`,`type`,`coding`,`unitUnit`,`lowestStock`,`lowestWarnStock`, \n"
						+"`highestWarnStock`,`usableStock`,`scrappedStock`,b.departname AS `departmentId` \n"
						+"FROM `k_waresstock` a \n"
						+"LEFT JOIN k_department b ON a.departmentId=b.autoId \n"
						+"WHERE 1=1 AND b.autoid IN (SELECT autoid FROM k_department \n"
						+"WHERE areaid =(SELECT areaid FROM k_department WHERE autoid = '"+departmentid+"'))  ${name} ${remark} ${type} ${coding} ${lowestStock} ${lowestWarnStock} ${highestWarnStock}";
	
			pp.setCustomerId("");
			pp.setTableID("list");
			pp.setOrderBy_CH("name");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);	//打开dg打印
			pp.setPrintTitle("库存查询与申领列表");
			pp.setSQL(sql);
			pp.setInputType("checkbox");
			
			pp.setColumnWidth("15,15,7,10,7,7,7,10");
			
		    pp.addColumn("物品名称", "name");
		    pp.addColumn("描述", "remark");
		    pp.addColumn("类型", "type");
		    pp.addColumn("编码", "coding");
		    pp.addColumn("计量单位", "unitUnit");
		    pp.addColumn("最低库存", "lowestStock");
		   // pp.addColumn("最低警告库存", "lowestWarnStock");
		   // pp.addColumn("最搞警告库存", "highestWarnStock");
		    pp.addColumn("当前可用的库存", "usableStock");
		    //pp.addColumn("当前报废的库存", "scrappedStock");
		    pp.addColumn("所属部门", "departmentId");
		    
		    pp.addSqlWhere("name", " and a.name like '%${name}%'");
		    pp.addSqlWhere("remark", "and a.remark like '%${remark}%'");
		    pp.addSqlWhere("type", " and a.type like '%${type}%'");
		    pp.addSqlWhere("coding", " and a.coding like '%${coding}%'");
		    pp.addSqlWhere("lowestStock", " and a.lowestStock like '%${lowestStock}%'");
		    pp.addSqlWhere("lowestWarnStock", " and a.lowestWarnStock like '%${lowestWarnStock}%'");
		    pp.addSqlWhere("highestWarnStock", " and a.highestWarnStock like '%${highestWarnStock}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 查看物品详情跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView viewSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(VIEWSKIP);
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			WaresStock waresStock = stockService.getCNWaresStock(uuid);
			
			modelAndView.addObject("waresStock",waresStock);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}

	/**
	 * 我的物品
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView myWaresList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(MYSTOCKLIST);
		
		DataGridProperty pp = new DataGridProperty(); //申请
		DataGridProperty applyPp = new DataGridProperty();//申领
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		try {	
			String sql = "SELECT a.`uuid`,b.name AS `waresStockId`,c.name AS `userId`,`quantity`,`applyDate`, \n"
						+"`applyReason`,`approveUserId`,`approveDate`,`approveQuantity`,`approveIdea`,`status` \n"
						+"FROM `k_waresstream` a  \n"
						+"LEFT JOIN `k_waresstock` b ON a.`waresStockId` = b.uuid \n"
						+"LEFT JOIN k_user c  ON a.`userId` = c.`id` \n"
						+"WHERE a.status = '待审批' AND a.`userId`='"+userSession.getUserId()+"'"
						+" ${name} ${quantity} ${applyDate} ${applyReason}";
	
			pp.setCustomerId("");
			pp.setTableID("myWaresApplyList");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);	//打开dg打印
			pp.setPrintTitle("我申请的物品列表");
			pp.setSQL(sql);
			pp.setInputType("radio");
			
			pp.setColumnWidth("15,7,15,15");
			
		    pp.addColumn("物品名称", "waresStockId");
		    pp.addColumn("数量", "quantity");
		    pp.addColumn("申请日期", "applyDate");
		    pp.addColumn("申请原因", "applyReason");
		    
		    pp.addSqlWhere("name", " and b.name like '%${name}%'");
		    pp.addSqlWhere("quantity", " and a.quantity like '%${quantity}%'");
		    pp.addSqlWhere("applyDate", " and a.applyDate like '%${applyDate}%'");
		    pp.addSqlWhere("applyReason", " and a.applyReason like '%${applyReason}%'");
		    
		    
		    String applySql = "SELECT a.waresStockId,b.name,c.name AS `userId`,`quantity`,`applyDate`, \n"
						+"`applyReason`,`approveUserId`,`approveDate`,`approveQuantity`,`approveIdea`,`status` \n"
						+"FROM `k_waresstream` a  \n"
						+"LEFT JOIN `k_waresstock` b ON a.`waresStockId` = b.uuid \n"
						+"LEFT JOIN k_user c  ON a.`userId` = c.`id` \n"
						+"WHERE a.status = '已审批' AND a.`userId`='"+userSession.getUserId()+"'"
						+" ${name} ${quantity} ${applyDate} ${applyReason}";

		    applyPp.setCustomerId("");
		    applyPp.setTableID("applyNeckList");
			applyPp.setOrderBy_CH("applyDate");
			applyPp.setDirection("asc");
			applyPp.setWhichFieldIsValue(1);
			applyPp.setPrintEnable(true);	//打开dg打印
			applyPp.setPrintTitle("我领用物品的列表");
			applyPp.setSQL(applySql);
			applyPp.setInputType("radio");
			
			applyPp.setColumnWidth("15,7,15,15");
			
		    applyPp.addColumn("物品名称", "name");
		    applyPp.addColumn("数量", "quantity");
		    applyPp.addColumn("申请日期", "applyDate");
		    applyPp.addColumn("申请原因", "applyReason");
		    
		    applyPp.addSqlWhere("name", " and b.name like '%${name2}%'");
		    applyPp.addSqlWhere("quantity", " and a.quantity like '%${quantity2}%'");
		    applyPp.addSqlWhere("applyDate", " and a.applyDate like '%${applyDate2}%'");
		    applyPp.addSqlWhere("applyReason", " and a.applyReason like '%${applyReason2}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(DataGrid.sessionPre + applyPp.getTableID(), applyPp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 删除申请的物品
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView deleteWaresStream(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String uuid = request.getParameter("uuid");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
		
			WaresStockService stockService = new WaresStockService(conn);
			
			stockService.deleteWaresStream(uuid);
			
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
			response.sendRedirect( request.getContextPath()+"/waresStock.do?method=myStockList");
		}
		
		return null ;
	}

	/**
	 * 部门审批list
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deparementAuditList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(DEPARTMENTAUDIT);
		
		DataGridProperty pp = new DataGridProperty(); 
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		try {	
		    
		    String applySql ="select a.*, c.`uuid`,d.name,d.type,e.`departname`,c.`waresStockId`,c.`userId`, \n"
							+"c.`quantity`,c.`applyDate`,c.`applyReason`,c.`approveUserId`, \n"
							+"c.`approveDate`,c.`approveQuantity`,c.`approveIdea`,c.`status` from \n"
							+"(  \n"
							+"	SELECT DISTINCT a.DBID_,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_, GROUP_CONCAT(c.userID_ ) AS userID_ \n"
							+"	FROM jbpm4_task a  \n"
							+"	INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
							+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
							+"	GROUP BY a.EXECUTION_ID_ \n"
							+" ) a "
							+"INNER JOIN `j_waresstreamprocss` b ON a.id_ = b.ProcessInstanceId "
							+" inner join k_waresstream c  on b.uuid = c.uuid "
							+"LEFT JOIN `k_waresstock` d ON c.waresStockId =d.uuid \n"
							+"LEFT JOIN  k_department e ON d.departmentId = e.autoId \n"
							+"left join k_user f on c.userId  = f.id "
							//+"WHERE departname <> '555555' and c.status = '已发起' and CONCAT(a.userID_,',') like '%"+userSession.getUserId()+",%'  \n"
							+"WHERE (e.level0 <>1 OR e.level0 IS NULL)  and c.status = '已发起' and CONCAT(a.userID_,',') like '%"+userSession.getUserId()+",%'  \n"
							+" ${name} ${type} ${applyDate} ${userName}";

		    pp.setCustomerId("");
		    pp.setTableID("deparementAuditList");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);	//打开dg打印
			pp.setPrintTitle("领用至部门列表");
			pp.setSQL(applySql);
			pp.setInputType("checkbox");
			
			pp.setColumnWidth("15,7,7,15,15");
			
		    pp.addColumn("物品名称", "name");
		    pp.addColumn("物品类型", "type");
		    pp.addColumn("数量", "quantity");
		    pp.addColumn("申请日期", "applyDate");
		    pp.addColumn("申请原因", "applyReason");
		    
		    pp.addSqlWhere("name", " and b.name like '%${name}%'");
		    pp.addSqlWhere("type", " and b.type like '%${type}%'");
		    pp.addSqlWhere("applyDate", " and a.applyDate like '%${applyDate2}%'");
		    pp.addSqlWhere("userName", " and d.name like '%${userId}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 机构审批
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView organizationAuditList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ORGANIZATIONAUDIT);
		
		DataGridProperty pp = new DataGridProperty(); 
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		try {	
		    
		    String applySql = "select a.*, c.`uuid`,d.name,d.type,e.`departname`,c.`waresStockId`,c.`userId`, \n"
							+"c.`quantity`,c.`applyDate`,c.`applyReason`,c.`approveUserId`, \n"
							+"c.`approveDate`,c.`approveQuantity`,c.`approveIdea`,c.`status` from \n"
							+"(  \n"
							+"	SELECT DISTINCT a.DBID_,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_, GROUP_CONCAT(c.userID_ ) AS userID_ \n"
							+"	FROM jbpm4_task a  \n"
							+"	INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
							+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
							+"	GROUP BY a.EXECUTION_ID_ \n"
							+" ) a "
							+"INNER JOIN `j_waresstreamprocss` b ON a.id_ = b.ProcessInstanceId "
							+" inner join k_waresstream c  on b.uuid = c.uuid "
							+"LEFT JOIN `k_waresstock` d ON c.waresStockId =d.uuid \n"
							+"LEFT JOIN  k_department e ON d.departmentId = e.autoId \n"
							+"left join k_user f on c.userId  = f.id "
							//+"WHERE  d.departmentId = '555555' and c.status = '已发起' and CONCAT(a.userID_,',') like '%"+userSession.getUserId()+",%'   \n"
							+"WHERE  e.level0 ='1' and c.status = '已发起' and CONCAT(a.userID_,',') like '%"+userSession.getUserId()+",%'   \n"
							+" ${name} ${type} ${applyDate} ${userName}";

		    pp.setCustomerId("");
		    pp.setTableID("organizationAuditList");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setPrintEnable(true);	//打开dg打印
			pp.setPrintTitle("领用至机构列表");
			pp.setSQL(applySql);
			pp.setInputType("checkbox");
			
			pp.setColumnWidth("15,7,7,15,15");
			
		    pp.addColumn("物品名称", "name");
		    pp.addColumn("物品类型", "type");
		    pp.addColumn("数量", "quantity");
		    pp.addColumn("申请日期", "applyDate");
		    pp.addColumn("申请原因", "applyReason");
		    
		    pp.addSqlWhere("name", " and b.name like '%${name}%'");
		    pp.addSqlWhere("type", " and b.type like '%${type}%'");
		    pp.addSqlWhere("applyDate", " and a.applyDate like '%${applyDate2}%'");
		    pp.addSqlWhere("userName", " and d.name like '%${userId}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
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
		String taskIds = request.getParameter("uuids");
		String ctype = request.getParameter("ctype");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			WaresStockService stockService = new WaresStockService(conn);
			Date date = new Date();
			ASFuntion asf = new ASFuntion();
			String applyDate = asf.getCurrentDate()+" " +asf.getCurrentTime();
			
			String[] taskId = taskIds.split(",");

			String uuids = ""; 
			String pdIds = "";
			for (int i = 0; i < taskId.length; i++) {
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId[i]);
				pdIds += pdid + ",";
				String sql = "select uuid from j_waresstreamprocss where ProcessInstanceId='"+pdid+"'";
				uuids += stockService.getValueBySql(sql); //得到公告ID
			}
			
			pdIds = pdIds.substring(0,pdIds.length()-1);
			List<WaresStream>  listWaresStream = new ArrayList();  
			if(!uuids.equals("")){
				String [] uuid = uuids.split("@`@");
				for (int i = 0; i < uuid.length; i++) {				
					
					WaresStream waresStream = stockService.getCNwaresStream(uuid[i]);
					
					//String areQuantity = stockService.getAvailableQuantity(waresStream.getWaresStockId()); //在途(正在申请的库存)
					WaresStock waresStock =  stockService.getCNWaresStock(waresStream.getWaresStockId()); //当前库存数量
					//String quantity = (Integer.parseInt(waresStock.getUsableStock()) - Integer.parseInt(areQuantity))+""; 
					waresStream.setApproveQuantity(waresStock.getType());//先临时放在这里
					waresStream.setStatus(waresStock.getUsableStock()); //先临时放在这里
					
					listWaresStream.add(waresStream);
				}
			}
			uuids = uuids.replace("@`@", ",");
			uuids = uuids.substring(0,uuids.length()-1);
			modelAndView.addObject("listWaresStream",listWaresStream);
			modelAndView.addObject("userSession",userSession);
			modelAndView.addObject("applyDate",applyDate);
			modelAndView.addObject("ctype",ctype);
			modelAndView.addObject("pdIds",pdIds);
			modelAndView.addObject("taskIds",taskIds);
			modelAndView.addObject("uuids",uuids);
			
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return modelAndView ;
	}

	/**
	 * 机构与部门 审批完成
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditSave(HttpServletRequest request,HttpServletResponse response){
		
		String[] uuid = request.getParameterValues("uuid"); // 物品ID
		String  uuids = request.getParameter("uuids");  //物品申请ID
		String[] status = request.getParameterValues("status");
		String approveDate = request.getParameter("approveDate");
		String ctype = request.getParameter("ctype");
		
		String taskIds = request.getParameter("taskIds");
		//String pdIds = request.getParameter("pdIds");  页面传过来的
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			WaresStockService stockService = new WaresStockService(conn);
			ProcessFormService processFormService = new ProcessFormService(conn);
			String[] waresApplyId = uuids.split(",");
			for (int i = 0; i < uuid.length; i++) {				
				
				String approveQuantity = request.getParameter("approveQuantity"+status[i]);
				
				WaresStream waresStream = stockService.getCNwaresStream(uuid[i]);
				
				waresStream.setApproveUserId(userSession.getUserId());
				waresStream.setApproveQuantity(approveQuantity);
				waresStream.setApproveDate(approveDate);
				waresStream.setStatus("已审批");
				boolean result = stockService.updateWaresStream(waresStream);
				
				if(result){
					stockService.updateUsableStock(waresStream.getWaresStockId(), approveQuantity,"减少");
				}
				
			}
			ProcessForm processForm = new ProcessForm();
			processForm.setKey("审批状态");
			processForm.setValue("已通过");
			processForm.setDealUserId(userSession.getUserId());
			processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
			processForm.setProperty("");
			
			
			String[] teskId = taskIds.split(",");
			for (int j = 0; j < teskId.length; j++) {
				String activeName = jbpmTemplate.getActivityName(teskId[j]); // 获取当前节点
				String pdId = jbpmTemplate.getProcessInstanceId(teskId[j]);
				
				processForm.setProcessInstanseId(pdId);
				processForm.setNodeName(activeName);
				processFormService.add(processForm);// 添加记录
				
				
				String sql = "UPDATE j_waresstreamprocss SET `state`  = '"+activeName+"' WHERE `uuid` = '"+waresApplyId[j]+"'";
				stockService.UpdateValueBySql(sql); //修改状态
				
			}
			
			
			if("bumen".equals(ctype)){
				response.sendRedirect( request.getContextPath()+"/waresStock.do?method=deparementAuditList");
			}else if("jigou".equals(ctype)){
				response.sendRedirect( request.getContextPath()+"/waresStock.do?method=organizationAuditList");
			}
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}finally{
			
			DbUtil.close(conn);
		}
		
		return null ;
	}
	
	/**
	 * 启动流程
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView startFlow (HttpServletRequest request, HttpServletResponse response,String flowKey ,String uuid) throws Exception{
		// 获取登录的用户
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
				conn = new DBConnect().getConnect();

				WaresStockService waresStockService = new WaresStockService(conn);
				Map<String, String> startMap = new HashMap<String, String>();
				startMap.put("applyUserId", userId);
		
				String sql = "select processDefinitionId from j_processdeploy where processKey='"+flowKey+"' " ;
				String pdIds = waresStockService.getValueBySql(sql);
				String[] paId = pdIds.split("@`@");
				
				String processDefinitionId = paId[0].toString() ;
				
				String judgeAuditUser = "";
				if("waresCancelFlow".equals(flowKey)){

					  sql = "SELECT b.rolename FROM k_userrole a \n"
							+"LEFT JOIN k_role b ON a.rid = b.id \n"
							+"WHERE a.userid = '"+userId+"' AND  rolename LIKE '%物品管理员%' " ;
					  
					 String result = asf.showNull(waresStockService.getValueBySql(sql));
					 
					 if(!"".equals(result)){
						 judgeAuditUser = "物品管理员发起"; //行政部经理审批
					 }else {
						 judgeAuditUser = "普通人发起"; //物品管理员审批
					 }
					 
					 startMap.put("judgeAuditUser", judgeAuditUser);
					 
				}
				
				// 启动流程
				ProcessInstance pi =jbpmTemplate.startProcessById(processDefinitionId, startMap);
		        
				// 获取节点任务
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
				Task myTask = taskList2.get(0);
		
				WaresStramFlow waresStramFlow = new WaresStramFlow();
				waresStramFlow.setProcessInstanceId(pi.getId());
				waresStramFlow.setUuid(uuid);
				waresStramFlow.setApplyuser(startMap.get("proposer"));
				waresStramFlow.setApplyDate(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				waresStramFlow.setState(myTask.getName());
				waresStramFlow.setProperty("");
		
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
		
				waresStockService.addWaresstreamProcss(waresStramFlow);// 添加流程 (自己的表)
				
				
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
	/**
	 * 查询当前登录人 所里的行政部ID
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView getUserAdministration(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		Connection conn = null;
		
		PrintWriter out = response.getWriter();
		
		try {
				conn = new DBConnect().getConnect();
				ProclamationService proclamationService = new ProclamationService(conn);
				//总所
				String sql = "SELECT GROUP_CONCAT( DISTINCT d.autoId)  FROM k_user a \n"
							+"LEFT JOIN k_department  b ON a.departmentid = b.autoid  -- //当前人所在的部门 \n"
							+"LEFT JOIN k_department c ON b.fullpath LIKE CONCAT(c.fullpath,'%') AND c.level0 = 1 -- //部门分所 \n"
							+"LEFT JOIN k_department d ON d.`fullpath` LIKE CONCAT(c.fullpath,'%') AND d.`departname` LIKE '%行政部%' -- //分所的所有行政部 \n"
							+"WHERE a.id= '"+userId+"'";
				String departmentId = new ASFuntion().showNull(proclamationService.getValueBySql(sql));
				
				departmentId = departmentId.replace("@`@", ",");
				
				out.write(departmentId);
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			response.sendRedirect(request.getContextPath()+"/proclamation.do?method=list");
		}
		return null;

	}
	
	/**
	 * 报废 审批list
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView cancelAuditList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(CANCELAUDITLISTt);
		
		DataGridProperty pp = new DataGridProperty(); 
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		try {	
		    
		    String applySql =  "SELECT a.*,e.`Name` as userName,c.`quantity`, c.`date`, d.`name` as waresName,d.`type` FROM \n"
							    	+"(  \n"
							    	+"	SELECT DISTINCT a.DBID_,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_, GROUP_CONCAT(c.userID_ ) AS userID_ \n"
							    	+"	FROM jbpm4_task a  \n"
							    	+"	INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
							    	+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
							    	+"	GROUP BY a.EXECUTION_ID_ \n"
							    	+" ) a  \n" 
							    	+"INNER JOIN `j_waresstreamprocss` b ON a.id_ = b.ProcessInstanceId  \n"
							    	+"inner JOIN `k_waresstockdetails` c ON b.`uuid` = c.`uuid` \n"
							    	+"LEFT JOIN  k_waresstock d ON c.`waresStockId`  = d.`uuid` \n"
							    	+"LEFT JOIN  k_user e ON c.`userId` = e.`id` \n"
							    	+"WHERE "+userSession.getUserId()+" like concat(a.userId_,'%') and c.status = '已发起' ${name} ${type} ${applyDate} ${userName}";

		    
		    pp.setCustomerId("");
		    pp.setTableID("cancelAuditList");
			pp.setOrderBy_CH("date");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setSQL(applySql);
			pp.setInputType("radio");
			
			pp.setColumnWidth("10,7,7,15,15");
			
			pp.addColumn("物品名称", "waresName");
		    pp.addColumn("物品类型", "type");
		    pp.addColumn("报废申请人", "userName");
		    pp.addColumn("报废数量", "quantity");
		    pp.addColumn("报废日期", "date");
		    
		    pp.addSqlWhere("name", " and d.name like '%${name}%'");
		    pp.addSqlWhere("type", " and d.type like '%${type}%'");
		    pp.addSqlWhere("applyDate", " and c.date like '%${applyDate}%'");
		    pp.addSqlWhere("userName", " and e.name like '%${userId}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	/**
	 * 报废审批的跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView cancelAuditSkip(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(CANCELAUDIT);
		String taskId = request.getParameter("taskId");
		String ctype = request.getParameter("ctype");
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			WaresStockService stockService = new WaresStockService(conn);
				
			String pdid = jbpmTemplate.getProcessInstanceId(taskId);
			String sql = "select uuid from j_waresstreamprocss where ProcessInstanceId='"+pdid+"'";
			String uuids = stockService.getValueBySql(sql); //报废ID
			
			String[] uuid = uuids.split("@`@");  //报废ID

			sql = "select waresStockId from k_waresstockdetails where uuid='"+uuid[0]+"'";
		    String waresStockIds = stockService.getValueBySql(sql); //物品ID
		
			String[] waresStockId = waresStockIds.split("@`@"); //物品ID
					
			WaresStock waresStock =  stockService.getCNWaresStock(waresStockId[0] ); //当前库存数量
			WaresStockDetails  waresStockDetails = stockService.getCNWaresStockDetails(uuid[0]);
			
			modelAndView.addObject("waresStockDetails",waresStockDetails);
			modelAndView.addObject("userSession",userSession);
			modelAndView.addObject("waresStock",waresStock);
			modelAndView.addObject("applyDate",new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
			modelAndView.addObject("ctype",ctype);
			modelAndView.addObject("pdId",pdid);
			modelAndView.addObject("taskId",taskId);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView ;
	}
	
	/**
	 * 物品领用清单查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView applyInventoryList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(APPLYINENTORYLIST);
		ASFuntion asf = new ASFuntion();
		DataGridProperty pp = new DataGridProperty(); 
		String uuid = asf.showNull(request.getParameter("uuid"));
		try {	
		    
		    String applySql =  "SELECT DISTINCT b.uuid,d.`departname`,c.name AS `userName`,b.name, b.type, \n"
							 +"CONCAT(  a.`quantity` , b.`unitUnit`) AS quantity,a.`applyDate` \n"
							 +"FROM `k_waresstream` a  \n"
							 +"inner JOIN k_waresstock b ON a.waresstockId = b.uuid  \n"
							 +"LEFT JOIN k_user c  ON a.userId = c.`id`  \n"
							 +"LEFT JOIN k_department d ON c.departmentid = d.`autoid` \n"
							 +"WHERE   a.`status` = '已审批'  ";
		    System.out.println("2222222"+applySql);
		    
		    if(!"".equals(uuid)){
		    	applySql+=" and b.uuid = '"+uuid+"'";
		    }
		    applySql +="${name} ${type} ${applyDate} ${departName} ${userName}";
		    
		    pp.setCustomerId("");
		    pp.setTableID("applyInventoryList");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setSQL(applySql);
		//	pp.setInputType("radio");
			
			pp.setPrintEnable(true);	//
			pp.setPrintTitle("物品领用清单");
		    pp.setPrintVerTical(false);
		    pp.setPrintColumnWidth("14,14,62,30,30");
			
			pp.setColumnWidth("10,7,7,10,10,15,8,8");
			
			pp.addColumn("所在部门", "departname");
		    pp.addColumn("领用人", "userName");
		    pp.addColumn("物品名称", "name");
		    pp.addColumn("物品类型", "type");
		    pp.addColumn("领用总量", "quantity");
		    pp.addColumn("领用日期", "applyDate");
		    //pp.addColumn("总价", "sumMoeny");
		    
		    pp.addSqlWhere("name", " and b.name like '%${name}%'");
		    pp.addSqlWhere("type", " and b.type like '%${type}%'");
		    pp.addSqlWhere("applyDate", " and a.applyDate like '%${applyDate}%'");
		    pp.addSqlWhere("departName", " and d.departname like '%${departName}%'");
		    pp.addSqlWhere("userName", " and c.name like '%${userName}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 办公用品统计
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView waresStatisticsList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(WARESSTATISTICELIST);
		
		ASFuntion asf = new ASFuntion();
		DataGridProperty pp = new DataGridProperty(); 
		String uuid = asf.showNull(request.getParameter("uuid"));
		try {	
		    
		    String applySql ="select * from (" 
		    	 			+"SELECT a.uuid,a.`name`,a.type,a.`unitUnit`,a.`usableStock`,b.purchaseQuantity, \n" 
		    				+"c.returnQuantity,a.`scrappedStock`,d.applyQuantity,d.applyQuantity-c.returnQuantity as loadQuantity \n"
							+"FROM k_waresstock a \n"
							+"INNER JOIN ( \n"
							+"	SELECT SUM(quantity) AS purchaseQuantity,waresstockId FROM `k_waresstockdetails` b  \n"
							+"	WHERE ctype = '入库' \n"
							+"	GROUP BY waresstockId \n"
							+") b ON a.`uuid` = b.waresstockId \n"
							+"LEFT JOIN ( \n"
							+"	SELECT SUM(quantity) AS returnQuantity,waresstockId FROM `k_waresstockdetails` b  \n"
							+"	WHERE ctype = '归还' \n"
							+"	GROUP BY waresstockId \n"
							+")c  ON a.`uuid` = c.waresstockId \n"
							+"LEFT JOIN  \n"
							+"(	 \n"
							+"	SELECT SUM(quantity) applyQuantity,waresstockId  \n"
							+"	FROM`k_waresstream` d  \n"
							+"	GROUP BY waresstockId \n"
							+") d ON a.`uuid` = d.waresstockId \n"
							+"GROUP BY a.uuid \n"
							+") a where 1=1 ";
		    
		    if(!"".equals(uuid)){
		    	applySql+=" and a.uuid = '"+uuid+"'";
		    }
		    applySql += " ${name} ${type}";
		    pp.setCustomerId("");
		    pp.setTableID("waresStatisticsList");
			pp.setOrderBy_CH("uuid");
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setSQL(applySql);
			pp.setInputType("radio");
			
			pp.setPrintEnable(true);	//
			pp.setPrintTitle("物品统计清单");
		    pp.setPrintVerTical(false);
		    pp.setPrintColumnWidth("14,14,62,30,30");
			
			pp.setColumnWidth("10,7,7,10,8,8,8,8");
			
			pp.addColumn("物品名称", "name");
			pp.addColumn("物品类型", "type");
		    pp.addColumn("计算单位", "unitUnit");
		    pp.addColumn("当前库存", "usableStock");
		    pp.addColumn("采购量", "purchaseQuantity");
		    pp.addColumn("归还量", "returnQuantity");
		    pp.addColumn("借出量", "loadQuantity");
		    pp.addColumn("领用量", "applyQuantity");
		    pp.addColumn("报废量", "scrappedStock");
		    //pp.addColumn("总价", "sumMoeny");
		    
		    pp.addSqlWhere("name", " and a.name like '%${name}%'");
		    pp.addSqlWhere("type", " and a.type like '%${type}%'");
		
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	public ModelAndView doCheckSteam(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStream waresStream=new WaresStream();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStream=webUtil.evalObject(waresStream);
		 	waresStream=dbUtil.load(waresStream,waresStream.getUuid());
		    results=waresStockService.doCheck(waresStream, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	
	public ModelAndView doReceiveSteam(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStream waresStream=new WaresStream();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStream=webUtil.evalObject(waresStream);
		 	waresStream=dbUtil.load(waresStream,waresStream.getUuid());
		    results=waresStockService.doReceive(waresStream, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	public ModelAndView doSureReceiveSteam(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStream waresStream=new WaresStream();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStream=webUtil.evalObject(waresStream);
		 	waresStream=dbUtil.load(waresStream,waresStream.getUuid());
		    results=waresStockService.doSureReceive(waresStream, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	public ModelAndView doReturnSteam(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStream waresStream=new WaresStream();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStream=webUtil.evalObject(waresStream);
		 	waresStream=dbUtil.load(waresStream,waresStream.getUuid());
		    results=waresStockService.doReturn(waresStream, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	public ModelAndView doSigninDetail(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStockDetails waresStockDetails=new WaresStockDetails();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStockDetails=webUtil.evalObject(waresStockDetails);
		 	waresStockDetails=dbUtil.load(waresStockDetails,waresStockDetails.getUuid());
		    results=waresStockService.doSignin(waresStockDetails, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	public ModelAndView doCheckDetail(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStockDetails waresStockDetails=new WaresStockDetails();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStockDetails=webUtil.evalObject(waresStockDetails);
		 	waresStockDetails=dbUtil.load(waresStockDetails,waresStockDetails.getUuid());
		    results=waresStockService.doCheck(waresStockDetails, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	
	public ModelAndView doGrantDetail(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresStockDetails waresStockDetails=new WaresStockDetails();
		List<String> results=null;
		WaresStockService waresStockService=null;
		UserSession userSession=null;
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	userSession=webUtil.getUserSession();
		 	waresStockService=new WaresStockService(conn);
		 	waresStockDetails=webUtil.evalObject(waresStockDetails);
		 	waresStockDetails=dbUtil.load(waresStockDetails,waresStockDetails.getUuid());
		    results=waresStockService.doGrant(waresStockDetails, userSession);
		    webUtil.alert(results.get(0));
		}catch(Exception ex){
			webUtil.alert(ex);
		}finally{
		    DbUtil.close(conn);	
		}
		return new ModelAndView();
	}
	
	/*doSignin
	public ModelAndView doCheckSteam(HttpServletRequest request,HttpServletResponse response){
		WebUtil webUtil=new WebUtil(request, response);
		Connection conn=null;
		DbUtil dbUtil=null;
		try{}catch(Exception ex){
			
		}finally{
			
		}
		return new ModelAndView();
	}
	*/

	public ModelAndView doCheckPurc(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		DbUtil dbUtil=null;
		String re="0";
		int eff=0;
		String uuid=request.getParameter("uuid");
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			eff+=dbUtil.queryForInt("select count(uuid) from k_wares_purchasing where ware_id=? and state=?",new Object[]{uuid,"未采购"});
			if(eff<1){
			   re="当前物品尚未有采购计划";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
		
	}
	
	public ModelAndView doCheckPurcQuantity(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		DbUtil dbUtil=null;
		String re="0";
		int eff=0;
		String uuid=request.getParameter("uuid");
		int quantity=0;
		WaresPrucVO waresPrucVO=null;
		try{
			quantity=Integer.parseInt(request.getParameter("quantity"));
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresPrucVO=dbUtil.load(WaresPrucVO.class, uuid);
			if(waresPrucVO.getExpect_quantity()<quantity){
			   re=MessageFormat.format("入库数量不能超过 {0}", waresPrucVO.getExpect_quantity());
			}
		}catch(NumberFormatException nex){
			re="请输入整数数字";
		}
		catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
		
	}
	
	public ModelAndView getPruc(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WaresPrucVO waresPrucVO=new WaresPrucVO();
		String uuid=request.getParameter("uuid");
		JSONObject json=new JSONObject();
		
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			waresPrucVO=dbUtil.load(WaresPrucVO.class, uuid);
			json=JSONObject.fromObject(waresPrucVO);
			
		}catch(Exception ex){
	        ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(json.toString());
		return null;
	}
}
