package com.matech.audit.service.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.process.model.ProcessForm;
import com.matech.framework.pub.db.DbUtil;

public class ProcessFormService {
	private Connection conn = null;
	
	public ProcessFormService(Connection conn) {
		this.conn = conn ;
	}
	
	
	public Map<String,String> getProcessForm(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String,String> map = new HashMap<String, String>() ;
		try {
			String sql = "select `key`,`value`,`nodeName`,`dealUserId`,`dealTime`,`property` from j_processForm where processInstanseId= ?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, processInstanseId) ;
			rs = ps.executeQuery() ;
			while(rs.next()) {
				map.put(rs.getString(1),rs.getString(2)) ;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return map ;
	}
	
	public boolean isNodeNameExist(String nodeName,String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from j_processForm where processInstanseId=? and nodeName like '%" + nodeName + "%' " ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, processInstanseId) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return true ;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false ;
	}
	
	public void add(ProcessForm pf) {
		PreparedStatement ps = null;
		try {
			String sql = "insert into j_processForm(`processInstanseId`,`key`,`value`,`nodeName`,`dealUserId`,`dealTime`,`property`) values(?,?,?,?, ?,?,?)" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,pf.getProcessInstanseId()) ;
			ps.setString(2,pf.getKey()) ;
			ps.setString(3,pf.getValue()) ;
			ps.setString(4,pf.getNodeName()) ;
			ps.setString(5,pf.getDealUserId()) ;
			ps.setString(6, pf.getDealTime()) ;
			ps.setString(7,pf.getProperty()) ;
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public List<ProcessForm> getNodeList(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null ;
		List<ProcessForm> nodeList = new ArrayList<ProcessForm>();
		try {
			String sql = "select DISTINCT nodeName,b.name,dealTime from j_processForm a " 
					   + " left join k_user b on a.dealUserId = b.id "
					   + " where processInstanseId= ? ORDER BY dealTime ASC,nodeName" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, processInstanseId) ;
			rs = ps.executeQuery() ;
			while(rs.next()) {
				
				String nodeName = rs.getString(1) ;
				String dealTime = rs.getString(3) ;
				ProcessForm pf = new ProcessForm() ;
				pf.setNodeName(nodeName) ;
				pf.setDealUserId(rs.getString(2)) ;
				pf.setDealTime(dealTime) ;
				
				List<ProcessForm> formList = new ArrayList<ProcessForm>();
				sql = "select `key`,`value`,`property` from j_processForm where processInstanseId= ? and nodeName='" + nodeName + "' and dealTime='"+dealTime+"'" ;
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,processInstanseId) ;
				rs2 = ps.executeQuery() ;
				while(rs2.next()) {
					ProcessForm cpf = new ProcessForm() ;
					cpf.setKey(rs2.getString(1)) ;
					cpf.setValue(rs2.getString(2)) ;
					cpf.setProperty(rs2.getString(3)) ;
					formList.add(cpf) ;
				}
				pf.setFormList(formList) ;
				
				nodeList.add(pf) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return nodeList ;
	}
	
	public List<ProcessForm> getNodeListGroupbyNodeName(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null ;
		List<ProcessForm> nodeList = new ArrayList<ProcessForm>();
		try {
			String sql = "select nodeName,dealUserId,dealTime from j_processForm a " 
					   + " where processInstanseId= ? group by nodeName,dealTime ORDER BY dealTime ASC,nodeName" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, processInstanseId) ;
			rs = ps.executeQuery() ;
			String tempNodeName = "" ;
			while(rs.next()) {
				
				String nodeName = rs.getString(1) ;
				String dealTime = rs.getString(3) ;
				ProcessForm pf = new ProcessForm() ;
				pf.setNodeName(nodeName) ;
				pf.setDealUserId(rs.getString(2)) ;
				pf.setDealTime(dealTime) ;
				
				if(tempNodeName.equals(nodeName)) {
					pf.setNodeName("") ;
				}
				
				List<ProcessForm> formList = new ArrayList<ProcessForm>();
				sql = "select `key`,`value`,`property`,dealTime from j_processForm where processInstanseId= ? and nodeName='" + nodeName + "' and dealTime='"+dealTime+"'" ;
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,processInstanseId) ;
				rs2 = ps.executeQuery() ;
				
				while(rs2.next()) {
					ProcessForm cpf = new ProcessForm() ;
					cpf.setKey(rs2.getString(1)) ;
					cpf.setValue(rs2.getString(2)) ;
					cpf.setProperty(rs2.getString(3)) ;
					formList.add(cpf) ;
				}
				pf.setFormList(formList) ;
				
				tempNodeName = rs.getString(1) ;
				nodeList.add(pf) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return nodeList ;
	}
}
