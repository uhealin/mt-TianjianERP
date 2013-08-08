package com.matech.audit.work.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.OAexamine.ExamineService;
import com.matech.audit.service.accright.AccRightService;
import com.matech.audit.service.contract.ContractService;
import com.matech.audit.service.customer.ActiveService;
import com.matech.audit.service.customer.CustomerBusinessService;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.CustomermanagerService;
import com.matech.audit.service.customer.ManagerService1;
import com.matech.audit.service.customer.UserExcelData;
import com.matech.audit.service.customer.model.Business;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.customer.model.Manager1;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.industry.IndustryService;
import com.matech.audit.service.investManage.InvestManageService;
import com.matech.audit.service.kdic.DicService;
import com.matech.audit.service.kdic.model.Dic;
import com.matech.audit.service.keys.KeyValue;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.setdef.SetdefObjectService;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.userdef.Userdef;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.multidb.MultiDbIF;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.excelupload.ExcelUploadService;
import com.matech.framework.service.print.PrintSetup;

public class CustomerAction extends MultiActionController {

	private final String _strSuccess = "customer.do";

	private final String _strList = "Customer/List.jsp";
	private final String _strListAnLian = "Customer/ListAnLian.jsp";//安联
	
	private final String _strAddaddEdit = "Customer/AddandEdit.jsp";
	
	private final String _strAddaddEditAnLian = "Customer/AddandEditAnLian.jsp";

	private final String _strView = "Customer/view.jsp";

	private final String _strUpLoad = "Customer/manuupload.jsp";

	private final String _strCustomerLevel = "Customer/CustomerLevel.jsp";

	private final String _strCustomerLevelList = "Customer/CustomerLevleList.jsp";

	private final String _strLevelHistory = "Customer/CustomerLevelHistory.jsp";

	private final String _strCustomerList = "Customer/CustomerList.jsp";
	
	private final String _strBusinessLook ="Customer/CustomerBusinessLook.jsp";
	
	private final String annianList ="annianbusiness/annianList.jsp";
	private final String newAnnianList ="annianbusiness/newAnnianList.jsp";
	
	private final String annianAddAndEdit ="annianbusiness/annianAddandEdit.jsp";
	
	private final String annianMulList ="annianbusiness/annianMulList.jsp";

	/**
	 * 跳转到客户列表
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		Connection conn = null;
		ModelAndView modelAndView = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String  gsName = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
			if(gsName.indexOf("安联")>-1){
				modelAndView = new ModelAndView(_strListAnLian);
			}else {
				modelAndView = new ModelAndView(_strList);
			}
			
			String flag = CHF.showNull(req.getParameter("flag")); //用于标志是什么显示[我的客户]
			
			UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
			String departmentid = "",userid = "",name = "",loginid = "";
			userid = userSession.getUserId();
			name = userSession.getUserName();
			loginid = userSession.getUserLoginId();
			
			conn = new DBConnect().getConnect("");
			DbUtil dbUtil = new DbUtil(conn);
			//是否部门负责人
			String isPrint = dbUtil.queryForString("select 1 from k_role a inner join k_userrole b on a.id = b.rid inner join k_user c on b.userid = c.id where (a.rolename = '部门负责人') and b.userid = '"+userid+"' ");
			modelAndView.addObject("isPrint", ("1".equals(isPrint) ? isPrint : "0") );
			
			String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
			
//			String opt = dbUtil.queryForString("select 1 from s_config where sname='是否分部门管理客户和项目' and svalue = '是'");
			String opt = UTILSysProperty.SysProperty.getProperty("是否分部门管理客户和项目");
			
			//insert into `s_config` (`sname`, `svalue`, `sautoid`) values('是否显示客户详细列表','是','399');
			String isView = UTILSysProperty.SysProperty.getProperty("是否显示客户详细列表");
			modelAndView.addObject("isView", isView);
			
			if("是".equals(opt)){
				departmentid = userSession.getUserAuditDepartmentId();
				if(departmentid != null && !"".equals(departmentid)){
					departmentid = " and  a.departmentid = '"+departmentid+"' ";
				}else{
					departmentid  = "";
				}
			}
			
			String opt1 = UTILSysProperty.SysProperty.getProperty("是否按负责人管理客户和项目");
			
			DataGridProperty pp = new DataGridProperty() {
				
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
					
						String CustomerID = this.getRequestValue("CustomerID");
						String inType = this.getRequestValue("inType");
						String linkMan = this.getRequestValue("linkMan");
						String manager = this.getRequestValue("manager");
						String groupname = this.getRequestValue("groupname");
						
						String vip = this.getRequestValue("vip");
						String customerIeve = this.getRequestValue("customerIeve");
						String approach = this.getRequestValue("approach");
						String hylx = this.getRequestValue("hylx");
						String companyProperty = this.getRequestValue("companyProperty");
						
						/*if (vip != null && !"".equals(vip)) {
							vip = "and a.vip = '" + vip + "' \n" ;
						}
						if (customerIeve != null && !"".equals(customerIeve)) {
							customerIeve = "and a.customerIeve like '" + customerIeve + "' \n" ;
						}
						if (approach != null && !"".equals(approach)) {
							approach = "and a.approach = '" + approach + "' \n" ;
						}
						if (hylx != null && !"".equals(hylx)) {
							hylx = "and a.hylx like '%" + hylx + "%' \n" ;
						}
						if (companyProperty != null && !"".equals(companyProperty)) {
							companyProperty = "and a.companyProperty = '" + companyProperty + "' \n" ;
						}*/
						
						
						String departmentid = this.getRequestValue("departmentid");
						if (CustomerID != null && !"".equals(CustomerID)) {
							CustomerID = "and a.DepartID like '%" + CustomerID + "%' \n" ;
						}
						
						if (inType != null && !"".equals(inType)) {
							inType = "and a.industryname = '" + inType + "' \n" ;
						}
						if (linkMan != null && !"".equals(linkMan)) {
							linkMan = "and a.linkMan = '" + linkMan + "' \n";
						}
						if (manager != null && !"".equals(manager)) {
							manager = "and a.manager = '" + manager + "' \n";
						}
						if (groupname != null && !"".equals(groupname)) {
							groupname = "and a.groupname = '" + groupname + "' \n";
						}
						if (departmentid != null && !"".equals(departmentid)) {
							departmentid = "and a.departmentid = '" + departmentid + "' \n";
						}
						this.setOrAddRequestValue("CustomerID", CustomerID);
						this.setOrAddRequestValue("inType", inType);
						this.setOrAddRequestValue("linkMan", linkMan);
						this.setOrAddRequestValue("manager", manager);
						this.setOrAddRequestValue("groupname", groupname);
						this.setOrAddRequestValue("departmentid", departmentid);
						
						/*this.setOrAddRequestValue("vip", vip);
						this.setOrAddRequestValue("customerIeve", customerIeve);
						this.setOrAddRequestValue("approach", approach);
						this.setOrAddRequestValue("hylx", hylx);
						this.setOrAddRequestValue("companyProperty", companyProperty);*/
				}
			};
			
			// 必要设置
			pp.setTableID("customer");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" DepartID='${DepartID}'\" ");
			pp.setPageSize_CH(50);
		   
		    pp.setColumnWidth("10,25") ;

			// sql设置
		    String strSQL = "";
