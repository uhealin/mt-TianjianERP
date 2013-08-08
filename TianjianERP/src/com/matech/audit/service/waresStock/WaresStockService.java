package com.matech.audit.service.waresStock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matech.audit.service.proclamation.model.ProclamationFlow;
import com.matech.audit.service.waresStock.model.WaresStock;
import com.matech.audit.service.waresStock.model.WaresStockDetails;
import com.matech.audit.service.waresStock.model.WaresStramFlow;
import com.matech.audit.service.waresStock.model.WaresStream;
import com.matech.audit.service.waresStock.model.WaresType;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

/**
 * @author YMM
 *物品库存
 *
 */
public class WaresStockService {
	
	Connection conn = null; 
	
	
	public WaresStockService(Connection conn){
		this.conn = conn;
	}
	
	
	/**
	 * 新增
	 * @param waresStock
	 * @return
	 */
	public boolean add(WaresStock waresStock){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = "INSERT INTO `asdb`.`k_waresstock`    " +
						"(`uuid`,`name`,`remark`,`type`,`coding`,`unitUnit`,`lowestStock`, " +
						"`lowestWarnStock`,`highestWarnStock`,`usableStock`,`scrappedStock`,`departmentId`," +
						"photo,photoTemp) " +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, waresStock.getUuid());
			ps.setString(i++, waresStock.getName());
			ps.setString(i++, waresStock.getRemark());
			ps.setString(i++, waresStock.getType());
			ps.setString(i++, waresStock.getCoding());
			ps.setString(i++, waresStock.getUnitUnit());
			ps.setString(i++, waresStock.getLowestStock());
			ps.setString(i++, waresStock.getLowestWarnStock());
			ps.setString(i++, waresStock.getHighestWarnStock());
			ps.setString(i++, waresStock.getUsableStock());
			ps.setString(i++, waresStock.getScrappedStock());
			ps.setString(i++, waresStock.getDepartmentId());
			ps.setString(i++, waresStock.getPhoto());
			ps.setString(i++, waresStock.getPhotoTemp());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("物品入库失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 已有物品入库登记
	 * @param wsd
	 * @return
	 */
	public boolean addWaresStockDetails(WaresStockDetails wsd){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = "INSERT INTO `k_waresstockdetails` \n"
				          +"  ( `uuid`,`waresStockId`,`userId`,`date`,`ctype`,`quantity`,`price`,`suppliers`) \n"
				          +" VALUES (?,?,?,now(), ?,?,?,?);";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, wsd.getUuid());
			ps.setString(i++, wsd.getWaresStockId());
			ps.setString(i++, wsd.getUserId());
			ps.setString(i++, wsd.getCtype());
			ps.setString(i++, wsd.getQuantity());
			ps.setString(i++, wsd.getPrice());
			ps.setString(i++, wsd.getSuppliers());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("已有物品入库登记失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 修改
	 * @param waresStock
	 * @return
	 */
	public boolean update(WaresStock waresStock){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = "UPDATE `k_waresstock` \n "
						+"SET  \n "
						+"  `name` = ?, \n "
						+" `remark` = ?, \n "
						+"  `type` = ?, \n "
						+"  `coding` = ?, \n "
						+"  `unitUnit` = ?, \n "
						+"  `lowestStock` = ?, \n "
						+"  `lowestWarnStock` = ?, \n "
						+"  `highestWarnStock` = ?, \n "
						+"  `departmentId` =?, \n "
						+" photo = ?, \n"
						+" phototemp = ? \n "
						+" WHERE `uuid` = ?;";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, waresStock.getName());
			ps.setString(i++, waresStock.getRemark());
			ps.setString(i++, waresStock.getType());
			ps.setString(i++, waresStock.getCoding());
			ps.setString(i++, waresStock.getUnitUnit());
			ps.setString(i++, waresStock.getLowestStock());
			ps.setString(i++, waresStock.getLowestWarnStock());
			ps.setString(i++, waresStock.getHighestWarnStock());
			ps.setString(i++, waresStock.getDepartmentId());
			ps.setString(i++, waresStock.getPhoto());
			ps.setString(i++, waresStock.getPhotoTemp());
			ps.setString(i++, waresStock.getUuid());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("物品入库失败："+e.getMessage());
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
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = "DELETE FROM k_waresstock WHERE `uuid`=?;";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("物品删除失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}

	/**
	 * 得到库存信息
	 * @param uuid
	 * @return
	 */
	public WaresStock getWaresStock(String uuid){
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		WaresStock waresStock = new WaresStock();
		
		String selectSql = "SELECT`uuid`,`name`,`remark`,`type`,`coding`,`unitUnit`, " +
						    "`lowestStock`,`lowestWarnStock`,`highestWarnStock`,`usableStock`,`scrappedStock`,`departmentId` " +
							"FROM `k_waresstock`  " +
							"WHERE `uuid` = ?";
		
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				waresStock.setUuid(uuid);
				waresStock.setName(rs.getString("name"));
				waresStock.setRemark(rs.getString("remark"));
				waresStock.setType(rs.getString("type"));
				waresStock.setCoding(rs.getString("coding"));
				waresStock.setUnitUnit(rs.getString("unitUnit"));
				waresStock.setLowestStock(rs.getString("lowestStock"));
				waresStock.setLowestWarnStock(rs.getString("lowestWarnStock"));
				waresStock.setHighestWarnStock(rs.getString("highestWarnStock"));
				waresStock.setUsableStock(rs.getString("usableStock"));
				waresStock.setScrappedStock(rs.getString("scrappedStock"));
				waresStock.setDepartmentId(rs.getString("departmentId"));
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询库存出错："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return waresStock;
		
	}

	/**
	 * 得到翻译后库存信息 
	 * @param uuid
	 * @return
	 */
	public WaresStock getCNWaresStock(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		WaresStock waresStock = new WaresStock();
		
		String selectSql = "SELECT a.`uuid`,a.`name`,a.`remark`,a.`type`,a.`coding`,a.`unitUnit`, " +
						    " a.`lowestStock`,a.`lowestWarnStock`,a.`highestWarnStock`,a.`usableStock`, " +
						    "a.`scrappedStock`,b.departname as`departmentId` " +
							"FROM `asdb`.`k_waresstock` as a  " +
							"LEFT JOIN k_department b ON a.departmentId=b.autoId "+
							"WHERE `uuid` = ?";
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				waresStock.setUuid(rs.getString("uuid"));
				waresStock.setName(rs.getString("name"));
				waresStock.setRemark(rs.getString("remark"));
				waresStock.setType(rs.getString("type"));
				waresStock.setCoding(rs.getString("coding"));
				waresStock.setUnitUnit(rs.getString("unitUnit"));
				waresStock.setLowestStock(rs.getString("lowestStock"));
				waresStock.setLowestWarnStock(rs.getString("lowestWarnStock"));
				waresStock.setHighestWarnStock(rs.getString("highestWarnStock"));
				waresStock.setUsableStock(rs.getString("usableStock"));
				waresStock.setScrappedStock(rs.getString("scrappedStock"));
				waresStock.setDepartmentId(rs.getString("departmentId"));
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询库存出错："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return waresStock;
		
	}
	 
	/**
	 * 修改 当前可用 (新增)库存
	 * @param uuid
	 * @param usableStock
	 * @param type
	 * @return
	 */
	public boolean updateUsableStock(String uuid,String usableStock,String type){
		
		PreparedStatement ps = null;
		boolean result = false;
		String sql = "";
		
		if("增加".equals(type)){			
			  sql = "UPDATE `k_waresstock` \n "
					+"SET  \n "
					+"  `usableStock` =IFNULL(usableStock,'0')+? \n "
					+" WHERE `uuid` = ?;";
		}else if("减少".equals(type)){
			  sql = "UPDATE `k_waresstock` \n "
					+"SET  \n "
					+"  `usableStock` =usableStock-? \n "
					+" WHERE `uuid` = ?;";
		}
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(sql);
			
			ps.setInt(i++, Integer.parseInt(usableStock));
			ps.setString(i++, uuid);
			 
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("修改当前可用库存失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 修改 当前 可用库存 并且修改 当前报废的库存
	 * @param uuid
	 * @param quantity
	 * @return
	 */
	public boolean updateUsableAndScrapped(String uuid,String quantity){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String updateSql = "UPDATE `k_waresstock` \n "
						+"SET  \n "
						+"  `usableStock` =usableStock-?, \n "
						+"	scrappedStock =ifnull(scrappedStock,'0')+? "
						+" WHERE `uuid` = ?;";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(updateSql);
			
			ps.setString(i++, quantity);
			ps.setString(i++, quantity);
			ps.setString(i++, uuid);
			 
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("修改当前可用和报废库存失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 获取 物品流水 详情
	 * @param autoId
	 * @return
	 */
	public WaresStockDetails getCNWaresStockDetails(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		WaresStockDetails details = new WaresStockDetails();
		
		String selectSql = "SELECT uuid,waresStockId,b.name AS userId,`date`,ctype,quantity,price,suppliers \n"
							+" FROM k_waresstockdetails a \n"
							+" LEFT JOIN k_user  b ON a.userId = b.id \n"
							+" WHERE uuid=?"; 
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				details.setUuid(uuid);
				details.setWaresStockId(rs.getString("waresStockId"));
				details.setUserId(rs.getString("userId"));
				details.setDate(rs.getString("date"));
				details.setCtype(rs.getString("ctype"));
				details.setQuantity(rs.getString("quantity"));
				details.setPrice(rs.getString("price"));
				details.setSuppliers(rs.getString("suppliers"));
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询库存出错："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return details;
		
	}

	/**
	 * 得到申请信息
	 * @param uuid
	 * @return
	 */
	public WaresStream getCNwaresStream(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		WaresStream waresStream = new WaresStream();
		
		String selectSql = "SELECT a.`uuid`,b.name,a.waresStockId,c.name AS `userId`, \n"
					        +" `quantity`,`applyDate`,`applyReason`,`approveUserId`,`approveDate`,`approveQuantity`,`approveIdea`,`status` \n"
					        +" FROM `k_waresstream` a \n"
					        +" LEFT JOIN k_waresstock b ON a.waresstockId = b.uuid \n"
					        +" LEFT JOIN k_user c  ON a.userId = c.`id` \n"
					        +"WHERE a.`uuid` = ?";
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				waresStream.setUuid(uuid);
				waresStream.setWaresStockId(rs.getString("waresStockId"));
				waresStream.setUserId(rs.getString("userId"));
				waresStream.setQuantity(rs.getString("quantity"));
				waresStream.setApplyDate(rs.getString("applyDate"));
				waresStream.setApplyReason(rs.getString("applyReason"));
				waresStream.setApproveUserId(rs.getString("approveUserId"));
				waresStream.setApproveDate(rs.getString("approveDate"));
				waresStream.setApproveQuantity(rs.getString("approveQuantity"));
				waresStream.setApproveIdea(rs.getString("name")); //先把物品名称临时存放在这里
				waresStream.setStatus(rs.getString("status"));
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询申请物品信息出错："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return waresStream;
		
	}
	
	/**
	 * 查询正在申请的库存
	 * @param uuid
	 * @return
	 */
	public String getAvailableQuantity(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String quantity = "";
		String selectSql = "SELECT SUM(quantity) FROM k_waresstream WHERE waresStockId = ? "; 
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				quantity = rs.getString(1); 
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询正在申请的数量："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return quantity;
		
	}
	
	/**
	 * 物品申领
	 * @param waresStream
	 * @return
	 */
	public boolean addWaresStream(WaresStream waresStream){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = " INSERT INTO `asdb`.`k_waresstream` \n"
						  +"  (`uuid`,`waresStockId`,`userId`,`quantity`,`applyDate`,`applyReason`, \n" 
						  +	"`approveUserId`,`approveDate`,`approveQuantity`,`approveIdea`,`status`) \n"
						  +"  VALUES (?,?,?,?,?,?,?,?,?,?,? );";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, waresStream.getUuid());
			ps.setString(i++, waresStream.getWaresStockId());
			ps.setString(i++, waresStream.getUserId());
			ps.setString(i++, waresStream.getQuantity());
			ps.setString(i++, waresStream.getApplyDate());
			ps.setString(i++, waresStream.getApplyReason());
			ps.setString(i++, waresStream.getApproveUserId());
			ps.setString(i++, waresStream.getApproveDate());
			ps.setString(i++, waresStream.getApproveQuantity());
			ps.setString(i++, waresStream.getApproveIdea());
			ps.setString(i++, waresStream.getStatus());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("物品申领失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	/**
	 * 查询申请物品的部门
	 * 
	 */
	 public String getDepartBywaresstock(String taskId){
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 String departmentId = "";
		 String sql3 ="select departmentid from  k_waresstock \n" +
		 "where uuid = ( select waresStockId from k_waresstream where uuid='"+taskId+"')";
		 try {
			ps = conn.prepareStatement(sql3);
		    rs = ps.executeQuery();
			departmentId =  rs.getString(0);
		} catch (SQLException e) {
			System.out.println("查不到物品的申请部门");
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return departmentId; 
	 } 
	
	/**
	 * 物品申领 完成的修改
	 * @param waresStream
	 * @return
	 */
	public boolean updateWaresStream(WaresStream waresStream){
		
		PreparedStatement ps = null;
		boolean result = false;
		
		String innerSql = " update `k_waresstream` \n"
						  +"  set `approveUserId`=?,`approveDate`=?,`approveQuantity`=?,`approveIdea`=?,`status`=? \n"
						  +" where uuid = ? ;";
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(innerSql);
			
			ps.setString(i++, waresStream.getApproveUserId());
			ps.setString(i++, waresStream.getApproveDate());
			ps.setString(i++, waresStream.getApproveQuantity());
			ps.setString(i++, waresStream.getApproveIdea());
			ps.setString(i++, waresStream.getStatus());
			ps.setString(i++, waresStream.getUuid());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("物品申领修改失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 删除 已申请的物品
	 * @param waresStream
	 * @return
	 */
	public boolean deleteWaresStream(String uuid){
		
		PreparedStatement ps = null;
		boolean result = false;
		String updateSql = " UPDATE `k_waresstream` SET `status` = '已删除' WHERE UUID=?";
		
		int i = 1;
		
		try {
			
			ps = conn.prepareStatement(updateSql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			System.out.println("删除申领物品失败："+e.getMessage());
		}finally{
			
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * 新增物品申领流程信息
	 * @param waresStramFlow
	 * @return
	 */
	public boolean addWaresstreamProcss(WaresStramFlow waresStramFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_waresstreamprocss (ProcessInstanceId,uuid,Applyuser,ApplyDate,State,Property) "
				+ "value (?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, waresStramFlow.getProcessInstanceId());
			ps.setString(i++, waresStramFlow.getUuid());
			ps.setString(i++, waresStramFlow.getApplyuser());
			ps.setString(i++, waresStramFlow.getApplyDate());
			ps.setString(i++, waresStramFlow.getState());
			ps.setString(i++, waresStramFlow.getProperty());

			result = ps.execute();

			result = true;
		} catch (SQLException e) {

			System.out.println("新增物品申领流程信息失败service:" + e.getMessage());
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
	
	//先不用
	public WaresStream getListCNwaresStream(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		WaresStream waresStream = new WaresStream();
		
		String selectSql = "SELECT a.`uuid`,b.name,a.waresStockId,c.name AS `userId`, \n"
					        +" `quantity`,`applyDate`,`applyReason`,`approveUserId`,`approveDate`,`approveQuantity`,`approveIdea`,`status` \n"
					        +" FROM `k_waresstream` a \n"
					        +" LEFT JOIN k_waresstock b ON a.waresstockId = b.uuid \n"
					        +" LEFT JOIN k_user c  ON a.userId = c.`id` \n"
					        +"WHERE a.`uuid` = ?";
		
		try {
			
			ps = conn.prepareStatement(selectSql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {

				waresStream.setUuid(uuid);
				waresStream.setWaresStockId(rs.getString("waresStockId"));
				waresStream.setUserId(rs.getString("userId"));
				waresStream.setQuantity(rs.getString("quantity"));
				waresStream.setApplyDate(rs.getString("applyDate"));
				waresStream.setApplyReason(rs.getString("applyReason"));
				waresStream.setApproveUserId(rs.getString("approveUserId"));
				waresStream.setApproveDate(rs.getString("approveDate"));
				waresStream.setApproveQuantity(rs.getString("approveQuantity"));
				waresStream.setApproveIdea(rs.getString("name")); //先把物品名称临时存放在这里
				waresStream.setStatus(rs.getString("status"));
				
			}
			
		} catch (SQLException e) {
			
			System.out.println("查询申请物品信息出错："+e.getMessage());
			
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return waresStream;
		
	}
	
	/*
	 * 根据autoid查找库存物品类型
	 */
	public WaresType findWaresTypeById(String autoid){
		WaresType waresType = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select * from k_autotree where autoid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			if(rs.next()){
				waresType = new WaresType();
				waresType.setAutoId(rs.getString("autoid"));
				waresType.setCName(rs.getString("cName"));
				waresType.setParentId(rs.getString("parentId"));
				waresType.setProperty(rs.getString("property"));
				waresType.setPopedom(rs.getString("popedom"));
				waresType.setLevel(rs.getString("level"));
				waresType.setFullpath(rs.getString("fullpath"));
				waresType.setUrl(rs.getString("url"));
				waresType.setCType(rs.getString("ctype"));
				waresType.setPostalcode(rs.getString("postalcode"));
				waresType.setProjectPopedom(rs.getString("projectPopedom"));
				waresType.setIsleaf(rs.getString("isleaf"));
				waresType.setEnname(rs.getString("enname"));
				waresType.setIsShow(rs.getString("isShow"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return waresType;
	}
	
	/*
	 * 更新库存物品类型
	 */
	public void updateType(String autoId,String cName,String cType){
		PreparedStatement ps = null;
		try {
			String sql = "update k_autotree set cname=?,ctype=? where autoid=?";
			ps=this.conn.prepareStatement(sql);
			ps.setString(1, cName);
			ps.setString(2, cType);
			ps.setString(3, autoId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 删除库存物品类型
	 */
	public void delType(String fullpath){
		PreparedStatement ps = null;
		try {
			String sql = "delete from k_autotree where fullpath like '"+fullpath+"%'";
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 增加一个最底层的库存物品类型
	 */
	public void addType(String cName,String cType,String autoId){
		PreparedStatement ps = null;
		try {
			String sql = "insert into k_autotree (cName,parentid,level,fullpath,cType,isleaf) values(?,'55555','1',?,?,'yes')";
			ps=this.conn.prepareStatement(sql);
			ps.setString(1, cName);
			ps.setString(2, autoId+"|");
			ps.setString(3, cType);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 增加下一级库存物品类型
	 */
	public void addNextType(WaresType waresType){
		PreparedStatement ps = null;
		try {
			String sql = "insert into k_autotree (cName,parentid,level,fullpath,cType,isleaf) values(?,?,?,?,?,'yes')";
			ps=this.conn.prepareStatement(sql);
			ps.setString(1, waresType.getCName());
			ps.setString(2, waresType.getParentId());
			ps.setString(3, waresType.getLevel());
			ps.setString(4, waresType.getFullpath());
			ps.setString(5, waresType.getCType());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 修改节点状态，把叶子变成不是叶子
	 */
	public void updateLeaf(String autoId){
		PreparedStatement ps = null;
		try {
			String sql = "update k_autotree set isleaf = 'no' where autoid=?";
			ps=this.conn.prepareStatement(sql);
			ps.setString(1, autoId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	
	public List<String> doCheck(WaresStream waresStream,UserSession userSession){
		List<String> results=new ArrayList<String>();
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
		
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	waresStream.setCheck_state("已审核");
		 	i+=dbUtil.update(waresStream);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 审核成功", waresStream.getWare_name()));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 审核失败", waresStream.getWare_name()));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	
	public List<String> doReceive(WaresStream waresStream,UserSession userSession){
		List<String> results=new ArrayList<String>();
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
		
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	waresStream.setReceive_state("已认领");
		 	i+=dbUtil.update(waresStream);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 认领成功", waresStream.getWare_name()));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 认领失败", waresStream.getWare_name()));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	
	public List<String> doSureReceive(WaresStream waresStream,UserSession userSession){
		List<String> results=new ArrayList<String>();
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
		String dt=StringUtil.getCurrentDateTime();
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	if("未领取".equals(waresStream.getReceive_state())){
		 		results.add(MessageFormat.format("物品 {0} 未领取,无法确认", waresStream.getWare_name(),dt));
		 		return results;
		 	}
		 	waresStream.setReceive_state("已认领");
		 	waresStream.setReceive_time(dt);
		 	i+=dbUtil.update(waresStream);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 于  {1} 确认认领成功", waresStream.getWare_name(),dt));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 于  {1} 确认认领失败", waresStream.getWare_name(),dt));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	/**
	 * 
	 * @param waresStream
	 * @param userSession
	 * @return
	 */
	public List<String> doReturn(WaresStream waresStream,UserSession userSession){
		List<String> results=new ArrayList<String>();
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
		
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	waresStream.setReturn_state("已归还");
		 	i+=dbUtil.update(waresStream);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 归还成功", waresStream.getWare_name()));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 归还失败", waresStream.getWare_name()));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	/**
	 * 上述的发至人员领取到物品后，系统上点击签收
	 * @param waresStockDetails
	 * @param userSession
	 * @return
	 */
	public List<String> doSignin(WaresStockDetails waresStockDetails,UserSession userSession){
		List<String> results=new ArrayList<String>();
		if(!"已发放".equals(waresStockDetails.getCheck_state())){
			results.add(MessageFormat.format("未发放不能签收",""));
		    return results;
		}
	
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
        String dt=StringUtil.getCurDateTime();
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
            waresStockDetails.setCheck_state("已签收");
            waresStockDetails.setCheck_time(dt);
            waresStockDetails.setChecker_id(userSession.getUserId());
            waresStockDetails.setChecker_name(userSession.getUserName());
		 	i+=dbUtil.update(waresStockDetails);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 发放审核成功",""));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 发放审核失败",""));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	/**
	 * 行政人事部门负责人审核物品发放登记申请
	 * @param waresStockDetails
	 * @param userSession
	 * @return
	 */
	public List<String> doCheck(WaresStockDetails waresStockDetails,UserSession userSession){
		List<String> results=new ArrayList<String>();
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
        String dt=StringUtil.getCurDateTime();
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
            waresStockDetails.setCheck_state("已审核");
            waresStockDetails.setCheck_time(dt);
            waresStockDetails.setChecker_id(userSession.getUserId());
            waresStockDetails.setChecker_name(userSession.getUserName());
		 	i+=dbUtil.update(waresStockDetails);
		 	if(i==1){
		 		results.add(MessageFormat.format("物品 {0} 发放审核成功",""));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 发放审核失败",""));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
	/**
	 * 发放人点击后物品才能进行发放
	 * @param waresStockDetails
	 * @param userSession
	 * @return
	 */
	public List<String> doGrant(WaresStockDetails waresStockDetails,UserSession userSession){
		
		List<String> results=new ArrayList<String>();
		if(!"已审核".equals(waresStockDetails.getCheck_state())){
			results.add(MessageFormat.format("未审核不能发发放",""));
		    return results;
		}
		
		Connection conn=null;
		DbUtil dbUtil=null;
		int i=0;
		WaresStream waresStream=new WaresStream();
		WaresStock waresStock=new WaresStock();
        String dt=StringUtil.getCurDateTime();
		try{
		 	conn=new DBConnect().getConnect();
		 	dbUtil=new DbUtil(conn);
		 	waresStock=dbUtil.load(waresStock, waresStockDetails.getWaresStockId());
		 	waresStream.setApplyDate(dt);
		 	waresStream.setApproveQuantity(waresStockDetails.getQuantity());
		 	waresStream.setApproveUserId(waresStockDetails.getReceiver_id());
		 	waresStream.setPro_type(waresStock.getPro_type());
		 	waresStream.setQuantity(waresStockDetails.getQuantity());
		 	waresStream.setWare_name(waresStock.getName());
		 	waresStream.setWaresStockId(waresStock.getCoding());
		 	waresStream.setApproveUserId(userSession.getUserId());
		 	waresStream.setUuid(UUID.randomUUID().toString());
		 	i+=dbUtil.insert(waresStream);
		 	if(i==1){
		 		waresStockDetails.setStatus("已发放");
		 		i+=dbUtil.update(waresStockDetails);
		 	}
		 	if(i==2){
		 		results.add(MessageFormat.format("物品 {0} 发放确认成功", waresStream.getWare_name()));
		 	}else{
		 		results.add(MessageFormat.format("物品 {0} 发放确认失败", waresStream.getWare_name()));
		 	}
		}catch(Exception ex){
			results.add(MessageFormat.format("程序异常:",ex.getLocalizedMessage()));
		}finally{
			DbUtil.close(conn);
		}
		return results;
	}
	
}
