package com.matech.audit.service.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.news.model.News;
import com.matech.framework.pub.db.DbUtil;

public class NewsService {
	private Connection conn = null;

	public NewsService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 得到新闻 list
	 * 
	 * @return
	 */
	public List<News> getListNews() {
		List<News> list = null;
		String sql = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
      
		
		
		try {
			sql = " select autoId,title,contents,updateTime,publishUserId,attachmentId,memo,property,big_type,type "
					+ " from oa_news order by updateTime desc limit 10";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			list = new ArrayList<News>();
			while (rs.next()) {
				News news = new News();
				news.setAutoId(rs.getInt("autoId"));
				news.setTitle(rs.getString("title"));
				news.setContents(rs.getString("contents"));
				news.setUpdateTime(rs.getString("updateTime"));
				news.setPublishUserId(rs.getString("publishUserId"));
				news.setAttachmentId(rs.getString("attachmentId"));
				news.setMemo(rs.getString("memo"));
				news.setProperty(rs.getString("property"));
				news.setBig_type("big_type");
				news.setType("type");
				list.add(news);
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
	public News getNewsByAutoId(String autoId) {
		News news = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		try {
			sql = " select autoId,title,contents,updateTime,publishUserId,attachmentId,memo,property,big_type,type,dept_type"
				+ " from oa_news where autoId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();
			
			if(rs.next()){
				int i = 1;
				news = new News();
				news.setAutoId(rs.getInt(i++));
				news.setTitle(rs.getString(i++));
				news.setContents(rs.getString(i++));
				news.setUpdateTime(rs.getString(i++));
				news.setPublishUserId(rs.getString(i++));
				news.setAttachmentId(rs.getString(i++));
				news.setMemo(rs.getString(i++));
				news.setProperty(rs.getString(i++));
				news.setBig_type(rs.getString(i++));
				news.setType(rs.getString(i++));
				news.setDept_type(rs.getString(i++));
			}
			System.out.println("sql = "+sql);
			return news;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return news;
	}
	

	/**
	 * 新增 news 对象
	 * @param autoId
	 * @return
	 */
	public boolean addNews(News news){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " insert into oa_news (title,contents,updateTime,publishUserId,attachmentId,memo,property,big_type,type,dept_type,area,menuid)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, news.getTitle());
			ps.setString(i++, news.getContents());
			ps.setString(i++, news.getUpdateTime());   
			ps.setString(i++, news.getPublishUserId());
			ps.setString(i++, news.getAttachmentId());
			ps.setString(i++, news.getMemo());
			ps.setString(i++, news.getProperty());
			ps.setString(i++, news.getBig_type());
			ps.setString(i++, news.getType());
			ps.setString(i++, news.getDept_type());
			ps.setString(i++, news.getArea());
			ps.setString(i++, news.getMenuid());
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
	 * 修改 news 对象
	 * @param autoId
	 * @return
	 */
	public boolean updateNewsByAutoId(News news){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " update oa_news set title = ?,contents = ?,updateTime = ?,publishUserId = ?,memo = ?,property = ?,attachmentId=?,big_type=?,type=?,dept_type=? "
				+ " where autoId = ?";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, news.getTitle());
			ps.setString(i++, news.getContents());
			ps.setString(i++, news.getUpdateTime());
			ps.setString(i++, news.getPublishUserId());
			ps.setString(i++, news.getMemo());
			ps.setString(i++, news.getProperty());
			ps.setString(i++, news.getAttachmentId());
			ps.setString(i++, news.getBig_type());
			ps.setString(i++, news.getType());
			ps.setString(i++, news.getDept_type());
			ps.setInt(i++, news.getAutoId());
			ps.execute();
			System.out.println("sql = "+sql);
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
	public boolean deleteNewsByAutoId(String autoId){
		PreparedStatement ps = null;
		String sql = null;
		try {
			sql = "delete from oa_news where autoId = ?";
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
}
