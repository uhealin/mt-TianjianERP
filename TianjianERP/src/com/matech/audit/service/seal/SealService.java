package com.matech.audit.service.seal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.seal.model.Seal;
import com.matech.audit.service.seal.model.SealFlow;
import com.matech.audit.service.waresStock.model.WaresStramFlow;
import com.matech.framework.pub.db.DbUtil;

/**
 * @author YMM
 * 印章 
 *
 */
public class SealService {

	Connection conn = null;
	
	public SealService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 新增
	 * @param seal
	 * @return
	 */
	public boolean add(Seal seal){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `asdb`.`k_seal` \n"
		            +"(`uuid`,`userId`,`applyDate`,`matter`,`ctype`,`status`,`fileName`,`remark`,applyDepartment,applyDepartId,sealCount,attachname,`printCount`) \n"
		            +"VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, seal.getUuid());
			ps.setString(i++, seal.getUserId());
			ps.setString(i++, seal.getApplyDate());
			ps.setString(i++, seal.getMatter());
			ps.setString(i++, seal.getCtype());
			ps.setString(i++, seal.getStatus());
			ps.setString(i++, seal.getFileName());
			ps.setString(i++, seal.getRemark());
			ps.setString(i++, seal.getApplyDepartment());
			ps.setString(i++, seal.getApplyDepartId());
			ps.setString(i++, seal.getSealCount());
			ps.setString(i++, seal.getAttachname());
			ps.setString(i++, seal.getPrintCount());
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("新增印章出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 修改
	 * @param seal
	 * @return
	 */
	public boolean update(Seal seal){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_seal` \n"
					+"SET  \n"
					+"  `userId` = ?, \n"
					+"  `matter` = ?, \n"
					+"  `ctype` = ?, \n"
					+"  `remark` = ?, \n"
					+"  applyDepartment=?,"
					+"  `filename` = ?, \n"
					+"	applyDepartId=?," 
					+"	sealCount=?, "
					+"  printCount=? "
					+"WHERE `uuid` =?;";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, seal.getUserId());
			ps.setString(i++, seal.getMatter());
			ps.setString(i++, seal.getCtype());
			ps.setString(i++, seal.getRemark());
			ps.setString(i++, seal.getApplyDepartment());
			ps.setString(i++, seal.getFileName());
			ps.setString(i++, seal.getApplyDepartId());
			ps.setString(i++, seal.getSealCount());
			ps.setString(i++, seal.getPrintCount());
			ps.setString(i++, seal.getUuid());
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("修公印章出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 删除
	 * @param uuid
	 * @return
	 */
	public boolean delete(String uuid){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "delete from k_seal where uuid=?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("删除印章出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 *得到印章信息 
	 * @param uuid
	 * @return
	 */
	public Seal getSeal(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Seal seal = new Seal();
		
		String sql = "SELECT`uuid`,`userId`,`applyDate`,`matter`,`ctype`,`status`,`fileName`,`remark` ,applyDepartment,applyDepartId,sealCount,`attachname`\n"
					+" FROM `k_seal` \n"
					+"where uuid=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				seal.setUuid(uuid);
				seal.setUserId(rs.getString("userId"));
				seal.setApplyDate(rs.getString("applyDate"));
				seal.setUserId(rs.getString("userId"));
				seal.setMatter(rs.getString("matter"));
				seal.setCtype(rs.getString("ctype"));
				seal.setFileName(rs.getString("fileName"));
				seal.setStatus(rs.getString("status"));
				seal.setRemark(rs.getString("remark"));
				seal.setApplyDepartment(rs.getString("applyDepartment"));
				seal.setApplyDepartId(rs.getString("applyDepartId"));
				seal.setSealCount(rs.getString("sealCount"));
				seal.setAttachname(rs.getString("attachname"));
			}
			
		} catch (SQLException e) {
			System.out.println("获取印章信息错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return seal;
	}
	
	/**
	 * 查询是否上传附件
	 * @param table
	 * @param uuid
	 * @return
	 */
	public String getAccessory(String table,String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String attachid = "";
		
		String sql = "SELECT attachid  FROM `k_attachext` where indextable =? AND indexid=? ";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, table);
			ps.setString(2, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				attachid+=rs.getString("attachid")+",";
			}
			
			
		} catch (SQLException e) {
			System.out.println("查询是否上传附件错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return attachid;
	}
	
	/**
	 * 修改印章状态
	 * @param uuid
	 * @param status
	 * @return
	 */
	public int updateStatus(String uuid,String status){
		
		PreparedStatement ps = null;
		int result = 0;
		
		String sql = "UPDATE k_seal SET STATUS=? WHERE uuid=? ";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, status);
			ps.setString(2, uuid);
			
			result = ps.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("修改印章状态错误："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 新增公章流程信息
	 * @param waresStramFlow
	 * @return
	 */
	public boolean addSealProcss(SealFlow sealFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_sealprocss (ProcessInstanceId,uuid,Applyuser,ApplyDate,State,Property) "
				+ "value (?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, sealFlow.getProcessInstanceId());
			ps.setString(i++, sealFlow.getUuid());
			ps.setString(i++, sealFlow.getApplyuser());
			ps.setString(i++, sealFlow.getApplyDate());
			ps.setString(i++, sealFlow.getState());
			ps.setString(i++, sealFlow.getProperty());

			result = ps.execute();

			result = true;
		} catch (SQLException e) {

			System.out.println("新增公章流程信息失败service:" + e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/**
	 * 根据sql 得到一列值
	 * @param sql
	 * @return
	 */
	public String getValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				value += rs.getString(1) +"@`@";
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	/**
	 * 修改莫列的值
	 * @param sql
	 * @return
	 */
	public boolean UpdateValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.executeUpdate();
			
			result = true;
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return result;
	}
	
}
