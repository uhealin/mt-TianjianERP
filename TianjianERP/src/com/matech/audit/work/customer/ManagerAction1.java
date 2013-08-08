package com.matech.audit.work.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.ManagerService1;

import com.matech.audit.service.customer.model.Manager1;

import com.matech.framework.pub.db.DbUtil;

public class ManagerAction1 extends MultiActionController{
	String _manager = "/Customer/list1.jsp";
	String _managerEdit ="/Customer/manageradd.jsp";
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView modelandview = new ModelAndView(_manager);
		
		HttpSession session = request.getSession();
		
		String customerid = request.getParameter("departid");
		
		
		DataGridProperty pp = new DataGridProperty();
		
		pp.setCustomerId("");
		
		pp.setTableID("managerlist");
		
		pp.setInputType("radio");
		
		pp.setWhichFieldIsValue(1);
		
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		
		pp.setPrintColumnWidth("20,20,20,20,20,20,20,20,20");
		
		pp.setPrintTitle("高管简历");
		
		pp.addColumn("职位","position" );
		pp.addColumn("姓名","name" );
		pp.addColumn("性别","sex" );
		pp.addColumn("学历","qualification" );
		pp.addColumn("手机","mobilephone" );
		pp.addColumn("座机","fixedphone" );
		pp.addColumn("邮箱","email" );
		pp.addColumn("生日","birthday" );
		pp.addColumn("其他联系方式1","contact1" );
		pp.addColumn("其他联系方式2","contact2" );
//		pp.addColumn("工作简历","resume" );

		
		String sql = "select autoid,customerid, position, name, sex, qualification,mobilephone, " 
				   + "fixedphone, email, contact1,contact2,resume,birthday from k_manager where customerid="+customerid;
		
		pp.setSQL(sql);

		pp.setOrderBy_CH("autoid");
		pp.setDirection("asc");


		session.setAttribute(DataGrid.sessionPre + pp.getTableID(),
				pp);

	
		return modelandview;
		
	}

	/**增加方法
	 * @param request
	 * @param response
	 * @param family
	 * @return
	 * @throws Exception
	 */
	public ModelAndView Manageradd(HttpServletRequest request,HttpServletResponse response, Manager1 manager1) throws Exception{
	

		
		Connection conn = null;
		
		
		try {
			
			 conn =   new DBConnect().getConnect("");
			 
			 ManagerService1 ms = new ManagerService1(conn);
			 
			 ms.Manageradd(manager1);
			
			 
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			DbUtil.close(conn);
		}
	
				response.sendRedirect("/AuditSystem/manager1.do?departid=" + manager1.getCustomerid());	
		
				
				return null;
				
	}


	
	/**修改方法	
	 * @param request
	 * @param response
	 * @param family
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateManager(HttpServletRequest request,HttpServletResponse response, Manager1 manager1) throws Exception{
	
	
			
		
			String customerid = request.getParameter("customerid");
			
			Connection conn = null;
			
			try {
				
				
				conn = new DBConnect().getConnect("");
				
				 ManagerService1 ms = new ManagerService1(conn);
				 
				
				 
				 String autoid = request.getParameter("autoid");
				
				 ms.updateManager(manager1, autoid);
				 
				;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				
				DbUtil.close(conn);
			}
			
			response.sendRedirect("/AuditSystem/manager1.do?departid=" + customerid);	
					
					
					return null;
				}
	
		
		/**编辑方法
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView exitManager(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
				
			ModelAndView modelAndView = new ModelAndView(_managerEdit);
			
			HttpSession session = request.getSession();

			Manager1 manager1 = new Manager1();
			
			
			String autoid = request.getParameter("autoid");
			PreparedStatement ps = null;
			ResultSet rs = null;

			Connection conn = null;
			

									
			try {
				conn = new DBConnect().getConnect("");
							
				String sql ="select  position, name, sex, qualification,mobilephone, fixedphone, email, contact1,contact2,resume,birthday from k_manager where autoid='"+autoid+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()) {
					manager1.setPosition(rs.getString("position"));
					manager1.setName(rs.getString("name"));
					manager1.setSex(rs.getString("sex"));
					manager1.setQualification(rs.getString("qualification"));
					manager1.setMobilephone(rs.getString("mobilephone"));
					manager1.setFixedphone(rs.getString("fixedphone"));
					manager1.setEmail(rs.getString("email"));
					manager1.setContact1(rs.getString("contact1"));
					manager1.setContact2(rs.getString("contact2"));
					manager1.setResume(rs.getString("resume"));
					manager1.setBirthday(rs.getString("birthday"));
		
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}
			
			modelAndView.addObject("autoid", autoid);//传值
			modelAndView.addObject("manager1",manager1);//同上
			String action = request.getParameter("action");
			modelAndView.addObject("action",action);
			
			return modelAndView;
					
												
			
				}

		/**删除方法
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */		

	public ModelAndView removeManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		String customerid = request.getParameter("customerid");
		Connection conn =null;
		
		try {
			conn = new DBConnect().getConnect("");
			
			
			
			String autoid = request.getParameter("autoid");
							
			ManagerService1 ms = new ManagerService1(conn);
			
			ms.removeManager(autoid);
			
						
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

										
		response.sendRedirect("/AuditSystem/manager1.do?departid=" + customerid);	
				
								
				
				return null;
			}
			

	                              		
	

}
