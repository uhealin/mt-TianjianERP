package com.matech.audit.work.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.StockholderService;
import com.matech.audit.service.customer.model.Stockholder;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class StockholderAction extends MultiActionController {

	public ModelAndView save(HttpServletRequest request, 
									HttpServletResponse response) 
									throws Exception {
//		PrintWriter out = response.getWriter();
		
		ASFuntion CHF = new ASFuntion();
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		String customerid = CHF.showNull(request.getParameter("departid"));
		
		String register = CHF.showNull(request.getParameter("register"));
		String curname = CHF.showNull(request.getParameter("curname"));
	
		String[] names = request.getParameterValues("name");
		String[] totalFunds = request.getParameterValues("totalFund");
		String[] registerFunds = request.getParameterValues("registerFund");
		String[] percentOfFunds = request.getParameterValues("percentOfFund");
		String[] factFunds = request.getParameterValues("factFund");
		String[] percentages = request.getParameterValues("percentage");
		
		Stockholder stockholder = null;
		List list = new ArrayList();
		String stockowner = "";
		
		try{
			conn = new DBConnect().getConnect("");
			StockholderService ss = new StockholderService(conn);
			ss.deleteStockHolderList(customerid);
			ss.deleteByCustomerid(customerid);
			
			/*if(names == null){
				out.println("<script language='javascript'>window.close();</script>");
				return null;
			}*/
			
			if(names!=null){	
				for(int i=0; i<names.length; i++){
					stockholder = new Stockholder();
					
					stockowner += CHF.showNull(names[i]) + " " + CHF.showNull(percentages[i]) + "%;";
					
					stockholder.setName(CHF.showNull(names[i]));
					stockholder.setTotalFund(CHF.showNull(totalFunds[i]));
					stockholder.setRegisterFund(CHF.showNull(registerFunds[i]));
					stockholder.setPercentOfFund(CHF.showNull(percentOfFunds[i]));
					stockholder.setFactFund(CHF.showNull(factFunds[i]));
					stockholder.setPercentage(CHF.showNull(percentages[i]));
					
					list.add(stockholder);
				}
				
				ss.save(list, customerid);
				ss.saveStockHolderList(stockowner, customerid);
			}
			String sql = "update k_customer set register=?,curname=? where departid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,register);
			ps.setString(2,curname);
			ps.setString(3,customerid);
			
			ps.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
		response.sendRedirect("stockholder.do?method=edit&departid="+customerid);
		
	//	out.println("<script language='javascript'>window.close();</script>");
		
		return null;
	}
	
	public ModelAndView edit(HttpServletRequest request, 
									HttpServletResponse response) 
									throws Exception{
		
		ModelAndView model = new ModelAndView("/Customer/stockholder.jsp");
		
		ASFuntion CHF = new ASFuntion();
		
		List list = null;
		
		String customerid = CHF.showNull(request.getParameter("departid"));
			
		String []getValues = null;
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			StockholderService ss = new StockholderService(conn);
			
			list = ss.getStockholderByCustomerid(customerid);
			getValues = ss.getCurname(customerid).split(",");
			
			model.addObject("stockholders", list);
			model.addObject("register", getValues[0]);	
			model.addObject("curname", getValues[1]);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return model;
	}
}
