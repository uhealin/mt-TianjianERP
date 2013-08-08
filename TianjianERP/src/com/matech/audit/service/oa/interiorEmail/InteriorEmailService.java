package com.matech.audit.service.oa.interiorEmail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Array;

import com.matech.audit.service.oa.interiorEmail.model.Email;
import com.matech.audit.service.oa.interiorEmail.model.EmailUser;
import com.matech.framework.pub.db.DbUtil;

/**
 * @author Administrator
 *内部邮箱
 */
public class InteriorEmailService {

	Connection conn = null;
	
	public InteriorEmailService (Connection conn ){
		this.conn = conn;
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
	
	public String getUserMobile(String sql){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				value = rs.getString(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return value;
	}
	/**
	 * 修改
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
	
	/**
	 * 添加邮件
	 * @param email
	 * @return
	 */
	public boolean addEmail(Email email){
		
		boolean result = false;
		
		String sql = "INSERT INTO `oa_email` \n"
		            +"(`uuid`,`addressee`,`title`,`importance`,`content`,`fileId`,`addresser`,`sendDate`,`property`,`status`) \n"
		            +"VALUES (?,?,?,?,?,?,?,now(),?,'正式');";
		
		int i = 1;
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			ps.setString(i++, email.getUuid());
			ps.setString(i++, email.getAddressee());
			ps.setString(i++, email.getTitle());
			ps.setString(i++, email.getImportance());
			ps.setString(i++, email.getContent());
			ps.setString(i++, email.getFileId());
			ps.setString(i++, email.getAddresser());
			ps.setString(i++, email.getProperty());
			
			ps.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return result ;
		
	}
	
	/**
	 * 得到邮箱信息
	 * @param uuid
	 * @return
	 */
	public Email getEmail(String uuid){
		
		Email email = new Email();
		ResultSet rs = null;
		String sql = "select  \n"
		            +"`uuid`,`addressee`,`title`,`importance`,`content`,`fileId`,`addresser`,`sendDate`,`property` from oa_email \n"
		            +"where uuid='"+uuid+"';";
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			rs = ps.executeQuery();
			while (rs.next()) {
				email.setUuid(uuid);
				email.setTitle(rs.getString("title"));
				email.setAddressee(rs.getString("addressee"));
				email.setImportance(rs.getString("importance"));
				email.setContent(rs.getString("content"));
				email.setFileId(rs.getString("fileId"));
				email.setAddresser(rs.getString("addresser"));
				email.setSendDate(rs.getString("sendDate"));
				email.setProperty(rs.getString("property"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return email ;
		
	}
	
	/**
	 * 状态不等于 已删除或 已撤销
	 * @param uuid
	 * @return
	 */
	public Email getEmailCheckStatus(String uuid){
		
		Email email = null;
		ResultSet rs = null;
		String sql = "select  \n"
		            +"`uuid`,`addressee`,`title`,`importance`,`content`,`fileId`,`addresser`,`sendDate`,`property` from oa_email \n"
		            +"where uuid='"+uuid+"' AND `status` <> '已删除' and `status`<>'已撤销' ;";
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			rs = ps.executeQuery();
			while (rs.next()) {
				email = new Email();
				email.setUuid(uuid);
				email.setTitle(rs.getString("title"));
				email.setAddressee(rs.getString("addressee"));
				email.setImportance(rs.getString("importance"));
				email.setContent(rs.getString("content"));
				email.setFileId(rs.getString("fileId"));
				email.setAddresser(rs.getString("addresser"));
				email.setSendDate(rs.getString("sendDate"));
				email.setProperty(rs.getString("property"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return email ;
		
	}
	
	/**
	 * 得到 草稿信息
	 * @param uuid
	 * @return
	 */
	public Email getDraftEmail(String uuid){
		
		Email email = new Email();
		ResultSet rs = null;
		String sql = "select  \n"
		            +"`uuid`,`addressee`,`title`,`importance`,`content`,`fileId`,`addresser`,`sendDate`,`property` from oa_emaildraft \n"
		            +"where uuid='"+uuid+"';";
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			rs = ps.executeQuery();
			while (rs.next()) {
				email.setUuid(uuid);
				email.setAddressee(rs.getString("addressee"));
				email.setTitle(rs.getString("title"));
				email.setImportance(rs.getString("importance"));
				email.setContent(rs.getString("content"));
				email.setFileId(rs.getString("fileId"));
				email.setAddresser(rs.getString("addresser"));
				email.setSendDate(rs.getString("sendDate"));
				email.setProperty(rs.getString("property"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return email ;
		
	}
	
	
	/**
	 * 草稿
	 * @param email
	 * @return
	 */
	public boolean addEmailDraft(Email email){
		
		boolean result = false;
		
		String sql = "INSERT INTO `oa_emailDraft` \n"
		            +"(`uuid`,`addressee`,`title`,`importance`,`content`,`fileId`,`addresser`,`sendDate`,`property`) \n"
		            +"VALUES (?,?,?,?,?,?,?,now(),?);";
		
		int i = 1;
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			ps.setString(i++, email.getUuid());
			ps.setString(i++, email.getAddressee());
			ps.setString(i++, email.getTitle());
			ps.setString(i++, email.getImportance());
			ps.setString(i++, email.getContent());
			ps.setString(i++, email.getFileId());
			ps.setString(i++, email.getAddresser());
			ps.setString(i++, email.getProperty());
			
			ps.execute();
			result = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return result ;
		
	}
	
	/**
	 * 添加邮件(密送/抄送/外部/收件人)人员
	 * @param email
	 * @return
	 */
	public boolean addEmailUser(EmailUser emailUser){
		
		boolean result = false;
		
		String sql = "INSERT INTO `oa_emailuser` \n"
		            +"(`uuid`,`userId`,`ctype`,`instationRemind`,`receiveRemind`,`isRead`,`readTime`,`property`,mobilePhoneRemind) \n"
		            +"VALUES (?,?,?,?,?,?,?,?,? );";
				
		int i = 1;
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			ps.setString(i++, emailUser.getUuid());
			ps.setString(i++, emailUser.getUserId());
			ps.setString(i++, emailUser.getCtype());
			ps.setString(i++, emailUser.getInstationRemind());
			ps.setString(i++, emailUser.getReceiveRemind());
			ps.setString(i++, emailUser.getIsRead());
			ps.setString(i++, emailUser.getReadTime());
			ps.setString(i++, emailUser.getProperty());
			ps.setString(i++, emailUser.getMobilePhoneRemind());
			ps.execute();
			result = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return result ;
		
	}
	
	/**
	 * 得到收件人员
	 * @param uuid
	 * @param userId
	 * @return
	 */
	public List<EmailUser> getListEmailUser(String uuid,String userId){
		
		ResultSet rs = null;
		List<EmailUser> listEmailUser = new ArrayList<EmailUser>();
		String sql = "SELECT`autoId`,`uuid`,`userId`,`ctype`,`instationRemind`,`receiveRemind`,`isRead`,`dustbin`,`readTime`,`property` \n"
					+"FROM `oa_emailuser` \n"
					+"WHERE UUID='"+uuid+"' ";
		if(!"".equals(userId)){
			sql += " and userId = '"+userId+"'"; 
		}
				
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery();
			while (rs.next()) {
					
				EmailUser emailUser = new EmailUser();
				
				emailUser.setAutoId(rs.getString("autoId"));
				emailUser.setUuid(rs.getString("uuid"));
				emailUser.setUserId(rs.getString("userId"));
				emailUser.setCtype(rs.getString("ctype"));
				emailUser.setInstationRemind(rs.getString("instationRemind"));
				emailUser.setReceiveRemind(rs.getString("receiveRemind"));
				emailUser.setIsRead(rs.getString("isRead"));
				emailUser.setDustbin(rs.getString("dustbin"));
				emailUser.setReadTime(rs.getString("readTime"));
				emailUser.setProperty(rs.getString("property"));
				listEmailUser.add(emailUser);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return listEmailUser ;
		
	}

	/**
	 * 日志 永久删除时 会使用
	 * @param emailUser
	 * @return
	 */
	public boolean addEmailUserLog(EmailUser emailUser){
		
		boolean result = false;
		
		String sql = "INSERT INTO `l_emailuser` \n"
		            +"(`uuid`,`userId`,`ctype`,`instationRemind`,`receiveRemind`,`isRead`,`readTime`,`property`) \n"
		            +"VALUES (?,?,?,?,?,?,?,? );";
				
		int i = 1;
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement(sql) ;
		
			ps.setString(i++, emailUser.getUuid());
			ps.setString(i++, emailUser.getUserId());
			ps.setString(i++, emailUser.getCtype());
			ps.setString(i++, emailUser.getInstationRemind());
			ps.setString(i++, emailUser.getReceiveRemind());
			ps.setString(i++, emailUser.getIsRead());
			ps.setString(i++, emailUser.getReadTime());
			ps.setString(i++, emailUser.getProperty());
			
			ps.execute();
			result = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return result ;
		
	}
	
	/**
	 * 得到那些人已经阅读，那些人还没阅读
	 * @param uuid
	 * @param userId
	 * @return
	 */
	public List<EmailUser> getListEmailReadUser(String uuid){
		
		ResultSet rs = null;
		List<EmailUser> listEmailUser = new ArrayList<EmailUser>();
		String sql =  "SELECT b.readDate,d.name as userId,IF(b.userid IS NULL OR b.dustbin ='是','del',IF(b.isread ='否','unread','read')) AS isRead" +
						",IF(b.userid IS NULL OR b.dustbin ='是','收件人已删除',IF(b.isread ='否','收件人未读','收件人已读')) AS msg,b.readTime \n"
					  +"FROM oa_email a \n"
					  +"LEFT JOIN k_user d ON CONCAT(',',a.`addressee`,',') LIKE CONCAT('%,',d.id,',%') \n"
					  +"LEFT JOIN oa_emailuser b ON a.`uuid` = b.uuid AND b.userid = d.id \n" 
					  +"WHERE a.UUID='"+uuid+"' ";
		 	
		PreparedStatement ps = null;
		System.out.println("sqlbbbbbbbbbbb="+sql);
		try {
			
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery();
			while (rs.next()) {
					
				EmailUser emailUser = new EmailUser();
				
				emailUser.setUserId(rs.getString("userId"));
				emailUser.setIsRead(rs.getString("isRead"));
				emailUser.setReadTime(rs.getString("readTime"));
				emailUser.setReadDate(rs.getString("readDate"));
				emailUser.setDustbin(rs.getString("msg"));
				listEmailUser.add(emailUser);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return listEmailUser ;
		
	}

	
}
