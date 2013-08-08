package com.matech.audit.service.businessTake;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.matech.audit.service.businessTake.model.BusinessTake;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.project.BusinessProjectService;
import com.matech.audit.service.project.ProjectService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class BusinessTakeService {
	private Connection conn = null;
	
	public BusinessTakeService(Connection conn) {
		this.conn = conn;
	}
	
	
	public void addBusinessTake(BusinessTake businessTake) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "INSERT INTO j_businesstake(bprojectid,processInstanseId,customerid,applyUserId,applyTime,cyear,fillDate,fillUserId,departmentid,partner,departManager,"
				 	   +" ristPartner,seniorManager,businessCost,property,projectNames)" 
					   + " values(?,?,?,?, ?,?,?,?, ?,?,?,? ,?,?,?,?)" ;
			int i = 1;                                                                                                                                                                                                                
			ps = conn.prepareStatement(sql);
			ps.setString(i++,businessTake.getbProjectId()) ;
			ps.setString(i++,businessTake.getProcessInstanseId()) ;
			ps.setString(i++,businessTake.getCustomerid()) ;
			ps.setString(i++,businessTake.getApplyUserId()) ;
			ps.setString(i++,businessTake.getApplyTime()) ;
			ps.setString(i++,businessTake.getCyear()) ;
			ps.setString(i++,businessTake.getFillDate()) ;
			ps.setString(i++,businessTake.getFillUserId()) ;
			ps.setString(i++,businessTake.getDepartmentid()) ;
			ps.setString(i++,businessTake.getPartner()) ;
			ps.setString(i++,businessTake.getDepartManager()) ;
			ps.setString(i++,businessTake.getRistPartner()) ;
			ps.setString(i++,businessTake.getSeniorManager()) ;
			ps.setString(i++,businessTake.getBusinessCost()) ;
			ps.setString(i++,businessTake.getProperty()) ;
			ps.setString(i++,businessTake.getProjectNames()) ;
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		
	}
	
	public BusinessTake getBusinessTake(String processInstanceId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		BusinessTake bt = null ;
		try{
			String sql = "select bprojectid,processInstanseId,c.departname,d.name,applyTime,cyear,fillDate,j.name as fillUser,e.departname as departmentname,f.name as partner,g.name as departManager," 
				   + " h.name as ristPartner,i.name as seniorManager,businessCost,a.property,b.ACTIVITYNAME_ \n" 
				   + " from j_businesstake a \n" 
				   + " left join jbpm4_execution b on a.processInstanseId = b.ID_ \n"
				   + " left join k_customer c on a.customerid = c.departId \n"
				   + " left join k_user d on a.applyUserId = d.id \n"
				   + " left join k_department e on a.departmentid = e.autoid \n"
				   + " left join k_user f on a.partner = f.id \n"
				   + " left join k_user g on a.departManager = g.id \n"
				   + " left join k_user h on a.ristPartner = h.id \n"
				   + " left join k_user i on a.seniorManager = i.id \n"
				   + " left join k_user j on a.fillUserId = j.id \n"
				   + " where processInstanseId=? ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,processInstanceId) ;
			rs = ps.executeQuery();
			if(rs.next()) {
				int i = 1 ;
				bt = new BusinessTake() ;
				bt.setbProjectId(rs.getString(i++)) ;
				bt.setProcessInstanseId(rs.getString(i++)) ;
				bt.setCustomerid(rs.getString(i++)) ;
				bt.setApplyUserId(rs.getString(i++)) ;
				bt.setApplyTime(rs.getString(i++)) ;
				bt.setCyear(rs.getString(i++)) ;
				bt.setFillDate(rs.getString(i++)) ;
				bt.setFillUserId(rs.getString(i++)) ;
				bt.setDepartmentid(rs.getString(i++)) ;
				bt.setPartner(rs.getString(i++)) ;
				bt.setDepartManager(rs.getString(i++)) ;
				bt.setRistPartner(rs.getString(i++)) ;
				bt.setSeniorManager(rs.getString(i++));
				bt.setBusinessCost(rs.getString(i++)) ;
				bt.setProperty(rs.getString(i++)) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
			DbUtil.close(rs) ;
		}
		return bt ;
	}
	
	public BusinessTake getBusinessTakeOfId(String processInstanceId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		BusinessTake bt = null ;
		try{
			String sql = "select bprojectid,processInstanseId,customerid,applyUserId,applyTime,cyear,fillDate,fillUserId,departmentid,partner,departManager," 
				   + " ristPartner,seniorManager,businessCost,a.property \n" 
				   + " from j_businesstake a \n" 
				   + " where processInstanseId=? ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,processInstanceId) ;
			rs = ps.executeQuery();
			if(rs.next()) {
				int i = 1 ;
				bt = new BusinessTake() ;
				bt.setbProjectId(rs.getString(i++)) ;
				bt.setProcessInstanseId(rs.getString(i++)) ;
				bt.setCustomerid(rs.getString(i++)) ;
				bt.setApplyUserId(rs.getString(i++)) ;
				bt.setApplyTime(rs.getString(i++)) ;
				bt.setCyear(rs.getString(i++)) ;
				bt.setFillDate(rs.getString(i++)) ;
				bt.setFillUserId(rs.getString(i++)) ;
				bt.setDepartmentid(rs.getString(i++)) ;
				bt.setPartner(rs.getString(i++)) ;
				bt.setDepartManager(rs.getString(i++)) ;
				bt.setRistPartner(rs.getString(i++)) ;
				bt.setSeniorManager(rs.getString(i++));
				bt.setBusinessCost(rs.getString(i++)) ;
				bt.setProperty(rs.getString(i++)) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
			DbUtil.close(rs) ;
		}
		return bt ;
	}
	
	public void updateBusinessTake(BusinessTake bt) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "update j_businesstake set bprojectid=?,customerid=?,cyear=?,fillDate=?,fillUserId=?,departmentid=?,partner=?,departManager=?," 
				   + " ristPartner=?,seniorManager=? \n" 
				   + " where processInstanseId=? ";
			
			ps = conn.prepareStatement(sql);
			int i= 1 ;
			ps.setString(i++,bt.getbProjectId()) ;
			ps.setString(i++,bt.getCustomerid()) ;
			ps.setString(i++,bt.getCyear()) ;
			ps.setString(i++,bt.getFillDate()) ;
			ps.setString(i++,bt.getFillUserId()) ;
			ps.setString(i++,bt.getDepartmentid()) ;
			ps.setString(i++,bt.getPartner()) ;
			ps.setString(i++,bt.getDepartManager()) ;
			ps.setString(i++,bt.getRistPartner()) ;
			ps.setString(i++,bt.getSeniorManager()) ;
			ps.setString(i++,bt.getProcessInstanseId()) ;
			
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void updateState(String processInstanceId,String state,String property) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "update j_businesstake set state=?,property=? where processInstanseId=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,state) ;
			ps.setString(2,property) ;
			ps.setString(3,processInstanceId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void delBusinessTake(String processInstanceId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String bprojectid = getBusinessTakeOfId(processInstanceId).getbProjectId() ;
			//先清除已发起的状态
			BusinessProjectService bps = new BusinessProjectService(conn) ;
			if(!"".equals(bprojectid)) {
				String[] idArr = bprojectid.substring(1, bprojectid.length() -1).split(",");
				for(int i=0;i<idArr.length;i++) {
					bps.updatePropertyByProjectId(idArr[i],"") ;
				}
			}
			
			String sql = "delete from j_businesstake where processInstanseId=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,processInstanceId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void delBusinessTakeByProjectId(String projectId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			
			String sql = "delete from j_businesstake where bprojectid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void sendMessage(String projectId,String departname, String partner,
			String controlUser,String signedCpa,String receiveUserId) {

		try {

			ASFuntion CHF = new ASFuntion();
			PlacardTable pt = new PlacardTable();
			PlacardService pls = new PlacardService(conn);
			pt.setAddresser("19");
			pt.setAddresserTime(CHF.getCurrentDate() + " "+ CHF.getCurrentTime());
			
			pt.setCaption("业务承接系统通知函");
			String projectName = new ProjectService(conn).getProjectById(projectId).getProjectName();
			
			String matter = "根据本所质量控制制度，经主任会计师孙勇老师确定，<br>" + departname + "新（续）接项目—— "
						  + projectName + "<br>项目的项目负责合伙人、质量控制合伙人及签字注册会计师如下：<br>"
						  + "项目负责人合伙人："+partner+"；质量控制合伙人："+controlUser+"；签字注册会计师："+signedCpa+"。" ;

			pt.setMatter(matter);
			pt.setIsRead(0);
			pt.setIsReversion(0);
			pt.setIsNotReversion(0);
			pt.setAddressee(receiveUserId);
			pls.AddPlacard(pt);

		} catch (Exception e) {
			Debug.print(Debug.iError, "抄送意见表信息发送失败！", e);
		}
	}
	
	public String getReportNumber(String cyear) {
		
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		String rNumber = "" ;
		try {
			//程序临时修改，空出几个一早被占用的报告号
			String sql = "SELECT MAX(SUBSTR(reportnumber,-4)) FROM z_projectbusiness WHERE reportnumber like '%"+cyear+"-%'" 
					   + " and reportnumber not in('2011-1100','2011-1101','2011-1102','2011-1103','2011-1104','2011-1105','2011-1106')"  ;
			
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			String rn = "" ;
			if(rs.next()) {
				rn = rs.getString(1) ;
			}
			
			if(rn != null && !"NULL".equalsIgnoreCase(rn) && !"".equals(rn)) {
				try {
					int reportNumber = Integer.parseInt(rn)+1 ;
					DecimalFormat df = new DecimalFormat("0000");
					String temp =  df.format(reportNumber);
					rNumber = temp ;
					
					//程序临时修改，空出几个一早被占用的报告号
					if("2011".equals(cyear) && "1100".equals(rNumber)) {
						rNumber = "1107" ;
					}
					
				}catch(NumberFormatException e) {
					e.printStackTrace() ;
					rNumber = "0001" ;
				}
			}else {
				rNumber = "0001" ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(rs) ;
			DbUtil.close(ps) ;
		}
		
		return rNumber ;
	}
	
	public String getEntrustNumber(String cyear) {
		
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		String rNumber = "" ;
		try {
			String sql = "select max(entrustNumber) from z_projectbusiness where entrustNumber like '%"+cyear+"-%' and entrustNumber not like '%-内%' " 
					   + " and entrustNumber not in('2011-1100','2011-1101','2011-1102','2011-1103','2011-1104','2011-1105','2011-1106')"  ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			String rn = "" ;
			if(rs.next()) {
				rn = rs.getString(1) ;
			}
			
			if(rn != null && !"NULL".equalsIgnoreCase(rn) && !"".equals(rn)) {
				rn = rn.substring(rn.indexOf("-")+1,rn.length()) ;
				try {
					int reportNumber = Integer.parseInt(rn)+1 ;
					DecimalFormat df = new DecimalFormat("0000");
					String temp =  df.format(reportNumber);
					
					//程序临时修改，空出几个一早被占用的报告号
					if("2011".equals(cyear) && "1100".equals(temp)) {
						temp = "1107" ;
					}
					rNumber = cyear+"-"+temp ;
					
				}catch(NumberFormatException e) {
					rNumber = cyear+"-0001" ;
				}
			}else {
				rNumber = cyear+"-0001" ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(rs) ;
			DbUtil.close(ps) ;
		}
		
		return rNumber ;
	}
	
	public void updateNumber(String processInstanceId,String WTBH) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" update j_businesstake set entrustNumber=? where processInstanseId=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, WTBH) ;
			ps.setString(2,processInstanceId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
}
