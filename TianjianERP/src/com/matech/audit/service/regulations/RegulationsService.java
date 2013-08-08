package com.matech.audit.service.regulations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.regulations.model.Regulations;
import com.matech.framework.pub.db.DbUtil;

public class RegulationsService {
	private Connection conn = null;
	public RegulationsService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 得到 Regulations  list
	 * 
	 * @return
	 */
	public List<Regulations> getListRegulations() {
		List<Regulations> list = null;
		String sql = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sql = " select autoId,title,contents,updateTime,publishUserId,attachmentId,memo,property,ctype,lookUser,lookRole "
					+ " from oa_regulations order by updateTime desc limit 10";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = new ArrayList<Regulations>();
			while (rs.next()) {
				Regulations regulations = new Regulations();
				regulations.setAutoId(rs.getString("autoId"));
				regulations.setTitle(rs.getString("title"));
				regulations.setContents(rs.getString("contents"));
				regulations.setUpdateTime(rs.getString("updateTime"));
				regulations.setPublishUserId(rs.getString("publishUserId"));
				regulations.setAttachmentId(rs.getString("attachmentId"));
				regulations.setMemo(rs.getString("memo"));
				regulations.setProperty(rs.getString("property"));
				regulations.setCtype(rs.getString("ctype"));
				regulations.setLookUser(rs.getString("lookUser"));
				regulations.setLookRole(rs.getString("lookRole"));
				list.add(regulations);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	

	/**
	 * 得到单个news对象
	 * @param autoId
	 * @return
	 */
	public Regulations getRegulationsByAutoId(String autoId) {
		Regulations r = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		try {
			sql = " select a.autoId,a.title,a.contents,a.updateTime,a.publishUserId,a.attachmentId,a.memo,a.property,a.lookUser,a.lookRole,a.ctype,c.value as ctypeName"
				+ " from oa_regulations a" +
				" left join k_dic c on a.ctype=c.autoId and c.ctype LIKE '%部门规章%' " +
				" where a.autoId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();
			String ctype = "";
			if(rs.next()){
				int i = 1;
				r = new Regulations();
				r.setAutoId(rs.getString(i++));
				r.setTitle(rs.getString(i++));
				r.setContents(rs.getString(i++));
				r.setUpdateTime(rs.getString(i++));
				r.setPublishUserId(rs.getString(i++));
				r.setAttachmentId(rs.getString(i++));
				r.setMemo(rs.getString(i++));
				r.setProperty(rs.getString("property"));
				r.setLookUser(rs.getString("lookUser"));
				r.setLookRole(rs.getString("lookRole"));
				ctype = rs.getString("ctypeName");
				if("".equals(ctype) || ctype==null){
					ctype = rs.getString("ctype");
				}
				r.setCtype(ctype);
			}
			
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	

	
	/**
	 * 新增 Regulations 对象
	 * @param autoId
	 * @return
	 */
	public boolean addRegulations(Regulations regulations){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " insert into oa_regulations (title,contents,updateTime,publishUserId,attachmentId,memo,property,lookUser,lookRole,ctype)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, regulations.getTitle());
			ps.setString(i++, regulations.getContents());
			ps.setString(i++, regulations.getUpdateTime());
			ps.setString(i++, regulations.getPublishUserId());
			ps.setString(i++, regulations.getAttachmentId());
			ps.setString(i++, regulations.getMemo());
			ps.setString(i++, regulations.getProperty());
			ps.setString(i++, regulations.getLookUser());
			ps.setString(i++, regulations.getLookRole());
			ps.setString(i++, regulations.getCtype());
			
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return false;
	}
	

	/**
	 * 修改 Regulations 对象
	 * @param autoId
	 * @return
	 */
	public boolean updateRegulationsByAutoId(Regulations regulations){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " update oa_regulations set title = ?,contents = ?,updateTime = now(),publishUserId = ?,memo = ?,property = ?,ctype =?,lookUser=?,lookRole=?,attachmentId=?  "
				+ " where autoId = ?";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, regulations.getTitle());
			ps.setString(i++, regulations.getContents());
			ps.setString(i++, regulations.getPublishUserId());
			ps.setString(i++, regulations.getMemo());
			ps.setString(i++, regulations.getProperty());
			ps.setString(i++, regulations.getCtype());
			ps.setString(i++, regulations.getLookUser());
			ps.setString(i++, regulations.getLookRole());
			ps.setString(i++, regulations.getAttachmentId());
			ps.setString(i++, regulations.getAutoId());
			
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	/**
	 * 删除 news 对象
	 * @param autoId
	 * @return
	 */
	public boolean deleteRegulationsByAutoId(String autoId){
		PreparedStatement ps = null;
		String sql = null;
		try {
			sql = "delete from oa_regulations where autoId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return false;
	}
	


	public List getAttach(String property) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Attach> list = new ArrayList<Attach>();
		try {
			String sql = "select * from asdb.k_attach where property='"+property+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){

				Attach attach = new Attach();
				attach.setUnid(rs.getString("unid"));
				attach.setTypeId(rs.getString("typeId"));
				attach.setTitle(rs.getString("title"));
				attach.setContent(rs.getString("content"));
				attach.setUdate(rs.getString("udate"));

				attach.setLastDate(rs.getString("lastDate"));
				attach.setLastPerson(rs.getString("lastPerson"));
				attach.setOrderId(rs.getString("orderId"));
				attach.setViewCount(rs.getInt("viewCount"));
				attach.setFilename(rs.getString("filename"));

				attach.setEdate(rs.getString("edate"));
				attach.setMime(rs.getString("mime"));
				attach.setDepartid(rs.getString("departid"));
				attach.setProperty(rs.getString("property"));
				attach.setProjectid(rs.getString("projectid"));
				attach.setReleasedate(rs.getString("releasedate"));
				
				list.add(attach);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 部门规章评价
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public List getListMapIdea(String autoId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map> list = new ArrayList<Map>();
		try {
			String sql = "SELECT `autoId`,`regulationsId`,b.name as userName,`content`,`date`,`property` "
						+"FROM `oa_regulationsidea` a " +
						" left join k_user b on a.userId = b.id \n"+
						"where regulationsId='"+autoId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Map  map = new HashMap();
				map.put("autoId", rs.getString("autoId"));
				map.put("regulationsId", rs.getString("regulationsId"));
				map.put("userName", rs.getString("userName"));
				map.put("content", rs.getString("content"));
				map.put("date", rs.getString("date"));
				map.put("property", rs.getString("property"));
				list.add(map);
				
			}
			return list; 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 部门规则评价
	 * @param regulationsId
	 * @param userId
	 * @param content
	 * @return
	 */
	public boolean addRegulationsIdea(String regulationsId,String userId,String content){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " insert into oa_regulationsidea (`regulationsId`,`userId`,`content`,`date`,`property`)"
				+ " values(?,?,?,now(),?)";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, regulationsId);
			ps.setString(i++, userId);
			ps.setString(i++, content);
			ps.setString(i++, "");
			
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return false;
	}
}
