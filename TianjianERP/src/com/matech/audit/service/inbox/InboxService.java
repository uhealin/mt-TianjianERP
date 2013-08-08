package com.matech.audit.service.inbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.matech.audit.service.inbox.model.Inbox;
import com.matech.audit.service.inboxIcon.model.InboxIcon;
import com.matech.framework.pub.db.DbUtil;

public class InboxService {
	
	Connection conn = null;
	
	public InboxService(Connection conn){
		
		this.conn = conn;
	
	}

	/**
	 * 新增
	 * @param inbox
	 * @return
	 */
	public boolean add(Inbox inbox){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `asdb`.`k_inbox`   " +
						" (`uuid`,`packageName`,`packageCode`,`logisticsCompany`,`floorNumber`,`inboxUserId`,`arrivalDate`,`remindMode`,`mphoneRemind`,`status`,addUserId)" +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, inbox.getUuid());
			ps.setString(i++, inbox.getPackageName());
			ps.setString(i++, inbox.getPackageCode());
			ps.setString(i++, inbox.getLogisticsCompany());
			ps.setString(i++, inbox.getFloorNumber());
			ps.setString(i++, inbox.getInboxUserId());
			ps.setString(i++, inbox.getArrivalDate());
			ps.setString(i++, inbox.getRemindMode());
			ps.setString(i++, inbox.getMphoneRemind());
			ps.setString(i++, inbox.getStatus());
			ps.setString(i++, inbox.getAddUserId());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("新增收件出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 修改
	 * @param inbox
	 * @return
	 */
	public boolean update(Inbox inbox){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = " UPDATE  `k_inbox` \n"
					+"SET \n"
					+"  `packageName` = ?, \n"
					+"  `packageCode` = ?, \n"
					+"  `logisticsCompany` = ?, \n"
					+"  `floorNumber` = ?, \n"
					+"  `inboxUserId` = ?, \n"
					+"  `mphoneRemind`= ?,"
					+"  `remindMode` = ? \n"
					+"   WHERE `uuid` =?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, inbox.getPackageName());
			ps.setString(i++, inbox.getPackageCode());
			ps.setString(i++, inbox.getLogisticsCompany());
			ps.setString(i++, inbox.getFloorNumber());
			ps.setString(i++, inbox.getInboxUserId());
			ps.setString(i++, inbox.getMphoneRemind());
			ps.setString(i++, inbox.getRemindMode());
			ps.setString(i++, inbox.getUuid());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("修改收件出错："+e.getMessage());
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
		
		String sql = "delete from k_inbox where uuid=?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("删除收件出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 * 领取收件
	 * @param packageCode
	 * @param receiveUserId
	 * @param identityName 
	 * @param identityCard
	 * @return
	 */
	public boolean updateStatus(String packageCode,String receiveUserId,String identityName,String identityCard){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = " UPDATE  `k_inbox` \n"
					 +"SET \n"
					 +"  `receiveUserId` = ?, \n"
					 +"  `receiveDate` = now(), \n"
					 +"  `identityName` = ?, \n"
					 +"  `identityCard` = ?, \n"
					 +"  `status` = '已领取' \n"
					 +"   WHERE `packageCode` = ?;";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
		 
			ps.setString(i++, receiveUserId);
			ps.setString(i++, identityName);
			ps.setString(i++, identityCard);
			ps.setString(i++, packageCode);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("领取收件出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 *得到收件信息 
	 * @param uuid
	 * @return
	 */
	public Inbox getInbox(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Inbox inbox = new Inbox();
		
		String sql = "SELECT`uuid`,`packageName`,`packageCode`,`logisticsCompany`,`floorNumber`," +
					"`inboxUserId`,`arrivalDate`,`remindMode`,`receiveUserId`,`receiveDate`,`status`,`mphoneRemind`" +
					"FROM `asdb`.`k_inbox` " +
					"where uuid=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				inbox.setUuid(rs.getString("uuid"));
				inbox.setPackageName(rs.getString("packageName"));
				inbox.setPackageCode(rs.getString("packageCode"));
				inbox.setLogisticsCompany(rs.getString("logisticsCompany"));
				inbox.setFloorNumber(rs.getString("floorNumber"));
				inbox.setInboxUserId(rs.getString("inboxUserId"));
				inbox.setArrivalDate(rs.getString("arrivalDate"));
				inbox.setRemindMode(rs.getString("remindMode"));
				inbox.setReceiveUserId(rs.getString("receiveUserId"));
				inbox.setReceiveDate(rs.getString("receiveDate"));
				inbox.setStatus(rs.getString("status"));
				inbox.setMphoneRemind(rs.getString("mphoneRemind"));
				
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return inbox;
	}
	
	public Inbox getInboxBypackageCode(String packageCode){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Inbox inbox = new Inbox();
		
		String sql = "SELECT`uuid`,`packageName`,`packageCode`,`logisticsCompany`,`floorNumber`," +
					"`inboxUserId`,`arrivalDate`,`remindMode`,`receiveUserId`,`receiveDate`,`status`,`mphoneRemind`" +
					"FROM `asdb`.`k_inbox` " +
					"where packageCode=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, packageCode);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				inbox.setUuid(rs.getString("uuid"));
				inbox.setPackageName(rs.getString("packageName"));
				inbox.setPackageCode(rs.getString("packageCode"));
				inbox.setLogisticsCompany(rs.getString("logisticsCompany"));
				inbox.setFloorNumber(rs.getString("floorNumber"));
				inbox.setInboxUserId(rs.getString("inboxUserId"));
				inbox.setArrivalDate(rs.getString("arrivalDate"));
				inbox.setRemindMode(rs.getString("remindMode"));
				inbox.setReceiveUserId(rs.getString("receiveUserId"));
				inbox.setReceiveDate(rs.getString("receiveDate"));
				inbox.setStatus(rs.getString("status"));
				inbox.setMphoneRemind(rs.getString("mphoneRemind"));
				
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return inbox;
	}
	
	/**
	 * 查询包裹条形码是否存在
	 * @param packageCodeId
	 * @return
	 */
	public String existsPackageCodeId(String packageCodeId){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String packageCode = "";
		
		String sql = "SELECT `packageCode` FROM `k_inbox` where packageCode=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, packageCodeId);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				packageCode=rs.getString("packageCode");
				
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return packageCode;
	}
	
	public void saveCard(InboxIcon icon,String photo) throws SQLException{
		PreparedStatement ps = null;
		FileInputStream fs=null;
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection( "jdbc:mysql://192.168.1.7:7188/asdb?characterEncoding=gb2312", "xoops_root", "654321");
//			String getPhotoBuffer=photo.substring(8);
			File file=new File(photo);
			fs=new FileInputStream(file);
//			InputStreamReader read=new InputStreamReader(fs,"GB2312");
			String sql="insert into k_inbox_icon (name,sex,nation,born,address,cardno,police,activity,getphotobuffer) values(?,?,?,?,?, ?,?,?,?)";
			int i = 1;
			ps = con.prepareStatement(sql);
			ps.setString(i++, icon.getName());
			ps.setString(i++, icon.getSex());
			ps.setString(i++, icon.getNation());
			ps.setString(i++, icon.getBorn());
			ps.setString(i++, icon.getAddress());
			ps.setString(i++, icon.getCardNo());
			ps.setString(i++, icon.getPolice());
			ps.setString(i++, icon.getActivity());
			ps.setBinaryStream(i++, fs, (int)file.length());
//			ps.setBinaryStream(i++, fs, fs.available());
			System.out.println(sql);
			ps.execute();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
			con.close();
		}
	}
	
	public InboxIcon getInboxIcon(String id){
		InboxIcon ii=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//String sql="select * from k_inbox_icon where cardno="+id;
			String sql = " SELECT c.name,b.id,b.sex,b.nation,b.born,b.address,a.identityName,a.identityCard,b.police,b.activity,b.getphotobuffer " +
					     " FROM k_inbox a" +
					     " LEFT JOIN k_inbox_icon b ON a.identityCard = b.cardNo" +
					     " LEFT JOIN k_user c ON a.receiveUserId = c.id " +
					     " WHERE a.identityCard = '"+id+"'";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				ii=new InboxIcon();
				ii.setId(rs.getString("id"));
				ii.setName(rs.getString("name"));
				ii.setSex(rs.getString("sex"));
				ii.setNation(rs.getString("identityName"));//国籍存放身份证名称
				ii.setBorn(rs.getString("born"));
				ii.setAddress(rs.getString("address"));
				ii.setCardNo(rs.getString("identityCard"));
				ii.setPolice(rs.getString("police"));
				ii.setActivity(rs.getString("activity"));
				ii.setGetPhotoBuffer(rs.getBytes("getphotobuffer"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return ii;
	}
	
	public InputStream query_getPhotoImageBlob(String id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		InputStream result = null;
		try {
			String sql = "select getphotobuffer from k_inbox_icon where cardno='"+id+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
				result = rs.getBlob("getphotobuffer").getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	/*
	 * 根据inbox id查找身份证
	 */
	public String getCardNum(String id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String cardNum=null;
		try {
			String sql="select identitycard from k_inbox where uuid="+id;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				cardNum=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return cardNum;
	}
}