//		    if("".equals(departmentid)){
//		    	//0、用户没有部门的，显示所有客户
//		    	strSQL = "		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" ;
//		    }else{
//		    	strSQL = "select * from (" +
//		    	//1、合伙人、部门负责人、项目维护 看到本部门
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		inner join (select distinct departmentid as departments from k_role a inner join k_userrole b on a.id = b.rid inner join k_user c on b.userid = c.id where (a.rolename like '%项目维护%' or a.rolename like '%合伙人%' or a.rolename = '部门负责人') and b.userid = '"+userid+"') e on a.departmentid = e.departments \n" +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
//		    	
//		    	"		union  \n" +
//		    	//2、授权客户 与部门表的ProjectPopedom 和人员表的ProjectPopedom 有关
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		INNER JOIN (" +
//				"			select CONCAT('.',b.departmentid, if(IFNULL(b.ProjectPopedom,'')='','.',b.ProjectPopedom),IFNULL(REPLACE(a.ProjectPopedom, ',', '.'),'.'),'.') as ProjectPopedom " +
//				"			FROM k_department a,k_user b  " +
//				"			where a.autoid = b.departmentid " +
//				"			AND b.id = '"+userid+"'  " +
//		    	"		) e on e.ProjectPopedom LIKE CONCAT('%.',a.departmentid,'.%') AND  a.Property = '1' \n" +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
//		    	
//		    	"		union \n" + 
//		    	//3、是客户主负责人、副负责人的客户
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		and (mostly = '"+name+"' or concat(',',subordination,',') like '%,"+name+",%') \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
//		    	
//		    	"		union \n" + 
//		    	//4、显示责任人为当前人的客户
//		    	"		select a.* " +
//		    	"		from k_Customer a,k_customermanager b  " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		and (b.user1 = '"+userid+"' or b.user2 = '"+userid+"') \n" +
//		    	"		and a.departid = b.customerid \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
//		    	") a " ;
//		    }
//		    
//		    if("mylist".equals(flag)){
//		    	strSQL = "		select a.* " +
//		    	"		from k_Customer a,k_customermanager b  " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		and (b.user1 = '"+userid+"' or b.user2 = '"+userid+"') \n" +
//		    	"		and a.departid = b.customerid \n" +
//		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" ;
//		    }
		    
		    
		    //新逻辑：通过人员与部门授权来得到显示的部门范围
		    //0、admin 可以看到所有客户
		    //1、基本范围：显示责任人/承接人为当前人的客户
		    //2、补充范围：getUserIdPopedom 授权
		    if("admin".equals(loginid)){
		    //	strSQL = "		select a.* " +
		    //	"		from k_Customer a " +
		    // 	"		where a.Property = '1'  \n" +
		    // 	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" ;
		    	
		    	strSQL = " k_Customer  " ;
		    	
		    }else{
		    	String menuid = CHF.showNull(req.getParameter("menuid")); //菜单ID
		    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
		    	strSQL = " (select * from (" +	
		    	//1、基本范围：显示责任人/承接人为当前人的客户
		    	"		select a.* " +
		    	"		from k_Customer a,k_customermanager b  " +
		    	"		where a.Property = '1' and a.departid<>555555  \n" +
		    	"		and (b.user1 = '"+userid+"' or b.user2 = '"+userid+"') \n" +
		    	"		and a.departid = b.customerid \n" +
		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
		    	"		union \n" + 
		    	//2、补充范围：getUserIdPopedom 授权
		    	"		select a.* " +
		    	"		from k_Customer a " +
		    	"		where a.Property = '1' and a.departid<>555555  \n" +
		    	"		and a.departmentid in ("+departments+") " +
		    	"		${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} \n" +
		    	") a ) " ;		    	
		    }
		    
		    
		    String sql = "";
		    if("是".equals(opt1)){
		    	//是否按负责人管理客户和项目
		    	sql = "	select a.*,AccPackageYear,projectNum,industryname,b1.departname as b1departname,ifnull(b2.groupName,a.groupname) as b2groupName,ifnull(b3.groupName,a.groupname) as b3groupName \n" +
		    	"	from " +
		    	strSQL + 
		  //  	" ${ORDERBY} ${LIMIT}  \n" +
		    	"  a " +
		    	"	left join k_industry b  on a.VocationID=b.industryid \n" +
		    	"	left join k_department b1 on a.departmentid = b1.autoid \n" +
		    	"	left join k_group b2 on a.groupname = b2.groupid \n" +
		    	"	left join k_group b3 on b3.parentid = 0 and b2.fullpath like concat(b3.fullpath,'%')  \n" +
		    	"	left join (SELECT CustomerId,group_concat(AccPackageYear order by AccPackageYear) as AccPackageYear from c_accpackage group by CustomerId) c on a.DepartID = c.CustomerId \n" + 
		    	"	left join (select count(*) as projectNum,customerid from z_project group by customerid) d on a.DepartID=d.customerid  \n" +
		    	"	where 1=1 and a.departid<>555555 ${FREEQUERY} ${vip} ${customerIeve} ${approach} ${hylx} ${companyProperty} ${state}" ;
		    	
		    	/*
		    	pp.setCountsql("select count(*) as datagrid_count from (" +
		    			strSQL + 
		    			") a "); */
		    }else{
		    	//是否分部门管理客户和项目
				sql = ""
				   + "select a.*,AccPackageYear,projectNum,industryname,b1.departname as b1departname,ifnull(b2.groupName,a.groupname) as b2groupName,ifnull(b3.groupName,a.groupname) as b3groupName  \n"
				   + " from  k_Customer  a "
				   + " left join k_industry b  on a.VocationID=b.industryid "
				   + " left join k_department b1 on a.departmentid = b1.autoid \n" 
				   + " left join k_group b2 on a.groupname = b2.groupid \n" 
				   + " left join k_group b3 on b3.parentid = 0 and b2.fullpath like concat(b3.fullpath,'%')  \n"
				   + " left join (SELECT CustomerId,group_concat(AccPackageYear order by AccPackageYear) as AccPackageYear from c_accpackage group by CustomerId) c on a.DepartID = c.CustomerId "
				   + " left join (select count(*) as projectNum,customerid from z_project group by customerid) d on a.DepartID=d.customerid "
				   + " where 1=1  and a.departid<>555555 "
				   + " and a.DepartID in (" +
					"		select a.DepartID " +
			    	"		from k_Customer a " +
			    	" 		where a.Property = '1' "+departmentid+" ${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid}" +
			    			" ${vip} ${customerIeve} ${approach} ${hylx} ${companyProperty} ${state}" +
			    	"		union " +
			    	"		select a.DepartID " +
			    	"		from k_Customer a " +
			    	" 		INNER JOIN k_user e on e.id = '"+userid+"' AND e.ProjectPopedom LIKE CONCAT('%.',a.departmentid,'.%') AND  a.Property = '1'" +
			    	" 		where a.Property = '1' ${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} " 
					+ " ${vip} ${customerIeve} ${approach} ${hylx} ${companyProperty} ${state}" 
				   + " ) ${FREEQUERY} ";
				   
				
				pp.setCountsql("select count(*) as datagrid_count from (" +
						"		select a.DepartID " +
				    	"		from k_Customer a " +
				    	" 		where a.Property = '1' and a.departid<>555555 "+departmentid+" ${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} " +
				    	"		union " +
				    	"		select a.DepartID " +
				    	"		from k_Customer a " +
				    	" 		INNER JOIN k_user e on e.id = '"+userid+"' AND e.ProjectPopedom LIKE CONCAT('%.',a.departmentid,'.%') AND  a.Property = '1'" +
				    	" 		where a.Property = '1' and  a.departid<>555555 ${state} ${CustomerID} ${inType} ${linkMan} ${manager} ${groupname} ${departmentid} " +
						") a");
						
		    }
			
			
		//    pp.setLimitByOwnEnable(true); 
			pp.setSQL(sql); 
			pp.setOrderBy_CH("DepartID") ;
			pp.setDirection("desc");   
			pp.setInputType("radio");
			pp.addColumn("单位编号", "departcode");
			pp.addColumn("单位名称", "DepartName");
		//	pp.addColumn("会计制度类型", "industryname");
			pp.addColumn("所属集团", "b2groupName");
			pp.addColumn("最终集团", "b3groupName");
			pp.addColumn("所属部门", "b1departname"); 
			pp.addColumn("会计制度类型", "industryname");
		//	pp.addColumn("电子邮件", "email");
			pp.addColumn("客户帐套", "AccPackageYear") ;
			pp.addColumn("客户项目数","projectNum") ;
			pp.addColumn("状态", "estate");
			
			//设置自由查询别名添加的字段
			pp.addfreeQuery("departcode","a.departcode") ;
			pp.addfreeQuery("DepartName","a.DepartName") ;
			pp.addfreeQuery("b2groupName","b2.groupName") ;
			pp.addfreeQuery("b3groupName","b3.groupName") ;
			pp.addfreeQuery("AccPackageYear","c.AccPackageYear") ;
			
			//pp.addfreeQuery("vip","a.vip") ;
			//pp.addfreeQuery("customerIeve","a.customerIeve") ;
			//pp.addfreeQuery("approach","a.approach") ;
			//pp.addfreeQuery("hylx","a.hylx") ;
			//pp.addfreeQuery("companyProperty","a.companyProperty") ;
			
			//把其它字段查出，用于查询
			pp.addColumn("法人代表","corporate","hide") ;
			pp.addColumn("纳税人识别号","taxpayer","hide") ;
			pp.addColumn("国税号","countrycess","hide") ;
			pp.addColumn("地税号","terracess","hide") ;
			pp.addColumn("营业执照注册号","BPR","hide") ;
			pp.addColumn("企业代码","enterprisecode","hide") ;
			pp.addColumn("注册地址","loginaddress","hide") ;
			pp.addColumn("成立日期","departdate","hide") ;
			pp.addColumn("单位联系人","linkman","hide") ;
			pp.addColumn("传真号码","fax","hide") ;
			pp.addColumn("联系电话","phone","hide") ;
			pp.addColumn("电子邮件","email","hide") ;
			pp.addColumn("单位地址","address","hide") ;
			pp.addColumn("邮政编码","postalcode","hide") ;
			pp.addColumn("从业人数","practitioner","hide") ;
			pp.addColumn("经营方式","fashion","hide") ;
			pp.addColumn("证券市场","sMarket","hide") ;
			pp.addColumn("股票代码","sockCode","hide") ;
			pp.addColumn("客户级别","customerIeve","hide") ;
			pp.addColumn("网址","webSite","hide") ;
			pp.addColumn("项目状态","projectState","hide") ;
			pp.addColumn("状态","state","hide") ;
			pp.addColumn("客户资料来源途径","approach","hide") ;
			pp.addColumn("介绍人姓名","intro","hide") ;
			pp.addColumn("主负责人","mostly","hide") ;
			pp.addColumn("副负责人","subordination","hide") ;
			pp.addColumn("经营范围","businessbound","hide") ;
			
			pp.setWhichFieldIsValue(1);
			pp.addSqlWhere("CustomerID", "${CustomerID}");
			pp.addSqlWhere("inType", "${inType}");
			pp.addSqlWhere("linkMan", "${linkMan}");
			pp.addSqlWhere("manager", "${manager}");
			pp.addSqlWhere("groupname", "${groupname}");
			pp.addSqlWhere("departmentid", "${departmentid}");
			
			pp.addSqlWhere("vip", "and a.vip = '${vip}'");
			pp.addSqlWhere("customerIeve", " and a.customerIeve like '%${customerIeve}%'");
			pp.addSqlWhere("approach", " and a.approach like '%${approach}%'");
			pp.addSqlWhere("hylx", " and a.hylx like '%${hylx}%'");
			pp.addSqlWhere("companyProperty", " and a.companyProperty = '${companyProperty}'");
			pp.addSqlWhere("state", " and a.estate = '${state}'");
			
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("客户列表");
		    pp.setPrintColumnWidth("14,14,62,30,30");
		    pp.setPrintSqlColumn("DepartID,departcode,departname,b2groupName,b1departname,iframework,approach,companyProperty,plate,mostly,subordination,departenname,loginaddress,address,phone,fax,email,postalcode,BPR,departdate,corporate,linkman,register,countrycess,terracess,businessbound");
			pp.setPrintColumn("内部编号`代码`客户名称`所属集团`所属部门`组织机构性质`客户来源`公司性质`所属板块`主负责人`副负责人`英文名称`注册地址`单位地址`联系电话`传真号码`电子邮件`邮政编码`营业执照注册号`成立日期`法人代表`单位联系人`注册资本`国税号`地税号`经营范围");
			
			req.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			if(svalue.equals("大华")){ 
				//【责任人分配】要改为【承做人分配】，其它不变
				modelAndView.addObject("iText", "承做人分配");
			}else{
				modelAndView.addObject("iText", "责任人分配");
			}
			modelAndView.addObject("svalue", svalue);
			modelAndView.addObject("flag", flag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
		
		
	}

	/**
	 * 跳转到添加客户的页面
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */
	public ModelAndView addAndEdit(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			// 变量
			CustomerService smm = new CustomerService(conn);
			Customer customer = new Customer();
			UserdefService udm = new UserdefService(conn);
			Userdef udt = new Userdef();
			ArrayList al = new ArrayList();
			DELAutocode t = new DELAutocode();
			
			String towhere = req.getParameter("towhere");

			ASFuntion CHF = new ASFuntion();

	        String frameTree = CHF.showNull(req.getParameter("frameTree"));
	        String flag = CHF.showNull(req.getParameter("flag"));
	        
			// req.setCharacterEncoding("GBK");
			String id = CHF.showNull(req.getParameter("chooseValue"));
			String act = CHF.showNull(req.getParameter("act"));
			String menuDetail = "";
			String did = "";
			String XML = "";
			// 试用版错误页面

			String businessOfLongTime = req.getParameter("businessOfLongTime");



			did = req.getParameter("departid");
			if ("".equals(did)) {
				did = t.getAutoCode("KHDH", "");
			}

			XML = "<departid>" + did + "</departid>"
					+ req.getParameter("submitStr");

			customer.setCustdepartid(asf.getXMLData(XML, "custdepartid"));

			String departid = asf.showNull(asf.getXMLData(XML, "departid"));
			if("".equals(departid)){
				departid = t.getAutoCode("KHDH", "");
			}
			customer.setDepartId(departid);
			
//			String dateDepartId  = new Date().getTime()+"";
//			dateDepartId = dateDepartId.substring(0, 8);
//			customer.setDepartId(dateDepartId);

			customer.setDepartName(asf.getXMLData(XML, "departname"));

			customer.setDepartEnName(asf.getXMLData(XML, "departEnName")) ;

			customer.setVocationId(asf.getXMLData(XML, "vocationid"));

			customer.setAddress(asf.getXMLData(XML, "address"));

			customer.setLinkMan(asf.getXMLData(XML, "linkman"));
			customer.setPhone(asf.getXMLData(XML, "phone"));

			customer.setEmail(asf.getXMLData(XML, "email"));

			customer.setCorporate(asf.getXMLData(XML, "corporate"));

			customer.setCountryCess(asf.getXMLData(XML, "countrycess"));

			customer.setTerraCess(asf.getXMLData(XML, "terracess"));

			customer.setEnterpriseCode(asf.getXMLData(XML, "enterprisecode"));

			customer.setDepartDate(asf.getXMLData(XML, "departdate"));

			customer.setLoginAddress(asf.getXMLData(XML, "loginaddress"));

			customer.setBusinessBound(asf.getXMLData(XML, "businessbound"));

			customer.setRemark(asf.getXMLData(XML, "remark"));
			
			customer.setVip(asf.getXMLData(XML, "vip"));

			customer.setProperty(asf.getXMLData(XML, "property"));

			customer.setBpr(asf.getXMLData(XML, "BPR"));

			customer.setRegister(asf.getXMLData(XML, "register"));

			customer.setStockowner(asf.getXMLData(XML, "stockowner"));

			customer.setFax(asf.getXMLData(XML, "fax"));

			customer.setPostalcode(asf.getXMLData(XML, "postalcode"));

			customer.setTaxpayer(asf.getXMLData(XML, "taxpayer"));

			customer.setStandbyname(asf.getXMLData(XML, "standbyname"));

			customer.setHylx(asf.getXMLData(XML, "hylx"));

			customer.setCurname(asf.getXMLData(XML, "curname"));
			customer.setPractitioner(asf.getXMLData(XML, "practitioner"));
			customer.setFashion(asf.getXMLData(XML, "fashion"));
			customer.setCalling(asf.getXMLData(XML, "calling"));
			customer.setEstate(asf.getXMLData(XML, "estate"));
			customer.setApproach(asf.getXMLData(XML, "approach"));
			customer.setMostly(asf.getXMLData(XML, "mostly"));
			customer.setSubordination(asf.getXMLData(XML, "subordination"));
			customer.setGroupname(asf.getXMLData(XML, "groupname"));
			
			String departmentid =asf.showNull(asf.getXMLData(XML, "departmentid"));
			UserSession userSession1 = (UserSession) req.getSession().getAttribute("userSession");
			if("".equals(departmentid )){
				departmentid  = userSession1.getUserAuditDepartmentId();
			}
			customer.setDepartmentid(departmentid);
			
			//单位曾用名
			customer.setBeforeName(asf.getXMLData(XML, "beforeName"));
			//简称
			customer.setCustomerShortName(asf.getXMLData(XML, "customerShortName"));
			//组织机构性质
			customer.setIframework(asf.getXMLData(XML, "iframework"));
			//所属板块
			customer.setPlate(asf.getXMLData(XML, "plate"));
			//介绍人姓名
			customer.setIntro(asf.getXMLData(XML, "intro"));
			//控股股东/上级公司
			customer.setParentName(asf.getXMLData(XML, "parentName"));
			//控股方 holding
			customer.setHolding(asf.getXMLData(XML, "holding"));
			//公司性质 companyProperty 
			customer.setCompanyProperty(asf.getXMLData(XML, "companyProperty"));
			
			/*
			 * 后期所要添加的字段
			 */
			customer.setsMarket(asf.getXMLData(XML, "sMarket"));           //证券市场1	
			System.out.println("证劵市场"+asf.getXMLData(XML, "sMarket"));
			customer.setSockCode(asf.getXMLData(XML, "sockCode"));         // 股票代码1	
			System.out.println("  // 股票代码"+asf.getXMLData(XML, "sockCode"));
			
			customer.setsMarket2(asf.getXMLData(XML, "sMarket2"));           //证券市场2	
			System.out.println("证劵市场2"+asf.getXMLData(XML, "sMarket2"));
			customer.setSockCode2(asf.getXMLData(XML, "sockCode2"));         // 股票代码	2
			System.out.println("  // 股票代码2"+asf.getXMLData(XML, "sockCode2"));
			
			customer.setCustomerIeve(asf.getXMLData(XML, "customerIeve")); //客户级别
			System.out.println("//客户级别"+asf.getXMLData(XML, "customerIeve"));
			customer.setWebSite(asf.getXMLData(XML, "webSite"));           //网    址
			System.out.println("//网    址"+asf.getXMLData(XML, "webSite"));
			customer.setProjectState(asf.getXMLData(XML, "projectState")); //项目状态	
			customer.setState(asf.getXMLData(XML, "state"));               //状态	
		     /*
			 * 后期所要添加的报备，报告信息字段
			 */			
			customer.setiTmentName(asf.getXMLData(XML, "iTmentName")); 
			customer.setAgency(asf.getXMLData(XML, "agency")); 
			customer.setaStateDate(asf.getXMLData(XML, "aStateDate")); 
			customer.setBusineLicense(asf.getXMLData(XML, "busineLicense")); 
			customer.setBstateDate(asf.getXMLData(XML, "bstateDate")); 
			customer.setDirectorName(asf.getXMLData(XML, "directorName")); 
			
			customer.setDirectorPhone(asf.getXMLData(XML, "directorPhone")); 
			customer.setdSecretary(asf.getXMLData(XML, "dSecretary")); 
			customer.setSecretaryPhone(asf.getXMLData(XML, "secretaryPhone")); 
			customer.setCtaffQuantity(asf.getXMLData(XML, "ctaffQuantity")); 
			customer.setsAccountant(asf.getXMLData(XML, "sAccountant")); 
			customer.setfDirector(asf.getXMLData(XML, "fDirector")); 
			
			customer.setAccountanrPhone(asf.getXMLData(XML, "accountanrPhone")); 
			customer.setfManager(asf.getXMLData(XML, "fManager")); 
			customer.setfPhone(asf.getXMLData(XML, "fPhone")); 
			customer.setStockStartDate(asf.getXMLData(XML, "stockStartDate")); 
			customer.setStockListingDate(asf.getXMLData(XML, "stockListingDate")); 
			customer.setpOfficeAddress(asf.getXMLData(XML, "pOfficeAddress")); 
			
			customer.setcOfficeAddress(asf.getXMLData(XML, "cOfficeAddress")); 
			customer.setFbusineDate(asf.getXMLData(XML, "fbusineDate")); 
			customer.setIschange(asf.getXMLData(XML, "ischange")); 
			customer.setExplain(asf.getXMLData(XML, "explain")); 
			customer.setMergerQuantity(asf.getXMLData(XML, "mergerQuantity")); 
			customer.setAgoOffice(asf.getXMLData(XML, "agoOffice")); 
			
			customer.setcReason(asf.getXMLData(XML, "cReason")); 

			customer.setGroupplate(asf.getXMLData(XML, "groupplate"));
			
			//2011-11-1
			customer.setNation(asf.getXMLData(XML, "nation"));
			customer.setTotalassets(asf.getXMLData(XML, "totalassets"));
			customer.setTotalcurname(asf.getXMLData(XML, "totalcurname"));
			
			customer.setBank(asf.getXMLData(XML, "bank"));
			
			if(businessOfLongTime != null && businessOfLongTime.equals("长期经营")){

				customer.setBusinessBegin(businessOfLongTime);

				customer.setBusinessEnd("");
			}
			else{
				customer.setBusinessBegin(asf.getXMLData(XML, "businessbegin"));

				customer.setBusinessEnd(asf.getXMLData(XML, "businessend"));
			}

//System.out.println("====================================================================");
			String[] userDefName = req.getParameterValues("UserDefName");
			String[] userDefValue = req.getParameterValues("UserDefValue");

			Userdef[] userdefs = null;
			//判断有无自定义信息
			int lengh = userDefName == null ? 0 : userDefName.length;
			userdefs = new Userdef[lengh];
			Userdef userdef = null;
			for(int i=0; i < userdefs.length; i++) {
				userdef = new Userdef();
				userdef.setName(userDefName[i]);
				userdef.setValue(userDefValue[i]);

				userdefs[i] = userdef;
			}

			System.out.println(did);
			udm.addOrupdateUserdef(userdefs, did, "cust");

//System.out.println("====================================================================");
			String commondefNames = CHF.showNull(req.getParameter("commondefNames"));
			String commondefValues = CHF.showNull(req.getParameter("commondefValues"));

			if(!commondefNames.equals("")&&!commondefValues.equals("")){



				System.out.println(commondefNames.substring(0,commondefNames.length()-1));
				System.out.println(commondefValues.substring(0,commondefValues.length()-1));


				String commondefnames[] = commondefNames.substring(0,commondefNames.length()-1).split("-");
				String commondefvalues[] = commondefValues.substring(0,commondefValues.length()-1).split("-");

				Userdef[] commonsetdef = new Userdef[commondefnames.length];

				for(int i = 0; i<commondefnames.length; i++){
					if(commondefvalues[i].equals("NaN")){
						commondefvalues[i] = "";
					}
					String name = commondefnames[i];
					String value = commondefvalues[i];

			//		System.out.println(name);
			//		System.out.println(value);

					commonsetdef[i] = new Userdef();
					commonsetdef[i].setName(name);
					commonsetdef[i].setValue(value);
					commonsetdef[i].setProperty("com_cust");

				}

				udm.addOrupdateUserdef(commonsetdef, did, "com_cust");

			}

//System.out.println("====================================================================");
			if (!act.equals("update")) {
				
				//新增时，增加责任人分工，第一责任人为建立客户的人
				UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
				Map parameters = new HashMap(); 
				parameters.put("customerid", customer.getDepartId());
				parameters.put("user1", userSession.getUserId());
				parameters.put("user2", "");
				parameters.put("property", "");
				parameters.put("userid", userSession.getUserId());
				
				CustomermanagerService cs = new CustomermanagerService(conn);
				cs.save(parameters);
				//----------------------------
				
				smm.addCustomer(customer);
				
				try {
					//加入股票：检查独立性
					new InvestManageService(conn).getInvestByStockCode(customer.getSockCode(), parameters);
				} catch (Exception e) {}
				
				res.sendRedirect(_strSuccess+"?flag="+flag);
			} else {
				UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
				Map parameters = new HashMap(); 
				parameters.put("customerid", customer.getDepartId());
				parameters.put("userid", userSession.getUserId());
				
				smm.updateCustomer(customer);
				try {
					//加入股票：检查独立性
					new InvestManageService(conn).getInvestByStockCode(customer.getSockCode(), parameters);
				} catch (Exception e) {}
				
				if("different".equals(CHF.showNull(req.getParameter("isdifferent")))){
					new KeyValue().createKeyResult(did);
				}

				if("1".equals(frameTree)){
					res.sendRedirect("/AuditSystem/customer.do?method=del&&act=update&&chooseValue="+customer.getDepartId()+"&frameTree="+frameTree);
				}else{
					res.sendRedirect(_strSuccess+"?flag="+flag);
				}
			}
			// 修改页面
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

    public ModelAndView del(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			CustomerService customerService = new CustomerService(conn);

			String act = CHF.showNull(request.getParameter("act"));

			Customer customer = new Customer();
			String autoId = CHF.showNull(request.getParameter("chooseValue"));
			System.out.println(act);

			SetdefObjectService setdefObjectService = new SetdefObjectService(conn);

			List setValueList =  setdefObjectService.getSetValueList("cust",autoId,"com_cust");

			UserdefService userDefService = new UserdefService(conn);
			String defIndustry = CHF.showNull(new IndustryService(conn)
					.getDefIndustry());
			String[] op = defIndustry.split("`");
			Map map = new HashMap();
			map.put("op", op);
			map.put("act", act);
	        map.put("setValueList", setValueList);
	        
	        String frameTree = CHF.showNull(request.getParameter("frameTree"));
	        map.put("frameTree", frameTree);
	        
	        String sysUsr = UTILSysProperty.SysProperty.getProperty("系统应用事务所");
	        map.put("sysUsr", sysUsr);
	        
			/*
			 * if(!autoId.equals("")){
			 * customer.setDepartId(Integer.parseInt(autoId)); }else{
			 * departmentVO.setAutoId(0); }
			 */
			if (act.equals("del")) {

				try {
					String departName=customerService.getCustomer(autoId).getDepartName();
					customerService.remove(autoId);
					userDefService.removeUserdef(autoId, "cust");

					LogService.updateToLog(userSession, "删除客户["+departName+"]", conn);
					response.sendRedirect(_strSuccess);
					return null;
				} catch (Exception e) {
					response.setContentType("text/html;charset=utf-8"); // 设置编码
					PrintWriter out = response.getWriter();
					out.print("<script>");
					out.print("		alert('" + e.getMessage() + "');");
					out.print("		window.location='/AuditSystem/customer.do';");
					out.print("</script>");
					return null;
				}


			} else if (act.equals("update")) {
				String menuDetail=null;
				try {
					customer = customerService.getCustomer(autoId);

					menuDetail = "<departid>" + autoId + "</departid>"+ 
					"<departname>" + customer.getDepartName()+ "</departname>" +
					"<departEnName>"+ customer.getDepartEnName()+"</departEnName>" +
					"<vocationid>"+ customer.getVocationId() + "</vocationid>" +
					"<hylx>"+ customer.getHylx() + "</hylx>" +
					"<linkman>"+ customer.getLinkMan() + "</linkman>" +
					"<phone>"+ customer.getPhone() + "</phone>" +
					"<email>"+ customer.getEmail() + "</email>" +
					"<address>"	+ customer.getAddress() + "</address>" +
					"<corporate>"+ customer.getCorporate() + "</corporate>" +
					"<countrycess>"	+ customer.getCountryCess()	+ "</countrycess>" +
					"<terracess>" + customer.getTerraCess()+ "</terracess>" +
					"<enterprisecode>"	+ customer.getEnterpriseCode()+ "</enterprisecode>" +
					"<departdate>"+ customer.getDepartDate()+ "</departdate>" +
					"<loginaddress>"	+ customer.getLoginAddress()+ "</loginaddress>" +
					"<businessbegin>"	+ customer.getBusinessBegin()+ "</businessbegin>" +
					"<businessend>"+ customer.getBusinessEnd()+ "</businessend>" +
					"<businessbound>"+ customer.getBusinessBound()+ "</businessbound>" +
					"<remark>" + customer.getRemark()	+ "</remark>" +
					"<property>" + customer.getProperty()+ "</property>" +
					"<register>" + customer.getRegister()+ "</register>" +
					"<BPR>" + customer.getBpr()+ "</BPR>" +
					"<stockowner>" + customer.getStockowner()+ "</stockowner>" +
					"<postalcode>"+ customer.getPostalcode() + "</postalcode>" +
					"<fax>"+ customer.getFax() + "</fax>" +
					"<taxpayer>"+ customer.getTaxpayer() + "</taxpayer>" +
					"<curname>"+ customer.getCurname()+"</curname>" +
					"<custdepartid>"+customer.getCustdepartid()+"</custdepartid>" +
					"<practitioner>"	+customer.getPractitioner()+"</practitioner>" +
					"<fashion>"+customer.getPractitioner()+"</fashion>" +
					"<calling>"+customer.getCalling()+"</calling>" +
					"<estate>"	+customer.getEstate()+"</estate>" +
					"<approach>"	+customer.getApproach()+"</approach>" +
					"<mostly>"+customer.getMostly()+"</mostly>" +
					"<subordination>"+customer.getSubordination()+"</subordination>" +
					"<groupname>"	+customer.getGroupname()+"</groupname>" +
					"<departmentid>"+customer.getDepartmentid()+"</departmentid>" +
					
					"<companyProperty>"+customer.getCompanyProperty()+"</companyProperty>" +
					"<holding>"+customer.getHolding()+"</holding>" +
					"<parentName>"+customer.getParentName()+"</parentName>" +
					"<intro>"+customer.getIntro()+"</intro>" +
					"<plate>"+customer.getPlate()+"</plate>" +
					"<customerShortName>"+customer.getCustomerShortName()+"</customerShortName>" +
					"<iframework>"+customer.getIframework()+"</iframework>" +
					"<beforeName>"+customer.getBeforeName()+"</beforeName>" +
					"<groupplate>"+customer.getGroupplate()+"</groupplate>" +
					
					//后期所加字段
					"<sMarket>"+customer.getsMarket()+"</sMarket>" +
					"<sockCode>"+customer.getSockCode()+"</sockCode>" +
					"<customerIeve>"+customer.getCustomerIeve()+"</customerIeve>" +
					"<webSite>"+customer.getWebSite()+"</webSite>" +
					"<projectState>"+customer.getProjectState()+"</projectState>" +
					"<state>"+customer.getState()+"</state>" +
					
					//报告,报备信息所加字段
					"<iTmentName>"+customer.getiTmentName()+"</iTmentName>" +
					"<agency>"+customer.getAgency()+"</agency>" +
					"<aStateDate>"+customer.getaStateDate()+"</aStateDate>" +
					"<busineLicense>"+customer.getBusineLicense()+"</busineLicense>" +
					"<bstateDate>"+customer.getBstateDate()+"</bstateDate>" +
					"<directorName>"+customer.getDirectorName()+"</directorName>" +
					
					"<directorPhone>"+customer.getDirectorPhone()+"</directorPhone>" +
					"<dSecretary>"+customer.getdSecretary()+"</dSecretary>" +
					"<secretaryPhone>"+customer.getSecretaryPhone()+"</secretaryPhone>" +
					"<ctaffQuantity>"+customer.getCtaffQuantity()+"</ctaffQuantity>" +
					"<sAccountant>"+customer.getsAccountant()+"</sAccountant>" +
					"<fDirector>"+customer.getfDirector()+"</fDirector>" +
					
					"<accountanrPhone>"+customer.getAccountanrPhone()+"</accountanrPhone>" +
					"<fManager>"+customer.getfManager()+"</fManager>" +
					"<fPhone>"+customer.getfPhone()+"</fPhone>" +
					"<stockStartDate>"+customer.getStockStartDate()+"</stockStartDate>" +
					"<stockListingDate>"+customer.getStockListingDate()+"</stockListingDate>" +
					"<pOfficeAddress>"+customer.getpOfficeAddress()+"</pOfficeAddress>" +
					
					"<cOfficeAddress>"+customer.getcOfficeAddress()+"</cOfficeAddress>" +
					"<fbusineDate>"+customer.getFbusineDate()+"</fbusineDate>" +
					"<ischange>"+customer.getIschange()+"</ischange>" +
					"<explain>"+customer.getExplain()+"</explain>" +
					"<mergerQuantity>"+customer.getMergerQuantity()+"</mergerQuantity>" +
					"<agoOffice>"+customer.getAgoOffice()+"</agoOffice>" +
					
					"<cReason>"+customer.getcReason()+"</cReason>" +
					
					//2011-11-1
					"<nation>"+customer.getNation()+"</nation>" +
					"<totalassets>"+customer.getTotalassets()+"</totalassets>" +
					"<totalcurname>"+customer.getTotalcurname()+"</totalcurname>" +
					"<bank>"+customer.getBank()+"</bank>" +
					"<vip>"+customer.getVip()+"</vip>" +
					"";
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				request.setAttribute("vocationid", customer.getVocationId());

				String property = "cust";
				// String act="update";
				Userdef[] userdef = userDefService.getUserdef(autoId, property);
				System.out.println(userdef.length);
				map.put("menuDetail", menuDetail);
				map.put("userdef", userdef);
				map.put("customer", customer);
				String  gsName = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
				if(gsName.indexOf("安联")>-1){
					return new ModelAndView(_strAddaddEditAnLian, map);

				}else{
					return new ModelAndView(_strAddaddEdit, map);
				}
			} else if (act.equals("view")) {

				customer = customerService.getCustomer(autoId);

				String menuDetail = "<departid>" + autoId + "</departid>"+ 
						"<departname>" + customer.getDepartName()+ "</departname>" +
						"<departEnName>"+ customer.getDepartEnName()+"</departEnName>" +
						"<vocationid>"+ customer.getVocationId() + "</vocationid>" +
						"<hylx>"+ customer.getHylx() + "</hylx>" +
						"<linkman>"+ customer.getLinkMan() + "</linkman>" +
						"<phone>"+ customer.getPhone() + "</phone>" +
						"<email>"+ customer.getEmail() + "</email>" +
						"<address>"	+ customer.getAddress() + "</address>" +
						"<corporate>"+ customer.getCorporate() + "</corporate>" +
						"<countrycess>"	+ customer.getCountryCess()	+ "</countrycess>" +
						"<terracess>" + customer.getTerraCess()+ "</terracess>" +
						"<enterprisecode>"	+ customer.getEnterpriseCode()+ "</enterprisecode>" +
						"<departdate>"+ customer.getDepartDate()+ "</departdate>" +
						"<loginaddress>"	+ customer.getLoginAddress()+ "</loginaddress>" +
						"<businessbegin>"	+ customer.getBusinessBegin()+ "</businessbegin>" +
						"<businessend>"+ customer.getBusinessEnd()+ "</businessend>" +
						"<businessbound>"+ customer.getBusinessBound()+ "</businessbound>" +
						"<remark>" + customer.getRemark()	+ "</remark>" +
						"<property>" + customer.getProperty()+ "</property>" +
						"<register>" + customer.getRegister()+ "</register>" +
						"<BPR>" + customer.getBpr()+ "</BPR>" +
						"<stockowner>" + customer.getStockowner()+ "</stockowner>" +
						"<postalcode>"+ customer.getPostalcode() + "</postalcode>" +
						"<fax>"+ customer.getFax() + "</fax>" +
						"<taxpayer>"+ customer.getTaxpayer() + "</taxpayer>" +
						"<curname>"+ customer.getCurname()+"</curname>" +
						"<custdepartid>"+customer.getCustdepartid()+"</custdepartid>" +
						"<practitioner>"	+customer.getPractitioner()+"</practitioner>" +
						"<fashion>"+customer.getPractitioner()+"</fashion>" +
						"<calling>"+customer.getCalling()+"</calling>" +
						"<estate>"	+customer.getEstate()+"</estate>" +
						"<approach>"	+customer.getApproach()+"</approach>" +
						"<mostly>"+customer.getMostly()+"</mostly>" +
						"<subordination>"+customer.getSubordination()+"</subordination>" +
						"<groupname>"	+customer.getGroupname()+"</groupname>" +
						"<departmentid>"+customer.getDepartmentid()+"</departmentid>" +
						
						"<companyProperty>"+customer.getCompanyProperty()+"</companyProperty>" +
						"<holding>"+customer.getHolding()+"</holding>" +
						"<parentName>"+customer.getParentName()+"</parentName>" +
						"<intro>"+customer.getIntro()+"</intro>" +
						"<plate>"+customer.getPlate()+"</plate>" +
						"<customerShortName>"+customer.getCustomerShortName()+"</customerShortName>" +
						"<iframework>"+customer.getIframework()+"</iframework>" +
						"<beforeName>"+customer.getBeforeName()+"</beforeName>" +
						"<groupplate>"+customer.getGroupplate()+"</groupplate>" +
						
						//后期所加字段
						"<sMarket>"+customer.getsMarket()+"</sMarket>" +
						"<sockCode>"+customer.getSockCode()+"</sockCode>" +
						"<customerIeve>"+customer.getCustomerIeve()+"</customerIeve>" +
						"<webSite>"+customer.getWebSite()+"</webSite>" +
						"<projectState>"+customer.getProjectState()+"</projectState>" +
						"<state>"+customer.getState()+"</state>" +
						
						//报告,报备信息所加字段
						"<iTmentName>"+customer.getiTmentName()+"</iTmentName>" +
						"<agency>"+customer.getAgency()+"</agency>" +
						"<aStateDate>"+customer.getaStateDate()+"</aStateDate>" +
						"<busineLicense>"+customer.getBusineLicense()+"</busineLicense>" +
						"<bstateDate>"+customer.getBstateDate()+"</bstateDate>" +
						"<directorName>"+customer.getDirectorName()+"</directorName>" +
						
						"<directorPhone>"+customer.getDirectorPhone()+"</directorPhone>" +
						"<dSecretary>"+customer.getdSecretary()+"</dSecretary>" +
						"<secretaryPhone>"+customer.getSecretaryPhone()+"</secretaryPhone>" +
						"<ctaffQuantity>"+customer.getCtaffQuantity()+"</ctaffQuantity>" +
						"<sAccountant>"+customer.getsAccountant()+"</sAccountant>" +
						"<fDirector>"+customer.getfDirector()+"</fDirector>" +
						
						"<accountanrPhone>"+customer.getAccountanrPhone()+"</accountanrPhone>" +
						"<fManager>"+customer.getfManager()+"</fManager>" +
						"<fPhone>"+customer.getfPhone()+"</fPhone>" +
						"<stockStartDate>"+customer.getStockStartDate()+"</stockStartDate>" +
						"<stockListingDate>"+customer.getStockListingDate()+"</stockListingDate>" +
						"<pOfficeAddress>"+customer.getpOfficeAddress()+"</pOfficeAddress>" +
						
						"<cOfficeAddress>"+customer.getcOfficeAddress()+"</cOfficeAddress>" +
						"<fbusineDate>"+customer.getFbusineDate()+"</fbusineDate>" +
						"<ischange>"+customer.getIschange()+"</ischange>" +
						"<explain>"+customer.getExplain()+"</explain>" +
						"<mergerQuantity>"+customer.getMergerQuantity()+"</mergerQuantity>" +
						"<agoOffice>"+customer.getAgoOffice()+"</agoOffice>" +
						
						"<cReason>"+customer.getcReason()+"</cReason>" +
						
						//2011-11-1
						"<nation>"+customer.getNation()+"</nation>" +
						"<totalassets>"+customer.getTotalassets()+"</totalassets>" +
						"<totalcurname>"+customer.getTotalcurname()+"</totalcurname>" +
						"<bank>"+customer.getBank()+"</bank>" +
						
						"";
				request.setAttribute("vocationid", customer.getVocationId());

				String property = "cust";
				// String act="update";
				Userdef[] userdef = userDefService.getUserdef(autoId, property);
				System.out.println(userdef.length);
				map.put("menuDetail", menuDetail);
				map.put("userdef", userdef);

				return new ModelAndView(_strView, map);
			} else {
				String  gsName = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
				if(gsName.indexOf("安联")>-1){
					return new ModelAndView(_strAddaddEditAnLian, map);
				}else{					
					return new ModelAndView(_strAddaddEdit, map);
				}
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 检查客户是否存在
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */

	public ModelAndView checkCustomer(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		PrintWriter out = res.getWriter();
		System.out.println();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			res.setHeader("Pragma", "No-cache");
			res.setHeader("Cache-Control", "no-cache");
			res.setDateHeader("Expires", 0);
			
			UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
			
			String customerName = req.getParameter("customerName");
			if (!customerName.equals("") && customerName != null) {

//				String strSql = "select 1 from s_config a where sname=? and svalue = ?";
//				Object[] params = new Object[] { "是否允许同名客户","不允许" };
//				String result = new DbUtil(conn).queryForString(strSql, params);
				String result = UTILSysProperty.SysProperty.getProperty("是否允许同名客户");
				String opt = UTILSysProperty.SysProperty.getProperty("是否分部门管理客户和项目");
				if("不允许".equals(result)){
					if("是".equals(opt)){
						String departmentid = userSession.getUserAuditDepartmentId();
						if(departmentid != null && !"".equals(departmentid)){
							out.print(new CustomerService(conn).getCustomerId(customerName,departmentid));
						}else{
							out.print(new CustomerService(conn).getCustomerId(customerName));
						}
					}else{
						out.print(new CustomerService(conn).getCustomerId(customerName));
					}
				}else{
					out.print("ok");
				}

			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 检查客户是否存在
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */
	public ModelAndView checkCustomerName(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			


			String customerName = request.getParameter("customerName");
			
			if (!customerName.equals("") && customerName != null) {

				String strSql = "select 1 from s_config a where sname=? and svalue = ?";
				Object[] params = new Object[] { "是否允许同名客户","不允许" };
				String result = new DbUtil(conn).queryForString(strSql, params);
				
				if("1".equals(result)){
					out.write(new CustomerService(conn).checkCustomerName(customerName));
				} else {
					out.print("");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 检查单位编号是否存在
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */

	public ModelAndView checkCustomerNumber(HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		PrintWriter out = res.getWriter();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");

			String customerNumber = req.getParameter("customerNumber");

			String sql = "select departcode from k_customer where departcode = '"+customerNumber+"'";
		    ps = conn.prepareStatement(sql);
		    rs = ps.executeQuery();

		    String result = "";
		    while(rs.next()){
		    	result = rs.getString(1);
		    }

			if("".equals(result)){
				out.print("ok");
			}else{
				out.print("no");
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 跳到批量导入页面
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */

	public ModelAndView manuUpLoad(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			// CustomerService smm = new CustomerService(conn);
			// final String TRY_URL = "../AS_SYSTEM/error_page.jsp?tip=999";
			// if( JRockey2Opp.getUserLic() <= 0) {
			// //无狗
			// if(smm.getCustomerCount() >= 2) {
			// //超过或者等于最大客户数量
			// res.sendRedirect(TRY_URL);
			// }
			// }
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return new ModelAndView(_strUpLoad);
	}

	/**
	 * 批量导入保存
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */
	public ModelAndView SaveUpload(HttpServletRequest request,HttpServletResponse response) throws Exception {

		Connection conn = null;

		PrintWriter out = null;

		Map parameters = null;

		String uploadtemppath = "";

		String strFullFileName = "";
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");

		int error = 0; // 用于标记程序是否出错,出错了后面就不会再继续执行了
		try {

			response.setContentType("text/html;charset=utf-8"); // 设置编码

			out = response.getWriter();

			// String nf = "";
			// 获取前台指定的客户ID

			String Cust = "";

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			Cust = (String) parameters.get("Cust");

			// 如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");

			if (uploadtemppath.equals(""))
				out.print("Error\n帐套数据上传及预处理失败");
			else
				out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

			// 分析帐套文件,取出帐套年份;

			out.println("预处理分析帐套文件<br/>");
			out.flush();

			conn = new DBConnect().getDirectConnect("");
		} catch (Exception e) {
			e.printStackTrace();
			out.println("无法联接数据库，请联系系统管理员，本次装载失败！<br>");
			error = 1;
		}

		// 初始化业务对象
		ExcelUploadService upload = null;
		try {
			upload = new ExcelUploadService(conn, strFullFileName);
		} catch (Exception e) {
			e.printStackTrace();
			out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
			error = 1;
		}

		// 检查用户指定年份的帐套是否存在;

		// 定义单一，避免其他用户干扰；
		Single sl = new Single();
		String lockmsg = "批量导入客户资料";
		try {
			sl.locked(lockmsg, userSession.getUserName());
		} catch (Exception e) {
			out.println(e.getMessage() + "<br/>");
			error = 1;
		}

		if (error > 0) {
			out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
		} else {
			org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
			out.println("继续处理装载<br>");
			out.println("<SCRIPT language=JavaScript>");
			out.println("var currentpos,timer;");
			out.println("function initialize()");
			out.println("{");
			out.println("timer=setInterval(\"scrollwindow()\",5);");
			out.println("}");
			out.println("function sc(){");
			out.println("clearInterval(timer);}");
			out.println("function scrollwindow(){");
			out.println("currentpos=document.body.scrollTop;");
			out.println("window.scroll(0,++currentpos);}");
			out.println("document.ondblclick=initialize;");
			out.println(" initialize();");
			out.println("</script>");

			// 继续装载
			try {
				UserExcelData ued = new UserExcelData(conn);

				// 准备处理EXCEL；
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				// 开始装载科目余额表信息
				// 首先清空指定表的指定帐套的数据;
				out.println("正在装载客户资料!......");
				out.flush();

				String[] tableRows = { "客户名称", 
						"代码","所属集团","英文名称","法人代表", "纳税人识别号", "国税号", "地税号",
						"营业执照注册号", "注册资本", "企业代码", "成立日期", "注册地址", "股东成员",
						"单位联系人", "传真号码", "联系电话", "电子邮件", "单位地址", "邮政编码",
						"经营范围", "备注","所属部门",
						
						"客户简称","曾用名","行业类型","货币类型","控股股东/上级公司","控股方",
						"组织机构性质","公司性质","所属板块","从业人数","经营方式","客户状态",
						"客户来源","介绍人姓名","主负责人","副负责人","经营期限起","经营期限至",
						"集团板块","资产总额","资产货币类型",
						
						//特殊列：用于初始化项目
						"项目名称","项目年度","项目类型","项目负责人","项目费用"
						};
				
				ued.newTable();
				
				upload.setExcelNum("注册资本,资产总额,项目费用");
				upload.setExcelString("客户名称,联系电话,传真号码,邮政编码,纳税人识别号,国税号,地税号,营业执照注册号,企业代码,项目年度");
				String[] exexlKmye = { "客户名称" };
				String[] tableKmye = { "departname" };
				String[] exexlPzmxOpt = { 
						"代码","所属集团","英文名称","法人代表", "纳税人识别号", "国税号", "地税号",
						"营业执照注册号", "注册资本", "企业代码", "成立日期", "注册地址", "股东成员",
						"单位联系人", "传真号码", "联系电话", "电子邮件", "单位地址", "邮政编码",
						"经营范围", "备注","所属部门",
						
						"客户简称","曾用名","行业类型","货币类型","控股股东/上级公司","控股方",
						"组织机构性质","公司性质","所属板块","从业人数","经营方式","客户状态",
						"客户来源","介绍人姓名","主负责人","副负责人","经营期限起","经营期限至",
						"集团板块","资产总额","资产货币类型",
						
						//特殊列：用于初始化项目
						"项目名称","项目年度","项目类型","项目负责人","项目费用"
						};

				String[] tablePzmxOpt = { 
						"departcode","groupname","departenname","corporate", "taxpayer","countrycess", "terracess", 
						"BPR", "register","enterprisecode", "departdate", "loginaddress","stockowner", 
						"linkman", "fax", "phone", "email","address", "postalcode", 
						"businessbound", "remark","departmentid",
						
						"customerShortName","beforeName","hylx","curname","parentName","holding",
						"iframework","companyProperty","plate","practitioner","fashion","estate",
						"approach","intro","mostly","subordination","businessbegin","businessend",
						"groupplate","totalassets","totalcurname",
						
						//特殊列：用于初始化项目
						"projectname","projectyear","auditpara","manageruser","businesscost"
						};

				String[] exexlKmyeFixFields = { "Property"};
				String[] excelKmyeFixFieldValues = { "1" };

				String result = "";

				result = upload.LoadFromExcel("客户列表", "tt_k_customer",
						exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
						exexlKmyeFixFields, excelKmyeFixFieldValues);

				out.println("装载客户资料完毕!<BR>");

				String[] excelAllRows = upload.getAllHeads("客户列表");

				String[] excelRows = ued.getExcelRows(tableRows, excelAllRows);
				String[] tabRows = ued.newUserDef(excelRows);

				result = upload.LoadFromExcel("客户列表", "tt_k_userdef",
						exexlKmye, tableKmye, excelRows, tabRows, null, null);

				out.flush();
				out.println("开始更新客户列表!......");
				out.flush();
				
				ued.updateData();
				
				String DepartmentId = userSession.getUserAuditDepartmentId();
				
				ued.CheckUpData(out, excelRows, DepartmentId);
				ued.insertData(out, excelRows,DepartmentId);
				
				out.println("<br>更新客户列表完毕!");
				out.flush();
				out.println("<hr>");
				out.flush();
				//初始化项目:以【客户编号、项目年度、项目类型】来判断，当前年度是否已建立项目
				if(ued.isAddProject()){
					out.println("<br>初始化客户的项目!......");
					out.flush();
					
					ued.insertProject(out, userSession.getUserId());
					
					out.println("<br>更新初始化项目完毕!");
					out.flush();
				}
				
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}
				out.println("<hr>数据装载成功");
				out.println("<script language=\"javascript\">");
				out.println("alert(\"数据装载成功!\");");
				out.println("document.location.href=\"customer.do\";");
//				out.println("</font><br>");
				out.println("</script>");

				//新增日志
				LogService.addTOLog(userSession, conn, null, "客户批量导入：" + (String) parameters.get("filename"),"客户批量导入");
				
			} catch (Exception e) {
				e.printStackTrace();
				out.println("<font style=\"color:red\">装载处理出现错误:<br/>"
						+ e.getMessage());
				out.println("<a href=\"customer.do?method=manuUpLoad\">返回装载页面</a>\"</font>");
			} finally {
				if (conn != null)
					DbUtil.close(conn);
				out.close();
				try {
					sl.unlocked(lockmsg, userSession.getUserName());
				} catch (Exception e) {
					out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
					error = 1;
				}
			}

		}// 继续装载的处理函数

		if (conn != null)
			DbUtil.close(conn);
		return null;
	}

	/**
	 * 设置打印属性
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView print(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		HashMap mapResult = new HashMap();
		try {
			// String temp =
			// com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("clientDog");
			conn = new DBConnect().getConnect("");
			String tableid = request.getParameter("tableid");

			DataGridProperty pp = (DataGridProperty) request.getSession()
					.getAttribute(DataGrid.sessionPre + tableid);

			PrintSetup printSetup = new PrintSetup(conn);

			printSetup.setStrTitles(new String[] { "客户列表" });

			printSetup.setStrQuerySqls(new String[] { pp.getFinishSQL() });
			printSetup
					.setStrChineseTitles(new String[] { "单位编号`单位名称`会计制度类型`单位联系人`联系电话" });
			printSetup.setCharColumn(new String[] { "1`2`3`4`5" });

			printSetup.setIColumnWidths(new int[] { 14,62,55,17,20 });


			String filename = printSetup.getExcelFile();

			// vpage strPrintTitleRows
			mapResult.put("refresh", "");

			mapResult.put("saveasfilename", "客户列表");
			mapResult.put("bVpage", "false");
			mapResult.put("strPrintTitleRows", "$2:$4");
			mapResult.put("filename", filename);

			// curProjectid curProjectState curAccPackageID user

		} catch (Exception e) {
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp", mapResult);
	}

	/**
	 * 批量打印
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView xlsPrint(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {

			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("customerPrint");
			pp.setCancelPage(true);
			pp.setInputType("checkbox");
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			String sql = "select DepartID,DepartName,industryname,LinkMan,Phone,'' isprint from k_Customer a,k_industry b where 1=1 and Property = '1' and a.VocationID=b.industryid ";


			pp.setOrderBy_CH("DepartID") ;
			pp.setDirection("desc");
			// pp.setOrderBy_CH("ID");
			// pp.setDirection("asc");
			// 编号,姓名,登录名,所属部门,岗位,是否打印
			// pp.addColumn("编号", "ID");
			pp.addColumn("单位编号", "DepartID");
			pp.addColumn("单位名称", "DepartName");
			pp.addColumn("会计制度类型", "industryname");
			pp.addColumn("单位联系人", "LinkMan");
			pp.addColumn("联系电话", "Phone");
			//pp.addColumn("是否打印", "isprint");

			pp.setSQL(sql);

			request.getSession().setAttribute(
					DataGrid.sessionPre + pp.getTableID(), pp);
			// mapResult.put("name", name);
		} catch (Exception e) {
			Debug.print(Debug.iError, "查询客户信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			// TODO: handle exception
		}
		return new ModelAndView("/Customer/CopyPub.jsp");
	}

	/**
	 * 打印选中
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView test(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap mapResult = new HashMap();
		ModelAndView modelAndView = new ModelAndView("/Excel/tempdata/PrintandSave.jsp");
		try {
			ASFuntion CHF = new ASFuntion();
			MultiDbIF db = (MultiDbIF) UTILSysProperty.context
					.getBean("MultiDbAction");
			String id = request.getParameter("id");
			// String PName =
			// "name,loginid,sex,borndate,educational,diploma,b.departname
			// bdname,c.departname cdname,rank,post,specialty";
			// String sql = "select
			// name,loginid,"+db.mIf("Sex='M'","'男'","'女'")+"
			// sex,borndate,educational,diploma,b.departname bdname,c.departname
			// cdname,rank,post,specialty from k_user a left join k_customer b
			// on a.departid=b.departid left join k_department c on
			// a.departmentid=c.autoid where 1=1 and id = '"+id+"'";
			String ids[] = id.split(",");
			ArrayList filename = new ArrayList();
			conn= new DBConnect().getConnect("");
			for(int j=0;j<ids.length;j++) {
				String sql = "select DepartID, DepartName,industryname,LinkMan,Phone,register,corporate,taxpayer,countrycess,terracess,BPR,enterprisecode,departdate,loginaddress,linkman,fax,phone,email,address," +
				"postalcode,businessbegin,businessend,remark,businessbound,stockowner " +
						"from k_Customer a,k_industry b where 1=1 and a.Property = '1'" +
						" and a.VocationID=b.industryid  and DepartID= '"+ids[j]+"'";
			conn = new DBConnect().getConnect("");
			// String [] Name = PName.split(",");
			HashMap VMap = new HashMap();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (rs.next()) {
				for (int i = 0; i < columnCount; i++) {
					VMap.put(rsmd.getColumnLabel(i + 1), CHF.showNull(rs
							.getString(i + 1)));
				}
			}

			PrintSetup printSetup = new PrintSetup(conn);

			printSetup.setVarMap(VMap);
			printSetup
					.setStrQuerySqls(new String[] { "select Name,Value from k_UserDef where ContrastID='"
							+ id + "' and Property ='cust' order by id asc" });
			printSetup.setStrExcelTemplateFileName("客户资料.xls");
			printSetup.setStrSheetName(new DbUtil(conn).queryForString("select DepartName from k_customer where DepartId='"+ids[j]+"'"));
			String excelName = printSetup.getExcelFile();
			filename.add(excelName);
			}
			modelAndView.addObject("filenameList", filename);

		} catch (Exception e) {
			Debug.print(Debug.iError, "查询客户失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 客户权限
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView customerRight(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String DepartID = CHF.showNull(request.getParameter("DepartID"));
		UserSession us = (UserSession) request.getSession().getAttribute(
				"userSession");

		String UserID = us.getUserName();
		Debug.printOut("UserID = |" + UserID);
		AccRightService arm = new AccRightService();
		Map map = new HashMap();
		map.put("UserID", UserID);
		map.put("DepartID", DepartID);

		try {
			conn = new DBConnect().getConnect("");
			// String sTable = arm.getARightTable("555555",conn);
			String sTable = arm.getATreeTable("555555", conn);
			// Debug.prtOut("sTable = |"+sTable);
			String al = arm.getAccpInfo(DepartID, conn);
			ArrayList alDep = arm.getDepartList(DepartID, conn);
			// String [] aDep = (String[]) alDep.toArray(new
			// String[alDep.size()]);

			map.put("sTable", sTable);
			map.put("alDep", alDep);
			map.put("al", al);
			map.put("alDep", alDep);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				DbUtil.close(conn);
		}

		return new ModelAndView("Customer/AccRight.jsp", map);
	}

	/**
	 * 调用BccUser.jsp页面
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView BccUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		AccRightService arm = new AccRightService();
		String opt = CHF.showNull(request.getParameter("opt"));
		String acc = CHF.showNull(request.getParameter("DepartID"));
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		try {
			conn = new DBConnect().getConnect("");
			if (opt.equals("1")) {
				String optSs = CHF.showNull(request.getParameter("optSs"));
				String optSr = CHF.showNull(request.getParameter("optSr"));

				out.print(arm.getBUserTable(optSs, optSr, conn,acc));
			} else if (opt.equals("2")) {
				arm.DelAccRight(acc, conn);
				out.print("已清除授权！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 调用AccUser.jsp页面
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView AccUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		AccRightService arm = new AccRightService();
		// String UserID = CHF.showNull(request.getParameter("UserID"));

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			conn = new DBConnect().getConnect("");
			out.print(arm.getAUserTable(conn));
			// out.print(arm.getAUserTable(UserID,conn));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 保存
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView SaveRight(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		AccRightService arm = new AccRightService();
		String DepartID = CHF.showNull(request.getParameter("DepartID"));
		String result = CHF.showNull(request.getParameter("result"));
		Debug.printOut("result = |" + result);

		try {
			conn = new DBConnect().getConnect("");
			arm.SaveAccRight(DepartID, result, conn);
			response.sendRedirect("customer.do?method=customerRight&&DepartID="
					+ DepartID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 设置客户等级
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strCustomerLevel);

		String customerid = request.getParameter("customerid");

		Connection conn = null;
		List list = new ArrayList();
		List listlast = new ArrayList();
		int num = 0;
		try {
			conn = new DBConnect().getConnect("");
			String sql1 = "select count(*) from asdb.oa_examinelibrary where ctype = '客户考核' and isenable='有效' and property='定性'";
			num = new DbUtil(conn).queryForInt(sql1);
			System.out.println("zyq:"+num);

			ExamineService ems = new ExamineService(conn);

			list = ems.Calculate("客户考核", customerid,"客户");
			for(int i=1;i<=num;i++) {
				listlast.add(list.get(list.size() - i));
			}
			for(int i=1;i<=num;i++) {
				list.remove(list.size() - 1);
			}
//			listlast.add(list.get(list.size()-1));
//			list.remove(list.size()-1);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

		model.addObject("customerid",customerid);
		model.addObject("customerExamineList",list);
		model.addObject("customerExamineDX",listlast);

		String toall = request.getParameter("toall");
		if(!"".equals(toall)&& toall!=null){
			model.addObject("toall",toall);
		}
		model.addObject("num",String.valueOf(num));

		return model;
	}


	/**
	 * 客户评级保存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveCustomerLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String username = userSession.getUserName();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = new DBConnect().getConnect("");

			String justlook = request.getParameter("justlook");
			String toall = request.getParameter("toall");

			//系统当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowtime = sdf.format(new Date());

			String customerid = request.getParameter("customerid");
			String totalscore = request.getParameter("totalscores");

			String sql = "";

			if("".equals(justlook)||justlook==null){

				String []cnames = request.getParameter("cnames").split(",");
				String []objvalues = request.getParameter("objvalues").split(",");
				String []sysscores = request.getParameter("sysscores").split(",");
				String []userscores = request.getParameter("userscores").split(",");

				sql = "insert into oa_examineresult (groupid,examinetype,customerid,examinename,examinevalue,result1,result2,recorder,starttime,examiner) values(?,?,?,?,?,?,?,?,?,?)";

				for(int i=0; i<cnames.length; i++){
					ps = conn.prepareStatement(sql);

					ps.setString(1, nowtime);
					ps.setString(2, "客户考核");
					ps.setString(3, customerid);
					ps.setString(4, cnames[i]);
					ps.setString(5, objvalues[i]);

					if(sysscores[i].length()>5){
						ps.setString(6, sysscores[i].substring(0,5));
					}else{
						ps.setString(6, sysscores[i]);
					}

					if(userscores[i].length()>5){
						ps.setString(7, userscores[i].substring(0,5));
					}else{
						ps.setString(7, userscores[i]);
					}

					ps.setString(8, username);
					ps.setString(9, "");
					ps.setString(10, "");

					ps.execute();
					ps.close();
				}


				String customername = "";

				sql = "select DepartName from k_customer where departid="+customerid;
				ps = conn.prepareStatement(sql);

				rs = ps.executeQuery();

				while(rs.next()){
					customername = rs.getString(1);
				}

				rs.close();
				ps.close();

				sql = "insert into oa_customerlevel(customerid,customername,scoure,customerlevel,recorder,recordtime,examiner,examtime) values(?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, customerid);
				ps.setString(2, customername);

				if(totalscore.length()>5){
					ps.setString(3, totalscore.substring(0, 5));
				}else{
					ps.setString(3, totalscore);
				}

				ps.setString(4, "");
				ps.setString(5, username);
				ps.setString(6, nowtime);
				ps.setString(7, "");
				ps.setString(8, "");

				ps.execute();

			}

			if("true".equals(toall)){
				response.sendRedirect("/AuditSystem/customer.do?method=levelHistory");
			}else{
				response.sendRedirect("/AuditSystem/customer.do?method=levelHistory&customerid=" + customerid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 客户评级验证
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkCustomerLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = new DBConnect().getConnect("");

			String customerid = request.getParameter("customerid");

			String sql = "select count(*) as result from oa_examineresult"
					   + " where starttime =''"
                       + " and customerid= ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);

		    rs = ps.executeQuery();

		    String resultset = "";
		    while(rs.next()){
		    	resultset = rs.getString("result");
		    }

		    response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();

		    if("0".equals(resultset)){
		    	out.print("yes");
		    }else{
		    	out.print("no");
		    }

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}


    /**
	 * 客户评级通过
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView passCustomerLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String username = userSession.getUserName();

		Connection conn = null;
		PreparedStatement ps = null;

		try {

			conn = new DBConnect().getConnect("");

			//系统当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime = sdf.format(new Date());

			String customerid = request.getParameter("customerid");
			String recordtime = request.getParameter("recordtime");
			String customerlevel = request.getParameter("customerlevel");
			String memo = request.getParameter("memo");

			String sql = "update oa_examineresult set starttime=?,examiner=?,memo=?"
					   + " where starttime ='' and examiner = ''"
                       + " and groupid= ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, nowtime);
			ps.setString(2, username);
			ps.setString(3, memo);
			ps.setString(4, recordtime);

			ps.execute();
			ps.close();

			sql = "update oa_customerlevel set examiner=?,examtime=?,customerlevel=?,memo=? where recordtime=?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, nowtime);
			ps.setString(3, customerlevel);
			ps.setString(4, memo);
			ps.setString(5, recordtime);

			ps.execute();

			if(!"".equals(customerid)&& customerid!=null){
				response.sendRedirect("/AuditSystem/customer.do?method=levelHistory&customerid=" + customerid);
			}else{
				response.sendRedirect("/AuditSystem/customer.do?method=levelHistory");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 客户评级记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView levelHistory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strLevelHistory);

		HttpSession session = request.getSession();

		try {

			String customerid = request.getParameter("customerid");

			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
						javax.servlet.http.HttpServletRequest request,
						javax.servlet.http.HttpServletResponse response)
						throws Exception {

					String customerName = this.getRequestValue("customerName");
					String recorder = this.getRequestValue("recorder");
					String examineTime = this.getRequestValue("examineTime");

					if (customerName != null && !customerName.equals("")) {
						customerName = "and customername like '" + customerName + "%' ";
					}

					if (recorder != null && !recorder.equals("")) {
						recorder = "and recorder like '" + recorder + "%' ";
					}

					if (examineTime != null && !examineTime.equals("")) {
						examineTime = "and examtime like '" + examineTime + "%' ";
					}

					this.setOrAddRequestValue("customerName", customerName);
					this.setOrAddRequestValue("recorder", recorder);
					this.setOrAddRequestValue("examineTime", examineTime);

				}
			};

			pp.setTableID("levellist");
			pp.setInputType("radio");

			pp.setWhichFieldIsValue(2);

			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setTrActionProperty(true);
			pp.setTrAction(" customerid='${customerid}' recordtime='${recordtime}' ");

			pp.setPrintColumnWidth("20,20,20,20,20,20,20,20");
			pp.setPrintTitle("客户评级历史记录");

			pp.addColumn("客户编号", "customerid");
			pp.addColumn("客户名称", "customername");
			pp.addColumn("客户分数", "scoure");
			pp.addColumn("客户级别", "customerlevel");
			pp.addColumn("提交人", "recorder");
			pp.addColumn("提交时间", "recordtime");
			pp.addColumn("审批人", "examiner");
			pp.addColumn("审批时间", "examtime");

			String sql = "";
			if("".equals(customerid)||customerid==null){
				sql = "select * from oa_customerlevel a," +
				" (select customerid as cid,max(recordtime) as rtime from oa_customerlevel group by customerid) b " +
				" where 1=1 and a.customerid = b.cid and a.recordtime = b.rtime " +
				" ${customerName} ${recorder} ${examineTime}" ;
			}else{
				sql = "select * from oa_customerlevel a," +
				" (select customerid as cid,max(recordtime) as rtime from oa_customerlevel group by customerid) b " +
				" where customerid="+customerid+"  and a.customerid = b.cid and a.recordtime = b.rtime " +
				" ${customerName} ${recorder} ${examineTime}";
				request.setAttribute("customerid", customerid);
			}
			
			
			pp.setSQL(sql);

			pp.setOrderBy_CH("recordtime");
			pp.setDirection("desc");

			pp.addSqlWhere("customerName", " ${customerName} ");
			pp.addSqlWhere("recorder", " ${recorder} ");
			pp.addSqlWhere("examineTime", " ${examineTime} ");

			session.setAttribute(DataGrid.sessionPre + pp.getTableID(),pp);


		} catch (Exception e) {

		}

		return model;
	}

	/**
	 * 客户评级历史记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView level(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strCustomerList);

		HttpSession session = request.getSession();

		try {

			String customerid = request.getParameter("customerid");

			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
						javax.servlet.http.HttpServletRequest request,
						javax.servlet.http.HttpServletResponse response)
						throws Exception {

					String customerName = this.getRequestValue("customerName");
					String recorder = this.getRequestValue("recorder");
					String examineTime = this.getRequestValue("examineTime");

					if (customerName != null && !customerName.equals("")) {
						customerName = "and customername like '" + customerName + "%' ";
					}

					if (recorder != null && !recorder.equals("")) {
						recorder = "and recorder like '" + recorder + "%' ";
					}

					if (examineTime != null && !examineTime.equals("")) {
						examineTime = "and examtime like '" + examineTime + "%' ";
					}

					this.setOrAddRequestValue("customerName", customerName);
					this.setOrAddRequestValue("recorder", recorder);
					this.setOrAddRequestValue("examineTime", examineTime);

				}
			};

			pp.setTableID("clevellist");
//			pp.setInputType("radio");

			pp.setWhichFieldIsValue(2);

			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setTrActionProperty(true);
			pp.setTrAction(" customerid='${customerid}' recordtime='${recordtime}' ");

			pp.setPrintColumnWidth("20,20,20,20,20,20,20,20");
			pp.setPrintTitle("客户评级历史记录");

			pp.addColumn("客户编号", "customerid");
			pp.addColumn("客户名称", "customername");
			pp.addColumn("客户分数", "scoure");
			pp.addColumn("客户级别", "customerlevel");
			pp.addColumn("提交人", "recorder");
			pp.addColumn("提交时间", "recordtime");
			pp.addColumn("审批人", "examiner");
			pp.addColumn("审批时间", "examtime");

			String sql = "";
			if("".equals(customerid)||customerid==null){
				sql = "select * from oa_customerlevel where 1=1 and examiner <> '' ${customerName} ${recorder} ${examineTime}" ;
			}else{
				sql = "select * from oa_customerlevel where customerid="+customerid+" and examiner <> '' ${customerName} ${recorder} ${examineTime}";
				request.setAttribute("customerid", customerid);
			}

			pp.setSQL(sql);

			pp.setOrderBy_CH("recordtime");
			pp.setDirection("desc");

			pp.addSqlWhere("customerName", " ${customerName} ");
			pp.addSqlWhere("recorder", " ${recorder} ");
			pp.addSqlWhere("examineTime", " ${examineTime} ");

			session.setAttribute(DataGrid.sessionPre + pp.getTableID(),pp);


		} catch (Exception e) {

		}

		return model;
	}

	
	/**
	 * 客户评级记录明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitCustomerLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView(_strCustomerLevelList);
		HttpSession session = request.getSession();

		String customerid = request.getParameter("customerid");

		DataGridProperty pp = new DataGridProperty();

		pp.setTableID("clevellist");

		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		pp.setPrintColumnWidth("20,20,50,20,20,20");
		pp.setPrintTitle("客户评级记录");

		pp.addColumn("客户编号", "customerid");
		pp.addColumn("考核性质", "examinetype");
		pp.addColumn("指标名称", "examinename");
		pp.addColumn("客户值", "examinevalue");
		pp.addColumn("系统分数", "result1");
		pp.addColumn("客户分数", "result2");
//		pp.addColumn("提交人", "recorder");
//		pp.addColumn("审批时间", "starttime");
//		pp.addColumn("审批人", "examiner");

		String sql = "select * from oa_examineresult where groupid='"+customerid+"'";

		pp.setSQL(sql);

		pp.setOrderBy_CH("autoid");
		pp.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + pp.getTableID(),pp);

		String onlylook = request.getParameter("onlylook");

		request.setAttribute("onlylook", onlylook);
		request.setAttribute("recode",customerid);


	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String customerId = "";
		String recorder = "";
		String starttime = "";
		String examiner = "";
		String memo = "";
		String customerlevel = "";

		try {

			conn = new DBConnect().getConnect("");

//			sql = "select customerid,recorder,starttime,examiner,memo from oa_examineresult where groupid='"+customerid+"'";
			sql = "select customerid,recorder,recordtime,examiner,memo,customerlevel from oa_customerlevel where recordtime='"+customerid+"'";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()){
				customerId = rs.getString(1);
				recorder = rs.getString(2);
				starttime = rs.getString(3);
				examiner = rs.getString(4);
				memo = rs.getString(5);
				customerlevel = rs.getString(6);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		model.addObject("customerid", customerId);
		model.addObject("recorder", recorder);
		model.addObject("starttime", starttime);
		model.addObject("examiner", examiner);
		model.addObject("memo", memo);
		model.addObject("customerlevel", customerlevel);

		String toall = request.getParameter("toall");
		if(!"".equals(toall)&& toall!=null){
			model.addObject("toall",toall);
		}

		return model;
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getestate (HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		try{

			conn = new DBConnect().getConnect("");

			String autoid = request.getParameter("autoid");

			PrintWriter out = response.getWriter();

			ContractService cs = new ContractService(conn);

			String temp = "";

			temp = cs.haveestate(autoid,"");

			if(!"".equals(temp)) {
				out.print("yes");
			} else {
				out.print("no");
			}
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}


	/**
	 * 获得单位信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getCustomerInfo (HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		PrintWriter out = response.getWriter();

		ASFuntion funtion = new ASFuntion();

		try{
			conn = new DBConnect().getConnect("");

			String customerId = request.getParameter("customerId");
			CustomerService customerService = new CustomerService(conn);
			Customer customer = customerService.getCustomer(customerId);

			String linkMan = funtion.showNull(customer.getLinkMan());

			out.write(linkMan);

		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 获得单位信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getInfo (HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			String customerId = request.getParameter("customerId");
			CustomerService customerService = new CustomerService(conn);
			Customer customer = customerService.getCustomer(customerId); 
			
			String customerShortName = customerService.getShortName(customerId);
			customer.setCustomerShortName(customerShortName);

			out.write(JSONObject.fromObject(customer).toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}


	public ModelAndView getDescription (HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			String autoId = request.getParameter("autoId");
			CustomerService customerService = new CustomerService(conn);
			String description = customerService.getDescription(autoId);

			out.write(description);

		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
			out.close();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}


	/**
	 * 批量删除客户
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView mutiDeleteList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {

			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String departmentid = userSession.getUserAuditDepartmentId();
			if(departmentid != null && !"".equals(departmentid)){
				departmentid = " and  departmentid = '"+departmentid+"' ";
			}else{
				departmentid  = "";
			}
			
			DataGridProperty pp = new DataGridProperty() {
				
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
					
						String CustomerID = this.getRequestValue("CustomerID");
						String inType = this.getRequestValue("inType");
						String linkMan = this.getRequestValue("linkMan");
						String manager = this.getRequestValue("manager");
						String groupname = this.getRequestValue("groupname");
						
						if (CustomerID != null && !"".equals(CustomerID)) {
							CustomerID = "and DepartID like '%" + CustomerID + "%' \n" ;
						}
						
						if (inType != null && !"".equals(inType)) {
							inType = "and industryname = '" + inType + "' \n" ;
						}
						if (linkMan != null && !"".equals(linkMan)) {
							linkMan = "and linkMan = '" + linkMan + "' \n";
						}
						if (manager != null && !"".equals(manager)) {
							manager = "and a.manager = '" + manager + "' \n";
						}
						if (groupname != null && !"".equals(groupname)) {
							groupname = "and a.groupname = '" + groupname + "' \n";
						}
						
						this.setOrAddRequestValue("CustomerID", CustomerID);
						this.setOrAddRequestValue("inType", inType);
						this.setOrAddRequestValue("linkMan", linkMan);
						this.setOrAddRequestValue("manager", manager);
						this.setOrAddRequestValue("groupname", groupname);
				}
			};
			pp.setTableID("customerMutiDelete");
			pp.setCancelPage(true);
			pp.setInputType("checkbox");
			pp.setUseBufferGrid(false) ;
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);

			/*
			String sql = "select DepartID,departcode,DepartName,industryname,LinkMan,Phone,AccPackageYear,projectNum \n"
				   + " from  k_Customer a left join k_industry b  on a.VocationID=b.industryid "
				   + " left join (SELECT CustomerId,group_concat(AccPackageYear) as AccPackageYear from c_accpackage group by CustomerId) c "
				   + " on a.DepartID = c.CustomerId left join (select count(*) as projectNum,customerid from z_project group by customerid) d on a.DepartID=d.customerid "
				   + "where Property = '1' " + departmentid;
			*/
			
			// sql设置
			String sql = "select DepartID,departcode,DepartName,industryname,LinkMan,email,Phone,AccPackageYear,projectNum \n"
					   + " from  ("
					   + "		select * from k_Customer a where Property = '1'  "+departmentid+"   "
					   /* 陆 2010.08.11修改 本人即使在项目组成员中，也不能维护项目所属客户。
					   + "		union "
					   + "		select c.* from z_auditpeople a,z_project b,k_customer c "
					   + "		where a.userid='"+userSession.getUserId()+"' and b.projectid=a.projectid and c.departid=b.customerid"
					   */
					   + " )  a left join k_industry b  on a.VocationID=b.industryid "
					   + " left join (SELECT CustomerId,group_concat(AccPackageYear order by AccPackageYear) as AccPackageYear from c_accpackage group by CustomerId) c "
					   + " on a.DepartID = c.CustomerId left join (select count(*) as projectNum,customerid from z_project group by customerid) d on a.DepartID=d.customerid "
					   + "where Property = '1' ${CustomerID} ${inType} ${linkMan} ${manager} ${groupname}";

			pp.setOrderBy_CH("DepartID") ;
			pp.setDirection("desc");


			pp.addColumn("单位编号", "DepartID");
			pp.addColumn("单位名称", "DepartName");
			pp.addColumn("会计制度类型", "industryname");
			pp.addColumn("单位联系人", "LinkMan");
			pp.addColumn("联系电话", "Phone");
			pp.addColumn("客户帐套", "AccPackageYear") ;
			pp.addColumn("客户项目数","projectNum") ;
			
			pp.addSqlWhere("CustomerID", "${CustomerID}");
			pp.addSqlWhere("inType", "${inType}");
			pp.addSqlWhere("linkMan", "${linkMan}");
			pp.addSqlWhere("manager", "${manager}");
			pp.addSqlWhere("groupname", "${groupname}");

			pp.setSQL(sql);

			request.getSession().setAttribute(
					DataGrid.sessionPre + pp.getTableID(), pp);

		} catch (Exception e) {
			Debug.print(Debug.iError, "查询客户信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			// TODO: handle exception
		}
		return new ModelAndView("/Customer/MutiList.jsp");
	}


	/**
	 * 选中批量删除客户
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectMutiDeleteList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {

			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("selectMutiDeleteList");
			pp.setCancelPage(true);
			pp.setInputType("checkbox");
			pp.setCheckboxIsChecked(true) ;
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);

			ASFuntion CHF = new ASFuntion() ;
			String customers = CHF.showNull(request.getParameter("departIds")) ;


			String sql = "select DepartID,departcode,DepartName,industryname,LinkMan,Phone,AccPackage,project \n"
				   + " from k_Customer a left join k_industry b on a.VocationID=b.industryid "
				   + " left join (select customerid,group_concat(concat('[',projectid,']',projectname) SEPARATOR '<br/>') as project "
				   + " from z_project group by customerid) c  on a.DepartID = c.CustomerId "
				   + "left join (select customerid,group_concat(accpackageyear SEPARATOR '<br/>' ) as AccPackage from c_accpackage "
				   + " group by customerid) d on a.DepartID=d.customerid where Property = '1' and a.DepartID in ("+customers+") ";

			pp.setOrderBy_CH("DepartID") ;
			pp.setDirection("desc");

			pp.addColumn("单位编号", "DepartID");
			pp.addColumn("单位名称", "DepartName");
			pp.addColumn("会计制度类型", "industryname");
			pp.addColumn("单位联系人", "LinkMan");
			pp.addColumn("联系电话", "Phone");
			pp.addColumn("帐套年份", "AccPackage") ;
			pp.addColumn("客户项目","project") ;

			pp.setSQL(sql);

			request.getSession().setAttribute(
					DataGrid.sessionPre + pp.getTableID(), pp);

		} catch (Exception e) {
			Debug.print(Debug.iError, "查询客户信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {

		}
		return new ModelAndView("/Customer/MutiList2.jsp");
	}



	public ModelAndView mutiDeleteCustomer(HttpServletRequest request,
			HttpServletResponse response){

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null ;
		try {
			conn = new DBConnect().getDirectConnect("");

			String departIds = CHF.showNull(request.getParameter("departIds"));
			UserdefService userDefService = new UserdefService(conn);
		  if(!"".equals(departIds)) {
			  String[] departIdsArr = departIds.split(",") ;


			for(int i=0;i<departIdsArr.length;i++) {


					String sql = "use asdb_"+departIdsArr[i];
					try {
						ps = conn.prepareStatement(sql) ;
						ps.execute() ;
					} catch(Exception e) {
						Debug.print(Debug.iError, "批量删除客户时业务库asdb_" + departIdsArr[i] + "不存在", e);
					}

					ProjectService projectService = new ProjectService(conn) ;
					List projectIds = projectService.getProjectIdsByCustomerId(departIdsArr[i]) ;

					for(int j=0;j<projectIds.size();j++) {
						String projectName = projectService.getProjectById((String)projectIds.get(j)).getProjectName();
						try {
							LogService.updateToLog(userSession, "删除工程 ：" + projectName,conn);
							projectService.removeProject((String)projectIds.get(j)) ;
						}catch(Exception e) {
							Debug.print(Debug.iError, "批量删除客户时删除项目"+projectName+"失败", e);
						}
					}

					sql = "use asdb";
					ps = conn.prepareStatement(sql) ;
					ps.execute() ;

					CustomerService customerService = new CustomerService(conn);
					String departName=customerService.getCustomer(departIdsArr[i]).getDepartName();
						
					try {
						//删除帐套信息
						sql = "delete from c_accpackage where CustomerID="+departIdsArr[i] ;
						ps = conn.prepareStatement(sql) ;
						ps.execute() ;
						LogService.updateToLog(userSession, "删除客户[" + departName + "]下帐套成功。", conn);
						//删除固定资产配置表
						sql = "delete from c_itemstat where concat(',',(select group_concat(AccPackageID) "
	                       +  " from c_accpackage where CustomerID="+departIdsArr[i]+"),',') like concat('%,',AccPackageID,',%')";
						ps = conn.prepareStatement(sql) ;
						ps.execute() ;
					}catch(Exception e) {
						Debug.print(Debug.iError, "批量删除客户时删除帐套失败", e);
					}
					
					try {
						customerService.remove(departIdsArr[i]);
						userDefService.removeUserdef(departIdsArr[i], "cust");
						LogService.updateToLog(userSession, "删除客户["+departName+"]", conn);
					}catch(Exception e) {
						Debug.print(Debug.iError, "删除客户信息失败", e);
					}

			}
		  }
		  response.sendRedirect(this._strSuccess) ;
		} catch (Exception e) {
			Debug.print(Debug.iError, "删除客户信息失败", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	//客户分配责任人
	private final String _strManager = "Customer/Customermanager.jsp";
	private final String _strManagerAdd = "Customer/CustomermanagerAdd.jsp";
	
	public ModelAndView manager(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strManager);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			ASFuntion CHF = new ASFuntion();
			String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
			String iText = "责任人";
			if(svalue.equals("大华")){ 
				//【责任人分配】要改为【承做人分配】，其它不变
				iText = "承做人";
			}
			
			String departmentid = "",userid = "",name = "",loginid = "";
			userid = userSession.getUserId();
			name = userSession.getUserName();
			loginid = userSession.getUserLoginId();
			
			String opt = UTILSysProperty.SysProperty.getProperty("是否分部门管理客户和项目");
			
			if("是".equals(opt)){
				departmentid = userSession.getUserAuditDepartmentId();
				if(departmentid != null && !"".equals(departmentid)){
					departmentid = " and  a.departmentid = '"+departmentid+"' ";
				}else{
					departmentid  = "";
				}
			}
			
			String opt1 = UTILSysProperty.SysProperty.getProperty("是否按负责人管理客户和项目");
			String strSQL = "";
//		    if("".equals(departmentid)){
//		    	//0、用户没有部门的，显示所有客户
//		    	strSQL = "		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID}  \n" ;
//		    }else{
//		    	strSQL = "select * from (" +
//		    	//1、合伙人、部门负责人、项目维护 看到本部门
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		inner join (select distinct departmentid as departments from k_role a inner join k_userrole b on a.id = b.rid inner join k_user c on b.userid = c.id where (a.rolename like '%项目维护%' or a.rolename like '%合伙人%' or a.rolename = '部门负责人') and b.userid = '"+userid+"') e on a.departmentid = e.departments \n" +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID}  \n" +
//		    	
//		    	"		union  \n" +
//		    	//2、授权客户 与部门表的ProjectPopedom 和人员表的ProjectPopedom 有关
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		INNER JOIN (" +
//				"			select CONCAT('.',b.departmentid, if(IFNULL(b.ProjectPopedom,'')='','.',b.ProjectPopedom),IFNULL(REPLACE(a.ProjectPopedom, ',', '.'),'.'),'.') as ProjectPopedom " +
//				"			FROM k_department a,k_user b  " +
//				"			where a.autoid = b.departmentid " +
//				"			AND b.id = '"+userid+"'  " +
//		    	"		) e on e.ProjectPopedom LIKE CONCAT('%.',a.departmentid,'.%') AND  a.Property = '1' \n" +
//		    	"		where a.Property = '1'  \n" +
//		    	"		${CustomerID}  \n" +
//		    	
//		    	"		union \n" + 
//		    	//3、是客户主负责人、副负责人的客户
//		    	"		select a.* " +
//		    	"		from k_Customer a " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		and (mostly = '"+name+"' or concat(',',subordination,',') like '%,"+name+",%') \n" +
//		    	"		${CustomerID}  \n" +
//		    	
//		    	"		union \n" + 
//		    	//4、显示责任人为当前人的客户
//		    	"		select a.* " +
//		    	"		from k_Customer a,k_customermanager b  " +
//		    	"		where a.Property = '1'  \n" +
//		    	"		and (b.user1 = '"+userid+"' or b.user2 = '"+userid+"') \n" +
//		    	"		and a.departid = b.customerid \n" +
//		    	"		${CustomerID}  \n" +
//		    	") a " ;
//		    }
		    
		    if("admin".equals(loginid)){
		    	strSQL = "		select distinct a.* " +
		    	"		from k_Customer a,k_customermanager b  " +
		    	" 		left join k_user c on b.user1 = c.id " +
    			" 		left join k_user d on b.user2 = d.id " +
		    	"		where a.Property = '1'  \n" +
		    	"		${CustomerID} ${departmentid} ${user} ${user_no} ${groupname} \n" ;
		    	
		    }else{
		    	String menuid = CHF.showNull(request.getParameter("menuid")); //菜单ID
		    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
		    	strSQL = "select * from (" +	
		    	//1、基本范围：显示责任人/承接人为当前人的客户
		    	"		select a.* " +
		    	"		from k_Customer a,k_customermanager b  " +
		    	" 		left join k_user c on b.user1 = c.id " +
    			" 		left join k_user d on b.user2 = d.id " +
		    	"		where a.Property = '1'  \n" +
		    	"		and (b.user1 = '"+userid+"' or b.user2 = '"+userid+"') \n" +
		    	"		and a.departid = b.customerid \n" +
		    	"		${CustomerID} ${departmentid} ${user} ${user_no} ${groupname} \n" +
		    	"		union \n" + 
		    	//2、补充范围：getUserIdPopedom 授权
		    	"		select a.* " +
		    	"		from k_Customer a " +
		    	" 		left join k_customermanager b on a.DepartID = b.customerid " +
		    	" 		left join k_user c on b.user1 = c.id " +
    			" 		left join k_user d on b.user2 = d.id " +
		    	"		where a.Property = '1'  \n" +
		    	"		and a.departmentid in ("+departments+") " +
		    	"		${CustomerID} ${departmentid} ${user} ${user_no} ${groupname} \n" +
		    	") a " ;		    	
		    }
		    
		    
		    
		    String sql = "";
		    if("是".equals(opt1)){
		    	//是否按负责人管理客户和项目
				sql = "select a.DepartID,a.DepartName,a.departcode," +
						" b1.departname as b1departname,b2.groupName as b2groupName,b3.groupName as b3groupName," +
						" user1,if(ifnull(user1,'')='','未分配',c.Name) as username1," +
						" user2,if(ifnull(user2,'')='','未分配',d.Name) as username2 " +
						"	from (" +
				    	strSQL + 
				    	" ${ORDERBY} ${LIMIT} ) a \n" +
						" left join k_customermanager b on a.DepartID = b.customerid " +
						" left join k_user c on b.user1 = c.id " +
						" left join k_user d on b.user2 = d.id " +
						" left join k_department b1 on a.departmentid = b1.autoid " +
						" left join k_group b2 on a.groupname = b2.groupid " +
						" left join k_group b3 on b3.parentid = 0 and b2.fullpath like concat(b3.fullpath,'%') " +
						" where a.Property = 1 ";		    	
		    }else{
		    	sql = "select a.DepartID,a.DepartName,a.departcode," +
		    			" b1.departname as b1departname,b2.groupName as b2groupName,b3.groupName as b3groupName," +
		    			" user1,if(ifnull(user1,'')='','未分配',c.Name) as username1," +
		    			" user2,if(ifnull(user2,'')='','未分配',d.Name) as username2 " +
		    			" from k_customer a " +
		    			" left join k_customermanager b on a.DepartID = b.customerid " +
		    			" left join k_user c on b.user1 = c.id " +
		    			" left join k_user d on b.user2 = d.id " +
		    			" left join k_department b1 on a.departmentid = b1.autoid " +
						" left join k_group b2 on a.groupname = b2.groupid " +
						" left join k_group b3 on b3.parentid = 0 and b2.fullpath like concat(b3.fullpath,'%') " +
		    			" where a.Property = 1 ${CustomerID} ${departmentid} ${user} ${user_no} ${groupname} " + departmentid;
		    }
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("managerList");
//			pp.setCancelPage(true);
			pp.setInputType("radio");
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);

			pp.setLimitByOwnEnable(true); 
			

			System.out.println(sql);
			pp.setOrderBy_CH("DepartID") ;
			pp.setDirection("asc");
			//pp.addColumn("内部编号", "DepartID");
			pp.addColumn("客户编号", "departcode");
			pp.addColumn("客户名称", "DepartName");
			pp.addColumn("所属集团/板块", "b2groupName");
			pp.addColumn("最终集团", "b3groupName");
			pp.addColumn("所属部门", "b1departname");
			pp.addColumn(iText + "一", "username1");
			pp.addColumn(iText + "二", "username2");

			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("承做人列表");
		    pp.setPrintColumnWidth("15,20,62,30,30");
		    pp.setPrintSqlColumn("DepartID,departcode,departname,b2groupName,b3groupName,b1departname,username1,username2");
			pp.setPrintColumn("内部编号`代码`客户名称`所属集团/板块`最终集团`所属部门`"+iText+"一`"+iText+"二");

			
			
			pp.addSqlWhere("CustomerID", " and a.DepartName like '%${CustomerID}%' ");
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");
			pp.addSqlWhere("user", " and (" +
					"b.user1 like '%${user}%' or b.user2 like '%${user}%' or " +
					"c.loginid like '%${user}%' or d.loginid like '%${user}%' or " +
					"c.name like '%${user}%' or d.name like '%${user}%' ) ");
			pp.addSqlWhere("user_no", " and ( 1='${user_no}' or (ifnull(b.user1,'') = '' or ifnull(b.user2,'') = '')) ");
			pp.addSqlWhere("groupname", " and a.groupname = '${groupname}' ");
			
			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}

	public ModelAndView update(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		ModelAndView modelAndView = new ModelAndView(_strManagerAdd);
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String CustomerID = CHF.showNull(request.getParameter("CustomerID"));
			
			String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
			String iText = "责任人";
			if(svalue.equals("大华")){ 
				//【责任人分配】要改为【承做人分配】，其它不变
				iText = "承做人";
			}
			
			conn = new DBConnect().getConnect("");

			CustomermanagerService cs = new CustomermanagerService(conn);
			Map map = cs.get(CustomerID);
			List<Map> mapList = cs.getManager_log(CustomerID);
			map.put("customerid", CustomerID);
			
			modelAndView.addObject("mapList", mapList);
			modelAndView.addObject("manager", map);
			modelAndView.addObject("iText", iText);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView managerSave(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
			String iText = "责任人";
			if(svalue.equals("大华")){ 
				//【责任人分配】要改为【承做人分配】，其它不变
				iText = "承做人";
			}
			
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String paramValue = request.getParameter(paramName);
				parameters.put(paramName, paramValue);
			
			}
			
			conn = new DBConnect().getConnect("");
			parameters.put("createUser", userSession.getUserId());
			CustomermanagerService cs = new CustomermanagerService(conn);
			cs.save(parameters);
			
			//发信息给承接人/责任人
			String customerid = (String)parameters.get("customerid");
			String user1 = (String)parameters.get("user1");
			String user2 = (String)parameters.get("user2");
			
			CustomerService customerService= new CustomerService(conn);
			String customername = customerService.getCustomer(customerid).getDepartName();
			
			PlacardService placardService=new PlacardService(conn); 
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setAddresser(userSession.getUserId());//发起
			placardTable.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			placardTable.setIsRead(0);
			placardTable.setIsReversion(0);
			placardTable.setIsNotReversion(0);
			placardTable.setCaption(iText + "分配");
			String sbString= userSession.getUserName() + "已经将【"+customername+"】(客户)分配你为第一承做人。";
			placardTable.setMatter(sbString);
			placardTable.setAddressee(user1); //接收的老大UserId
			placardService.AddPlacard(placardTable);
				
			sbString= userSession.getUserName() + "已经将【"+customername+"】(客户)分配你为第二承做人。";
			placardTable.setMatter(sbString);
			placardTable.setAddressee(user2); //接收的老大UserId
			placardService.AddPlacard(placardTable);
			
			response.sendRedirect(request.getContextPath() + "/customer.do?method=manager");
			
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	//
	
	private final String _strContract = "Customer/CustomerContract.jsp";//客户收益评估
	public ModelAndView contract(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strContract);
		try {
			
			UserSession userSession =(UserSession)request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			String strWhere = "";
			if(!"19".equals(loginid)){
				//admin看到全部,其它用户只能看到本部门以及有部门监控的权限
				strWhere = " AND a.departid IN ( \n" +
				"	SELECT DISTINCT customerid FROM z_project a ,( " +	
				"		SELECT DISTINCT a.projectid " +
				"		FROM z_auditpeople a,( " +
				"			SELECT b.* FROM k_user a ,k_user b " + 
				"			WHERE a.id = '" + loginid + "'   " +
				"			AND a.departmentid = b.departmentid " + 
				"			UNION   " +
				"			SELECT c.* FROM k_user a ,k_user c " + 
				"			WHERE a.id = '" + loginid + "'   " +
				"			AND IFNULL(c.departmentid,'') <> '' " +		
				"			AND CONCAT('.',a.projectpopedom,'.') LIKE CONCAT('%.',c.departmentid,'.%') " + 
				"		) b  " +
				"		WHERE 1=1 " +	
				"		AND a.userid = b.id " +
				"	) b WHERE a.ProjectID = b.projectid \n" +
				") ";
			}
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("ContractList");
//			pp.setCancelPage(true);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);

			String sql = " select a.departid,a.departname,b.cje, \n" +
			"	c.planmoney,d.bargainmoney , e.hj,e.projectYear,f.user1,f.user2,\n" +
			"	ifnull(b.cje,0) - ifnull(d.bargainmoney,0) as mon \n" +
			"	from k_customer a \n" +
			"	left join ( \n" +
			"		select sum(bargainmoney) as cje,customerid \n" +
			"		from oa_contract \n" +
			"		group by customerid \n" +
			"	)b on a.departid=b.customerid \n" +
			"	left join( \n" +
			"		select sum(b.planmoney) as planmoney,a.customerid \n" + 
			"		from oa_contract a,oa_bargainbalance b \n" +
			"		where a.bargainid=b.bargainid \n" +
			"		group by a.customerid  \n" +
			"	)c on a.departid=c.customerid \n" +
			"	left join( \n" +
			"		select sum(b.bargainmoney) as bargainmoney,a.customerid \n" + 
			"		from oa_contract a,oa_practicalbalance b \n" +
			"		where a.bargainid=b.cid \n" +
			"		group by a.customerid  \n" +
			"	)d on a.departid=d.customerid \n" +
			"	left join ( \n" +
			"		select customerid,count(*) as hj,min(projectYear) as projectYear \n" +
			"		from z_project group by customerid \n" +
			"	) e ON a.departid=e.customerid \n" +
			"	LEFT JOIN k_customermanager f ON a.DepartID = f.customerid	\n" +
			"	where a.property=1 \n ${userid}  ${projectYear} " + strWhere ;
//			"	order by  mon desc ";

			System.out.println(sql);
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
//			pp.setPrintColumnWidth("20,20,50,50,20,20,20");
			pp.setPrintTitle("客户收益评估");
			
			pp.setOrderBy_CH("mon") ;
			pp.setDirection("desc");
			pp.addColumn("客户编号", "departid");
			pp.addColumn("客户名称", "departname");
			pp.addColumn("签约年度", "projectYear","showCenter");
			pp.addColumn("项目个数", "hj","showCenter");
			pp.addColumn("合同金额", "cje","showMoney");
//			pp.addColumn("应收金额", "cje","showMoney");
			pp.addColumn("已收金额", "bargainmoney","showMoney");
			pp.addColumn("差额", "mon","showMoney");
			
			pp.addSqlWhere("userid", " and ifnull(f.user1,f.user2) = '${userid}' ");
			pp.addSqlWhere("projectYear", " and e.projectYear = '${projectYear}' ");
			
			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return modelAndView;
	}
	
	
//	
	private final String _strBusiness = "Customer/CustomerBusiness.jsp";
	private final String _strBusinessAdd = "Customer/CustomerBusinessAdd.jsp";
	private final String _strBusinessAudit = "Customer/CustomerBusinessAudit.jsp";
	private final String _strBusinessAuditAdd = "Customer/CustomerBusinessAuditAdd.jsp";
	private final String _strBusinessTracking = "Customer/CustomerBusinessTracking.jsp";
	private final String _strBusinessTrackingAdd = "Customer/CustomerBusinessTrackingAdd.jsp";
	
	public ModelAndView business(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBusiness);
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserLoginId();
			String userid = userSession.getUserId();
			conn = new DBConnect().getConnect("");
			String customerid = CHF.showNull(request.getParameter("customerid"));
			String frameTree = CHF.showNull(request.getParameter("frameTree"));
//			System.out.println(request.getRequestURI()); // /AuditSystem/customer.do
//			System.out.println(request.getServletPath()); // /customer.do
//			System.out.println(request.getContextPath()); // /AuditSystem
//			System.out.println(request.getQueryString()); // method=business&menuid=10000472
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("CustomerBusiness");
//			pp.setCancelPage(true);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
			String strSql = "";
			if(!"".equals(customerid)){
				strSql += " and a.customerid ='"+customerid+"' ";
			}
			
			String sql = "";
			if("admin".equals(loginid)){//admin看到全部
				sql = "select * from (" +
				"	select a.*, " +
				"	IFNULL(b.name,a.owner) AS ownername,b1.departname as b1departname, " +
				"	IFNULL(c.name,a.iuser) AS iusername,c1.departname as c1departname, " +
				"	if(deadtime >= CURDATE(),ifnull(a.state,'未过期'),'已过期') as astate " +
				"	from oa_business a  " +
				"	LEFT JOIN k_user b ON a.owner = b.id " + 
				"	LEFT JOIN k_department b1 ON b.departmentid = b1.autoid " +
				"	LEFT JOIN k_user c ON a.iuser = c.id  " +
				"	LEFT JOIN k_department c1 ON c.departmentid = c1.autoid " +
				") a where 1=1 " + strSql + 
				"	${customername} ${caption} ${owner} ${astate} " ;
			}else{//显示我是承做人的商机或者我发起的商机+人员菜单授权逻辑；
				String menuid = CHF.showNull(request.getParameter("menuid")); //菜单ID
		    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
		    	
				sql = "select * from (" + 
				"	select a.*, " +
				"	IFNULL(b.name,a.owner) AS ownername,b1.departname as b1departname, " +
				"	IFNULL(c.name,a.iuser) AS iusername,c1.departname as c1departname, " +
				"	if(deadtime >= CURDATE(),ifnull(a.state,'未过期'),'已过期') as astate " +
				"	from oa_business a  " +
				"	LEFT JOIN k_user b ON a.owner = b.id " + 
				"	LEFT JOIN k_department b1 ON b.departmentid = b1.autoid " +
				"	LEFT JOIN k_user c ON a.iuser = c.id  " +
				"	LEFT JOIN k_department c1 ON c.departmentid = c1.autoid " +
				"	where (a.owner = '"+userid+"' or a.iuser = '"+userid+"' or b.departmentid in ("+departments+")) " +
				") a where 1=1 " + strSql + 
				"	${customername} ${caption} ${owner} ${astate} " ;
			}
				
			pp.setPrintEnable(true);
			pp.setPrintTitle("商机登记");
			
			pp.setOrderBy_CH("deadtime") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "customername");
			pp.addColumn("标题", "caption");
			pp.addColumn("联系人", "contact");
			pp.addColumn("客户联系方式", "contactway");
			pp.addColumn("客户来源", "source");
			pp.addColumn("责任人部门", "b1departname");
			pp.addColumn("商机责任人", "ownername");
			pp.addColumn("商机截止日期", "deadtime");
			pp.addColumn("录入人部门", "c1departname");
			pp.addColumn("录入人", "iusername");
			pp.addColumn("录入日期", "idate");
			pp.addColumn("状态", "astate");
			
			pp.addSqlWhere("customername", " and a.customername like '%${customername}%' ");
			pp.addSqlWhere("caption", " and a.caption like '%${caption}%' ");
			pp.addSqlWhere("owner", " and a.owner = '${owner}' ");
			pp.addSqlWhere("astate", " and astate = if('是' = '${astate}','已过期','未过期')");
			
			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	/**
	 * 得到商机的状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView businessState(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		
		Connection conn = null;
		
		try {
			ASFuntion asf = new ASFuntion();
			
			response.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			conn = new DBConnect().getConnect("");
			
			String autoId = asf.showNull(request.getParameter("autoId"));
			
			
			String sql = "select state from  `oa_business` WHERE autoid='"+autoId+"'";
			
			String state = new DbUtil(conn).queryForString(sql);
			
			response.getWriter().write(state+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	public ModelAndView businessAdd(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		ModelAndView modelAndView = new ModelAndView(_strBusinessAdd);
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid"));
			String customerid = CHF.showNull(request.getParameter("customerid"));
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);

			Map map = cbs.get(autoid);
			
			String state = CHF.showNull((String)map.get("state"));
			if("".equals(state)){
				state = "待审核"; 
			}
			map.put("state", state);
			map.put("iuser",loginid);
			map.put("idate",CHF.getCurrentDate());
			if("".equals(autoid)){//新增
				map.put("customerid", customerid);
				if(!"".equals(customerid)){
					CustomermanagerService cms = new CustomermanagerService(conn);
					CustomerService customerService = new CustomerService(conn);
					Customer customer = customerService.getCustomer(customerid);
					Map map1 = cbs.getCustomer(customerid);
					Map cmsMap = cms.get(customerid);
					String user1 = CHF.showNull((String)cmsMap.get("user1"));
					String user2 = CHF.showNull((String)cmsMap.get("user2"));
					map.put("owner", ("".equals(user1) ? user2 : user1));//商机责任人
					map.put("customername", customer.getDepartName());
					map.putAll(map1);
				}
				map.put("memo", "项目需求：\n项目的采购时间：\n项目的预算：\n项目的决策人：");
				
			}
			modelAndView.addObject("business", map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//失败
	public ModelAndView businessFail(HttpServletRequest request,HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		
		Connection conn = null;
		
		try {
			
			response.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			conn = new DBConnect().getConnect("");
			
			String autoId = asf.showNull(request.getParameter("autoId"));
			
			String result = asf.showNull(request.getParameter("result"));
			
			String sql = "UPDATE `oa_business` SET state='已失败',result='"+result+"' WHERE autoid='"+autoId+"'";
			
			int row = new DbUtil(conn).executeUpdate(sql);
			
			response.getWriter().write(row+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	/**
	 * 商机批量导入跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView businessUploadSkip(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("/Customer/CustomerBusinessUpload.jsp");
	}
	
	/**
	 * 商机批量导入
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * @throws MatechException 
	 */
	public ModelAndView businessUpload(HttpServletRequest request, HttpServletResponse response) throws MatechException, Exception {

		PrintWriter out = null;
		Connection conn=null;

		try {
			
			ASFuntion  asf = new ASFuntion();
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			out = response.getWriter();
			
			Map parameters = null;
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String uploadtemppath = "";
			String strFullFileName = "";

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			System.out.println(parameters);

			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			//获取上传文件名
			strFullFileName = uploadtemppath+(String) parameters.get("filename");  
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			
			//获取上传目录
			uploadtemppath = (String) parameters.get("tempdir"); 

			int error = 0; //用于标记程序是否出错,出错了后面就不会再继续执行了

			out.println("预处理分析商机文件<br/>");
			out.flush();

			conn = new DBConnect().getDirectConnect("");

			//初始化业务对象
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn,strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("临时路径设置有误,请与系统管理员联系<br>");
				error = 1;
			}
			

			if (error > 0) {
				out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
			} else {
				org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
				out.println("继续处理装载<br>");
				out.flush();
				
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载用户内容!......");
				out.flush();
				
				//创建临时表
				new DbUtil(conn).execute("create table t_oa_business LIKE oa_business ");
				
				upload.setExcelNum("");
				upload.setExcelString("商机截止日期"); //设置中文列
				String[] exexlKmye = { "客户名称", "客户来源", "客户联系人","客户联系电话","标题","商机截止日期" };  //必须要设置的列名
				String[] tableKmye = { "customername", "source", "contact","contactway","caption","deadtime" }; //必须要设置的字段
				String[] exexlPzmxOpt = { "详细情况", "商机责任人"};
				String[] tablePzmxOpt = { "memo", "owner"};
				String[] exexlKmyeFixFields = { "state"};
				String[] excelKmyeFixFieldValues = { "待审核"};

				String result = "";
				result = upload.LoadFromExcel("商机列表", "t_oa_business",
				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
				exexlKmyeFixFields, excelKmyeFixFieldValues);

				out.println("装载商机完毕!<BR>");
				out.flush();
				
				out.println("开始新增商机!......<BR>");
				out.flush();
				
				
				CustomerBusinessService cbs = new CustomerBusinessService(conn);
				List<Map> listMap = cbs.getT(); //获取所有导入的商机
				
				for (int i = 0; i < listMap.size(); i++) {
					Map map = listMap.get(i);
					
					//得到客户名
					String customerName = (String)map.get("customername");
					
					String departId = asf.showNull(new DbUtil(conn).queryForString("SELECT departId from k_customer where departname = '"+customerName+"'"));
					
					//新增客户
					if("".equals(departId)){
						out.println("【"+customerName+"】客户不存在，系统将创建客户...<BR>");
						out.flush();
						
						departId = addBusinessCustomer(request, response, customerName);
						
						out.println("【"+customerName+"】客户创建成功...<BR>");
						out.flush();
						
					}
					
					//新增客户联系人
					if(!"".equals(departId)){
						
						String lxr = (String)map.get("contact"); //得到联系人名称
						try{
							
							String name = new DbUtil(conn).queryForString("SELECT name from k_manager where customerId = '"+departId+"' and name='"+lxr+"'");
							
							if("".equals(name)){
								out.println("【"+lxr+"】联系人不存在，系统将自动创建...<BR>");
								out.flush();
								
								//新增客户联系人
								ManagerService1 ms = new ManagerService1(conn);
								Manager1 manager1 = new Manager1();
								manager1.setCustomerid(departId);
								manager1.setName((String)map.get("contact"));
								manager1.setFixedphone((String)map.get("contactway"));
								ms.Manageradd(manager1);
								
								out.println("【"+lxr+"】联系人，系统自动创建成功...<BR>");
								out.flush();
							}

						}catch (Exception e) {
							out.println("<br>"+customerName+"客户，新增联系人【"+lxr+"】出错："+e.getMessage());
						}
						
					}
					
					//客户来源
					String source = asf.showNull((String)map.get("source"));
					if(!"".equals(source)){
						
						String name = asf.showNull(new DbUtil(conn).queryForString("SELECT name from k_dic where ctype = '客户来源' and name='"+source+"'"));

						if("".equals(name)){
							out.println("【"+source+"】客户来源，系统自动创建...<BR>");
							out.flush();

							DicService dicService = new DicService(conn); 
							Dic dic = new Dic();
							dic.setName(source);
							dic.setValue(source);
							dic.setUserdata("1");
							dic.setCtype("客户来源");
							
							dicService.add(dic);
							
							out.println("【"+source+"】客户来源，系统自动创建成功...<BR>");
							out.flush();
						}
					}
					
					
					map.put("customerid", departId);
					map.put("autoid", "");
					map.put("iuser", userSession.getUserId());
					map.put("auser", userSession.getUserId());
					//新增商机
					cbs.save(map);
					
					out.println("新增商机成功!...<BR>");
					out.flush();
					
				}
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"customer.do?method=business\">返回查询页面</a>\"</font>");
				
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"customer.do?method=businessUploadSkip\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "装载商机数据失败！", e);
			e.printStackTrace();
		} finally {
			
			//删除临时表
			new DbUtil(conn).execute("drop table t_oa_business");

			DbUtil.close(conn);
			 
		}
		return null;
	}
	
	/**
	 * 市场机会分析 新增客户
	 * @param request
	 * @param response
	 * @param customerName
	 * @return
	 */
	public String  addBusinessCustomer(HttpServletRequest request,HttpServletResponse response,String customerName){
		
		//不存在客户，就新建
		Customer customer = new Customer();
		DELAutocode t = new DELAutocode();
		String area = "";
		String departId = "";
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		ASFuntion CHF = new ASFuntion();
		
		try{
			departId = t.getAutoCode("KHDH", "");
			customer.setDepartId(departId);

			conn = new DBConnect().getConnect("");
			String sql = "select a.enname from k_department a,k_department b where b.autoid = ? and a.level0 = 1 and b.fullpath like concat(a.fullpath,'%')";
			DbUtil db = new DbUtil(conn);
			area = db.queryForString(sql,new String[]{userSession.getUserAuditDepartmentId()});
	
		
			CustomerService customerService  = new CustomerService(conn);
			
			String time = CHF.replaceStr(CHF.replaceStr(CHF.getCurrentDate()+CHF.getCurrentTime(), "-", ""), ":", "");
			String did2 = t.getAutoCode("ZDBH","",new String[]{area,time}); //单位编号(外部编号)
			customer.setCustdepartid(did2);
			
			customer.setVocationId("1"); //会计制度类型  1：A、企业（旧准则）
			customer.setProperty("1"); //无解，不知道是什么意思
			customer.setDepartName(customerName);
			customer.setDepartmentid(userSession.getUserAuditDepartmentId());
			customer.setEstate("潜在");
			customerService.addCustomer(customer); //新增客户
		} catch (Exception e) {
			area = "";
		} finally {
			DbUtil.close(conn);
		}
		
		return departId ;
		
	}
	
	
	static final String newCustomer = "newCustomer/AddandEdit.jsp"; //客户承接登记
	static final String bidProject = "BidProject/addAndEdit.jsp"; //招投标登记
	
	/**
	 * 发起招投标登记  或 客户承接登记
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView startBidOrContinue(HttpServletRequest request,HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		
		Connection conn = null;
		ModelAndView modelAndView = null;
		try {
			
			response.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			
			String autoId = asf.showNull(request.getParameter("autoId"));
			
			String opt = asf.showNull(request.getParameter("opt"));
			Map map = null;
			if(!"".equals(autoId) && !"".equals(opt)){

				String state = "";
				if("0".equals(opt)){
					state = "已发起招投标登记";
				} else if("1".equals(opt)){ 
					state = "已发起客户承接登记";
				}
				
				conn = new DBConnect().getConnect("");
				
				String sql = "UPDATE `oa_business` SET state='"+state+"' WHERE autoid='"+autoId+"'";
				
				new DbUtil(conn).executeUpdate(sql); //修改状态
				
				CustomerService cs = new CustomerService(conn);
				CustomerBusinessService cms = new CustomerBusinessService(conn);
				
				map = cms.get(autoId);
				
				String departmentId = cs.getCustomerId(map.get("customername").toString());
				
				if(!"".equals(departmentId)){
					
					map.put("customername", departmentId);
				}
				
			}
			//招投标登记
			if("0".equals(opt)){
				modelAndView = new ModelAndView(bidProject);
			} else if("1".equals(opt)){ //客户承接登记
				modelAndView = new ModelAndView(newCustomer);
			}

			modelAndView.addObject("map",map);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	public ModelAndView customer(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String customerid = CHF.showNull(request.getParameter("customerid"));
			String customername = CHF.showNull(request.getParameter("customername"));
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);
			cbs.setDepartmentid(userSession.getUserAuditDepartmentId());
			if("".equals(customerid) && !"".equals(customername)){
				customerid = customername;
			}
			
			Map map = cbs.getCustomer(customerid); //customerid 有可能是ID，也有可能是Name
			
			customerid = (String)map.get("customerid"); //统一为customerid 
			CustomermanagerService cms = new CustomermanagerService(conn);
			Map cmsMap = cms.get(customerid);
			String user1 = CHF.showNull((String)cmsMap.get("user1"));
			String user2 = CHF.showNull((String)cmsMap.get("user2"));
			map.put("owner", ("".equals(user1) ? user2 : user1));//商机责任人

			String result = ""; //||###:####||###:####||
			Set coll = map.keySet();
			for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				String value = (String) map.get(key);
				
				result += key + ":" + value + "||";
			}
			if(!"".equals(result)){
				result = "||" + result;
			}
			
//			System.out.println(result);
			response.setContentType("text/html;charset=utf-8"); // 设置编码
			PrintWriter out = response.getWriter();
			out.write(result);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView businessSave(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String paramValue = request.getParameter(paramName);
				parameters.put(paramName, paramValue);
			
			}

			String opt = CHF.showNull((String)parameters.get("opt"));
			
			conn = new DBConnect().getConnect("");

			CustomerBusinessService cbs = new CustomerBusinessService(conn);
			CustomermanagerService cm = new CustomermanagerService(conn);
			//发信息给商机责任人
			String autoid =  CHF.showNull((String)parameters.get("autoid"));
			String customerid =  CHF.showNull((String)parameters.get("customerid"));
			String caption =  CHF.showNull((String)parameters.get("caption"));
			String deadtime =  CHF.showNull((String)parameters.get("deadtime"));
			String owner =  CHF.showNull((String)parameters.get("owner"));
			String customername =  CHF.showNull((String)parameters.get("customername"));
			String departId = "";
			CustomerService customerService= new CustomerService(conn);
			Customer customer = customerService.getCustomer(customerid);
			if(customer != null){
				customername = customer.getDepartName();
			
			}else{
				Connection conn2  = null;
				if(!"".equals(customername) || "".equals(customerid)){
					
					departId = CHF.showNull(new DbUtil(conn).queryForString("SELECT departId from k_customer where departname ='"+customername+"'"));
					
					if("".equals(departId)){
						
						//不存在客户，就新建
						customer = new Customer();
						DELAutocode t = new DELAutocode();
						departId = t.getAutoCode("KHDH", "");
						customerid = departId;
						customer.setDepartId(departId);
						String area = "";
						try{
							String sql = "select a.enname from k_department a,k_department b where b.autoid = ? and a.level0 = 1 and b.fullpath like concat(a.fullpath,'%')";
							conn2 = new DBConnect().getConnect("");
							DbUtil db = new DbUtil(conn);
							area = db.queryForString(sql,new String[]{userSession.getUserAuditDepartmentId()});
						} catch (Exception e) {
							area = "";
						} finally {
							DbUtil.close(conn2);
						}
						String time = CHF.replaceStr(CHF.replaceStr(CHF.getCurrentDate()+CHF.getCurrentTime(), "-", ""), ":", "");
						String did2 = t.getAutoCode("ZDBH","",new String[]{area,time}); //单位编号(外部编号)
						customer.setCustdepartid(did2);
						
						customer.setVocationId("1"); //会计制度类型  1：A、企业（旧准则）
						customer.setProperty("1"); //无解，不知道是什么意思
						customer.setDepartName(customername);
						customer.setDepartmentid(userSession.getUserAuditDepartmentId());
						customer.setEstate("潜在");
						customerService.addCustomer(customer); //新增客户
					}
					parameters.put("customerid", customerid);
					if(!"".equals(owner)){
						
						Map map = new HashMap();
						map.put("customerid", departId);
						map.put("user1", owner);
						cm.save(map);
					}
				}
			}
			PlacardService placardService=new PlacardService(conn); 
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setAddresser(userSession.getUserId());//发起
			placardTable.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			placardTable.setIsRead(0);
			placardTable.setIsReversion(0);
			placardTable.setIsNotReversion(0);
			placardTable.setCaption("商机管理");
			String sbString= userSession.getUserName() + "分配了"+customername+"(客户)的【"+caption+"】潜在项目由你跟进，该项目截至日期是"+deadtime+"，请尽快处理。";
			placardTable.setMatter(sbString);
			placardTable.setAddressee(owner); //接收的老大UserId
			if("".equals(autoid)){
				//新增
				placardService.AddPlacard(placardTable);
			}else{
				//修改商机责任人
				String owner_old = CHF.showNull((String)cbs.get(autoid).get("owner"));
				if(!owner_old.equals(owner)){
					placardService.AddPlacard(placardTable);	
				}
			}
			
			cbs.save(parameters);
			String frameTree = CHF.showNull((String)parameters.get("frameTree"));
			String strUrl = ""; 
			if("1".equals(frameTree)){
				strUrl = "&customerid="+customerid+"&frameTree="+frameTree;
			}
			
			if("audit".equals(opt)){//商机审核
				response.sendRedirect(request.getContextPath() + "/customer.do?method=businessAudit" + strUrl);
			}else if("track".equals(opt)){//商机跟踪
				response.sendRedirect(request.getContextPath() + "/customer.do?method=businessTracking" + strUrl);
			}else{//商机list
				response.sendRedirect(request.getContextPath() + "/customer.do?method=business" + strUrl);
			}
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	 
	
	public ModelAndView businessDel(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			
			String autoid = CHF.showNull(request.getParameter("autoid"));
			String customerid = CHF.showNull(request.getParameter("customerid"));
			String frameTree = CHF.showNull(request.getParameter("frameTree"));
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);
			
			String result = cbs.del(autoid);
			
			String strUrl = ""; 
			if("1".equals(frameTree)){
				strUrl = "&customerid="+customerid+"&frameTree="+frameTree;
			}
			
			PrintWriter out = null ;
			response.setContentType("text/html;charset=UTF-8") ;
			out = response.getWriter() ;
			out.println("<script>alert('"+result+"');window.location=\""+request.getContextPath() + "/customer.do?method=business"+strUrl+"\"</script>");
			out.close();
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	public ModelAndView businessAudit(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBusinessAudit);
		
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("businessAudit");
//			pp.setCancelPage(true);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
//			String sql = "select a.*  from oa_business a where state = '待审核' " ;
			String sql = "";
			if("19".equals(loginid)){//admin看到全部
				sql = "select a.*  ,IFNULL(b.name,a.owner) AS ownername from oa_business a LEFT JOIN k_user b ON a.owner = b.id where a.state = '待审核'" ;
			}else{//其它用户只能看到本部门以及有部门监控的权限
				sql = "SELECT a.*,IFNULL(b.name,a.owner) AS ownername  " +
				"	FROM oa_business a " + 
				"	LEFT JOIN ( " +
				"		SELECT b.* FROM k_user a ,k_user b " + 
				"		WHERE a.id = '"+loginid+"'  " +
				"		AND a.departmentid = b.departmentid " +
				"		UNION  " +
				"		SELECT c.* FROM k_user a ,k_user c " +
				"		WHERE a.id = '"+loginid+"'  " +
				"		AND CONCAT('.',a.projectpopedom,'.') LIKE CONCAT('%.',c.departmentid,'.%') " +
				"	) b ON a.owner = b.id" +
				"	where a.state = '待审核'";
			}
			
			System.out.println(sql);
			pp.setPrintEnable(true);
			pp.setPrintTitle("商机审核");
			
			pp.setOrderBy_CH("deadtime") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "customername");
			pp.addColumn("标题", "caption");
			pp.addColumn("联系人", "contact");
			pp.addColumn("联系方式", "contactway");
			pp.addColumn("客户来源", "source");
			pp.addColumn("商机责任人", "ownername");
			pp.addColumn("商机截止日期", "deadtime");
			pp.addColumn("状态", "state");
			
//			pp.addSqlWhere("CustomerID", " and a.DepartID = '${CustomerID}' ");
			
			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return modelAndView;
	}

	
	public ModelAndView businessAuditAdd(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		ModelAndView modelAndView = new ModelAndView(_strBusinessAuditAdd);
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid"));
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);

			Map map = cbs.get(autoid);
			
			map.put("auser",loginid);
			map.put("adate",CHF.getCurrentDate());
			
			modelAndView.addObject("business", map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView businessTracking(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBusinessTracking);
		
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("businessTracking");
//			pp.setCancelPage(true);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
//			String sql = "select a.*  from oa_business a where state = '待跟踪'" ;
			String sql = "";
			if("19".equals(loginid)){//admin看到全部
				sql = "select a.*  ,IFNULL(b.name,a.owner) AS ownername from oa_business a LEFT JOIN k_user b ON a.owner = b.id where a.state = '待跟踪'" ;
			}else{//其它用户只能看到责任人是自己的
				sql = "select a.*  ,IFNULL(b.name,a.owner) AS ownername from oa_business a LEFT JOIN k_user b ON a.owner = b.id where a.state = '待跟踪' and a.owner = '"+loginid+"'" ;
			}
			
			System.out.println(sql);
			pp.setPrintEnable(true);
			pp.setPrintTitle("商机跟踪"); 
			
			pp.setOrderBy_CH("deadtime") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "customername");
			pp.addColumn("标题", "caption");
			pp.addColumn("联系人", "contact");
			pp.addColumn("联系方式", "contactway");
			pp.addColumn("客户来源", "source");
			pp.addColumn("商机责任人", "ownername");
			pp.addColumn("商机截止日期", "deadtime");
			pp.addColumn("状态", "state");
			
//			pp.addSqlWhere("CustomerID", " and a.DepartID = '${CustomerID}' ");
			
			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return modelAndView;
	}
	
	
	public ModelAndView businessTrackingAdd(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		ModelAndView modelAndView = new ModelAndView(_strBusinessTrackingAdd);
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid"));
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);

			Map map = cbs.get(autoid);
			
			map.put("tracking",loginid);
			map.put("tdate",CHF.getCurrentDate());
			
			modelAndView.addObject("business", map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
//	批量设置客户权限
	String _userRight = "Customer/UserRight.jsp";
	public ModelAndView userRight(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_userRight);
		try {
			ASFuntion CHF = new ASFuntion();
			String userid = CHF.showNull(request.getParameter("userid"));
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			DataGridProperty pp = new DataGridProperty() ;
			
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("客户列表");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
			String sql = "select a.DepartID,departcode,a.DepartName,industryname,LinkMan,email,Phone,AccPackageYear,projectNum,IFNULL(e.departname,'') AS departmentname,IF(f.DepartID IS NOT NULL,'[是授权客户]','') AS fDepartID "
					   + " from  k_Customer a "
					   + " left join k_industry b  on a.VocationID=b.industryid "
					   + " left join (SELECT CustomerId,group_concat(AccPackageYear order by AccPackageYear) as AccPackageYear from c_accpackage group by CustomerId) c on a.DepartID = c.CustomerId "
					   + " left join (select count(*) as projectNum,customerid from z_project group by customerid) d on a.DepartID=d.customerid "
					   + " LEFT JOIN k_department e ON a.departmentid = e.autoid "
					   + " LEFT JOIN k_accright f ON f.userid ='"+userid+"' AND a.DepartID = f.DepartID "
					   + " where a.Property = '1' ";

			pp.setSQL(sql); 
			pp.setOrderBy_CH("a.DepartID") ;
			pp.setDirection("desc");
			pp.setInputType("checkbox");
			
			pp.addColumn("单位编号", "departcode");
			pp.addColumn("单位名称", "DepartName");
		//	pp.addColumn("会计制度类型", "industryname");
			pp.addColumn("单位联系人", "LinkMan");
			pp.addColumn("联系电话", "Phone");
			pp.addColumn("电子邮件", "email");
			pp.addColumn("客户帐套", "AccPackageYear") ;
			pp.addColumn("客户项目数","projectNum") ;
			pp.addColumn("所属部门","departmentname") ;
			pp.addColumn("授权客户","fDepartID") ;
			pp.setWhichFieldIsValue(1);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			modelAndView.addObject("userid", userid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	public ModelAndView saveUserRight(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		AccRightService arm = new AccRightService();

		try {
			String userid = CHF.showNull(request.getParameter("userid"));
			String customers = CHF.showNull(request.getParameter("customers"));
			
			conn = new DBConnect().getConnect("");
			arm.SaveUserRight(userid, customers, conn);
			
			response.sendRedirect("customer.do?method=userRight&userid="+ userid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	/**
	 * //综合查询 CustomerView
	 * 1、收款提醒 CustomerRemind
	 * 2、收费按业务组成分析 
	 * 3、收费按部门组成分析 CustomerCharge
	 * 4、客户应收账款前十大分析 CustomerVable
	 * 5、客户收款前十大分析 CustomerPact
	 * 6、客户收益综合分析 CustomerGains
	 */
	private final String CustomerView = "Customer/CustomerView.jsp";//综合查询
	private final String CustomerRemind = "Customer/CustomerRemind.jsp";
	private final String CustomerCharge = "Customer/CustomerCharge.jsp";
	private final String CustomerVable = "Customer/CustomerVable.jsp";
	private final String CustomerPact = "Customer/CustomerPact.jsp";
	private final String CustomerGains = "Customer/CustomerGains.jsp";
	public ModelAndView view(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerView);
		return modelAndView;
	}
	
	//收款提醒
	public ModelAndView remind(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerRemind);
		try {
			DataGridProperty pp = new DataGridProperty() ;
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setWhichFieldIsValue(1);
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("收款提醒");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
			String sql = "select a.*,c.departname from oa_bargainbalance a  " +
				"	inner join oa_contract b on a.bargainid = b.bargainid " +
				"	left join k_department c on b.departmentid = c.autoid" +
				"	where 1=1 ${beginDate} ${endDate} " ;

			pp.setSQL(sql); 
			pp.setOrderBy_CH("plandate") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "firstparty");
			pp.addColumn("归属部门", "departname");
			pp.addColumn("计划收款日期", "plandate");
			pp.addColumn("计划收款金额", "planmoney","showMoney");
			//pp.addColumn("状态","departmentname") ;
			
			pp.addSqlWhere("beginDate", " and plandate >= '${beginDate}' ");//起始日期
			pp.addSqlWhere("endDate", " and plandate <= '${endDate}' ");//结束日期

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	
	//3、收费按部门组成分析
	public ModelAndView charge(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerCharge);
		try {
			DataGridProperty pp = new DataGridProperty() ;
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setWhichFieldIsValue(1);
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("收费按业务组成分析");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
			String sql = "select ifnull(c.autoid,0) autoid,ifnull(c.departname,'无部门') as departname," +
					"	sum(a.bargainmoney) as abargainmoney ,sum(ifnull(b.bargainmoney,0)) as bbargainmoney," +
					"	sum(ifnull(b.bargainmoney,0))/sum(a.bargainmoney) * 100 as bb " +
					"	from oa_contract a " +
					"	left join oa_practicalbalance b on b.cid = a.bargainid " +
					"	left join k_department c on a.departmentid = c.autoid " +
					"	where 1=1 ${year} " +
					"	group by ifnull(c.autoid,0) " ;

			pp.setSQL(sql); 
			pp.setOrderBy_CH("c.autoid") ;
			pp.setDirection("asc");
			
			pp.addColumn("分所/部门", "departname");
			pp.addColumn("合同金额", "abargainmoney","showMoney");
			pp.addColumn("占比", "bb");
			pp.addColumn("已费金额", "bbargainmoney","showMoney");
			
			pp.addSqlWhere("year", " and bargainterm like '%${year}%' ");//年份

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	//4、客户应收账款前十大分析
	public ModelAndView vable(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerVable);
		try {
			DataGridProperty pp = new DataGridProperty() ;
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setWhichFieldIsValue(1);
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("客户应收账款前十大分析");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
			String sql = "select *,ROUND(money/moneyall * 100,2) as bb from ( " +
			"		select * from ( " +
			"			select a.customerid,c.DepartName," +
			"			group_concat(if(mostly='',null,mostly),if(subordination='',null,subordination)) as mostly," +
			"			sum(a.bargainmoney - ifnull(b.bargainmoney,0)) as money " +
			"			from oa_contract a  " +
			"			left join oa_practicalbalance b on b.cid = a.bargainid " +
			"			left join k_customer c on a.customerid = c.DepartID " +
			"			where 1=1 ${departmentid} " +
			"			group by a.customerid " +
			"		) a 	 " +
			"		order by money desc limit 10 " +

			"	) a,( " +
			"		select sum(a.bargainmoney - ifnull(b.bargainmoney,0)) as moneyall " +
			"		from oa_contract a  " +
			"		left join oa_practicalbalance b on b.cid = a.bargainid " +
			"		where 1=1 ${departmentid}  " +
			"	) b  " +
			"	where 1=1" ;

			pp.setSQL(sql); 
			pp.setOrderBy_CH("money") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "departname");
			pp.addColumn("应收账款", "money","showMoney");
			pp.addColumn("总应收账款", "moneyall","showMoney");
			pp.addColumn("占比", "bb","showMoney");
			pp.addColumn("客户责任人", "mostly");
			
			pp.setTableHead("客户名称,应收账款分析{应收账款,总应收账款,占比},客户责任人");
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");//分所/部门

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	//5、客户收款前十大分析
	public ModelAndView pact(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerPact);
		try {
			DataGridProperty pp = new DataGridProperty() ;
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setWhichFieldIsValue(1);
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("客户收款前十大分析");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
		    String sql = "select *,ROUND(money/moneyall * 100,2) as bb from ( " +
			"		select * from ( " +
			"			select a.customerid,c.DepartName," +
			"			group_concat(if(mostly='',null,mostly),if(subordination='',null,subordination)) as mostly," +
			"			sum(a.bargainmoney) as money " +
			"			from oa_contract a  " +
			"			left join k_customer c on a.customerid = c.DepartID " +
			"			where 1=1 ${departmentid} " +
			"			group by a.customerid " +
			"		) a 	 " +
			"		order by money desc limit 10 " +

			"	) a,( " +
			"		select sum(a.bargainmoney) as moneyall " +
			"		from oa_contract a  " +
			"		where 1=1 ${departmentid}  " +
			"	) b  " +
			"	where 1=1" ;

			pp.setSQL(sql); 
			pp.setOrderBy_CH("money") ;
			pp.setDirection("desc");
			
			pp.addColumn("客户名称", "departname");
			pp.addColumn("合同金额", "money","showMoney");
			pp.addColumn("总合同金额", "moneyall","showMoney");
			pp.addColumn("占比", "bb","showMoney");
			pp.addColumn("客户责任人", "mostly");
			
			pp.setTableHead("客户名称,合同金额分析{合同金额,总合同金额,占比},客户责任人");
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");//分所/部门

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	//6、客户收益综合分析
	public ModelAndView gains(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(CustomerGains);
		try {
			DataGridProperty pp = new DataGridProperty() ;
			
			String tmpName = "tt_" + DELUnid.getCharUnid();
			pp.setTableID(tmpName);
			pp.setCustomerId("");
			
			// 基本设置
			pp.setWhichFieldIsValue(1);
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("客户收益综合分析");
		    pp.setPrintColumnWidth("14,62,55,17,20");

			// sql设置
		    String sql = "select * from ( " +
		    "		select *,ROUND(money/moneyall * 100,2) as bb from ( " +	
		    "			select a.customerid,c.DepartName, " +
		    "			group_concat(if(mostly='',null,mostly),if(subordination='',null,subordination)) as mostly, " +
		    "			sum(a.bargainmoney) as money " +
		    "			from oa_contract a 		 " +
		    "			left join k_customer c on a.customerid = c.DepartID " +
		    "			where 1=1 ${departmentid} ${year} " +
		    "			group by a.customerid " +
		    "		) a,( " +
		    "			select sum(a.bargainmoney) as moneyall " +
		    "			from oa_contract a  " +
		    "			left join oa_practicalbalance b on b.cid = a.bargainid " +
		    "			where 1=1 ${departmentid} ${year} " +
		    "		) b  " +
		    "	) a,( " +
		    "		select *,ROUND(bmoney/bmoneyall * 100,2) as bbb from ( " +	
		    "			select a.customerid as bcustomerid, " +
		    "			sum(a.bargainmoney - ifnull(b.bargainmoney,0)) as bmoney " +
		    "			from oa_contract a  " +
		    "			left join oa_practicalbalance b on b.cid = a.bargainid " +
		    "			where 1=1 ${departmentid} ${year} " +
		    "			group by a.customerid " +	
		    "		) a,( " +
		    "			select sum(a.bargainmoney - ifnull(b.bargainmoney,0)) as bmoneyall " +
		    "			from oa_contract a  " +
		    "			left join oa_practicalbalance b on b.cid = a.bargainid " +
		    "			where 1=1 ${departmentid} ${year} " +
		    "		) b  " +
		    "	) b " + 
		    "	where 1=1 " +
		    "	and a.customerid = b.bcustomerid " ;

			pp.setSQL(sql); 
			pp.setOrderBy_CH("customerid") ;
			pp.setDirection("asc");
			
			pp.addColumn("客户名称", "departname");
			pp.addColumn("客户责任人", "mostly");
			pp.addColumn("合同金额", "money","showMoney");
			pp.addColumn("总合同金额", "moneyall","showMoney");
			pp.addColumn("占比", "bb","showMoney");
			pp.addColumn("应收账款", "bmoney","showMoney");
			pp.addColumn("总应收账款", "bmoneyall","showMoney");
			pp.addColumn("占比", "bbb","showMoney");
			
			pp.setTableHead("客户名称,客户责任人,合同金额分析{合同金额,总合同金额,占比},应收账款分析{应收账款,总应收账款,占比}");
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");//分所/部门
			pp.addSqlWhere("year", " and a.bargainterm like '%${year}%' ");//年份
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tmpName", pp.getTableID());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	/**
	 * 市场机会查看
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView businessLook(HttpServletRequest request,HttpServletResponse response) throws Exception { 
		ModelAndView modelAndView = new ModelAndView(_strBusinessLook);
		Connection conn = null;
		try{
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid"));

			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String loginid = userSession.getUserId();
			
			conn = new DBConnect().getConnect("");
			CustomerBusinessService cbs = new CustomerBusinessService(conn);

			Map map = cbs.get(autoid);
			
			String state = CHF.showNull((String)map.get("state"));
			if("".equals(state)){
				state = "待审核"; 
			}
			map.put("state", state);
			map.put("iuser",loginid);
			map.put("idate",CHF.getCurrentDate());
			modelAndView.addObject("business", map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	//批量删除
	public ModelAndView newAnnianList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		   ModelAndView modelAndView = new ModelAndView(newAnnianList);
		   
		   UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		   Connection conn = new DBConnect().getConnect("");
			
			String bcRoleString =new ASFuntion().showNull(new DbUtil(conn).queryForString("SELECT a.rid FROM k_userrole a LEFT JOIN k_role b ON a.`rid` = b.`id` WHERE a.userid='"+userSession.getUserId()+"' AND b.`rolename` = '市场部负责人'"));
			if(!"".equals(bcRoleString)){
				bcRoleString = " OR 1=1 ";
			}
			DbUtil.close(conn);
			//a.iuser = '" +userSession.getUserName()+"' or		,a.iuser
		   String sql ="select a.autoid,a.customername,a.customerlevel,a.source,a.indistry,a.companyType,a.demandType,a.demand,a.contact,a.rank,a.contactway" +
		   		",a.email,a.QQorMSN ,a.iuser,b.name as distriman,c.name as follow from oa_business a left join k_user b on a.distriman = b.Id" +
		   		" left join k_user c on a.follow= c.Id " +
		   		" where 1=1 and (a.iuser = '" +userSession.getUserId()+"' or a.distriman="+userSession.getUserId()+" or a.follow = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' "+bcRoleString+" )" +
		   		" ${customerId} ${customerlevel} ${source} ${indistry} ${companyType}" +
		   		"${demandType} ${distriman} ${follow}";
		   
		   DataGridProperty pp = new DataGridProperty();
		   pp.setTableID("annianbusiness");
		   pp.setCustomerId("");
		   pp.setPageSize_CH(20);
		   pp.setWhichFieldIsValue(1);
		   pp.setInputType("checkbox");
		   pp.setOrderBy_CH("autoid");
		   pp.setDirection_CH("asc");
		   pp.setPrintEnable(true);
		   pp.setPrintVerTical(false);
		   
		   pp.setSQL(sql);
		   pp.addColumn("客户名称", "customername");
		   pp.addColumn("客户级别", "customerlevel");
		   pp.addColumn("客户来源", "source");
		   pp.addColumn("行业类型", "indistry");
		   pp.addColumn("公司性质", "companyType");
		   pp.addColumn("需求类型", "demandType");
		   pp.addColumn("具体需求", "demand");
		   pp.addColumn("联系人", "contact");
		   pp.addColumn("职位", "rank");
		   pp.addColumn("电话","contactway");
		   pp.addColumn("邮箱", "email");
		   pp.addColumn("QQ/MSN", "QQorMSN");
		   pp.addColumn("分配人", "distriman");
		   pp.addColumn("跟进人", "follow");
		   
		   pp.addSqlWhere("customerId", " and customerId = '${customerId}'");
		   pp.addSqlWhere("customerlevel", " and customerlevel like '${customerlevel}'");
		   pp.addSqlWhere("source", " and source = '${source}'");
		   pp.addSqlWhere("indistry", " and indistry = '${indistry}'");
		   pp.addSqlWhere("companyType", " and companyType = '${companyType}'");
		   pp.addSqlWhere("demandType", " and demandType = '${demandType}'");
		   pp.addSqlWhere("distriman", " and distriman = '${distriman}'");
		   pp.addSqlWhere("follow", " and follow = '${follow}'");
		   request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		   return modelAndView;
		
	}
	public ModelAndView annianList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		   ModelAndView modelAndView = new ModelAndView(annianList);
		   
		   UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		   Connection conn = new DBConnect().getConnect("");
			
			String bcRoleString =new ASFuntion().showNull(new DbUtil(conn).queryForString("SELECT a.rid FROM k_userrole a LEFT JOIN k_role b ON a.`rid` = b.`id` WHERE a.userid='"+userSession.getUserId()+"' AND b.`rolename` = '市场部负责人'"));
			if(!"".equals(bcRoleString)){
				bcRoleString = " OR 1=1 ";
			}
			//DbUtil.close(conn);
			//品牌部
			Connection conn1 = new DBConnect().getConnect("");
			String departmentId =new ASFuntion().showNull(new DbUtil(conn1).queryForString("SELECT autoid FROM k_department WHERE departname = '品牌部'"));
			String ppRoleString =new ASFuntion().showNull(new DbUtil(conn1).queryForString("SELECT * FROM k_user WHERE departmentId =' "+departmentId +"' "));
			if(!"".equals(ppRoleString)){
				ppRoleString = " OR 1=1 ";
			}
			DbUtil.close(conn1);
			//a.iuser = '" +userSession.getUserName()+"' or		,a.iuser
		   String sql ="select a.autoid,a.customername,a.customerlevel,a.source,a.indistry,a.companyType,a.demandType,a.demand,a.contact,a.rank,a.contactway" +
		   		",a.email,a.QQorMSN ,a.iuser,b.name as distriman,c.name as follow ,f.activeName from oa_business a left join k_user b on a.distriman = b.Id" +
		   		" left join k_user c on a.follow= c.Id " +
		   		"left join k_activecompany e on a.customername = e.company "+
		   		"left join k_active f on e.active=f.autoId "+ 																																		//+bcRoleString+
		   		" where 1=1 and (a.iuser = '" +userSession.getUserId()+"' or a.distriman="+userSession.getUserId()+" or a.follow = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' "+ bcRoleString+ ppRoleString+ " )" +//and (a.iuser = '" +userSession.getUserId()+"' or a.distriman="+userSession.getUserId()+" or a.follow = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' "+bcRoleString+" )
		   		" ${customerId} ${customerlevel} ${source} ${indistry} ${companyType}" +
		   		"${demandType} ${distriman} ${follow}";
		   
		   DataGridProperty pp = new DataGridProperty();
		   pp.setTableID("annianbusiness");
		   pp.setCustomerId("");
		   pp.setPageSize_CH(20);
		   pp.setWhichFieldIsValue(1);
		   pp.setInputType("radio");
		   pp.setOrderBy_CH("autoid");
		   pp.setDirection_CH("asc");
		   pp.setPrintEnable(true);
		   pp.setPrintVerTical(false);
		   
		   pp.setSQL(sql);
		   pp.addColumn("客户名称", "customername");
		   pp.addColumn("客户级别", "customerlevel");
		   pp.addColumn("客户来源", "source");
		   
		   pp.addColumn("客户参与活动", "activeName");
		   
		   pp.addColumn("行业类型", "indistry");
		   pp.addColumn("公司性质", "companyType");
		   pp.addColumn("分配人", "distriman");
		   pp.addColumn("跟进人", "follow");
		   pp.addColumn("需求类型", "demandType");
		   pp.addColumn("具体需求", "demand");
		   pp.addColumn("联系人", "contact");
		   pp.addColumn("职位", "rank");
		   pp.addColumn("电话","contactway");
		   pp.addColumn("邮箱", "email");
		   pp.addColumn("QQ/MSN", "QQorMSN");
		   
		   pp.addSqlWhere("customerId", " and customerId = '${customerId}'");
		   pp.addSqlWhere("customerlevel", " and customerlevel like '${customerlevel}'");
		   pp.addSqlWhere("source", " and source = '${source}'");
		   pp.addSqlWhere("indistry", " and indistry = '${indistry}'");
		   pp.addSqlWhere("companyType", " and companyType = '${companyType}'");
		   pp.addSqlWhere("demandType", " and demandType = '${demandType}'");
		   pp.addSqlWhere("distriman", " and distriman = '${distriman}'");
		   pp.addSqlWhere("follow", " and follow = '${follow}'");
		   request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		   return modelAndView;
		
	}
	public ModelAndView annianEdit(HttpServletRequest request,HttpServletResponse response) throws Exception{
			ModelAndView modelAndView = new ModelAndView(annianAddAndEdit);
			ASFuntion asf = new ASFuntion();
			String id = asf.showNull(request.getParameter("id"));
			Connection conn = null;
			Business business = null;
			try{
				conn = new DBConnect().getConnect("");
				CustomerService customerService = new CustomerService(conn); 
				if(!id.equals("")){
					business = customerService.getBusiness(id);
					modelAndView.addObject("business",business);
				}	
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			
			return modelAndView;
	}
	
	public void getCustomerDetail(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String customer = asf.showNull(request.getParameter("customer"));
		try{
			  conn = new DBConnect().getConnect("");
			  CustomerService customerService = new CustomerService(conn);
			  String json = customerService.getCustomerJson(customer);
			  out.write(json);
			  out.close();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(conn);
		  }
	}
	
	public ModelAndView annianadd(HttpServletRequest request,HttpServletResponse response){
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		 
		 ModelAndView modelAndView = new ModelAndView(annianList);
	     ASFuntion asf = new ASFuntion();
	     	Connection conn = null;
	    
	    	// conn = new DBConnect().getConnect("");
	    	 String customerid = asf.showNull(request.getParameter("customerId"));
	    	 String customerName = asf.showNull(request.getParameter("customerName"));
	    	 String customerlevel = asf.showNull(request.getParameter("customerlevel"));
	    	 String source = asf.showNull(request.getParameter("source"));
	    	 String indistry = asf.showNull(request.getParameter("indistry"));
	    	 String companyType = asf.showNull(request.getParameter("companyType"));
	    	 String demandType = asf.showNull(request.getParameter("demandType"));
	    	 String demand = asf.showNull(request.getParameter("demand"));
	    	 String contact = asf.showNull(request.getParameter("contact"));
	    	 String rank = asf.showNull(request.getParameter("rank"));
	    	 String contactway = asf.showNull(request.getParameter("contactway"));
	    	 String email = asf.showNull(request.getParameter("email"));
	    	 String QQorMSN = asf.showNull(request.getParameter("QQorMSN"));
	    	 String distriman = asf.showNull(request.getParameter("distriman"));
	    	 String follow = asf.showNull(request.getParameter("follow"));
	    	 String iuser = asf.showNull(userSession.getUserId());
	    	 
	    	 Business business = new Business();
	    	 business.setCustomerId(customerid);
	    	 business.setCustomername(customerName);
	    	 business.setCustomerlevel(customerlevel);
	    	 business.setSource(source);
	    	 business.setIndistry(indistry);
	    	 business.setCompanyType(companyType);
	    	 business.setDemandType(demandType);
	    	 business.setDemand(demand);
	    	 business.setContact(contact);
	    	 business.setRank(rank);
	    	 business.setContactway(contactway);
	    	 business.setEmail(email);
	    	 business.setQQorMSN(QQorMSN);
	    	 business.setDistriman(distriman);
	    	 business.setFollow(follow);
	    	 business.setIuser(iuser);
	     try{
	    	 conn = new DBConnect().getConnect("");
	    	 CustomerService customerService =  new CustomerService(conn);
	    	 customerService.annianSave(business);
	    	 response.sendRedirect(request.getContextPath()+"/customer.do?method=annianList");
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }finally{
	    	 DbUtil.close(conn);
	     }
		 return modelAndView;
	}
	
	public ModelAndView annianUpdate(HttpServletRequest request,HttpServletResponse response){
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		
		ModelAndView modelAndView = new ModelAndView(annianList);
		ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 String customerid = asf.showNull(request.getParameter("customerId"));
    	 String customerName = asf.showNull(request.getParameter("customerName"));
    	 String customerlevel = asf.showNull(request.getParameter("customerlevel"));//
    	 String source = asf.showNull(request.getParameter("source"));
    	 String indistry = asf.showNull(request.getParameter("indistry"));  //
    	 String companyType = asf.showNull(request.getParameter("companyType"));//
    	 String demandType = asf.showNull(request.getParameter("demandType"));
    	 String demand = asf.showNull(request.getParameter("demand"));
    	 String contact = asf.showNull(request.getParameter("contact"));
    	 String rank = asf.showNull(request.getParameter("rank"));
    	 String contactway = asf.showNull(request.getParameter("contactway"));
    	 String email = asf.showNull(request.getParameter("email"));
    	 String QQorMSN = asf.showNull(request.getParameter("QQorMSN"));
    	 String distriman = asf.showNull(request.getParameter("distriman"));
    	 String follow = asf.showNull(request.getParameter("follow"));
    	 String auser = asf.showNull(userSession.getUserId());
    	 
    	 Business business = new Business();
    	 business.setAutoId(autoId);
    	 business.setCustomerId(customerid);
    	 business.setCustomername(customerName);
    	 business.setCustomerlevel(customerlevel);
    	 business.setSource(source);
    	 business.setIndistry(indistry);
    	 business.setCompanyType(companyType);
    	 business.setDemandType(demandType);
    	 business.setDemand(demand);
    	 business.setContact(contact);
    	 business.setRank(rank);
    	 business.setContactway(contactway);
    	 business.setEmail(email);
    	 business.setQQorMSN(QQorMSN);
    	 business.setDistriman(distriman);
    	 business.setFollow(follow);
    	 business.setAuser(auser);
    	 Connection  conn = null;
    	 try{
    		 conn = new DBConnect().getConnect("");
    		 CustomerService customerService = new CustomerService(conn);
    		 customerService.annianUpdate(business);
    		 response.sendRedirect(request.getContextPath()+"/customer.do?method=annianList");
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }finally{
    		 DbUtil.close(conn);
    	 }
		return modelAndView;
	}
	
	public ModelAndView annianDelete(HttpServletRequest request,HttpServletResponse response) throws IOException{
		ModelAndView  modelAndView = new ModelAndView();
		ASFuntion asf = new ASFuntion();
		String id = asf.showNull(request.getParameter("id"));
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			CustomerService customerService = new CustomerService(conn);
			customerService.annianDelete(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		 response.sendRedirect(request.getContextPath()+"/customer.do?method=annianList");
		return modelAndView;
	}
	/*
	 *批量设置分配人和跟进人
	 */
	public ModelAndView annianMultiMan(HttpServletRequest request,HttpServletResponse response) throws Exception{
			ModelAndView modelAndView = new ModelAndView(annianMulList);
			  String sql ="select a.autoid,a.customername,a.customerlevel,a.source,a.indistry,a.companyType,a.demandType,a.demand,a.contact,a.rank,a.contactway" +
		   		",a.email,a.QQorMSN,b.name as distriman,c.name as follow from oa_business a left join k_user b on a.distriman = b.Id" +
		   		" left join k_user c on a.follow= c.Id where 1=1 ${customerId} ${customerlevel} ${source} ${indistry} ${companyType}" +
		   		"${demandType} ${distriman} ${follow}";
		   
		   DataGridProperty pp = new DataGridProperty();
		   pp.setTableID("annianMulList");
		   pp.setCustomerId("");
		   pp.setPageSize_CH(20);
		   pp.setWhichFieldIsValue(1);
		   pp.setInputType("checkbox");
		   pp.setOrderBy_CH("autoid");
		   pp.setDirection_CH("asc");
		   pp.setPrintEnable(true);
		   pp.setPrintVerTical(false);
		   
		   pp.setSQL(sql);
		   pp.addColumn("客户名称", "customername");
		   pp.addColumn("客户级别", "customerlevel");
		   pp.addColumn("客户来源", "source");
		   pp.addColumn("行业类型", "indistry");
		   pp.addColumn("公司性质", "companyType");
		   pp.addColumn("需求类型", "demandType");
		   pp.addColumn("具体需求", "demand");
		   pp.addColumn("联系人", "contact");
		   pp.addColumn("职位", "rank");
		   pp.addColumn("电话","contactway");
		   pp.addColumn("邮箱", "email");
		   pp.addColumn("QQ/MSN", "QQorMSN");
		   pp.addColumn("分配人", "distriman");
		   pp.addColumn("跟进人", "follow");
		   
		   pp.addSqlWhere("customerId", " and customerId = '${customerId}'");
		   pp.addSqlWhere("customerlevel", " and customerlevel like '${customerlevel}'");
		   pp.addSqlWhere("source", " and source = '${source}'");
		   pp.addSqlWhere("indistry", " and indistry = '${indistry}'");
		   pp.addSqlWhere("companyType", " and companyType = '${companyType}'");
		   pp.addSqlWhere("demandType", " and demandType = '${demandType}'");
		   pp.addSqlWhere("distriman", " and distriman = '${distriman}'");
		   pp.addSqlWhere("follow", " and follow = '${follow}'");
		   request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			return modelAndView;
		
	}
	public ModelAndView annianMulSet(HttpServletRequest request,HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(annianList);
		 ASFuntion asf = new ASFuntion();
		 String flag = asf.showNull(request.getParameter("flag"));
		 String customerId = asf.showNull(request.getParameter("customer"));
		 String userId = asf.showNull(request.getParameter("userId"));
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerService customerService = new CustomerService(conn);
			 customerService.annianSetmul(customerId, flag, userId);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(conn);
		 }
		 response.sendRedirect(request.getContextPath()+"/customer.do?method=annianList");
		 return modelAndView;
	}
	
	public ModelAndView annianAddFollow(HttpServletRequest request,HttpServletResponse response) throws IOException{
			ModelAndView modelAndView = new ModelAndView(annianList);
			Connection conn = null;
			ASFuntion asf = new ASFuntion();
			String autoId = asf.showNull(request.getParameter("autoId"));
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			String createUser = userSession.getUserId();
			String createDepartment = userSession.getUserAuditDepartmentId();
			String createTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
			try{
				conn = new DBConnect().getConnect("");
				CustomerService customerService = new CustomerService(conn);
				customerService.annianAddFollow(autoId, createUser, createDepartment, createTime);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			 response.sendRedirect(request.getContextPath()+"/customer.do?method=annianList");
			return modelAndView;
	}
	//批量删除
	public ModelAndView piLiangDel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String result = "false";
		Connection conn = null;
		PrintWriter out = null;
		try {
		    out = response.getWriter();
		    response.setCharacterEncoding("utf-8");
		    request.setCharacterEncoding("utf-8");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion asf = new ASFuntion();
			String ids = asf.showNull(request.getParameter("ids"));
		
			conn = new DBConnect().getConnect("");
			
			CustomerService customerService = new CustomerService(conn);
			
			//批量删除
				String[] id = ids.split(",");
				
				for (int i = 0; i < id.length; i++) {
					customerService.delAPlacard(id[i]);
				}
				result = "true";
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
}
