package com.matech.audit.service.affairreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.affairreport.model.AffairReportTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class LookUpMessageService implements Job{
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		List list = null;
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			list = checkReport(conn);
			
			Iterator it = list.iterator();
			AffairReportTable art = null;
			while(it.hasNext()){
				art = (AffairReportTable)it.next();
				
				if(checkPlacard(art, conn)){
					updatePlacard(art, conn);
				}
				else{
					addPlacard(art, conn);
					if(art.getPrincipal() != null && !art.getPrincipal().equals("")){
						art.setAuthor(art.getPrincipal());
						addPlacard(art, conn);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 检查到期的重大事项
	 * return 
	 * @throws Exception
	 */
	public List checkReport(Connection conn) throws Exception {
		
		List list = new ArrayList();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select * from z_affairreport \n"
						+ "where timeLimit is not null \n"
						+ "and timeLimit <> '' \n"
						+ "and timeLimit <> '0000-00-00 00:00:00' \n"
						+ "and status = '未解决' \n"
						+ "and timeLimit <= now() \n"
						+ "and Executer = ''";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			AffairReportTable art = null;
			while(rs.next()){
				art = new AffairReportTable();
				
				art.setAuthor(rs.getString("Author"));
				art.setCaption(rs.getString("Caption"));
				art.setCreateTime(rs.getString("CreateTime"));
				art.setStatus(rs.getString("status"));
				art.setID(rs.getInt("ID"));
				art.setMatter(rs.getString("Matter"));
				art.setProjectID(rs.getInt("ProjectID"));
				art.setPorperty(rs.getString("Porperty"));
				art.setPrincipal(rs.getString("Principal"));
				
				list.add(art);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	/**
	 * 检查k_placard表（公告表）里是否有提醒记录
	 * return 
	 * 
	 */
	public boolean checkPlacard(AffairReportTable art, Connection conn) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select * from k_placard where property = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, new Integer(art.getID()).toString());
			
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
			
		}catch(Exception e){
			
		}finally{
			DbUtil.close(ps);
		}
		
		return false;
	} 
	
	/**
	 * 
	 * 更新 k_placard表（公告表）为未读
	 */
	
	public void updatePlacard(AffairReportTable art, Connection conn) throws Exception {
		
		PreparedStatement ps = null;
		try{
			String sql = "update k_placard set IsRead = '0', IsReversion = '0', IsNotReversion = '1' where property = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, new Integer(art.getID()).toString());
			
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 添加k_placard表（公告表）里记录
	 * 
	 */
	
	public void addPlacard(AffairReportTable art, Connection conn){
		
		ASFuntion CHF = new ASFuntion();
		
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			sql = "select * from k_placard where property = ? and Addressee = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, new Integer(art.getID()).toString());
			ps.setString(2, art.getAuthor());
			
			rs = ps.executeQuery();
			if(rs.next()){
				return;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		
		try{
			String level = "";
			if(art.getPorperty() == null || art.getPorperty().equals("") || art.getPorperty().trim().equals("null")){
				level = "无";
			}
			else{
				level = art.getPorperty();
			}
			
			System.out.println("//"+level);
			
			AffairReportService affairReportService = new AffairReportService(conn);
			
			String curDepartId = affairReportService.getDepartID(art);
			
			if(!"".equals(curDepartId)){
				
				String PlacardBody = "您的重大事项已经到期:<br>级别：［"+level+"］<br>主题：［"+art.getCaption()+"］"
								+ "<br><a href='../affairReport/ViewDetail.jsp?chooseValue="+art.getID()+"&DepartId="+curDepartId+"&opt=2' target='_self'>点击查看重大事件</a><br>";
				
				sql = "insert into k_placard (Addresser, AddresserTime, Caption, \n"
					+ "Matter, Addressee, IsRead, IsReversion, \n" 
					+ "IsNotReversion, Property) \n" 
					+ "values (?,?,?,?,?,?,?,?,?)";
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, "19");
				ps.setString(2, CHF.getCurrentDate()+ "" +CHF.getCurrentTime());
				ps.setString(3, "友情提示：您的重大事项已经到期！");
				ps.setString(4, PlacardBody);
				ps.setString(5, art.getAuthor());
				ps.setString(6, "0");
				ps.setString(7, "0");
				ps.setString(8, "1");
				ps.setString(9, new Integer(art.getID()).toString());
				
				ps.execute();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
