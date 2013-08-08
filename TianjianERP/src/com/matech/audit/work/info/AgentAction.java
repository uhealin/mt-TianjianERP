package com.matech.audit.work.info;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.framework.listener.UserSession;


public class AgentAction extends MultiActionController {
	private static final String birthTodepart = "/info/birthTodepart.jsp";
	private static final String birthTocust   = "/info/birthToCust.jsp";
	private static final String departDate    = "/info/departDate.jsp";
	
	/**
	 * 我部门近期生日的人
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView birthToDepart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(birthTodepart);
		DataGridProperty pp = new DataGridProperty();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String sql = "SELECT id,`name`,date_format(bornDate,'%m-%d') as bornDate,educational,b.departName,a.`Rank`,a.`mobilePhone`,a.`email` " 
				    + "  FROM k_user a LEFT JOIN k_department b ON a.`departmentid` = b.`autoid` "
					+ "	WHERE borndate <> '' AND DATE_FORMAT(CURDATE(),'%m-%d')  BETWEEN DATE_FORMAT(DATE_ADD(borndate,INTERVAL -15 DAY),'%m-%d') "
					+ "  AND DATE_FORMAT(DATE_ADD(borndate,INTERVAL  15 DAY),'%m-%d')   AND departmentid = "+userSession.getUserAuditDepartmentId();
	
			pp.setSQL(sql);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setOrderBy_CH("bornDate");
			pp.setTableID("birthToDepart");
	    	pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
		//	pp.setPrintTitle("人员生日清单");
		//	pp.setInputType("radio");
		//	pp.setOrderBy_CH("bornDate");
		//	pp.setDirection("desc");
			pp.setSQL(sql);
			
			pp.addColumn("姓名","name");
			pp.addColumn("生日", "bornDate");
			pp.addColumn("所在部门", "departName");
			pp.addColumn("职级","Rank");
			pp.addColumn("手机", "mobilePhone");
			pp.addColumn("电子邮件", "email");
			
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			return modelAndView;

	}
	/**
	 * 近期生日的客户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView birthToCust(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(birthTocust);
		DataGridProperty pp = new DataGridProperty();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String sql = " SELECT   b.`autoid`,a.departName,b.`position`,b.`name`,date_format(b.`birthday`,'%m-%d') as birthday,b.`mobilephone`,b.`fixedphone`,b.`email` FROM ("	
					+" SELECT a.*,b.`customerid` FROM k_Customer a,k_customermanager b LEFT JOIN k_user c ON b.user1 = c.id "
					+" LEFT JOIN k_user d ON b.user2 = d.id  WHERE a.Property = '1'  "
					+" AND (b.user1 = '"+userSession.getUserId()+"' OR b.user2 = '"+userSession.getUserId()+"') "
					+" AND a.departid = b.customerid UNION "
					+" SELECT a.*,b.`customerid` FROM k_Customer a LEFT JOIN k_customermanager b ON a.DepartID = b.customerid"
					+" LEFT JOIN k_user c ON b.user1 = c.id   LEFT JOIN k_user d ON b.user2 = d.id "
					+" WHERE a.Property = '1'  AND a.departmentid =  "+userSession.getUserAuditDepartmentId()		
					+" ) a  LEFT JOIN k_manager b ON a.customerid = b.`customerid` "
					+" WHERE b.`birthday` <> ''"
					+" AND DATE_FORMAT(CURDATE(),'%m-%d')  BETWEEN DATE_FORMAT(DATE_ADD(birthday,INTERVAL -15 DAY),'%m-%d') "
					+" AND DATE_FORMAT(DATE_ADD(birthday,INTERVAL  15 DAY),'%m-%d')   ";
	
			pp.setSQL(sql);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setOrderBy_CH("birthday");
			pp.setTableID("birthToCust");
	    	pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
		//	pp.setPrintTitle("人员生日清单");
		//	pp.setInputType("radio");
		//	pp.setOrderBy_CH("bornDate");
		//	pp.setDirection("desc");
			pp.setSQL(sql);
			
			pp.addColumn("客户公司","departName");
			pp.addColumn("客户联系人", "name");
			pp.addColumn("职位", "position");
			pp.addColumn("生日","birthday");
			pp.addColumn("手机", "mobilePhone");
			pp.addColumn("传真", "fixedphone");
			pp.addColumn("电子邮件", "email");

			
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			return modelAndView;

	}
	/**
	 * 客户公司成立周年
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView departDate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(departDate);
		DataGridProperty pp = new DataGridProperty();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String sql = " SELECT  a.departId,a.departName,b.`position`,b.`name`,departdate,"
					 +" b.`mobilephone`,b.`fixedphone`,b.`email`" 
					 +" FROM ( SELECT a.*,b.`customerid` FROM k_Customer a,k_customermanager b "
					 +" LEFT JOIN k_user c ON b.user1 = c.id  LEFT JOIN k_user d ON b.user2 = d.id " 
					 +" WHERE a.Property = '1'   AND (b.user1 = '"+userSession.getUserId()+"' OR b.user2 = '"+userSession.getUserId()+"')  "
					 +" AND a.departid = b.customerid UNION  SELECT a.*,b.`customerid` FROM k_Customer a " 
					 +" LEFT JOIN k_customermanager b ON a.DepartID = b.customerid LEFT JOIN k_user c ON b.user1 = c.id "  
					 +" LEFT JOIN k_user d ON b.user2 = d.id  WHERE a.Property = '1'  AND a.departmentid =  "+userSession.getUserAuditDepartmentId()+" ) a "
					 +" LEFT JOIN k_manager b ON a.customerid = b.`customerid`  WHERE b.`birthday` <> '' "
					 +" AND DATE_FORMAT(CURDATE(),'%m-%d')  BETWEEN DATE_FORMAT(DATE_ADD(birthday,INTERVAL -15 DAY),'%m-%d')"  
					 +" AND DATE_FORMAT(DATE_ADD(birthday,INTERVAL  15 DAY),'%m-%d')";
	
			pp.setSQL(sql);
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setOrderBy_CH("birthday");
			pp.setTableID("departDate");
	    	pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			
		//	pp.setPrintTitle("人员生日清单");
		//	pp.setInputType("radio");
		//	pp.setOrderBy_CH("bornDate");
		//	pp.setDirection("desc");
			pp.setSQL(sql);
			
			pp.addColumn("客户公司","departName");
			pp.addColumn("成立日期", "departdate");
			pp.addColumn("传真", "fixedphone");
			pp.addColumn("客户联系人", "name");
			pp.addColumn("联系人职位", "position");
			pp.addColumn("联系人手机", "mobilePhone");
			pp.addColumn("电子邮件", "email");

			
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			return modelAndView;

	}


}
