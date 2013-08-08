package com.matech.audit.work.customer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.ManagerService;
import com.matech.audit.service.customer.model.Manager;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ManagerAction extends MultiActionController {

	public ModelAndView save(HttpServletRequest request, 
								HttpServletResponse response)
								throws Exception{
		
//		PrintWriter out = response.getWriter();
		
		ASFuntion CHF = new ASFuntion();
				
		String customerid = request.getParameter("departid");
		
		customerid = CHF.showNull(customerid);
		
		String[] position = request.getParameterValues("position");
		String[] manager_name = request.getParameterValues("manager_name");
		String[] manager_sex = request.getParameterValues("manager_sex");
		String[] qualification = request.getParameterValues("qualification");
		String[] mobilephone = request.getParameterValues("mobilephone");
		String[] fixedphone = request.getParameterValues("fixedphone");
		String[] email = request.getParameterValues("email");
		String[] contact1 = request.getParameterValues("contact1");
		String[] contact2 = request.getParameterValues("contact2");
		
		Manager manager = null;
		List list = new ArrayList();
		
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			ManagerService ms = new ManagerService(conn);
			ms.deleteByCustomerid(customerid);
			
			/*if(position == null){
				out.println("<script language='javascript'>window.close();</script>");
				return null;
			}*/
			
			if(position != null){
				for(int i=0; i<position.length; i++){
					manager = new Manager();
					
					manager.setPosition(position[i]);
					manager.setName(manager_name[i]);
					manager.setSex(CHF.showNull(manager_sex[i]));
					manager.setQualification(CHF.showNull(qualification[i]));
					manager.setMobilephone(CHF.showNull(mobilephone[i]));
					manager.setFixedphone(CHF.showNull(fixedphone[i]));
					manager.setEmail(CHF.showNull(email[i]));
					manager.setOther_contact1(CHF.showNull(contact1[i]));
					manager.setOther_contact2(CHF.showNull(contact2[i]));
					
					list.add(manager);
				}
				
				ms.addManagers(list, customerid);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		response.sendRedirect("manager.do?method=edit&departid="+customerid);
		
	//	out.println("<script language='javascript'>window.close();</script>");
		
		return null;
	}
	
	public ModelAndView edit(HttpServletRequest request, 
									HttpServletResponse response) 
									throws Exception {
		ASFuntion CHF = new ASFuntion();
		
		String customerid = request.getParameter("departid");
		customerid = CHF.showNull(customerid);
		
		Connection conn = null;
		List list = null;
		
		try{
			conn = new DBConnect().getConnect("");
			ManagerService ms = new ManagerService(conn);
			
			list = ms.getManagerByCustomerid(customerid);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		ModelAndView model = new ModelAndView("/Customer/manager.jsp?departid="+customerid);
		model.addObject("manager_list", list);
		
		return model;
	}
}
